package com.pivot.pivot360.pivoteye;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.pivot.pivot360.pivotglass.R;

import java.io.File;
import java.io.IOException;

public class DocumentViewerActivity extends Activity {

    //private final String mSampleFileName = "thermal4.jpg";
    //private final String mSampleFileName = "note_aug_29_2019.pdf";
    private final String mSampleFileName = "analyzer_test.pdf";
    private final String mSampleFolderName = "Pivot";
    //private final String mSampleMimeType = "image/jpeg";
    private final String mSampleMimeType = "application/pdf";

    private File mSampleFile;

    private static final int DOCUMENT_REQUEST_CODE = 1890;

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
        setContentView(R.layout.document_main);

        try {
            mSampleFile = Utils.copyFromAssetsToExternal(this, mSampleFileName, mSampleFolderName);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setDataAndType(Uri.fromFile(mSampleFile), mSampleMimeType);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            //
            // Optionally can control visual appearance
            //
            intent.putExtra("page", "1"); // Open a specific page
            intent.putExtra("zoom", "1"); // Open at a specific zoom level

            startActivityForResult(intent, DOCUMENT_REQUEST_CODE);
        } catch (IOException ex) {
            Toast.makeText(this, "Failed to copy sample file", Toast.LENGTH_LONG).show();

            finish();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        finish();
    }

    /**
     * Listener for when a the launch document viewer button is clicked
     *
     * @param view The launch launch document viewer button
     */
    public void onLaunchDocument(View view) {
        if (mSampleFile == null) {
            Toast.makeText(
                    getApplicationContext(),
                    "Failed to find sample file",
                    Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(Uri.fromFile(mSampleFile), mSampleMimeType);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //
        // Optionally can control visual appearance
        //
        intent.putExtra("page", "1"); // Open a specific page
        intent.putExtra("zoom", "3"); // Open at a specific zoom level

        startActivity(intent);
    }
}
