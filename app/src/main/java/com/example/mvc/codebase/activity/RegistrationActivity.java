package com.example.mvc.codebase.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mvc.codebase.MyApplication;
import com.example.mvc.codebase.R;
import com.example.mvc.codebase.api.RequestCode;
import com.example.mvc.codebase.customdialog.CustomDialog;
import com.example.mvc.codebase.enumerations.CalendarDateSelection;
import com.example.mvc.codebase.enumerations.RegisterBy;
import com.example.mvc.codebase.graphicsUtils.GraphicsUtil;
import com.example.mvc.codebase.helper.ToastHelper;
import com.example.mvc.codebase.interfaces.ClickEvent;
import com.example.mvc.codebase.interfaces.DataObserver;
import com.example.mvc.codebase.models.CheckVersionModel;
import com.example.mvc.codebase.models.CountryModel;
import com.example.mvc.codebase.models.CustomerDetails;
import com.example.mvc.codebase.models.RegisterCustomerModel;
import com.example.mvc.codebase.permissionUtils.PermissionClass;
import com.example.mvc.codebase.utils.Constants;
import com.example.mvc.codebase.utils.Debug;
import com.example.mvc.codebase.utils.GenericView;
import com.example.mvc.codebase.utils.Util;
import com.example.mvc.codebase.validator.ValidationClass;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("ALL")
public class RegistrationActivity extends AppCompatActivity implements ClickEvent, TextWatcher, DataObserver {

    // xml component declaration
    private ImageView ivUserDp;
    private Snackbar snackbar;
    private TextView txtRegistration, txtDateFormat, txtBirthDate, txtCountry;
    private TextInputLayout inputTextFirstName, inputTextLastName, inputTextEmail,
            inputTextPhoneNumber, inputTextPassword, inputTextConfPassword;
    private EditText edtFirstName, edtLastName, edtEmail, edtPhoneNumber, edtPassword,
            edtConfPassword;
    private Button btnRegister;
    private RelativeLayout activity_registration;
    private CheckBox cbTermsConditions;

    // variables declaration
    public static final String TAG = RegistrationActivity.class.getSimpleName();
    public static final int REQUEST_CAMERA = 1;
    public static final int REQUEST_GALLERY = 2;
    private int[] screenWH;
    private final int CROPPED_IMAGE_REQUEST = 3;

    private long birthDayInMilliSecond;

    private String selectedCountryName;
    private String selectedImagePath;


    //class object declaration
    Activity activityContext;
    private RegisterCustomerModel registerUser;
    private CountryModel countryModel;
    private CustomerDetails customerDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        init();

