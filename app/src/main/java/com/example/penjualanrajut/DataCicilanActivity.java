package com.example.penjualanrajut;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class DataCicilanActivity extends AppCompatActivity {
    Button btnTambah;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitydatapembayarancicilan);

        btnTambah = findViewById(R.id.ButtonTambah);
        btnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DataCicilanActivity.this, TambahCicilanActivity.class);
                startActivity(intent);
            }
        });
    }
}