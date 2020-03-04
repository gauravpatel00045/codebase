package com.example.mvc.codebase.utils;

/**
 * This class store the important default constants of the application so, we can use it in entire
 * project.
 * <p><strong>Example: </strong> How to use constant
 * <pre>{@code
 * Constants.APP_APPROVED_AND_NO_UPDATE
 * }</pre>
 * </p>
 * <strong>--------------------- Helpful Note ---------------------</strong>
 * <p></p>
 * <p>
 * 1. Defining class:
 * <a href="https://docs.oracle.com/javase/tutorial/java/javaOO/classdecl.html"> Declaring class </a>
 * </p>
 * <p>
 * 2. Defining Methods:
 * <a href="https://docs.oracle.com/javase/tutorial/java/javaOO/methods.html"> Defining Methods </a>
 * </p>
 * <p>
 * 3. Defining variable:
 * <a href="https://docs.oracle.com/javase/tutorial/java/nutsandbolts/variables.html"> Defining Variable </a>
 * </p>
 * <p>
 * 4. Defining Member Variables (Model class):
 * <a href="https://docs.oracle.com/javase/tutorial/java/javaOO/variables.html"> Defining Member Variables (Model class) </a>
 * </p>
 * <p>
 * 5. Defining an Interfaces:
 * <a href="http://docs.oracle.com/javase/tutorial/java/IandI/interfaceDef.html"> Defining an Interface </a> <strong> , </strong>
 * <a href="https://docs.oracle.com/javase/tutorial/java/concepts/interface.html"> What Is an Interface ? </a>
 * </p>
 * <p>
 * 6. Defining enumeration:
 * <a href="https://docs.oracle.com/javase/tutorial/java/javaOO/enum.html"> Enum Types </a>
 * </p>
 * <p>
 * 7. Defining Comments:
 * <a href="http://www.oracle.com/technetwork/java/javase/documentation/index-137868.html#descriptions"> Write Doc Comments</a>
 * </p>
 */
public class Constants {

    public static final int ZERO = 0;

    public static final int APP_APPROVED_AND_NO_UPDATE = 1;

    public static final int APP_NOT_APPROVED = 0;

    public static final int APP_NO_UPDATE_AVAILABLE = 0;

    public static final int APP_MANDATORY_UPDATE = 3;

    public static final int APP_OPTIONAL_UPDATE = 2;

    public static final int PHONE_NUMBER_LENGTH = 10;

    public static final int PASSWORD_LENGTH = 6;

    public static final int REQUEST_TIMEOUT = 30000;

    public static final int DEFAULT_CALENDER_YEAR = 1970;

    public static final int DEFAULT_CALENDER_MONTH = 1;

    public static final int DEFAULT_CALENDER_DATE = 1;

    public static final int DEFAULT_CUSTOMER_ID = 0;

    public static final int VALUE_SIMPLE_LOGIN_TYPE = 1;

    public static final int VALUE_FACEBOOK_LOGIN_TYPE = 2;

    public static final int VALUE_GOOGLE_LOGIN_TYPE = 3;

    public static final int VALUE_TWITTER_LOGIN_TYPE = 4;

    public static final int DEFAULT_VALUE_DEVICE_TYPE_ANDROID = 2;

    public static final int NOTIFICATION_REQUEST_CODE = 0;

    public final static int MEDIA_AUDIO = 1;

    public final static int MEDIA_VIDEO = 2;

    public final static int MEDIA_IMAGE = 3;

    public final static float VALUE_IMAGE_RATIO = 0.75f;

    public static final String DEFAULT_SOCIAL_MEDIA_USER_ID = "0";

    public static final String DEFAULT_BLANK_STRING = "";

    public static final String MESSAGE = "message";

    public static final String KEY_REGISTER_REQUIRED = "registerRequired";

    public static final String KEY_FROM_NOTIFICATION = "fromNotification";

    public static final String KEY_NOTIFICATION_MESSAGE = "message";

    public static final String KEY_NOTIFICATION_MESSAGE_DATA = "notificationMessageData";

    public static final String KEY_SHOW_POP_UP = "showPopUp";

    public static final String KEY_FACEBOOK_FIELD = "fields";

    public static final String FACEBOOK_SHARE_PHOTO_CAPTION = "share by Codebase Integration";

    public static final String FACEBOOK_SHARE_WITH_DIALOG_CONTENT_URL = "http://www.narendramodi.in/";

