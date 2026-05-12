package com.example.thequality;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;

public class MainActivity5 extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_STORAGE_PERMISSION = 101;

    private ImageView imageViewSensor;
    private TextView textViewResult;
    private Button buttonAnalyze;
    private Uri currentImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main31);

        imageViewSensor = findViewById(R.id.imageViewSensor);
        textViewResult = findViewById(R.id.textViewResult);
        buttonAnalyze = findViewById(R.id.buttonAnalyze);

        Button buttonTakePhoto = findViewById(R.id.buttonTakePhoto);
        Button buttonSelectImage = findViewById(R.id.buttonSelectImage);

        buttonTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermission();
            }
        });

        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkStoragePermission();
            }
        });
        buttonAnalyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                analyzeSensorColor();
            }
        });
    }
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            dispatchTakePictureIntent();
        }
    }

    private void checkStoragePermission() {
        String permission;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {

            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {

            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, REQUEST_STORAGE_PERMISSION);
        } else {
            dispatchPickImageIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Izin kamera ditolak.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchPickImageIntent();
            } else {
                Toast.makeText(this, "Izin penyimpanan ditolak.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void dispatchPickImageIntent() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imageViewSensor.setImageBitmap(imageBitmap);
                textViewResult.setText("Kadar CO2: Siap dianalisis");
            } else if (requestCode == REQUEST_IMAGE_PICK) {

                currentImageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), currentImageUri);
                    imageViewSensor.setImageBitmap(bitmap);
                    textViewResult.setText("Kadar CO2: Siap dianalisis");
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Gagal memuat gambar", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void analyzeSensorColor() {
        if (imageViewSensor.getDrawable() == null) {
            Toast.makeText(this, "Harap ambil atau pilih foto sensor terlebih dahulu.", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap bitmap = ((BitmapDrawable) imageViewSensor.getDrawable()).getBitmap();

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int startX = (int) (width * 0.4);
        int startY = (int) (height * 0.4);
        int endX = (int) (width * 0.6);
        int endY = (int) (height * 0.6);

        long totalRed = 0;
        long totalGreen = 0;
        long totalBlue = 0;
        int pixelCount = 0;

        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                int pixel = bitmap.getPixel(x, y);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = pixel & 0xff;

                totalRed += red;
                totalGreen += green;
                totalBlue += blue;
                pixelCount++;
            }
        }

        if (pixelCount == 0) {
            textViewResult.setText("Kadar CO2: Gagal menganalisis piksel.");
            return;
        }

        int avgRed = (int) (totalRed / pixelCount);
        int avgGreen = (int) (totalGreen / pixelCount);
        int avgBlue = (int) (totalBlue / pixelCount);
        double colorRatio = (double) (avgRed + avgGreen) / (2 * avgBlue);

        double estimatedCO2 = 3000 * colorRatio - 1100; // Formula hipotetik

        estimatedCO2 = Math.max(400, estimatedCO2);
        estimatedCO2 = Math.min(5000, estimatedCO2);

        String resultText = String.format("Kadar CO2: %.0f ppm\n(Rata-rata RGB: R=%d, G=%d, B=%d)",
                estimatedCO2, avgRed, avgGreen, avgBlue);

        textViewResult.setText(resultText);

        int colorDisplay = android.graphics.Color.rgb(avgRed, avgGreen, avgBlue);
        textViewResult.setBackgroundColor(colorDisplay);

        Toast.makeText(this, "Analisis selesai. Rasio Warna: " + String.format("%.2f", colorRatio), Toast.LENGTH_LONG).show();
    }
}