package com.example.mvc.codebase.graphicsUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.example.mvc.codebase.MyApplication;
import com.example.mvc.codebase.utils.Debug;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * TODO stub is generated but developer or programmer need to add code as required
 */

public class GraphicsUtil {

    // variable declaration
    public static final int SHAPE_CIRCLE = 1;
    public static final int SHAPE_SQUARE = 2;
    public static final int SHAPE_RECTANGLE = 3;

    public static final String STORAGE_DIRECTORY = Environment.getExternalStorageDirectory() + File.separator + "Codebase";
    public static final String CONTEST_DIRECTORY = STORAGE_DIRECTORY + File.separator + "Images";
    public static final String DOWNLOAD_DIRECTORY = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "prep_images";
    public static final String CAPTURED_DIRECTORY_NAME = STORAGE_DIRECTORY + File.separator + "Camera";

    // class object declaration
    private static GraphicsUtil instance;


    // constructor
    private GraphicsUtil() {

    }

    /**
     * @return instance (GraphicsUtil) : it return class instance
     */
    public static GraphicsUtil getInstance() {
        if (instance == null) {
            instance = new GraphicsUtil();
        }
        return instance;
    }


    /**
     * To calculate screen width and height.
     *
     * @param options   : To get screen width and height
     * @param reqWidth  : Required screen width
     * @param reqHeight : Required screen height
     */
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    /**
     * To save image with specified name and root directory
     *
     * @param finalBitmap (Bitmap) : bitmap that to be saved in External storage
     * @param rootDir     (String) : directory path
     */
    public String saveImage(Bitmap finalBitmap, String rootDir) {
        String timeStamp, imageName;
        File mediaStorageDir;
        if (!TextUtils.isEmpty(rootDir)) {
            mediaStorageDir = new File(rootDir);
        } else {
            mediaStorageDir = new File(rootDir, "temp");
        }

        // make directory in External storage
        mediaStorageDir.mkdirs();


        if (rootDir.equalsIgnoreCase(CONTEST_DIRECTORY)) {
            imageName = "IMG_" + System.currentTimeMillis() + ".jpg";
        } else {
            // define required image name
            imageName = "IMG_contest.jpg";

        }

        File file = new File(mediaStorageDir, imageName);
        if (file.exists()) {
            file.delete();
        }
        try {

            if (finalBitmap != null) {
                FileOutputStream out = new FileOutputStream(file);
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file.getPath();
    }

    /**
     * Convert Bitmap to ByteArray
     *
     * @param bitmap : bitmap that to be saved in ByteArray
     */
    public byte[] bitmapToByteArray(Bitmap bitmap) {
        try {

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Method that accept bitmap, apply rotation on bitmap.
     *
     * @param src    : source bitmap.
     * @param degree : at degree bitmap is rotated e.g. 90,180.
     * @return Bitmap : final processed bitmap.
     */
    public static Bitmap makeRotate(Bitmap src, float degree) {

        // create new matrix
        Matrix matrix = new Matrix();
        // setup rotation degree
        matrix.postRotate(degree);

        // return new bitmap rotated using matrix
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    /**
     * To get device screen width and height
     *
     * @return widthAndHeight (int[]) : it return screen width and height in an array.
     */
    public static int[] getScreenWidthHeight() {
        WindowManager wm = (WindowManager) MyApplication.getInstance().getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);

        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        int[] widthAndHeight = {screenWidth, screenHeight};

        return widthAndHeight;
    }
    /**
     * it convert gallery or camera image in bitmap from it's reference
     * <br><strong> Note: </strong> selectedImagePath should be like this</br>
     * <pre>{@code
     * selectedImagePath = "/storage/emulated/0/1483959236587.jpg" // camera image path
     * selectedImagePath = "/storage/sdcard/Download/ic_abbacus.png" // gallery image path
     * }
     *
     * </pre>
     *
     * @param selectedImagePath (String) : gallery or camera image destination path
     */
    public static Bitmap convertImageInBitmap(String selectedImagePath) {
        Bitmap thumbnail;
        thumbnail = BitmapFactory.decodeFile(selectedImagePath);
        return thumbnail;
    }


}
