package com.example.mvc.codebase.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mvc.codebase.MyApplication;
import com.example.mvc.codebase.R;
import com.example.mvc.codebase.api.RequestCode;
import com.example.mvc.codebase.customdialog.CustomDialog;
import com.example.mvc.codebase.helper.ToastHelper;
import com.example.mvc.codebase.interfaces.ClickEvent;
import com.example.mvc.codebase.interfaces.DataObserver;
import com.example.mvc.codebase.models.CheckVersionModel;
import com.example.mvc.codebase.models.CustomerDetails;
import com.example.mvc.codebase.utils.Constants;
import com.example.mvc.codebase.utils.GenericView;
import com.example.mvc.codebase.utils.Util;


public class SplashActivity extends AppCompatActivity implements ClickEvent, DataObserver {

    // xml component declaration
    private ImageView ivSplashLogo;
    private TextView txtMaintenance, txtAppVersion;
    private LinearLayout linUnderConstruction, linProgressBar;

    //variable declaration
    private String notificationMsgData;

    //class object declaration
    private CheckVersionModel checkVersionModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (getIntent() != null && getIntent().hasExtra(Constants.KEY_FROM_NOTIFICATION)) {
            notificationMsgData = getIntent().getStringExtra(Constants.KEY_FROM_NOTIFICATION);
        } else if (getIntent() != null && getIntent().getExtras() != null) {
            // it will get data when notification comes from firebase console when
            // application state is in background.
            notificationMsgData = getIntent().getExtras().getString(Constants.MESSAGE);
        }

        init();
        callCheckVersionAPI();
    }


    /**
     * Initialise view of xml component here
     * e.g. textView, editText
     * and initialisation of required class objects
     */
    private void init() {

        linUnderConstruction = GenericView.findViewById(this, R.id.lin_splashUnderConstruction);
        ivSplashLogo = GenericView.findViewById(this, R.id.iv_splashLogo);
        txtMaintenance = GenericView.findViewById(this, R.id.tv_splashMaintenance);
        linProgressBar = GenericView.findViewById(this, R.id.lin_progressbar);
        txtAppVersion = GenericView.findViewById(this, R.id.txt_appVersion);
        txtAppVersion.setTypeface(MyApplication.mTypefaceMap.get(Constants.HELVETICA_NEUE_LIGHT));

        // display application version
        txtAppVersion.setText(Util.getAppVersion());

        checkVersionModel = new CheckVersionModel();
    }


    @Override
    public void onClickEvent(View view) {

        Util.clickEffect(view);
        switch (view.getId()) {

            case R.id.btnYes:
                CustomDialog.getInstance().hide();
                String btnText = (String) view.getTag();
                if (btnText.equals(getString(R.string.lblOk))) {
                    finish();
                } else if (btnText.equals(getString(R.string.lblYes))) {
                    openPlayStore(checkVersionModel.getUrl());
                } else if (btnText.equals(getString(R.string.lblUpdate))) {
                    openPlayStore(checkVersionModel.getUrl());
                }
                break;

            case R.id.btnNo:
                CustomDialog.getInstance().hide();
                openActivity();
                break;

        }
    }

    @Override
    public void OnSuccess(RequestCode requestCode) {
        linProgressBar.setVisibility(View.GONE);
        switch (requestCode) {

            case CHECK_VERSION:

                callNextScreen();

                break;
        }

    }

    @Override
    public void OnFailure(RequestCode requestCode, String error) {
        linProgressBar.setVisibility(View.GONE);
        if (error.equals(getString(R.string.errorMsgInternetSlow)) || error.equals(getString(R.string.errorMsgInternetConnUnavailable))) {
            CustomDialog.getInstance().showAlert(this, error, false, Util.getAppKeyValue(this, R.string.lblDismiss));
        } else {
            ToastHelper.getInstance(this).displayCustomToast(error);
        }
    }

    @Override
    public void onRetryRequest(RequestCode requestCode) {
        switch (requestCode) {
            case CHECK_VERSION:
                callCheckVersionAPI();
                break;
        }
    }

    /**
     * it call the new screen based on update requirement
     */
    private void callNextScreen() {

        checkVersionModel = CheckVersionModel.getCheckVersionModel();

        switch (checkVersionModel.getIsUpdateType()) {

            case Constants.APP_NOT_APPROVED:
                // To show maintenance message to user
                underConstruction(checkVersionModel.getMaintenanceMsg());
                break;

            case Constants.APP_APPROVED_AND_NO_UPDATE:
                // Go to LoginActivity or MainActivity if there is no update available
                openActivity();
                break;

            case Constants.APP_OPTIONAL_UPDATE:
                // it show dialog with two button that new update is available
                if (!CustomDialog.getInstance().isDialogShowing()) {
                    CustomDialog.getInstance().showAlert(this, checkVersionModel.getUpdateMessage(), false, Util.getAppKeyValue(this, R.string.lblYes), Util.getAppKeyValue(this, R.string.lblNo));
                }
                break;

            case Constants.APP_MANDATORY_UPDATE:
                // it show single button dialog that indicate user to download new app version
                if (!CustomDialog.getInstance().isDialogShowing()) {
                    CustomDialog.getInstance().showAlert(this, checkVersionModel.getUpdateMessage(), true, Util.getAppKeyValue(this, R.string.lblUpdate));
                }
                break;

        }

    }

    /**
     * it open activity based on user credentials
     * it redirect activity either on mainActivity or loginActivity
     */
    private void openActivity() {
        // if user logged in than it go to the main activity
        if (CustomerDetails.isLoggedIn()) {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            if (!TextUtils.isEmpty(notificationMsgData)) {
                intent.putExtra(Constants.KEY_FROM_NOTIFICATION, notificationMsgData);
            }
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            if (!TextUtils.isEmpty(notificationMsgData)) {
                intent.putExtra(Constants.KEY_FROM_NOTIFICATION, notificationMsgData);
            }
            startActivity(intent);
            finish();
        }

    }

    /**
     * it open play store and open specific app that we have pass as an apkUrl
     *
     * @param apkUrl (String) : application playStore url
     */
    private void openPlayStore(String apkUrl) {

        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(apkUrl)));
        finish();

    }

    /**
     * This method shows under maintenance message while programmer or developer need to
     * add code as required.
     * <br>
     * It shows this dialog when {@link CheckVersionModel#isUpdateType} parameter value
     * <code> isUpdateType = 0</code>.
     * </br>
     *
     * @param maintenanceMsg (String) : it shows the appropriate required message
     *                       e.g. A short maintenance break will occur. Please check back shortly.
     */
    private void underConstruction(String maintenanceMsg) {

        ivSplashLogo.setVisibility(View.INVISIBLE);
        linUnderConstruction.setVisibility(View.VISIBLE);
        txtMaintenance.setText(maintenanceMsg);
    }

    /**
     * TO call check version api
     */
    private void callCheckVersionAPI() {
        if (Util.checkInternetConnection()) {
            checkVersionModel.callCheckVersionAPI(this, this);
        } else {
            linProgressBar.setVisibility(View.GONE);
            CustomDialog.getInstance().showAlert(this,
                    Util.getAppKeyValue(this, R.string.errorMsgInternetConnUnavailable), false,
                    Util.getAppKeyValue(this, R.string.lblRetry), Util.getAppKeyValue(this, R.string.lblOk),
                    RequestCode.CHECK_VERSION, this);
        }
    }


}
