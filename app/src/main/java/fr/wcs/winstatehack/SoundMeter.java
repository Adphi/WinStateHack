package fr.wcs.winstatehack;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

/**
 * Created by adphi on 21/12/17.
 */

public class SoundMeter {

    private AudioRecord ar = null;
    private int minSize;
    private boolean mIsPlaying = false;

    public SoundMeter() {

    }

    public void start() {
        minSize= AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        ar = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000,AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,minSize);
        ar.startRecording();
        mIsPlaying = true;
    }

    public void stop() {
        if (ar != null) {
            ar.stop();
            mIsPlaying = false;
        }
    }

    public double getAmplitude() {
        short[] buffer = new short[minSize];
        ar.read(buffer, 0, minSize);
        int max = 0;
        for (short s : buffer)
        {
            if (Math.abs(s) > max)
            {
                max = Math.abs(s);
            }
        }
        return max;
    }

    public boolean isPlaying() {
        return mIsPlaying;
    }
}
