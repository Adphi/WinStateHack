package fr.wcs.winstatehack;

import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import fr.wcs.winstatehack.Controllers.FirebaseController;
import fr.wcs.winstatehack.Controllers.SoundMeterController;
import fr.wcs.winstatehack.Utils.Utils;

public class TestActivity extends AppCompatActivity {

    private final String TAG = Utils.getTAG(this);

    // Requesting permission to RECORD_AUDIO
    private boolean mPermissionToRecordAccepted = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private static final int GRAIN_SIZE = 150;

    private String [] permissions = {android.Manifest.permission.RECORD_AUDIO};
    private SoundMeterController mSoundMeterController = null;
    private TextView mTextView;
    private ProgressBar mProgressBar;
    private FirebaseController mFirebaseController;
    private ProgressBar mFirebaseProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        Log.d(TAG, "onCreate: Audio Permissions: " + mPermissionToRecordAccepted);
        mTextView = findViewById(R.id.textView);
        mProgressBar = findViewById(R.id.progressBar);
        mFirebaseProgressBar = findViewById(R.id.progressBar2);
        mFirebaseController = FirebaseController.getInstance();
    }

    private void init() {
        mSoundMeterController = SoundMeterController.getInstance();
        mSoundMeterController.setSoundMeterListener(amp -> runOnUiThread(() -> {
            mTextView.setText(String.valueOf(amp));
            ObjectAnimator.ofInt(mProgressBar, "progress", amp)
                    .setDuration(GRAIN_SIZE)
                    .start();
        }));
        mFirebaseController.setFirebaseAmplitudeListener(amp -> runOnUiThread(() -> {
            mFirebaseProgressBar.setProgress(amp);
        }));
        mSoundMeterController.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mPermissionToRecordAccepted && mSoundMeterController != null){
            mSoundMeterController.start();
        }
        else if(mPermissionToRecordAccepted) {
            mSoundMeterController.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSoundMeterController.stop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                mPermissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                Log.d(TAG, "onRequestPermissionsResult: Permission Granted");
                init();
                break;
        }
        if (!mPermissionToRecordAccepted) finish();

    }
}
