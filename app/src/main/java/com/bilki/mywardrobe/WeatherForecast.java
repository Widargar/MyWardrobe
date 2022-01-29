package com.bilki.mywardrobe;

import android.app.DialogFragment;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WeatherForecast extends DialogFragment {

    private View view;
    private RecyclerView weatherForecastRecycler;
    private WeatherAdapter weatherAdapter;
    private ArrayList<WeatherHelperClass> weatherArrayList;
    private JSONArray hourArray;
    private final static String TAG = "bilki: Weatherforecas: ";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_weather_forecast, container, false);

        weatherForecastRecycler = (RecyclerView) view.findViewById(R.id.weather_forecast_recycler);
        weatherForecastRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        MainActivity mainActivity = (MainActivity) getActivity();
//        weatherAdapter = mainActivity.getWeatherAdapter();
        weatherArrayList = mainActivity.getWeatherArrayList();




        hourArray = mainActivity.getHourArray();

        try{

            for(int i = 0; i < hourArray.length(); i++){

                JSONObject hourObject = hourArray.getJSONObject(i);
                String time = hourObject.getString("time");
                String temperat = hourObject.getString("temp_c");
                String img = hourObject.getJSONObject("condition").getString("icon");
                String conditon = hourObject.getJSONObject("condition").getString("text");
                weatherArrayList.add(new WeatherHelperClass(time, temperat, img, conditon));
                Log.d(TAG, "temp:" + temperat);
                Log.d(TAG, "array: " + weatherArrayList);


            }


        }catch (JSONException e){

            e.printStackTrace();

        }
        weatherAdapter = new WeatherAdapter(getActivity(), weatherArrayList);
        weatherForecastRecycler.setAdapter(weatherAdapter);
        weatherAdapter.notifyDataSetChanged();

        return view;
    }




}