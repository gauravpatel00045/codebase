package com.example.mvc.codebase.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.mvc.codebase.R;
import com.example.mvc.codebase.customdialog.CustomDialog;
import com.example.mvc.codebase.interfaces.ClickEvent;
import com.example.mvc.codebase.models.CustomerDetails;
import com.example.mvc.codebase.socialIntegration.FacebookIntegration;
import com.example.mvc.codebase.socialIntegration.GoogleIntegration;
import com.example.mvc.codebase.socialIntegration.TwitterIntegration;
import com.example.mvc.codebase.utils.Constants;
import com.example.mvc.codebase.utils.GenericView;
import com.example.mvc.codebase.utils.Util;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements ClickEvent {
    // xml components declaration
    private Button btnLogout;


    // variables declaration
    public static final String TAG = MainActivity.class.getSimpleName();
    private String notificationMsgData;
    private boolean isShowPopUp = false;

    //class object declaration


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        GoogleIntegration.getInstance(this).initGoogleSdk();

        setContentView(R.layout.activity_main);

        // get data from intent that passed by other activity and perform its relevant action
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Constants.KEY_FROM_NOTIFICATION)) {
            isShowPopUp = intent.hasExtra(Constants.KEY_FROM_NOTIFICATION);
            notificationMsgData = intent.getStringExtra(Constants.KEY_FROM_NOTIFICATION);
        }

        init();

    }


    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.KEY_SHOW_POP_UP);
        registerReceiver(receiver, filter);

    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.KEY_SHOW_POP_UP) && intent.hasExtra(Constants.KEY_FROM_NOTIFICATION)) {
                notificationMsgData = intent.getStringExtra(Constants.KEY_FROM_NOTIFICATION);
                CustomDialog.getInstance().showAlert(context, notificationMsgData, true, getString(R.string.lblOk));
            }
        }
    };

    /**
     * Initialise view of xml component here
     * eg. textView, editText
     * and initialisation of required class objects
     */
    private void init() {
        GoogleIntegration.getInstance(this).mGoogleApiClient.connect();
        btnLogout = GenericView.findViewById(this, R.id.btn_logout);
        btnLogout.setText(Util.getAppKeyValue(this, R.string.lblLogout));

        //if isShowPopUp = true than it shows popUp with message
        if (isShowPopUp) {
            showNotificationPopUp();
            isShowPopUp = false;
        }
    }

    @Override
    public void onClickEvent(View view) {

        Util.clickEffect(view);
        switch (view.getId()) {

            case R.id.btn_logout:

                CustomerDetails customerDetails = CustomerDetails.getCurrentLoginUser();
                switch (customerDetails.getRegisterBy()) {

                    case Constants.VALUE_SIMPLE_LOGIN_TYPE:
                        CustomerDetails.logoutUser();
                        break;

                    case Constants.VALUE_GOOGLE_LOGIN_TYPE:
                        GoogleIntegration.getInstance(this).logOutToGoogle();
                        CustomerDetails.logoutUser();
                        break;

                    case Constants.VALUE_FACEBOOK_LOGIN_TYPE:
                        FacebookIntegration.getInstance(this).logoutToFacebook();
                        CustomerDetails.logoutUser();
                        break;

                    case Constants.VALUE_TWITTER_LOGIN_TYPE:
                        TwitterIntegration.getInstance(this).logOutFromTwitter();
                        CustomerDetails.logoutUser();
                        break;

                }

                Intent iToLoginActivity = new Intent(MainActivity.this, LoginActivity.class);
                iToLoginActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(iToLoginActivity);
                finish();
                break;

            case R.id.btnYes:
                String btnText = (String) view.getTag();
                if (btnText.equals(getString(R.string.lblOk))) {
                    if (CustomDialog.getInstance().isDialogShowing()) {
                        CustomDialog.getInstance().hide();
                    }
                }
                break;
        }
    }

    /**
     * To show notification message with popUp.
     */
    private void showNotificationPopUp() {
        CustomDialog.getInstance().showAlert(this, notificationMsgData, true, getString(R.string.lblOk));

    }
}
