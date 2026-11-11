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

public class EditPaketActivity extends AppCompatActivity {
    EditText editDp, editTenor, editBunga;
    Button btnUpdate, btnBatal;
    String kodepaket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityeditpaket);

        editDp = findViewById(R.id.editDp);
        editTenor = findViewById(R.id.editTenor);
        editBunga = findViewById(R.id.editBunga);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnBatal = findViewById(R.id.btnBatal);

        Intent intent = getIntent();
        kodepaket = intent.getStringExtra("kodepaket");
        editDp.setText(intent.getStringExtra("uangmuka"));
        editTenor.setText(intent.getStringExtra("tenor"));
        editBunga.setText(intent.getStringExtra("bunga"));

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uangmuka = editDp.getText().toString().trim();
                String tenor = editTenor.getText().toString().trim();
                String bunga = editBunga.getText().toString().trim();

                if (uangmuka.isEmpty() || tenor.isEmpty() || bunga.isEmpty()) {
                    Toast.makeText(EditPaketActivity.this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show();
                } else {
                    new UpdateData().execute(kodepaket, uangmuka, tenor, bunga);
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
            String uangmuka = params[1];
            String tenor = params[2];
            String bunga = params[3];

            try {
                URL url = new URL(Config.URL_EDIT_PAKET);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                String postData = "kodepaket=" + URLEncoder.encode(id, "UTF-8") +
                        "&uangmuka=" + URLEncoder.encode(uangmuka, "UTF-8") +
                        "&tenor=" + URLEncoder.encode(tenor, "UTF-8") +
                        "&bungacicilan=" + URLEncoder.encode(bunga, "UTF-8");
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
                    Toast.makeText(EditPaketActivity.this, "Update berhasil", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(EditPaketActivity.this, message, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}