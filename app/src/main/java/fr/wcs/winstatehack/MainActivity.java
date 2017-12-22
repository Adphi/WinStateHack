package fr.wcs.winstatehack;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import fr.wcs.winstatehack.Controllers.FirebaseController;
import fr.wcs.winstatehack.Controllers.SoundMeterController;
import fr.wcs.winstatehack.Models.UserModel;
import fr.wcs.winstatehack.Utils.Utils;

public class MainActivity extends AppCompatActivity {

    private final String TAG = Utils.getTAG(this);

    FirebaseAuth mAuth;
    FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private TextView mTextViewUserName;
    private ImageButton mImageButtonFire;

    // Requesting permission to RECORD_AUDIO
    private boolean mPermissionToRecordAccepted = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private String [] permissions = {android.Manifest.permission.RECORD_AUDIO};
    private UserModel mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextViewUserName = findViewById(R.id.textViewName);
        mImageButtonFire = findViewById(R.id.imageButtonFire);
        mImageButtonFire.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));

        FirebaseController firebaseController = FirebaseController.getInstance();
        mUser = firebaseController.getUser();
        if(mUser != null) {
            mTextViewUserName.setText(mUser.getName());
        }
        else {
            firebaseController.setUserReadyListener(u -> {
                mUser = u;
                mTextViewUserName.setText(mUser.getName());
            });
        }

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        Log.d(TAG, "onCreate: Audio Permissions: " + mPermissionToRecordAccepted);

        mImageButtonFire = findViewById(R.id.imageButtonFire);
        mImageButtonFire.setOnClickListener(v -> {
            startActivity(new Intent(this, FireActivity.class));
        });

        Button buttonFriends = findViewById(R.id.buttonMessage);
        buttonFriends.setOnClickListener(v -> startActivity(new Intent(this, FriendsActivity.class)));

        ImageButton buttonShake = findViewById(R.id.buttonShake);
        buttonShake.setOnClickListener(v -> startActivity(new Intent(this, SensorActivity.class)));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                mPermissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                Log.d(TAG, "onRequestPermissionsResult: Permission Granted");
                SoundMeterController.permissionGranted = mPermissionToRecordAccepted;
                break;
        }
        if (!mPermissionToRecordAccepted) finish();

    }
}
