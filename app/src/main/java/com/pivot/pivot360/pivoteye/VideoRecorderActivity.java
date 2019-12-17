package com.pivot.pivot360.pivoteye;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.pivot.pivot360.pivotglass.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Activity that shows how to use the camera to take a picture on a HMT-1 device
 */
public class VideoRecorderActivity extends Activity {

    // Request code identifying camera events
    private static final int VIDEO_REQUEST_CODE = 1890;

    // Identifier for the image returned by the camera
    private static final String EXTRA_RESULT = "data";

    private ImageView mImageView;

    private Uri mResult;

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
        setContentView(R.layout.video_main);

        mImageView = (ImageView) findViewById(R.id.video_image_view);

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, VIDEO_REQUEST_CODE);
    }

    /**
     * Listener for when a the launch camera button is clicked
     *
     * @param view The launch camera button
     */
    public void onLaunchCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, VIDEO_REQUEST_CODE);
    }

    public void onSaveVideo(View view) {

        FileOutputStream outputStream;

        String filename = mResult.toString().substring( mResult.toString().lastIndexOf('/')+1, mResult.toString().length() );

        File outputFile = new File(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator),
                filename);

        try {
            File source = new File(mResult.getPath());
            FileChannel src = new FileInputStream(source).getChannel();
            FileChannel dst = new FileOutputStream(outputFile).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        finish();
    }

    public void onPreview(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(mResult, "video/mp4");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
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
            mResult = data.getData();
            //mResult = data.getExtras().getParcelable(EXTRA_RESULT);
            Log.d("TAG", "!!!!!!!!!!mresult="+mResult.toString());

            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();

            mediaMetadataRetriever.setDataSource(this, mResult);
            Bitmap bmFrame = mediaMetadataRetriever.getFrameAtTime(0); //unit in microsecond
            mImageView.setImageBitmap(bmFrame);
        } else {
            finish();
        }
    }
}
