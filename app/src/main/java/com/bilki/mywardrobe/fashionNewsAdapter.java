package com.bilki.mywardrobe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bilki.mywardrobe.NewsModels.NewsHeadlines;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class fashionNewsAdapter extends RecyclerView.Adapter<fashionNewsAdapter.fashionNewsViewHolder> {

    private Context context;
    private List<NewsHeadlines> fashionArrayList;
    private SelectNewsListener listener;

    public fashionNewsAdapter(Context context, List<NewsHeadlines> fashionArrayList, SelectNewsListener listener){

        this.context = context;
        this.fashionArrayList = fashionArrayList;
        this.listener = listener;

    }


    @NonNull
    @Override
    public fashionNewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fashion_news_card_design, parent, false);
        fashionNewsAdapter.fashionNewsViewHolder fashionNewsViewHolder = new fashionNewsAdapter.fashionNewsViewHolder(view);
        return fashionNewsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull fashionNewsViewHolder holder, @SuppressLint("RecyclerView") int position) {

        NewsHeadlines newsHeadlines = fashionArrayList.get(position);
        holder.title.setText(newsHeadlines.getTitle());
        holder.article.setText(newsHeadlines.getContent());
        holder.resource.setText(newsHeadlines.getSource().getName());

        if(fashionArrayList.get(position).getUrlToImage() != null){

            Picasso.get().load(newsHeadlines.getUrlToImage()).fit().centerCrop().into(holder.image);

        }



    }

    @Override
    public int getItemCount() {
        return fashionArrayList.size();
    }

    public class fashionNewsViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView title, article, resource;
        CardView cardView;

        @SuppressLint("ResourceType")
        public fashionNewsViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.news_image);
            title = itemView.findViewById(R.id.news_title);
            article = itemView.findViewById(R.id.news_article);
            resource = itemView.findViewById(R.id.news_resource);
            cardView = itemView.findViewById(R.layout.fashion_news_card_design);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();

                    listener.OnNewsClick(fashionArrayList.get(position));

                }
            });

        }
    }

    public interface SelectNewsListener{

        void OnNewsClick(NewsHeadlines headlines);

    }

//    public void setOnNewsCLickListener(SelectNewsListener listener){
//
//        this.listener = listener;
//
//    }

}
