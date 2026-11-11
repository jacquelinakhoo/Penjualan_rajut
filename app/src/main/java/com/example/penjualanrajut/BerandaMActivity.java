package com.example.penjualanrajut;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class BerandaMActivity extends AppCompatActivity {
    Button btnCash, btnKredit, btnCicilan, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_berandam);

        btnCash = findViewById(R.id.tombolbayarcash);
        btnKredit = findViewById(R.id.tombolbelikredit);
        btnCicilan = findViewById(R.id.tombolbayarcicil);
        btnLogout = findViewById(R.id.tombollogout);

        // Menyesuaikan dengan nama Activity yang sudah kamu punya
        btnCash.setOnClickListener(v -> startActivity(new Intent(BerandaMActivity.this, DataBeliCashActivity.class)));
        btnKredit.setOnClickListener(v -> startActivity(new Intent(BerandaMActivity.this, DataBeliKreditActivity.class)));
        btnCicilan.setOnClickListener(v -> startActivity(new Intent(BerandaMActivity.this, DataCicilanActivity.class)));

        btnLogout.setOnClickListener(v -> {
            startActivity(new Intent(BerandaMActivity.this, MainActivity.class));
            finish();

            btnCash = findViewById(R.id.tombolbayarcash);
            btnCash.setOnClickListener(view -> startActivity(new Intent(BerandaMActivity.this, LaporanCashActivity.class)));
        });
    }
}