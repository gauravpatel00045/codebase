package com.example.mvc.codebase;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.graphics.Typeface;
import android.text.TextUtils;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.mvc.codebase.utils.Constants;
import com.example.mvc.codebase.utils.MyLifecycleHandler;

import java.util.HashMap;

/**
 * Base class for those who need to maintain global application state.
 * You can provide your own implementation by specifying its name in your AndroidManifest.xml's <application> tag,
 * which will cause that class to be instantiated for you when the process for your application/package is created.
 * In most situation, static singletons can provide the same functionality in a more modular way.
 * If your singleton needs a global context (for example to register broadcast receivers),
 * the function to retrieve it can be given a Context which internally uses Context.getApplicationContext()
 * when first constructing the singleton.
 */

public class MyApplication extends Application {

    //variable declaration
    private static final String TAG = MyApplication.class.getSimpleName();
    // Store typeface into hashMap, and Use this var to access typeface
    public static HashMap<String, Typeface> mTypefaceMap;
    // Store message list with key and value
    public static HashMap<String, String> msgHashMap;
    /* Notification based variables */
    public static boolean isAppRunning = false;


    //class object declaration
    @SuppressLint("StaticFieldLeak")
    static MyApplication appInstance;
    public MyLifecycleHandler myLifecycleHandler;
    // Declare Globally user classes
    private RequestQueue mRequestQueue;

    /**
     * This method return instance of application class
     *
     * @return appInstance (MyApplication): it return MyApplication instance
     */
    public static synchronized MyApplication getInstance() {
        return appInstance;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        // To catch Uncaught exception error and send error log to server.
        // Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        appInstance = this;

        myLifecycleHandler = new MyLifecycleHandler();
        registerActivityLifecycleCallbacks(myLifecycleHandler);
         /* Initialize typeface hashmap */
        mTypefaceMap = new HashMap<>();
        msgHashMap = new HashMap<>();

         /* set typeface and add in hashmap with name value pair */
        getTypeface(Constants.HELVETICA_CONDENSED_BOLD);
        getTypeface(Constants.MYRIADPRO_LIGHTSEMIEXT);
        getTypeface(Constants.HELVETICA_NEUE_LIGHT);
        getTypeface(Constants.OSWALD_BOLD);

    }

    /**
     * This method use to set TypeFace
     *
     * @param file (String) : file path from asset folder
     */
    public void getTypeface(final String file) {
        Typeface result = mTypefaceMap.get(file);
        if (result == null) {
            result = Typeface.createFromAsset(getAssets(), file);
            mTypefaceMap.put(file, result);
        }

    }

    /*-------------------------------Volley Related methods--------------------------------*/

    /**
     * This method add the request in the requestQueue with specific tag
     *
     * @param req (Request) : volley request type e.g. JSONObject,JSONArray,JSONString
     * @param tag (String)  : to identify the request type
     */
    public <T> void addToRequestQueue(String tag, Request<T> req) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    /**
     * This method returns the volley requestQueue
     *
     * @return mRequestQueue (RequestQueue)  : to return RequestQueue instance
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    /**
     * This method cancel pending request from requestQueue
     *
     * @param tag (String): tag to identify the request
     */
    public void cancelPendingRequests(String tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    /**
     * This method set timeout duration of Request
     *
     * @param req (Request) : volley request type e.g. JSONObject, JSONArray, JSONString
     */
    public <T> void setRequestTimeout(Request<T> req) {
        int requestTimeout = Constants.REQUEST_TIMEOUT;
        req.setRetryPolicy(new DefaultRetryPolicy(
                requestTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private Activity mCurrentActivity = null;

    public Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    public void setCurrentActivity(Activity mCurrentActivity) {
        this.mCurrentActivity = mCurrentActivity;
    }

}