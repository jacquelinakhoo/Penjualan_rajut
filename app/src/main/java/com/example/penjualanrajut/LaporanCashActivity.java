package com.example.penjualanrajut;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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

public class LaporanCashActivity extends AppCompatActivity {
    Spinner spinnerPeriode;
    EditText etTanggal;
    ListView listLaporan;
    ArrayList<String> listData = new ArrayList<>();

    // Uses Config.java
    private static final String URL_LAPOR = Config.URL_LAPOR_CASH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.laporanjualcash);

        spinnerPeriode = findViewById(R.id.spinnerpilihan);
        etTanggal = findViewById(R.id.edittanggal);
        listLaporan = findViewById(R.id.listcash);

        String[] periode = {"Bulanan", "Tahunan"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, periode);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPeriode.setAdapter(adapter);

        etTanggal.setOnClickListener(v -> showDatePickker());
        spinnerPeriode.setOnItemSelectedListener(new SimpleItemSelectedListener(this::loadData));
    }

    private void showDatePickker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        String selectedPeriode = spinnerPeriode.getSelectedItem().toString();

        DatePickerDialog dialog = new DatePickerDialog(this, (view, Y, m, d) -> {
            if (selectedPeriode.equals("Tahunan")) {
                etTanggal.setText(String.valueOf(Y));
            } else {
                String tanggal = Y + "-" + String.format("%02d", (m + 1));
                etTanggal.setText(tanggal);
            }
            loadData();
        }, year, month, 1);

        try {
            int dayId = getResources().getIdentifier("day", "id", "android");
            int monthId = getResources().getIdentifier("month", "id", "android");
            if (selectedPeriode.equals("Tahunan")) {
                if (dialog.getDatePicker().findViewById(dayId) != null)
                    dialog.getDatePicker().findViewById(dayId).setVisibility(View.GONE);
                if (dialog.getDatePicker().findViewById(monthId) != null)
                    dialog.getDatePicker().findViewById(monthId).setVisibility(View.GONE);
            } else {
                if (dialog.getDatePicker().findViewById(dayId) != null)
                    dialog.getDatePicker().findViewById(dayId).setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e("DataPicker", "Gagal menyembunyikan field: " + e.getMessage());
        }
        dialog.show();
    }

    private void loadData() {
        String periode = spinnerPeriode.getSelectedItem().toString().equals("Tahunan") ? "tahun" : "bulan";
        String tanggal = etTanggal.getText().toString().trim();
        if (tanggal.isEmpty()) return;

        String url = URL_LAPOR + "?periode=" + periode + "&tanggal=" + tanggal;

        StringRequest req = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONArray arr = new JSONArray(response);
                listData.clear();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);

                    // Using your exact column names
                    String tampil = "Kode Cash: " + obj.getString("KodeCash") +
                            "\nPembeli: " + obj.getString("NamaPembeli") +
                            "\nMobil: " + obj.getString("Merk") + " " + obj.getString("Type") +
                            "\nTanggal: " + obj.getString("TglCash") +
                            "\nBayar: Rp " + obj.getString("BayarCash");
                    listData.add(tampil);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
                listLaporan.setAdapter(adapter);
            } catch (Exception e) {
                Log.e("JSONError", e.getMessage());
                Toast.makeText(this, "Gagal memproses data!", Toast.LENGTH_SHORT).show();
            }
        }, error -> Toast.makeText(this, "Gagal Memuat Data", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(req);
    }
}