package com.example.drinktalk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Countdown extends AppCompatActivity {

    private ProgressBar progressBar;
    private String time;
    private TextView tvTime;
    private static TextView tvTimer;
    private static CountDownTimer countDownTimer = null;
    private static final long pocetak = 10000;
    private static final long interval = 1000;
    private static final String JOIN_GAME_STR = "JOIN_GAME";
    private static final String LEAVE_GAME_STR = "LEAVE_GAME";
    private static final String I_LOST_STR = "I_LOST";
    private static final String GAME_START_STR = "GAME_START";
    private static final String GAME_OVER_STR = "GAME_OVER";
    private static final String GAME_WON_STR = "GAME_WON";

    public static final int SERVERPORT = 3003;
    private ClientThread clientThread;
    private Thread thread;
    private String server_ip;
    private String username;
    private FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown);

        server_ip = getIntent().getStringExtra("server");
        username = getIntent().getStringExtra("username");
        ft = getSupportFragmentManager().beginTransaction();

        time = getIntent().getStringExtra("time");

        Log.v("msg", "Server:"+server_ip+"Time:"+ time);

        Log.v("msg", "Connecting...");
        clientThread = new ClientThread();
        thread = new Thread(clientThread);
        thread.start();
        Log.v("msg", "Connected...");

        new CountDownTimer(1000, 1000) {
            public void onTick(long millisUntilFinished) {
                // Do nothing
            }

            public void onFinish() {
                clientThread.sendMessage("Code:"+JOIN_GAME_STR+" Player:"+username);
            }
        }.start();

        registerReceiver(new PhoneUnlockedReceiver(), new IntentFilter("android.intent.action.USER_PRESENT"));

        progressBar = findViewById(R.id.progressBarCircle);
        tvTimer = findViewById(R.id.tv_timer);
        tvTime = findViewById(R.id.tv_showTime);

        List<String> temp = new ArrayList<String>(Arrays.asList(time.split(" ")));
        tvTime.setText(temp.get(0)+" sat "+temp.get(2)+" min");

        countDownTimer = new MyCountDownTimer(pocetak, interval);
        if(tvTimer!=null){
            tvTimer.setText(String.valueOf(pocetak / 1000));
        }

    }

    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
            if(tvTimer!=null){
                Bundle bundle = new Bundle();
                bundle.putString("time", tvTime.getText().toString());
                CountdownFragment cdown = new CountdownFragment();
                cdown.setArguments(bundle);
                ft.replace(R.id.placeholder, cdown);
                ft.addToBackStack(null);
                ft.commit();
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if(tvTimer!=null){
                tvTimer.setText("" + millisUntilFinished / interval);
                progressBar.setProgress((int)(millisUntilFinished/interval));
            }
        }
    }

    class ClientThread implements Runnable {

        private Socket socket;
        private BufferedReader input;

        @Override
        public void run() {

            try {
                InetAddress serverAddr = InetAddress.getByName(server_ip);
                socket = new Socket(serverAddr, SERVERPORT);

                while (!Thread.currentThread().isInterrupted()) {

                    this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String message = input.readLine();
                    if (null == message || "Disconnect".contentEquals(message)) {
                        Thread.interrupted();
                        message = "Server Disconnected.";
                        break;
                    }

                    String code = message.substring(message.indexOf("Code:") + 5);
                    code = code.substring(0, code.indexOf(' '));
                    String player = message.substring(message.indexOf("Player:") + 7);

                    handleServerMessage(code,player);

                }

            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }

        void sendMessage(final String message) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (null != socket) {
                            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                            out.println(message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        void handleServerMessage(String code, String player) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            switch (code) {
                case GAME_WON_STR:
                    ft.replace(R.id.placeholder, new SuccessFragment());
                    ft.commit();
                    break;
                case GAME_OVER_STR:
                    Bundle bundle = new Bundle();
                    bundle.putString("loser", player);
                    LoserFragment loser = new LoserFragment();
                    loser.setArguments(bundle);
                    ft.replace(R.id.placeholder, loser);
                    ft.addToBackStack(null);
                    ft.commit();
                    break;
                case GAME_START_STR:
                    countDownTimer.start();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != clientThread) {
            clientThread.sendMessage("Code:"+LEAVE_GAME_STR+" Player:"+username);
            clientThread = null;
        }
    }

    public class PhoneUnlockedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            KeyguardManager keyguardManager = (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
            if (keyguardManager.isKeyguardSecure()) {
                clientThread.sendMessage("Code:"+I_LOST_STR+" Player:"+username); // NA POMAK MOB KOPIRAT OVO
            }
        }
    }
}