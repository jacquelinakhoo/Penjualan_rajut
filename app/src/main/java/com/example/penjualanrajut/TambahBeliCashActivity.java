package com.example.penjualanrajut;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TambahBeliCashActivity extends AppCompatActivity {

    EditText etKodeCash, etTanggal;
    Spinner spinnerktp, spinnermobil;
    Button btnSimpan;

    ArrayList<String> listPembeli = new ArrayList<>();
    ArrayList<String> listMobil = new ArrayList<>();

    ArrayList<String> listKtpValue = new ArrayList<>();
    ArrayList<String> listMobilValue = new ArrayList<>();

    private static final String URL_TAMBAH  = Config.URL_TAMBAH_CASH;
    private static final String URL_PEMBELI = Config.URL_TAMPIL_PEMBELI;
    private static final String URL_MOBIL   = Config.URL_TAMPIL_MOBIL;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitytambahbelicash);

        etKodeCash = findViewById(R.id.editkodecash);
        etTanggal = findViewById(R.id.edittanggal);
        spinnerktp = findViewById(R.id.spinnerktp);
        spinnermobil = findViewById(R.id.spinnerkodemobil);
        btnSimpan = findViewById(R.id.tombolsimpan);

        loadPembeli();
        loadMobil();

        etTanggal.setOnClickListener(v -> showDatePicker());

        btnSimpan.setOnClickListener(v -> {
            String kodeCash = etKodeCash.getText().toString().trim();
            String tanggal = etTanggal.getText().toString().trim();

            if (kodeCash.isEmpty() || tanggal.isEmpty()) {
                Toast.makeText(this, "Harap isi semua data", Toast.LENGTH_SHORT).show();
            } else {
                int posPembeli = spinnerktp.getSelectedItemPosition();
                int posMobil = spinnermobil.getSelectedItemPosition();
                String ktp = listKtpValue.get(posPembeli);
                String kodeMobil = listMobilValue.get(posMobil);

                simpanData(kodeCash, ktp, kodeMobil, tanggal);
            }
        });
    }

    private void showDatePicker(){
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(TambahBeliCashActivity.this,
                (DatePicker view, int year1, int month1, int dayOfMonth) -> {
                    String tanggal = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
                    etTanggal.setText(tanggal);
                },
                year, month, day
        );
        dialog.show();
    }

    private void loadPembeli(){
        StringRequest req = new StringRequest(Request.Method.GET, URL_PEMBELI, response -> {
            try {
                JSONArray arr = new JSONArray(response);
                listPembeli.clear();
                listKtpValue.clear();

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    String ktp = obj.getString("KTP");
                    String nama = obj.getString("NamaPembeli");

                    listPembeli.add(ktp + "-" + nama);
                    listKtpValue.add(ktp);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listPembeli);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerktp.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> Log.e("Volley", error.toString()));

        Volley.newRequestQueue(this).add(req);
    }

    private void loadMobil(){
        StringRequest req = new StringRequest(Request.Method.GET, URL_MOBIL, response -> {
            try {
                JSONArray arr = new JSONArray(response);
                listMobil.clear();
                listMobilValue.clear();

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    String kode = obj.getString("KodeMobil");
                    String merk = obj.getString("Merk");
                    String type = obj.getString("Type");
                    String warna = obj.getString("Warna");
                    String harga = obj.getString("Harga");

                    listMobil.add(kode + "-" + merk + "-" + type + "-" + warna + "-" + harga);
                    listMobilValue.add(kode);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listMobil);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnermobil.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> Log.e("Volley", error.toString()));

        Volley.newRequestQueue(this).add(req);
    }

    private void simpanData(final String kodeCash, final String ktp, final String kodeMobil, final String tanggal){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_TAMBAH, response -> {
            Log.d("Response", response);
            Toast.makeText(this, response, Toast.LENGTH_LONG).show();
            if (response.contains("Data berhasil disimpan")) {
                startActivity(new Intent(TambahBeliCashActivity.this, DataBeliCashActivity.class));
                finish();
            }
        }, error -> {
            Toast.makeText(this, "Gagal Menyimpan Data!", Toast.LENGTH_SHORT).show();
            Log.e("Volley error", error.toString());
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("kodecash", kodeCash);
                params.put("ktp", ktp);
                params.put("kodemobil", kodeMobil);
                params.put("tanggalcash", tanggal);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }
}