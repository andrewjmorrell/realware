package com.pivot.pivot360.pivoteye;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.pivot.pivot360.pivotglass.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

/**
 * Activity that shows how to use the camera to take a picture on a HMT-1 device
 */
public class CameraActivity extends Activity {

    // Request code identifying camera events
    private static final int CAMERA_REQUEST_CODE = 1889;

    // Identifier for the image returned by the camera
    private static final String EXTRA_RESULT = "data";

    private ImageView mImageView;

    private Bitmap mPhoto;

    /**
     * Called when the activity is created
     *
     * @param savedInstanceState See Android docs
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.camera_main);

        mImageView = (ImageView) findViewById(R.id.camera_image_view);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    /**
     * Listener for when a the launch camera button is clicked
     *
     * @param view The launch camera button
     */
    public void onLaunchCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    public void onSavePicture(View view) {
        String filename = new Date().toString() + ".jpg";
        FileOutputStream outputStream;

        File outputFile = new File(new File("//mnt//sdcard//Download"),
                filename);

        try {
            outputStream = new FileOutputStream(outputFile);
            mPhoto.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        finish();
    }

    /**
     * Listener for result from external activities. Receives image data from camera.
     *
     * @param requestCode See Android docs
     * @param resultCode  See Android docs
     * @param data        See Android docs
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            mPhoto = data.getExtras().getParcelable(EXTRA_RESULT);
            mImageView.setImageBitmap(mPhoto);
        } else {
            finish();
        }
    }
}
