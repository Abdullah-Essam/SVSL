package com.example.svslsavemoneysavelife.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
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
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    private EditText phone, pass;
    private Button login, forgetPassword, signup, lang;

    private String loginPhone, loginPassword;
    private boolean isSaved = false;
    private SharedPreferences sharedPref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPref = this.getSharedPreferences(SharedData.PREF_KEY, Context.MODE_PRIVATE);
        isSaved = sharedPref.getBoolean(SharedData.IS_USER_SAVED, false);
        if(isSaved) {
            loginPhone = sharedPref.getString(SharedData.PHONE, "");
            loginPassword = sharedPref.getString(SharedData.PASS, "");
            login();
        }

        phone = findViewById(R.id.phone);
        pass = findViewById(R.id.pass);
        login = findViewById(R.id.login);
        forgetPassword = findViewById(R.id.forget_pass);
        signup = findViewById(R.id.signup);
        lang = findViewById(R.id.lang);

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideKeyboard(LoginActivity.this);
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideKeyboard(LoginActivity.this);
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()) {
                    loginPhone = phone.getText().toString();
                    loginPassword = pass.getText().toString();
                    login();
                }
            }
        });

        lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change lang
                Locale current = getCurrentLocale();
                if (current.getLanguage().equals("ar")) {
                    Resources res = getResources();
                    DisplayMetrics dm = res.getDisplayMetrics();
                    android.content.res.Configuration conf = res.getConfiguration();
                    conf.setLocale(new Locale("en".toLowerCase()));
                    res.updateConfiguration(conf, dm);

                    Intent i = getBaseContext().getPackageManager().
                            getLaunchIntentForPackage(getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    finish();
                    startActivity(i);
                } else if (current.getLanguage().equals("en")) {
                    Resources res = getResources();
                    DisplayMetrics dm = res.getDisplayMetrics();
                    android.content.res.Configuration conf = res.getConfiguration();
                    conf.setLocale(new Locale("ar".toLowerCase()));
                    res.updateConfiguration(conf, dm);

                    Intent i = getBaseContext().getPackageManager().
                            getLaunchIntentForPackage(getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    finish();
                    startActivity(i);
                }
            }
        });
    }

    private void login() {
        Utils.hideKeyboard(LoginActivity.this);
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle(getString(R.string.logging));
        progressDialog.setCancelable(false);
        progressDialog.show();

        new UserController().checkLogin(loginPhone, loginPassword, new UserCallback() {
            @Override
            public void onSuccess(ArrayList<User> users) {
                if(users.size() > 0) {
                    SharedData.currentUser = users.get(0);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean(SharedData.IS_USER_SAVED, true);
                    editor.putString(SharedData.PHONE, loginPhone);
                    editor.putString(SharedData.PASS, loginPassword);
                    editor.apply();

                    progressDialog.dismiss();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this,
                            R.string.login_error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFail(String error) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }


    private boolean validate() {
        boolean validData = true;
        if(TextUtils.isEmpty(phone.getText().toString())){
            validData = false;
            phone.setError(getString(R.string.required));
        }
        if(TextUtils.isEmpty(pass.getText().toString())){
            validData = false;
            pass.setError(getString(R.string.required));
        }
        return validData;
    }

    private Locale getCurrentLocale(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return this.getResources().getConfiguration().getLocales().get(0);
        } else{
            //noinspection deprecation
            return this.getResources().getConfiguration().locale;
        }
    }
}
