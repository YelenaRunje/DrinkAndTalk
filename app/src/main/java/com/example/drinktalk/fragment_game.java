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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;


public class fragment_game extends Fragment {

    private Spinner spinner;
    private Button back;
    private Button kreni;
    private ImageView qrCode;
    private String server_ip = "tempServer";
    private ListView listView;//*******
    private ArrayAdapter<String> arrayAdapter;//********
    private ArrayList<String> arrayList;//***********

    private ServerSocket serverSocket;
    private Socket tempClientSocket;
    Thread serverThread = null;
    public int client_ctr = 1;
    public static final int SERVER_PORT = 3003;
    private static final String JOIN_GAME_STR = "JOIN_GAME:";
    private static final String I_LOST_STR = "I_LOST:";
    private static final String GAME_OVER_STR = "GAME_OVER:";
    private static final String GAME_WON_STR = "GAME_WON:";


    Bitmap bitmap;
    QRGEncoder encoder;

    public fragment_game() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_game, container, false);

        spinner = view.findViewById(R.id.spinner);
        qrCode = view.findViewById(R.id.qrCode);
        listView= view.findViewById(R.id.listView);
        arrayList =new ArrayList<String>();
        arrayAdapter=new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);
        listView.setDivider(getActivity().getResources().getDrawable(R.drawable.footer_divider));
        arrayList.add("khfkjsdfhskdjf");
        arrayList.add("khfkjsdskdjf");
        arrayList.add("fffkjsdfhskdjf");
        arrayList.add("kh43sdfhskdjf");
        arrayList.add("aaaasdfhskdjf");
        arrayAdapter.notifyDataSetChanged();

        String username = getArguments().getString("usernameBack");
        back = view.findViewById(R.id.btn_back1);
        kreni = view.findViewById(R.id.btn_kreni);
        kreni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(getContext(), ScanScreen.class);
                intent.putExtra("username", username);
                startActivity(intent);*/
                TextView text = (TextView) view.findViewById(R.id.tv_gameTime);
                text.setTextColor(Color.parseColor("#CC8A06"));


                //TODO
            }
        });

        this.serverThread = new Thread(new ServerThread());
        this.serverThread.start();
        Context context = getContext();
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        server_ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        Log.v("ivana","Server Started at IP: "+ server_ip);
        Toast.makeText(getContext(),"STartan server sa ip adresom"+server_ip, Toast.LENGTH_SHORT).show();


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction trans = getFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString("username", username);

                fragment_hello hello = new fragment_hello();
                hello.setArguments(bundle);
                trans.replace(R.id.placeholder, hello);
                trans.addToBackStack(null);
                trans.commit();
            }
        });

        String[] plants = new String[]{
                "1 H 00 MIN",
                "1 H 15 MIN",
                "1 H 30 MIN",
                "1 H 45 MIN",
                "2 H 00 MIN"
        };

        final List<String> plantsList = new ArrayList<>(Arrays.asList(plants));

        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(view.getContext(),R.layout.spinner_item,plantsList){

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
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

                WindowManager manager = (WindowManager)view.getContext().getSystemService(Context.WINDOW_SERVICE);
                Display display = manager.getDefaultDisplay();

                Point point = new Point();
                display.getSize(point);
                int width = point.x;
                int height = point.y;

                int dimen = width < height ? width : height;
                dimen = dimen * 3 / 4;

                QRcontent = "Server: " + server_ip + "Time: " + selectedItemText;

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

    class ServerThread implements Runnable {

        public void run() {
            Socket socket;
            try {
                serverSocket = new ServerSocket(SERVER_PORT);
            } catch (IOException e) {
                e.printStackTrace();
                Log.v("ivana","Error Starting Server : " + e.getMessage());
            }
            if (null != serverSocket) {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        socket = serverSocket.accept();
                        CommunicationThread commThread = new CommunicationThread(socket);
                        new Thread(commThread).start();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.v("ivana","Error Communicating to Client :" + e.getMessage());
                    }
                }
            }
        }
    }

    class CommunicationThread implements Runnable {

        private Socket clientSocket;

        private BufferedReader input;

        public CommunicationThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
            tempClientSocket = clientSocket;
            try {
                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
                Log.v("ivana","Error Connecting to Client!!");
            }
            Log.v("ivana","Connected to Client!!");
            client_ctr++;
            Log.v("ivana","Number of clients : " + client_ctr);

        }

        public void run() {

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String read = input.readLine();
                    if (null == read || "Disconnect".contentEquals(read)) {
                        Thread.interrupted();
                        read = "Client Disconnected";
                        client_ctr--;
                        Log.v("ivana","Client : " + read);
                        Log.v("ivana","Number of clients : " + client_ctr);
                        break;
                    }

                    // switch tip poruke, substring do :
                    //dodavanje igraca
                    //za gubitak


                    Log.v("ivana","Client : " + read);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
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
            if (null != tempClientSocket) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        PrintWriter out = null;
                        try {
                            out = new PrintWriter(new BufferedWriter(
                                    new OutputStreamWriter(tempClientSocket.getOutputStream())),
                                    true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        out.println(message);
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}