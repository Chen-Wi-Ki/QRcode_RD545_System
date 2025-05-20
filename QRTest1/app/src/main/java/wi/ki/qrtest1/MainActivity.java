package wi.ki.qrtest1;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.vision.CameraSource;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private SurfaceView surfaceView;
    private TextView textView;
    private CameraSource cameraSource;

    public Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        surfaceView = findViewById(R.id.surfaceView);
        textView = findViewById(R.id.textView);
        Intent intent = new Intent(this, MeasurementActivity.class);
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE).build();
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>(){

            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCodes=detections.getDetectedItems();
                if(qrCodes.size()!=0){
                    textView.post(() -> textView.setText(qrCodes.valueAt(0).displayValue));
                }
            }
        });
        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                //.setRequestedPreviewSize(300, 300) // You can customize the preview window content size
                .setAutoFocusEnabled(true) // Autofocus
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED)
                    return;
                try {
                    cameraSource.start(surfaceHolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });

        getPermissionCamera();
    }
    private static final int REQUEST_CAMERA_PERMISSION = 1;

    /**
     * camera permission get
     */
    public void getPermissionCamera() {
        // if have camera permission, no need to ask again.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            //If pc has been denied permission by a user, pc can remind the user why the permission is needed here.
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("需要相機權限")
                    .setMessage("需要相機權限才能掃描 QR Code，請授予相機權限")
                    .setPositiveButton("OK", (dialog, which) -> {
                                // Show the permission grant window.
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                            }
                    )
                    .show();
        } else {
            // The first time the user is asked to call.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }
    /**
     * Result of camera permission
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // User consent permissions.
                    Toast.makeText(this, "已取得相機權限", Toast.LENGTH_SHORT).show();
                } else {
                    // User denied permission.
                    Toast.makeText(this, "未取得相機權限", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    public void BtnClick(View v)
    {
        Intent intent = new Intent(this, MeasurementActivity.class);
        intent.putExtra("key", textView.getText());
        startActivity(intent);

        /*
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(
                "wi.ki.rd545cmd1", // APP2 的 package name
                "wi.ki.rd545cmd1.MeasurementActivity" // APP2 的完整 Activity 名稱
        ));
        String[] parts = textView.getText().toString().split("\\|");
        intent.putExtra("Phone",parts[0]);
        intent.putExtra("Area",parts[1]);
        intent.putExtra("Gender",parts[2]);
        intent.putExtra("Birthday",parts[3]);
        intent.putExtra("Height",parts[4]);
        intent.putExtra("Weight",parts[5]);
        intent.putExtra("ClothesWeight",parts[6]);
        startActivity(intent);
        */
    }
}