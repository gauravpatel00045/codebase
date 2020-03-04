package com.example.mvc.codebase.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;

/**
 * This class bind the xml view with activity
 * with custom methods.
 */
public class GenericView {

    /**
     * Convenience method of findViewById
     *
     * @param parent : view
     * @param id     : resource id e.g. R.id.txt_appVersion
     */
    @SuppressWarnings("unchecked")
    public static <T extends View> T findViewById(final View parent, final int id) {
        return (T) parent.findViewById(id);
    }

    /**
     * Convenience method of findViewById
     *
     * @param activity : activity
     * @param id       : resource id e.g. R.id.txt_appVersion
     */
    @SuppressWarnings("unchecked")
    public static <T extends View> T findViewById(final Activity activity, final int id) {
        return (T) activity.findViewById(id);
    }

    /**
     * Convenience method of findViewById
     *
     * @param dialog : dialog
     * @param id     : resource id e.g. R.id.txt_appVersion
     */
    @SuppressWarnings("unchecked")
    public static <T extends View> T findViewById(final Dialog dialog, final int id) {
        return (T) dialog.findViewById(id);
    }

    /**
     * This method get the string values from resources
     *
     * @param context : context
     * @param id      : resource id of string in String.xml file e.g. R.string.lblLoading
     */
    public static String getString(final Context context, final int id) {
        return context.getResources().getString(id);
    }
}
