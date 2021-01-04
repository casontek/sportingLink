package com.mycornership.betlink.adapters;

import android.content.Context;
import android.net.Uri;
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
import com.mycornership.betlink.models.Match;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MatchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_LOAD = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private static final int VIEW_TYPE_ADS = 2;
    private List<Object> matches;
    private Context context;
    private String cnt = "";

    public MatchAdapter(Context c, List<Object> objects) {
        this.context = c;
        this.matches = objects;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_ITEM){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.match_inflator, parent, false);
            return new matchHolder(view);
        }
        else if(viewType == VIEW_TYPE_LOAD){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_inflator, parent, false);
            return new loadHolder(view);
        }
        else {
            View ads = LayoutInflater.from(parent.getContext()).inflate(R.layout.ads_inflator, parent, false);
            return new adsHolder(ads);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof matchHolder){
            Match match = (Match)matches.get(position);
            //display matches
            populatesMatch((matchHolder)holder, position, match);
        }
        else if(holder instanceof adsHolder){
            //display ads
            UnifiedNativeAd nativeAd = (UnifiedNativeAd)matches.get(position);
            showAdsItem((adsHolder)holder, nativeAd);
        }
        else {
            //shows the loading bar
            ((loadHolder)holder).bar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return matches == null ? 0 : matches.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object object = matches.get(position);
        if( object instanceof Match){

            return VIEW_TYPE_ITEM;
        }
        else if(object instanceof UnifiedNativeAd){ //replace Integer with 'UnifiedNativeAd'

            return VIEW_TYPE_ADS;
        }
        else {

            return VIEW_TYPE_LOAD;
        }
    }

    private class matchHolder extends RecyclerView.ViewHolder {
        View hwin, awin, rlayout;
        TextView tv_league, tv_time_status, tv_home, tv_away,
            tv_h_score, tv_a_score;
        ImageView country;
        public matchHolder(View view) {
            super(view);
            tv_league = view.findViewById(R.id.tournament);
            tv_time_status = view.findViewById(R.id.match_time_status);
            tv_home = view.findViewById(R.id.home_team);
            tv_away = view.findViewById(R.id.away_team);
            hwin = view.findViewById(R.id.hview);
            awin = view.findViewById(R.id.aview);
            tv_a_score = view.findViewById(R.id.away_score);
            tv_h_score = view.findViewById(R.id.home_score);
            rlayout = view.findViewById(R.id.rlt);
            country = view.findViewById(R.id.country_logo);
        }
    }

    private class loadHolder extends RecyclerView.ViewHolder {
        ProgressBar bar;
        public loadHolder(View view) {
            super(view);
            bar = view.findViewById(R.id.loading_bar);
        }
    }

    private class adsHolder extends RecyclerView.ViewHolder {
        private UnifiedNativeAdView adView;

        public adsHolder(View adsView) {
            super(adsView);
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

    private void showAdsItem(adsHolder holder, UnifiedNativeAd nativeAd) {
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

    private void populatesMatch(matchHolder holder, int position, Match match) {
        cnt = match.getLeague().getCountry();
        String lgn;
        if(cnt.length() > 0 && !cnt.equals("World"))
            lgn = match.getLeague().getName() + " - " + match.getLeague().getCountry();
        else
            lgn = match.getLeague().getName();
        //sets the league name and country if applicable
        holder.tv_league.setText(lgn);
        if(match.getLeague().getName().equals(""))
            holder.rlayout.setVisibility(View.GONE);
        else
            holder.rlayout.setVisibility(View.VISIBLE);

        //sets the country Image logo
        Glide.with(context).load(match.getLeague().getLogo())
                .into(holder.country);
        //sets the winning icon
        if(match.getHomeGoal() == match.getAwayGoal()){
            holder.hwin.setVisibility(View.GONE);
            holder.awin.setVisibility(View.GONE);
        }
        else if(match.getHomeGoal() > match.getAwayGoal()){
            holder.hwin.setVisibility(View.VISIBLE);
            holder.awin.setVisibility(View.GONE);
        }
        else{
            holder.hwin.setVisibility(View.GONE);
            holder.awin.setVisibility(View.VISIBLE);
        }

        String h = "" + match.getHomeGoal();
        String a = "" + match.getAwayGoal();
        //sets the match time
        if(match.getStatus().equals("Match Postponed")) {
            holder.tv_time_status.setText("PST");
            holder.tv_h_score.setText("-");
            holder.tv_a_score.setText("-");
        }
        else if(match.getStatus().equals("Match Finished")) {
            holder.tv_time_status.setText("FT");
            holder.tv_h_score.setText(h);
            holder.tv_a_score.setText(a);
        }
        else if(match.getStatus().equals("Match Cancelled")) {
            holder.tv_time_status.setText("CNC");
            holder.tv_h_score.setText("-");
            holder.tv_a_score.setText("-");
        }
        else {
            holder.tv_time_status.setText(getMatchTime(match.getMatchTime()));
            holder.tv_h_score.setText(h);
            holder.tv_a_score.setText(a);
        }
        //sets other properties
        holder.tv_home.setText(match.getHomeTeam().getName());
        holder.tv_away.setText(match.getAwayTeam().getName());


    }

    private String getMatchTime(long unix_time){
        Date date = new Date(unix_time * 1000L);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.setTimeZone(TimeZone.getDefault());
        String hour, min;
        int h = c.get(Calendar.HOUR_OF_DAY);
        int m = c.get(Calendar.MINUTE);
        if(h < 10)
            hour = "0" + h;
        else
            hour = h + "";
        if(m < 10)
            min = "0" + m;
        else
            min = m + "";

        return hour + ":" + min ;
    }

}
