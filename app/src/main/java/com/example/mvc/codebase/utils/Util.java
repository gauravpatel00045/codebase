package com.example.mvc.codebase.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.mvc.codebase.MyApplication;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * This class has required methods that used globally in our project
 * <p><strong>Example: </strong></p> internetConnection, encryption, decryption, serialize and deserialize etc...
 */

public class Util {

    /**
     * This method gives the internet connection status
     * whether it is ON or OFF
     *
     * @return (boolean) : it return true or false if internet connection available or not, respectively
     * @see NetworkInfo#isConnectedOrConnecting()
     */
    public static boolean checkInternetConnection() {

        ConnectivityManager connectivity = (ConnectivityManager) MyApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivity != null) {
            activeNetworkInfo = connectivity.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    /**
     * This method check google play service available or not
     *
     * @param context (Context) : application context
     * @return (boolean) : it return true or false, if google play service available or not, respectively
     */
    public static boolean checkGooglePlayServiceAvailable(Context context) {
        int REQUEST_CODE_RECOVER_PLAY_SERVICES = 101;

        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(context);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog((Activity) context, result,
                        REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
            }

            return false;
        }

        return true;
    }

    /**
     * This method identify touch event. If user touches outside of keyboard
     * on screen instead of input control then call hide Keyboard method to hide keyboard.
     *
     * @param view (View) :parent view
     */
    @SuppressLint("ClickableViewAccessibility")
    public static void setupOutSideTouchHideKeyboard(final View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard(view);
                    return false;
                }

            });
        }

        // If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);
                setupOutSideTouchHideKeyboard(innerView);
            }
        }
    }

    /**
     * This method hide keyboard
     *
     * @param view (View) : it contains view, on which touch event has been performed.
     */
    private static void hideKeyboard(View view) {

        InputMethodManager mgr = (InputMethodManager) MyApplication.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    /**
     * This method return the Application version
     *
     * @return (String) version : it return app version
     * return version - e.g. 1, 2, 3
     */
    public static String getAppVersion() {

        String version = "";
        try {
            PackageInfo pInfo = MyApplication.getInstance().getPackageManager().getPackageInfo(MyApplication.getInstance().getPackageName(), 0);
            version = pInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * This method give the App versionCode
     *
     * @return (int) version :  it return app version code e.g. 1, 2, 3
     * return version - e.g. 1, 2, 3
     */
    public static int getAppVersionCode() {
        int version = 0;
        try {
            PackageInfo pInfo = MyApplication.getInstance().getPackageManager().getPackageInfo(MyApplication.getInstance().getPackageName(), 0);
            version = pInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * This method convert an object into a string
     *
     * @param obj (Serializable) : object to be converted in string
     * @return (String) : it return string from object
     */
    public static String objectToString(Serializable obj) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(
                    new Base64OutputStream(baos, Base64.NO_PADDING
                            | Base64.NO_WRAP));
            oos.writeObject(obj);
            oos.close();
            return baos.toString("UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method convert string into an object
     *
     * @param str (String) : string to be converted into object
     * @return (Object) : it return object from string
     */
    public static Object stringToObject(String str) {
        try {
            return new ObjectInputStream(new Base64InputStream(
                    new ByteArrayInputStream(str.getBytes()), Base64.NO_PADDING
                    | Base64.NO_WRAP)).readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method serialize object and convert into string
     * refer below link for Serialization
     *
     * @param obj : class object
     * @return (String) : it return string
     * @see <a href="http://docs.oracle.com/javase/7/docs/technotes/guides/serialization/index.html"/>Serialization</a>
     * @see <a href="http://www.studytonight.com/java/serialization-and-deserialization.php"/>Serialization and Desirialization</a>
     */
    public static String serialize(Serializable obj) {

        if (obj != null) {
            ByteArrayOutputStream serialObj = null;
            try {
                serialObj = new ByteArrayOutputStream();
                ObjectOutputStream objStream = new ObjectOutputStream(serialObj);
                objStream.writeObject(obj);
                objStream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (serialObj != null) {
                return encodeBytes(serialObj.toByteArray());
            } else {
                return "";
            }

        } else {
            return "";
        }

    }

    /**
     * This method deserialize  and convert  string into object
     * refer below link for deserialization
     *
     * @param str (String) : string
     * @return (Object) : return object
     * @see <a href="http://www.studytonight.com/java/serialization-and-deserialization.php"/>Serialization and Desirialization</a>
     */
    public static Object deSerialize(String str) throws IOException, ClassNotFoundException {

        if (!TextUtils.isEmpty(str)) {
            ObjectInputStream objStream = null;
            try {
                ByteArrayInputStream serialObj = new ByteArrayInputStream(decodeBytes(str));
                objStream = new ObjectInputStream(serialObj);

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (objStream != null) {
                return objStream.readObject();
            } else {
                return Constants.DEFAULT_BLANK_STRING;
            }
        } else {
            return Constants.DEFAULT_BLANK_STRING;
        }


    }

    /**
     * This method encode data on your side
     *
     * @param bytes (byte[]) : byte value
     * @return strBuf (string) : it return encoded string
     */
    public static String encodeBytes(byte[] bytes) {
        StringBuilder strBuilder = new StringBuilder();

        for (byte aByte : bytes) {
            strBuilder.append((char) (((aByte >> 4) & 0xF) + ((int) 'a')));
            strBuilder.append((char) (((aByte) & 0xF) + ((int) 'a')));
        }

        return strBuilder.toString();
    }

    /**
     * This method decode data on other side, by processing encoded data
     *
     * @param str (String) : string value
     * @return bytes (byte[]) : it return decoded bytes
     */
    public static byte[] decodeBytes(String str) {
        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length(); i += 2) {
            char c = str.charAt(i);
            bytes[i / 2] = (byte) ((c - 'a') << 4);
            c = str.charAt(i + 1);
            bytes[i / 2] += (c - 'a');
        }
        return bytes;
    }

    /**
     * This method use to get value from api that comes in checkVersion API to manage labels and required
     * messages. Here all xml component text value like header, labels and other required strings
     * are set dynamically. If client needs to change any text or label name than he/she will change
     * message value from api side and our app will set modified value automatically with below logic
     * onwards if any value will not get from api response than it fetch the default value from
     * <strong> strings.xml </strong> file using resource id with
     * {@link android.content.res.Resources#getString(int)} method.
     * <p>
     * <P>
     * Example e.g.{@code <string name="lblLogin">Log In</string>}
     * <br>where key or name = lblLogin and value  = Log In </br>
     * </P>
     * <p>
     * <br> 1. Get key name from resource Id </br>
     * <br> It will get the key name from resource id like lblLogin where lblLogin is string name that we defined
     * in strings.xml file. We get the name from resource id like lblLogin resource id is "2131230887".</br>
     * </p>
     * <p>
     * <br> 2. Get key's value that comes in checkVersion API
     * <br> With key name it check in {@link MyApplication#msgHashMap} where all the value stored
     * in application level that comes in checkVersion api with name value pair.</br>
     * </p>
     * <p>
     * <br>2.1 <strong>Response Success: </strong> if value got from app level {@link MyApplication#msgHashMap}
     * than it set value</br>
     * <br>2.2 <strong>Response Fail: </strong> if value does not get from app level {@link MyApplication#msgHashMap}
     * than it get from strings.xml file where string name value pair is already defined.
     * {@code strValue = context.getResources().getString(resId)}</br>
     * </p>
     *
     * @param context (Context) : context
     * @param resId   (int)       :   string name that defined in string.xml e.g. R.string.lblLogin
     * @return strValue (String) : return key value that passed in resId as a key e.g. Log In
     */
    public static String getAppKeyValue(Context context, int resId) {

        String strKey = context.getResources().getResourceEntryName(resId);

		/*If key is null then return blank*/
        if (TextUtils.isEmpty(strKey))
            return "";

        String strValue = "";
        try {
            /* Get value from hashmap */
            if (MyApplication.getInstance() != null && MyApplication.msgHashMap != null) {
                strValue = MyApplication.msgHashMap.get(strKey);
            }
            /*Note: If any valMessageId = 152ue is not found then get from local string file*/
            if (TextUtils.isEmpty(strValue)) {
                //Note: To get message value from strings.xml file
                strValue = context.getResources().getString(resId);
            } else {
                //Note: In API \n is converted to \\n so replace \\n with \n
                strValue = strValue.replace("\\n", "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strValue;
    }

    /**
     * This method return String resource of passed string key
     *
     * @param context (Context) : context
     * @param strKey  (String)   : string name e.g. lblAddress
     * @return (String) : it return the string value from the resource id
     */
    private static String getStringResourceByName(Context context, String strKey) {
        String packageName = context.getPackageName();
        int resId = context.getResources().getIdentifier(strKey, "string", packageName);
        return context.getString(resId);
    }


    /**
     * Return Date in specified format. This method convert date
     * from millisecond to date format e.g. 1478180961448 to 3/11/2016
     *
     * @param milliSeconds (long) : Date in milliseconds e.g. 1478180961448
     * @param dateFormat   (String) :   Date format e.g. dd/MM/yyyy
     * @return (String) : representing Date in specified format e.g. 3/11/2016
     * @see SimpleDateFormat#format(Object)
     */
    public static String dateFormat(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying Date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.getDefault());

        return formatter.format(milliSeconds);
    }

    /**
     * This method gives date in Milliseconds
     *
     * @param dates  (String)  : date to be converted in Millisecond  e.g. 3/11/2016
     * @param format (String) : date format which to be formed e.g. dd/MM/yyyy
     * @return milliseconds (long) : it return milliseconds e.g. 1478180961448
     * @see Date#getTime()
     */
    @SuppressLint("SimpleDateFormat")
    public static long getDateInMillis(String dates, String format) {
        long milliseconds = 0;
        try {
            Date date = new SimpleDateFormat(format).parse(dates);
            milliseconds = date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return milliseconds;
    }

    /**
     * @param dateFormat (String) : date format which to be formed e.g. dd/MM/yyyy
     * @return formattedDate (String) : it return current date as per date format e.g. 3/11/2016
     */
    public static String getCurrentDate(String dateFormat) {
        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(c.getTime());
    }

    /**
     * This method gives device information like device name, device brand name, android version
     * and all required information
     *
     * @return deviceDetails (String) : return device details
     * e.g. deviceDetails -"\n************ DEVICE INFORMATION ***********\n
     * Brand: Android\n
     * Device: generic_x86_64\n
     * Model: Android SDK built for x86_64\n
     * Id: LMY48X\nProduct: sdk_google_phone_x86_64\n
     * \n************ FIRMWARE ************\n
     * SDK: 22\nRelease: 5.1.1\n
     * Incremental: 3287191\n"
     */
    public static String getDeviceDetails() {

        String deviceDetails = "\n************ DEVICE INFORMATION ***********\n" +
                "Brand: " +
                Build.BRAND +
                Constants.LINE_SEPARATOR +
                "Device: " +
                Build.DEVICE +
                Constants.LINE_SEPARATOR +
                "Model: " +
                Build.MODEL +
                Constants.LINE_SEPARATOR +
                "Id: " +
                Build.ID +
                Constants.LINE_SEPARATOR +
                "Product: " +
                Build.PRODUCT +
                Constants.LINE_SEPARATOR +
                "\n************ FIRMWARE ************\n" +
                "SDK: " +
                Build.VERSION.SDK_INT +
                Constants.LINE_SEPARATOR +
                "Release: " +
                Build.VERSION.RELEASE +
                Constants.LINE_SEPARATOR +
                "Incremental: " +
                Build.VERSION.INCREMENTAL +
                Constants.LINE_SEPARATOR +
                "Version: "
                + getAppVersion() +
                Constants.LINE_SEPARATOR +
                "VersionCode: " +
                getAppVersionCode() +
                Constants.LINE_SEPARATOR;

        return deviceDetails;
    }

    /**
     * This method give click effect of the button
     *
     * @param v (View) : view to be give click effect
     */
    public static void clickEffect(final View v) {
        v.setEnabled(false);
        AlphaAnimation obja = new AlphaAnimation(1.0f, 0.3f);
        obja.setDuration(5);
        obja.setFillAfter(false);
        v.startAnimation(obja);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                v.setEnabled(true);
            }
        }, 2);
    }

    /**
     * This method use to calculate years,months and days
     * from current Local time to selected Local time
     * <br><Strong> Note: </Strong> You can modify this method's return type and return result
     * as per your own requirement.</br>
     *
     * @param birthDate  (String)  : date to be passed to calculate required value e.g. 3/10/2015
     * @param dateFormat (String) : date format to convert in date e.g. dd/MM/yyyy
     * @return age (String)  : it return string as with years, months and days
     * e.g.  age - 1 years 1 months 27 days
     * tested on 30/11/2016
     */
    public static String calculateAge(String birthDate, String dateFormat) {
        String age = "";
        if (!TextUtils.isEmpty(birthDate) && !TextUtils.isEmpty(dateFormat)) {

            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

            Date bDay = null;
            try {
                bDay = sdf.parse(birthDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            int years;
            int months;
            int days;
            //create calendar object for birth day
            Calendar birthDay = Calendar.getInstance();
            birthDay.setTimeInMillis(bDay != null ? bDay.getTime() : 0);
            //create calendar object for current day
            long currentTime = System.currentTimeMillis();
            Calendar now = Calendar.getInstance();
            now.setTimeInMillis(currentTime);
            //Get difference between years
            years = now.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
            int currMonth = now.get(Calendar.MONTH) + 1;
            int birthMonth = birthDay.get(Calendar.MONTH) + 1;
            //Get difference between months
            months = currMonth - birthMonth;
            //if month difference is in negative then reduce years by one and calculate the number of months.
            if (months < 0) {
                years--;
                months = 12 - birthMonth + currMonth;
                if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
                    months--;
            } else if (months == 0 && now.get(Calendar.DATE) < birthDay.get(Calendar.DATE)) {
                years--;
                months = 11;
            }
            //Calculate the days
            if (now.get(Calendar.DATE) > birthDay.get(Calendar.DATE))
                days = now.get(Calendar.DATE) - birthDay.get(Calendar.DATE);
            else if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE)) {
                int today = now.get(Calendar.DAY_OF_MONTH);
                now.add(Calendar.MONTH, -1);
                days = now.getActualMaximum(Calendar.DAY_OF_MONTH) - birthDay.get(Calendar.DAY_OF_MONTH) + today;
            } else {
                days = 0;
                if (months == 12) {
                    years++;
                    months = 0;
                }
            }
            // TODO modify it as per your own requirement
            age = String.valueOf(years) + " years " + String.valueOf(months) + " months " + String.valueOf(days) + " days ";
        }
        return age;
    }

    /**
     * <p>
     * This method use to calculate years, months and days
     * from current Local time to selected Local time.
     * <br><Strong> Note: </Strong> You can modify this method's return type and return result
     * as per your own requirement.</br>
     *
     * @param date       (String)  : date to be passed to calculate required value e.g. 3/10/2015
     * @param dateFormat (String) : date format to convert in date e.g. dd/MM/yyyy
     * @return age (String)  : it return string as with years,months and days
     * e.g.  age - 1 years 1 months 27 days
     * tested on 30/11/2016
     */
    public static String calculateAgeUsingJodaTime(String date, String dateFormat) {
        Date bDate;
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();

        try {
            bDate = sdf.parse(date);
            calendar.setTimeInMillis(bDate.getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }
        LocalDate birthDate = new LocalDate(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DATE));

        LocalDate currentDate = new LocalDate();

        Period period = new Period(birthDate, currentDate, PeriodType.yearMonthDay());

        int year = period.getYears();
        int month = period.getMonths();
        int day = period.getDays();

        // TODO modify it as per your own requirement
        return String.valueOf(year) + " years " + String.valueOf(month) + " months " + String.valueOf(day) + " days ";
    }

    /**
     * This method use to calculate years, months and days
     * from current Local time to selected Local time.
     * <br><Strong> Note: </Strong> You can modify this method's return type and return result
     * as per your own requirement.</br>
     *
     * @param milliSeconds (String)  : date in Millisecond to be passed to calculate required value e.g. 1478180961448
     * @param dateFormat   (String) : date format to convert in date e.g. dd/MM/yyyy
     * @return age (String)  : it return string as with years,months and days
     * e.g.  age - 1 years 1 months 27 days
     * tested on 30/11/2016
     */
    public static String calculateAgeUsingJodaTime(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying Date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.getDefault());
        formatter.format(milliSeconds);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);

        LocalDate birthDate = new LocalDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DATE));

        LocalDate now = new LocalDate();

        Period period = new Period(birthDate, now, PeriodType.yearMonthDay());

        int year = period.getYears();
        int month = period.getMonths();
        int day = period.getDays();

        // TODO modify it as per your own requirement
        return String.valueOf(year) + " years " + String.valueOf(month) + " months " + String.valueOf(day) + " days ";
    }


}