package com.example.mvc.codebase.interfaces;

import com.example.mvc.codebase.api.RequestCode;

/**
 * This interface includes call back methods
 * to notify the activity when background process completed
 */

public interface DataObserver {

    /**
     * This method called when a request completes with the response.
     * Executed by background thread.
     *
     * @param requestCode (RequestCode) : To identify the request type
     */
    void OnSuccess(RequestCode requestCode);

    /**
     * This method called when a request has a network or request error.
     * Executed by background thread.
     *
     * @param requestCode (RequestCode) : To identify the request type
     * @param error       (String) : required error message e.g. network error, request time out error
     */
    void OnFailure(RequestCode requestCode, String error);

    /**
     * This method called when a request needs to recall
     *
     * @param requestCode (RequestCode) : To identify the request type
     */
    void onRetryRequest(RequestCode requestCode);
}
