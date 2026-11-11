package com.example.penjualanrajut;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class EditMobilActivity extends AppCompatActivity {
    EditText editMerk, editType, editWarna, editHarga;
    Button btnUpdate, btnBatal;
    String kodeMobil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityeditmobil);

        editMerk = findViewById(R.id.editMerk);
        editType = findViewById(R.id.editType);
        editWarna = findViewById(R.id.editWarna);
        editHarga = findViewById(R.id.editHarga);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnBatal = findViewById(R.id.btnBatal);

        Intent intent = getIntent();
        kodeMobil = intent.getStringExtra("kode_mobil");
        editMerk.setText(intent.getStringExtra("merk"));
        editType.setText(intent.getStringExtra("type"));
        editWarna.setText(intent.getStringExtra("warna"));
        editHarga.setText(intent.getStringExtra("harga"));

        btnUpdate.setOnClickListener(v ->
        {
            String merk = editMerk.getText().toString().trim();
            String type = editType.getText().toString().trim();
            String warna = editWarna.getText().toString().trim();
            String harga = editHarga.getText().toString().trim();
            if (merk.isEmpty() || type.isEmpty() || warna.isEmpty() || harga.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show();
            } else {
                new UpdateData().execute(kodeMobil, merk, type, warna, harga);
            }

        });

        btnBatal.setOnClickListener(v -> finish());
    }

    private class UpdateData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String id = params[0];
            String merk = params[1];
            String type = params[2];
            String warna = params[3];
            String harga = params[4];

            try {
                URL url = new URL(Config.URL_EDIT_MOBIL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                String postData = "kodemobil=" + URLEncoder.encode(id, "UTF-8") +
                        "&merk=" + URLEncoder.encode(merk, "UTF-8") +
                        "&type=" + URLEncoder.encode(type, "UTF-8") +
                        "&warna=" + URLEncoder.encode(warna, "UTF-8") +
                        "&harga=" + URLEncoder.encode(harga, "UTF-8");
                writer.write(postData);
                writer.flush();
                writer.close();
                os.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
                return sb.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return "{\"status\":0,\"message\":\"Error Update\"}";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject response = new JSONObject(result);
                int status = response.getInt("status");
                String message = response.getString("message");

                if (status == 1) {
                    Toast.makeText(EditMobilActivity.this, "Update berhasil", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(EditMobilActivity.this, message, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}