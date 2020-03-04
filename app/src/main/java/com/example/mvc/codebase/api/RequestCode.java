package com.example.mvc.codebase.api;


import com.example.mvc.codebase.firebaseNotification.DeviceTokenModel;
import com.example.mvc.codebase.models.CheckVersionModel;
import com.example.mvc.codebase.models.CustomerDetails;
import com.example.mvc.codebase.models.RegisterCustomerModel;

import java.lang.reflect.Type;

/**
 * This enum store all API Request Codes
 */
public enum RequestCode {

    ADD_DEVICE_TOKEN(DeviceTokenModel.class),
    CHECK_VERSION(CheckVersionModel.class),
    CUSTOMER_LOGIN(CustomerDetails.class),
    REGISTER_CUSTOMER(RegisterCustomerModel.class),
    CRASH_REPORT(null);


    private Class<?> localClass = null;
    private Type localType = null;

    RequestCode(Class<?> localClass) {
        this.localClass = localClass;
    }

    /**
     * @return localClass(Class) : to get class
     */
    public Class<?> getLocalClass() {
        return localClass;
    }

    /**
     * @return localType(Type) : to get localType
     */
    public Type getLocalType() {
        return localType;
    }
}
