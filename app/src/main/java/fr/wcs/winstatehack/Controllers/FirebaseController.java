package fr.wcs.winstatehack.Controllers;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static fr.wcs.winstatehack.Utils.Constants.AMPLITUDE_ENTRY;

/**
 * Created by adphi on 21/12/17.
 */

public class FirebaseController {
    private static FirebaseController sInstance;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mAmplitudeRef;
    private FirebaseAmplitudeListener mListener = null;
    private int mAmplitudeValue = 0;


    private FirebaseController() {
        mDatabase = FirebaseDatabase.getInstance();
        mDatabase.setPersistenceEnabled(true);
        mAmplitudeRef = mDatabase.getReference(AMPLITUDE_ENTRY);
        mAmplitudeRef.addValueEventListener(mAmlitudeListener);
    }

    public static FirebaseController getInstance() {
        if (sInstance == null) {
            sInstance = new FirebaseController();
        }
        return sInstance;
    }

    private ValueEventListener mAmlitudeListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mAmplitudeValue = dataSnapshot.getValue(Integer.class);
            if(mListener != null) {
                mListener.onAmplitudeChanged(mAmplitudeValue);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    void setAmplitudeValue(int amp) {
        mAmplitudeRef.setValue(amp);
    }

    public void setFirebaseAmplitudeListener(FirebaseAmplitudeListener listener) {
        mListener = listener;
    }

    public interface FirebaseAmplitudeListener {
        void onAmplitudeChanged(int amp);
    }
}
