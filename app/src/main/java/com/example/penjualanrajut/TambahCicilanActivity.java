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

public class TambahCicilanActivity extends AppCompatActivity {
    EditText etKodeCicilan,etTanggal,etCicilanke,etJumlahCicilan,etsisaKe,etSisacicilann;
    Spinner spinnerKredit;
    Button btnSimpan;

    ArrayList<String> listKredit=new ArrayList<>();
    ArrayList<String> valKredit=new ArrayList<>();
    ArrayList<Integer> bayarKredit=new ArrayList<>();
    ArrayList<Integer> tenorList=new ArrayList<>();
    ArrayList<Integer> totalCicilList=new ArrayList<>();
    ArrayList<Integer> cicilankeList=new ArrayList<>();
    ArrayList<Integer> sisakeList=new ArrayList<>();
    ArrayList<Integer> sisahargaList=new ArrayList<>();

    private static final String URL_KREDIT=Config.URL_TAMPIL_KREDIT_HITUNG;
    private static final String URL_TAMBAH=Config.URL_TAMBAH_CICILAN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitytambahbayarcicilan);
        etKodeCicilan=findViewById(R.id.editkodecicilan);
        etTanggal=findViewById(R.id.edittanggal);
        etCicilanke=findViewById(R.id.editcicilke);
        etJumlahCicilan=findViewById(R.id.editbayar);
        etsisaKe=findViewById(R.id.editsisake);
        etSisacicilann=findViewById(R.id.editsisa);
        spinnerKredit=findViewById(R.id.spinnerkodekredit);
        btnSimpan=findViewById(R.id.tombolsimpan);

        loadKredit();
        etTanggal.setOnClickListener(v->showDatePicker());

        spinnerKredit.setOnItemSelectedListener(new SimpleItemSelectedListener(this::isiOtomatis));

        btnSimpan.setOnClickListener(v -> {
            String kodeCicilan=etKodeCicilan.getText().toString().trim();
            String tanggal=etTanggal.getText().toString().trim();
            if(kodeCicilan.isEmpty() || tanggal.isEmpty()){
                Toast.makeText(this,"Semua Data Wajib diisi",Toast.LENGTH_SHORT).show();
                return;
            }

            int pos=spinnerKredit.getSelectedItemPosition();
            String kodeKredit=valKredit.get(pos);
            String cicilanKe=etCicilanke.getText().toString().trim();
            String jumlahCicilan=etJumlahCicilan.getText().toString().trim();
            String sisaKe=etsisaKe.getText().toString().trim();
            String sisaCicilan=etSisacicilann.getText().toString().trim();

            simpanData(kodeCicilan,kodeKredit,tanggal,cicilanKe,jumlahCicilan,sisaKe,sisaCicilan);
        });
    }

    private void showDatePicker(){
        final Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog=new DatePickerDialog(TambahCicilanActivity.this,
                (DatePicker view, int year1, int month1, int dayOfMonth)->{
                    String tanggal= year1+"-"+(month1+1)+"-"+dayOfMonth;
                    etTanggal.setText(tanggal);
                },
                year,month,day
        );
        dialog.show();
    }

    private void loadKredit(){
        StringRequest req=new StringRequest(Request.Method.GET,URL_KREDIT, response->{
            try{
                JSONArray arr=new JSONArray(response);
                listKredit.clear();
                valKredit.clear();
                bayarKredit.clear();
                tenorList.clear();
                totalCicilList.clear();
                cicilankeList.clear();
                sisakeList.clear();
                sisahargaList.clear();
                for(int i=0;i<arr.length();i++){
                    JSONObject obj=arr.getJSONObject(i);
                    String kode= obj.getString("KodeKredit");
                    String ktp= obj.getString("KTP");
                    int bayar=obj.getInt("BayarKredit");
                    int tenor=obj.getInt("Tenor");
                    int total=obj.getInt("TotalCicil");
                    int cicilanke=obj.getInt("cicilanke");
                    int sisake=obj.getInt("sisake");
                    int sisaharga=obj.getInt("sisaharga");

                    listKredit.add(kode+"-"+ktp);
                    valKredit.add(kode);
                    bayarKredit.add(bayar);
                    tenorList.add(tenor);
                    totalCicilList.add(total);
                    cicilankeList.add(cicilanke);
                    sisakeList.add(sisake);
                    sisahargaList.add(sisaharga);
                }
                ArrayAdapter<String> adapter=new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,listKredit);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerKredit.setAdapter(adapter);
            }catch (Exception e){
                e.printStackTrace();
            }
        },
                error->Log.e("Volley",error.toString()));
        Volley.newRequestQueue(this).add(req);
    }

    private void isiOtomatis(){
        int pos = spinnerKredit.getSelectedItemPosition();
        if(pos>=0){   // CHECK: his frame shows pos>0, which skips the first kredit in the list
            etCicilanke.setText(String.valueOf(cicilankeList.get(pos)));
            etJumlahCicilan.setText(String.valueOf(bayarKredit.get(pos)));
            etsisaKe.setText(String.valueOf(sisakeList.get(pos)));
            etSisacicilann.setText(String.valueOf(sisahargaList.get(pos)));
        }
    }

    private void simpanData(final String kodeCicilan,final String kodeKredit,final String tanggal,
                            final String cicilanKe,final String jumlahCicilan,
                            final String sisaKe,final String sisaCicilan){
        StringRequest stringRequest=new StringRequest(Request.Method.POST,URL_TAMBAH, response->{
            Log.d("Response",response);
            Toast.makeText(this,response,Toast.LENGTH_LONG).show();
            if(response.contains("Data berhasil disimpan")){
                startActivity(new Intent(TambahCicilanActivity.this,DataCicilanActivity.class));
                finish();
            }
        },
                error->{
                    Toast.makeText(this,"Gagal Menyimpan Data!", Toast.LENGTH_SHORT).show();
                    Log.e("Volley error",error.toString());
                })
        {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params=new HashMap<>();
                params.put("kodecicilan",kodeCicilan);
                params.put("kodekredit",kodeKredit);
                params.put("tanggalcicilan",tanggal);
                params.put("cicilanke",cicilanKe);
                params.put("jumlahcicilan",jumlahCicilan);
                params.put("sisacicilke",sisaKe);
                params.put("sisacicilan",sisaCicilan);

                return params;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }
}