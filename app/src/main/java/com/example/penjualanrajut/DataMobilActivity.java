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

public class DataMobilActivity extends AppCompatActivity {
    Button btnDataMobil;
    ListView listView;
    ArrayList<String> itemList;
    ArrayList<String> idList;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitymenumobil);

        btnDataMobil = findViewById(R.id.AddDataButton);
        listView = findViewById(R.id.listdatamobil);

        itemList = new ArrayList<>();
        idList = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemList);
        listView.setAdapter(adapter);
        itemList.add("TEST: If you can see this, the ListView works!");
        adapter.notifyDataSetChanged();
        btnDataMobil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DataMobilActivity.this, TambahDataMobilActivity.class);
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

    // ============================================
    // ONLY THIS INNER CLASS HAS CHANGED
    // ============================================
    private class TampilData extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL("http://192.168.137.166/penjualanmobil/tampilmobil.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line).append("\n");
                    }
                    reader.close();
                } else {
                    result.append("HTTP_ERROR: ").append(responseCode);
                }
            } catch (Exception e) {
                result.append("EXCEPTION: ").append(e.getMessage());
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            itemList.clear();
            idList.clear();

            // If there was a network error, show it immediately
            if (result.startsWith("HTTP_ERROR") || result.startsWith("EXCEPTION")) {
                itemList.add("Network/Server Error:\n" + result);
                adapter.notifyDataSetChanged();
                return;
            }

            // Try to parse JSON
            try {
                JSONArray jsonArray = new JSONArray(result);

                // If database is empty
                if (jsonArray.length() == 0) {
                    itemList.add("Database is empty. No records found.");
                }

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String id = obj.getString("kodemobil");
                    String merk = obj.getString("merk");
                    String type = obj.getString("type");
                    String warna = obj.getString("warna");
                    String harga = obj.getString("harga");

                    idList.add(id);
                    itemList.add("Kode\t" + id +
                            "\nMerk\t" + merk +
                            "\nTipe Mobil\t" + type +
                            "\nWarna\t" + warna +
                            "\nHarga\t" + harga +
                            "\n\n");
                }
                adapter.notifyDataSetChanged();

            } catch (Exception e) {
                // JSON failed - show the raw response so we can see what the server actually sent
                itemList.add("JSON Parse Failed: " + e.getMessage() +
                        "\n\nRaw Response from Server:\n" + result);
                adapter.notifyDataSetChanged();
            }
        }
    }
}