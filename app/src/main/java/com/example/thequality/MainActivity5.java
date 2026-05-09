package com.example.thequality;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.thequality.homepage.MainActivityHome;
import com.example.thequality.homepage.MainActivitydeteksi;


public class MainActivity5 extends AppCompatActivity {

    private TextView tampilanQdong, tampilanCdong;
    private Button backlagidan, newdetection, exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        tampilanQdong = findViewById(R.id.tampilanQdong);
        tampilanCdong = findViewById(R.id.tampilanCdong);
        backlagidan = findViewById(R.id.backlagidan);
        newdetection = findViewById(R.id.newdetection);
        exit = findViewById(R.id.exit);

        // SharedPreferences
        SharedPreferences sharedData2 = getSharedPreferences("MyData2", MODE_PRIVATE);
        SharedPreferences sharedI0 = getSharedPreferences("DataI0", MODE_PRIVATE);
        SharedPreferences fungsiPrefs = getSharedPreferences("fungsiPrefs", MODE_PRIVATE);

        // Hitung Iavg dari sharedData2
        int countI = sharedData2.getInt("countI", 0);
        float sumI = 0f;
        for (int i = 0; i < countI; i++) {
            sumI += sharedData2.getFloat("I_" + i, 0f);
        }
        float Iavg = (countI > 0) ? sumI / countI : 0f;

        float I0avg = sharedI0.getFloat("avgI0", 0f);

        String slopeStr = fungsiPrefs.getString("slope", "1");
        String interceptStr = fungsiPrefs.getString("intercept", "0");
        String lodAwalStr = fungsiPrefs.getString("lod_awal", "0");
        String lodAkhirStr = fungsiPrefs.getString("lod_akhir", "0");

        try {
            float m = Float.parseFloat(slopeStr);
            float b = Float.parseFloat(interceptStr);
            float lodAwal = Float.parseFloat(lodAwalStr);
            float lodAkhir = Float.parseFloat(lodAkhirStr);

            if (I0avg <= 0 || Iavg <= 0 || Iavg >= I0avg) {
                tampilanCdong.setText("NaN");
                tampilanQdong.setText("Negative");
                Toast.makeText(this, "Nilai I0 atau I tidak valid", Toast.LENGTH_SHORT).show();
            } else {
                float Q = (I0avg - Iavg) / I0avg;

                if (Q <= 0) {
                    tampilanCdong.setText("NaN");
                    tampilanQdong.setText("Negative");
                    Toast.makeText(this, "Nilai Q tidak valid", Toast.LENGTH_SHORT).show();
                } else {
                    double lnQ = Math.log(Q);
                    double C = Math.exp((lnQ - b) / m);

                    if (C >= lodAwal && C <= lodAkhir) {
                        tampilanCdong.setText(String.format("%.4f", C));
                        tampilanQdong.setText("Positive");
                    } else if (C > lodAkhir) {
                        tampilanCdong.setText("");
                        tampilanQdong.setText("Positive");
                        Toast.makeText(this, "Konsentrasi tidak terdeteksi", Toast.LENGTH_SHORT).show();
                    } else if (C < lodAwal) {
                        tampilanCdong.setText("");
                        tampilanQdong.setText("Negative");
                        Toast.makeText(this, "Konsentrasi tidak terdeteksi", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        } catch (Exception e) {
            tampilanQdong.setText("Error");
            tampilanCdong.setText("Error");
            Toast.makeText(this, "Gagal menghitung C", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        backlagidan.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity5.this, MainActivitydeteksi.class);
            startActivity(intent);
            finish();
        });

        newdetection.setOnClickListener(v -> {
            SharedPreferences.Editor editor2 = sharedData2.edit();
            for (int i = 0; i < countI; i++) {
                editor2.remove("I_" + i);
            }
            editor2.remove("countI");
            editor2.remove("N");
            editor2.remove("T");
            editor2.remove("SigmaI");
            editor2.apply();

            tampilanCdong.setText("");
            tampilanQdong.setText("");

            Intent intent = new Intent(MainActivity5.this, MainActivityHome.class);
            startActivity(intent);
            finish();
        });

        exit.setOnClickListener(v -> finishAffinity());
    }
}
