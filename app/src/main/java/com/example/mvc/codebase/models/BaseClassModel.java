package com.example.mvc.codebase.models;

import android.text.TextUtils;

import com.example.mvc.codebase.api.RestClient;

import java.io.Serializable;

/**
 * This class is super class of other model class
 * which contains methods to convert model class into json and
 * json to model class.
 *
 * Declaring Member Variables
 * <a href="https://docs.oracle.com/javase/tutorial/java/javaOO/variables.html">Declaring Member Variables</a>
 */
public class BaseClassModel implements Serializable {


    //Position variable is common for all class
    private int position;

    public BaseClassModel() {
        this.position = -1;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Convert Model class to JsonObject
     */
    public String toJson() {
        try {
            return RestClient.gson.toJson(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method convert json string to respective model class
     *
     * @param strJson (String) : model class json string
     * @param modelClass      : models class object
     */
    public static Object toModelClass(String strJson, Class<?> modelClass) {
        try {
            if (!TextUtils.isEmpty(strJson))
                return RestClient.gson.fromJson(strJson, modelClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BaseClassModel clone() {
        BaseClassModel clone = null;
        try {
            //clone = (ClassBase) super.clone();
            clone = (BaseClassModel) toModelClass(toJson(), this.getClass());

        } catch (Exception e) {
            throw new RuntimeException(e); // won't happen
        }
        return clone;
    }
}
