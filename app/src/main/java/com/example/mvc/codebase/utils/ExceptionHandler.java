package com.example.mvc.codebase.utils;

import android.content.Context;
import android.os.StrictMode;

import com.example.mvc.codebase.R;
import com.example.mvc.codebase.api.ApiList;
import com.example.mvc.codebase.api.ServerConfig;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * This class handle all un caught exception in application and
 * send exception report to respective email ids.
 * <p><strong> Example </strong></p>
 * <pre {@code
 * public class MyApplication extends Application {
 * public void onCreate() {
 * super.onCreate();
 * // To catch un caught exception error and send error log to server.
 * Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
 * }
 * }
 * }
 *
 * @see <a href="https://developer.android.com/reference/java/lang/Thread.UncaughtExceptionHandler.html"> Thread.UncaughtExceptionHandler </a>
 */
@SuppressWarnings("FieldCanBeLocal")
public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    // variable declaration
    private final String LINE_SEPARATOR = "\n";
    private String response;

    // class object declaration
    private final Context mContext;


    // constructor
    public ExceptionHandler(Context context) {
        mContext = context;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable exception) {

        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        Calendar cal = Calendar.getInstance();

        StringBuilder errorReport = new StringBuilder();

        errorReport.append("***** LOCAL CAUSE OF ERROR (")
                .append(mContext.getString(R.string.app_name))
                .append(") Version: ")
                .append(Util.getAppVersion())
                .append(" Date: ")
                .append(cal.getTime())
                .append(" *****\n\n");
        errorReport.append("Method Name: ")
                .append(exception.getStackTrace()[0].getMethodName())
                .append(LINE_SEPARATOR);
        errorReport.append("Localized Error Message: ")
                .append(exception.getLocalizedMessage())
                .append(LINE_SEPARATOR);
        errorReport.append("Error Message: ")
                .append(exception.getMessage())
                .append(LINE_SEPARATOR);
        errorReport.append("StackTrace: ")
                .append(stackTrace.toString())
                .append(LINE_SEPARATOR)
                .append(LINE_SEPARATOR);

        errorReport.append(Util.getDeviceDetails());

        try {
            URL url;
            Debug.trace("URL: " + ServerConfig.SERVER_URL + ApiList.API_ADD_CRASH_REPORT);
            JSONObject objParam = new JSONObject();
            objParam.put("errorText", errorReport.toString());
            Debug.trace("TAG", "errorText: " + objParam.toString());
            String crashReportUrl = ServerConfig.SERVER_URL + ApiList.API_ADD_CRASH_REPORT;
            url = new URL(crashReportUrl);


            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(60000);
            conn.setConnectTimeout(60000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("User-Agent", "android");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Type", "application/json");

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            writer.write(objParam.toString());
            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
                errorReport.append("Response: ")
                        .append(LINE_SEPARATOR)
                        .append(response)
                        .append(LINE_SEPARATOR);
            } else {
                response = "";
            }

            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ToDO Remain.
     */
    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {

        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}