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

public class fashionNewsAdapter extends RecyclerView.Adapter<fashionNewsAdapter.fashionNewsViewHolder> {

    private ArrayList<fashionNewsHelperClass> fashionArrayList;

    public fashionNewsAdapter(ArrayList<fashionNewsHelperClass> fashionArrayList){

        this.fashionArrayList = fashionArrayList;

    }


    @NonNull
    @Override
    public fashionNewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fashion_news_card_design, parent, false);
        fashionNewsAdapter.fashionNewsViewHolder fashionNewsViewHolder = new fashionNewsAdapter.fashionNewsViewHolder(view);
        return fashionNewsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull fashionNewsViewHolder holder, int position) {

        fashionNewsHelperClass fashionNewsHelperClass = fashionArrayList.get(position);
        Picasso.get().load(fashionNewsHelperClass.getImage()).fit().centerCrop().into(holder.image);
        holder.title.setText(fashionNewsHelperClass.getName());
        holder.article.setText(fashionNewsHelperClass.getArticle());

    }

    @Override
    public int getItemCount() {
        return fashionArrayList.size();
    }

    public class fashionNewsViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView title, article;

        public fashionNewsViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.news_image);
            title = itemView.findViewById(R.id.news_title);
            article = itemView.findViewById(R.id.news_article);

        }
    }
}
