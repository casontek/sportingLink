package com.mycornership.betlink.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.mycornership.betlink.R;
import com.mycornership.betlink.models.Prediction;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class InsideTipAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> objectList;
    private Context context;
    private final static int VIEW_TYPE_ITEM  = 0;
    private final static int VIEW_TYPE_ADS = 1;

    public InsideTipAdapter(Context c,List<Object> items){
        this.objectList = items;
        this.context = c;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_ITEM){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.insid_tip_inflator, parent, false);
            InsideHolder holder = new InsideHolder(view);
            return holder;
        }
        else {
            View ads = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_adds_inflator, parent, false);
            return new adsHolder1(ads);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof InsideHolder)
            populateView((InsideHolder)holder, position);
        else {
            UnifiedNativeAd nativeAd = (UnifiedNativeAd)objectList.get(position);
            populateAdsView((adsHolder1)holder, nativeAd);
        }

    }

    @Override
    public int getItemCount() {
        return objectList == null ? 0 : objectList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object obj = objectList.get(position);
        if(obj instanceof Prediction){
            return VIEW_TYPE_ITEM;
        }
        else {
            return VIEW_TYPE_ADS;
        }
    }

    public class InsideHolder extends  RecyclerView.ViewHolder{
        private TextView tv_date_time, tv_teams, tv_league, tv_post_date, tv_tip;
        private View status_vw;

        public InsideHolder(@NonNull View itemView) {
            super(itemView);
            tv_teams = itemView.findViewById(R.id.teams_0100);
            tv_league = itemView.findViewById(R.id.league);
            tv_date_time = itemView.findViewById(R.id.date_time);
            tv_tip = itemView.findViewById(R.id.user_tip);
            tv_post_date = itemView.findViewById(R.id.tv_p_date_0100);
            status_vw = itemView.findViewById(R.id.view_status);

        }
    }

    public class adsHolder1 extends RecyclerView.ViewHolder{

        private UnifiedNativeAdView adView;

        public adsHolder1(@NonNull View itemView) {
            super(itemView);
            adView = itemView.findViewById(R.id.ad_view);
            //register the ad view with its assets
            adView.setMediaView((MediaView)adView.findViewById(R.id.ad_media));
            adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
            adView.setBodyView(adView.findViewById(R.id.ad_body));
            adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
            adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        }

        public UnifiedNativeAdView getUnifiedAdView(){
            return adView;
        }


    }

    private void populateView(InsideHolder holder, int position) {
        Prediction prediction = (Prediction)objectList.get(position);
        String hTeam = prediction.getMatch().getHomeTeam().getName();
        String aTeam = prediction.getMatch().getAwayTeam().getName();
        String league = prediction.getMatch().getLeague().getName();

        holder.tv_teams.setText(hTeam + " - " + aTeam);
        holder.tv_league.setText(league);
        holder.tv_tip.setText(prediction.getPredictionTip());
        holder.tv_post_date.setText(prediction.getPredictionDate());
        holder.tv_date_time.setText(getMatcheDateTime(prediction.getMatch().getMatchTime()));

        if(prediction.getStatus().equals("won")){
            //sets the label to win
            holder.status_vw.setBackground(context.getDrawable(R.drawable.ic_done));
        }
        else if(prediction.getStatus().equals("lost")){
            //sets the label to lose
            holder.status_vw.setBackground(context.getDrawable(R.drawable.lost));
        }
        else{
            //set it to pending
            holder.status_vw.setBackground(context.getDrawable(R.drawable.pending010));
        }
    }

    private void populateAdsView(adsHolder1 holder, UnifiedNativeAd nativeAd) {
        //bind the ads content to the UI
        ((TextView)holder.getUnifiedAdView().getHeadlineView()).setText(nativeAd.getHeadline());
        ((TextView)holder.getUnifiedAdView().getBodyView()).setText(nativeAd.getBody());
        ((Button)holder.getUnifiedAdView().getCallToActionView()).setText(nativeAd.getCallToAction());

        if(nativeAd.getBody() == null){
            holder.getUnifiedAdView().getBodyView().setVisibility(View.INVISIBLE);
        }
        else {
            holder.getUnifiedAdView().getBodyView().setVisibility(View.VISIBLE);
            ((TextView)holder.getUnifiedAdView().getBodyView()).setText(nativeAd.getBody());
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

    private String getMatcheDateTime(long unix_time){
        Date dt = new Date(unix_time * 1000L);
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.setTimeZone(TimeZone.getDefault());
        int t = c.get(Calendar.DAY_OF_MONTH);
        int x = c.get(Calendar.MONTH) + 1;
        int h = c.get(Calendar.HOUR_OF_DAY);
        int m = c.get(Calendar.MINUTE);
        String day, month, hour, min;
        //sets th day
        if(t < 10)
            day = "0" + t;
        else
            day = t + "";
        //sets the month
        if(x < 10)
            month = "0" + x;
        else
            month = x + "";

        String time = getMatchTime(unix_time);
        return day + "/" + month + "/" + c.get(Calendar.YEAR) + "  " + time;
    }

    private String getMatchTime(long unix) {
        Date date = new Date(unix * 1000L);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.setTimeZone(TimeZone.getDefault());
        int h = c.get(Calendar.HOUR_OF_DAY);
        int m = c.get(Calendar.MINUTE);
        String hour,minutes;
        if(h < 10)
            hour = "0" + h;
        else
            hour = h + "";

        if(m < 10)
            minutes = "0" + m;
        else
            minutes = m + "";

        return hour + ":" + minutes;
    }


}