    public static final String FACEBOOK_SHARE_WITH_DIALOG_CONTENT_TITLE = "Hi..from codebase integration demo";

    public static final String FACEBOOK_SHARE_WITH_DIALOG_CONTENT_DESCRIPTION = "sharing post with facebook from app";

    public static final String FACEBOOK_SHARE_WITH_DIALOG_CONTENT_IMAGE_URL = "http://cdn.narendramodi.in/wp-content/uploads/2012/07/kakariya-nner2.jpg";

    public static final String KEY_FACEBOOK_ID = "id";

    public static final String KEY_FACEBOOK_FIRST_NAME = "first_name";

    public static final String KEY_FACEBOOK_LAST_NAME = "last_name";

    public static final String KEY_FACEBOOK_GENDER = "gender";

    public static final String KEY_FACEBOOK_BIRTHDAY = "birthDate";

    public static final String KEY_FACEBOOK_LOCATION = "location";

    public static final String KEY_FACEBOOK_EMAIL = "email";

    public static final String TAG = "TAG ";

    public static final String DEBUG_KEY_FIREBASE_DEVICE_TOKEN = "firebaseDeviceToken ";

    public static final String DEBUG_KEY_ADD_DEVICE_TOKEN_POST_PARAM = "addDeviceTokenPostParam ";

    public static final String DEBUG_KEY_CHECKVERSION_POST_PARAM = "checkVersionPostParam ";

    public static final String DEBUG_KEY_LOGIN_VERIFICATION_POST_PARAM = "loginVerificationPostParam ";

    public static final String DEBUG_KEY_REGISTER_CUSTOMER_POST_PARAM = "registerCustomerPostParam ";

    public static final String STANDARD_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_FORMAT = "dd/MM/yyyy";

    public static final String HELVETICA_CONDENSED_BOLD = "fonts/HELVETICA_CONDENSED_BOLD.OTF";

    public static final String MYRIADPRO_LIGHTSEMIEXT = "fonts/MyriadPro_LightSemiExt_0.otf";

    public static final String HELVETICA_NEUE_LIGHT = "fonts/Helvetica_Neue_Light_0.otf";

    public static final String HELVETICA_NEUE_BOLD = "fonts/HelveticaNeue-Bold.ttf";

    public static final String OSWALD_BOLD = "fonts/OSWALD_BOLD.TTF";

    public static final String HELVETICA_CONDENSED_MEDIUM = "fonts/Helvetica-Condensed_Medium.otf";

    public static final String DEFAULT_PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=";

    public static final String LINE_SEPARATOR = "\n";

    public static final String GALLERY_FILE_TYPE = "image/*";

    /**
     * TODO stub is generated but developer or programmer need to add code as required
     * <p>
     * Note: Examples: Matches following phone numbers:
     * (123)456-7890, 123-456-7890, 1234567890, (123)-456-7890
     * Note: http://www.zparacha.com/validate-email-ssn-phone-number-using-java-regular-expression
     * Note: http://stackoverflow.com/questions/2113908/what-regular-expression-will-match-valid-international-phone-numbers
     */
    public static final String PATTERN_PHONE_NUMBER = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$";

    /**
     * This regex include {-, _ , A-Z and a-z}
     */
    public static final String PATTERN_ALPHABET = "^[_A-Za-z-]+";

    /**
     * phone number with plus include <ul>
     * <li>plus sign e.g. (+)</li>
     * <li>digits e.g.[0-9]</li>
     */
    public static final String PATTERN_PHONE_NUMBER_WITH_PLUS = "[+]?[0-9]+";

    /**
     * alphanumeric pattern include,
     * <ul>
     * <li>uppercase letter e.g. [A-Z]</li>
     * <li>lowercase letter e.g. [a-z]</li>
     * <li>special characters e.g. [@#$%]</li>
     * <li>numbers e.g [0-9]</li>
     * <li>minimum character six</li>
     * <li>maximum character twenty</li>
     * </ul>
     */
    public static final String PATTERN_ALPHANUMERIC_PASSWORD = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";

    /* TODO need to get from server - remaining */
    public static final String PLAY_STORE_URL = "market://details?id=";

    public static final String CUSTOM_EXCEPTION_ERROR = "minLength and maxLength must be positive";

    public static final String CUSTOM_EXCEPTION_CALENDER_ERROR = "year,month and day must be positive, year digit must be less than five,month must be between [1-12], day must be between [1-31]";

}
