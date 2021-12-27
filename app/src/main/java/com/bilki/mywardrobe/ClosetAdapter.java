package com.bilki.mywardrobe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ClosetAdapter extends RecyclerView.Adapter<ClosetAdapter.ClosetViewHolder> {

    private Context context;
    private ArrayList<FeaturedHelperClass> closetLocations;
    private ClosetViewHolder.OnItemListener onItemListener;

    public ClosetAdapter(ArrayList<FeaturedHelperClass> closetLocations, ClosetViewHolder.OnItemListener onItemListener) {

        this.closetLocations = closetLocations;
        this.onItemListener = onItemListener;

    }

    @NonNull
    @Override
    public ClosetAdapter.ClosetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.close_card_design, parent, false);
        ClosetAdapter.ClosetViewHolder closetViewHolder = new ClosetAdapter.ClosetViewHolder(view, onItemListener);
        return closetViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ClosetViewHolder holder, int position) {

        FeaturedHelperClass featuredHelperClass = closetLocations.get(position);

        //Picasso.get().load(featuredHelperClass.getUrl()).resize(95,95).into(holder.image);
        holder.image.setImageResource(featuredHelperClass.getImage());
        holder.name.setText(featuredHelperClass.getName());

    }



    @Override
    public int getItemCount() {

        return closetLocations.size();

    }

    public static class ClosetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView image;
        TextView name;
        OnItemListener onItemListener;

        public ClosetViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);

            image = itemView.findViewById(R.id.close_image);
            name = itemView.findViewById(R.id.close_name);
            this.onItemListener = onItemListener;

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            onItemListener.onItemClick(getAdapterPosition());

        }

        public interface OnItemListener{

            void onItemClick(int position);

        }

    }
}
