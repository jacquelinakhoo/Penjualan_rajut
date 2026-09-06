package com.example.penjualanrajut;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class DataPaketActivity extends AppCompatActivity {
    Button btnTambah;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitydatapaketkredit);

        btnTambah = findViewById(R.id.ButtonTambah);
        btnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DataPaketActivity.this, TambahDataPaketActivity.class);
                startActivity(intent);
            }
        });
    }
}