package com.example.mvc.codebase.models;

import android.content.Context;

import com.android.volley.Request;
import com.example.mvc.codebase.api.ApiList;
import com.example.mvc.codebase.api.RequestCode;
import com.example.mvc.codebase.api.RequestListener;
import com.example.mvc.codebase.api.RestClient;
import com.example.mvc.codebase.enumerations.RegisterBy;
import com.example.mvc.codebase.helper.PrefHelper;
import com.example.mvc.codebase.interfaces.DataObserver;
import com.example.mvc.codebase.utils.Constants;
import com.example.mvc.codebase.utils.Debug;
import com.example.mvc.codebase.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * This class store the required userDetails
 */

public class CustomerDetails implements Serializable {


    private int customerId;
    private int deviceType;
    private int countryId;
    private int registerBy;

    private long birthDate;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String address;
    private String zipCode;
    private String phone;
    private String deviceToken;
    private String deviceDetails;
    private String socialMediaUserId;

    private boolean isActive;

    //constructor
    public CustomerDetails() {
        this.customerId = Constants.DEFAULT_CUSTOMER_ID;
        this.firstName = Constants.DEFAULT_BLANK_STRING;
        this.lastName = Constants.DEFAULT_BLANK_STRING;
        this.email = Constants.DEFAULT_BLANK_STRING;
        this.phone = Constants.DEFAULT_BLANK_STRING;
        this.birthDate = Constants.ZERO;
        this.password = Constants.DEFAULT_BLANK_STRING;
        this.countryId = Constants.ZERO;
        this.registerBy = RegisterBy.APP.getType();
        this.socialMediaUserId = Constants.DEFAULT_SOCIAL_MEDIA_USER_ID;
        this.deviceToken = PrefHelper.getInstance().getString(PrefHelper.KEY_DEVICE_TOKEN, Constants.DEFAULT_BLANK_STRING);
        this.deviceType = Constants.DEFAULT_VALUE_DEVICE_TYPE_ANDROID;
        this.deviceDetails = Util.getDeviceDetails();
    }

    /**
     * @return The customerId
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * @param customerId The CustomerId
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    /**
     * @return The firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName The FirstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return The lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName The LastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email The Email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return The password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password The Password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return The address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address The Address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return The zipcode
     */
    public String getZipCode() {
        return zipCode;
    }

    /**
     * @param zipCode The zipCode
     */
    public void setZipcode(String zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * @return The phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone The Phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return The isActive
     */
    public boolean getIsActive() {
        return isActive;
    }

    /**
     * @param isActive The IsActive
     */
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * @return The deviceToken
     */
    public String getDeviceToken() {
        return deviceToken;
    }

    /**
     * @param deviceToken The deviceToken
     */
    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    /**
     * @return The deviceType
     */
    public int getDeviceType() {
        return deviceType;
    }

    /**
     * @param deviceType The deviceType
     */
    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    /**
     * @return registerBy
     */
    public int getRegisterBy() {
        return registerBy;
    }

    /**
     * @param registerBy The registerBy
     */
    public void setRegisterBy(int registerBy) {
        this.registerBy = registerBy;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    /**
     * @return birthDate
     */
    public long getBirthDate() {
        return birthDate;
    }

    /**
     * @param birthDate The birthDate
     */
    public void setBirthDate(long birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * @return socialMediaUserId
     */
    public String getSocialMediaUserId() {
        return socialMediaUserId;
    }

    /**
     * @param socialMediaUserId the socialMediaUserId
     */
    public void setSocialMediaUserId(String socialMediaUserId) {
        this.socialMediaUserId = socialMediaUserId;
    }

    public String getDeviceDetails() {
        return deviceDetails;
    }

    public void setDeviceDetails(String deviceDetails) {
        this.deviceDetails = deviceDetails;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    /**
     * This method store the Logged in User credentials
     * e.g. first name, last name, email and other required information
     *
     * @param user (CustomerDetails) : object of LoginCustomer
     */
    public static void saveLoginUserCredentials(CustomerDetails user) {

        PrefHelper.getInstance().setString(PrefHelper.KEY_CURRENT_LOGGED_IN_USER, Util.objectToString(user));
    }

    /**
     * To get the current logged in user details object from shared preferences
     *
     * @return (CustomerDetails) : it return saved details as an object
     */
    public static CustomerDetails getCurrentLoginUser() {

        return (CustomerDetails) Util.stringToObject(PrefHelper.getInstance().getString(PrefHelper.KEY_CURRENT_LOGGED_IN_USER, Constants.DEFAULT_BLANK_STRING));
    }

    /**
     * This method check the User is Logged in Or not
     * by checking the value in shared preferences
     *
     * @return boolean : return true if user logged in and false if not
     */
    public static boolean isLoggedIn() {

        return !PrefHelper.getInstance().getString(PrefHelper.KEY_CURRENT_LOGGED_IN_USER, Constants.DEFAULT_BLANK_STRING).isEmpty();
    }

    /**
     * This method delete the stored preferences of user
     * logged out the user
     */
    public static void logoutUser() {

        PrefHelper.getInstance().setString(PrefHelper.KEY_CURRENT_LOGGED_IN_USER, Constants.DEFAULT_BLANK_STRING);
    }

    /**
     * This method do callLoginVerificationAPI by posting required parameter
     * userName and password at server
     *
     * @param context      (Context) : context
     * @param dataObserver (DataObserver) : interface instance
     * @see RestClient#post(Context, int, String, JSONObject, RequestListener, RequestCode, Boolean)
     * @see #saveLoginUserCredentials(CustomerDetails)
     * @see DataObserver
     */
    public void callLoginVerificationAPI(Context context, final DataObserver dataObserver) {

        try {

            JSONObject params = new JSONObject();
            params.put(ApiList.KEY_USERNAME, email);
            params.put(ApiList.KEY_PASSWORD, password);
            params.put(ApiList.KEY_DEVICE_TYPE, deviceType);
            params.put(ApiList.KEY_DEVICE_TOKEN, deviceToken);
            params.put(ApiList.KEY_DEVICE_DETAILS, deviceDetails);

            Debug.trace(Constants.DEBUG_KEY_LOGIN_VERIFICATION_POST_PARAM, params.toString());

            RestClient.getInstance().post(context, Request.Method.POST, ApiList.API_LOGIN_CUSTOMER,
                    params, new RequestListener() {
                        @Override
                        public void onRequestComplete(RequestCode requestCode, Object object) {
                            CustomerDetails customerDetails = (CustomerDetails) object;
                            customerDetails.setRegisterBy(RegisterBy.APP.getType());
                            saveLoginUserCredentials(customerDetails);
                            dataObserver.OnSuccess(requestCode);
                        }

                        @Override
                        public void onRequestError(String error, int status, RequestCode requestCode) {

                            dataObserver.OnFailure(requestCode, error);
                        }
                    }, RequestCode.CUSTOMER_LOGIN, true);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
