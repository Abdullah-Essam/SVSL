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
import com.example.svslsavemoneysavelife.callback.UserCallback;
import com.example.svslsavemoneysavelife.controller.UserController;
import com.example.svslsavemoneysavelife.models.User;
import com.example.svslsavemoneysavelife.utils.SharedData;
import com.example.svslsavemoneysavelife.utils.Utils;

import java.util.ArrayList;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText pass, confirmPass;
    private Button save, lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        pass = findViewById(R.id.pass);
        confirmPass = findViewById(R.id.confirm_pass);

        save = findViewById(R.id.save);
        lang = findViewById(R.id.lang);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideKeyboard(ResetPasswordActivity.this);
                final ProgressDialog progressDialog = new ProgressDialog(ResetPasswordActivity.this);
                progressDialog.setTitle("saving...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                if(validate()) {
                    SharedData.currentUser.setPass(pass.getText().toString());
                    new UserController().save(SharedData.currentUser, new UserCallback() {
                        @Override
                        public void onSuccess(ArrayList<User> users) {
                            progressDialog.dismiss();
                            SharedData.currentUser = users.get(0);
                            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }

                        @Override
                        public void onFail(String error) {
                            progressDialog.dismiss();
                            Toast.makeText(ResetPasswordActivity.this, error, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    private boolean validate() {
        boolean validData = true;
        if(!pass.getText().toString().equals(confirmPass.getText().toString())) {
            validData = false;
            confirmPass.setError("not match with password");
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
