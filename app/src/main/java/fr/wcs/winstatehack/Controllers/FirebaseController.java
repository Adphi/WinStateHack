package fr.wcs.winstatehack.Controllers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import fr.wcs.winstatehack.Models.FireModel;
import fr.wcs.winstatehack.Models.UserModel;

import static fr.wcs.winstatehack.Utils.Constants.AMPLITUDE_ENTRY;
import static fr.wcs.winstatehack.Utils.Constants.USERS_ENTRY;

/**
 * Created by adphi on 21/12/17.
 */

public class FirebaseController {
    private static FirebaseController sInstance;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mAmplitudeRef = null;
    private DatabaseReference mUsersReference = null;
    private FirebaseAmplitudeListener mAmplitudeListener = null;
    private int mAmplitudeValue = 0;
    private UsersListener mOnUsersChangedListener = null;

    private ArrayList<UserModel> mUsers = new ArrayList<>();
    private UserModel mUser = null;
    private FireModel mFire = null;

    private FirebaseController() {
        mDatabase = FirebaseDatabase.getInstance();
        mDatabase.setPersistenceEnabled(true);

        mUsersReference = mDatabase.getReference(USERS_ENTRY);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null) {
            return;
        }

        mUsersReference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(UserModel.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static FirebaseController getInstance() {
        if (sInstance == null) {
            sInstance = new FirebaseController();
        }
        return sInstance;
    }

    private void initAmplitudeListener() {
        mAmplitudeRef = mDatabase.getReference(USERS_ENTRY).child(mUser.getUid()).child(AMPLITUDE_ENTRY);
        mAmplitudeRef.addValueEventListener(mAmlitudeListener);
    }

    public void createUser(UserModel user) {
        mUser = user;
        String uid = mUsersReference.push().getKey();
        mUser.setUid(uid);
        mUsersReference.child(uid).setValue(uid);

    }

    public void createFire(FireModel fire) {

    }
    
    private ValueEventListener mAmlitudeListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mAmplitudeValue = dataSnapshot.getValue(Integer.class);
            if(mAmplitudeListener != null) {
                mAmplitudeListener.onAmplitudeChanged(mAmplitudeValue);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public ArrayList<UserModel> getUsers() {
        return mUsers;
    }

    void setAmplitudeValue(int amp) {
        if(mAmplitudeRef != null)
        mAmplitudeRef.setValue(amp);
    }

    public void setFirebaseAmplitudeListener(FirebaseAmplitudeListener listener) {
        mAmplitudeListener = listener;
    }

    public interface FirebaseAmplitudeListener {
        void onAmplitudeChanged(int amp);
    }

    public void setUsersListener(UsersListener listener) {
        mOnUsersChangedListener = listener;
    }

    public interface UsersListener {
        void onUsersChanged(UserModel user);
    }

    private ChildEventListener mUsersListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            UserModel user = dataSnapshot.getValue(UserModel.class);
            mUsers.add(user);
            if(mUsersListener != null) {
                mOnUsersChangedListener.onUsersChanged(user);
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            UserModel updatedUser = dataSnapshot.getValue(UserModel.class);
            UserModel user = findUser(updatedUser);
            mUsers.remove(user);
            mUsers.add(updatedUser);
            if(mUsersListener != null) {
                mOnUsersChangedListener.onUsersChanged(updatedUser);
            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            UserModel user = dataSnapshot.getValue(UserModel.class);
            mUsers.remove(user);
            if(mUsersListener != null) {
                mOnUsersChangedListener.onUsersChanged(user);
            }
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private UserModel findUser(UserModel user) {
        for(UserModel u : mUsers) {
            if(u.getUid().equals(user.getUid())) {
                return u;
            }
        }
        return null;
    }
}
