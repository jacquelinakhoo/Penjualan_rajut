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

public class TambahBeliKreditActivity extends AppCompatActivity {
    EditText etKodeKredit,etTanggal,etCicilan;
    Spinner spinnerPembeli,spinnerPaket,spinnerMobil;
    Button btnSimpan;
    ArrayList<String> listPembeli=new ArrayList<>();
    ArrayList<String> listPaket=new ArrayList<>();
    ArrayList<String> listMobil=new ArrayList<>();

    ArrayList<String> valPembeli=new ArrayList<>();
    ArrayList<String> valPaket=new ArrayList<>();
    ArrayList<String> valMobil=new ArrayList<>();

    ArrayList<Integer> hargaMobil=new ArrayList<>();
    ArrayList<Integer> tenor=new ArrayList<>();
    ArrayList<Double> uangMuka=new ArrayList<>();
    ArrayList<Double> bunga=new ArrayList<>();

    // CHECK: add URL_TAMBAH_KREDIT (TambahKredit.php) and URL_TAMPIL_PAKET (Tampilpaket.php) to your Config class
    private static final String URL_TAMBAH=Config.URL_TAMBAH_KREDIT;
    private static final String URL_PEMBELI=Config.URL_TAMPIL_PEMBELI;
    private static final String URL_MOBIL=Config.URL_TAMPIL_MOBIL;
    private static final String URL_PAKET=Config.URL_TAMPIL_PAKET;
    int cicilanPerbulan=0,totalCicil=0,tenorDipilih=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitytambahbelikredit);
        etKodeKredit=findViewById(R.id.editkodekredit);
        etTanggal=findViewById(R.id.edittanggal);
        etCicilan=findViewById(R.id.editbesarciclan);   // CHECK: his id is editcicilan, yours is editbesarciclan

        spinnerPembeli=findViewById(R.id.spinnerktp);
        spinnerPaket=findViewById(R.id.spinnerpaket);
        spinnerMobil=findViewById(R.id.spinnerkodemobil);

        btnSimpan=findViewById(R.id.ButtonSimpan);      // CHECK: his id is tombolsimpan, yours is ButtonSimpan
        loadPembeli();
        loadMobil();
        loadPaket();
        etTanggal.setOnClickListener(v -> showDatePicker());
        btnSimpan.setOnClickListener(v -> {
            String kodeKredit=etKodeKredit.getText().toString().trim();
            String tanggal=etTanggal.getText().toString().trim();

            if(kodeKredit.isEmpty() || tanggal.isEmpty() || cicilanPerbulan==0){
                Toast.makeText(this,"Harap isi semua data",Toast.LENGTH_SHORT).show();
            }else{
                int posPembeli=spinnerPembeli.getSelectedItemPosition();
                int posMobil=spinnerMobil.getSelectedItemPosition();
                int posPaket=spinnerPaket.getSelectedItemPosition();
                String ktp=valPembeli.get(posPembeli);
                String kodeMobil=valMobil.get(posMobil);
                String kodePaket=valPaket.get(posPaket);

                simpanData(kodeKredit,ktp,kodePaket,kodeMobil,tanggal,cicilanPerbulan,
                        tenorDipilih,totalCicil);
            }
        });
        spinnerPaket.setOnItemSelectedListener(new SimpleItemSelectedListener(()->hitungCicilan()));
        spinnerMobil.setOnItemSelectedListener(new SimpleItemSelectedListener(()->hitungCicilan()));
    }
    private void showDatePicker(){
        final Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog=new DatePickerDialog(TambahBeliKreditActivity.this,
                (DatePicker view, int year1, int month1, int dayOfMonth)->{
                    String tanggal= year1+"-"+(month1+1)+"-"+dayOfMonth;
                    etTanggal.setText(tanggal);
                },
                year,month,day
        );
        dialog.show();
    }
    private void loadPembeli(){
        StringRequest req=new StringRequest(Request.Method.GET,URL_PEMBELI, response->{
            try{
                JSONArray arr=new JSONArray(response);
                listPembeli.clear();
                valPembeli.clear();
                for(int i=0;i<arr.length();i++){
                    JSONObject obj=arr.getJSONObject(i);
                    String ktp=obj.getString("KTP");
                    String nama=obj.getString("NamaPembeli");

                    listPembeli.add(ktp+"-"+nama);
                    valPembeli.add(ktp);
                }
                ArrayAdapter<String> adapter=new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,listPembeli);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerPembeli.setAdapter(adapter);
            }catch (Exception e){
                e.printStackTrace();
            }
        },
                error->Log.e("Volley",error.toString()));
        Volley.newRequestQueue(this).add(req);
    }
    private void loadMobil(){
        StringRequest req=new StringRequest(Request.Method.GET,URL_MOBIL, response->{
            try{
                JSONArray arr=new JSONArray(response);
                listMobil.clear();
                valMobil.clear();
                hargaMobil.clear();
                for(int i=0;i<arr.length();i++){
                    JSONObject obj=arr.getJSONObject(i);
                    String kode= obj.getString("KodeMobil");
                    String merk= obj.getString("Merk");
                    String type=obj.getString("Type");
                    String warna=obj.getString("Warna");
                    String harga=obj.getString("Harga");

                    listMobil.add(kode+"-"+merk+"-"+type+"-"+warna+"-"+harga);
                    valMobil.add(kode);
                    hargaMobil.add(Integer.parseInt(harga));
                }
                ArrayAdapter<String> adapter=new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,listMobil);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerMobil.setAdapter(adapter);
            }catch (Exception e){
                e.printStackTrace();
            }
        },
                error->Log.e("Volley",error.toString()));
        Volley.newRequestQueue(this).add(req);
    }
    private void loadPaket(){
        StringRequest req=new StringRequest(Request.Method.GET,URL_PAKET, response->{
            try{
                JSONArray arr=new JSONArray(response);
                listPaket.clear();
                valPaket.clear();
                uangMuka.clear();
                tenor.clear();
                bunga.clear();
                for(int i=0;i<arr.length();i++){
                    JSONObject obj=arr.getJSONObject(i);
                    String kode= obj.getString("KodePaket");
                    Double dp= obj.getDouble("UangMuka");
                    int tnr=obj.getInt("Tenor");
                    Double bng=obj.getDouble("BungaCicilan");

                    listPaket.add(kode+"- DP:"+dp+"% Tenor:"+tnr+" Bunga"+bng+"%");
                    valPaket.add(kode);
                    uangMuka.add(dp);
                    tenor.add(tnr);
                    bunga.add(bng);
                }
                ArrayAdapter<String> adapter=new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,listPaket);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerPaket.setAdapter(adapter);
            }catch (Exception e){
                e.printStackTrace();
            }
        },
                error->Log.e("Volley",error.toString()));
        Volley.newRequestQueue(this).add(req);
    }
    private  void hitungCicilan(){
        if(spinnerPaket.getSelectedItemPosition()>=0 && spinnerMobil.getSelectedItemPosition()>=0){
            int posPaket=spinnerPaket.getSelectedItemPosition();
            int posMobil=spinnerMobil.getSelectedItemPosition();
            int harga=hargaMobil.get(posMobil);
            double dpPersen=uangMuka.get(posPaket);
            tenorDipilih=tenor.get(posPaket);
            double bng=bunga.get(posPaket);
            double dpRupiah=harga*dpPersen/100.0;
            double hutang= harga-dpRupiah;
            double bungaPerBulan=(hutang*bng/100.0)/12.0;
            double bungaTotal=bungaPerBulan*tenorDipilih;

            totalCicil=(int)(hutang+bungaTotal);
            cicilanPerbulan=totalCicil/tenorDipilih;

            etCicilan.setText("Rp "+cicilanPerbulan);
        }
    }
    private void simpanData(final String kodeKredit,final String ktp,final String kodePaket,final String kodeMobil
            ,final String tanggal, final int bayarKredit,final int tenor,final int totalCicil){
        StringRequest stringRequest=new StringRequest(Request.Method.POST,URL_TAMBAH, response->{
            Log.d("Response",response);
            Toast.makeText(this,response,Toast.LENGTH_LONG).show();
            if(response.contains("Data berhasil disimpan")){
                startActivity(new Intent(TambahBeliKreditActivity.this,DataBeliKreditActivity.class));
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
                params.put("kodekredit",kodeKredit);
                params.put("ktp",ktp);
                params.put("kodepaket",kodePaket);
                params.put("kodemobil",kodeMobil);
                params.put("tanggalkredit",tanggal);
                params.put("bayarkredit",String.valueOf(bayarKredit));
                params.put("tenor",String.valueOf(tenor));
                params.put("totalcicil",String.valueOf(totalCicil));

                return params;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }
}