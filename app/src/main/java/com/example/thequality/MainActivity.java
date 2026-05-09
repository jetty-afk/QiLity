package com.example.thequality;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.thequality.login.MainActivity9login;


public class MainActivity extends AppCompatActivity {
    private Button GetStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button teButton = findViewById(R.id.GetStarted);

        teButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity9login.class);
                startActivity(intent);
            }
        });
    }

// ...
}