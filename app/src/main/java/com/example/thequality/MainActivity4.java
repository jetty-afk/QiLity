package com.example.thequality;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thequality.homepage.HasilDeteksi;
import com.example.thequality.homepage.riwayat.RiwayatAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity4 extends AppCompatActivity {

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
        FirebaseUser user = auth.getCurrentUser();

        // RecyclerView
        recyclerRiwayat = findViewById(R.id.recyclerRiwayat);
        recyclerRiwayat.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RiwayatAdapter(listHasil);
        recyclerRiwayat.setAdapter(adapter);

        btnKembali = findViewById(R.id.btnKembali);
        btnKembali.setOnClickListener(v -> finish());

        if (user != null) {
            String userId = user.getUid();
            databaseRef = FirebaseDatabase.getInstance()
                    .getReference("hasil_deteksi")
                    .child(userId);

            ambilDataRiwayat();
        } else {
            Toast.makeText(this, "Silakan login dulu", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void ambilDataRiwayat() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                listHasil.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    HasilDeteksi hasil = ds.getValue(HasilDeteksi.class);
                    if (hasil != null) listHasil.add(hasil);
                }

                adapter.notifyDataSetChanged();

                if (listHasil.isEmpty()) {
                    Toast.makeText(MainActivity4.this,
                            "Belum ada data riwayat.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity4.this,
                        "Gagal memuat data: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
