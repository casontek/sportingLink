package com.mycornership.betlink.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.mycornership.betlink.R;
import com.mycornership.betlink.activities.VideoActivity;
import com.mycornership.betlink.models.Video;

import java.util.List;

public class VideoRelatedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_LOAD = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private static final int VIEW_TYPE_ADS  = 2;
    private Context context;
    private List<Object> videos;

    public VideoRelatedAdapter(Context c, List<Object> objects) {
        this.context = c;
        this.videos = objects;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_ITEM){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_related_inflator, parent, false);
            return new videoHolder(view);
        }
        else if(viewType == VIEW_TYPE_ADS){
            View ads = LayoutInflater.from(parent.getContext()).inflate(R.layout.ads_inflator, parent, false);
            return new adsHolder(ads);
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_inflator, parent, false);
            return new loadHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof videoHolder){
            populateView((videoHolder)holder, position);
        }
        else if(holder instanceof adsHolder){
            //sets up ads using banner
            UnifiedNativeAd nativeAd = (UnifiedNativeAd)videos.get(position);
            populateAdsView((adsHolder)holder, nativeAd);
        }
        else {
            //shows the loading bar
            ((loadHolder)holder).bar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return videos == null ? 0 : videos.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = videos.get(position);
        if(item instanceof Video){
            return VIEW_TYPE_ITEM;
        }
        else if(item instanceof UnifiedNativeAd){
            return VIEW_TYPE_ADS;
        }
        else {
            return VIEW_TYPE_LOAD;
        }
    }

    private class videoHolder extends RecyclerView.ViewHolder {
        ImageView videoView;
        TextView tv_ttl, tv_date;
        View play_btn;

        public videoHolder(View view) {
            super(view);
            videoView = view.findViewById(R.id.video_play);
            tv_ttl = view.findViewById(R.id.video_title);
            tv_date = view.findViewById(R.id.video_date);
            play_btn = view.findViewById(R.id.play_button);
        }
    }

    private class adsHolder extends RecyclerView.ViewHolder {
        private UnifiedNativeAdView adView;

        public adsHolder(View ads) {
            super(ads);
            adView = itemView.findViewById(R.id.ad_view);
            //register the ad view with its assets
            adView.setMediaView((MediaView)adView.findViewById(R.id.ad_media));
            adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
            adView.setBodyView(adView.findViewById(R.id.ad_body));
            adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
            adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));
            adView.setStoreView(adView.findViewById(R.id.ad_store));
            adView.setPriceView(adView.findViewById(R.id.ad_price));
            adView.setIconView(adView.findViewById(R.id.ad_icon));
            adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        }

        public UnifiedNativeAdView getUnifiedAdView(){
            return adView;
        }
    }

    private class loadHolder extends RecyclerView.ViewHolder {
        private ProgressBar bar;
        public loadHolder(View view) {
            super(view);
            bar = view.findViewById(R.id.loading_bar);
        }
    }

    private void populateView(videoHolder holder, int position) {
        Video video = (Video)videos.get(position);
        holder.tv_ttl.setText(video.getTitle());
        holder.tv_date.setText(video.getEventDate());

        //gets the video thumb nail
        Glide.with(context).asBitmap().load(video.getVideoUrl()).into(holder.videoView);
        //play button
        holder.play_btn.setOnClickListener(v -> {
            //plays the video
            Intent i = new Intent(context, VideoActivity.class);
            i.putExtra("url", video.getVideoUrl());
            i.putExtra("category", video.getCategory());
            i.putExtra("title", video.getTitle());
            i.putExtra("id", video.getId());
            //start new activity
            context.startActivity(i);
        });

        holder.itemView.setOnClickListener(v ->{
            //plays the video
            Intent i = new Intent(context, VideoActivity.class);
            i.putExtra("url", video.getVideoUrl());
            i.putExtra("category", video.getCategory());
            i.putExtra("title", video.getTitle());
            i.putExtra("id", video.getId());
            //start new activity
            context.startActivity(i);
        });

    }

    private void populateAdsView(adsHolder holder, UnifiedNativeAd nativeAd) {
        //bind the ads content to the UI
        ((TextView)holder.getUnifiedAdView().getHeadlineView()).setText(nativeAd.getHeadline());
        ((TextView)holder.getUnifiedAdView().getBodyView()).setText(nativeAd.getBody());
        ((Button)holder.getUnifiedAdView().getCallToActionView()).setText(nativeAd.getCallToAction());

        NativeAd.Image icon = nativeAd.getIcon();
        if(icon == null){
            holder.getUnifiedAdView().getIconView().setVisibility(View.INVISIBLE);
        }
        else {
            ((ImageView)holder.getUnifiedAdView().getIconView()).setImageDrawable(icon.getDrawable());
        }

        if(nativeAd.getBody() == null){
            holder.getUnifiedAdView().getBodyView().setVisibility(View.INVISIBLE);
        }
        else {
            holder.getUnifiedAdView().getBodyView().setVisibility(View.VISIBLE);
            ((TextView)holder.getUnifiedAdView().getBodyView()).setText(nativeAd.getBody());
        }

        if(nativeAd.getPrice() == null){
            holder.getUnifiedAdView().getPriceView().setVisibility(View.INVISIBLE);
        }
        else {
            holder.getUnifiedAdView().getPriceView().setVisibility(View.VISIBLE);
            ((TextView)holder.getUnifiedAdView().getPriceView()).setText(nativeAd.getPrice());
        }

        if(nativeAd.getStore() == null){
            holder.getUnifiedAdView().getStoreView().setVisibility(View.INVISIBLE);
        }
        else {
            holder.getUnifiedAdView().getStoreView().setVisibility(View.VISIBLE);
            ((TextView)holder.getUnifiedAdView().getStoreView()).setText(nativeAd.getStore());
        }

        if(nativeAd.getStarRating() == null){
            holder.getUnifiedAdView().getStarRatingView().setVisibility(View.INVISIBLE);
        }
        else {
            holder.getUnifiedAdView().getStarRatingView().setVisibility(View.VISIBLE);
            ((RatingBar)holder.getUnifiedAdView().getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
        }

        if(nativeAd.getAdvertiser() == null){
            holder.getUnifiedAdView().getAdvertiserView().setVisibility(View.INVISIBLE);
        }
        else {
            holder.getUnifiedAdView().getAdvertiserView().setVisibility(View.VISIBLE);
            ((TextView)holder.getUnifiedAdView().getAdvertiserView()).setText(nativeAd.getAdvertiser());
        }

        if(nativeAd.getCallToAction() == null){
            holder.getUnifiedAdView().getCallToActionView().setVisibility(View.INVISIBLE);
        }
        else {
            holder.getUnifiedAdView().getCallToActionView().setVisibility(View.VISIBLE);
            ((Button)holder.getUnifiedAdView().getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        holder.getUnifiedAdView().setNativeAd(nativeAd);
    }
}
