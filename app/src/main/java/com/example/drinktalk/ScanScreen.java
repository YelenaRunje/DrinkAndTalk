package com.example.drinktalk;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.drinktalk.databinding.ActivityScanScreenBinding;
import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import static android.Manifest.permission.CAMERA;

public class ScanScreen extends AppCompatActivity {
    private CodeScanner mCodeScanner;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_screen);

        Intent intent = getIntent();
        Button back = findViewById(R.id.btn_back);

        username = intent.getStringExtra("username");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanScreen.super.onBackPressed();
            }
        });
        CodeScannerView scannerView = findViewById(R.id.scanner_view);

        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String scanResult = result.getText();
                        Toast.makeText(getApplicationContext(), scanResult, Toast.LENGTH_SHORT).show();
                        String server = scanResult.substring(scanResult.indexOf("Server: ") + 8);
                        String time = scanResult.substring(scanResult.indexOf("Time: ") + 6);
                        Log.v("Tag", "S:"+server+"T:"+ time);

                        Intent intent = new Intent(getApplicationContext(), Countdown.class);
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