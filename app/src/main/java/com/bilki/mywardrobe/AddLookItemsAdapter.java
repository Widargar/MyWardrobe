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

import java.util.ArrayList;

public class AddLookItemsAdapter extends RecyclerView.Adapter<AddLookItemsAdapter.AddLookItemsViewHolder>{

    private Context context;
    private final ArrayList<LookItems> lookItems;

    public AddLookItemsAdapter(Context context, ArrayList<LookItems> lookItems) {
        this.context = context;
        this.lookItems = lookItems;
    }


    @NonNull
    @Override
    public AddLookItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.clothe_card_design, parent, false);
        AddLookItemsViewHolder addLookItemsViewHolder = new AddLookItemsViewHolder(view);
        return addLookItemsViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull AddLookItemsViewHolder holder, int position) {

        LookItems lookItems = this.lookItems.get(position);

        Picasso.get().load(lookItems.getClotheImageUrl()).fit().centerInside().into(holder.image);
        holder.name.setText(lookItems.getClotheName());

    }

    @Override
    public int getItemCount() {
        return this.lookItems.size();
    }

    public class AddLookItemsViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView name;

        public AddLookItemsViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.close_image);
            name = itemView.findViewById(R.id.close_name);

        }
    }

}
