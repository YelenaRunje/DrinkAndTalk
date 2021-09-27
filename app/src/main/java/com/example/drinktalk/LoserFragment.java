package com.example.drinktalk;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class LoserFragment extends Fragment {

    private String loserStr = "";
    private Button newGame;
    private TextView loser;
    private FragmentTransaction trans;

    public LoserFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_loser, container, false);

        if(getArguments()!=null){
            loserStr = getArguments().getString("loser");
        }

        loser = view.findViewById(R.id.loser);
        loser.setText(loserStr);

        newGame = view.findViewById(R.id.btn_newGame);
        newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trans = getFragmentManager().beginTransaction();
                trans.replace(R.id.placeholder, new LoginFragment());
                trans.addToBackStack(null);
                trans.commit();
            }
        });
        return view;
    }
}