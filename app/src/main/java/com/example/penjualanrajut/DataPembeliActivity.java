package com.example.penjualanrajut;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
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
import java.util.ArrayList;

public class DataPembeliActivity extends AppCompatActivity {
    Button btnTambah;
    ListView listView;
    ArrayList<String> itemList;
    ArrayList<String> idList;
    ArrayAdapter<String> adapter;

    private static final int REQUEST_EDIT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitydatapembeli);

        btnTambah = findViewById(R.id.ButtonTambah);
        listView = findViewById(R.id.listpembeli);

        itemList = new ArrayList<>();
        idList = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemList);
        listView.setAdapter(adapter);

        btnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DataPembeliActivity.this, TambahDataPembeliActivity.class);
                startActivity(intent);
            }
        });

        new TampilData().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new TampilData().execute();
        listView.setOnItemClickListener((parent, view, position, id) -> showOptionDialog(position));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT && resultCode == RESULT_OK) {
            new TampilData().execute();
        }
    }

    private class TampilData extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(Config.URL_TAMPIL_PEMBELI);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONArray jsonArray = new JSONArray(result);
                itemList.clear();
                idList.clear();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String ktp = obj.getString("KTP");
                    String nama = obj.getString("NamaPembeli");
                    String alamat = obj.getString("AlamatPembeli");
                    String telp = obj.getString("TelpPembeli");

                    idList.add(ktp);
                    itemList.add("KTP\t" + ktp +
                            "\nNama\t" + nama +
                            "\nAlamat\t" + alamat +
                            "\nTelp\t" + telp +
                            "\n\n");
                }

                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showOptionDialog(int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pilihan Aksi")
                .setItems(new CharSequence[]{"Detail","Edit","Hapus"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            showDetail(position);
                            break;
                        case 1:
                            editItem(position);
                            break;
                        case 2:
                            confirmDelete(position);
                            break;
                    }
                });
        builder.show();
    }

    private void showDetail(int position){
        String detail = itemList.get(position);
        new AlertDialog.Builder(this)
                .setTitle("Detail Pembeli")
                .setMessage(detail)
                .setPositiveButton("OK", null)
                .show();
    }

    private void editItem(int position){
        Intent intent = new Intent(DataPembeliActivity.this, EditPembeliActivity.class);
        intent.putExtra("ktp", idList.get(position));
        String[] lines = itemList.get(position).split("\n");
        intent.putExtra("nama", lines[1].replace("Nama\t", "").trim());
        intent.putExtra("alamat", lines[2].replace("Alamat\t", "").trim());
        intent.putExtra("telp", lines[3].replace("Telp\t", "").trim());
        startActivityForResult(intent, REQUEST_EDIT);
    }

    private void confirmDelete(int position){
        new AlertDialog.Builder(this).setTitle("Konfirmasi Hapus").setMessage("Apakah Anda yakin untuk menghapus data ini?")
                .setPositiveButton("Hapus", (dialog, which) -> deleteItem(position))
                .setNegativeButton("Batal", null).show();
    }

    private void deleteItem(int position){
        String id = idList.get(position);
        new HapusDataPembeli().execute(id, String.valueOf(position));
    }

    private class HapusDataPembeli extends AsyncTask<String, Void, String> {
        int positionToDelete;

        @Override
        protected String doInBackground(String... params) {
            String id = params[0];
            positionToDelete = Integer.parseInt(params[1]);
            try {
                URL url = new URL(Config.URL_HAPUS_PEMBELI);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                String postData = "KTP=" + URLEncoder.encode(id, "UTF-8");
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
                return "{\"status\":0,\"message\":\"Error menghapus data\"}";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject response = new JSONObject(result);
                int status = response.getInt("status");

                if (status == 1) {
                    itemList.remove(positionToDelete);
                    idList.remove(positionToDelete);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(DataPembeliActivity.this, "Data Berhasil dihapus", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DataPembeliActivity.this, "Gagal menghapus data", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}