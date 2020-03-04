package com.example.mvc.codebase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mvc.codebase.MyApplication;
import com.example.mvc.codebase.R;
import com.example.mvc.codebase.api.RequestCode;
import com.example.mvc.codebase.customdialog.CustomDialog;
import com.example.mvc.codebase.helper.ToastHelper;
import com.example.mvc.codebase.interfaces.ClickEvent;
import com.example.mvc.codebase.interfaces.DataObserver;
import com.example.mvc.codebase.models.CustomerDetails;
import com.example.mvc.codebase.socialIntegration.FacebookIntegration;
import com.example.mvc.codebase.socialIntegration.GoogleIntegration;
import com.example.mvc.codebase.socialIntegration.TwitterIntegration;
import com.example.mvc.codebase.utils.Constants;
import com.example.mvc.codebase.utils.GenericView;
import com.example.mvc.codebase.utils.Util;
import com.example.mvc.codebase.validator.ValidationClass;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;


public class LoginActivity extends AppCompatActivity implements ClickEvent, TextWatcher, DataObserver, GoogleApiClient.OnConnectionFailedListener {

    // xml components declaration
    private TextView txtLogin, txtOr, txtGooglePlus, txtFacebook, txtTwitter, txtAppVersion;
    private EditText edtEmail, edtPassword;
    private TextInputLayout inputTextEmail, inputTextPassword;
    private Button btnRegister, btnLogin;


    // variables declaration
    private static final String TAG = LoginActivity.class.getSimpleName();
    private String notificationMsgData;


    // class object declaration
    private CustomerDetails customerDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleIntegration.getInstance(this).initGoogleSdk();
        FacebookIntegration.getInstance(this).initFacebookSdk();
        TwitterIntegration.getInstance(this).initTwitterSdk();

