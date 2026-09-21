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

public class TambahDataPaketActivity extends AppCompatActivity {

    EditText etKodePaket, etDp, etTenor, etBunga;
    Button btnSimpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivtytambahdatapaket);

        etKodePaket = findViewById(R.id.editodepaket);
        etDp = findViewById(R.id.editdp);
        etTenor = findViewById(R.id.edittenor);
        etBunga = findViewById(R.id.editBunga);
        btnSimpan = findViewById(R.id.ButtonSimpan);

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String kodepaket = etKodePaket.getText().toString().trim();
                String uangmuka = etDp.getText().toString().trim();
                String tenor = etTenor.getText().toString().trim();
                String bunga = etBunga.getText().toString().trim();

                if (kodepaket.isEmpty() || uangmuka.isEmpty() || tenor.isEmpty() || bunga.isEmpty()) {
                    Toast.makeText(TambahDataPaketActivity.this, "Harap isi semua data", Toast.LENGTH_SHORT).show();
                } else {
                    simpanData(kodepaket, uangmuka, tenor, bunga);
                }
            }
        });
    }

    private void simpanData(final String kodepaket, final String uangmuka, final String tenor, final String bunga) {
        String url = "http://192.168.1.4/penjualanmobil/tambahpaket.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(TambahDataPaketActivity.this, response, Toast.LENGTH_LONG).show();
                        if (response.contains("Data berhasil disimpan")) {
                            startActivity(new Intent(TambahDataPaketActivity.this, DataPaketActivity.class));
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(TambahDataPaketActivity.this, "Gagal Menyimpan Data", Toast.LENGTH_SHORT).show();
                        android.util.Log.e("Volley Error", error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("kodepaket", kodepaket);
                params.put("uangmuka", uangmuka);
                params.put("tenor", tenor);
                params.put("bungacicilan", bunga);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }
}