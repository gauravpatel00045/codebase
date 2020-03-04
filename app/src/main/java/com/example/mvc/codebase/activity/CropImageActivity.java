package com.example.mvc.codebase.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.mvc.codebase.R;
import com.example.mvc.codebase.customView.CustomOverlay;
import com.example.mvc.codebase.customView.TouchImageView;
import com.example.mvc.codebase.graphicsUtils.GraphicsUtil;
import com.example.mvc.codebase.helper.PrefHelper;
import com.example.mvc.codebase.interfaces.ClickEvent;
import com.example.mvc.codebase.utils.Constants;
import com.example.mvc.codebase.utils.Debug;
import com.example.mvc.codebase.utils.GenericView;
import com.example.mvc.codebase.utils.Util;

import java.io.File;

/**
 * TODO stub is generated but developer or programmer need to add code as required
 */
public class CropImageActivity extends AppCompatActivity implements ClickEvent {

    // xml component declaration
    private FrameLayout frameLayout;
    private Button btnCancel;
    private Button btnDone;
    private ImageView ivZoomInst;
    private ImageView ivRotate;
    private RelativeLayout relParent;
    private ProgressBar pBarCropAct;

    // variable declaration
    /**
     * shape: 1 - circle, 2 - square, 3 - rectangle
     */
    private int shape = 1;
    private int radius = 100, length, breadth;
    private int durationInst = 3000; /* In milli second */
    private float aspectRatio = Constants.VALUE_IMAGE_RATIO;
    private float bitmapRatio;
    private int[] screenWH;
    private boolean rotate = true;

    private String directoryPath;
    public static final String IMAGE_PATH = "imagePath";
    public static final String SHAPE = "shape";
    public static final String IS_ROTATE = "isRotate";
    public static final String DIRECTORY_PATH = "directoryPath";
    public static final String IMAGE_RATIO = "imageRatio";
    public static final String BITMAP_RATIO = "bitmapRatio";

    // class object declaration
    private DisplayMetrics metrics = new DisplayMetrics();
    private Bitmap mBitmap = null;
    private TouchImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        init();

        screenWH = GraphicsUtil.getScreenWidthHeight();

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            String imagePath = bundle.getString(IMAGE_PATH);
            shape = bundle.getInt(SHAPE);
            bitmapRatio = bundle.getFloat(BITMAP_RATIO);
            if (bundle.containsKey(IS_ROTATE)) {
                rotate = bundle.getBoolean(IS_ROTATE);
            }
            aspectRatio = bundle.getFloat(IMAGE_RATIO);
            directoryPath = bundle.getString(DIRECTORY_PATH);
            image = new TouchImageView(this);

            if (!rotate) {
                ivRotate.setVisibility(View.GONE);
            } else {
                ivRotate.setVisibility(View.VISIBLE);
            }

