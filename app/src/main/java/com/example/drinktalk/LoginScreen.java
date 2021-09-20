package com.example.drinktalk;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;


public class LoginScreen extends AppCompatActivity {

    ImageView imgLogo;
    Button btnPrijaviSe;
    EditText etLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);


        imgLogo=findViewById(R.id.imgLogo);
        btnPrijaviSe=findViewById(R.id.btn_prijavi_se);
        etLogin=findViewById(R.id.et_login);


    }
}