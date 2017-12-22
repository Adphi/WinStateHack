package fr.wcs.winstatehack;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import fr.wcs.winstatehack.Controllers.FirebaseController;
import fr.wcs.winstatehack.Controllers.SoundMeterController;
import fr.wcs.winstatehack.Utils.Utils;

public class FireActivity extends AppCompatActivity {
    private final String TAG = Utils.getTAG(this);

    private ImageView mFlameGrand;
    private ImageView mFlameMoyen;
    private ImageView mFlamePetit;
    private ImageView mBuche;

    private static final int GRAIN_SIZE = 150;

    private SoundMeterController mSoundMeterController = null;
    private FirebaseController mFirebaseController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire);

        mFirebaseController = FirebaseController.getInstance();

        mFlameGrand = findViewById(R.id.flamegrand);
        mFlameGrand.getDrawable().setColorFilter(getResources().getColor(android.R.color.holo_orange_light),PorterDuff.Mode.MULTIPLY);
        mFlameGrand.setAlpha(0f);
        mFlameMoyen = findViewById(R.id.flameMoyen);
        mFlameMoyen.getDrawable().setColorFilter(getResources().getColor(android.R.color.holo_orange_light),PorterDuff.Mode.MULTIPLY);
        mFlameMoyen.setAlpha(0f);
        mFlamePetit = findViewById(R.id.flamePetit);
        mFlamePetit.getDrawable().setColorFilter(getResources().getColor(android.R.color.holo_red_light),PorterDuff.Mode.MULTIPLY);
        mFlamePetit.setAlpha(0f);
        mBuche = findViewById(R.id.buche);
        animateImageView(mFlameGrand, android.R.color.holo_orange_light,1000, 200);
        animateImageView(mFlamePetit, android.R.color.holo_red_dark, 1000, 400);
        animateImageView(mFlameMoyen, android.R.color.holo_orange_dark, 1000, 600);

        init();
    }


    public void animateImageView(final ImageView v,@ColorRes int color, int duration, int delay) {
        final int orange = getResources().getColor(color);

        final ValueAnimator colorAnim = ObjectAnimator.ofFloat(0f, 1f);
        colorAnim.addUpdateListener(animation -> {
            float mul = (Float) animation.getAnimatedValue();
            int alphaOrange = adjustAlpha(orange, mul);
            v.setColorFilter(alphaOrange, PorterDuff.Mode.SRC_ATOP);
            if (mul == 0.0) {
                v.setColorFilter(null);
            }
        });

        colorAnim.setDuration(duration);
        colorAnim.setStartDelay(delay);
        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        colorAnim.setRepeatCount(-1);
        colorAnim.start();

    }

    public int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    private void init() {
        // TODO: remove (direct sound from phone used for testing)
        mSoundMeterController = SoundMeterController.getInstance();
        // Real Value to display
        mFirebaseController.setFirebaseAmplitudeListener(amp -> runOnUiThread(() -> {
            // TODO: display Animation
            Log.d(TAG, "init: " + amp);
            float alpha = (float) amp / 100;
            float alpha1 = 0;
            float alpha2 = 0;
            float alpha3 = 0;
            if(alpha > 0 && alpha <= 1) {
                alpha1 = alpha;
            }
            else if(alpha > 1 && alpha <= 2) {
                alpha1 = 1;
                alpha2 = alpha - 1;
            }
            else if(alpha > 2 && alpha <= 3) {
                alpha1 = 1;
                alpha2 = 1;
                alpha3 = alpha - 2;
            }
            else if (alpha > 3) {
                alpha1 = 1;
                alpha2 = 1;
                alpha3 = 1;
            }

            ObjectAnimator.ofFloat(mFlamePetit, "alpha", alpha1)
                    .setDuration(GRAIN_SIZE)
                    .start();
            ObjectAnimator.ofFloat(mFlameMoyen, "alpha", alpha2)
                    .setDuration(GRAIN_SIZE)
                    .start();
            ObjectAnimator.ofFloat(mFlameGrand, "alpha", alpha3)
                    .setDuration(GRAIN_SIZE)
                    .start();
        }));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseController.getInstance().joinFire(null);
        mSoundMeterController.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSoundMeterController.stop();
    }
}
