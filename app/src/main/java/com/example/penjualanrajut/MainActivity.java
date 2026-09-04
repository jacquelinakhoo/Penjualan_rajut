package com.example.penjualanrajut;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnMobil = findViewById(R.id.ButtonDatamobil);
        Button btnPembeli = findViewById(R.id.ButtonPembeli);
        Button btnPaket = findViewById(R.id.ButtonPaket);
        Button btnBeliCash = findViewById(R.id.ButtonBeliCash);
        Button btnBeliKredit = findViewById(R.id.ButtombeliKredit);
        Button btnCicilan = findViewById(R.id.ButtonCicilan);

        btnMobil.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DataMobilActivity.class)));
        btnPembeli.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DataPembeliActivity.class)));
        btnPaket.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DataPaketActivity.class)));
        btnBeliCash.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DataBeliCashActivity.class)));
        btnBeliKredit.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DataBeliKreditActivity.class)));
        btnCicilan.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DataCicilanActivity.class)));
    }
}