package com.example.penjualanrajut;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class TambahDataMobilActivity extends AppCompatActivity {

    EditText etKode, etMerk, etType, etWarna, etHarga;
    Button btnSimpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityaddmobil);

        etKode = findViewById(R.id.editkode);
        etMerk = findViewById(R.id.Merk);
        etType = findViewById(R.id.edittYPE);
        etWarna = findViewById(R.id.editwarna);
        etHarga = findViewById(R.id.editharga);
        btnSimpan = findViewById(R.id.CreationButton);

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String kode = etKode.getText().toString().trim();
                String merk = etMerk.getText().toString().trim();
                String tipe = etType.getText().toString().trim();
                String warna = etWarna.getText().toString().trim();
                String harga = etHarga.getText().toString().trim();

                if (kode.isEmpty() || merk.isEmpty() || tipe.isEmpty() || warna.isEmpty() || harga.isEmpty()) {
                    Toast.makeText(TambahDataMobilActivity.this, "Harap isi semua data", Toast.LENGTH_SHORT).show();
                } else {
                    simpanData(kode, merk, tipe, warna, harga);
                }
            }
        });
    }

    private void simpanData(final String kode, final String merk, final String tipe, final String warna, final String harga) {
        String url = Config.URL_TAMBAH_MOBIL;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(TambahDataMobilActivity.this, response, Toast.LENGTH_LONG).show();
                        if (response.contains("Data berhasil disimpan")) {
                            startActivity(new Intent(TambahDataMobilActivity.this, DataMobilActivity.class));
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(TambahDataMobilActivity.this, "Gagal Menyimpan Data", Toast.LENGTH_SHORT).show();
                        android.util.Log.e("Volley Error", error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("kodemobil", kode);
                params.put("merk", merk);
                params.put("type", tipe);
                params.put("warna", warna);
                params.put("harga", harga);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }
}