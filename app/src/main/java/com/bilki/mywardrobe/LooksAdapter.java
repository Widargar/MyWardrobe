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

public class LooksAdapter extends FirestoreRecyclerAdapter<Look, LooksAdapter.LooksViewHolder> {

    private Context context;
    private FirestoreRecyclerOptions<Look> looks;

    public LooksAdapter(Context context, FirestoreRecyclerOptions<Look> look) {
        super(look);

        this.context = context;
        this.looks = look;

    }

    @NonNull
    @Override
    public LooksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(this.context).inflate(R.layout.look_card_design, parent, false);
        LooksViewHolder looksViewHolder = new LooksViewHolder(view);
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
    protected void onBindViewHolder(@NonNull LooksViewHolder holder, int position, @NonNull Look model) {

        Look look = looks.getSnapshots().get(position);
        Picasso.get().load(look.getLookImageUrl()).fit().centerCrop().into(holder.image);

    }

    @Override
    public int getItemCount() {

        return looks.getSnapshots().size();

    }

    public static class LooksViewHolder extends RecyclerView.ViewHolder{

        ImageView image;

        public LooksViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.look_image);

        }
    }

}
