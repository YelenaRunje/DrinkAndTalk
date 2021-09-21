package com.example.drinktalk;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

        btnPrijaviSe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId()==R.id.btn_prijavi_se){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FragmentHellou()).commit();

                    // e ovde sad triba ono iz login.screen.xml NEKAKO SAKRIT setvisible stogod// JA sam podilila u dva relativa ,
                    // i fragment container je ono di se triba minjat , jer ovo sve ostaje isto -mozda bi bilo bolje minjat cili
                    //screen, nezz??! ovako je uglavnom sve pomisano :D
                    //al aj funkcionira ovo s fragmentima, samo pazi , na iducem se zove fragment iz fragmenta
                }
            }

        });

    }
}