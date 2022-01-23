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

public class LookItemsAdapter extends FirestoreRecyclerAdapter<LookItems, LookItemsAdapter.LookItemsViewHolder> {

    private Context context;
    private FirestoreRecyclerOptions<LookItems> lookItems;
//    private OnItemClickListener listener;

    public LookItemsAdapter(Context context, FirestoreRecyclerOptions<LookItems> lookItems){
        super(lookItems);

        this.context = context;
        this.lookItems = lookItems;

    }

    @NonNull
    @Override
    public LookItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(this.context).inflate(R.layout.close_card_design, parent, false);
        LookItemsViewHolder lookItemsViewHolder = new LookItemsViewHolder(view);
        return lookItemsViewHolder;


    }

    @Override
    protected void onBindViewHolder(@NonNull LookItemsViewHolder holder, int position, @NonNull LookItems model) {

        LookItems lookItem = lookItems.getSnapshots().get(position);
        Picasso.get().load(lookItem.getClotheImageUrl()).fit().centerInside().into(holder.image);
        holder.name.setText(model.getClotheName());

    }

    @Override
    public int getItemCount() {

        return lookItems.getSnapshots().size();

    }

    public class LookItemsViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView name;

        public LookItemsViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.close_image);
            name = itemView.findViewById(R.id.close_name);

        }
    }



}
