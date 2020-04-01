package com.example.svslsavemoneysavelife.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.svslsavemoneysavelife.R;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText phone;
    private Button reset, lang;

    private String enteredPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        phone = findViewById(R.id.phone);
        reset = findViewById(R.id.reset);
        lang = findViewById(R.id.lang);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()) {
                    enteredPhone = phone.getText().toString();
                    Intent intent = new Intent(ForgetPasswordActivity.this, OTPActivity.class);
                    intent.putExtra("from", 2);
                    intent.putExtra("phone", enteredPhone);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean validate() {
        if(TextUtils.isEmpty(phone.getText().toString())){
            phone.setError("required field!");
            return false;
        }
        return true;
    }
}
