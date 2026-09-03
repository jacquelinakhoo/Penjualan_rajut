package com.example.penjualanrajut;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.QuickContactBadge;

import androidx.appcompat.app.AppCompatActivity;

import com.example.penjualanrajut.DataMobilActivity;
import com.example.penjualanrajut.R;

public class MainActivity extends AppCompatActivity{
    Button BtnMobil;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BtnMobil=findViewById(R.id.ButtonDatamobil);


        BtnMobil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent intent  = new Intent(MainActivity.this, DataMobilActivity.class);
                startActivity(intent);

            }
        });
    }
}