package fr.wcs.winstatehack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import fr.wcs.winstatehack.Controllers.FirebaseController;
import fr.wcs.winstatehack.Utils.Utils;

import static fr.wcs.winstatehack.Utils.Constants.PREFERENCES;
import static fr.wcs.winstatehack.Utils.Constants.UID;

public class SplashScreen extends AppCompatActivity {
    private final String TAG = Utils.getTAG(this);

    private static int SPLASH_TIME_OUT = 3000;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SharedPreferences sharedPreferences = this.getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        String uid = sharedPreferences.getString(UID, "");

        new Handler().postDelayed(() -> {
            Intent intent;
            if(uid.isEmpty()) {
                intent = new Intent(SplashScreen.this, loginActivity.class);
            }
            else {
                FirebaseController.getInstance().setUserUid(uid);
                intent = new Intent(this, MainActivity.class);
            }
            startActivity(intent);
            finish();
        }, SPLASH_TIME_OUT);

        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInAnonymously:success ");
                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.d(TAG, "User Uid: " + user.getUid());
                        if(uid.isEmpty()) {
                            sharedPreferences.edit().putString(UID, user.getUid()).commit();
                            FirebaseController.getInstance().setUserUid(user.getUid());
                        }
                    } else {
                        // If sign in fails, display a message to the user.

                    }

                    // ...
                });
    }
}
