package com.example.drinktalk;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CountdownFragment extends Fragment {

    private TextView tvTime;

    public CountdownFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_countdown, container, false);
        tvTime = view.findViewById(R.id.tv_Time);
        tvTime.setText(getArguments().getString("time"));
        return view;
    }
}