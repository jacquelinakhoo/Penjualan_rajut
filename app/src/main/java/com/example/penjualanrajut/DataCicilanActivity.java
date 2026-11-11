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
import java.util.Iterator;

public class DataCicilanActivity extends AppCompatActivity {
    Button btnTambah;
    ListView listView;
    ArrayList<String> itemList;
    ArrayList<String> idList;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitydatapembayarancicilan);

        btnTambah = findViewById(R.id.ButtonTambah);
        listView = findViewById(R.id.listbayarcicilan);

        itemList = new ArrayList<>();
        idList = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemList);
        listView.setAdapter(adapter);

        btnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DataCicilanActivity.this, TambahCicilanActivity.class);
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
                URL url = new URL(Config.URL_TAMPIL_CICILAN);
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

                    // CHECK: shows every column as it comes from the table. Once I know your
                    // bayarcicilan columns, I can swap this for nicer labels like the other screens.
                    StringBuilder sb = new StringBuilder();
                    String firstValue = "";
                    Iterator<String> keys = obj.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        String value = obj.getString(key);
                        if (firstValue.isEmpty()) firstValue = value;
                        sb.append(key).append("\t").append(value).append("\n");
                    }

                    idList.add(firstValue);
                    itemList.add(sb.toString() + "\n");
                }

                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}