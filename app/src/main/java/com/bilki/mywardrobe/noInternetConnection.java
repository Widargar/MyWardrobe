package com.bilki.mywardrobe;

import android.app.DialogFragment;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class noInternetConnection extends DialogFragment {

    private View view;
    private Button reconnectBttn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_no_internet_connection, container, false);

        reconnectBttn = (Button) view.findViewById(R.id.reconnect_bttn);

        reconnectBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CheckInternet checkInternet = new CheckInternet();
                if(checkInternet.isConnected(getActivity())){

                    ((MainActivity)getActivity()).progressBar.setVisibility(View.GONE);
                    ((MainActivity)getActivity()).weatherDataLayout.setVisibility(View.VISIBLE);
                    ((MainActivity)getActivity()).getWeatherInfo(((MainActivity) getActivity()).cityName);

                    getDialog().dismiss();

                }

            }
        });

        return view;
    }
}