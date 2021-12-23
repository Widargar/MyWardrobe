package com.bilki.mywardrobe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.TypeViewHolder>{

    ArrayList<TypeHelperClass> typeLocations;

    public TypeAdapter(ArrayList<TypeHelperClass> typeLocations) {
        this.typeLocations = typeLocations;
    }

    @NonNull
    @Override
    public TypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.type_and_season_card_design, parent, false);
        TypeAdapter.TypeViewHolder typeViewHolder = new TypeAdapter.TypeViewHolder(view);
        return typeViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TypeViewHolder holder, int position) {

        TypeHelperClass typeHelperClass = typeLocations.get(position);

        holder.type.setText(typeHelperClass.getType());

    }

    @Override
    public int getItemCount() {
        return typeLocations.size();
    }


    public static class TypeViewHolder extends RecyclerView.ViewHolder{

        TextView type;

        public TypeViewHolder(@NonNull View itemView) {
            super(itemView);

            type = itemView.findViewById(R.id.type_text);

        }
    }

}
