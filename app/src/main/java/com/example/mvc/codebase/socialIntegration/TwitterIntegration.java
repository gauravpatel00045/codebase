package com.example.mvc.codebase.socialIntegration;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;

import com.example.mvc.codebase.MyApplication;
import com.example.mvc.codebase.R;
import com.example.mvc.codebase.enumerations.RegisterBy;
import com.example.mvc.codebase.helper.ToastHelper;
import com.example.mvc.codebase.interfaces.DataObserver;
import com.example.mvc.codebase.models.CustomerDetails;
import com.example.mvc.codebase.models.RegisterCustomerModel;
import com.example.mvc.codebase.utils.Constants;
import com.example.mvc.codebase.utils.Debug;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.fabric.sdk.android.Fabric;

import static android.support.v4.content.ContextCompat.startActivity;

/*
 * Todo: Before implement twitter login integration you must follow below steps.
 *
 * 1. Create a twitter fabric account and sign Up
 *
 *    1.1) Go to below link and create a twitter fabric account
 *         @link: https://fabric.io/sign_up?signup_campaign_id=https://get.fabric.io
 *
 *    1.2) Sign up login to twitter by below link
 *         @link: https://fabric.io/onboard
 *
 * 2. Now go to twitter application management
 *     @link: https://apps.twitter.com
 *
 * 3. Click on Create New App and fill the form to create your app.
 *
 * 4. After the creation you will get your API Key and API Secret
 *
 * 5. Installing Fabric in Android Studio
 *
 *     5.1) Go to file -> settings
 *     5.2) Click on Browser repositories.
 *     5.3) Search for Fabric
 *     5.4) Install Fabric for Android Studio and Restart Android Studio to complete the installation.
 *
 * 6. Adding Twitter to Your Android Project
 *
 *     6.1) Once your project is loaded, click on the fabric icon from the top to add Android Twitter Client
 *          or you can click right side of android studio screen where fabric is written
 *     6.2) Click on the power button now.
 *     6.3) Select organization and click on next.
 *     6.4) Now from the list click on Twitter.
 *     6.5) Now accept the license agreement and click on I already have an account.
 *     6.6) Now enter your API KEY and API SECRET and click on next.
 *          API KEY and API SECRET you will find from twitter application management dashboard
 *          @link: https://apps.twitter.com
 *     6.7) Now just click on apply.
 *     6.8) Now your project will sync with gradle. Wait for its completion.
 *     6.9) Now user ready to access following methods
 *     6.10) TODO Refer step 8 how to access methods
 *
 * 7. Access twitter email
 *
 *    7.1) Go to twitter developer console and login with credentials
 *         @link : https://dev.twitter.com/
 *
 *    7.2) Go to "My app" and select your project
 *
 *    7.3) Go to "Permission"
 *
 *         7.3.1) from "Access" - Select option "Read, Write and Access direct messages"
 *
 *         7.3.2) from "Additional Permissions"
 *                check true on "Request email addresses from users"
 *
 *         7.3.3) click on "Update Settings"
 *
 *    7.4) Go to "Settings"
 *
 *         7.4.1) add url in Privacy Policy URL and Terms of Service URL option
 *
 *                define url in "Privacy Policy URL" and "Terms of Service URL"
 *                e.g. http://codebase.on-linedemo.com/
 *
 *         7.4.2) click on "Update Settings"
 *
 *    7.5) Go to "Keys and Access Tokens"
 *
 *         7.5.1) Regenerate Consumer key and Secret
 *
 *                from "Application Settings" select option - "Regenerate Consumer key and Secret"
 *
 *    7.6) Update  Consumer key and Consumer Secret in your project
 *
 *         7.6.1) open fabric panel in android studio
 *
 *         7.6.2) Enter your account keys
 *
 *
 */
public class TwitterIntegration {

    // variable declaration
    private static final String TAG = TwitterIntegration.class.getName();
    /**
     * Note : request code for twitter integration is fix so you do not need to change it
     * refer {@link TwitterAuthConfig#DEFAULT_AUTH_REQUEST_CODE} to see default request code
     */
    public static final int DEFAULT_AUTH_TWITTER_REQUEST_CODE = 140;
    /*default request code to share post in twitter yet this request code is not predefined */
    public static final int DEFAULT_AUTH_TWITTER_SHARE_REQUEST_CODE = 141;

    private static final String TWITTER_APP_PACKAGE_NAME = "com.twitter.android";
    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "SXMFBxeI70XfFjGH9jry5JIj3";
    private static final String TWITTER_SECRET = "F2OGC3ozOuxAync7YILLfEzsAHQz28twTPVR8AiV8qlLciLFNi";