            PrefHelper.getInstance().setBoolean(PrefHelper.KEY_ZOOM_INST, true);
            if (PrefHelper.getInstance().getBoolean(PrefHelper.KEY_ZOOM_INST, true)) {

                ivZoomInst.setVisibility(View.VISIBLE);
                PrefHelper.getInstance().setBoolean(PrefHelper.KEY_ZOOM_INST, false);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after #durationInst#ms
                        ivZoomInst.setVisibility(View.GONE);
                    }
                }, durationInst);
            } else {
                ivZoomInst.setVisibility(View.GONE);
            }

            frameLayout.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imagePath, options);

            // Calculate inSampleSize
            options.inSampleSize = GraphicsUtil.getInstance().calculateInSampleSize(options, screenWH[0], (int) (screenWH[0] * bitmapRatio));
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;

            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            mBitmap = BitmapFactory.decodeFile(new File(imagePath).getAbsolutePath(), options);
            /**
             * Crop image Resolution 1080x1360 1360/1080 = 1.26 Aspect ratio=
             * 1.26
             */
            int screenWH[] = GraphicsUtil.getScreenWidthHeight();

            switch (shape) {
                case GraphicsUtil.SHAPE_CIRCLE:
                    radius = screenWH[0] / 2;
                    break;
                case GraphicsUtil.SHAPE_SQUARE:
                    length = ((screenWH[0] - 40));
                    breadth = ((screenWH[0] - 40));
                    break;
                case GraphicsUtil.SHAPE_RECTANGLE:
                    length = screenWH[0];
                    breadth = (int) (length * aspectRatio);
                    //  image.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    break;
            }

            image.setLength(length);
            image.setBreadth(breadth);
            image.setRadius(radius);
            image.setShape(shape);
            image.setImageBitmap(mBitmap);


            CustomOverlay customOverlay = new CustomOverlay(this);
            switch (shape) {
                case GraphicsUtil.SHAPE_CIRCLE:
                    customOverlay.setRadius(radius);
                    customOverlay.setLength(0);
                    customOverlay.setBreadth(0);
                    break;
                case GraphicsUtil.SHAPE_SQUARE:
                    customOverlay.setRadius(0);
                    customOverlay.setLength(length);
                    customOverlay.setBreadth(breadth);
                    break;
                case GraphicsUtil.SHAPE_RECTANGLE:
                    customOverlay.setRadius(0);
                    customOverlay.setLength(length);
                    customOverlay.setBreadth(breadth);
                    break;
            }

            customOverlay.setShape(shape);
            customOverlay.setTemplateImage(imagePath);
            frameLayout.addView(image);
            frameLayout.addView(customOverlay);
        }

    }

    /**
     * Initialise view of xml component here
     * eg. textView, editText
     * and initialisation of required class objects
     */
    public void init() {

        relParent = GenericView.findViewById(this, R.id.activity_crop_image);

        ivRotate = GenericView.findViewById(this, R.id.iv_rotate);

        btnDone = GenericView.findViewById(this, R.id.btn_done);
        btnCancel = GenericView.findViewById(this, R.id.btn_cancel);
        ivZoomInst = GenericView.findViewById(this, R.id.iv_zoomInst);
        frameLayout = GenericView.findViewById(this, R.id.frame_lay);
        pBarCropAct = GenericView.findViewById(this, R.id.pb_cropAct);
    }

    @Override
    public void onClickEvent(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                Util.clickEffect(view);
                finish();
                break;
            case R.id.iv_rotate:
                try {
                    Bitmap mBitmap1 = ((BitmapDrawable) image.getDrawable()).getBitmap();
                    mBitmap = GraphicsUtil.makeRotate(mBitmap1, 90);
                    image.setImageBitmap(mBitmap);
                    mBitmap1.recycle();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_done:

                image.setDrawingCacheEnabled(true);
                mBitmap = Bitmap.createScaledBitmap(image.getDrawingCache(), image.getWidth(), image.getHeight(), true);
                image.setDrawingCacheEnabled(false); // clear drawing cache
                processBitmapImage(mBitmap);

                break;
        }
    }

    private void processBitmapImage(Bitmap bitmap) {

        Debug.trace("SHAPE:" + shape);
        int width;
        int height;
        int x;
        int y;

        switch (shape) {

            case GraphicsUtil.SHAPE_CIRCLE:
                mBitmap = bitmap;

                width = image.getWidth();
                height = image.getHeight();
                x = width / 2 - (radius);
                y = height / 2 - (radius);

                if (mBitmap != null) {
                    Bitmap finalBitmap = getCroppedCircleImage(x, y, radius * 2, radius * 2);
                    String picPath = GraphicsUtil.getInstance().saveImage(finalBitmap, directoryPath);
                    finalBitmap.recycle();
                    Intent intent = new Intent();
                    intent.putExtra(IMAGE_PATH, picPath);
                    setResult(RESULT_OK, intent);
                    finish();
                    // saveBitmap(finalBitmap);
                }
                break;

            case GraphicsUtil.SHAPE_SQUARE:
                mBitmap = bitmap;
                width = image.getWidth();
                height = image.getHeight();
                x = width / 2 - (length / 2);
                y = height / 2 - (length / 2);
                if (mBitmap != null) {
                    Bitmap finalBitmap = getCroppedImage(x, y, length, length);

                    String picPath = GraphicsUtil.getInstance().saveImage(finalBitmap, directoryPath);
                    finalBitmap.recycle();
                    Intent intent = new Intent();
                    intent.putExtra(IMAGE_PATH, picPath);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Log.d("System out", "original");
                }
                break;

            case GraphicsUtil.SHAPE_RECTANGLE:
                mBitmap = bitmap;
                width = image.getWidth();
                height = image.getHeight();
                x = width / 2 - (length / 2);
                y = height / 2 - (breadth / 2);

                if (mBitmap != null) {
                    Bitmap finalBitmap = getCroppedImage(x, y, length, breadth);

                    String picPath = GraphicsUtil.getInstance().saveImage(finalBitmap, directoryPath);
                    finalBitmap.recycle();
                    Intent intent = new Intent();
                    intent.putExtra(IMAGE_PATH, picPath);
                    setResult(RESULT_OK, intent);
                    finish();

                } else {
                    Log.d("System out", "original");
                }
                break;
        }
    }

    public Bitmap getCroppedImage(int x, int y, int width, int height) {
        // Crop the subset from the original Bitmap.
        final Bitmap croppedBitmap = Bitmap.createBitmap(mBitmap, x, y, width, height);
        return croppedBitmap;
    }

    /**
     * Gets the cropped circle image based on the current crop selection.
     *
     * @return a new Circular Bitmap representing the cropped image
     */
    public Bitmap getCroppedCircleImage(int x, int y, int width, int height) {
        Bitmap bitmap = getCroppedImage(x, y, width, height);
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        // Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        // return _bmp;
        return output;
    }
}
