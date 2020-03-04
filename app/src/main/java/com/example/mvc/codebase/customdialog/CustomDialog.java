package com.example.mvc.codebase.customdialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mvc.codebase.MyApplication;
import com.example.mvc.codebase.R;
import com.example.mvc.codebase.adapter.AdpLocation;
import com.example.mvc.codebase.api.RequestCode;
import com.example.mvc.codebase.customView.ClearableEditText;
import com.example.mvc.codebase.enumerations.CalendarDateSelection;
import com.example.mvc.codebase.interfaces.DataObserver;
import com.example.mvc.codebase.models.CountryModel;
import com.example.mvc.codebase.utils.Constants;
import com.example.mvc.codebase.utils.GenericView;
import com.example.mvc.codebase.utils.Util;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * This class showProgressBar required custom dialog e.g. dialog with single button for alert,
 * dialog with two button with custom layouts and design.
 */
public class CustomDialog {

    // variable declaration
    private static final int PREV_MONTH = 1;
    private String btnText;

    // class object declaration
    private static CustomDialog instance;
    private Dialog dialog;

    // constructor
    private CustomDialog() {
    }

    /**
     * @return (CustomDialog) instance : it return the CustomDialog instance
     */
    public static CustomDialog getInstance() {
        if (instance == null) {
            instance = new CustomDialog();
        }
        return instance;
    }