    // class object declaration
    private static TwitterIntegration twitterIntegration;
    private static Activity activityContext;
    Twitter twitter;
    public TwitterAuthClient mTwitterAuthClient;
    private TwitterSession twitterSession;

    // constructor
    private TwitterIntegration() {

    }

    /*======================== Twitter relevant integration methods ========================*/

    /*
    * TODO: Step 8 to access methods:
    *    8.1) Import "socialIntegration" package or required class (TwitterIntegration.java)
    *
    *    8.2) Initialize twitter sdk
    *
    *         8.2.1) Copy below code in your activity before setContentView(R.layout.XXX);
    *                 in onCreate(Bundle savedInstanceState) method
    *
    *                TwitterIntegration.getInstance(this).initTwitterSdk();
    *
    *    8.3) Perform button click event action
    *
    *         8.3.1) Copy below code in onclick of your custom twitter login button
    *
    *                 TwitterIntegration.getInstance(this).loginToTwitter(this);
    *
    *                 "this" - dataObserver
    *
    * 9. Implement onActivityResult(int requestCode, int resultCode, Intent data) method
    *
    *    9.1) Implementation in Activity
    *
    *        9.1.1) Manage response that comes in onActivityResult(int requestCode, int resultCode, Intent data)
    *               in your activity
    *
    *              copy below code in onActivityResult(int requestCode, int resultCode, Intent data)
    *                 method in your activity
    *
    *                switch (requestCode){
    *
    *                   case TwitterIntegration.DEFAULT_AUTH_TWITTER_REQUEST_CODE:
    *
    *                       TwitterIntegration.getInstance(this).mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
    *                       if (resultCode == Activity.RESULT_OK) {
    *
    *                        }
    *                       break;
    *
    *       9.1.2) Go to step 10
    *
    *    9.2) Implementation in Fragment
    *
    *         9.2.1) copy below code in your parent activity where fragment loads.
    *
    *                @Override
    *               protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    *               super.onActivityResult(requestCode, resultCode, data);
    *                   for (Fragment fragment : getSupportFragmentManager().getFragments()) {
    *                    fragment.onActivityResult(requestCode, resultCode, data);
    *                       }
    *                   }
    *
    *               @Override
    *              public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    *                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    *                    for (Fragment fragment : getSupportFragmentManager().getFragments()) {
    *                        fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    *                       }
    *                   }
    *
    *        9.2.2) implement onActivityResult() method in fragment
    *               Now you will get the result in fragment
    *
    *               copy below code in onActivityResult(int requestCode, int resultCode, Intent data)
    *                method in your fragment
    *
    *               switch (requestCode){
    *
    *                   case TwitterIntegration.DEFAULT_AUTH_TWITTER_REQUEST_CODE:
    *
    *                       TwitterIntegration.getInstance(this).mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
    *                       if (resultCode == Activity.RESULT_OK) {
    *
    *                        }
    *                       break;
    *
    *       9.2.3) Go to step 10
    *
    * 10. Manage data
    *
    *     10.1) Store or manage user's required information
    *
    *            use getTwitterUserDetails(Result<TwitterSession> twitterSessionResult) method
    *             TwitterSession twitterSession = twitterSessionResult.data;
    *
    *     10.2) To access email follow step 7
    *
    * TODO need to add required comments in this stub
    * 11. There are two ways to access permission
    *
    *    11.1)
    *
    *    11.2) Access requestEmail method
    *         Note: refer and follow step 7 before access this method
    *
    *         copy below code where you want to request for email
    *
    *         e.g.
    *         mTwitterAuthClient.requestEmail(twitterSessionResult.data, new Callback<String>() {
    *                @Override
    *                public void success(Result<String> result) {
    *
    *                   String twitterEmail = result.data;
    *
    *                  }
    *
    *                   @Override
    *                  public void failure(TwitterException exception) {
    *
    *                   }
    *             });
    *
    **/


    /**
     * To get a TwitterIntegration class instance
     *
     * @param mActivityContext : context
     * @return twitterIntegration (TwitterIntegration) : it return TwitterIntegration instance
     */
    public static synchronized TwitterIntegration getInstance(Activity mActivityContext) {
        activityContext = mActivityContext;
        if (twitterIntegration == null) {
            twitterIntegration = new TwitterIntegration();
        }
        return twitterIntegration;
    }

