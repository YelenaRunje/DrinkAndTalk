package com.example.drinktalk;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.CountDownTimer;
import android.widget.ProgressBar;
import android.widget.TextView;



public class fragment_countdown extends Fragment  {

    private ProgressBar progressBar;
    private static TextView tvTimer;
    private static CountDownTimer countDownTimer = null;
    private static final long pocetak = 120 * 1000;
    private static final long interval = 1 * 1000;

    public fragment_countdown() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_odbrojavanje, container, false);

        progressBar = view.findViewById(R.id.progressBarCircle);
        tvTimer = view.findViewById(R.id.tv_timer);
        countDownTimer = new MyCountDownTimer(pocetak, interval);
        if(tvTimer!=null){
            tvTimer.setText(String.valueOf(pocetak / 1000));
        }
        countDownTimer.start();

        return view;
    }

    public class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
            if(tvTimer!=null){

                tvTimer.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);

            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if(tvTimer!=null){
                tvTimer.setText("" + millisUntilFinished / 1000);
                progressBar.setProgress((int)(millisUntilFinished/1000));
            }
        }


    }
    private void setProgressBarValues() {

        progressBar.setMax((int) pocetak / 1000);
        progressBar.setProgress((int) interval / 1000);
    }



}