package com.example.mvc.codebase.api;

/**
 * This class handle response status and message.
 * <pre>{@code
 * status = 1  // response status success
 * status = 2  // response status fail
 * status = 3  // response status error
 * status = 4  // response status invalid
 * }
 * </pre>
 */

public class ResponseStatus {
    // variable declaration
    public static final int STATUS_SUCCESS = 1;   // response status success
    public static final int STATUS_FAIL = 2;   // response status fail
    public static final int STATUS_ERROR = 3;   // response status error
    public static final int STATUS_INVALID = 4;   // response status invalid

    private int status;
    private String message;

    /**
     * @return (String) message : to get status message
     */
    String getMessage() {
        return message;
    }

    /**
     * @param message (String)  : to set status message
     */
    public void setMessage(final String message) {
        this.message = message;
    }

    /**
     * @return (String) status  : to get status success or fail
     * <pre>{@code
     * status = 1  // response status success
     * status = 2  // response status fail
     * status = 3  // response status error
     * status = 4  // response status invalid
     * }
     * </pre>
     */
    int getStatus() {
        return status;
    }

    /**
     * @param status (String) : to set status success or fail
     */
    public void setStatus(final int status) {
        this.status = status;
    }

    /**
     * give status of API call
     *
     * @return (boolean) : return Either true or false on API response
     */
    boolean isSuccess() {
        if (status != 0) {
            if (status == (STATUS_SUCCESS)) {
                return true;
            }
        }
        return false;
    }

    /**
     * give status of error API call
     *
     * @return (boolean) : return either true or false on API response
     */
    boolean isFail() {
        if (status != 0) {
            if (status == (STATUS_FAIL)) {
                return true;
            }
        }
        return false;
    }

    /**
     * give status of error API call
     *
     * @return (boolean) : return either true or false on API response
     */
    boolean isError() {
        if (status != 0) {
            if (status == (STATUS_ERROR)) {
                return true;
            }
        }
        return false;
    }

    /**
     * give status of error API call
     *
     * @return (boolean) : return either true or false on API response
     */
    boolean isInvalid() {
        if (status != 0) {
            if (status == (STATUS_INVALID)) {
                return true;
            }
        }
        return false;
    }

}
