package com.bilki.mywardrobe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class LooksAdapter extends FirestoreRecyclerAdapter<Upload, LooksAdapter.LooksViewHolder> {

    private Context context;
    private FirestoreRecyclerOptions<Upload> uploads;

    public LooksAdapter(Context context, FirestoreRecyclerOptions<Upload> uploads) {
        super(uploads);

        this.context = context;
        this.uploads = uploads;

    }

    @NonNull
    @Override
    public LooksAdapter.LooksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(this.context/*parent.getContext()*/).inflate(R.layout.look_card_design, parent, false);
        LooksAdapter.LooksViewHolder looksViewHolder = new LooksAdapter.LooksViewHolder(view);
        return looksViewHolder;

    }

//    @Override
//    public void onBindViewHolder(@NonNull LooksAdapter.LooksViewHolder holder, int position) {
//
//        Upload upload = uploads.getSnapshots().get(position);
//
//
////        LooksHelperClass looksHelperClass = looksLocations.get(position);
//
//        Picasso.get().load(upload.getImageUrl()).into(holder.image);
//
////        holder.image.setImageResource(looksHelperClass.getImage());
//
//    }

    @Override
    protected void onBindViewHolder(@NonNull LooksViewHolder holder, int position, @NonNull Upload model) {

        Upload upload = uploads.getSnapshots().get(position);


//        LooksHelperClass looksHelperClass = looksLocations.get(position);

        Picasso.get().load(upload.getImageUrl()).fit().centerCrop().into(holder.image);

//        holder.image.setImageResource(looksHelperClass.getImage());

    }

    @Override
    public int getItemCount() {

        return uploads.getSnapshots().size();

    }

    public static class LooksViewHolder extends RecyclerView.ViewHolder{

        ImageView image;

        public LooksViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.look_image);

        }
    }

}
