package com.example.mvc.codebase.socialIntegration;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.example.mvc.codebase.MyApplication;
import com.example.mvc.codebase.R;
import com.example.mvc.codebase.customdialog.CustomDialog;
import com.example.mvc.codebase.enumerations.RegisterBy;
import com.example.mvc.codebase.interfaces.DataObserver;
import com.example.mvc.codebase.models.CustomerDetails;
import com.example.mvc.codebase.models.RegisterCustomerModel;
import com.example.mvc.codebase.utils.Debug;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.PlusShare;


/*
 * Todo: Before implement google login integration you must follow below steps.
 *
 * 1. Update sdk google play service in android studio
 *
 *    1.1) Refer below link how to update google play service in android studio
 *
 *         @Note : follow (Prerequisites) point
 *         @link : https://developers.google.com/identity/sign-in/android/start-integrating
 *
 * 2. Generate SHA-1 key
 *    @Note: prefer signed apk to generate SHA-1 key
 *
 *    There are two way to generate SHA-1 key
 *
 *    2.1) get SHA-1 using command prompt
 *         2.1.1) get debug SHA-1 key
 *              refer below link to generate SHA-1 key
 *              @link : https://developers.google.com/android/guides/client-auth
 *                                    OR
 *              run cmd and paste below line
 *              keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
 *
 *        2.1.2) get release or signed apk SHA-1 key
 *
 *              run cmd and paste below line
 *             keytool -list -v -keystore "D:\CodeBase Documentation\codebase.jks" -alias codebase
 *
 *             where
 *             .jks file path - "D:\CodeBase Documentation\codebase.jks"  e.g. codebase.jks is file name
 *             alias name - codebase e.g. -alias codebase
 *
 *    2.2) get SHA-1 from android studio
 *          refer below link
 *          @link : http://stackoverflow.com/questions/27609442/how-to-get-the-sha-1-fingerprint-certificate-in-android-studio-for-debug-mode
 *                                       OR
 *              Open Android Studio
 *              Open your Project
 *              Click on Gradle (From Right Side Panel, you will see Gradle Bar)
 *              Click on Refresh (Click on Refresh from Gradle Bar, you will see List Gradle scripts of your Project)
 *              Click on Your Project (Your Project Name form List (root))
 *              Click on Tasks
 *              Click on Android
 *              Double Click on signingReport (You will get SHA1 and MD5 in Run Bar)
 *
 *    2.3) Save generated SHA-1 key in notepad file or in your resource for future use
 *
 * 3. Get a configuration google-services.json file
 *
 *    3.1) Refer below link to get a configuration file
 *         @Note : follow (Get a configuration file) point
 *         @link : https://developers.google.com/identity/sign-in/android/start-integrating
 *
 *    3.2) Add configuration json file to your project
 *
 *         Refer below link to add a configuration file
 *         @Note : follow (Add the configuration file to your project) point
 *         @link : https://developers.google.com/identity/sign-in/android/start-integrating
 *
 * 4. Add the Google Services plugin
 *
 *    4.1) Add the dependency to your project-level build.gradle:
 *         dependencies{
 *         classpath 'com.google.gms:google-services:3.0.0'
 *         }
 *         till 23-3-2017 latest version is 3.0.0.
 *
 *    4.2) Add the plugin to your app-level build.gradle:
 *         apply plugin: 'com.google.gms.google-services'
 *
 * 5. Import latest google play service gradle file in app level build.gradle
 *
 *    @link : https://developers.google.com/android/guides/setup#add_google_play_services_to_your_project
 *
 *    In your app-level build.gradle file, declare Google Play Services as a dependency:
 *    apply plugin: 'com.android.application'
 *    ...
 *    dependencies {
 *       compile'com.google.android.gms:play-services-plus:10.2.1'
 *     }
 *     till 23-3-2017 latest version is 10.2.1
 *
 * 6. Enable the Google+ API
 *    @Note : refer below link to follow below steps
 *    @link : https://developers.google.com/+/mobile/android/getting-started
 *
 *    6.1)Go to the Google API Console APIs library.
 *
 *    6.2)From the project drop-down, select the project  you previously created.
 *
 *    6.3)In the list of Google APIs, search for the Google+ API service.
 *
 *    6.4)Select Google+ API from the results list.
 *
 *    6.5)Select Enable API.
 *
 *    6.6)When the process completes, Google+ API appears in the list of enabled APIs. To access, select API Manager on the left sidebar menu, then select the Enabled APIs tab.
 *
 *    5.7)In the sidebar under "API Manager", select Credentials.
 *
 *    6.8)In the Credentials tab, select the New credentials drop-down list, and choose API key.
 *
 *    6.9)From the Create a new key pop-up, choose the appropriate kind of key for your project: Server key, Browser key, Android key, or iOS key.
 *
 *    6.10)Enter a key Name, fill in any other fields as instructed, then select Create.
 *
 * 7. Generate android client Id from google dashboard
 *
 *    7.1) Go to google dashboard, refer below link
 *     @link : https://console.cloud.google.com/apis/dashboard?project=codebase-150306&duration=PT1H
 *
 *    7.2) select "credentials" from the left panel
 *
 *    7.3) select "create credentials"
 *
 *    7.4) select "OAuth client ID"
 *
 *    7.5) select "Android"
 *
 *    7.6) add SHA-1 key and project package name in Signing-certificate fingerprint
 *         e.g. SHA-1 key -  0A:B9:35:B2:8A:15:FD:62:2B:CA:04:06:C8:81:F0:53:6E:8A:B2:2D
 *         e.g. package name - com.example.mvc.codebase
 *
 *    7.7) save Web client id (auto created by Google Service) in strings.xml or in your resource
 *         it will use during initialize GoogleSdk
 *
 * 8. Now user ready to access following methods
 *
 *    8.1) refer step 8 how to access methods
 */

