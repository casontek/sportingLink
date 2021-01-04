package com.mycornership.betlink.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mycornership.betlink.R;
import com.mycornership.betlink.activities.NewsActivity;
import com.mycornership.betlink.models.News;

import java.util.List;

public class RelatedNewsAdapter extends RecyclerView.Adapter<RelatedNewsAdapter.RVHolder> {
    private List<News> hotlines;
    private Context c;

    public RelatedNewsAdapter(Context context, List<News> items){
        this.hotlines = items;
        this.c = context;
    }

    @NonNull
    @Override
    public RelatedNewsAdapter.RVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.related_inflator, parent, false);
        return new RVHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RelatedNewsAdapter.RVHolder holder, final int position) {
        holder.title.setText(hotlines.get(position).getTitle());
        holder.source.setText(hotlines.get(position).getSource());
        holder.time.setText(hotlines.get(position).getEventDate());

        String url = hotlines.get(position).getMediaUrl();
        Glide.with(c).asBitmap()
                .load(url)
                .into(holder.img);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open new window to read the entire news
                News timeline = hotlines.get(position);
                showDetail(timeline);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hotlines.size();
    }

    public class RVHolder extends RecyclerView.ViewHolder {
        TextView title, time, source;
        ImageView img;

        public RVHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.related_time);
            title = itemView.findViewById(R.id.related_title);
            source = itemView.findViewById(R.id.related_source);
            img = itemView.findViewById(R.id.related_image_handle);
        }
    }

    private void showDetail(News news) {
        Intent i = new Intent(c, NewsActivity.class);
        //i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("body",news.getDetail());
        i.putExtra("title", news.getTitle());
        i.putExtra("url", news.getMediaUrl());
        i.putExtra("editor", news.getEditor());
        i.putExtra("date", news.getEventDate());
        i.putExtra("time", news.getEventTime());
        i.putExtra("category", news.getCategory());
        i.putExtra("news_id", String.valueOf(news.getId()));
        //navigates to news activity
        c.startActivity(i);
    }
}
