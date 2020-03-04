package com.example.mvc.codebase.models;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.android.volley.Request;
import com.example.mvc.codebase.activity.RegistrationActivity;
import com.example.mvc.codebase.api.ApiList;
import com.example.mvc.codebase.api.RequestCode;
import com.example.mvc.codebase.api.RequestListener;
import com.example.mvc.codebase.api.RestClient;
import com.example.mvc.codebase.enumerations.RegisterBy;
import com.example.mvc.codebase.interfaces.DataObserver;
import com.example.mvc.codebase.utils.Constants;
import com.example.mvc.codebase.utils.Debug;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;

/**
 * This class store the required user information
 * TODO stub is generated but developer or programmer need to add code as required.
 */
public class RegisterCustomerModel implements Serializable {

    private CustomerDetails customerDetails;
    private boolean isRegisterRequired;

    //  constructor
    public RegisterCustomerModel() {

    }

    public CustomerDetails getCustomerDetails() {
        return customerDetails;
    }

    public void setCustomerDetails(CustomerDetails customerDetails) {
        this.customerDetails = customerDetails;
    }

    private boolean getIsRegisterRequired() {
        return isRegisterRequired;
    }

    private void setIsRegisterRequired(boolean registerRequired) {
        isRegisterRequired = registerRequired;
    }

    /**
     * This method register new user by API call
     * with posting necessary post parameters
     *
     * @param activityContext (Activity)         : context
     * @param dataObserver    (DataObserver) : instance of DataObserver
     * @see RestClient#post(Context, int, String, JSONObject, RequestListener, RequestCode, Boolean)
     * @see #registrationRequired(CustomerDetails)
     * @see CustomerDetails#saveLoginUserCredentials(CustomerDetails)
     * @see DataObserver
     */
    public void callRegisterCustomerAPI(final Activity activityContext, final DataObserver dataObserver) {
        JSONObject params = new JSONObject();

        try {

            params.put(ApiList.KEY_CUSTOMER_ID, getCustomerDetails().getCustomerId());
            params.put(ApiList.KEY_FIRST_NAME, getCustomerDetails().getFirstName());
            params.put(ApiList.KEY_LAST_NAME, getCustomerDetails().getLastName());
            params.put(ApiList.KEY_EMAILID, getCustomerDetails().getEmail());
            params.put(ApiList.KEY_PASSWORD, getCustomerDetails().getPassword());
            params.put(ApiList.KEY_PHONE_NO, getCustomerDetails().getPhone());
            params.put(ApiList.KEY_DEVICE_TOKEN, getCustomerDetails().getDeviceToken());
            params.put(ApiList.KEY_DEVICE_TYPE, getCustomerDetails().getDeviceType());
            params.put(ApiList.KEY_REGISTER_BY, getCustomerDetails().getRegisterBy());
            params.put(ApiList.KEY_SOCIAL_MEDIA_USER_ID, getCustomerDetails().getSocialMediaUserId());
            params.put(ApiList.KEY_DEVICE_DETAILS, getCustomerDetails().getDeviceDetails());
            params.put(ApiList.KEY_BIRTHDAY, getCustomerDetails().getBirthDate());
            params.put(ApiList.KEY_COUNTRY_ID, getCustomerDetails().getCountryId());

            Debug.trace(Constants.DEBUG_KEY_REGISTER_CUSTOMER_POST_PARAM, params.toString());

            RestClient.getInstance().post(activityContext, Request.Method.POST, ApiList.API_REGISTER_CUSTOMER, params,
                    new RequestListener() {
                        @Override
                        public void onRequestComplete(RequestCode requestCode, Object object) {

                            RegisterCustomerModel registerUser = (RegisterCustomerModel) object;
                            /* From social integration if necessary data not get than it will
                            redirect to registration activity for registration */
                            if ((getCustomerDetails().getRegisterBy() != RegisterBy.APP.getType() && registerUser.getIsRegisterRequired())
                                    && registrationRequired(registerUser.getCustomerDetails())) {

                                Intent i2RegistrationActivity = new Intent(activityContext, RegistrationActivity.class);
                                i2RegistrationActivity.putExtra(Constants.KEY_REGISTER_REQUIRED, registerUser.getCustomerDetails());

                                activityContext.startActivity(i2RegistrationActivity);
                                activityContext.finish();
                            } else {
                                CustomerDetails.saveLoginUserCredentials(registerUser.getCustomerDetails());
                                dataObserver.OnSuccess(requestCode);
                            }
                        }

                        @Override
                        public void onRequestError(String error, int status, RequestCode requestCode) {

                            dataObserver.OnFailure(requestCode, error);
                        }
                    }, RequestCode.REGISTER_CUSTOMER, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method checks required information or mandatory field information
     * that comes in api response
     * like e.g. firstName, email
     *
     * @param userProfile (CustomerDetails) : CustomerDetails object
     * @return (boolean) : it return true if required field is empty or null
     */
    private static boolean registrationRequired(CustomerDetails userProfile) {
        return TextUtils.isEmpty(userProfile.getFirstName()) || TextUtils.isEmpty(userProfile.getEmail());

    }
}
