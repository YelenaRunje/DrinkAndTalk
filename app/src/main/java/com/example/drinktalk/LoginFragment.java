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

public class LoginFragment extends Fragment {

    private Button btnLogin;
    private ImageView imgLogo;
    private EditText input;


    public LoginFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        btnLogin = view.findViewById(R.id.btn_prijavi_se);
        imgLogo = view.findViewById(R.id.imgLogo);
        input = view.findViewById(R.id.et_login);

        if(getArguments()!=null){
            input.setText(getArguments().getString("usernameBack"));
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                String username = input.getText().toString();

                if (!username.isEmpty()) {
                    FragmentTransaction trans = getFragmentManager().beginTransaction();
                    bundle.putString("username", username);
                    HelloFragment hello = new HelloFragment();
                    hello.setArguments(bundle);
                    trans.replace(R.id.placeholder, hello);
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