package com.example.drinktalk;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;


public class ParticipantsFragment extends Fragment {

    private Button close;
    private ArrayList<String> participants;
    private ArrayList<String> temp = new ArrayList<String>();
    private ListView listView;
    private FragmentTransaction ft;
    private ArrayAdapter<String> adapter;
    private Bundle bundle;

    public ParticipantsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_prijavljeni, container, false);
        ft = getFragmentManager().beginTransaction();
        bundle = new Bundle();
        close = view.findViewById(R.id.btn_close);
        listView = view.findViewById(R.id.listView);

        participants = (ArrayList) getArguments().getSerializable("participants");
        for (int i=0; i<participants.size(); i++) {
            temp.add((i+1)+". "+participants.get(i));
        }
        adapter = new ArrayAdapter<String>(view.getContext(), R.layout.list_item,temp);
        listView.setAdapter(adapter);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("usernameBack", getArguments().getString("username"));
                bundle.putString("flag", "0");
                bundle.putSerializable("list", participants);
                GameFragment game = new GameFragment();
                game.setArguments(bundle);
                ft.replace(R.id.placeholder, game);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        return view;
    }
}