/**
 * RealWear Development Software, Source Code and Object Code
 * (c) RealWear, Inc. All rights reserved.
 * <p>
 * Contact info@realwear.com for further information about the use of this code.
 */

package com.pivot.pivot360.pivoteye;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.pivot.pivot360.pivotglass.R;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Activity that shows how capture audio at different frequencies on a HMT-1 device
 */
public class AudioCaptureActivity extends Activity implements Runnable {
    private final static String TAG = "HMT1DevApp-Audio";

    private final int SAMPLE_LENGTH_SECONDS = 10;

    private Button mRecordButton;
    private Button mPlaybackButton;
    private Button mStopButton;
    private Button mSaveButton;
    private ProgressBar mProgressBar;
    CountDownTimer mPlaybackAudioTimer;
    private TextView mFilenameLabel;

    private RadioButton mMonoButton;
    private RadioButton mStereoButton;

    private RadioButton mRate8Button;
    private RadioButton mRate16Button;
    private RadioButton mRate44Button;
    private RadioButton mRate48Button;

    private int mSampleRate;
    private String mSampleRatedString;
    private short mChannels;
    private String mChannelsString;
    private String mFilename;
    private String mTempFilename;

    private AudioRecord mAudioRecorder;

    private Thread mMotor;

    private boolean mRecording = false;

    private ByteArrayOutputStream mAudio;

    private int mBitsPerSecond;

    private AudioTrack mAudioTrack;

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
        setContentView(R.layout.audio_capture_main);

        mRecordButton = (Button) findViewById(R.id.recordButton);
        mStopButton = (Button) findViewById(R.id.stopButton);
        mSaveButton = (Button) findViewById(R.id.saveButton);
        mPlaybackButton = (Button) findViewById(R.id.playbackButton);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mFilenameLabel = (TextView) findViewById(R.id.fileTextLabel);

        mMonoButton = (RadioButton) findViewById(R.id.monoButton);
        mStereoButton = (RadioButton) findViewById(R.id.stereoButton);

        mRate8Button = (RadioButton) findViewById(R.id.rate8Button);
        mRate16Button = (RadioButton) findViewById(R.id.rate16Button);
        mRate44Button = (RadioButton) findViewById(R.id.rate44Button);
        mRate48Button = (RadioButton) findViewById(R.id.rate48Button);

