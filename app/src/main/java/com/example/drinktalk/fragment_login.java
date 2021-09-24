package com.example.drinktalk;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class fragment_login extends Fragment {

    private Button btnLogin;
    private ImageView imgLogo;
    private EditText unos;


    public fragment_login() {
        // Required
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        btnLogin = view.findViewById(R.id.btn_prijavi_se);
        imgLogo = view.findViewById(R.id.imgLogo);
        unos = view.findViewById(R.id.et_login);
        if(getArguments()!=null){
            unos.setText(getArguments().getString("usernameBack"));
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                String username = unos.getText().toString();

                if (!username.isEmpty()) {

                    bundle.putString("username", username);

                    fragment_hello second = new fragment_hello();
                    second.setArguments(bundle);
                    FragmentTransaction trans = getFragmentManager().beginTransaction();
                    trans.replace(R.id.placeholder, second);
                    trans.addToBackStack(null);
                    trans.commit();
                }
                else {
                    Toast.makeText(v.getContext(), "Username must not be empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}