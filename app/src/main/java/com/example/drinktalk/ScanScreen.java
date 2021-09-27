package com.example.drinktalk;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.core.app.ActivityCompat;
import com.google.zxing.Result;

public class ScanScreen extends AppCompatActivity {

    private CodeScanner mCodeScanner;
    private CodeScannerView scannerView;
    private String username;
    private Intent intent;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_screen);

        intent = getIntent();
        back = findViewById(R.id.btn_back);
        username = intent.getStringExtra("username");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanScreen.super.onBackPressed();
            }
        });

        scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String scanResult = result.getText();
                        String server = scanResult.substring(scanResult.indexOf("Server: ") + 8);
                        server = server.substring(0, server.indexOf(' '));
                        String time = scanResult.substring(scanResult.indexOf("Time: ") + 6);
                        Log.v("Tag", "S:"+server+" T:"+ time);

                        intent = new Intent(getApplicationContext(), Countdown.class);
                        intent.putExtra("server", server);
                        intent.putExtra("time", time);
                        intent.putExtra("username", username);
                        startActivity(intent);

                    }
                });
            }
        });

        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

}