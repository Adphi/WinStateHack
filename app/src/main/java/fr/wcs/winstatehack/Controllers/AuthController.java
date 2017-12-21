package fr.wcs.winstatehack.Controllers;

/**
 * Created by adphi on 21/12/17.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Patterns;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import fr.wcs.winstatehack.R;

/**
 * Created by adphi on 15/11/17.
 */

public class AuthController {

    private final String TAG = this.getClass().getSimpleName();

    private static volatile AuthController sInstance = null;

    // Listeners
    private AuthListener mAuthListener = null;
    private PhoneConfirmationListener mPhoneConfirmationListener = null;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseAuth.AuthStateListener mFirebaseAuthListener;
    private OnCompleteListener<AuthResult> mFirebaseAuthCompleteListener;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private GoogleSignInClient mGoogleSignInClient;

    private String mEmail;
    private String mPassword;
    private String mConfirmPassword;

    /**
     * Private Constructor of the UserController AuthController
     */
    private AuthController(Context context){
        // Prevent from the reflection API.
        if(sInstance != null) {
            throw new RuntimeException("Use getInstance() to get the single instance of this class.");
        }

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);

        // Firebase Auth Listener
        mFirebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in UID:" + mUser.getUid());
                    Log.d(TAG, "onAuthStateChanged:signed_in Phone Number:" + mUser.getPhoneNumber());
                    mUser = mAuth.getCurrentUser();
                    if(mUser.getPhoneNumber() == null || mUser.getPhoneNumber().isEmpty()) {
                        // TODO: Remove debug code
                        //connectionSuccess(true);
                        connectionSuccess(false);
                    }
                    else {
                        connectionSuccess(false);
                    }

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    connectionClose();
                }
            }
        };

        // Firebase Auth Completed Listener
        mFirebaseAuthCompleteListener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success");
                    mUser = mAuth.getCurrentUser();
                    if(mUser.getPhoneNumber() == null || mUser.getPhoneNumber().isEmpty()) {
                        // TODO: remove debug code
                        connectionSuccess(false);
                    }
                    else {
                        connectionSuccess(false);
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    connectionFailure(task.getException().getMessage().toString());
                }
            }
        };
    }

    /**
     * Method to call to get the AuthController Instance
     *
     * @return the AuthController Instance
     */
    public static AuthController getInstance(Context context){
        // Check for the first time
        if(sInstance == null) {
            // Check for the second time
            synchronized (AuthController.class) {
                // If no Instance available create new One
                if(sInstance == null) {
                    sInstance = new AuthController(context);
                }
            }
        }
        return sInstance;
    }

    public static AuthController getInstance() {
        if(sInstance == null) {
            throw new RuntimeException("The first call to getInstance() should contains the activity");
        }
        else {
            return sInstance;
        }
    }

    /**
     * Method return the Firebase User
     *
     * @return the user
     */
    public FirebaseUser getUser() {
        return mUser;
    }

    /**
     * Handles the Authentication to Firebase with mEmail and mPassword.
     *
     * @param eMail
     * @param password
     */
    public void getAuthWithEmail(String eMail, String password) {
        mAuth.signInWithEmailAndPassword(eMail, password).
                addOnCompleteListener(mFirebaseAuthCompleteListener);
    }

    /**
     * Method to call in order to get the Google's User to give to getAuthWithGoogle in the onActivityResult
     * corresponding to the Constants.RC_SIGN_IN (1)
     */
    public Intent getUserWithGoogle() {
        // Start Google Sign In Intent
        return mGoogleSignInClient.getSignInIntent();
    }

    /**
     * Method handling Firebase User creation with mEmail and mPassword.
     *
     * @param email
     * @param password
     * @param confirmPassword
     */
    public void createUserWithMail(String email, String password, String confirmPassword) {
        // Bad Formatted mail
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            connectionFailure("Email badly formatted.");
        }
        // Bad Password confirmation
        else if(!password.equals(confirmPassword)) {
            connectionFailure("Password and confirmation are different.");
        }
        // Too short Password
        else if(password.length() < 6) {
            connectionFailure("Too Short password.");
        }
        // Everything is ok, so let's try to register
        else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(mFirebaseAuthCompleteListener);
        }
    }

    /**
     * Handles the Authentication with Google
     *
     * @param data
     */
    public void getAuthWithGoogle(Intent data){
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = task.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            // Google Sign In failed
            Log.w(TAG, "Google sign in failed", e);
            connectionFailure(e.getMessage());
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(mFirebaseAuthCompleteListener);
    }

    /**
     * Method handling Firebase Account Phone Confirmation
     *
     * @param phoneNumber
     * @param activity
     */
    public void verifyPhoneNumber(String phoneNumber, final Activity activity) {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verificaiton without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

                linkPhoneNumber(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                phoneConfirmationFailed(e.getMessage());

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

            }
        };

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                activity,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    /**
     * Internal Method to link Phone to User account
     *
     * @param credential
     */
    private void linkPhoneNumber(AuthCredential credential) {
        mUser.linkWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "linkWithCredential:success");
                            mUser = task.getResult().getUser();
                            phoneConfirmationSuccess();
                        } else {
                            Log.w(TAG, "linkWithCredential:failure", task.getException());
                            phoneConfirmationFailed(task.getException().getMessage());
                        }
                    }
                });
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public void setConfirmPassword(String confirmPassword) {
        mConfirmPassword = confirmPassword;
    }

    public void register(Activity activity){
        createUserWithMail(mEmail,mPassword,mConfirmPassword);
    }

    /**
     * Method Signing the User out
     */
    public void signOut(){
        mAuth.signOut();
        mGoogleSignInClient.signOut();
        connectionClose();
    }

    /**
     * Set the Auth Listener (I'm not kidding)
     * @param listener
     */
    public void setAuthListener(AuthListener listener) {
        mAuthListener = listener;
    }

    /**
     * Remove the Auth Listener
     */
    public void removeAuthListener() {
        mAuthListener = null;
    }

    /**
     * Listener for Authentication Status changes
     */
    public interface AuthListener {
        void onSuccess(boolean phoneConfirmationNeeded);
        void onFailure(String error);
        void onSignOut();
    }

    public void setPhoneConfirmationListener(PhoneConfirmationListener listener) {
        mPhoneConfirmationListener = listener;
    }

    public interface PhoneConfirmationListener {
        void onPhoneConfirmationSuccess();
        void onPhoneConfirmationFailure(String error);
    }

    /**
     * Attach the Controller to the Firebase Auth Listener
     * The AuthController is typically attached in the Activity onStart method
     */
    public void attach() {
        mAuth.addAuthStateListener(mFirebaseAuthListener);
    }

    /**
     * Detach the Controller to the Firebase Auth Listener
     * the AuthController is typically detached in the Activity onStop method
     */
    public void detach(){
        mAuth.removeAuthStateListener(mFirebaseAuthListener);
    }

    private void connectionSuccess(boolean phoneConfirmationNeeded){
        if(mAuthListener != null) {
            mAuthListener.onSuccess(phoneConfirmationNeeded);
        }
    }

    private void connectionFailure(String error) {
        if(mAuthListener != null) {
            mAuthListener.onFailure(error);
        }
    }

    private void connectionClose() {
        if(mAuthListener != null) {
            mAuthListener.onSignOut();
        }
    }

    private void phoneConfirmationSuccess(){
        if(mPhoneConfirmationListener != null) {
            mPhoneConfirmationListener.onPhoneConfirmationSuccess();
        }
    }

    private void phoneConfirmationFailed(String error) {
        if(mPhoneConfirmationListener != null) {
            mPhoneConfirmationListener.onPhoneConfirmationFailure(error);
        }
    }

    public static class AuthBuilder {
        private static AuthBuilder sInstance;
        private String mEmail = "";
        private String mPassword = "";
        private String mConfirmPassword = "";


        private AuthBuilder() {
        }

        public static AuthBuilder getInstance() {
            if (sInstance == null) {
                sInstance = new AuthBuilder();
            }
            return sInstance;
        }

        public AuthBuilder email(String email) {
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                throw new RuntimeException("Email badly formatted.");
            }
            else {
                mEmail = email;
                return this;
            }
        }

        public AuthBuilder password(String password) {
            if(password.length() < 6) {
                throw new RuntimeException("Password should be at least 6 characters.");
            }
            else {
                mPassword = password;
                return this;
            }
        }

        public AuthBuilder confirmPassword(String confirmPassword) {
            if(!confirmPassword.equals(mPassword)) {
                throw new RuntimeException("Password and confirmation are different.");
            }
            else {
                mConfirmPassword = confirmPassword;
                return this;
            }
        }

        public void build(Activity activity) {
            if(mEmail.isEmpty() || mPassword.isEmpty() || mConfirmPassword.isEmpty()) {
                throw new RuntimeException("Email, password and confirm password must be filled.");
            }
            else {
                AuthController authController = AuthController.getInstance(activity);
                authController.createUserWithMail(mEmail, mPassword, mConfirmPassword);
            }
        }
    }

}