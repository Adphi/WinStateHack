package fr.wcs.winstatehack;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SensorActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager mSenSensorManager;
    private Sensor mSenAccelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;
    private int i = 50;
    private TextView mTextViewNbShakes;
    private TextView mTextViewShake;
    private ImageView mImageViewCork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        mSenSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSenAccelerometer = mSenSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSenSensorManager.registerListener(this, mSenAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

        mTextViewNbShakes = findViewById(R.id.textViewNbShakes);
        mTextViewShake = findViewById(R.id.textViewShake);
        mImageViewCork = findViewById(R.id.imageViewCork);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    if (i>0) {
                        i--;
                        mTextViewNbShakes.setText(String.valueOf(i));
                    } else {
                        mTextViewShake.setVisibility(View.INVISIBLE);
                        mTextViewNbShakes.setVisibility(View.INVISIBLE);
                        mImageViewCork.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "Win Envoyé !", Toast.LENGTH_SHORT).show();
                    }
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    protected void onPause() {
        super.onPause();
        mSenSensorManager.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();
        mSenSensorManager.registerListener(this, mSenAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
}
