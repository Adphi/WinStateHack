package fr.wcs.winstatehack;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import fr.wcs.winstatehack.Controllers.FirebaseController;
import fr.wcs.winstatehack.Models.UserModel;


public class loginActivity extends AppCompatActivity {
    private EditText mEditTextUserName;
    private Button mButtonConfirmUserName;
    private String mUserName;
    private FirebaseController mUser = FirebaseController.getInstance();
    private UserModel mUserModel = new UserModel();

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
                mUserName = mEditTextUserName.getText().toString().trim();
                checkName();
            }
        });

        mButtonConfirmUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserModel.setName(mUserName);
                mUser.createUser(mUserModel);
                Intent intent = new Intent(loginActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

    }
    private void checkName(){
        if (!mUserName.isEmpty()){
            mButtonConfirmUserName.setBackground(this.getResources().getDrawable(R.drawable.login_button));
            mButtonConfirmUserName.setEnabled(true);
        }
    }
}
