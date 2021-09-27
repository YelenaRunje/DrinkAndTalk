package com.example.drinktalk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.WriterException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;


public class GameFragment extends Fragment{

    private TextView tv_gameTime;
    private FloatingActionButton expand;
    private Spinner spinner;
    private Button back, kreni;
    private ImageView qrCode;
    private static CountDownTimer countDownTimer = null;
    private String server_ip = "";
    private String timeString = "";
    private String server_username;
    private ListView listView;

    private List<Socket> clientSocketList = new ArrayList<>();
    private List<String> clientNameList = new ArrayList<>();
    Bundle bundle = new Bundle();

    private ServerSocket serverSocket;
    Thread serverThread = null;
    public int client_ctr = 0;
    public static final int SERVER_PORT = 3003;
    private static final String JOIN_GAME_STR = "JOIN_GAME";
    private static final String LEAVE_GAME_STR = "LEAVE_GAME";
    private static final String I_LOST_STR = "I_LOST";
    private static final String GAME_START_STR = "GAME_START";
    private static final String GAME_OVER_STR = "GAME_OVER";
    private static final String GAME_WON_STR = "GAME_WON";
    private static final long TIMER_INTERVAL = 1000;
    private ArrayAdapter<String> adapter;
    private FragmentTransaction trans;

    Bitmap bitmap;
    QRGEncoder encoder;