public class GoogleIntegration {

    //variable declaration
    private static final String TAG = GoogleIntegration.class.getName();

    public static final int DEFAULT_AUTH_GOOGLE_REQUEST_CODE = 9001;
    /*default request code to share post in google yet this request code is not predefined */
    private static final int DEFAULT_AUTH_GOOGLE_SHARE_REQUEST_CODE = 9002;

    private static final String GOOGLE_PLUS_APP_PACKAGE_NAME = "com.google.android.apps.plus";
    private static final String GOOGLE_SHARE_CONTENT_TYPE_TEXT = "text/plain";
    private static final String GOOGLE_SHARE_CONTENT_TYPE_IMAGE = "image/*";


    //class object declaration
    private static GoogleIntegration googleIntegration;
    private static Activity activityContext;
    private DataObserver dataObserver;
    public GoogleApiClient mGoogleApiClient;

    // constructor
    private GoogleIntegration() {

    }


/*========================google relevant integration methods========================*/
    /*
    * 8. TODO: Step 8 to access methods:
    *
    *    8.1) Import "socialIntegration" package or required class (GoogleIntegration.java)
    *
    *    8.2) Initialize google sdk
    *
    *         8.2.1) Copy below code in your activity before setContentView(R.layout.XXX);
    *                 in onCreate(Bundle savedInstanceState) method where login operation performed
    *
    *                GoogleIntegration.getInstance(this).initGoogleSdk();
    *
    *         8.2.2) Copy below code in your activity before setContentView(R.layout.XXX);
    *                in onCreate(Bundle savedInstanceState) method where you manage logout session
    *
    *                GoogleIntegration.getInstance(this).initGoogleSdk();
    *
    *         8.2.3) Copy below code in onCreate(Bundle savedInstanceState) method or xml component
    *                initialized, where you manage logout session
    *
    *                GoogleIntegration.getInstance(this).mGoogleApiClient.connect();
    *
    *    8.3) Perform button click event action
    *
    *         8.3.1) Copy below code in onclick of your custom login button
    *
    *                 GoogleIntegration.getInstance(this).logInToGoogle(this);
    *
    *                 "this" - dataObserver
    *
    * 9. Implement onActivityResult(int requestCode, int resultCode, Intent data) method
    *
    *    9.1) Implementation in Activity
    *
    *        9.1.1) Manage response that comes in onActivityResult(int requestCode, int resultCode, Intent data)
    *                in your activity
    *
    *            copy below code in onActivityResult(int requestCode, int resultCode, Intent data)
    *                 method in your activity
    *
    *              switch (requestCode){
    *
    *                 case GoogleIntegration.DEFAULT_AUTH_GOOGLE_REQUEST_CODE:
    *
    *                      GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
    *                      if (result.isSuccess()) {
    *                         GoogleIntegration.getInstance(this).saveGooglePlusLoginData(result);
    *                      }
    *                      break;
    *
    *       9.1.2) Go to step 10
    *
    *    9.2) Implementation in Fragment
    *
    *         9.2.1) copy below code in your parent activity where fragment loads.
    *
    *                 @Override
    *           protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    *               super.onActivityResult(requestCode, resultCode, data);
    *                   for (Fragment fragment : getSupportFragmentManager().getFragments()) {
    *                    fragment.onActivityResult(requestCode, resultCode, data);
    *                       }
    *                   }
    *
    *           @Override
    *           public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    *                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    *                    for (Fragment fragment : getSupportFragmentManager().getFragments()) {
    *                        fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    *                       }
    *                   }
    *
    *        9.2.2) Implement onActivityResult() method in fragment
    *               Now you will get the result in fragment
    *
    *               copy below code in onActivityResult(int requestCode, int resultCode, Intent data)
    *                 method in your activity
    *
    *              switch (requestCode){
    *
    *                 case GoogleIntegration.DEFAULT_AUTH_GOOGLE_REQUEST_CODE:
    *
    *                      GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
    *                      if (result.isSuccess()) {
    *                         GoogleIntegration.getInstance(this).saveGooglePlusLoginData(result);
    *                      }
    *                      break;
    *
    *       9.2.3) Go to step 10
    *
    * 10. Manage data
    *
    *     10.1) Store or manage user's required information
    *            use saveGooglePlusLoginData to get the response
    *
    * *************************************************************************************************
    **/


