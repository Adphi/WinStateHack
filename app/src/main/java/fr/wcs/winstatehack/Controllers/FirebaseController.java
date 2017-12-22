package fr.wcs.winstatehack.Controllers;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import fr.wcs.winstatehack.Models.FireModel;
import fr.wcs.winstatehack.Models.UserModel;

import static fr.wcs.winstatehack.Utils.Constants.FIRE_ENTRY;
import static fr.wcs.winstatehack.Utils.Constants.USERS_ENTRY;

public class FirebaseController {
    private static FirebaseController sInstance;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mAmplitudeRef = null;
    private DatabaseReference mUsersReference = null;
    private FirebaseAmplitudeListener mAmplitudeListener = null;
    private UsersListener mOnUsersChangedListener = null;
    private String mUserUid = "";

    private ArrayList<UserModel> mUsers = new ArrayList<>();
    private UserModel mUser = null;
    private FireModel mFire = null;
    private UserReadyListener mUserReadyListener = null;

    private FirebaseController() {
        mDatabase = FirebaseDatabase.getInstance();

        mUsersReference = mDatabase.getReference(USERS_ENTRY);
    }

    public static FirebaseController getInstance() {
        if (sInstance == null) {
            sInstance = new FirebaseController();
        }
        return sInstance;
    }

    public void setUserUid(String uid) {
        mUserUid = uid;
        initUser();
    }

    private void initAmplitudeListener() {
        mAmplitudeRef = mDatabase.getReference(FIRE_ENTRY);
        mAmplitudeRef.addValueEventListener(mAmlitudeListener);
    }

    private void initUser() {
        mUsersReference.child(mUserUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(UserModel.class);
                if(mUserReadyListener != null) {
                    mUserReadyListener.onUserReady(mUser);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mUsersReference.addChildEventListener(mUsersListener);
    }

    public UserModel getUser(){
        return mUser;
    }

    public void setUserReadyListener(UserReadyListener listener) {
        mUserReadyListener = listener;
    }

    public interface UserReadyListener {
        void onUserReady(UserModel user);
    }

    public void createUser(UserModel user) {
        mUser = user;
        mUser.setUid(mUserUid);
        mUsersReference.child(mUserUid).setValue(mUser);
        initUser();
    }

    public void createFire() {
        initAmplitudeListener();
        // TODO: Create Fire on Firebase
    }

    public void joinFire(FireModel fire) {
        // TODO
        initAmplitudeListener();
    }
    
    private ValueEventListener mAmlitudeListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            FireModel fire = dataSnapshot.getValue(FireModel.class);
            int amp = 0;
            if(!fire.getUsers().isEmpty()) {
                for (int a : fire.getUsers().values()) {
                    amp += a;
                }
            }

            if(mAmplitudeListener != null) {
                mAmplitudeListener.onAmplitudeChanged(amp);
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
        mAmplitudeRef.child(USERS_ENTRY).child(mUser.getUid()).setValue(amp);
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
            if(mOnUsersChangedListener != null) {
                mOnUsersChangedListener.onUsersChanged(user);
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            UserModel updatedUser = dataSnapshot.getValue(UserModel.class);
            UserModel user = findUser(updatedUser);
            mUsers.remove(user);
            mUsers.add(updatedUser);
            if(mOnUsersChangedListener != null) {
                mOnUsersChangedListener.onUsersChanged(updatedUser);
            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            UserModel user = dataSnapshot.getValue(UserModel.class);
            mUsers.remove(user);
            if(mOnUsersChangedListener != null) {
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
