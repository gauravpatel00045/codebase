package com.example.mvc.codebase.api;

import com.example.mvc.codebase.utils.Debug;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class handle all API responses as per requirement. it parse response using {@link Gson}.
 *
 * @see <a href="https://github.com/google/gson"> Gson</a>
 */
class ResponseManager {

    /**
     * This method parse the the response using Gson library and convert into an object
     *
     * @param requestCode (RequestCode) : To identify the response
     * @param response    (String)         : Response from server
     * @param gson        (Gson)               : To parse response with appropriate Model class
     */
    static <T> Object parse(RequestCode requestCode, String response, Gson gson) {
        Debug.trace("Response: " + response);
        Object object = null;
        try {
            final JSONObject jsonObject;
            jsonObject = new JSONObject(response).getJSONObject("result");

            switch (requestCode) {

                case CHECK_VERSION:
                    object = gson.fromJson(jsonObject.toString(), requestCode.getLocalClass());
                    break;

                case REGISTER_CUSTOMER:
                    object = gson.fromJson(jsonObject.toString(), requestCode.getLocalClass());
                    break;

                case CUSTOMER_LOGIN:
                    object = parseUserLogin(requestCode, jsonObject, gson);
                    break;

                case ADD_DEVICE_TOKEN:
                    object = gson.fromJson(response, requestCode.getLocalClass());
                    break;

                default:
                    object = response;
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }

    /**
     * This method parse the user LoginVerification API response and convert into an object
     *
     * @param requestCode    (RequestCode)   : To identify the response
     * @param responseObject (JSONObject) : Response from server
     * @param gson           (Gson)                 : To type cast with appropriate Model class
     */
    private static <T> Object parseUserLogin(RequestCode requestCode, JSONObject responseObject, Gson gson) throws JsonSyntaxException, JSONException {
        return gson.fromJson(responseObject.getJSONObject("customerDetails").toString(), requestCode.getLocalClass());
    }
}