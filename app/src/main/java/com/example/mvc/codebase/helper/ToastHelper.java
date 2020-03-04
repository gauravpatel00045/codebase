package com.example.mvc.codebase.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mvc.codebase.MyApplication;
import com.example.mvc.codebase.R;
import com.example.mvc.codebase.utils.Constants;

/**
 * This is custom toast class to display message. Advantage of {@link ToastHelper} is that, we can
 * cancel unnecessary toast when user click on button more than once.
 */
public class ToastHelper {

    // class object declaration
    private static Toast toast;
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    @SuppressLint("StaticFieldLeak")
    private static ToastHelper toastHelper;

    /**
     * @param mContext (Context) : context
     * @return toastHelper (ToastHelper) : to get ToastHelper instance
     */
    public static ToastHelper getInstance(Context mContext) {
        if (toast == null && toastHelper == null) {
            context = mContext;
            toast = new Toast(mContext);
            toastHelper = new ToastHelper();
        }
        return toastHelper;
    }

    /**
     * This method return a Toast instance
     *
     * @return toast (Toast) : to get toast object
     */
    public static Toast getToast() {
        return toast;
    }

    /**
     * This method dismiss toast when not required
     */
    public void cancelToast() {
        if (toast != null) {
            toast.cancel();
        }
    }

    /**
     * This method display the message in toast
     *
     * @param message (String) : required message to display
     */
    public void displayToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * This method display the message in custom toast layout
     *
     * @param message (String) : required message to display
     */
    public void displayCustomToast(String message) {


        View mView = View.inflate(context,R.layout.custom_toast,null);

        TextView mTxtMessage = (TextView) mView.findViewById(R.id.tv_message);
        mTxtMessage.setTypeface(MyApplication.mTypefaceMap.get(Constants.HELVETICA_CONDENSED_BOLD));
        mTxtMessage.setText(message);

        toast.setView(mView);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,
                0, 0);

        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }
}
