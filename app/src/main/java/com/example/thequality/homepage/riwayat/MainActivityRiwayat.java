package com.example.thequality.homepage.riwayat;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thequality.R;
import com.example.thequality.homepage.HasilDeteksi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivityRiwayat extends AppCompatActivity {

    private RecyclerView recyclerRiwayat;
    private RiwayatAdapter adapter;
    private ArrayList<HasilDeteksi> listHasil = new ArrayList<>();
    private DatabaseReference databaseRef;
    private FirebaseAuth auth;
    private Button btnKembali;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_riwayat);

        // Firebase
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        // UI
        recyclerRiwayat = findViewById(R.id.recyclerRiwayat);
        recyclerRiwayat.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RiwayatAdapter(listHasil); // ⬅ gunakan HasilDeteksi
        recyclerRiwayat.setAdapter(adapter);

        btnKembali = findViewById(R.id.btnKembali);
        btnKembali.setOnClickListener(v -> finish());

        // Ambil data
        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseRef = FirebaseDatabase.getInstance()
                    .getReference("hasil_deteksi")
                    .child(userId);

            ambilDataRiwayat();
        } else {
            Toast.makeText(this, "Silakan login untuk melihat riwayat.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void ambilDataRiwayat() {
        if (databaseRef == null) return;

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listHasil.clear();

                for (DataSnapshot dataSnap : snapshot.getChildren()) {
                    HasilDeteksi hasil = dataSnap.getValue(HasilDeteksi.class);
                    if (hasil != null) listHasil.add(hasil);
                }

                adapter.notifyDataSetChanged();

                if (listHasil.isEmpty()) {
                    Toast.makeText(MainActivityRiwayat.this, "Belum ada data riwayat.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivityRiwayat.this, "Gagal memuat: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
