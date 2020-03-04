package com.example.mvc.codebase.models;

import java.io.Serializable;

/**
 *
 * This class store the required all messages
 */

public class MessageListModel implements Serializable {

    private int id;

    private String keyValue;
    private String msgValue;


    /**
     * @return The id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id The Id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return The keyValue
     */
    public String getKeyValue() {
        return keyValue;
    }

    /**
     * @param keyValue The keyValue
     */
    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    /**
     * @return The msgValue
     */
    public String getMsgValue() {
        return msgValue;
    }

    /**
     * @param msgValue The msgValue
     */
    public void setMsgValues(String msgValue) {
        this.msgValue = msgValue;
    }

}
