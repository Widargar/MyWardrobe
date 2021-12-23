package com.bilki.mywardrobe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SizeAdapter extends RecyclerView.Adapter<SizeAdapter.SizeViewHolder>{

    ArrayList<SizeHelperClass> sizeLocations;

    public SizeAdapter(ArrayList<SizeHelperClass> sizeLocations) {
        this.sizeLocations = sizeLocations;
    }

    @NonNull
    @Override
    public SizeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.size_card_design, parent, false);
        SizeAdapter.SizeViewHolder sizeViewHolder = new SizeAdapter.SizeViewHolder(view);
        return sizeViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SizeViewHolder holder, int position) {

        SizeHelperClass sizeHelperClass = sizeLocations.get(position);

        holder.size.setText(sizeHelperClass.getSize());

    }

    @Override
    public int getItemCount() {
        return sizeLocations.size();
    }

    public static class SizeViewHolder extends RecyclerView.ViewHolder{

        TextView size;

        public SizeViewHolder(@NonNull View itemView) {
            super(itemView);

            size = itemView.findViewById(R.id.size_text);

        }
    }

}
