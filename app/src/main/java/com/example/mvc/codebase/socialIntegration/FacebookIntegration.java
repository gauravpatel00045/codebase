package com.example.mvc.codebase.socialIntegration;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.webkit.URLUtil;

import com.example.mvc.codebase.MyApplication;
import com.example.mvc.codebase.R;
import com.example.mvc.codebase.enumerations.RegisterBy;
import com.example.mvc.codebase.helper.ToastHelper;
import com.example.mvc.codebase.interfaces.DataObserver;
import com.example.mvc.codebase.models.CustomerDetails;
import com.example.mvc.codebase.models.RegisterCustomerModel;
import com.example.mvc.codebase.utils.Constants;
import com.example.mvc.codebase.utils.Debug;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/*
 * Todo: Before implement facebook login integration you must follow below steps.
 *
 * 1. Import latest facebook plugin
 *    There are two ways to setup facebook plugin
 *         @Note : prefer step "1.2" to install facebook plugin
 *                 it gives latest Graph request version with gradle
 *
 *    1.1) Download facebook sdk
 *         @ download link : https://developers.facebook.com/docs/android/
 *
 *         1.1.1) Link Facebook SDK
 *
 *                Add Module Dependency For Facebook SDK
 *                refer below link how to add module in Android studio
 *                 @link : https://developer.android.com/studio/projects/android-library.html
 *                 @link : http://www.theappguruz.com/blog/library-project-module-as-dependency-in-android-studio
 *
 *         1.1.2) follow step 2
 *
 *    1.2) Import latest facebook gradle file in app level build.gradle
 *
 *         @link : https://developers.facebook.com/docs/android/downloads/
 *
 *         dependency: 'compile 'com.facebook.android:facebook-android-sdk:4.+'
 *
 *          till 28-1-2017 latest version is 4.19.0.
 *
 *         GraphRequest API version 2.8 available until At least October 2018.
 *         @link : https://developers.facebook.com/docs/apps/changelog
 *
 *         1.2.1) follow step 2
 *
 * 2. Generate hash key (application signature)
 *     @Note : Prefer to generate signed apk hash key
 *             Refer below link how to generate signed apk
 *             @link : https://developer.android.com/studio/publish/app-signing.html
 *
 *    To generate hash key there are two ways
 *
 *    2.1) run command prompt
 *
 *         2.1.1) Get debug hash key
 *                open (command prompt) cmd and run following command
 *                  @link : http://www.androidhive.info/2012/03/android-facebook-connect-tutorial/
 *                copy below code in cmd
 *                keytool -exportcert -alias androiddebugkey -keystore "C:\Users\as4\.android\debug.keystore" | openssl sha1 -binary | openssl base64
 *                                                                OR
 *                @link: http://stackoverflow.com/questions/4388992/key-hash-for-android-facebook-app
 *                   -> keytool -exportcert -alias androiddebugkey -keystore "C:\Users\as4\.android\debug.keystore" | "C:\OpenSSL\bin\openssl" sha1 -binary | openssl base64
 *                  in above command use your
 *                       i) debug.keystore path
 *                       ii) openssl path
 *
 *         2.1.2) Get release or signed apk hash key
 *                run cmd and copy below link
 *                keytool -exportcert -alias <aliasNameUseInReleaseKeystore> -keystore <ReleasekeystoreFilePath> | openssl sha1 -binary | openssl base64
 *
 *                e.g. keytool -exportcert -alias codebase -keystore D:\Codebase Documentation | openssl sha1 -binary | openssl base64
 *
 *   2.2) use getFBHashKey method
 *
 * 3. Register your project on facebook developer site
 *
 *    3.1) Login with facebook account credentials in facebook developer site
 *          @link : https://developers.facebook.com
 *
 *    3.2) Go to facebook developer site and follow required step to register your project
 *          @link: https://developers.facebook.com/docs/android/getting-started
 *
 *    3.3) Select option "Quick start for Android"
 *         @refer below link to open dashboard
 *         @link : https://developers.facebook.com/quickstarts
 *
 *         Follow below steps that mentioned on facebook developer sites
 *         Here only name mentioned and other details mentioned in developer site
 *         @link : https://developers.facebook.com/quickstarts
 *
 *         3.3.1) Import SDK
 *
 *         3.3.2) Add SDK
 *
 *         3.3.3) App info
 *
 *         3.3.4) Key Hash
 *
 *         3.3.5) App Events
 *
 *         3.3.6) Finished
 *
 * 4. Save facebook app id
 *
 *    4.1) After generating facebook app id you need to save in notepad,
 *          as this id will be use for future purpose in your app
 *
 * 5. Define required information in facebook developer console
 *
 *    5.1) Define hash key, package name and other required information in facebook developer console
 *           Go to facebook developer site and open your project dashboard
 *           @link : https://developers.facebook.com/docs/
 *    5.2) Go to setting and define project package name, class name, key hash and required data
 *
 * 6. Now user ready to access following methods
 *
 *   6.1) refer step 7 how to access methods
 *
 */

