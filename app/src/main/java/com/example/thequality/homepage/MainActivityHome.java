package com.example.thequality.homepage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.thequality.MainActivity2;
import com.example.thequality.R;
import com.example.thequality.homepage.riwayat.MainActivityRiwayat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivityHome extends AppCompatActivity {

    private TextView tvWelcome;
    private CardView cardDeteksi, cardPublik, cardProfil, cardRiwayat;
    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainhome);

        mAuth = FirebaseAuth.getInstance();

        tvWelcome = findViewById(R.id.tvWelcome);
        cardDeteksi = findViewById(R.id.cardDeteksi);
        cardPublik = findViewById(R.id.cardPublik);
        cardProfil = findViewById(R.id.cardProfil);
        cardRiwayat = findViewById(R.id.cardRiwayat); // ← tambahkan inisialisasi

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            String username = userEmail != null ? userEmail.split("@")[0] : "Pengguna";
            tvWelcome.setText("Halo, " + username + " 👋");
        } else {
            tvWelcome.setText("Halo, Pengguna 👋");
        }

        cardDeteksi.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivityHome.this, MainActivitydeteksi.class);
            startActivity(intent);
        });

        cardPublik.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivityHome.this,MainActivitydeteksi.class);
            startActivity(intent);
        });

        cardProfil.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivityHome.this, MainActivity2.class);
            startActivity(intent);
        });

        cardRiwayat.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivityHome.this, MainActivityRiwayat.class);
            startActivity(intent);
        });

    }
}
