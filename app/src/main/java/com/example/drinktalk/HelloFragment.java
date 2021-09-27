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

public class HelloFragment extends Fragment implements View.OnClickListener {

    private TextView username;
    private Button back, scan;
    private MaterialButton kreiraj;


    public HelloFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_hello, container, false);
        username = view.findViewById(R.id.tv_hellouUser);
        username.setText(getArguments().getString("username"));

        back = view.findViewById(R.id.btn_back);
        kreiraj = view.findViewById(R.id.btn_prijavi_se);
        scan = view.findViewById(R.id.btn_prijavi_u_postojecu);

        back.setOnClickListener(this);
        kreiraj.setOnClickListener(this);
        scan.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction trans = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("usernameBack", username.getText().toString());

        switch (v.getId()) {

            case R.id.btn_back:
                LoginFragment login = new LoginFragment();
                login.setArguments(bundle);
                trans.replace(R.id.placeholder, login);
                trans.addToBackStack(null);
                trans.commit();
                break;

            case R.id.btn_prijavi_se:
                GameFragment game = new GameFragment();
                game.setArguments(bundle);
                trans.replace(R.id.placeholder, game);
                trans.addToBackStack(null);
                trans.commit();
                break;

            case R.id.btn_prijavi_u_postojecu:
                Intent intent = new Intent(getContext(), ScanScreen.class);
                intent.putExtra("username", username.getText().toString());
                startActivity(intent);
        }
    }
}