public class FacebookIntegration {

    //variable declaration
    private static final String TAG = FacebookIntegration.class.getName();
    /* Request code for login authentication yet this request code is not predefined */
    public static final int DEFAULT_AUTH_FACEBOOK_REQUEST_CODE = 64206;
    /* Request code to share post in facebook yet this request code is not predefined */
    public static final int DEFAULT_AUTH_FACEBOOK_SHARE_REQUEST_CODE = 64207;

    private static final String FACEBOOK_APP_PACKAGE_NAME = "com.facebook.katana";
    private static final String FB_REQUEST_PARAMETER = "id, first_name, last_name, email,gender,location";
    private static final String FIELDS = "fields";

    /**
     * To get a profile picture use this link like
     * <pre>{@code
     * https://graph.facebook.com/ + socialMediaUserId + /picture?type=large;
     * https://graph.facebook.com/145432062594143/picture?type=large;
     * }
     *                          OR
     * String fbProfileImageUrl = String.format(activityContext.getString(R.string.strFbProfileImageUrl), socialMediaUserId);
     * </pre>
     */
    public static final String PROFILE_PICTURE_GRAPH_FRONT_URL = "https://graph.facebook.com/";
    public static final String PROFILE_PICTURE_GRAPH_REAR_URL = "/picture?type=large";


    //class object declaration

    /**
     * Extended permissions give access to more sensitive information and allows the app to publish and delete data.
     * Provides the ability to publish Posts, Open Graph actions, achievements, scores and other activity on behalf of the user.
     */
    private List<String> publishActionPermissionNeeds = Collections.singletonList("publish_actions");

    private final List<String> accessUserDetailPermission = Arrays.asList("public_profile", "email");
    /**
     * if you want to use extend permission e.g. user_birthday, user_location, user_hometown
     * you need to submit your app into app_review section, and for testing purpose you need
     * to add one tester account
     * <p>
     * i) Go to facebook developer console and login with credentials.
     * Go to <a href="https://developers.facebook.com">Facebook developer console</a>
     * <br> ii) Go to myApps and select your project</br>
     * <br>iii) Go to "Roles" from your left panel</br>
     * <br>iv) select "Testers" and add testers</br>
     * <br>v) add facebook id and submit it</br>
     * <p>
     * until facebook pass review you can get data for that extened permission in your tester account
     */
    private List<String> accessUserDetailsPermission = Arrays.asList("public_profile", "email", "user_birthday", "user_location");


    // class object declaration
    private static FacebookIntegration fbInstance;
    private static Activity activityContext;
    public CallbackManager callbackManager;
    @SuppressWarnings("FieldCanBeLocal")
    private AccessToken FBAccessToken;
    public ShareDialog fbShareDialog;


    //constructor
    private FacebookIntegration() {

    }




    /*======================== Facebook relevant integration methods ========================*/

