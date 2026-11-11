package com.example.penjualanrajut;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText edituser, editpass;
    Button tombolsimpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edituser = findViewById(R.id.edituser);
        editpass = findViewById(R.id.editpass);
        tombolsimpan = findViewById(R.id.tombolsimpan);

        tombolsimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        String username = edituser.getText().toString().trim();
        String password = editpass.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username dan password tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_LOGIN,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        boolean success = json.getBoolean("success");

                        if (success) {
                            int level = json.getInt("level");
                            // Level 1 = Staff, Level 2 = Manager
                            if (level == 1) {
                                startActivity(new Intent(MainActivity.this, BerandaSActivity.class));
                                finish();
                            } else if (level == 2) {
                                startActivity(new Intent(MainActivity.this, BerandaMActivity.class));
                                finish();
                            } else {
                                Toast.makeText(MainActivity.this, "Level tidak dikenali", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(MainActivity.this, "Koneksi gagal: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }
}