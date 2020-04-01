package com.example.svslsavemoneysavelife.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.svslsavemoneysavelife.R;
import com.example.svslsavemoneysavelife.models.User;
import com.example.svslsavemoneysavelife.utils.SharedData;
import com.example.svslsavemoneysavelife.utils.Utils;

public class RegisterActivity extends AppCompatActivity {

    private EditText name, phone, pass, confirmPass;
    private Button signup, lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        pass = findViewById(R.id.pass);
        confirmPass = findViewById(R.id.confirm_pass);
        signup = findViewById(R.id.signup);
        lang = findViewById(R.id.lang);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideKeyboard(RegisterActivity.this);
                if(validate()) {
                    User user = new User();
                    user.setName(name.getText().toString());
                    user.setPhone(phone.getText().toString());
                    user.setPass(pass.getText().toString());
                    SharedData.currentUser = user;
                    Intent intent = new Intent(RegisterActivity.this, OTPActivity.class);
                    intent.putExtra("from", 1);
                    intent.putExtra("phone", user.getPhone());
                    startActivity(intent);
                }

            }
        });

        lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change lang
            }
        });

    }

    private boolean validate() {
        boolean validData = true;
        if(!pass.getText().toString().equals(confirmPass.getText().toString())) {
            validData = false;
            confirmPass.setError("not match with password");
        }
        if(TextUtils.isEmpty(name.getText().toString())){
            validData = false;
            name.setError("required field!");
        }
        if(TextUtils.isEmpty(phone.getText().toString())){
            validData = false;
            phone.setError("required field!");
        }
        if(TextUtils.isEmpty(pass.getText().toString())){
            validData = false;
            pass.setError("required field!");
        }
        if(TextUtils.isEmpty(confirmPass.getText().toString())){
            validData = false;
            confirmPass.setError("required field!");
        }
        return validData;
    }
}
