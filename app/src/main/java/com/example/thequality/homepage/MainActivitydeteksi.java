package com.example.thequality.homepage;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.ScaleGestureDetector;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
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
    private static final String[] REQUIRED_PERMISSIONS =
            new String[]{Manifest.permission.CAMERA};

    private PreviewView previewView;
    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;

    private TextView tvRGB, tvGray, tvPPB, tvKelayakan, tvS;
    private Button btnAmbilFoto, btnFlash;
    private Camera camera;
    private boolean isFlashOn = false;

    private ScaleGestureDetector scaleGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        previewView   = findViewById(R.id.previewView);
        tvRGB         = findViewById(R.id.tvRGB);
        tvGray        = findViewById(R.id.tvGray);
        tvPPB         = findViewById(R.id.tvPPB);
        tvKelayakan   = findViewById(R.id.tvKelayakan);
        tvS           = findViewById(R.id.tvS);
        btnAmbilFoto  = findViewById(R.id.btnAmbilFoto);
        btnFlash      = findViewById(R.id.btnFlash);

        cameraExecutor = Executors.newSingleThreadExecutor();

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS
            );
        }

        btnAmbilFoto.setOnClickListener(v -> ambilFotoDanAnalisis());
        btnFlash.setOnClickListener(v -> toggleFlash());

        scaleGestureDetector = new ScaleGestureDetector(this,
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    @Override
                    public boolean onScale(ScaleGestureDetector detector) {
                        if (camera == null) return false;
                        float currentZoom = camera.getCameraInfo()
                                .getZoomState().getValue().getZoomRatio();
                        float scaleFactor = detector.getScaleFactor();
                        float newZoom = currentZoom * scaleFactor;
                        camera.getCameraControl().setZoomRatio(newZoom);
                        return true;
                    }
                });

        previewView.setOnTouchListener((v, event) -> {
            scaleGestureDetector.onTouchEvent(event);
            return true;
        });
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

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
                camera = cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        imageCapture
                );

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void ambilFotoDanAnalisis() {
        if (imageCapture == null) return;

        imageCapture.takePicture(
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy imageProxy) {
                        Bitmap bitmap = imageToBitmap(imageProxy);
                        imageProxy.close();

                        Bitmap croppedROI = cropToROI(bitmap);

                        int[] avgRGB = getAverageRGB(croppedROI);
                        int r = avgRGB[0];
                        int g = avgRGB[1];
                        int b = avgRGB[2];

                        analyzeColor(r, g, b);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(
                                MainActivitydeteksi.this,
                                "Gagal memproses gambar",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }

    private Bitmap imageToBitmap(ImageProxy image) {
        ImageProxy.PlaneProxy plane = image.getPlanes()[0];
        ByteBuffer buffer = plane.getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        Matrix matrix = new Matrix();
        matrix.postRotate(image.getImageInfo().getRotationDegrees());
        return Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.getWidth(),
                bitmap.getHeight(),
                matrix,
                true
        );
    }

    private Bitmap cropToROI(Bitmap bitmap) {
        int width  = bitmap.getWidth();
        int height = bitmap.getHeight();

        double ratio = 80.0 / 260.0;
        int side = (int) (Math.min(width, height) * ratio);

        int left = (width  - side) / 2;
        int top  = (height - side) / 2;

        return Bitmap.createBitmap(bitmap, left, top, side, side);
    }

    private int[] getAverageRGB(Bitmap bitmap) {
        long rSum = 0, gSum = 0, bSum = 0;
        int count = 0;

        for (int x = 0; x < bitmap.getWidth(); x += 5) {
            for (int y = 0; y < bitmap.getHeight(); y += 5) {
                int pixel = bitmap.getPixel(x, y);
                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;

                rSum += r;
                gSum += g;
                bSum += b;
                count++;
            }
        }

        int rAvg = (int) (rSum / count);
        int gAvg = (int) (gSum / count);
        int bAvg = (int) (bSum / count);

        return new int[]{rAvg, gAvg, bAvg};
    }

    private void analyzeColor(int r, int g, int b) {
        int gray = (int) (0.299 * r + 0.587 * g + 0.114 * b);

        int x = g;
        int x0 = 200;
        int xRef = 255;

        double denominator = (double) (x0 - xRef);
        double S = 0.0;
        if (denominator != 0) {
            S = 100.0 * (x - x0) / denominator;
        }

        double m = 100.0;
        double n = 0.0;

        double cGasPpm = 0.0;
        if (m != 0) {
            double logc = (S - n) / m;
            cGasPpm = Math.pow(10.0, logc);
        }

        double cGasPpb = cGasPpm * 1000.0;

        double finalSGas = S;
        double finalPPB  = cGasPpb;
        int finalGray    = gray;

        runOnUiThread(() -> {
            tvRGB.setText(String.format("Nilai RGB: (%d, %d, %d)", r, g, b));
            tvGray.setText("Nilai Gray: " + finalGray);
            tvS.setText(String.format("Respons S: %.2f %%", finalSGas));
            tvPPB.setText(String.format("Kadar gas (estimasi): %.2f ppb", finalPPB));

            if (finalGray < 100) {
                tvKelayakan.setText("Status: Buruk / Pekat");
                tvKelayakan.setTextColor(
                        ContextCompat.getColor(
                                MainActivitydeteksi.this,
                                android.R.color.holo_red_dark
                        )
                );
            } else {
                tvKelayakan.setText("Status: Baik / Layak");
                tvKelayakan.setTextColor(
                        ContextCompat.getColor(
                                MainActivitydeteksi.this,
                                android.R.color.holo_green_dark
                        )
                );
            }
        });
    }

    private void toggleFlash() {
        if (camera == null || !camera.getCameraInfo().hasFlashUnit()) return;

        isFlashOn = !isFlashOn;
        camera.getCameraControl().enableTorch(isFlashOn);

        if (isFlashOn) {
            btnFlash.setText("Flash: ON");
        } else {
            btnFlash.setText("Flash: OFF");
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
            ) != PackageManager.PERMISSION_GRANTED) {
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