package com.bilki.mywardrobe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {

    private Context context;
    private ArrayList<WeatherHelperClass> weatherArrayList;

    public WeatherAdapter(Context context, ArrayList<WeatherHelperClass> weatherArrayList) {
        this.context = context;
        this.weatherArrayList = weatherArrayList;
    }

    @NonNull
    @Override
    public WeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.weather_card_design, parent, false);
        WeatherAdapter.ViewHolder viewHolder = new WeatherAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherAdapter.ViewHolder holder, int position) {

        WeatherHelperClass weatherObject = weatherArrayList.get(position);

        Picasso.get().load("https:".concat(weatherObject.getIcon())).fit().into(holder.conditionImage);
        holder.temperature.setText(weatherObject.getTemperature() + "°C");
        holder.condition.setText(weatherObject.getCondition());
        SimpleDateFormat day = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat hour = new SimpleDateFormat("hh:mm aa");

        try{

            Date date = day.parse(weatherObject.getTime());
            holder.time.setText(hour.format(date));


        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return  weatherArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView temperature, time, condition;
        private ImageView conditionImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            temperature = itemView.findViewById(R.id.weather_temperature);
            time = itemView.findViewById(R.id.weather_time);
            condition = itemView.findViewById(R.id.weather_condition);
            conditionImage = itemView.findViewById(R.id.weather_icon);

        }
    }
}
