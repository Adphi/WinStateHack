package fr.wcs.winstatehack;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseUser;

import fr.wcs.winstatehack.Controllers.FirebaseController;

public class loginActivity extends AppCompatActivity {
    private EditText mEditTextUserName;
    private Button mButtonConfirmUserName;
    private String mUserName;
    private FirebaseController mUser = FirebaseController.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEditTextUserName = findViewById(R.id.editTextLoginUser);
        mButtonConfirmUserName = findViewById(R.id.buttonLoginConfirm);
        mButtonConfirmUserName.setEnabled(false);

        mEditTextUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mButtonConfirmUserName.setEnabled(true);
                mUserName = mEditTextUserName.getText().toString();
            }
        });

    }
}
