package com.example.mvc.codebase.validator;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Pattern;

/**
 * This class validate all required credentials.
 * <p><strong>Example: </strong></p> string length, URL format, pattern matching
 */
public class ValidationClass {

    // variable declaration
    private static final String PLUS_SIGN = "+";
    private static final String TAG = ValidationClass.class.getSimpleName();


    /**
     * This method validate string if it is empty or not
     *
     * @param inputString(String) : user input string
     * @return (boolean) : <code>true</code> if string is null or zero length;
     * @see <a href="https://developer.android.com/reference/android/text/TextUtils.html#isEmpty%28java.lang.CharSequence%29">isEmpty</a>
     */
    public static boolean isEmpty(String inputString) {
        return TextUtils.isEmpty(inputString);
    }


    /**
     * This method validate string with specific patterns
     * e.g. email,alphaNumericPassword,webUrl
     *
     * @param inputString(String) : user input string
     * @param pattern(String)     : patterns to be matched with inputString
     * @return (boolean) : it return <code>false</code> if inputString does not match with required pattern.
     * @see android.util.Patterns#EMAIL_ADDRESS
     */
    public static boolean matchPattern(String inputString, String pattern) {
        return !(TextUtils.isEmpty(inputString) || pattern.isEmpty()) && Pattern.matches(pattern, inputString);

    }

    /**
     * This method validate the phone number with specific length
     *
     * @param inputPhoneNumber(String) : user input inputPhoneNumber e.g. 9999999999
     * @param phoneNumberLength(int)   : phone number length e.g. 10
     * @return (boolean) : it return <code>false</code> if inputPhoneNumber length is not equal to phoneNumberLength
     */
    public static boolean checkPhoneNumber(String inputPhoneNumber, int phoneNumberLength) {
        return !(TextUtils.isEmpty(inputPhoneNumber) || phoneNumberLength <= 0) && inputPhoneNumber.trim().length() == phoneNumberLength;
    }

    /**
     * This method check string with minimum length
     *
     * @param inputString(String) : input string
     * @param minLength(int)      :  minimum length e.g. 6
     * @return (boolean) : it return <code>false<code/>  if inputString is less than minLength
     */
    public static boolean checkMinLength(String inputString, int minLength) {
        return !(TextUtils.isEmpty(inputString) || minLength <= 0) && inputString.trim().length() >= minLength;
    }

    /**
     * This method check string with maximum length
     *
     * @param inputString(String) : input string
     * @param maxLength(int)      :  maximum length e.g 10
     * @return (boolean) : it return <code>false</code> if input string is greater than maxLength
     */
    public static boolean checkMaxLength(String inputString, int maxLength) {
        return !(TextUtils.isEmpty(inputString) || maxLength <= 0) && inputString.trim().length() <= maxLength;
    }

    /**
     * This method validate if the number is negative or not
     *
     * @return (boolean) : it return <code>true</code> if the parameter number is less than 0(zero)
     */
    private static boolean isNumberNegative(int number) {
        return number <= 0;
    }

    /**
     * This method check whether inputPhoneNumber starts with +(plus sign) or not.
     *
     * @param inputPhoneNumber(String) : user input inputPhoneNumber
     * @return (boolean) : it return <code>false</code> if and only if inputPhoneNumber does not start with +(plus sign)
     */
    public static boolean checkPlusSign(String inputPhoneNumber) {
        return inputPhoneNumber.trim().startsWith(PLUS_SIGN);
    }

    /**
     * This logic use to check email or phone number in single EditText.
     *
     * @param inputString (String) : user input String
     */
    public boolean validateEmailOrPhone(String inputString) {
        if (inputString.isEmpty()) {
            return false;
        } else {
            if (!TextUtils.isDigitsOnly(inputString)) {
                // show number related error message
                return false;
            } else {
                if (!matchPattern(inputString, Patterns.EMAIL_ADDRESS.pattern())) {
                    // show email related error message
                    return false;
                } else {
                    return true;
                }
            }
        }
    }

}