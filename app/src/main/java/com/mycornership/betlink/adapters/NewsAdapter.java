package com.mycornership.betlink.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.mycornership.betlink.R;
import com.mycornership.betlink.activities.NewsActivity;
import com.mycornership.betlink.models.News;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_LOAD = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private static final int VIEW_TYPE_ADS  = 2;
    private Context context;
    private List<Object> timelines = new ArrayList<>();
    private AdLoader adLoader;

    public NewsAdapter(Context c, List<Object> objects){
        this.context = c;
        this.timelines = objects;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_ITEM){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeline_inflator, parent, false);
            return new timelineHolder(view);
        }
        else if(viewType == VIEW_TYPE_ADS){
            View ads = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_ads_view, parent, false);
            return new adsHolder(ads);
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_inflator, parent, false);
            return new loadHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof timelineHolder){
            populateView((timelineHolder)holder, position);
        }
        else if(holder instanceof adsHolder){
            //sets up ads using banner
            adLoader = new AdLoader.Builder(context, "ca-app-pub-9133642311191277~6577598985")
                    .forUnifiedNativeAd(unifiedNativeAd -> {
                        NativeTemplateStyle styles = new
                                NativeTemplateStyle.Builder().build();
                        ((adsHolder) holder).templateView.setStyles(styles);
                        ((adsHolder) holder).templateView.setNativeAd(unifiedNativeAd);
                    })
                    .build();

            adLoader.loadAd(new AdRequest.Builder().build());
        }
        else {
            //shows the loading bar
            ((loadHolder)holder).bar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return timelines == null ? 0 : timelines.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = timelines.get(position);
        if(item instanceof News){
            return VIEW_TYPE_ITEM;
        }
        else if(item instanceof Integer){
            return VIEW_TYPE_ADS;
        }
        else {
            return VIEW_TYPE_LOAD;
        }
    }

    private class timelineHolder extends RecyclerView.ViewHolder {
        ImageView tln_image;
        TextView tv_title, tv_date, tv_source;
        public timelineHolder(View view) {
            super(view);
            tln_image = itemView.findViewById(R.id.timeline_media);
            tv_title = itemView.findViewById(R.id.timeline_headline);
            tv_date = itemView.findViewById(R.id.timeline_date_time);
            tv_source = itemView.findViewById(R.id.timeline_source);
        }
    }

    private class adsHolder extends RecyclerView.ViewHolder {
        TemplateView templateView;

        public adsHolder(View ads) {
            super(ads);
            templateView = ads.findViewById(R.id.my_template);
        }

    }

    private class loadHolder extends RecyclerView.ViewHolder {
        private ProgressBar bar;
        public loadHolder(View view) {
            super(view);
            bar = view.findViewById(R.id.loading_bar);
        }
    }

    private void populateView(timelineHolder holder, int position) {
        News newsItem = (News)timelines.get(position);
        Log.d("item", newsItem.toString());
        holder.tv_title.setText(newsItem.getTitle());
        holder.tv_date.setText(newsItem.getEventDate());
        holder.tv_source.setText(newsItem.getSource());
        //displays the image on the image view
        Glide.with(context)
                .load(newsItem.getMediaUrl()).into(holder.tln_image);

        holder.itemView.setOnClickListener(v ->{
            //opens the netail activity  //news
            Intent i = new Intent(context, NewsActivity.class);
            //i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("body",newsItem.getDetail());
            i.putExtra("title", newsItem.getTitle());
            i.putExtra("url", newsItem.getMediaUrl());
            i.putExtra("editor", newsItem.getEditor());
            i.putExtra("date", newsItem.getEventDate());
            i.putExtra("time", newsItem.getEventTime());
            i.putExtra("category", newsItem.getCategory());
            i.putExtra("news_id", String.valueOf(newsItem.getId()));
            //start activity
            context.startActivity(i);
        });
    }

}
