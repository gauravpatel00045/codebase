package com.example.mvc.codebase.api;


/**
 * This class contain the API
 * Keys,values and API list
 */
public class ApiList {

    /* callCheckVersionAPI api keys*/
    public static final String KEY_APP_VERSION = "appVersion";
    public static final String KEY_DEVICE_TYPE = "deviceType";
    public static final String KEY_CUSTOMER_ID = "customerId";
    public static final String KEY_DEVICE_DETAILS = "deviceDetails";

    /* loginVerification and API_REGISTER_CUSTOMER api keys*/
    public static final String KEY_USERNAME = "username";
    public static final String KEY_EMAILID = "emailId";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PHONE_NO = "phone";
    public static final String KEY_DEVICE_TOKEN = "deviceToken";
    public static final String KEY_MSG_UPDATE_DATE = "messageUpdateDate";

    public static final String KEY_FIRST_NAME = "firstName";
    public static final String KEY_LAST_NAME = "lastName";
    public static final String KEY_REGISTER_BY = "registerBy";
    public static final String KEY_SOCIAL_MEDIA_USER_ID = "socialMediaUserId";
    public static final String KEY_BIRTHDAY = "birthDate";
    public static final String KEY_COUNTRY_ID = "countryId";

    public static final String KEY_LAST_ID = "lastId";
    public static final String KEY_PAGE_SIZE = "pagesize";
    public static final String KEY_ERROR_TEXT = "errorText";

    /* APIs */
    public static final String API_ADD_DEVICE_TOKEN = "CommonWebService.asmx/addDeviceToken";
    public static final String API_CHECK_VERSION = "CommonWebService.asmx/checkAppVersion";
    public static final String API_LOGIN_CUSTOMER = "CommonWebService.asmx/loginVerification";
    public static final String API_REGISTER_CUSTOMER = "CommonWebService.asmx/registerCustomer";
    public static final String API_ADD_CRASH_REPORT = "CommonWebService.asmx/addCrashReport";
    public static final String API_GET_NOTIFICATION = "CommonWebService.asmx/getNotification";

}