        mAudio = new ByteArrayOutputStream();
    }

    /**
     * Called when activity is resumed - See Android docs
     */
    @Override
    protected void onResume() {
        super.onResume();

        mRecordButton.setEnabled(true);
        mPlaybackButton.setEnabled(true);
        mProgressBar.setVisibility(View.INVISIBLE);
        mFilenameLabel.setText("");
        mFilename = "";

        mSaveButton.setVisibility(View.INVISIBLE);
        mPlaybackButton.setVisibility(View.INVISIBLE);

        setButtonState(true);
        mRate44Button.performClick();
        mStereoButton.performClick();
    }

    /**
     * Called when activity is paused - See Android docs
     */
    @Override
    protected void onPause() {
        super.onPause();

        if (mMotor != null && mAudioRecorder != null) {
            mAudioRecorder.stop();
            mAudioRecorder.release();
            mAudioRecorder = null;

            try {
                mMotor.join();
            } catch (Exception ex) {
                Log.e(TAG, "Failed to stop thread", ex);
            }
        }

        mMotor = null;

        if (mPlaybackAudioTimer != null) {
            mPlaybackAudioTimer.cancel();
        }
    }

    public void onSave(View view) {
        String date = new Date().toString();
        date = date.replaceAll("\\s+", "_");
        mFilename = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator +
                date +".wav";
        writeWAVFile(mFilename, mAudio, mBitsPerSecond);

        new File(mTempFilename).delete();

        finish();

    }

    /**
     * Called when the start record button is pressed
     *
     * @param view The start record button
     */
    public void onStartRecord(View view) {
        mRecordButton.setVisibility(View.INVISIBLE);
        mStopButton.setVisibility(View.VISIBLE);
        mSaveButton.setVisibility(View.INVISIBLE);
        mPlaybackButton.setVisibility(View.INVISIBLE);
//        mFilename = mFilenameLabel.getText().toString();
//        if (mFilename.isEmpty()) {
//            Toast.makeText(this, "No file to record to", Toast.LENGTH_LONG).show();
//            return;
//        }

        int monoOrStereo = mChannels == 1 ?
                AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO;

        mAudioRecorder = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                mSampleRate,
                monoOrStereo,
                AudioFormat.ENCODING_PCM_16BIT,
                4096);

        if (mAudioRecorder.getState() == AudioRecord.STATE_UNINITIALIZED) {
            Toast.makeText(this, "Failed to initialize AudioRecorder", Toast.LENGTH_LONG).show();
            return;
        }

        mProgressBar.setProgress(0);
        mProgressBar.setVisibility(View.VISIBLE);
        setButtonState(false);

        mRecording = true;

        mBitsPerSecond = mAudioRecorder.getAudioFormat() == AudioFormat.ENCODING_PCM_16BIT ?
                16 : 8;

        mAudioRecorder.startRecording();

        mMotor = new Thread(this);
        mMotor.setName("Recording Thread");
        mMotor.setPriority(Thread.MIN_PRIORITY);
        mMotor.start();
    }

    public void onStopRecord(View view) {
        mRecording = false;
    }

    /**
     * Called when the start playback button is pressed
     *
     * @param view The start playback button
     */
    public void onStartPlayback(View view) {
        //mFilename = mFilenameLabel.getText().toString();
        if (mTempFilename.isEmpty()) {
            Toast.makeText(this, "No file to play back", Toast.LENGTH_LONG).show();
            return;
        }

        String base64EncodedString = Base64.encodeToString(mAudio.toByteArray(), Base64.DEFAULT);
        String url = "data:audio/amr;base64,"+base64EncodedString;
        try {
            final MediaPlayer mp = new MediaPlayer();
            mp.setDataSource(mTempFilename);
            mp.setDataSource(url);
            mp.prepare();
            mp.start();

//            int monoOrStereo = mChannels == 1 ?
//                AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO;
//            mAudioTrack = new AudioTrack(
//                    AudioManager.STREAM_MUSIC,
//                    mSampleRate,
//                    AudioFormat.ENCODING_PCM_16BIT,
//                    mBitsPerSecond,
//                    4096,    //buffer length in bytes
//                    AudioTrack.MODE_STATIC);
//            mAudioTrack.write(mAudio.toByteArray(), 0, mAudio.toByteArray().length);
//            mAudioTrack.setNotificationMarkerPosition(mAudio.toByteArray().length);
//            mAudioTrack.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
//                @Override
//                public void onMarkerReached(AudioTrack audioTrack) {
//
//                }
//
//                @Override
//                public void onPeriodicNotification(AudioTrack audioTrack) {
//                }
//            });
//            mAudioTrack.play();

            setButtonState(false);
            mProgressBar.setProgress(0);
            mProgressBar.setVisibility(View.VISIBLE);

            mPlaybackAudioTimer =
                    new CountDownTimer(mp.getDuration(), TimeUnit.SECONDS.toMillis(1)) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            float duration = mp.getDuration();
                            float msPlayed = duration - (int) millisUntilFinished;

                            float progress = msPlayed / duration * mProgressBar.getMax();
                            mProgressBar.setProgress((int) progress);
                        }

                        @Override
                        public void onFinish() {
                            setButtonState(true);
                            mProgressBar.setVisibility(View.INVISIBLE);
                        }
                    }.start();
        } catch (IOException ex) {
            Toast.makeText(this, "Failed to play back file", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Playback error: ", ex);
            ex.printStackTrace();
        }
    }

    /**
     * Thread used to record audio and update the progress bar
     */
    public void run() {

        //ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte buffer[] = new byte[4096 * 2];

        final long startTime = System.currentTimeMillis();
        float seconds = 0;

        while (mRecording &&
                mAudioRecorder != null &&
                mAudioRecorder.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
            int bytesRead = mAudioRecorder.read(buffer, 0, buffer.length);
            if (bytesRead > 0) {
                mAudio.write(buffer, 0, bytesRead);
            }

            seconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startTime);
            updateProgressBar(seconds);
        }

        if (mAudioRecorder != null) {
            mAudioRecorder.stop();
            mAudioRecorder.release();
            mAudioRecorder = null;

            String date = new Date().toString();
            date = date.replaceAll("\\s+", "_");
            mTempFilename =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + date + ".wav";
            writeWAVFile(mTempFilename, mAudio, mBitsPerSecond);
        }

        runOnUiThread(new Runnable() {
            public void run() {
                mProgressBar.setVisibility(View.INVISIBLE);
                setButtonState(true);

                mRecordButton.setVisibility(View.VISIBLE);
                mStopButton.setVisibility(View.INVISIBLE);

                mSaveButton.setVisibility(View.VISIBLE);
                mPlaybackButton.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Update the progress bar based on the number of seconds that have been recorded
     *
     * @param seconds The number of seconds recorded
     */
    private void updateProgressBar(final float seconds) {
        runOnUiThread(new Runnable() {
            public void run() {
                float progress = seconds / SAMPLE_LENGTH_SECONDS * mProgressBar.getMax();
                mProgressBar.setProgress((int) progress);
            }
        });
    }

    /**
     * Called when the user changes the number of channels
     *
     * @param view The selected number of channels
     */
    public void onChannelsChanged(View view) {
        if (view.equals(mMonoButton)) {
            mChannels = 1;
            mChannelsString = "mono";
        }
        if (view.equals(mStereoButton)) {
            mChannels = 2;
            mChannelsString = "stereo";
        }

        updateFileName();
    }


    /**
     * Called when the user changes the sample rate
     *
     * @param view The selected sample rate
     */
    public void onSampleRateChanged(View view) {
        if (view.equals(mRate8Button)) {
            mSampleRate = 8000;
            mSampleRatedString = "8KHz";
        }

        if (view.equals(mRate16Button)) {
            mSampleRate = 16000;
            mSampleRatedString = "16KHz";
        }
        if (view.equals(mRate44Button)) {
            mSampleRate = 44100;
            mSampleRatedString = "44KHz";
        }
        if (view.equals(mRate48Button)) {
            mSampleRate = 48000;
            mSampleRatedString = "48KHz";
        }

        updateFileName();
    }

    /**
     * Updated the filename based on the selected settings
     */
    private void updateFileName() {
        String date = new Date().toString();
        date = date.replaceAll("\\s+", "_");
        mFilename =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + date + "_" + mSampleRatedString + "_" + mChannelsString + ".wav";
        mFilenameLabel.setText(mFilename);
    }

    /**
     * Enable or disable the on screen options
     *
     * @param isEnabled true to enable the options, false to disable them
     */
    private void setButtonState(boolean isEnabled) {
        mRate8Button.setEnabled(isEnabled);
        mRate16Button.setEnabled(isEnabled);
        mRate44Button.setEnabled(isEnabled);
        mRate48Button.setEnabled(isEnabled);

        mMonoButton.setEnabled(isEnabled);
        mStereoButton.setEnabled(isEnabled);

        mPlaybackButton.setEnabled(isEnabled);
        mRecordButton.setEnabled(isEnabled);
    }

    /**
     * Write the recorded audio data to a wav file
     *
     * @param filename     The path to the file to create
     * @param outputStream The recorded audio data
     */
    private void writeWAVFile(
            String filename, ByteArrayOutputStream outputStream, int bitsPerSamples) {

        byte[] audioData = outputStream.toByteArray();

        try {
            FileOutputStream fos = new FileOutputStream(filename);
            DataOutputStream dos = new DataOutputStream(fos);

            writeWaveFileHeader(dos, audioData.length, bitsPerSamples);

            fos.write(audioData);
            fos.flush();
            fos.close();
        } catch (final FileNotFoundException ex) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(AudioCaptureActivity.this, "Error creating wav file", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Error creating wav file: ", ex);
                }
            });
        } catch (final IOException ex) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(AudioCaptureActivity.this, "Error writing wav file", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Error writing wav file: ", ex);
                }
            });
        }

        File file = new File(filename);
        rescanFolder(file.getParent());
    }

    /**
     * Write wav file header information to a file
     *
     * @param dos           The stream to write the header information to
     * @param audioLen      The number of bytes of audio data that will be written
     * @param bitsPerSample The number of bits per sample
     * @throws IOException if an error occurs while writing to the output stream
     */
    private void writeWaveFileHeader(DataOutputStream dos, int audioLen, int bitsPerSample)
            throws IOException {
        final int wavHeaderSize = 44;
        final int totalDataLen = audioLen + wavHeaderSize - 8 /* Ignore chunk marker and size */;
        final short waveFormatPcm = 0x1;
        final int byteSize = 8;

        //
        // Riff chunk marker and size
        //
        dos.writeBytes("RIFF");
        dos.write(intToByteArray(totalDataLen));
        dos.writeBytes("WAVE");

        //
        // Format chunk marker and size
        //
        dos.writeBytes("fmt ");
        dos.write(intToByteArray(16)); // WAVE file type has 16 bits of format data

        //
        // Format data (16 bits)
        //
        dos.write(shortToByteArray(waveFormatPcm));
        dos.write(shortToByteArray(mChannels));
        dos.write(intToByteArray(mSampleRate));

        int blockAlign = mChannels * bitsPerSample / byteSize;
        int avgBytesPerSec = mSampleRate * blockAlign;

        dos.write(intToByteArray(avgBytesPerSec));
        dos.write(shortToByteArray(new Integer(blockAlign).shortValue()));
        dos.write(shortToByteArray(new Integer(bitsPerSample).shortValue()));

        //
        // Data chunk marker and size
        //
        dos.writeBytes("data");
        dos.write(intToByteArray(audioLen));
    }

    /**
     * Scan through the output folder to ensure Android finds the new files
     *
     * @param dest The output folder path
     */
    private void rescanFolder(String dest) {
        File[] files = new File(dest).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                // Scan files only (not folders)
                return pathname.isFile();
            }
        });

        String[] paths = new String[files.length];
        for (int co = 0; co < files.length; co++) {
            paths[co] = files[co].getAbsolutePath();
        }

        MediaScannerConnection.scanFile(this, paths, null, null);

        files = new File(dest).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                // and now recursively scan subfolders
                return pathname.isDirectory();
            }
        });

        for (File file : files) {
            rescanFolder(file.getAbsolutePath());
        }
    }

    /**
     * Convert an int to a byte array
     *
     * @param data The int to convert
     * @return Byte array containing the int data
     */
    private static byte[] intToByteArray(int data) {
        return new byte[]{
                (byte) (data & 0xff),
                (byte) ((data >> 8) & 0xff),
                (byte) ((data >> 16) & 0xff),
                (byte) ((data >> 24) & 0xff)
        };
    }

    /**
     * Convert a short to a byte array
     *
     * @param data the short to convert
     * @return Byte array containing the short data
     */
    private static byte[] shortToByteArray(short data) {
        return new byte[]{(byte) (data & 0xff), (byte) ((data >> 8) & 0xff)};
    }
}
