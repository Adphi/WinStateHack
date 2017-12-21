package fr.wcs.winstatehack.Controllers;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import fr.wcs.winstatehack.SoundMeter;
import fr.wcs.winstatehack.Utils.Utils;

/**
 * Created by adphi on 21/12/17.
 */

public class SoundMeterController {

    public static boolean permissionGranted = false;

    private static SoundMeterController sInstance;
    private SoundMeter mSoundMeter;
    private ArrayList<Integer> mLevelTable;
    private SoundMeterListener mMeterListener = null;
    private FirebaseController mFirebaseController;
    private Timer mTimer;

    private SoundMeterController() {
        mLevelTable = Utils.getLogTable();
        mSoundMeter = new SoundMeter();
        mFirebaseController = FirebaseController.getInstance();
        mTimer = new Timer();
    }

    public void start() {
        mSoundMeter.start();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int amp = (int) (mSoundMeter.getAmplitude() / 32767f * 100);
                if(mMeterListener != null) {
                    mMeterListener.onMeterChanged(amp);
                }
                mFirebaseController.setAmplitudeValue(amp);

            }
        }, 0, 250);
    }

    public void stop() {
        mSoundMeter.stop();

    }

    public void setSoundMeterListener(SoundMeterListener listener) {
        this.mMeterListener = listener;
    }

    public interface SoundMeterListener {
        void onMeterChanged(int amp);
    }

    public static SoundMeterController getInstance() {
        if (sInstance == null) {
            sInstance = new SoundMeterController();
        }
        return sInstance;
    }
}
