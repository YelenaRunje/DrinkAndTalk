package com.example.drinktalk;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;

public class fragment_hello extends Fragment implements View.OnClickListener {

    private TextView username;
    private Button back,prijavaUpostojecu;
    private MaterialButton kreiraj;

    public fragment_hello() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_hello, container, false);
        username = view.findViewById(R.id.tv_hellouUser);
        username.setText(getArguments().getString("username"));

        back = view.findViewById(R.id.btn_back);
        kreiraj = view.findViewById(R.id.btn_prijavi_se);
        prijavaUpostojecu=view.findViewById(R.id.btn_prijavi_u_postojecu);

        back.setOnClickListener(this);
        kreiraj.setOnClickListener(this);
        prijavaUpostojecu.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction trans = getFragmentManager().beginTransaction();

        switch (v.getId()) {

            case R.id.btn_back:
                Bundle bundle = new Bundle();
                bundle.putString("usernameBack", username.getText().toString());
                fragment_login first = new fragment_login();
                first.setArguments(bundle);
                trans.replace(R.id.placeholder, first);
                trans.addToBackStack(null);
                trans.commit();
                break;

            case R.id.btn_prijavi_se:
                trans.replace(R.id.placeholder, new fragment_game());
                trans.addToBackStack(null);
                trans.commit();
                break;

            case R.id.btn_prijavi_u_postojecu:
                Intent intent = new Intent(getContext(), ScanScreen.class);
                startActivity(intent);
        }
    }
}