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

public class TambahDataPembeliActivity extends AppCompatActivity {

    EditText etKtp, etNama, etAlamat, etNohp;
    Button btnSimpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitytambahdatapembeli);

        etKtp = findViewById(R.id.editktp);
        etNama = findViewById(R.id.editnama);
        etAlamat = findViewById(R.id.editalamat);
        etNohp = findViewById(R.id.editnohp);
        btnSimpan = findViewById(R.id.ButtonSimpan);

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ktp = etKtp.getText().toString().trim();
                String nama = etNama.getText().toString().trim();
                String alamat = etAlamat.getText().toString().trim();
                String telp = etNohp.getText().toString().trim();

                if (ktp.isEmpty() || nama.isEmpty() || alamat.isEmpty() || telp.isEmpty()) {
                    Toast.makeText(TambahDataPembeliActivity.this, "Harap isi semua data", Toast.LENGTH_SHORT).show();
                } else {
                    simpanData(ktp, nama, alamat, telp);
                }
            }
        });
    }

    private void simpanData(final String ktp, final String nama, final String alamat, final String telp) {
        String url = Config.URL_TAMBAH_PEMBELI;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(TambahDataPembeliActivity.this, response, Toast.LENGTH_LONG).show();
                        if (response.contains("Data berhasil disimpan")) {
                            startActivity(new Intent(TambahDataPembeliActivity.this, DataPembeliActivity.class));
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(TambahDataPembeliActivity.this, "Gagal Menyimpan Data", Toast.LENGTH_SHORT).show();
                        android.util.Log.e("Volley Error", error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("KTP", ktp);
                params.put("NamaPembeli", nama);
                params.put("AlamatPembeli", alamat);
                params.put("TelpPembeli", telp);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }
}