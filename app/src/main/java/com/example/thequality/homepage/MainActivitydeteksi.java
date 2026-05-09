package com.example.thequality.homepage;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.thequality.R;
import com.google.common.util.concurrent.ListenableFuture;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivitydeteksi extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};

    private PreviewView previewView;
    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;

    private TextView tvRGB, tvGray, tvPPB, tvKelayakan;
    private Button btnAmbilFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);


        previewView = findViewById(R.id.previewView);
        tvRGB = findViewById(R.id.tvRGB);
        tvGray = findViewById(R.id.tvGray);
        tvPPB = findViewById(R.id.tvPPB);
        tvKelayakan = findViewById(R.id.tvKelayakan);
        btnAmbilFoto = findViewById(R.id.btnAmbilFoto);

        cameraExecutor = Executors.newSingleThreadExecutor();

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        btnAmbilFoto.setOnClickListener(v -> ambilFotoDanAnalisis());
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void ambilFotoDanAnalisis() {
        if (imageCapture == null) return;

        imageCapture.takePicture(ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy imageProxy) {
                        Bitmap bitmap = imageToBitmap(imageProxy);
                        imageProxy.close();

                        // 1. Potong gambar tepat di area ROI (tengah)
                        Bitmap croppedROI = cropToROI(bitmap);

                        // 2. Hitung RGB Rata-rata
                        int[] avgRGB = getAverageRGB(croppedROI);
                        int r = avgRGB[0];
                        int g = avgRGB[1];
                        int b = avgRGB[2];

                        // 3. Hitung Grayscale & Rumus CO2 (Ganti dengan rumus skripsi kamu)
                        int gray = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                        double co2Result = (255 - gray) * 2.5; // Contoh rumus linear

                        runOnUiThread(() -> {
                            tvRGB.setText(String.format("Nilai RGB: (%d, %d, %d)", r, g, b));
                            tvGray.setText("Nilai Gray: " + gray);
                            tvPPB.setText(String.format("Kadar CO₂: %.2f ppb", co2Result));

                            // Indikator sederhana
                            if (gray < 100) {
                                tvKelayakan.setText("Status: Buruk / Pekat");
                                tvKelayakan.setTextColor(ContextCompat.getColor(MainActivitydeteksi.this, android.R.color.holo_red_dark));
                            } else {
                                tvKelayakan.setText("Status: Baik / Layak");
                                tvKelayakan.setTextColor(ContextCompat.getColor(MainActivitydeteksi.this, android.R.color.holo_green_dark));
                            }
                        });
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(MainActivitydeteksi.this, "Gagal memproses gambar", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private Bitmap imageToBitmap(ImageProxy image) {
        ImageProxy.PlaneProxy plane = image.getPlanes()[0];
        ByteBuffer buffer = plane.getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        // Rotasi otomatis sesuai orientasi HP
        Matrix matrix = new Matrix();
        matrix.postRotate(image.getImageInfo().getRotationDegrees());
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private Bitmap cropToROI(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // ROI 80dp dari Preview 260dp = ~30% dari lebar gambar
        double ratio = 80.0 / 260.0;
        int side = (int) (Math.min(width, height) * ratio);

        int left = (width - side) / 2;
        int top = (height - side) / 2;

        return Bitmap.createBitmap(bitmap, left, top, side, side);
    }

    private int[] getAverageRGB(Bitmap bitmap) {
        long r = 0, g = 0, b = 0;
        int count = 0;

        for (int x = 0; x < bitmap.getWidth(); x += 5) {
            for (int y = 0; y < bitmap.getHeight(); y += 5) {
                int pixel = bitmap.getPixel(x, y);
                r += (pixel >> 16) & 0xFF;
                g += (pixel >> 8) & 0xFF;
                b += pixel & 0xFF;
                count++;
            }
        }
        return new int[]{(int)(r/count), (int)(g/count), (int)(b/count)};
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}