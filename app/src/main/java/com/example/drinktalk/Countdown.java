package com.example.drinktalk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
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

public class Countdown extends AppCompatActivity {
    private ProgressBar progressBar;
    private static TextView tvTimer;
    private static CountDownTimer countDownTimer = null;
    private static final long pocetak = 10 * 1000;
    private static final long interval = 1 * 1000;
    private static final String JOIN_GAME_STR = "JOIN_GAME:";
    private static final String I_LOST_STR = "I_LOST:";
    private static final String GAME_OVER_STR = "GAME_OVER:";
    private static final String GAME_WON_STR = "GAME_WON:";

    public static final int SERVERPORT = 3003;
    private ClientThread clientThread;
    private Thread thread;
    private String server_ip;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown);

        server_ip = getIntent().getStringExtra("server");
        username = getIntent().getStringExtra("username");

        String time = getIntent().getStringExtra("time");

        Log.v("Tag", "Server:"+server_ip+"Time:"+ time);

        Log.v("ivana", "Connecting to Server...");
        clientThread = new ClientThread();
        thread = new Thread(clientThread);
        thread.start();
        Log.v("ivana", "Connected to Server...");

        clientThread.sendMessage(JOIN_GAME_STR+username);

        progressBar = findViewById(R.id.progressBarCircle);
        tvTimer = findViewById(R.id.tv_timer);
        countDownTimer = new MyCountDownTimer(pocetak, interval);
        if(tvTimer!=null){
            tvTimer.setText(String.valueOf(pocetak / 1000));
        }

        // trigger eventova i kad se dogode clientThread.sendmsg(I_LOST_STR+username)

        //On game starrt
        //countDownTimer.start();
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
                        Log.v("ivana",message);
                        break;
                    }
                    // isto provejrit sta je server posla, game over, game won, itd
                    Log.v("ivana","Server: " + message);
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
                            PrintWriter out = new PrintWriter(new BufferedWriter(
                                    new OutputStreamWriter(socket.getOutputStream())),
                                    true);
                            out.println(message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != clientThread) {
            clientThread.sendMessage("Disconnect");
            clientThread = null;
        }
    }
}