    public GameFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_game, container, false);
        trans = getFragmentManager().beginTransaction();

        spinner = view.findViewById(R.id.spinner);
        qrCode = view.findViewById(R.id.qrCode);
        if(getArguments()!=null){
            server_username = getArguments().getString("usernameBack");
        }

        listView = view.findViewById(R.id.listView);

        adapter = new ArrayAdapter<String>(view.getContext(), R.layout.list_item, clientNameList);
        listView.setAdapter(adapter);
        tv_gameTime = view.findViewById(R.id.tv_gameTime);
        expand = view.findViewById(R.id.fab);
        back = view.findViewById(R.id.btn_back1);
        kreni = view.findViewById(R.id.btn_kreni);

        expand.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                bundle.putSerializable("participants", (Serializable)clientNameList);
                bundle.putString("username", server_username);
                ParticipantsFragment participantsFragment = new ParticipantsFragment();
                participantsFragment.setArguments(bundle);
                trans.replace(R.id.placeholder, participantsFragment);
                trans.addToBackStack(null);
                trans.commit();
            }
        });

        kreni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Countdown.class);
                intent.putExtra("username", server_username);
                intent.putExtra("time", timeString);
                startActivity(intent);


                new CountDownTimer(3000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        // Do nothing
                    }

                    public void onFinish() {
                        sendMessage("Code:"+GAME_START_STR+" Player: ");
                        List<String> temp = new ArrayList<String>(Arrays.asList(timeString.split(" ")));
                        long hours = Long.valueOf(temp.get(0));
                        long minutes = Long.valueOf(temp.get(2));
                        Log.v("timer",hours+ " " +minutes);
                        long duration = 360000*hours + 60000*minutes+10;
                        countDownTimer = new GameFragment.MyCountDownTimer(duration, TIMER_INTERVAL);
                        countDownTimer.start();
                    }
                }.start();
            }
        });

        Context context = getContext();


        if (this.serverThread == null) {

            this.serverThread = new Thread(new ServerThread());
            this.serverThread.start();
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            server_ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            Log.v("msg","Server Started at IP: "+ server_ip);
            Toast.makeText(getContext(),"Startan server sa ip adresom"+server_ip, Toast.LENGTH_SHORT).show();
        }
        Log.v("msg", "nam:"+server_username);

        if (!clientNameList.contains(server_username)) {
            clientNameList.add(server_username);
            adapter.notifyDataSetChanged();
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("username", server_username);

                HelloFragment hello = new HelloFragment();
                hello.setArguments(bundle);
                trans.replace(R.id.placeholder, hello);
                trans.addToBackStack(null);
                trans.commit();
            }
        });

        String[] time = new String[]{
                "1 H 00 MIN",
                "1 H 15 MIN",
                "1 H 30 MIN",
                "1 H 45 MIN",
                "2 H 00 MIN"
        };

        final List<String> timesList = new ArrayList<>(Arrays.asList(time));

        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(view.getContext(), R.layout.spinner_item,timesList){

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position%2 == 1) {
                    tv.setBackgroundColor(Color.parseColor("#FFC9A3FF"));
                }
                else {
                    tv.setBackgroundColor(Color.parseColor("#FFAF89E5"));
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                String QRcontent;

                /*WindowManager manager = (WindowManager)view.getContext().getSystemService(Context.WINDOW_SERVICE);
                Display display = manager.getDefaultDisplay();

                Point point = new Point();
                display.getSize(point);*/
                int width = 720;
                int height = 1400;

                //Log.v("msg", "w:"+width+"h"+height);

                int dimen = width < height ? width : height;
                dimen = dimen * 3 / 4;

                timeString = selectedItemText;

                QRcontent = "Server: " + server_ip + " Time: " + timeString;
                encoder = new QRGEncoder(QRcontent, null, QRGContents.Type.TEXT, dimen);
                try {
                    bitmap = encoder.encodeAsBitmap();
                    qrCode.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    Log.e("Tag", e.toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
            sendMessage("Code:"+GAME_WON_STR+" Player: ");
            countDownTimer.cancel();
            serverThread.interrupt();
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }
    }

    class ServerThread implements Runnable {

        public void run() {
            Socket socket;
            try {
                serverSocket = new ServerSocket(SERVER_PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (null != serverSocket) {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        socket = serverSocket.accept();
                        CommunicationThread commThread = new CommunicationThread(socket);
                        new Thread(commThread).start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    class CommunicationThread implements Runnable {
        private BufferedReader input;

        public CommunicationThread(Socket clientSocket) {
            clientSocketList.add(clientSocket);
            try {
                this.input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
                Log.v("msg","Connection error!");
            }
            Log.v("msg","Client connected!");

            client_ctr++;
            Log.v("msg","Number of clients : " + client_ctr);
            if(client_ctr>1){
                disableSpinner();
            }
        }

        public void run() {

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String read = input.readLine();
                    String code = read.substring(read.indexOf("Code:") + 5);
                    code = code.substring(0, code.indexOf(' '));
                    String player = read.substring(read.indexOf("Player:") + 7);

                    if (code.equals(LEAVE_GAME_STR)) {
                        Thread.interrupted();
                        Log.v("msg", "remove "+clientNameList);
                        clientNameList.remove(player);
                        client_ctr--;
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                                if (client_ctr == 0) {
                                    spinner.setEnabled(true);
                                }
                            }});
                        Log.v("msg","Number of clients : " + client_ctr);
                        break;
                    }

                    handleClientMessage(code,player);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    void handleClientMessage(String code, String player) {
        switch(code) {
            case I_LOST_STR:
                sendMessage("Code:"+GAME_OVER_STR+" Player:"+player);
                this.serverThread.interrupt();
                break;
            case JOIN_GAME_STR:
                if (!player.contentEquals(server_username))
                    clientNameList.add(player);
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        spinner.setEnabled(false);
                    }});
                Log.v("msg", player + " joined game.");
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != serverThread) {
            sendMessage("Disconnect");
            serverThread.interrupt();
            serverThread = null;
        }
    }

    private void sendMessage(final String message) {
        try {
            if (!clientSocketList.isEmpty()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        PrintWriter out = null;

                        for (Socket soc : clientSocketList) {
                            try {
                                out = new PrintWriter(new BufferedWriter(
                                        new OutputStreamWriter(soc.getOutputStream())),
                                        true);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            out.println(message);
                        }
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void disableSpinner(){
        spinner.setEnabled(false);
    }
}