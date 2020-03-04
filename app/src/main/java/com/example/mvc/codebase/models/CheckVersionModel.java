package com.example.mvc.codebase.models;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Request;
import com.example.mvc.codebase.MyApplication;
import com.example.mvc.codebase.api.ApiList;
import com.example.mvc.codebase.api.RequestCode;
import com.example.mvc.codebase.api.RequestListener;
import com.example.mvc.codebase.api.RestClient;
import com.example.mvc.codebase.helper.PrefHelper;
import com.example.mvc.codebase.interfaces.DataObserver;
import com.example.mvc.codebase.utils.Constants;
import com.example.mvc.codebase.utils.Debug;
import com.example.mvc.codebase.utils.Util;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class store the required version information
 */

public class CheckVersionModel implements Serializable {

    //variable declaration
    private static final String TAG = CheckVersionModel.class.getName();


    private static CheckVersionModel checkVersionModel;

    private int deviceType;

    /**
     * <pre>{@code
     * isUpdateType =  0 // App not approved
     * isUpdateType =  1 // App approved OR No update available
     * isUpdateType =  2 // Optional update available
     * isUpdateType =  3 // Mandatory update available
     *     }
     * </pre>
     */
    private int isUpdateType;
    private int appVersion;

    private String deviceToken;
    private String deviceDetails;
    private String updateMessage;
    private String url;
    private String maintenanceMsg;
    private String messageUpdateDate;

    private ArrayList<MessageListModel> messageList = new ArrayList<>();
    private ArrayList<CountryModel> countryList = new ArrayList<>();

    //constructor
    public CheckVersionModel() {

        this.appVersion = Util.getAppVersionCode();
        this.deviceType = Constants.DEFAULT_VALUE_DEVICE_TYPE_ANDROID;
        this.deviceToken = PrefHelper.getInstance().getString(PrefHelper.KEY_DEVICE_TOKEN, Constants.DEFAULT_BLANK_STRING);
        this.deviceDetails = Util.getDeviceDetails();
    }


    public static CheckVersionModel getCheckVersionModel() {
        return checkVersionModel;
    }

    private static void setCheckVersionModel(CheckVersionModel checkVersionModelObj) {
        checkVersionModel = checkVersionModelObj;
    }


    /**
     * @return updateMessage (String) : it return string
     */
    public String getUpdateMessage() {
        return updateMessage;
    }

    /**
     * @param updateMessage The updateMessage
     */
    public void setUpdateMessage(String updateMessage) {
        this.updateMessage = updateMessage;
    }

    /**
     * @return url(String)  : it return server url to download apk file
     * url value : e.g. http://code.on-linedemo.com/admin
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return isUpdateType (int) : it return value in integer e.g. 1,0
     * <pre>{@code
     * isUpdateType =  0 // App not approved
     * isUpdateType =  1 // App approved OR No update available
     * isUpdateType =  2 // Optional update available
     * isUpdateType =  3 // Mandatory update available
     *     }
     * </pre>
     */
    public int getIsUpdateType() {
        return isUpdateType;
    }

    /**
     * @param isUpdateType The isUpdateType
     */
    public void setIsUpdateType(int isUpdateType) {
        this.isUpdateType = isUpdateType;
    }

    /**
     * @return maintenanceMsg (String) : The maintenanceMsg
     */
    public String getMaintenanceMsg() {
        return maintenanceMsg;
    }

    /**
     * @param maintenanceMsg The maintenanceMsg
     */
    public void setMaintenanceMsg(String maintenanceMsg) {
        this.maintenanceMsg = maintenanceMsg;
    }

    public ArrayList<MessageListModel> getMessageList() {
        return messageList;
    }

    /**
     * @param messageList (ArrayList<MessageListModel>) : The messageList
     */
    private void setMessageList(ArrayList<MessageListModel> messageList) {

        if (messageList != null && messageList.size() > 0) {
            for (int i = 0; i < messageList.size(); i++) {

                MyApplication.msgHashMap.put(messageList.get(i).getKeyValue(), messageList.get(i).getMsgValue());
            }

            PrefHelper.getInstance().setString(PrefHelper.KEY_SAVED_MESSAGE_LIST, MyApplication.msgHashMap);
        }

        String result = PrefHelper.getInstance().getString(PrefHelper.KEY_SAVED_MESSAGE_LIST, Constants.DEFAULT_BLANK_STRING);
        MyApplication.msgHashMap = RestClient.gson.fromJson(result, new TypeToken<HashMap<String, String>>() {
        }.getType());


    }

