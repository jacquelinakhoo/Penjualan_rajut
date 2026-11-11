package com.example.penjualanrajut;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DataBeliCashActivity extends AppCompatActivity {
    Button btnTambah;
    ListView listView;
    ArrayList<String> itemList;
    ArrayList<String> idList;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitydatabayarcash);

        btnTambah = findViewById(R.id.ButtonTambah);
        listView = findViewById(R.id.listbayarcash);

        itemList = new ArrayList<>();
        idList = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemList);
        listView.setAdapter(adapter);

        btnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DataBeliCashActivity.this, TambahBeliCashActivity.class);
                startActivity(intent);
            }
        });

        new TampilData().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new TampilData().execute();
    }

    private class TampilData extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(Config.URL_TAMPIL_CASH);
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
                    String kode = obj.getString("KodeCash");
                    String ktp = obj.getString("KTP");
                    String mobil = obj.getString("KodeMobil");
                    String tanggal = obj.getString("CashTgl");
                    String bayar = obj.getString("CashBayar");

                    idList.add(kode);
                    itemList.add("Kode Pembelian\t" + kode +
                            "\nPembeli\t" + ktp +
                            "\nMobil\t" + mobil +
                            "\nTanggal\t" + tanggal +
                            "\nTotal Bayar\t" + bayar +
                            "\n\n");
                }

                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}