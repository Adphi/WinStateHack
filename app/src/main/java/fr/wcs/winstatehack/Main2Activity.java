package fr.wcs.winstatehack;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class Main2Activity extends AppCompatActivity {
    private ImageView mFlameGrand;
    private ImageView mFlameMoyen;
    private ImageView mFlamePetit;
    private ImageView mBuche;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mFlameGrand = findViewById(R.id.flamegrand);
        mFlameGrand.getDrawable().setColorFilter(getResources().getColor(android.R.color.holo_orange_light),PorterDuff.Mode.MULTIPLY);
        mFlameMoyen = findViewById(R.id.flameMoyen);
        mFlameGrand.getDrawable().setColorFilter(getResources().getColor(android.R.color.holo_orange_light),PorterDuff.Mode.MULTIPLY);
        mFlamePetit = findViewById(R.id.flamePetit);
        mFlameGrand.getDrawable().setColorFilter(getResources().getColor(android.R.color.holo_red_light),PorterDuff.Mode.MULTIPLY);
        mBuche = findViewById(R.id.buche);
        animateImageView(mFlameGrand, android.R.color.holo_orange_light,1000, 200);
        animateImageView(mFlamePetit, android.R.color.holo_red_dark, 1000, 400);
        animateImageView(mFlameMoyen, android.R.color.holo_orange_dark, 1000, 600);
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
}
