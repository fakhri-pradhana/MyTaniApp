package com.android.mytani.fragment.discover;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.mytani.R;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;


public class DiscoverPriceFragment extends Fragment {

    private TextView tv_discover_tgl;
    private CardView cardView;

    private java.util.Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;
    private String urlComodityPrice = "http://aplikasi.pertanian.go.id/smshargakab/qrylaphar.asp";

    Date curentTime;



    public DiscoverPriceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_discover_price, container, false);

        // initialize calendar
        curentTime = Calendar.getInstance().getTime();
        String formattedDate = DateFormat.getDateInstance(DateFormat.FULL).format(curentTime);

        tv_discover_tgl = view.findViewById(R.id.tv_discover_tgl);
        cardView = view.findViewById(R.id.cardView);

        tv_discover_tgl.setText(formattedDate);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Mengarahkan anda ke website ..");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlComodityPrice));
                startActivity(intent);
            }
        });


        return view;
    }

    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}