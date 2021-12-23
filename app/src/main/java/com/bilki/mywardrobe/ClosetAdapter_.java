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

public class ClosetAdapter_ extends FirestoreRecyclerAdapter<Upload, ClosetAdapter_.ClosetViewHolder> {

    private Context context;
    private FirestoreRecyclerOptions<Upload> uploads;

    public ClosetAdapter_(Context context, FirestoreRecyclerOptions<Upload> uploads) {
        super(uploads);

        this.context = context;
        this.uploads = uploads;

    }

    @NonNull
    @Override
    public ClosetAdapter_.ClosetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(this.context).inflate(R.layout.close_card_design, parent, false);
        ClosetAdapter_.ClosetViewHolder closetViewHolder = new ClosetAdapter_.ClosetViewHolder(view);
        return closetViewHolder;

    }

//    @Override
//    public void onBindViewHolder(@NonNull ClosetViewHolder holder, int position) {
//
//        FeaturedHelperClass featuredHelperClass = closetLocations.get(position);
//
//        //Picasso.get().load(featuredHelperClass.getUrl()).resize(95,95).into(holder.image);
//        holder.image.setImageResource(featuredHelperClass.getImage());
//        holder.name.setText(featuredHelperClass.getName());
//
//    }

    @Override
    protected void onBindViewHolder(@NonNull ClosetViewHolder holder, int position, @NonNull Upload model) {

        Upload upload = uploads.getSnapshots().get(position);
        Picasso.get().load(upload.getImageUrl()).into(holder.image);
        holder.name.setText(model.getType());

    }

    @Override
    public int getItemCount() {

        return uploads.getSnapshots().size();

    }

    public static class ClosetViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView name;

        public ClosetViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.close_image);
            name = itemView.findViewById(R.id.close_name);


        }
    }
}
