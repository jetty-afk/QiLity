package com.example.thequality;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity6 extends AppCompatActivity {

    private TextView tampilkanIavg;
    private Button deleteButton;
    private Button backButton;
    private Button linearFittingButton;
    private Button simpaniavg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);

        tampilkanIavg = findViewById(R.id.tampilkanIavg);
        deleteButton = findViewById(R.id.deleteButton);
        backButton = findViewById(R.id.backButton);
        linearFittingButton = findViewById(R.id.linearFittingButton);
        simpaniavg = findViewById(R.id.simpaniavg);

        tampilkanRataRataI0();
        tampilkanNilaiICT();

        simpaniavg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getSharedPreferences("MyData", MODE_PRIVATE);
                int countI0 = sharedPref.getInt("countI0", 0);

                if (countI0 == 0) {
                    Toast.makeText(MainActivity6.this, "Data I0 kosong, tidak bisa disimpan", Toast.LENGTH_SHORT).show();
                    return;
                }

                float avgI0 = hitungAvgI0(sharedPref, countI0);

                SharedPreferences prefI0 = getSharedPreferences("DataI0", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefI0.edit();
                editor.putFloat("avgI0", avgI0);
                editor.apply();

                Toast.makeText(MainActivity6.this, "Nilai avgI0 berhasil disimpan", Toast.LENGTH_SHORT).show();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hapusData();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity6.this, MainActivity4.class);
                startActivity(intent);
                finish();
            }
        });

        linearFittingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getSharedPreferences("MyData", MODE_PRIVATE);
                int countData = sharedPref.getInt("countData", 0);
                int countI0 = sharedPref.getInt("countI0", 0);

                if (countData == 0 || countI0 == 0) {
                    Toast.makeText(MainActivity6.this, "Data belum lengkap", Toast.LENGTH_SHORT).show();
                    return;
                }

                float avgI0 = hitungAvgI0(sharedPref, countI0);

                SharedPreferences prefI0 = getSharedPreferences("DataI0", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefI0.edit();
                editor.putFloat("avgI0", avgI0);
                editor.apply();

                ArrayList<Double> concentrations = new ArrayList<>();
                ArrayList<Double> quenchingRatios = new ArrayList<>();

                for (int i = 0; i < countData; i++) {
                    if (!sharedPref.contains("I_" + i) || !sharedPref.contains("C_" + i)) continue;

                    float nilaiI = sharedPref.getFloat("I_" + i, 0);
                    float nilaiC = sharedPref.getFloat("C_" + i, 0);
                    float qr = avgI0 != 0 ? (avgI0 - nilaiI) / avgI0 : 0;

                    if (nilaiC > 0) {
                        concentrations.add((double) nilaiC);
                        quenchingRatios.add((double) qr);
                    }
                }

                Intent intent = new Intent(MainActivity6.this, MainActivity7.class);
                intent.putExtra("concentrationList", concentrations);
                intent.putExtra("quenchingRatioList", quenchingRatios);
                startActivity(intent);
            }
        });
    }

    private float hitungAvgI0(SharedPreferences sharedPref, int countI0) {
        float totalI0 = 0;
        for (int i = 0; i < countI0; i++) {
            totalI0 += sharedPref.getFloat("I0_" + i, 0);
        }
        return countI0 > 0 ? totalI0 / countI0 : 0;
    }

    private void tampilkanRataRataI0() {
        SharedPreferences sharedPref = getSharedPreferences("MyData", MODE_PRIVATE);
        int countI0 = sharedPref.getInt("countI0", 0);

        if (countI0 == 0) {
            tampilkanIavg.setText("Data kosong");
            return;
        }

        float avgI0 = hitungAvgI0(sharedPref, countI0);
        tampilkanIavg.setText(String.format("%.4f", avgI0));
    }

    private void tampilkanNilaiICT() {
        SharedPreferences sharedPref = getSharedPreferences("MyData", MODE_PRIVATE);
        int countData = sharedPref.getInt("countData", 0);
        int countI0 = sharedPref.getInt("countI0", 0);

        if (countData == 0 || countI0 == 0) {
            kosongkanTabel();
            return;
        }

        float avgI0 = hitungAvgI0(sharedPref, countI0);

        for (int i = 0; i < countData && i < 10; i++) {
            if (!sharedPref.contains("I_" + i) || !sharedPref.contains("C_" + i) || !sharedPref.contains("T_" + i))
                continue;

            float nilaiI = sharedPref.getFloat("I_" + i, 0);
            float nilaiC = sharedPref.getFloat("C_" + i, 0);
            float nilaiT = sharedPref.getFloat("T_" + i, 0);
            float nilaiQ = avgI0 != 0 ? (avgI0 - nilaiI) / avgI0 : 0;

            int idI = getResources().getIdentifier("textI" + (i + 1), "id", getPackageName());
            int idC = getResources().getIdentifier("textC" + (i + 1), "id", getPackageName());
            int idT = getResources().getIdentifier("textT" + (i + 1), "id", getPackageName());
            int idQ = getResources().getIdentifier("textQ" + (i + 1), "id", getPackageName());

            TextView textI = findViewById(idI);
            TextView textC = findViewById(idC);
            TextView textT = findViewById(idT);
            TextView textQ = findViewById(idQ);

            if (textI != null) textI.setText(String.format("%.4f", nilaiI));
            if (textC != null) textC.setText(String.format("%.4f", nilaiC));
            if (textT != null) textT.setText(String.format("%.4f", nilaiT));
            if (textQ != null) textQ.setText(String.format("%.4f", nilaiQ));
        }

        for (int i = countData; i < 10; i++) {
            kosongkanBaris(i + 1);
        }
    }

    private void hapusData() {
        SharedPreferences sharedPref = getSharedPreferences("MyData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();

        tampilkanIavg.setText("Data kosong");
        kosongkanTabel();
    }

    private void kosongkanTabel() {
        for (int i = 1; i <= 10; i++) {
            kosongkanBaris(i);
        }
    }

    private void kosongkanBaris(int baris) {
        int idI = getResources().getIdentifier("textI" + baris, "id", getPackageName());
        int idC = getResources().getIdentifier("textC" + baris, "id", getPackageName());
        int idT = getResources().getIdentifier("textT" + baris, "id", getPackageName());
        int idQ = getResources().getIdentifier("textQ" + baris, "id", getPackageName());

        TextView textI = findViewById(idI);
        TextView textC = findViewById(idC);
        TextView textT = findViewById(idT);
        TextView textQ = findViewById(idQ);

        if (textI != null) textI.setText("");
        if (textC != null) textC.setText("");
        if (textT != null) textT.setText("");
        if (textQ != null) textQ.setText("");
    }
}
