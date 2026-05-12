package com.example.thequality;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {

    private TextView userNameTextView;
    private EditText editUserName;
    private Button saveButton;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_profile);

        userNameTextView = findViewById(R.id.userName);
        editUserName = findViewById(R.id.editUserName);
        saveButton = findViewById(R.id.saveButton);
        logoutButton = findViewById(R.id.logout);

        String userName = "John Doe"; // Example static data
        userNameTextView.setText(userName);
        editUserName.setText(userName);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUserName = editUserName.getText().toString();
                if (!newUserName.isEmpty()) {
                    userNameTextView.setText(newUserName);
                } else {
                    editUserName.setError("Name cannot be empty");
                }
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Example, you might want to go back to login screen or clear user data
            }
        });
    }
}
