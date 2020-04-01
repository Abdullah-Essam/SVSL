package com.example.svslsavemoneysavelife;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class setspendinglimit  extends  AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setspendinglimit);
        TextView limit = (TextView) findViewById(R.id.tventerlimit);
        TextView mess= (TextView) findViewById(R.id.tvlimit2);

        mess.setText("Dear"  + "please make sure that will enter the correct amount and you can not change it until the end of the month " );
        limit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText amount = (EditText) findViewById(R.id.etamount);
                if(amount!=null){

                    Intent in = new Intent(setspendinglimit.this, Hme.class);
                    startActivity(in);

                }
                else{
                    System.out.println("please enter an amount");
                }
            }
        });
    }
}