    /**
     * This method use to check that twitter application installed in
     * device or not
     *
     * @return boolean (boolean) : it return true if application is installed
     * otherwise it return false
     */
    public static boolean isTwitterAppInstalled() {

        PackageManager pm = MyApplication.getInstance().getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(TWITTER_APP_PACKAGE_NAME, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    /**
     * The key hash value is used by Twitter as security check for login.
     * To get key hash value of your machine, write following code in onCreate method
     */
    public void getHashKey() {
        try {
            @SuppressLint("PackageManagerGetSignatures")
            PackageInfo info = MyApplication.getInstance().getPackageManager().getPackageInfo(
                    MyApplication.getInstance().getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                Debug.trace("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    /**
     * This method initialise twitter login sdk. Initialise twitter sdk in your activity or
     * fragment before {@link Activity#setContentView(int)} method in
     * {@link Activity#onCreate(Bundle)} like
     * <pre>{@code
     * @Override
     * protected void onCreate(Bundle savedInstanceState) {
     * super.onCreate(savedInstanceState);
     *
     * TwitterIntegration.getInstance(this).initTwitterSdk(this);
     *
     * setContentView(R.layout.activity_facebook);
     *
     * }
     * }
     * </pre>
     */
    public void initTwitterSdk() {
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(MyApplication.getInstance(), new Twitter(authConfig));
        mTwitterAuthClient = new TwitterAuthClient();
    }

    /**
     * This method allow login to twitter, use onClick of twitter login button
     *
     * @param dataObserver (dataObserver) : dataObserver instance
     */
    public void loginToTwitter(final DataObserver dataObserver) {

        mTwitterAuthClient.authorize(activityContext, new Callback<TwitterSession>() {

            @Override
            public void success(Result<TwitterSession> twitterSessionResult) {
                twitterSession = Twitter.getSessionManager().getActiveSession();

                final String firstName = twitterSession.getUserName();
                final String socialMediaUserId = (String.valueOf(twitterSession.getUserId()));
                // request to access email
                mTwitterAuthClient.requestEmail(twitterSession, new Callback<String>() {
                    @Override
                    public void success(Result<String> result) {
                        final String twitterEmail = result.data;

                        setTwitterData(socialMediaUserId, firstName, twitterEmail, dataObserver);
                    }

                    @Override
                    public void failure(TwitterException exception) {

                        setTwitterData(socialMediaUserId, firstName, Constants.DEFAULT_BLANK_STRING, dataObserver);
                    }
                });


            }

            @Override
            public void failure(TwitterException e) {
                e.printStackTrace();
                ToastHelper.getInstance(activityContext).displayCustomToast(activityContext.getString(R.string.errorMsgLoginFailed));
            }
        });
    }

    /**
     * This method pass required details and make registerCustomer api call to login
     *
     * @param socialMediaUserId (String) : twitter social media user id
     * @param firstName         (String) : twitter username
     * @param twitterEmail      (String) : twitter user email
     * @param dataObserver      (dataObserver) : dataObserver instance
     */
    private void setTwitterData(String socialMediaUserId, String firstName,
                                String twitterEmail, DataObserver dataObserver) {

        final int registerBy = RegisterBy.TWITTER.getType();

        RegisterCustomerModel registerUser = new RegisterCustomerModel();
        CustomerDetails customerDetails = new CustomerDetails();

        customerDetails.setSocialMediaUserId(socialMediaUserId);
        customerDetails.setFirstName(firstName);
        customerDetails.setRegisterBy(registerBy);
        // if user allow to access email than we can use it
        if (!twitterEmail.equals(Constants.DEFAULT_BLANK_STRING)) {
            customerDetails.setEmail(twitterEmail);
        }

        registerUser.setCustomerDetails(customerDetails);

        registerUser.callRegisterCustomerAPI(activityContext, dataObserver);

        Debug.trace(TAG, registerUser.getCustomerDetails().toString());

    }

    /**
     * This method gives the user's information that will be comes on
     * {@link Callback#success(Result)} method.
     * <br> <strong> Example: </strong> To get Twitter data</br>
     * <pre>
     *      {@code
     *  String firstName = twitterSession.getUserName();
     *  String socialMediaUserId = (String.valueOf(twitterSession.getUserId()));
     *      }
     *  </pre>
     *
     * @param twitterSessionResult (Result<TwitterSession>) : class object
     */
    public void getTwitterUserDetails(Result<TwitterSession> twitterSessionResult) {
        twitterSession = twitterSessionResult.data;
    }

    /**
     * This method allow to logOut from Twitter by clearing the web cookies
     */
    public void logOutFromTwitter() {
        Twitter.logOut();
    }
}