    /*
     ******************************************************************************************************
     * 7. Now user ready to access following methods
     *
     *    TODO: Step 7 to access methods:
     *    7.1) Import "socialIntegration" package or required class (FacebookIntegration.java)

     *    7.2) Initialize facebook sdk
     *
     *         7.2.1) Copy below code in your activity before setContentView(R.layout.XXX);
     *                 in onCreate(Bundle savedInstanceState) method
     *
     *                 FacebookIntegration.getInstance(this).initFacebookSdk();
     *
     *    7.3) Perform button click event action
     *
     *         7.3.1) Copy below code in onclick of your custom facebook login button
     *
     *                 FacebookIntegration.getInstance(this).logInToFacebook(this,this);
     *
     *                 "this" - dataObserver instance
     *
     *
     *  8. Implement onActivityResult(int requestCode, int resultCode, Intent data) method
     *
     *     8.1) Implementation in Activity
     *
     *         8.1.1) Manage response that comes in onActivityResult(int requestCode, int resultCode, Intent data)
     *               in your activity
     *
     *         copy below code in onActivityResult(int requestCode, int resultCode, Intent data)
     *                 method in your activity
     *
     *         switch (requestCode){
     *
     *                case FacebookIntegration.DEFAULT_AUTH_FACEBOOK_REQUEST_CODE:
     *
     *                    FacebookIntegration.getInstance(this).callbackManager.onActivityResult(requestCode, resultCode, data);
     *                     if (facebookIntegration.isLoggedInWithFacebook()) {
     *
     *                     }
     *                   break;
     *
     *         8.1.2) Go to step 9
     *
     *     8.2) Implementation in Fragment
     *
     *          8.2.1) copy below code in your parent activity where fragment loads.
     *
     *                  @Override
     *               protected void onActivityResult(int requestCode, int resultCode, Intent data) {
     *                   super.onActivityResult(requestCode, resultCode, data);
     *                     for (Fragment fragment : getSupportFragmentManager().getFragments()) {
     *                      fragment.onActivityResult(requestCode, resultCode, data);
     *                         }
     *                     }
     *
     *                  @Override
     *              public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
     *                 super.onRequestPermissionsResult(requestCode, permissions, grantResults);
     *                     for (Fragment fragment : getSupportFragmentManager().getFragments()) {
     *                           fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
     *                        }
     *                     }
     *
     *         8.2.2) implement onActivityResult() method in fragment
     *                Now you will get the result in fragment
     *
     *                copy below code in onActivityResult(int requestCode, int resultCode, Intent data)
     *                 method in your fragment
     *
     *         switch (requestCode){
     *
     *                case FacebookIntegration.DEFAULT_AUTH_FACEBOOK_REQUEST_CODE:
     *
     *                    FacebookIntegration.getInstance(this).callbackManager.onActivityResult(requestCode, resultCode, data);
     *                     if (facebookIntegration.isLoggedInWithFacebook()) {
     *
     *                     }
     *                   break;
     *
     *         8.2.3) Go to step 9
     *
     * 9. Store or Manage user's required information
     *     use getFBUserDetails method to get the response
     *
     * 10. Access additional information like e.g. user_birthday, user_location
     *         if you want to use extend permission e.g. user_birthday, user_location, user_hometown
     *         you need to submit your app into app_review section, and for testing purpose you need
     *         to add one tester account
     *
     *         10.1.1) Go to facebook developer console and login with credentials
     *                @link : https://developers.facebook.com
     *
     *         10.1.2) Go to myApps and select your project
     *
     *         10.1.3) Go to "Roles" from your left panel
     *
     *         10.1.4) select "Testers" and add testers
     *
     *         10.1.5) add facebook id and submit it
     *
     * 11. Share with facebook
     *      refer below link for more information
     *      @ link : https://developers.facebook.com/docs/sharing/android
     *
     *    11.1) define ContentProvider in manifest
     *         You also need to set up a ContentProvider in your AndroidManifest.xml
     *         where {APP_ID} is your app ID:
     *
     *        <provider android:authorities="com.facebook.app.FacebookContentProvider{APP_ID}"
     *           android:name="com.facebook.FacebookContentProvider"
     *           android:exported="true"/>
     *          e.g.
     *              <provider
     *              android:name="com.facebook.FacebookContentProvider"
     *              android:authorities="com.facebook.app.FacebookContentProvider177030759423308"
     *              android:exported="true" />
     *
     *    11.2) use publish_action permission when we use below method
     *         loginToFaceBook(final Activity activityContext, final DataObserver dataObserver)
     *
     *        e.g.
     *        public void loginToFaceBook(final Activity activityContext,
     *                  final DataObserver dataObserver) {
     *
     *          LoginManager.getInstance().logInWithPublishPermissions(activityContext,
     *              publishActionPermissionNeeds);
     *          LoginManager.getInstance().registerCallback(callbackManager,
     *                      new FacebookCallback<LoginResult>() {
     *                  //
     *                  }
     *             }
     *
     *    11.3) call sharing methods on click event action
     **********************************************************************************************
     **/


