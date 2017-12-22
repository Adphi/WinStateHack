package fr.wcs.winstatehack;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextViewUserName = findViewById(R.id.textViewName);
        mImageButtonFire = findViewById(R.id.imageButtonFire);
        mImageButtonFire.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));

        Intent intent = getIntent();
        String name = intent.getStringExtra("username");

        DatabaseReference userRef = mFirebaseDatabase.getReference("users");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    UserModel user = userSnapshot.getValue(UserModel.class);
                    if (user.getName().equals(name)){
                        mTextViewUserName.setText(user.getName());
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInAnonymously:success ");
                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.d(TAG, "User Uid: " + user.getUid());
                    } else {
                        // If sign in fails, display a message to the user.

                    }

                    // ...
                });

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        Log.d(TAG, "onCreate: Audio Permissions: " + mPermissionToRecordAccepted);

        mImageButtonFire = findViewById(R.id.imageButtonFire);
        mImageButtonFire.setOnClickListener(v -> {
            startActivity(new Intent(this, FireActivity.class));
        });

        Button buttonFriends = findViewById(R.id.buttonMessage);
        buttonFriends.setOnClickListener(v -> startActivity(new Intent(this, FriendsActivity.class)));
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

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
}
