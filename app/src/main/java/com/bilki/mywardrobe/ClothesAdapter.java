package com.bilki.mywardrobe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class ClothesAdapter extends FirestoreRecyclerAdapter<Upload, ClothesAdapter.ClothesViewHolder> {

    private Context context;
    private FirestoreRecyclerOptions<Upload> uploads;
    private OnItemClickListener listener;

    public ClothesAdapter(Context context, FirestoreRecyclerOptions<Upload> uploads) {
        super(uploads);

        this.context = context;
        this.uploads = uploads;

    }

    @NonNull
    @Override
    public ClothesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(this.context).inflate(R.layout.close_card_design, parent, false);
        ClothesViewHolder closetViewHolder = new ClothesViewHolder(view);
        return closetViewHolder;

    }

//    @Override
//    public void onBindViewHolder(@NonNull ClothesViewHolder holder, int position) {
//
//        FeaturedHelperClass featuredHelperClass = closetLocations.get(position);
//
//        //Picasso.get().load(featuredHelperClass.getUrl()).resize(95,95).into(holder.image);
//        holder.image.setImageResource(featuredHelperClass.getImage());
//        holder.name.setText(featuredHelperClass.getName());
//
//    }

    @Override
    protected void onBindViewHolder(@NonNull ClothesViewHolder holder, int position, @NonNull Upload model) {

        Upload upload = uploads.getSnapshots().get(position);
        Picasso.get().load(upload.getImageUrl()).fit().centerInside().into(holder.image);
        holder.name.setText(model.getName());

    }

    @Override
    public int getItemCount() {

        return uploads.getSnapshots().size();

    }

    public class ClothesViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView name;
//        OnClotheListener onClotheListener;

        public ClothesViewHolder(@NonNull View itemView/*, OnClotheListener onClotheListener*/) {
            super(itemView);

            image = itemView.findViewById(R.id.close_image);
            name = itemView.findViewById(R.id.close_name);

//            this.onClotheListener = onClotheListener;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();

                    if(position != RecyclerView.NO_POSITION && listener != null){

                        listener.onItemClick(getSnapshots().getSnapshot(position), position);

                    }

                }
            });


        }

    }

    public interface OnItemClickListener {

        void onItemClick(DocumentSnapshot documentSnapshot, int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener){

        this.listener = listener;

    }
}