        setContentView(R.layout.activity_login);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Constants.KEY_FROM_NOTIFICATION)) {
            notificationMsgData = intent.getStringExtra(Constants.KEY_FROM_NOTIFICATION);

        }

        init();

    }


    /**
     * Initialise view of xml component here
     * eg. textView, editText
     * and initialization of required class objects
     */
    private void init() {

        txtLogin = GenericView.findViewById(this, R.id.tv_login);
        txtLogin.setTypeface(MyApplication.mTypefaceMap.get(Constants.HELVETICA_CONDENSED_BOLD));
        txtLogin.setText(Util.getAppKeyValue(this, R.string.lblLogin));

        inputTextEmail = GenericView.findViewById(this, R.id.inputText_LoginEmail);
        inputTextEmail.setHint(Util.getAppKeyValue(this, R.string.lblEmail));

        edtEmail = GenericView.findViewById(this, R.id.edt_loginUsername);
        edtEmail.setTypeface(MyApplication.mTypefaceMap.get(Constants.HELVETICA_NEUE_LIGHT));

        inputTextPassword = GenericView.findViewById(this, R.id.inputText_LoginPassword);
        inputTextPassword.setHint(Util.getAppKeyValue(this, R.string.lblPwd));

        edtPassword = GenericView.findViewById(this, R.id.edt_loginPassword);
        edtPassword.setTypeface(MyApplication.mTypefaceMap.get(Constants.HELVETICA_NEUE_LIGHT));

        btnLogin = GenericView.findViewById(this, R.id.btn_login);
        btnLogin.setText(Util.getAppKeyValue(this, R.string.lblLogin));

        btnRegister = GenericView.findViewById(this, R.id.btn_register);
        btnRegister.setText(Util.getAppKeyValue(this, R.string.lblRegister));

        txtOr = GenericView.findViewById(this, R.id.tv_OR);
        txtOr.setText(Util.getAppKeyValue(this, R.string.lblOr));

        txtGooglePlus = GenericView.findViewById(this, R.id.tv_google);
        txtGooglePlus.setText(Util.getAppKeyValue(this, R.string.lblLoginWithGooglePlus));

        txtFacebook = GenericView.findViewById(this, R.id.tv_faceBook);
        txtFacebook.setText(Util.getAppKeyValue(this, R.string.lblLoginWithFb));

        txtTwitter = GenericView.findViewById(this, R.id.tv_twitter);
        txtTwitter.setText(Util.getAppKeyValue(this, R.string.lblLoginWithTwitter));

        txtAppVersion = GenericView.findViewById(this, R.id.txt_appVersion);
        txtAppVersion.setTypeface(MyApplication.mTypefaceMap.get(Constants.HELVETICA_NEUE_LIGHT));
        txtAppVersion.setText(Util.getAppVersion());

        edtEmail.addTextChangedListener(this);
        edtPassword.addTextChangedListener(this);

        Util.setupOutSideTouchHideKeyboard(findViewById(R.id.activity_login));

        customerDetails = new CustomerDetails();

    }



    @Override
    public void onClickEvent(View view) {
        Util.clickEffect(view);

        switch (view.getId()) {
            case R.id.btn_login:
                if (validateLogin()) {
                    customerDetails.setEmail(edtEmail.getText().toString().trim());
                    customerDetails.setPassword(edtPassword.getText().toString().trim());
                    customerDetails.callLoginVerificationAPI(this, this);
                }
                break;

            case R.id.btn_register:
                clearFocus();
                Intent iToRegistrationActivity = new Intent(LoginActivity.this, RegistrationActivity.class);
                iToRegistrationActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                iToRegistrationActivity.putExtra(Constants.KEY_FROM_NOTIFICATION, notificationMsgData);
                startActivity(iToRegistrationActivity);

                break;

            case R.id.lin_googleLogin:
                if (Util.checkInternetConnection()) {
                    clearFocus();
                    GoogleIntegration.getInstance(this).logInToGoogle(this);
                } else {
                    CustomDialog.getInstance().showAlert(this, Util.getAppKeyValue(this, R.string.errorMsgInternetConnUnavailable), false, Util.getAppKeyValue(this, R.string.lblOk));
                }
                break;

            case R.id.lin_faceBookLogin:
                if (Util.checkInternetConnection()) {
                    clearFocus();
                    FacebookIntegration.getInstance(this).loginToFaceBook(this);
                } else {
                    CustomDialog.getInstance().showAlert(this, Util.getAppKeyValue(this, R.string.errorMsgInternetConnUnavailable), false, Util.getAppKeyValue(this, R.string.lblOk));
                }
                break;

            case R.id.lin_twitterLogin:
                if (Util.checkInternetConnection()) {
                    clearFocus();
                    TwitterIntegration.getInstance(this).loginToTwitter(this);
                } else {
                    CustomDialog.getInstance().showAlert(this, Util.getAppKeyValue(this, R.string.errorMsgInternetConnUnavailable), false, Util.getAppKeyValue(this, R.string.lblOk));
                }
                break;

            case R.id.btnYes:
                CustomDialog.getInstance().hide();
                break;
        }
    }

    @Override
    public void OnSuccess(RequestCode requestCode) {
        Intent iToMainActivity;
        switch (requestCode) {

            case CUSTOMER_LOGIN:
                // when user login successfully
                iToMainActivity = new Intent(this, MainActivity.class);
                if (!TextUtils.isEmpty(notificationMsgData)) {
                    iToMainActivity.putExtra(Constants.KEY_FROM_NOTIFICATION, notificationMsgData);
                }
                startActivity(iToMainActivity);
                finish();
                break;
            case REGISTER_CUSTOMER:
                // when new user registered
                iToMainActivity = new Intent(this, MainActivity.class);
                if (!TextUtils.isEmpty(notificationMsgData)) {
                    iToMainActivity.putExtra(Constants.KEY_FROM_NOTIFICATION, notificationMsgData);
                }
                startActivity(iToMainActivity);
                finish();
                break;
        }

    }

    @Override
    public void OnFailure(RequestCode requestCode, String error) {
        /* it show dialog when Internet connection is too slow or Internet connection unavailable */
        if (error.equals(getString(R.string.errorMsgInternetSlow)) || error.equals(getString(R.string.errorMsgInternetConnUnavailable))) {
            CustomDialog.getInstance().showAlert(this, error, false, getString(R.string.lblDismiss));
        } else {
            ToastHelper.getInstance(this).displayCustomToast(error);
        }
    }

    @Override
    public void onRetryRequest(RequestCode requestCode) {

    }

    /**
     * This method check validation of required view components
     *
     * @return (boolean) : it return either true if validation function matches with their requirement
     * or it return false on validation correction,
     */
    private boolean validateLogin() {

        if (ValidationClass.isEmpty(edtEmail.getText().toString().trim())) {
            displayErrorAndRequestFocus(edtEmail, Util.getAppKeyValue(this, R.string.errorMsgEmailAddress));
            return false;
        } else if (!ValidationClass.matchPattern(edtEmail.getText().toString().trim(), Patterns.EMAIL_ADDRESS.pattern())) {
            displayErrorAndRequestFocus(edtEmail, Util.getAppKeyValue(this, R.string.errorMsgValidEmailAddress));
            return false;
        } else if (ValidationClass.isEmpty(edtPassword.getText().toString().trim())) {
            displayErrorAndRequestFocus(edtPassword, Util.getAppKeyValue(this, R.string.errorMsgPwd));
            return false;
        } else if (!ValidationClass.checkMinLength(edtPassword.getText().toString().trim(), Constants.PASSWORD_LENGTH)) {
            displayErrorAndRequestFocus(edtPassword, Util.getAppKeyValue(this, R.string.errorMsgValidPwd));
            return false;
        }
        return true;
    }

    /**
     * This method display required validation error message and focus cursor on editText.
     *
     * @param editText     (EditText)   : EditText object
     * @param errorMessage (String) : errorMessage to be display
     */
    private void displayErrorAndRequestFocus(EditText editText, String errorMessage) {
        editText.requestFocus();
        ToastHelper.getInstance(this).displayCustomToast(errorMessage);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //TODO auto generated method or stub
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //TODO auto generated method or stub
        // it cancel toast when user perform action like start typing
        ToastHelper.getInstance(this).cancelToast();
    }

    @Override
    public void afterTextChanged(Editable s) {
        //TODO auto generated method or stub
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        ToastHelper.getInstance(this).displayCustomToast(connectionResult.getErrorMessage());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case GoogleIntegration.DEFAULT_AUTH_GOOGLE_REQUEST_CODE:

                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    GoogleIntegration.getInstance(this).saveGooglePlusLoginData(result);
                } else {
                    ToastHelper.getInstance(this).displayCustomToast(getString(R.string.errorMsgLoginFailed));
                }
                break;

            case TwitterIntegration.DEFAULT_AUTH_TWITTER_REQUEST_CODE:
                TwitterIntegration.getInstance(this).mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
                break;

            case FacebookIntegration.DEFAULT_AUTH_FACEBOOK_REQUEST_CODE:
                FacebookIntegration.getInstance(this).callbackManager.onActivityResult(requestCode, resultCode, data);
                break;

        }
    }

    /**
     * This method clear the focus of view
     */
    private void clearFocus() {
        edtEmail.setText(Constants.DEFAULT_BLANK_STRING);
        edtEmail.clearFocus();
        edtPassword.setText(Constants.DEFAULT_BLANK_STRING);
        edtPassword.clearFocus();
    }
}