        screenWH = GraphicsUtil.getScreenWidthHeight();
        /* it check that if registration required than it will set data that we get and
        fill required data from user */
        if (getIntent() != null && getIntent().hasExtra(Constants.KEY_REGISTER_REQUIRED)) {
            customerDetails = (CustomerDetails) getIntent().getSerializableExtra(Constants.KEY_REGISTER_REQUIRED);
            setUserData(customerDetails);
        } else {
            customerDetails = new CustomerDetails();
        }
    }

    /**
     * Initialise view of xml component here
     * eg. textView, editText
     * and initialisation of required class objects
     */
    private void init() {

        activityContext = this;


        activity_registration = GenericView.findViewById(this, R.id.activity_registration);

        ivUserDp = GenericView.findViewById(this, R.id.iv_userDp);

        txtRegistration = GenericView.findViewById(this, R.id.txt_registration);
        txtRegistration.setTypeface(MyApplication.mTypefaceMap.get(Constants.HELVETICA_CONDENSED_BOLD));
        txtRegistration.setText(Util.getAppKeyValue(this, R.string.lblRegistration));

        inputTextFirstName = GenericView.findViewById(this, R.id.inputText_registrationFirstName);
        inputTextFirstName.setHint(Util.getAppKeyValue(this, R.string.lblFirstName));

        edtFirstName = GenericView.findViewById(this, R.id.edt_registrationFirstName);
        edtFirstName.setTypeface(MyApplication.mTypefaceMap.get(Constants.HELVETICA_NEUE_LIGHT));

        inputTextLastName = GenericView.findViewById(this, R.id.inputText_registrationLastName);
        inputTextLastName.setHint(Util.getAppKeyValue(this, R.string.lblLastName));

        edtLastName = GenericView.findViewById(this, R.id.edt_registrationLastName);
        edtLastName.setTypeface(MyApplication.mTypefaceMap.get(Constants.HELVETICA_NEUE_LIGHT));

        inputTextEmail = GenericView.findViewById(this, R.id.inputText_registrationEmail);
        inputTextEmail.setHint(Util.getAppKeyValue(this, R.string.lblEmail));

        edtEmail = GenericView.findViewById(this, R.id.edt_registrationEmail);
        edtEmail.setTypeface(MyApplication.mTypefaceMap.get(Constants.HELVETICA_NEUE_LIGHT));

        inputTextPhoneNumber = GenericView.findViewById(this, R.id.inputText_registrationPhone);
        inputTextPhoneNumber.setHint(Util.getAppKeyValue(this, R.string.lblPhoneNo));

        edtPhoneNumber = GenericView.findViewById(this, R.id.edt_registrationPhone);
        edtPhoneNumber.setTypeface(MyApplication.mTypefaceMap.get(Constants.HELVETICA_NEUE_LIGHT));

        txtBirthDate = GenericView.findViewById(this, R.id.tv_RegistrationBirthDate);
        txtBirthDate.setText(Util.getAppKeyValue(this, R.string.lblDOB));

        txtDateFormat = GenericView.findViewById(this, R.id.txt_registrationDateFormat);
        txtDateFormat.setHint(Util.getAppKeyValue(this, R.string.lblDateFormat));

        inputTextPassword = GenericView.findViewById(this, R.id.inputText_registrationPassword);
        inputTextPassword.setHint(Util.getAppKeyValue(this, R.string.lblPwd));

        edtPassword = GenericView.findViewById(this, R.id.edt_registrationPassword);
        edtPassword.setTypeface(MyApplication.mTypefaceMap.get(Constants.HELVETICA_NEUE_LIGHT));

        inputTextConfPassword = GenericView.findViewById(this, R.id.inputText_registrationConfirmPassword);
        inputTextConfPassword.setHint(Util.getAppKeyValue(this, R.string.lblConfPwd));

        edtConfPassword = GenericView.findViewById(this, R.id.edt_registrationConfirmPassword);
        edtConfPassword.setTypeface(MyApplication.mTypefaceMap.get(Constants.HELVETICA_NEUE_LIGHT));

        txtCountry = GenericView.findViewById(this, R.id.tv_RegistrationCountry);
        txtCountry.setTypeface(MyApplication.mTypefaceMap.get(Constants.HELVETICA_NEUE_LIGHT));

        cbTermsConditions = GenericView.findViewById(this, R.id.cb_registrationTermConditions);

        btnRegister = GenericView.findViewById(this, R.id.btn_registration);
        btnRegister.setText(Util.getAppKeyValue(this, R.string.lblRegister));

        edtFirstName.addTextChangedListener(this);
        edtLastName.addTextChangedListener(this);
        edtEmail.addTextChangedListener(this);
        edtPhoneNumber.addTextChangedListener(this);
        edtPassword.addTextChangedListener(this);
        edtConfPassword.addTextChangedListener(this);

        Util.setupOutSideTouchHideKeyboard(findViewById(R.id.activity_registration));

        registerUser = new RegisterCustomerModel();
    }

    @Override
    public void onClickEvent(View view) {
        Util.clickEffect(view);
        switch (view.getId()) {
            case R.id.iv_userDp:
                selectImage();
                break;

            case R.id.txt_registrationDateFormat:
                ToastHelper.getInstance(RegistrationActivity.this).cancelToast();
                CustomDialog.showDatePickerDialog(this, txtDateFormat, CalendarDateSelection.CALENDAR_WITH_PAST_DATE,
                        Constants.DEFAULT_CALENDER_YEAR, Constants.DEFAULT_CALENDER_MONTH, Constants.DEFAULT_CALENDER_DATE);
                break;

            case R.id.tv_RegistrationCountry:
                CustomDialog.getInstance().showLocationListDialog(this, CheckVersionModel.getCheckVersionModel().getCountryList(), txtCountry);
                break;

            case R.id.btn_registration:

                if (validateRegistration()) {
                    customerDetails.setFirstName(edtFirstName.getText().toString().trim());
                    customerDetails.setLastName(edtLastName.getText().toString().trim());
                    customerDetails.setEmail(edtEmail.getText().toString().trim());
                    customerDetails.setPhone(edtPhoneNumber.getText().toString().trim());
                    customerDetails.setBirthDate(birthDayInMilliSecond);
                    customerDetails.setPassword(edtPassword.getText().toString().trim());
                    customerDetails.setCountryId(countryModel.getCountryId());
                    customerDetails.setRegisterBy(customerDetails.getRegisterBy());
                    customerDetails.setSocialMediaUserId(customerDetails.getSocialMediaUserId());

                    registerUser.setCustomerDetails(customerDetails);

                    registerUser.callRegisterCustomerAPI(this, this);
                }
                break;

            case R.id.btnYes:
                CustomDialog.getInstance().hide();
                break;
        }
    }

    @Override
    public void OnSuccess(RequestCode requestCode) {

        switch (requestCode) {

            case REGISTER_CUSTOMER:
                Intent iToMainActivity = new Intent(this, MainActivity.class);
                iToMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(iToMainActivity);
                finish();
                break;
        }
    }

    @Override
    public void OnFailure(RequestCode requestCode, String error) {

        if (error.equals(getString(R.string.errorMsgInternetSlow)) || error.equals(getString(R.string.errorMsgInternetConnUnavailable))) {
            CustomDialog.getInstance().showAlert(this, error, false, getString(R.string.lblDismiss));
        } else {
            ToastHelper.getInstance(this).displayCustomToast(error);
        }
    }

    @Override
    public void onRetryRequest(RequestCode requestCode) {
        switch (requestCode) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CAMERA:

                if (resultCode == RESULT_OK && data != null) {
                    onCaptureImageResult(data);
                }

                break;

            case REQUEST_GALLERY:

                if (resultCode == RESULT_OK && data != null) {
                    onSelectFromGalleryResult(data);
                }

                break;

            case CROPPED_IMAGE_REQUEST:

                if (resultCode == RESULT_OK && data != null) {
                    afterCroppedImageResult(data);
                }

                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case PermissionClass.REQUEST_CODE_RUNTIME_PERMISSION_CAMERA:
                if (grantResults.length > 0) {
                    if (PermissionClass.verifyPermission(grantResults)) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, REQUEST_CAMERA);
                    } else if (((!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) ||
                            !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)))) {
                        showCustomSnackBar();
                    }
                }
                break;

            case PermissionClass.REQUEST_CODE_RUNTIME_PERMISSION_STORAGE:
                if (grantResults.length > 0) {
                    if (PermissionClass.verifyPermission(grantResults)) {

                        Intent intent = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType(Constants.GALLERY_FILE_TYPE);
                        startActivityForResult(Intent.createChooser(intent, getString(R.string.strSelectFile)), REQUEST_GALLERY);

                    } else if (((!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                            !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)))) {
                        showCustomSnackBar();
                    }
                }
                break;

        }
    }

    /**
     * This method check validation of required view component
     *
     * @return (boolean) : return either true or false, if all validation succeed than it return true
     */
    private boolean validateRegistration() {

        if (ValidationClass.isEmpty(edtFirstName.getText().toString().trim())) {
            displayErrorAndRequestFocus(edtFirstName, Util.getAppKeyValue(this, R.string.errorMsgFirstName));
            return false;

        } else if (!ValidationClass.matchPattern(edtFirstName.getText().toString().trim(), Constants.PATTERN_ALPHABET)) {
            displayErrorAndRequestFocus(edtFirstName, getString(R.string.errorMsgValidFirstName));
            return false;

        } else if (ValidationClass.isEmpty(edtLastName.getText().toString().trim())) {
            displayErrorAndRequestFocus(edtLastName, Util.getAppKeyValue(this, R.string.errorMsgLastName));
            return false;

        } else if (ValidationClass.isEmpty(edtEmail.getText().toString().trim())) {
            displayErrorAndRequestFocus(edtEmail, Util.getAppKeyValue(this, R.string.errorMsgEmailAddress));
            return false;

        } else if (!ValidationClass.matchPattern(edtEmail.getText().toString().trim(), Patterns.EMAIL_ADDRESS.pattern())) {
            displayErrorAndRequestFocus(edtEmail, Util.getAppKeyValue(this, R.string.errorMsgValidEmailAddress));
            return false;

        } else if (ValidationClass.isEmpty(edtPhoneNumber.getText().toString().trim())) {
            displayErrorAndRequestFocus(edtPhoneNumber, Util.getAppKeyValue(this, R.string.errorMsgPhoneNo));
            return false;

        } else if (!ValidationClass.checkPhoneNumber(edtPhoneNumber.getText().toString().trim(), Constants.PHONE_NUMBER_LENGTH)) {
            displayErrorAndRequestFocus(edtPhoneNumber, Util.getAppKeyValue(this, R.string.errorMsgValidPhoneNo));
            return false;

        } else if (ValidationClass.isEmpty(txtDateFormat.getText().toString().trim())) {
            ToastHelper.getInstance(this).displayCustomToast(Util.getAppKeyValue(this, R.string.errorMsgDOB));
            return false;

        }
        // check validation for password, confirm password and other required fields
        // when user register by app
        else if (customerDetails.getRegisterBy() == RegisterBy.APP.getType()) {

            if (ValidationClass.isEmpty(edtPassword.getText().toString().trim())) {
                displayErrorAndRequestFocus(edtPassword, Util.getAppKeyValue(this, R.string.errorMsgPwd));
                return false;

            } else if (!ValidationClass.checkMinLength(edtPassword.getText().toString().trim(), Constants.PASSWORD_LENGTH)) {
                displayErrorAndRequestFocus(edtPassword, Util.getAppKeyValue(this, R.string.errorMsgValidPwd));
                return false;

            } else if (ValidationClass.isEmpty(edtConfPassword.getText().toString().trim())) {
                displayErrorAndRequestFocus(edtConfPassword, getString(R.string.errorMsgPwd));
                return false;

            } else if (!ValidationClass.checkMinLength(edtConfPassword.getText().toString().trim(), Constants.PASSWORD_LENGTH)) {
                displayErrorAndRequestFocus(edtConfPassword, Util.getAppKeyValue(this, R.string.errorMsgValidPwd));
                return false;

            } else if (!ValidationClass.matchPattern(edtPassword.getText().toString().trim(), edtConfPassword.getText().toString().trim())) {
                ToastHelper.getInstance(this).displayCustomToast(Util.getAppKeyValue(this, R.string.errorMsgSamePwd));
                return false;

            } else if (ValidationClass.isEmpty(txtCountry.getText().toString().trim())) {
                ToastHelper.getInstance(this).displayCustomToast(Util.getAppKeyValue(this, R.string.errorMsgSelectCountry));
                return false;
            } else if (!cbTermsConditions.isChecked()) {
                ToastHelper.getInstance(this).displayCustomToast(Util.getAppKeyValue(this, R.string.errorMsgTermsAndConditions));
                return false;
            } else {
                Object object = txtCountry.getTag();

                if (object == null) {
                    countryModel = new CountryModel();
                } else {
                    countryModel = (CountryModel) object;
                }
                birthDayInMilliSecond = Long.parseLong(String.valueOf(txtDateFormat.getTag()));
                return true;
            }

        } else {
            // when user login via social media than we do not need to validate password and
            // confirm password.
            if (ValidationClass.isEmpty(txtCountry.getText().toString().trim())) {
                ToastHelper.getInstance(this).displayCustomToast(Util.getAppKeyValue(this, R.string.errorMsgSelectCountry));
                return false;
            } else if (!cbTermsConditions.isChecked()) {
                ToastHelper.getInstance(this).displayCustomToast(Util.getAppKeyValue(this, R.string.errorMsgTermsAndConditions));
                return false;
            } else {
                Object object = txtCountry.getTag();
                if (object == null) {
                    countryModel = new CountryModel();
                } else {
                    countryModel = (CountryModel) object;
                }
                birthDayInMilliSecond = Util.getDateInMillis(txtDateFormat.getText().toString().trim(), Constants.DATE_FORMAT);
                return true;
            }
        }
    }


    /**
     * This method display required validation error message and focus cursor on editText.
     *
     * @param editText     (EditText)   : EditText object
     * @param errorMessage (String) : errorMessage to be display
     */
    private void displayErrorAndRequestFocus(EditText editText, String errorMessage) {
        editText.requestFocus();
        ToastHelper.getInstance(this).displayCustomToast(errorMessage);
    }

    /**
     * it show popup with option to select an image
     */
    public void selectImage() {

        final CharSequence[] items = {getString(R.string.strTakePhoto), getString(R.string.strOpenGallery), getString(R.string.strCancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
        builder.setTitle(getString(R.string.strDialogTitle));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals(getString(R.string.strTakePhoto))) {

                    if (PermissionClass.checkPermission(activityContext, PermissionClass.REQUEST_CODE_RUNTIME_PERMISSION_CAMERA,
                            Arrays.asList(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE))) {

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, REQUEST_CAMERA);

                    }

                    //remaining process
                } else if (items[item].equals(getString(R.string.strOpenGallery))) {

                    if (PermissionClass.checkPermission(activityContext, PermissionClass.REQUEST_CODE_RUNTIME_PERMISSION_STORAGE,
                            Arrays.asList(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))) {

                        Intent intent = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType(Constants.GALLERY_FILE_TYPE);
                        startActivityForResult(Intent.createChooser(intent, getString(R.string.strSelectFile)), REQUEST_GALLERY);

                    }

                } else if (items[item].equals(getString(R.string.strCancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    /**
     * To manage camera image
     *
     * @param data (Intent) : data to be comes on onActivityResult
     */
    private void onCaptureImageResult(Intent data) {

        Bitmap thumbnail = (Bitmap) data.getExtras().get(getString(R.string.strData));
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                //currentTimeMillis is create the new image name
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Uri camimagepath = Uri.parse(destination.getAbsolutePath());
        // store camera image path into global variable "selectedImagePath"
        // selectedImagePath = camimagepath.toString();

        Log.d("cameraImgPath", camimagepath.toString());

        Intent intent = new Intent(getApplicationContext(), CropImageActivity.class);
        if (camimagepath.toString().length() > 0) {
            int[] screenWH = GraphicsUtil.getScreenWidthHeight();

            BitmapFactory.Options options1 = new BitmapFactory.Options();
            options1.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(camimagepath.toString(), options1);

            // Calculate inSampleSize
            options1.inSampleSize = GraphicsUtil.getInstance().calculateInSampleSize(options1, screenWH[0], (int) (screenWH[0] * 1.26));
            options1.inJustDecodeBounds = false;

            options1.inPreferredConfig = Bitmap.Config.ARGB_8888;

            Bitmap mBitmap = BitmapFactory.decodeFile(camimagepath.toString(), options1);
            Bitmap finalBitmap = null;

            ExifInterface ei = null;

            try {
                ei = new ExifInterface(camimagepath.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);


            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    finalBitmap = rotateImage(mBitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    finalBitmap = rotateImage(mBitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    finalBitmap = rotateImage(mBitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                    finalBitmap = mBitmap;
                    break;
                default:
                    finalBitmap = mBitmap;
                    break;
            }

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(camimagepath.toString(), options);
            int height = options.outHeight;
            int width = options.outWidth;


            if (finalBitmap != null) {
                String picPath = GraphicsUtil.getInstance().saveImage(finalBitmap, GraphicsUtil.CAPTURED_DIRECTORY_NAME);
                /*intent.putExtra(CropImageActivity.IMAGE_PATH, picPath);
                intent.putExtra(CropImageActivity.SHAPE, 3);
                if (width > 0 && height > 0) {
                    float ratio = height / width;
                    intent.putExtra(CropImageActivity.BITMAP_RATIO, 0);
                }*/
                /*intent.putExtra(CropImageActivity.SHAPE,1);
                intent.putExtra(CropImageActivity.IMAGE_RATIO, Constants.VALUE_IMAGE_RATIO);
                intent.putExtra(CropImageActivity.DIRECTORY_PATH, GraphicsUtil.CAPTURED_DIRECTORY_NAME);*/
                intent.putExtra(CropImageActivity.IMAGE_PATH, picPath);
                intent.putExtra(CropImageActivity.SHAPE, GraphicsUtil.SHAPE_CIRCLE);
                intent.putExtra(CropImageActivity.BITMAP_RATIO, (float) 0 / 0);
                intent.putExtra(CropImageActivity.IMAGE_RATIO, Constants.VALUE_IMAGE_RATIO);
                intent.putExtra(CropImageActivity.DIRECTORY_PATH, GraphicsUtil.CONTEST_DIRECTORY);
                startActivityForResult(intent, CROPPED_IMAGE_REQUEST);
            }

        }

    }

    /**
     * To manage data that selected from External Storage
     *
     * @param data (Intent) : data that to be comes on onActivityResult
     */
    private void onSelectFromGalleryResult(Intent data) {

        Uri selectedImage = data.getData();
        String[] path = {MediaStore.Images.Media.DATA};

        Cursor c = getContentResolver().query(selectedImage, path, null, null, null);
        c.moveToFirst();
        int columnindex = c.getColumnIndex(path[0]);
        String imagePath = c.getString(columnindex);
        c.close();

        // it will use when we want to get actual image resolution
        // Bitmap bitmap = (BitmapFactory.decodeFile(imagePath));

        // store camera image path into global variable "selectedImagePath"
        //  selectedImagePath = imagePath;

        int[] screenWH = GraphicsUtil.getScreenWidthHeight();

        Log.d("galleryImagePath", imagePath);

        BitmapFactory.Options options1 = new BitmapFactory.Options();
        options1.inJustDecodeBounds = true;
        options1.inSampleSize = GraphicsUtil.getInstance().calculateInSampleSize(options1, screenWH[0], (int) (screenWH[0] * 1.26));
        options1.inJustDecodeBounds = false;
        options1.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options1);


        Intent intent = new Intent(getApplicationContext(), CropImageActivity.class);
        if (imagePath.length() > 0) {
            String picPath = GraphicsUtil.getInstance().saveImage(bitmap, GraphicsUtil.CONTEST_DIRECTORY);
            intent.putExtra(CropImageActivity.IMAGE_PATH, picPath);
            intent.putExtra(CropImageActivity.SHAPE, GraphicsUtil.SHAPE_CIRCLE);
            intent.putExtra(CropImageActivity.BITMAP_RATIO, (float) 0 / 0);
            intent.putExtra(CropImageActivity.IMAGE_RATIO, Constants.VALUE_IMAGE_RATIO);
            intent.putExtra(CropImageActivity.DIRECTORY_PATH, GraphicsUtil.CONTEST_DIRECTORY);
            startActivityForResult(intent, CROPPED_IMAGE_REQUEST);
        }

    }

    /**
     * Final result will get from Crop Activity. In {@link CropImageActivity} user can modify the
     * image like crop, rotate etc.
     *
     * @param data (Intent) : data that to be comes on onActivityResult
     */
    public void afterCroppedImageResult(Intent data) {
        File cropFile = new File(data.getStringExtra(CropImageActivity.IMAGE_PATH));

        if (!TextUtils.isEmpty(cropFile.toString())) {

            if (cropFile.exists()) {
                selectedImagePath = cropFile.getAbsolutePath();

            }
        }

        Picasso.with(this)
                .load(new File(selectedImagePath))
                .placeholder(R.drawable.ic_missing_profile_dp)
                .resize(screenWH[0], (int) (screenWH[0] * Constants.VALUE_IMAGE_RATIO))
                .into(ivUserDp);

        Debug.trace("resize wh " + "screenWidth " + screenWH[0] + " " + "screenHeight " + screenWH[0] * Constants.VALUE_IMAGE_RATIO);
    }


    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    /**
     * To show snackbar when user permanently denied to accept run time permssion. It will open
     * Setting bar on pressing "OK" button option by this
     * {@link PermissionClass#openSettingBar(Activity)} method.
     */
    public void showCustomSnackBar() {
        snackbar = Snackbar.make(activity_registration, getString(R.string.strPermissionRequired), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.lblOk), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PermissionClass.openSettingBar(activityContext);
                    }
                });

        snackbar.show();
    }


    /**
     * This method set or display the required user data on UI as per requirement
     *
     * @param customerDetails (CustomerDetails) : model class object
     */
    private void setUserData(CustomerDetails customerDetails) {
        if (customerDetails.getRegisterBy() != RegisterBy.APP.getType()) {
            edtPassword.setVisibility(View.GONE);
            edtConfPassword.setVisibility(View.GONE);
        }

        if (!ValidationClass.isEmpty(customerDetails.getFirstName())) {
            edtFirstName.setText(customerDetails.getFirstName());
        }

        if (!ValidationClass.isEmpty(customerDetails.getLastName())) {
            edtLastName.setText(customerDetails.getLastName());
        }

        if (!ValidationClass.isEmpty(customerDetails.getEmail())) {
            edtEmail.setText(customerDetails.getEmail());
        }

        if (!ValidationClass.isEmpty(customerDetails.getPhone())) {
            edtPhoneNumber.setText(customerDetails.getPhone());
        }

        if (!ValidationClass.isEmpty(String.valueOf(customerDetails.getBirthDate())) && customerDetails.getBirthDate() != Constants.ZERO) {

            txtDateFormat.setText(Util.dateFormat(customerDetails.getBirthDate(), Constants.DATE_FORMAT));
        }


        if (!ValidationClass.isEmpty(String.valueOf(customerDetails.getCountryId())) && customerDetails.getCountryId() != Constants.ZERO) {

            List<CountryModel> countryList = CheckVersionModel.getCheckVersionModel().getCountryList();
            for (int i = 0; i < countryList.size(); i++) {
                if (customerDetails.getCountryId() == countryList.get(i).getCountryId()) {
                    selectedCountryName = countryList.get(i).getCountryName();
                    txtCountry.setText(selectedCountryName);
                    break;
                }
            }
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //TODO auto generated method or stub
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //TODO auto generated method or stub
        ToastHelper.getInstance(this).cancelToast();
    }

    @Override
    public void afterTextChanged(Editable s) {
        //TODO auto generated method or stub
    }


}
