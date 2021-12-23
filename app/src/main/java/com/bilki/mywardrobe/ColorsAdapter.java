package com.bilki.mywardrobe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ColorsAdapter extends RecyclerView.Adapter<ColorsAdapter.ColorsViewHolder>{

    ArrayList<ColorsHelperClass> colorsLocations;

    public ColorsAdapter(ArrayList<ColorsHelperClass> colorsLocations) {
        this.colorsLocations = colorsLocations;
    }

    @NonNull
    @Override
    public ColorsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.color_card_design, parent, false);
        ColorsAdapter.ColorsViewHolder colorsViewHolder = new ColorsAdapter.ColorsViewHolder(view);
        return colorsViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ColorsViewHolder holder, int position) {

        ColorsHelperClass colorsHelperClass = colorsLocations.get(position);

        holder.image.setImageResource(colorsHelperClass.getImage());

    }

    @Override
    public int getItemCount() {

        return colorsLocations.size();

    }

    public static class ColorsViewHolder extends RecyclerView.ViewHolder{

        ImageView image;

        public ColorsViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.color_image);

        }
    }

}