    /**
     * @return messageUpdateDate (String) : The messageUpdateDate
     */
    private String getMessageUpdateDate() {
        return messageUpdateDate;
    }

    /**
     * @param messageUpdateDate The messageUpdateDate
     */
    public void setMessageUpdateDate(String messageUpdateDate) {
        this.messageUpdateDate = messageUpdateDate;
    }

    /**
     * @return countryList (ArrayList) : it return list of country
     */
    public ArrayList<CountryModel> getCountryList() {
        return countryList;
    }

    /**
     * @param countryList (ArrayList<CountryModel>) : The countryList
     */
    public void setCountryList(ArrayList<CountryModel> countryList) {
        this.countryList = countryList;
    }

    /**
     * This method set the checkVersionModel instance
     *
     * @param checkVersionModel (CheckVersionModel): instance of CheckVersionModel class
     * @see #setCheckVersionModel(CheckVersionModel)
     * @see #setMessageList(ArrayList)
     */
    private void setCheckVersionDetails(CheckVersionModel checkVersionModel) {

        setCheckVersionModel(checkVersionModel);
        setMessageList(checkVersionModel.messageList);

        PrefHelper.getInstance().setString(PrefHelper.KEY_MESSAGE_UPDATE_DATE, checkVersionModel.getMessageUpdateDate());

    }

    /**
     * This method call checkAppVersion API by API call
     * with posting necessary post parameters
     *
     * @param activity     (Activity)         : context
     * @param dataObserver (DataObserver) : instance of DataObserver
     * @see RestClient#post(Context, int, String, JSONObject, RequestListener, RequestCode, Boolean)
     * @see PrefHelper#getString(String, String)
     * @see DataObserver
     */
    public void callCheckVersionAPI(final Activity activity, final DataObserver dataObserver) {

        JSONObject param = new JSONObject();

        try {
            param.put(ApiList.KEY_APP_VERSION, appVersion);
            param.put(ApiList.KEY_DEVICE_TYPE, deviceType);
            param.put(ApiList.KEY_DEVICE_TOKEN, deviceToken);

            if (PrefHelper.getInstance().getString(PrefHelper.KEY_MESSAGE_UPDATE_DATE, Constants.DEFAULT_BLANK_STRING).equals(Constants.DEFAULT_BLANK_STRING)) {
                param.put(ApiList.KEY_MSG_UPDATE_DATE, Constants.DEFAULT_BLANK_STRING);
            } else {
                param.put(ApiList.KEY_MSG_UPDATE_DATE, PrefHelper.getInstance().getString(PrefHelper.KEY_MESSAGE_UPDATE_DATE, Constants.DEFAULT_BLANK_STRING));
            }

            if (CustomerDetails.isLoggedIn()) {
                param.put(ApiList.KEY_CUSTOMER_ID, CustomerDetails.getCurrentLoginUser().getCustomerId());
            } else {
                param.put(ApiList.KEY_CUSTOMER_ID, Constants.DEFAULT_CUSTOMER_ID);
            }
            param.put(ApiList.KEY_DEVICE_DETAILS, deviceDetails);

            Debug.trace(Constants.DEBUG_KEY_CHECKVERSION_POST_PARAM, param.toString());

            RestClient.getInstance().post(activity, Request.Method.POST, ApiList.API_CHECK_VERSION, param,
                    new RequestListener() {
                        @Override
                        public void onRequestComplete(RequestCode requestCode, Object object) {

                            setCheckVersionDetails((CheckVersionModel) object);
                            dataObserver.OnSuccess(requestCode);

                        }

                        @Override
                        public void onRequestError(String error, int status, RequestCode requestCode) {

                            dataObserver.OnFailure(requestCode, error);
                        }
                    }, RequestCode.CHECK_VERSION, false);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