    /**
     * To get a GoogleIntegration class instance
     *
     * @param mActivityContext : context
     * @return googleIntegration (GoogleIntegration) : it return GoogleIntegration instance
     */
    public static synchronized GoogleIntegration getInstance(Activity mActivityContext) {
        activityContext = mActivityContext;
        if (googleIntegration == null) {
            googleIntegration = new GoogleIntegration();
        }
        return googleIntegration;
    }

    /**
     * This method use to check that google plus application installed in
     * device or not
     *
     * @return boolean (boolean) : it return true if application is installed
     * otherwise it return false
     */
    public static boolean isGooglePlusAppInstalled() {

        PackageManager pm = MyApplication.getInstance().getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(GOOGLE_PLUS_APP_PACKAGE_NAME, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }


    /**
     * This method initialise google login sdk. Initialise google sdk in your activity or
     * fragment before {@link Activity#setContentView(int)} method in
     * {@link Activity#onCreate(Bundle)} like
     * <pre>{@code
     * @Override
     * protected void onCreate(Bundle savedInstanceState) {
     * super.onCreate(savedInstanceState);
     *
     * GoogleIntegration.getInstance(this).initGoogleSdk();
     *
     * setContentView(R.layout.activity_facebook);
     *
     * }
     * }
     * </pre>
     */
    public void initGoogleSdk() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                /*developer need to define Web client id (auto created by Google Service)
                 * refer step 7 how to generate android client id */
                .requestIdToken(MyApplication.getInstance().getString(R.string.google_default_web_client_id))
                .requestProfile()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(MyApplication.getInstance())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    /**
     * This method keeps google login, if activity run in background
     * regularly this method called in onStart() method of activity
     */
    public void onStartGoogle() {

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);

        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            GoogleSignInResult result = opr.get();

        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    CustomDialog.getInstance().hide();

                }
            });
        }
    }

    /**
     * This method do Log In to Google account
     *
     * @param dataObserver (DataObserver) : interface object
     */
    public void logInToGoogle(DataObserver dataObserver) {
        this.dataObserver = dataObserver;
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        activityContext.startActivityForResult(signInIntent, DEFAULT_AUTH_GOOGLE_REQUEST_CODE);
    }

    /**
     * This method log out the current logged in user from google account
     */
    public void logOutToGoogle() {

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            // need to done code as per requirement
                        }

                    }
                });


    }


    /**
     * This method revoke the Access of Google login account
     * It is highly recommended that you provide users that signed in with Google the ability to
     * disconnect their Google account from your app. If the user deletes their account,
     * you must delete the information that your app obtained from the Google APIs.
     * <p>
     * Note: You must confirm that GoogleApiClient.onConnected has been called before you call revokeAccess.
     */
    private void revokeAccessGoogle() {

        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {

                    }
                });
    }

    /**
     * This method use to get google logged in information
     *
     * @param result (GoogleSignInResult) : class object
     * @see GoogleSignInResult#getSignInAccount()
     */
    public void saveGooglePlusLoginData(GoogleSignInResult result) {
        if (result.getSignInAccount() != null) {

            String firstName = result.getSignInAccount().getGivenName();
            String lastName = result.getSignInAccount().getFamilyName();
            String email = result.getSignInAccount().getEmail();
            int registerBy = RegisterBy.GOOGLE.getType();
            String socialMediaUserId = result.getSignInAccount().getId();

            RegisterCustomerModel registerUser = new RegisterCustomerModel();
            CustomerDetails customerDetails = new CustomerDetails();
            customerDetails.setFirstName(firstName);
            customerDetails.setLastName(lastName);
            customerDetails.setEmail(email);
            customerDetails.setRegisterBy(registerBy);
            customerDetails.setSocialMediaUserId(socialMediaUserId);

            registerUser.setCustomerDetails(customerDetails);

            Debug.trace(TAG, customerDetails.toString());

            registerUser.callRegisterCustomerAPI(activityContext, dataObserver);
        } else {
            Debug.trace(TAG, "GoogleSignInResult.getSignInAccount() has no details");
        }

    }

    /**
     * It open dialog and show preview whatever we are sharing.
     *
     * @param contentTitle (String) : content title or description
     * @param contentUrl   (String) : content url that to be hyper link on its website
     * @see <a href ="https://developers.google.com/+/mobile/android/share/"> Adding basic sharing </a>
     */
    public void shareContentWithGooglePlus(String contentTitle, String contentUrl) {

        if (!TextUtils.isEmpty(contentTitle) && !TextUtils.isEmpty(contentUrl) && URLUtil.isNetworkUrl(contentUrl)) {
            Intent shareIntent = new PlusShare.Builder(activityContext)
                    .setType(GOOGLE_SHARE_CONTENT_TYPE_TEXT)
                    .setText(contentTitle)
                    .setContentUrl(Uri.parse(contentUrl))
                    .getIntent();

            activityContext.startActivityForResult(shareIntent, DEFAULT_AUTH_GOOGLE_SHARE_REQUEST_CODE);
        }
    }

    /**
     * TODO stub is generated but developer or programmer need to add code as required.
     * To post image with google Plus.
     *
     * @param imageUri (String) : image Uri
     * @see <a href="https://developers.google.com/+/mobile/android/share/media"> Share an image </a>
     */
    public void shareDynamicBitmapWithGooglePlus(String imageUri) {

        if (!TextUtils.isEmpty(imageUri) && URLUtil.isNetworkUrl(imageUri)) {

            Intent shareIntent = new PlusShare.Builder(activityContext)
                    .setType(GOOGLE_SHARE_CONTENT_TYPE_IMAGE)
                    .addStream(Uri.parse(imageUri))
                    .getIntent();

            activityContext.startActivityForResult(shareIntent, DEFAULT_AUTH_GOOGLE_SHARE_REQUEST_CODE);

        }

    }

    /**
     * TODO stub is generated but developer or programmer need to add code as required.
     * To post image with google Plus.
     *
     * @param contentTitle (String) : content caption (title) that to be shared
     * @param imageUri     (String) : image Uri
     * @see <a href="https://developers.google.com/+/mobile/android/share/media"> Share an image </a>
     */
    public void shareDynamicBitmapWithGooglePlus(String contentTitle, String imageUri) {

        if (!TextUtils.isEmpty(contentTitle) && !TextUtils.isEmpty(imageUri) && URLUtil.isNetworkUrl(imageUri)) {

            Intent shareIntent = new PlusShare.Builder(activityContext)
                    .setType(GOOGLE_SHARE_CONTENT_TYPE_IMAGE)
                    .setText(contentTitle)
                    .addStream(Uri.parse(imageUri))
                    .getIntent();

            activityContext.startActivityForResult(shareIntent, DEFAULT_AUTH_GOOGLE_SHARE_REQUEST_CODE);

        }

    }
}
