package com.example.mvc.codebase.api;

import android.content.Context;
import android.webkit.URLUtil;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.mvc.codebase.MyApplication;
import com.example.mvc.codebase.R;
import com.example.mvc.codebase.customdialog.CustomDialog;
import com.example.mvc.codebase.utils.Debug;
import com.example.mvc.codebase.utils.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class send API requests
 * using volley network library and verify response.
 * <p><strong>1.</strong> API calling </p>
 * post request using {@link RestClient#post(Context, int, String, JSONObject, RequestListener, RequestCode, Boolean)}.
 * <p><strong>2.</strong> Get Response</p>
 * Volley network call, response will get and handle in this two Listener.
 * <ul>
 * <li>{@link com.android.volley.Response.Listener#onResponse(Object)}</li>
 * <li>{@link com.android.volley.Response.ErrorListener#onErrorResponse(VolleyError)}</li>
 * </ul>
 * <p><strong>3.</strong> <code> onResponse </code> </p>
 * Verify response using {@link RestClient#verifyResponse(String, RequestCode, IListener)}
 * <br> In this method, it will parse the response and check the response status as per our
 * <strong>API Structure.</strong></br>
 * <p><strong>3.1</strong> <code> responseStatus.isSuccess() </code> </p>
 * Again response Verified using {@link RestClient#processSuccess(String, ResponseStatus, RequestCode, IListener)}
 * and go to the next step for parsing.
 * <p><strong>3.1.1</strong> parse Response </code> </p>
 * it parse the response using {@link ResponseManager#parse(RequestCode, String, Gson)}
 * <p><strong>3.2</strong> <code> responseStatus.isFail() </code> </p>
 * Handle error relevant action using {@link RestClient#processError(ResponseStatus, IListener)}
 * <p><strong>4.</strong><code>onErrorResponse</code></p>
 * It checks the error type and call it's relevant method.
 *
 * @see <a href="http://stackoverflow.com/questions/24700582/handle-volley-error">Handle Volley error</a>
 */
public class RestClient {

    // variable declaration
    private static final String TAG = RestClient.class.getSimpleName();

    // class object declaration
    public static final Gson gson;
    private static RestClient instance;

    /**
     * get the instance of the Gson
     * */
    static {
        gson = new GsonBuilder().enableComplexMapKeySerialization().serializeNulls().setPrettyPrinting().create();
    }

    // constructor
    private RestClient() {
    }

    /**
     * This method return the instance of RestClient Class
     *
     * @return (RestClient) instance : it return RestClient instance
     */
    public static RestClient getInstance() {
        if (null == instance) {
            instance = new RestClient();
        }
        return instance;
    }

    /**
     * This method send the API request to server using volley request library.
     * <br>when response comes success, We get volley response in
     * {@link com.android.volley.Response.Listener#onResponse(Object)},</br> And
     * <br> when error comes, We get error information in
     * {@link com.android.volley.Response.ErrorListener#onErrorResponse(VolleyError)} </br>
     *
     * @param mContext         (Context)                : Activity context
     * @param requestType      (int)                 :  Request type e.g. GET, POST
     * @param requestUrl       (String)               : Api url e.g. ApiList.API_CHECK_VERSION
     * @param params           (JOSNObject)               : post parameters for post request
     * @param responseHandler  (requestListener) : instance of RequestListener
     * @param requestCode      (RequestCode)         : Request code type e.g. RequestCode.API_CHECK_VERSION
     * @param isDialogRequired (boolean)        : true or false for required Loading dialog
     *                         e.g. true - it show dialog during api call
     *                         false - it does not show dialog during api call
     * @see <a href="http://stackoverflow.com/questions/24700582/handle-volley-error">Handle Volley error</a>
     */
    public void post(final Context mContext, int requestType, String requestUrl,
                     final JSONObject params, final RequestListener responseHandler,
                     final RequestCode requestCode, final Boolean isDialogRequired) {

        if (Util.checkInternetConnection()) {

            if (params == null) {
                Debug.trace(TAG + " " + getAbsoluteUrl(requestUrl) + " No requestParams");
            } else {
                Debug.trace(TAG + " " + getAbsoluteUrl(requestUrl) + " " + params.toString());
            }
            if (isDialogRequired) {
                CustomDialog.getInstance().showProgressBar(mContext, Util.getAppKeyValue(mContext, R.string.lblPleaseWait));
            }
            if (!URLUtil.isNetworkUrl(requestUrl)) {
                requestUrl = getAbsoluteUrl(requestUrl);
            }
            final JsonObjectRequest postRequest = new JsonObjectRequest(requestType, requestUrl, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {


                            Debug.trace("TAG", "Response:" + response.toString());

                            verifyResponse(response.toString(), requestCode, new IListener() {
                                @Override
                                public void onOtherError(String errorMessage, int status) {
                                    if (mContext != null) {
                                        if (isDialogRequired) {
                                            if (CustomDialog.getInstance().isDialogShowing()) {
                                                CustomDialog.getInstance().hide();
                                            }
                                        }
                                    }
                                    if (responseHandler != null) {
                                        if (errorMessage.equalsIgnoreCase("")) {
                                            if (mContext != null) {
                                                errorMessage = Util.getAppKeyValue(mContext, R.string.errorMsgShortMaintananceBreak);
                                            }
                                        }
                                        responseHandler.onRequestError(errorMessage, status, requestCode);
                                    }
                                }

                                @Override
                                public void onHideProgressDialog() {
                                    if (mContext != null) {
                                        if (isDialogRequired) {
                                            if (CustomDialog.getInstance().isDialogShowing()) {
                                                CustomDialog.getInstance().hide();
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onProcessCompleted(Object object) {
                                    if (responseHandler != null) {
                                        responseHandler.onRequestComplete(requestCode, object);
                                    }
                                }
                            });

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {

                            volleyError.printStackTrace();

                            if (mContext != null) {
                                if (isDialogRequired) {
                                    if (CustomDialog.getInstance().isDialogShowing()) {
                                        CustomDialog.getInstance().hide();
                                    }
                                }
                            }

                            String errorMessage = null;

                            if (mContext != null) {
                                // it handle error message if any error occur from volley side

                                if (volleyError instanceof TimeoutError) {
                                    errorMessage = Util.getAppKeyValue(mContext, R.string.errorMsgInternetSlow);

                                } else if (volleyError instanceof NoConnectionError) {
                                    errorMessage = Util.getAppKeyValue(mContext, R.string.errorMsgInternetConnUnavailable);

                                } else {
                                    errorMessage = Util.getAppKeyValue(mContext, R.string.errorMsgRequestError);
                                }
                            }
                            responseHandler.onRequestError(errorMessage, ResponseStatus.STATUS_FAIL, requestCode);
                        }
                    }

            );

            /*setting the timing for request timeout*/
            MyApplication.getInstance().setRequestTimeout(postRequest);
            /*adding request in queue*/
            MyApplication.getInstance().addToRequestQueue(requestCode.name(), postRequest);

        } else {
            if (mContext != null)
                CustomDialog.getInstance().showAlert(mContext, Util.getAppKeyValue(mContext, R.string.errorMsgInternetConnUnavailable), false, mContext.getString(R.string.lblOk));
        }
    }

    /**
     * This method gives the absolute url path
     *
     * @param relativeUrl (String) : API url from API list e.g. CommonWebService.asmx/registerCustomer
     * @return (String) URL : complete url with server live url
     * e.g. http://192.168.192.90:5555/CommonWebService.asmx/registerCustomer
     */
    private String getAbsoluteUrl(String relativeUrl) {
        return ServerConfig.SERVER_URL + relativeUrl;
    }

    /**
     * This method verify the response pattern.
     * <p><strong> Note : </strong> api build in .NET Technology then all responses will come in single json object
     * that start with <strong>"d".</strong></p>
     *
     * @param response    (String)        : Response from server
     * @param requestCode (RequestCode enum) : Identify the response identity e.g. RequestCode.API_CHECK_VERSION
     * @param listener    (IListener instance) : Handle progress dialog visibility, response completion and
     *                    other errors e.g slow internet connection, request timeout
     * @see RestClient#processError(ResponseStatus, IListener)
     * @see RestClient#processSuccess(String, ResponseStatus, RequestCode, IListener)
     */
    private static void verifyResponse(final String response, final RequestCode requestCode, final IListener listener) {

        if (listener != null) {
            ResponseStatus responseStatus;
            try {
                JSONObject jResult = new JSONObject(response);
                if (jResult.has("d")) {
                    String strResult = jResult.getString("d");
                    jResult = new JSONObject(strResult);

                }

                responseStatus = gson.fromJson(new JSONObject(jResult.toString()).toString(), ResponseStatus.class);


                if (responseStatus.isFail()) {
                    processError(responseStatus, listener);
                } else {

                    if (responseStatus.isSuccess()) {

                        listener.onHideProgressDialog();

                        processSuccess(jResult.toString(), responseStatus, requestCode, listener);
                    }
                }
            } catch (JsonSyntaxException | JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method process the succeed response and parse response using
     * {@link ResponseManager#parse(RequestCode, String, Gson)} method.
     *
     * @param response       (String)              : succeed response
     * @param responseStatus (ResponseStatus instance) : response status e.g success
     * @param listener       (IListener instance) : handle progress dialog visibility, response completion and
     *                       other errors e.g slow internet connection, request timeout
     * @see ResponseManager#parse(RequestCode, String, Gson)
     */
    private static <T> void processSuccess(final String response, final ResponseStatus responseStatus, final RequestCode requestCode, final IListener listener) {

        if (listener != null) {
            Object object;
            if ((requestCode.getLocalClass() != null) && requestCode.getLocalClass().getSimpleName().equalsIgnoreCase("ResponseStatus")) {
                object = responseStatus;
            } else {

                object = ResponseManager.parse(requestCode, response, gson);

            }
            if (object instanceof ResponseStatus) {
                listener.onOtherError(responseStatus.getMessage(), responseStatus.getStatus());
            } else {
                listener.onProcessCompleted(object);
            }
        }
    }

    /**
     * This method handle errors e.g slow internet connection, request timeout
     *
     * @param responseStatus (ResponseStatus instance) : it process the response error
     * @param listener       (IListener instance) : handle progress dialog visibility, response completion and
     *                       other errors e.g slow internet connection, request timeout
     */
    private static void processError(final ResponseStatus responseStatus, final IListener listener) {
        if (listener != null) {
            listener.onOtherError(responseStatus.getMessage(), responseStatus.getStatus());
        }
    }

    /**
     * interface for handling the
     * other errors, hiding process dialog
     * and process further the response
     **/
    private interface IListener {
        /**
         * <pre>{@code
         * status = 1  // response status success
         * status = 2  // response status fail
         * status = 3  // response status error
         * status = 4  // response status invalid
         * }
         * </pre>
         *
         * @param errorMessage (String) : appropriate message relevant to errors
         * @param status       (String)       : status of response
         */
        void onOtherError(String errorMessage, int status);

        /**
         * This method hide progress dialog
         */
        void onHideProgressDialog();

        /**
         * @param object (Object) : succeed response in object
         */
        void onProcessCompleted(Object object);
    }
}