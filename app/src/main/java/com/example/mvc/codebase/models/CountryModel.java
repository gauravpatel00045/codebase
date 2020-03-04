package com.example.mvc.codebase.models;

import com.example.mvc.codebase.utils.Constants;


/**
 * This class store the country data
 */

public class CountryModel extends BaseClassModel {

    private int countryId;

    private String countryCode;
    private String countryName;
    private String currencyCode;
    private String currencySymbol;
    private String cultureName;
    private String timezoneOffset;
    private String ISDCode;

    private boolean isActive;
    private boolean isDeleted;

    public CountryModel() {
        countryId = Constants.ZERO;
        countryCode = Constants.DEFAULT_BLANK_STRING;
        countryName = Constants.DEFAULT_BLANK_STRING;
        currencyCode = Constants.DEFAULT_BLANK_STRING;
        currencySymbol = Constants.DEFAULT_BLANK_STRING;
        cultureName = Constants.DEFAULT_BLANK_STRING;
        timezoneOffset = Constants.DEFAULT_BLANK_STRING;
        ISDCode = Constants.DEFAULT_BLANK_STRING;
        isActive = false;
        isDeleted = false;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getCultureName() {
        return cultureName;
    }

    public void setCultureName(String cultureName) {
        this.cultureName = cultureName;
    }

    public String getTimezoneOffset() {
        return timezoneOffset;
    }

    public void setTimezoneOffset(String timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
    }

    public String getISDCode() {
        return ISDCode;
    }

    public void setISDCode(String ISDCode) {
        this.ISDCode = ISDCode;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
