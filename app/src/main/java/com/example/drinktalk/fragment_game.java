package com.example.drinktalk;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.zxing.WriterException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;


public class fragment_game extends Fragment implements BottomSheetDialogFragment {

    private LinearLayout layoutBottomSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private Button btnUpostojecu;
    private ListView listView;
    private Spinner spinner;
    private ImageView qrCode;
    Bitmap bitmap;
    QRGEncoder encoder;

    public fragment_game() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_game, container, false);

        layoutBottomSheet = view.findViewById(R.id.bottomSheet);
        btnUpostojecu = view.findViewById(R.id.btn_prijavi_u_postojecu);

        spinner = view.findViewById(R.id.spinner);
        qrCode = view.findViewById(R.id.qrCode);


        btnUpostojecu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment_scan second = new fragment_scan();
                FragmentTransaction trans = getFragmentManager().beginTransaction();
                trans.replace(R.id.placeholder, second);
                trans.addToBackStack(null);
                trans.commit();
            }
        });

        // Initializing a String Array
        String[] plants = new String[]{
                "1 H 00 MIN",
                "1 H 15 MIN",
                "1 H 30 MIN",
                "1 H 45 MIN",
                "2 H 00 MIN"
        };


        final List<String> plantsList = new ArrayList<>(Arrays.asList(plants));

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                view.getContext(), R.layout.spinner_item, plantsList) {
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position % 2 == 1) {
                    // Set the item background color
                    tv.setBackgroundColor(Color.parseColor("#FFC9A3FF"));
                } else {
                    // Set the alternate item background color
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

                WindowManager manager = (WindowManager) view.getContext().getSystemService(Context.WINDOW_SERVICE);
                Display display = manager.getDefaultDisplay();

                Point point = new Point();
                display.getSize(point);

                int width = point.x;
                int height = point.y;

                Log.v("ivana", "W:" + width + " H:" + height);

                // generating dimension from width and height.
                int dimen = width < height ? width : height;
                dimen = dimen * 3 / 4;

                encoder = new QRGEncoder(selectedItemText, null, QRGContents.Type.TEXT, dimen);
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

}