    /**
     * To get a FacebookIntegration class instance
     *
     * @param mActivityContext : context
     * @return fbInstance (FacebookIntegration) : it return FacebookIntegration instance
     */
    public static synchronized FacebookIntegration getInstance(Activity mActivityContext) {
        activityContext = mActivityContext;
        if (fbInstance == null) {
            fbInstance = new FacebookIntegration();
        }
        return fbInstance;
    }

    /**
     * This method use to check that facebook application installed in
     * device or not
     *
     * @return boolean (boolean) : it return true if application is installed
     * otherwise it return false
     */
    public static boolean isFacebookAppInstalled() {

        PackageManager pm = MyApplication.getInstance().getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(FACEBOOK_APP_PACKAGE_NAME, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }


    /**
     * This method initialise facebook login sdk. Initialise facebook sdk in your activity or
     * fragment before {@link Activity#setContentView(int)} method in
     * {@link Activity#onCreate(Bundle)} like
     * <pre>{@code
     * @Override
     * protected void onCreate(Bundle savedInstanceState) {
     * super.onCreate(savedInstanceState);
     *
     * facebookIntegration = new FacebookIntegration();
     * FacebookIntegration.getInstance(this).initFacebookSdk();
     *
     * setContentView(R.layout.activity_facebook);
     *
     * }
     * }
     * </pre>
     *
     * @see #getFbHashKey()
     */
    public void initFacebookSdk() {
        AppEventsLogger.activateApp(MyApplication.getInstance());
        callbackManager = CallbackManager.Factory.create();
        getFbHashKey();
    }


    /**
     * This method initialise facebook share Dialog. Initialise facebook share dialog in your
     * activity or fragment above {@link Activity#setContentView(int)} method in
     * {@link Activity#onCreate(Bundle)} method like
     * <pre>{@code
     * @Override
     * protected void onCreate(Bundle savedInstanceState) {
     * super.onCreate(savedInstanceState);
     *
     * FacebookIntegration.getInstance(this).initFacebookShareDialog();
     *
     * setContentView(R.layout.activity_facebook);
     *
     * }
     * }
     * </pre>
     *
     * @see <a href="https://developers.facebook.com/docs/sharing/android/">Share Dialog</a>
     */
    public void initFacebookShareDialog() {
        // initialize ShareDialog object
        fbShareDialog = new ShareDialog(activityContext);

        fbShareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Debug.trace(MyApplication.getInstance().getString(R.string.strShareSuccess));
            }

            @Override
            public void onCancel() {
                Debug.trace(MyApplication.getInstance().getString(R.string.strShareFail));
            }

            @Override
            public void onError(FacebookException error) {
                Debug.trace(MyApplication.getInstance().getString(R.string.strShareFail));
            }
        });
    }

    /**
     * The key hash value is used by Facebook as security check for login.
     * To get key hash value of your machine, write following code in onCreate method
     */
    public void getFbHashKey() {
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
     * This method open facebook login page and ask for permission to access user's basic information.
     * With this method you do not need to use facebook login button.
     *
     * @param dataObserver (DataObserver) : dataObserver instance
     * @see DataObserver
     * @see #accessUserDetailPermission
     * @see LoginManager#logInWithReadPermissions(Activity, Collection)
     * @see LoginManager#registerCallback(CallbackManager, FacebookCallback)
     * @see #getFBUserDetails(DataObserver)
     */
    public void loginToFaceBook(final DataObserver dataObserver) {
        // This LoginManager helps you eliminate adding a facebook LoginButton to your UI
        LoginManager.getInstance().logInWithReadPermissions(activityContext, accessUserDetailPermission);
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                getFBUserDetails(dataObserver);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                ToastHelper.getInstance(activityContext).displayCustomToast(activityContext.getString(R.string.errorMsgLoginFailed));
            }
        });
    }

    /**
     * This method gives the user's information that GraphRequest requested and
     * request approved by user like id,first_name,last_name,email and etc.
     *
     * @param dataObserver (DataObserver) : dataObserver instance
     * @see GraphRequest
     * @see #FB_REQUEST_PARAMETER
     */
    private void getFBUserDetails(final DataObserver dataObserver) {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        Debug.trace(TAG, object.toString());

                        String firstName = object.optString(Constants.KEY_FACEBOOK_FIRST_NAME);
                        String lastName = object.optString(Constants.KEY_FACEBOOK_LAST_NAME);
                        String email = object.optString(Constants.KEY_FACEBOOK_EMAIL);
                        int registerBy = RegisterBy.FACEBOOK.getType();
                        String socialMediaUserId = object.optString(Constants.KEY_FACEBOOK_ID);

                        String fbProfileImageUrl = String.format(activityContext.getString(R.string.strFbProfileImageUrl), socialMediaUserId);

                        RegisterCustomerModel registerUser = new RegisterCustomerModel();
                        CustomerDetails customerDetails = new CustomerDetails();
                        customerDetails.setFirstName(firstName);
                        customerDetails.setLastName(lastName);
                        customerDetails.setEmail(email);
                        customerDetails.setRegisterBy(registerBy);
                        customerDetails.setSocialMediaUserId(socialMediaUserId);
                        registerUser.setCustomerDetails(customerDetails);
                        registerUser.callRegisterCustomerAPI(activityContext, dataObserver);

                    }
                }
        );
        Bundle parameters = new Bundle();
        parameters.putString(FIELDS, FB_REQUEST_PARAMETER);
        request.setParameters(parameters);
        request.executeAsync();

    }

    /**
     * This method check user logged in with facebook id than it will return it's access token
     *
     * @return FBAccessToken (boolean)  : it return current logged in user's active access token
     */
    public boolean isLoggedInWithFacebook() {
        FBAccessToken = AccessToken.getCurrentAccessToken();
        return FBAccessToken != null;
    }

    /**
     * This method destroy the current access token.
     *
     * @see LoginManager#logOut()
     */
    public void logoutToFacebook() {
        LoginManager.getInstance().logOut();
    }


    /**
     * it open dialog and show preview whatever we are sharing. Make sure that you should use
     * publish_action permission {@link #publishActionPermissionNeeds} in
     * {@link #loginToFaceBook(DataObserver)} while login like
     * <pre>{@code
     * public void loginToFaceBook(final Activity activityContext, final DataObserver dataObserver) {
     *
     * LoginManager.getInstance().logInWithPublishPermissions(activityContext, publishActionPermissionNeeds);
     * LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
     *          //
     *  }
     *   }
     * }
     * </pre>
     * Note : facebook is not allow to post only text from anywhere
     * (neither from SDK nor from ShareIntent). You must pass imageUrl and contentUrl
     * in proper format. like
     * <pre>{@code
     * imageUrl = http://cdn.narendramodi.in/wp-content/uploads/2012/07/kakariya-nner2.jpg
     * contentUrl = http://www.narendramodi.in/
     * }
     * </pre>
     *
     * @param contentTitle       (String) : content title
     * @param contentDescription (String) : content description
     * @param imageUrl           (String) : image url
     * @param contentUrl         (String) : content url
     * @see <a href="https://developers.facebook.com/docs/sharing/android">Modeling Content</a>
     */
    public void shareContentWithDialog(String contentTitle, String contentDescription, String imageUrl, String contentUrl) {

        if (!TextUtils.isEmpty(imageUrl) && !TextUtils.isEmpty(contentUrl) && URLUtil.isNetworkUrl(imageUrl) && URLUtil.isNetworkUrl(contentUrl)) {

            if (ShareDialog.canShow(ShareLinkContent.class)) {
                ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                        .setContentTitle((!TextUtils.isEmpty(contentDescription)) ? contentTitle : Constants.DEFAULT_BLANK_STRING)
                        .setContentDescription((!TextUtils.isEmpty(contentDescription)) ? contentDescription : Constants.DEFAULT_BLANK_STRING)
                        .setImageUrl(Uri.parse(imageUrl))
                        .setContentUrl(Uri.parse(contentUrl))
                        .build();

                fbShareDialog.show(shareLinkContent);
            }

        }
    }

    /**
     * To post image (bitmap) with facebook.
     * Make sure that you should use publish_action permission {@link #publishActionPermissionNeeds}
     * in {@link #loginToFaceBook(DataObserver)} while login like
     * <pre>{@code
     * public void loginToFaceBook(final Activity activityContext, final DataObserver dataObserver) {
     *
     * LoginManager.getInstance().logInWithPublishPermissions(activityContext, publishActionPermissionNeeds);
     * LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
     *          //
     *  }
     *   }
     * }
     * </pre>
     * <p>
     * Note : FacebookCallback is optional. it gives call back on Success method when sharing done
     * pass null in {@link ShareApi#share(ShareContent, FacebookCallback)} method like
     * <pre>{@code
     *  ShareApi.share(photoContent, new FacebookCallback<Sharer.Result>(){}); // when u want callback
     *                  OR
     *  ShareApi.share(photoContent, null); // when u do not want callback result
     *  }</pre>
     * </p>
     */
    public void shareStaticBitmapWithFb() {

        Bitmap image = BitmapFactory.decodeResource(MyApplication.getInstance().getResources(), R.drawable.abdulkalam_slogan);

        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
                .setCaption(Constants.FACEBOOK_SHARE_PHOTO_CAPTION)
                .build();

        SharePhotoContent photoContent = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        ShareApi.share(photoContent, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Debug.trace(MyApplication.getInstance().getString(R.string.strShareStaticBitmapWithFb));
            }

            @Override
            public void onCancel() {
                Debug.trace(MyApplication.getInstance().getString(R.string.strShareFail));
            }

            @Override
            public void onError(FacebookException error) {
                Debug.trace(MyApplication.getInstance().getString(R.string.strShareFail));

            }
        });

    }

    /**
     * To post image (bitmap) with facebook.
     * you should convert image in bitmap. Refer this {@link com.example.mvc.codebase.graphicsUtils.GraphicsUtil#convertImageInBitmap(String)} method.
     * <br>Make sure that you should use publish_action permission {@link #publishActionPermissionNeeds}
     * in {@link #loginToFaceBook(DataObserver)} while login like</br>
     * <pre>{@code
     * public void loginToFaceBook(final Activity activityContext, final DataObserver dataObserver) {
     *
     * LoginManager.getInstance().logInWithPublishPermissions(activityContext, publishActionPermissionNeeds);
     * LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
     *          //
     *  }
     *   }
     * }
     * </pre>
     * <p>
     * Note : FacebookCallback is optional. it gives call back on Success method when sharing done
     * pass null in {@link ShareApi#share(ShareContent, FacebookCallback)} method like
     * <pre>{@code
     * ShareApi.share(photoContent, new FacebookCallback<Sharer.Result>(){}); // required callback
     * OR
     * ShareApi.share(photoContent, null); // Do not required callback result
     * }</pre>
     * </p>
     *
     * @param bitmap (Bitmap) : image that to be share with facebook
     */
    public void shareDynamicBitmapWithFb(Bitmap bitmap) {

        if (bitmap != null) {

            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();

            SharePhotoContent photoContent = new SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build();

            ShareApi.share(photoContent, new FacebookCallback<Sharer.Result>() {
                @Override
                public void onSuccess(Sharer.Result result) {
                    Debug.trace(MyApplication.getInstance().getString(R.string.strShareDynamicBitmapWithFb));
                }

                @Override
                public void onCancel() {
                    Debug.trace(MyApplication.getInstance().getString(R.string.strShareFail));

                }

                @Override
                public void onError(FacebookException error) {
                    Debug.trace(MyApplication.getInstance().getString(R.string.strShareFail));

                }
            });

        }
    }

    /**
     * To post image (bitmap) with facebook.
     * you should convert image in bitmap. Refer this {@link com.example.mvc.codebase.graphicsUtils.GraphicsUtil#convertImageInBitmap(String)} method.
     * <br>Make sure that you should use publish_action permission {@link #publishActionPermissionNeeds}
     * in {@link #loginToFaceBook(DataObserver)} while login like</br>
     * <pre>{@code
     * public void loginToFaceBook(final Activity activityContext, final DataObserver dataObserver) {
     *
     * LoginManager.getInstance().logInWithPublishPermissions(activityContext, publishActionPermissionNeeds);
     * LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
     *          //
     *  }
     *   }
     * }
     * </pre>
     * <p>
     * <strong> Note: </strong> FacebookCallback is optional. it gives call back on Success method
     * when sharing done pass null in {@link ShareApi#share(ShareContent, FacebookCallback)} method
     * like
     * <pre>{@code
     * ShareApi.share(photoContent, new FacebookCallback<Sharer.Result>(){}); // when you want callback
     * OR
     * ShareApi.share(photoContent, null); // when you do not want callback result
     * }</pre>
     * </p>
     *
     * @param bitmap  (Bitmap) : image that to be share with facebook
     * @param caption (String) : caption (title) for the photo
     */
    public void shareDynamicBitmapWithFb(Bitmap bitmap, String caption) {

        if (bitmap != null && !TextUtils.isEmpty(caption)) {

            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .setCaption(caption)
                    .build();

            SharePhotoContent photoContent = new SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build();

            ShareApi.share(photoContent, new FacebookCallback<Sharer.Result>() {
                @Override
                public void onSuccess(Sharer.Result result) {
                    Debug.trace(MyApplication.getInstance().getString(R.string.strShareDynamicBitmapAndCaptionWithFb));
                }

                @Override
                public void onCancel() {
                    Debug.trace(MyApplication.getInstance().getString(R.string.strShareFail));

                }

                @Override
                public void onError(FacebookException error) {
                    Debug.trace(MyApplication.getInstance().getString(R.string.strShareFail));

                }
            });

        }
    }

    /**
     * To share link content with facebook. Using shareDialog we can share with facebook no matter
     * that user have installed app in their device or not. It will open web view automatically
     * and after logged in, user can share content with facebook.
     * <br></br>
     * <pre
     * {@code
     * public void onClickEvent(View view) {
     * switch (view.getId()) {
     * case R.id.xxx:
     * if (Util.checkInternetConnection()) {
     * facebookIntegration.shareLinkContentWithFb(this,imageUrl);
     * } else {
     * // Perform relevant action
     * }
     * break;
     * }
     * }
     * }</pre>
     *
     * @param imageUrl (String) : imageUrl
     */
    public void shareLinkContentWithFb(String imageUrl) {

        if (!TextUtils.isEmpty(imageUrl) && URLUtil.isNetworkUrl(imageUrl)) {

            ShareDialog shareDialog = new ShareDialog(activityContext);
            ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                    .setImageUrl(Uri.parse(imageUrl))
                    .build();

            shareDialog.show(shareLinkContent);

        }

    }
}
