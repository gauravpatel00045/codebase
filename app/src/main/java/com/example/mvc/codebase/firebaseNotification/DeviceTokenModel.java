package com.example.mvc.codebase.firebaseNotification;

import android.content.Context;

import com.android.volley.Request;
import com.example.mvc.codebase.api.ApiList;
import com.example.mvc.codebase.api.RequestCode;
import com.example.mvc.codebase.api.RequestListener;
import com.example.mvc.codebase.api.ResponseStatus;
import com.example.mvc.codebase.api.RestClient;
import com.example.mvc.codebase.helper.PrefHelper;
import com.example.mvc.codebase.models.BaseClassModel;
import com.example.mvc.codebase.models.CustomerDetails;
import com.example.mvc.codebase.utils.Constants;
import com.example.mvc.codebase.utils.Debug;
import com.example.mvc.codebase.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class store the values of the device token model
 * and do registration device token to server
 */
public class DeviceTokenModel extends BaseClassModel {


    //variable declaration
    private static final String TAG = DeviceTokenModel.class.getName();

    private int customerId;
    private int deviceType;
    private String deviceToken;
    private String deviceDetails;

    private String message;
    private int status;

    //constructor
    public DeviceTokenModel() {
        if (CustomerDetails.isLoggedIn()) {
            this.customerId = CustomerDetails.getCurrentLoginUser().getCustomerId();
        } else {
            this.customerId = Constants.DEFAULT_CUSTOMER_ID;
        }
        this.deviceType = Constants.DEFAULT_VALUE_DEVICE_TYPE_ANDROID;
        this.deviceToken = PrefHelper.getInstance().getString(PrefHelper.KEY_DEVICE_TOKEN, Constants.DEFAULT_BLANK_STRING);
        this.deviceDetails = Util.getDeviceDetails();
    }


    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getDeviceDetails() {
        return deviceDetails;
    }

    public void setDeviceDetails(String deviceDetails) {
        this.deviceDetails = deviceDetails;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * This method register the device token to the local server
     * The device token will get from the firebase sever
     *
     * @see RestClient#post(Context, int, String, JSONObject, RequestListener, RequestCode, Boolean)
     */
    public void callAddDeviceTokenAPI() {

        try {
            JSONObject params = new JSONObject();

            params.put(ApiList.KEY_CUSTOMER_ID, customerId);
            params.put(ApiList.KEY_DEVICE_TYPE, deviceType);
            params.put(ApiList.KEY_DEVICE_TOKEN, deviceToken);
            params.put(ApiList.KEY_DEVICE_DETAILS, deviceDetails);

            Debug.trace(Constants.DEBUG_KEY_ADD_DEVICE_TOKEN_POST_PARAM, params.toString());

            RestClient.getInstance().post(null, Request.Method.POST, ApiList.API_ADD_DEVICE_TOKEN, params
                    , new RequestListener() {
                        @Override
                        public void onRequestComplete(RequestCode requestCode, Object object) {

                            DeviceTokenModel tokenModel = (DeviceTokenModel) object;

                            if (tokenModel.getStatus() == ResponseStatus.STATUS_SUCCESS) {
                                Debug.trace(TAG, tokenModel.getMessage());
                                Debug.trace(Constants.DEBUG_KEY_FIREBASE_DEVICE_TOKEN, getDeviceToken());
                            } else if (tokenModel.getStatus() == ResponseStatus.STATUS_FAIL) {
                                Debug.trace(TAG, tokenModel.getMessage());
                            }

                        }

                        @Override
                        public void onRequestError(String error, int status, RequestCode requestCode) {

                            if (status == ResponseStatus.STATUS_ERROR) {
                                callAddDeviceTokenAPI();
                            } else if (status == ResponseStatus.STATUS_SUCCESS) {
                                Debug.trace(TAG, error);
                                Debug.trace(Constants.DEBUG_KEY_FIREBASE_DEVICE_TOKEN, getDeviceToken());
                            }
                        }
                    }, RequestCode.ADD_DEVICE_TOKEN, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
