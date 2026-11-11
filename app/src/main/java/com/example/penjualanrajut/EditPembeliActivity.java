package com.example.penjualanrajut;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
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

public class EditPembeliActivity extends AppCompatActivity {
    EditText editNama, editAlamat, editTelp;
    Button btnUpdate, btnBatal;
    String ktp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityeditpembeli);

        editNama = findViewById(R.id.editNama);
        editAlamat = findViewById(R.id.editAlamat);
        editTelp = findViewById(R.id.editTelp);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnBatal = findViewById(R.id.btnBatal);

        Intent intent = getIntent();
        ktp = intent.getStringExtra("ktp");
        editNama.setText(intent.getStringExtra("nama"));
        editAlamat.setText(intent.getStringExtra("alamat"));
        editTelp.setText(intent.getStringExtra("telp"));

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nama = editNama.getText().toString().trim();
                String alamat = editAlamat.getText().toString().trim();
                String telp = editTelp.getText().toString().trim();

                if (nama.isEmpty() || alamat.isEmpty() || telp.isEmpty()) {
                    Toast.makeText(EditPembeliActivity.this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show();
                } else {
                    new UpdateData().execute(ktp, nama, alamat, telp);
                }
            }
        });

        btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private class UpdateData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String id = params[0];
            String nama = params[1];
            String alamat = params[2];
            String telp = params[3];

            try {
                URL url = new URL(Config.URL_EDIT_PEMBELI);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                String postData = "KTP=" + URLEncoder.encode(id, "UTF-8") +
                        "&NamaPembeli=" + URLEncoder.encode(nama, "UTF-8") +
                        "&AlamatPembeli=" + URLEncoder.encode(alamat, "UTF-8") +
                        "&TelpPembeli=" + URLEncoder.encode(telp, "UTF-8");
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
                    Toast.makeText(EditPembeliActivity.this, "Update berhasil", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(EditPembeliActivity.this, message, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}