    /**
     * This method showProgressBar dialog with message
     *
     * @param context       (Context)      : context
     * @param loaderMessage (String) : required dialog message e.g. Loading...,
     *                      please wait..
     */
    public void showProgressBar(Context context, String loaderMessage) {

        dialog = new Dialog(context, R.style.DialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.custom_progressbar);

        TextView mTxtLoading = (TextView) dialog.findViewById(R.id.tv_txtLoading);
        mTxtLoading.setTypeface(MyApplication.mTypefaceMap.get(Constants.HELVETICA_NEUE_LIGHT));

        if (!TextUtils.isEmpty(loaderMessage.trim())) {
            mTxtLoading.setText(loaderMessage);
        }

        try {
            if (dialog != null) {
                if (!dialog.isShowing()) {
                    dialog.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method hide the current showing dialog
     */
    public void hide() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    /**
     * @return (boolean) : return true or false, if the dialog is showing or not
     */
    public boolean isDialogShowing() {

        return dialog != null && dialog.isShowing();
    }

    /**
     * This method shows dialog with alert message with single button
     *
     * @param context      (Context)      : context
     * @param msg          (String)           : required message on dialog
     * @param isCancelable (boolean) : true or false, for dialog cancellation
     *                     e.g. true - dismiss dialog when user touch on screen outside dialog view
     *                     false - disable outside touch except dialog view
     * @param buttonText   (String)    : required text on button eg. submit, update, dismiss
     */
    public void showAlert(final Context context, String msg, boolean isCancelable, String buttonText) {

        dialog = new Dialog(context, R.style.DialogTheme);
        @SuppressLint("InflateParams")
        final View view = LayoutInflater.from(context).inflate(R.layout.custom_dialog_alert, null);

        TextView mTxtAlert = (TextView) view.findViewById(R.id.txtAlert);
        mTxtAlert.setTypeface(MyApplication.mTypefaceMap.get(Constants.HELVETICA_NEUE_LIGHT));

        Button mBtnYes = (Button) view.findViewById(R.id.btnYes);

        mBtnYes.setTypeface(MyApplication.mTypefaceMap.get(Constants.HELVETICA_CONDENSED_BOLD));

        mBtnYes.setText(buttonText);
        mTxtAlert.setText(msg);
        mBtnYes.setTag(buttonText);

        dialog.setCanceledOnTouchOutside(isCancelable);
        dialog.setContentView(view);
        try {
            if (dialog != null) {
                if (!dialog.isShowing()) {
                    dialog.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method shows dialog with required message and automatically dismiss
     * after its time duration
     *
     * @param context      (Context)      : context
     * @param msg          (String)           : message or text that to be show when dialog open
     * @param isCancelable (boolean) : pass true or false to make dialog cancelable or not
     *                     e.g. true - dismiss dialog when user touch on screen outside dialog view
     *                     false - disable outside touch except a dialog view
     */
    public void showAutoHideAlert(final Context context, String msg, boolean isCancelable) {

        long TIME_DELAY = 3000;

        dialog = new Dialog(context, R.style.DialogTheme);
        @SuppressLint("InflateParams")
        final View view = LayoutInflater.from(context).inflate(R.layout.custom_dialog_alert, null);

        TextView mTxtAlert = (TextView) view.findViewById(R.id.txtAlert);
        mTxtAlert.setTypeface(MyApplication.mTypefaceMap.get(Constants.HELVETICA_NEUE_LIGHT));

        mTxtAlert.setText(msg);

        Button mBtnYes = (Button) view.findViewById(R.id.btnYes);
        mBtnYes.setVisibility(View.GONE);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                dialog.dismiss();
            }
        }, TIME_DELAY);

        dialog.setCanceledOnTouchOutside(isCancelable);
        dialog.setContentView(view);
        try {
            if (dialog != null) {
                if (!dialog.isShowing()) {
                    dialog.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method shows dialog with two button, positive action button and negative action button
     *
     * @param context            (Context)           : context
     * @param msg                (String)                : required message on dialog
     * @param isCancelable       (boolean)      : true or false for dialog cancellation
     *                           e.g. true - dismiss dialog when user touch on screen outside dialog view
     *                           false - disable outside touch except a dialog view
     * @param positiveButtonText (String) : positive button text
     * @param negativeButtonText (String) : negative button text
     */
    public void showAlert(Context context, String msg, boolean isCancelable, String positiveButtonText, String negativeButtonText) {

        dialog = new Dialog(context, R.style.DialogTheme);

        @SuppressLint("InflateParams")
        final View view = LayoutInflater.from(context).inflate(R.layout.custom_dialog_two_button, null);

        TextView mTxtAlert = (TextView) view.findViewById(R.id.txtAlert);
        mTxtAlert.setTypeface(MyApplication.mTypefaceMap.get(Constants.HELVETICA_NEUE_LIGHT));

        Button mBtnYes = (Button) view.findViewById(R.id.btnYes);
        mBtnYes.setText(positiveButtonText);
        mBtnYes.setTypeface(MyApplication.mTypefaceMap.get(Constants.HELVETICA_CONDENSED_BOLD));

        Button mBtnNo = (Button) view.findViewById(R.id.btnNo);
        mBtnNo.setText(negativeButtonText);
        mBtnNo.setTypeface(MyApplication.mTypefaceMap.get(Constants.HELVETICA_CONDENSED_BOLD));

        mTxtAlert.setText(msg);
        mBtnYes.setTag(positiveButtonText);

        dialog.setCanceledOnTouchOutside(isCancelable);
        dialog.setCancelable(false);
        dialog.setContentView(view);
        try {
            if (dialog != null) {
                if (!dialog.isShowing()) {
                    dialog.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method shows dialog with two button. It shows with Retry option.
     *
     * @param context            (Context)           : context
     * @param msg                (String)                : required message on dialog
     * @param isCancelable       (boolean)      : true or false for dialog cancellation
     *                           e.g. true - dismiss dialog when user touch on screen outside dialog view
     *                           false - disable outside touch except a dialog view
     * @param positiveButtonText (String) :  button text
     * @param negativeButtonText (String) :  button text
     * @param requestCode        (RequestCode) : request code
     * @param dataObserver       (DataObserver) : interface instance
     */
    public void showAlert(final Context context, String msg, boolean isCancelable,
                          String positiveButtonText, String negativeButtonText,
                          final RequestCode requestCode, final DataObserver dataObserver) {


        dialog = new Dialog(context, R.style.DialogTheme);

        @SuppressLint("InflateParams")
        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_retry, null);

        TextView mTxtAlert = (TextView) view.findViewById(R.id.txtAlert);
        mTxtAlert.setTypeface(MyApplication.mTypefaceMap.get(Constants.HELVETICA_NEUE_LIGHT));

        Button btnRetry = (Button) view.findViewById(R.id.btn_retry);
        btnRetry.setText(positiveButtonText);
        btnRetry.setTypeface(MyApplication.mTypefaceMap.get(Constants.HELVETICA_CONDENSED_BOLD));

        final Button btnOk = (Button) view.findViewById(R.id.btn_ok);
        btnOk.setText(negativeButtonText);
        btnOk.setTypeface(MyApplication.mTypefaceMap.get(Constants.HELVETICA_CONDENSED_BOLD));

        mTxtAlert.setText(msg);

        btnRetry.setTag(positiveButtonText);
        btnOk.setTag(negativeButtonText);

        dialog.setCanceledOnTouchOutside(isCancelable);
        dialog.setCancelable(false);
        dialog.setContentView(view);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ((Activity) context).finish();
            }
        });

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dataObserver.onRetryRequest(requestCode);
            }
        });

        try {
            if (dialog != null) {
                if (!dialog.isShowing()) {
                    dialog.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method open Date picker dialog to select Date.
     * This method manages future, past and all Date selection in calender
     * e.g.
     * <p></p>
     * case 1: {@link CalendarDateSelection#CALENDAR_WITH_ALL_DATE}
     * <p>
     * No constraint in date selection
     * </p>
     * case 2 : {@link CalendarDateSelection#CALENDAR_WITH_FUTURE_DATE}
     * <br>Note : To compatible with jelly bean (api 16) you must subtract 1000 milliseconds
     * from current millisecond like</br>
     * <pre>{@code
     *  mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
     *  mDatePicker.getDatePicker().setMaxDate(minDate.getTimeInMillis());
     * }
     *
     * </pre>
     * <p>
     * Set future Date limit e.g. event - pass Date[1-31], month [1-12], year[2016-2099].
     * <br>i will pass value in parameter like this</br>
     * <pre>{@code
     *     year = 2020
     *     month = 12
     *     day = 31
     *     }
     * </pre>
     * </p>
     * case 3:  {@link CalendarDateSelection#CALENDAR_WITH_PAST_DATE}
     * <p>
     * Set past Date limit e.g. birthDate, age limit - pass Date[1-31], month [1-12], year[1970-2016].
     * <br> Pass value in parameter like this</br>
     * <pre>{@code
     *     year = 1970
     *     month = 1
     *     day = 3
     *     }
     * </pre>
     * </p>
     *
     * @param context               (Context)  : context
     * @param textView              (TextView)   : to show selected Date
     * @param calendarDateSelection (enum) :  e.g. CALENDER_WITH_PAST_DATE
     * @param year                  (int)     : year e.g. 2016
     * @param month                 (int)     : month e.g. 9
     * @param day                   (int)     : day   e.g. 20
     */
    public static void showDatePickerDialog(final Context context, final TextView textView, final CalendarDateSelection calendarDateSelection, int year, int month, int day) {

        final Calendar mCurrentDate = Calendar.getInstance();

        final Calendar minDate = Calendar.getInstance();

        int mYear = mCurrentDate.get(Calendar.YEAR);
        int mMonth = mCurrentDate.get(Calendar.MONTH);
        int mDay = mCurrentDate.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog mDatePicker = new DatePickerDialog(context, R.style.DatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {

                mCurrentDate.set(selectedyear, selectedmonth, selectedday);
                textView.setText(Util.dateFormat(mCurrentDate.getTimeInMillis(), Constants.DATE_FORMAT));
                /* it is used to pass selected Date in millisecond*/
                textView.setTag(mCurrentDate.getTimeInMillis());

            }
        }, mYear, mMonth, mDay);


        switch (calendarDateSelection) {

            case CALENDAR_WITH_ALL_DATE:

                break;
            case CALENDAR_WITH_PAST_DATE:

                minDate.set(Calendar.YEAR, year);
                minDate.set(Calendar.MONTH, month - PREV_MONTH);
                minDate.set(Calendar.DAY_OF_MONTH, day);

                mDatePicker.getDatePicker().setMinDate(minDate.getTimeInMillis());
                mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
                break;
            case CALENDAR_WITH_FUTURE_DATE:

                minDate.set(Calendar.YEAR, year);
                minDate.set(Calendar.MONTH, month - PREV_MONTH);
                minDate.set(Calendar.DAY_OF_MONTH, day);

                mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis());
                mDatePicker.getDatePicker().setMaxDate(minDate.getTimeInMillis());
                break;
        }

        mDatePicker.setTitle(context.getString(R.string.lblSelectDate));
        try {
            if (!mDatePicker.isShowing()) {
                mDatePicker.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * This method open Date picker dialog to select Date.This method  manages interval in
     * past or in future
     * <p><strong> Example: </strong> </p>
     * case 1: {@link CalendarDateSelection#CALENDAR_WITH_PAST_OR_FUTURE_DATE_INTERVAL}
     * <p>
     * set interval in past like from 1970 to 2000.
     * <br> pass value in parameter like this</br>
     * <pre>{@code
     * fromYear = 1970
     * fromMonth = 1
     * fromDay = 1
     * toYear = 2000
     * toMonth = 12
     * toDay = 31
     * }
     * </pre>
     * </p>
     * case 2: {@link CalendarDateSelection#CALENDAR_WITH_PAST_OR_FUTURE_DATE_INTERVAL}
     * <p>
     * set interval in future like from 2020 to 2030.
     * <br>Note : To compatible with jelly bean (api 16) you must subtract 1000 milliseconds
     * from current millisecond like</br>
     * <pre>{@code
     *  mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
     *  mDatePicker.getDatePicker().setMaxDate(minDate.getTimeInMillis());
     * }
     * </pre>
     * <br> pass value in parameter like this</br>
     * <pre>{@code
     * fromYear = 2020
     * fromMonth = 1
     * fromDay = 1
     * toYear = 2030
     * toMonth = 12
     * toDay = 31
     * }
     * </pre>
     * </p>
     * case 3: {@link CalendarDateSelection#CALENDAR_WITH_PAST_OR_FUTURE_DATE_INTERVAL}
     * <p>
     * set age limit e.g. 10
     * <p>
     * Example: i want to set age limit 10. 10 year old is minimum age to register.
     * </p>
     * <p>
     * <pre>{@code
     * private Calendar calendar = Calendar.getInstance();
     * currentYear = calendar.get(Calendar.YEAR);
     * currentMonth = calendar.get(Calendar.MONTH);
     * currentDay = calendar.get(Calendar.DAY_OF_MONTH);
     * }
     * </pre>
     * <p>
     * <br> pass value in parameter like this</br>
     * <pre>{@code
     * fromYear = 1970
     * fromMonth = 1
     * fromDay = 1
     * toYear = currentYear - 10 // subtract age limit from current year
     * toMonth = currentMonth + 1 // refer below link {@link Calendar#MONTH}
     * toDay = currentDay
     * }
     * </pre>
     * case 4: {@link CalendarDateSelection#CALENDAR_WITH_PAST_OR_FUTURE_DATE_INTERVAL}
     * <p>
     * set limit from past to future like from 2000 to 2020.
     * <br>Note : To compatible with jelly bean (api 16) you must subtract 1000 milliseconds
     * from current millisecond like</br>
     * <pre>{@code
     *  mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
     *  mDatePicker.getDatePicker().setMaxDate(minDate.getTimeInMillis());
     * }
     * </pre>
     * <p>
     * <br> pass value in parameter like this</br>
     * <pre>{@code
     * fromYear = 2000
     * fromMonth = 1
     * fromDay = 1
     * toYear = 2020
     * toMonth = 12
     * toDay = 31
     * }
     * </pre>
     * <p>
     * <strong> Note: </strong> The Months are numbered from 0 (January) to 11 (December).
     * </p>
     *
     * @param context               (Context)  : context
     * @param textView              (TextView)   : to show selected Date
     * @param calendarDateSelection (enum) :  e.g. CALENDAR_WITH_PAST_OR_FUTURE_DATE_INTERVAL
     * @param fromYear              (int)     : year e.g. 2016
     * @param fromMonth             (int)     : month e.g. 1
     * @param fromDay               (int)     : day   e.g. 1
     * @param toYear                (int)     : year e.g. 2020
     * @param toMonth               (int)     : month e.g. 12
     * @param toDay                 (int)     : day   e.g. 31
     * @see <a href="http://docs.oracle.com/javase/6/docs/api/java/util/Calendar.html#MONTH"> Calendar.MONTH </a>
     */
    public static void showDatePickerDialog(final Context context, final TextView textView, final CalendarDateSelection calendarDateSelection,
                                            int fromYear, int fromMonth, int fromDay,
                                            int toYear, int toMonth, int toDay) {

        // calender instance
        final Calendar fromCalender = Calendar.getInstance();
        final Calendar toCalender = Calendar.getInstance();

        int mFromYear = fromCalender.get(Calendar.YEAR);
        int mFromMonth = fromCalender.get(Calendar.MONTH);
        int mFromDay = fromCalender.get(Calendar.DAY_OF_MONTH);

        int mToYear = toCalender.get(Calendar.YEAR);
        int mToMonth = toCalender.get(Calendar.MONTH);
        int mToDay = toCalender.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog mDatePicker = new DatePickerDialog(context, R.style.DatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {

                toCalender.set(selectedyear, selectedmonth, selectedday);
                textView.setText(Util.dateFormat(toCalender.getTimeInMillis(), Constants.DATE_FORMAT));
                /* it is used to pass selected Date in millisecond*/
                textView.setTag(toCalender.getTimeInMillis());

            }
        }, mToYear, mToMonth, mToDay);


        switch (calendarDateSelection) {

            case CALENDAR_WITH_PAST_OR_FUTURE_DATE_INTERVAL:
                // start calender from
                fromCalender.set(Calendar.YEAR, fromYear);
                fromCalender.set(Calendar.MONTH, fromMonth - PREV_MONTH);
                fromCalender.set(Calendar.DAY_OF_MONTH, fromDay);

                // end calender to
                toCalender.set(Calendar.YEAR, toYear);
                toCalender.set(Calendar.MONTH, toMonth - PREV_MONTH);
                toCalender.set(Calendar.DAY_OF_MONTH, toDay);

                mDatePicker.getDatePicker().setMinDate(fromCalender.getTimeInMillis());
                mDatePicker.getDatePicker().setMaxDate(toCalender.getTimeInMillis());
                break;
        }

        mDatePicker.setTitle(context.getString(R.string.lblSelectDate));
        try {
            if (!mDatePicker.isShowing()) {
                mDatePicker.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * This method show the dialog with list of locations and having functionality of search
     *
     * @param context      (Context)        : context
     * @param listLocation (ArrayList) : list of locations with countries, states and cities
     * @param textView     (TextView)      : to show selected location e.g country-United Kingdom,
     *                     state- new york, city- london
     */
    public void showLocationListDialog(final Context context, final ArrayList<CountryModel> listLocation, final TextView textView) {
        try {

            dialog = new Dialog(context, R.style.DialogTheme);
            // View view = LayoutInflater.from(context).inflate(R.layout.custom_dialog_location_list, null);
            dialog.setContentView(R.layout.custom_dialog_location_list);

            /** Set Dialog width match parent */
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            /*Set out side touch hide keyboard*/
            Util.setupOutSideTouchHideKeyboard(dialog.findViewById(R.id.parentDialog));

            TextView txtTitleDialog = GenericView.findViewById(dialog, R.id.tv_titleDialog);
            txtTitleDialog.setTypeface(MyApplication.mTypefaceMap.get(Constants.OSWALD_BOLD));
            // Set Dialog title
            txtTitleDialog.setText(Util.getAppKeyValue(context, R.string.errorMsgSelectCountry));

            ClearableEditText txtSearch = GenericView.findViewById(dialog, R.id.et_searchLocation);
            txtSearch.setHint(Util.getAppKeyValue(context, R.string.lblSearch));

            final ListView listViewLocation = GenericView.findViewById(dialog, R.id.lv_location);

            final TextView txtNoRecord = GenericView.findViewById(dialog, R.id.tv_noRecord);
            txtNoRecord.setTypeface(MyApplication.mTypefaceMap.get(Constants.HELVETICA_CONDENSED_MEDIUM));
            txtNoRecord.setText(Util.getAppKeyValue(context, R.string.lblNoCountryFound));

            LinearLayout linSearch = GenericView.findViewById(dialog, R.id.lin_search);
            Button btnCancel = GenericView.findViewById(dialog, R.id.btn_cancel);
            btnCancel.setText(Util.getAppKeyValue(context, R.string.lblCancel));

            final EditText edtSearchLocation = GenericView.findViewById(dialog, R.id.et_searchLocation);
            edtSearchLocation.setTypeface(MyApplication.mTypefaceMap.get(Constants.HELVETICA_CONDENSED_MEDIUM));

            //if listLocation.size() > 0 than set adapter in ListView
            if (listLocation != null && listLocation.size() > 0) {

                listViewLocation.setVisibility(View.VISIBLE);
                AdpLocation adpLocationList = new AdpLocation(context, listLocation, Constants.DEFAULT_BLANK_STRING);
                listViewLocation.setAdapter(adpLocationList);
            } else {

                listViewLocation.setVisibility(View.GONE);
                txtNoRecord.setVisibility(View.VISIBLE);
            }

            if (dialog != null && !dialog.isShowing()) {
                dialog.show();
            }

            listViewLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    /* After select any country we get the name from selected position and
                    * dialog should be dismiss */
                    dialog.dismiss();

                    /* Get value using getTag and setTag method */
                    TextView txtLocationName = GenericView.findViewById(view, R.id.txtLocationName);
                    Object tag = txtLocationName.getTag();

                    if (tag != null) {
                        //cast object to relevant model (CountryModel) class
                        CountryModel countryModel = (CountryModel) tag;
                        textView.setText(countryModel.getCountryName());
                        textView.setTag(countryModel);
                    }

                }
            });

            edtSearchLocation.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                    /** Show clear icon on editText length greater than zero */
                    String searchStr = edtSearchLocation.getText().toString().trim();

                    ArrayList<CountryModel> filterList = new ArrayList<>();

                    if (listLocation != null && listLocation.size() > 0) {
                        for (int i = 0; i < listLocation.size(); i++) {
                            try {

                                CountryModel countryModel = listLocation.get(i);

                                if (countryModel.getCountryName().toLowerCase().startsWith(searchStr.toLowerCase().trim())) {
                                    filterList.add(countryModel);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    /* it shows country list with user searched keywords */
                    if (filterList.size() > 0) {
                        txtNoRecord.setVisibility(View.GONE);
                        listViewLocation.setVisibility(View.VISIBLE);

                        AdpLocation adpLocationList = new AdpLocation(context, filterList, searchStr/*, locationType*/);
                        listViewLocation.setAdapter(adpLocationList);

                    } else {
                        txtNoRecord.setVisibility(View.VISIBLE);
                        listViewLocation.setVisibility(View.GONE);
                    }

                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}