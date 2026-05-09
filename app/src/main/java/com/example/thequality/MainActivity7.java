package com.example.thequality; // Ganti dengan nama package kamu

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity7 extends AppCompatActivity {

    EditText editSlope, editIntercept, editLODawal, editLODakhir;
    Button saveButton, deleteButton, backButton, startDetectionButton;

    SharedPreferences sharedPreferences;
    public static final String PREF_NAME = "fungsiPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main7); // Ganti jika nama file layout XML kamu berbeda

        // Inisialisasi
        editSlope = findViewById(R.id.tampilkanslope);
        editIntercept = findViewById(R.id.tampilkanintercept);
        editLODawal = findViewById(R.id.tampilkanLODawal);
        editLODakhir = findViewById(R.id.tampilkanLODakhir);

        saveButton = findViewById(R.id.savefungsi);
        deleteButton = findViewById(R.id.deletefungsi);
        backButton = findViewById(R.id.backlagiButton);
        startDetectionButton = findViewById(R.id.startdetectionnButton);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // SAVE BUTTON
        saveButton.setOnClickListener(v -> {
            String slope = editSlope.getText().toString().trim();
            String intercept = editIntercept.getText().toString().trim();
            String lodAwal = editLODawal.getText().toString().trim();
            String lodAkhir = editLODakhir.getText().toString().trim();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("slope", slope);
            editor.putString("intercept", intercept);
            editor.putString("lod_awal", lodAwal);
            editor.putString("lod_akhir", lodAkhir);
            editor.apply();

            Toast.makeText(MainActivity7.this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
        });

        // DELETE BUTTON
        deleteButton.setOnClickListener(v -> {
            boolean hasData = sharedPreferences.contains("slope") ||
                    sharedPreferences.contains("intercept") ||
                    sharedPreferences.contains("lod_awal") ||
                    sharedPreferences.contains("lod_akhir");

            if (hasData) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("slope");
                editor.remove("intercept");
                editor.remove("lod_awal");
                editor.remove("lod_akhir");
                editor.apply();

                // Kosongkan juga EditText-nya
                editSlope.setText("");
                editIntercept.setText("");
                editLODawal.setText("");
                editLODakhir.setText("");

                Toast.makeText(MainActivity7.this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity7.this, "Tidak ada data yang dihapus", Toast.LENGTH_SHORT).show();
            }
        });

        // BACK BUTTON
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity7.this, MainActivity6.class);
            startActivity(intent);
            finish();
        });

        // START DETECTION BUTTON
        startDetectionButton.setOnClickListener(v -> {
        });
    }
}
