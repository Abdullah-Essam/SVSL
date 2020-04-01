package com.example.svslsavemoneysavelife.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {

    private EditText otp;
    private Button check, lang;

    private FirebaseAuth mAuth;

    private String mVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private static final String TAG = "PhoneAuthActivity";

    private int from = 0;
    private String phone = "";

    private SharedPreferences sharedPref;
    private static final String IS_USER_SAVED = "SAVED_USER";
    private static final String PHONE = "PHONE";
    private static final String PASS = "PASS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        sharedPref = this.getSharedPreferences("login", Context.MODE_PRIVATE);

        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        from = intent.getIntExtra("from", 0);

        mAuth = FirebaseAuth.getInstance();

        otp = findViewById(R.id.otp);
        check = findViewById(R.id.check);
        lang = findViewById(R.id.lang);


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Snackbar.make(findViewById(android.R.id.content), "Invalid phone number.",
                            Snackbar.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
            }
        };

        //send sms
        sendVerificationCode();

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(otp.getText().toString())) {
                    checkCode();
                }else {
                    Toast.makeText(OTPActivity.this, "Enter the code!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void checkCode() {
        String code = otp.getText().toString();
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
            signInWithPhoneAuthCredential(credential);
        }catch (Exception e){
            Toast toast = Toast.makeText(this, "Verification Code is wrong", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        Utils.hideKeyboard(OTPActivity.this);
        final ProgressDialog progressDialog = new ProgressDialog(OTPActivity.this);
        progressDialog.setTitle("Checking...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if(from == 1) {
                                progressDialog.setTitle("Registering your account...");
                                new UserController().newUser(SharedData.currentUser, new UserCallback() {
                                    @Override
                                    public void onSuccess(ArrayList<User> users) {
                                        SharedData.currentUser = users.get(0);
                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        editor.putBoolean(IS_USER_SAVED, true);
                                        editor.putString(PHONE, users.get(0).getPhone());
                                        editor.putString(PASS, users.get(0).getPass());
                                        editor.apply();

                                        progressDialog.dismiss();
                                        Intent intent = new Intent(OTPActivity.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onFail(String error) {
                                        progressDialog.dismiss();
                                        Toast.makeText(OTPActivity.this, error, Toast.LENGTH_LONG).show();
                                    }
                                });
                            }else if(from == 2) {
                                progressDialog.setTitle("finding your account...");
                                new UserController().getUserByPhone(phone, new UserCallback() {
                                    @Override
                                    public void onSuccess(ArrayList<User> users) {
                                        if(users.size() > 0) {
                                            SharedData.currentUser = users.get(0);
                                            SharedPreferences.Editor editor = sharedPref.edit();
                                            editor.putBoolean(IS_USER_SAVED, true);
                                            editor.putString(PHONE, users.get(0).getPhone());
                                            editor.putString(PASS, users.get(0).getPass());
                                            editor.apply();

                                            progressDialog.dismiss();
                                            Intent intent = new Intent(OTPActivity.this, ResetPasswordActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }else {
                                            Toast.makeText(OTPActivity.this, "user not registered", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onFail(String error) {
                                        progressDialog.dismiss();
                                        Toast.makeText(OTPActivity.this, error, Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        } else {
                            progressDialog.dismiss();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(OTPActivity.this,
                                        "Incorrect Verification Code ", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void sendVerificationCode(){
        if(phone.isEmpty()){
            Toast.makeText(OTPActivity.this, "Error while sending code!", Toast.LENGTH_LONG).show();
            return;
        }

        if(phone.length() < 10 ){
            Toast.makeText(OTPActivity.this, "phone is not valid!", Toast.LENGTH_LONG).show();
            return;
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }
}
