package com.mycornership.betlink.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.core.Amplify;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.mycornership.betlink.R;
import com.mycornership.betlink.activities.CelebActivity;
import com.mycornership.betlink.models.BirthdaySticker;
import com.mycornership.betlink.models.Celebrant;
import com.mycornership.betlink.models.DataResponse;
import com.mycornership.betlink.networks.RestApi;
import com.mycornership.betlink.networks.RestService;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CelebAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_LOAD = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private static final int VIEW_TYPE_ADS = 2;
    private Context context;
    private List<Object> celebs;
    private String applink = "https://sportinglinks.page.link/birthday";

    public CelebAdapter(Context c, List<Object> objects) {
        this.context = c;
        this.celebs = objects;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_ITEM){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.celeb_inflator, parent, false);
            return new celebHolder(view);
        }
        else if(viewType == VIEW_TYPE_LOAD){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_inflator, parent, false);
            return new loadHolder(view);
        }
        else {
            View ads = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_adds_inflator, parent, false);
            return new adsHolder(ads);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof celebHolder){
            //display matches
            populatesCeleb((celebHolder)holder, position);
        }
        else if(holder instanceof adsHolder){
            UnifiedNativeAd nativeAd = (UnifiedNativeAd)celebs.get(position);
            populatesAds((adsHolder)holder, nativeAd);
        }
        else {
            //shows the loading bar
            ((loadHolder)holder).bar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return celebs == null ? 0 : celebs.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object object = celebs.get(position);
        if( object instanceof Celebrant){

            return VIEW_TYPE_ITEM;
        }
        else if(object instanceof UnifiedNativeAd){

            return VIEW_TYPE_ADS;
        }
        else {

            return VIEW_TYPE_LOAD;
        }
    }

    private class celebHolder extends RecyclerView.ViewHolder {
        View love, like, share, message;
        ImageView photo;
        TextView tv_lk, tv_lv, tv_wlc, tv_note;
        public celebHolder(View view) {
            super(view);
            love = view.findViewById(R.id.love);
            like = view.findViewById(R.id.like);
            share = view.findViewById(R.id.share);
            message = view.findViewById(R.id.message);
            tv_lk = view.findViewById(R.id.like_count);
            tv_lv = view.findViewById(R.id.love_count);
            tv_wlc = view.findViewById(R.id.celeb_mssg_name);
            tv_note = view.findViewById(R.id.note);
            photo = view.findViewById(R.id.celeb_img);
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

        public adsHolder(@NonNull View itemView) {
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

    private void populatesCeleb(celebHolder holder, int position) {
        Celebrant celeb = (Celebrant)celebs.get(position);
        String no_lv = "" + celeb.getNoLoves();
        String no_lk = "" + celeb.getNoLikes();
        holder.tv_lv.setText(no_lv);
        holder.tv_lk.setText(no_lk);
        //display photo
        Glide.with(context).load(celeb.getPhoto()).into(holder.photo);
        holder.tv_wlc.setText("Happy Birthday " + celeb.getName());
        holder.tv_note.setText("" + celeb.getNote());

        holder.itemView.setOnClickListener(v ->{
            //opens the celebrant activity
            Intent i = new Intent(context, CelebActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("note", celeb.getNote());
            i.putExtra("message", celeb.getMessage());
            i.putExtra("bio", celeb.getShortBio());
            i.putExtra("photo", celeb.getPhoto());
            i.putExtra("photo2", celeb.getPhoto2());
            i.putExtra("name", celeb.getName());
            i.putExtra("achievements", celeb.getAchievements());
            i.putExtra("video", celeb.getVideo());
            i.putExtra("id", celeb.getId());
            //launch the activity
            context.startActivity(i);
        });
        //sends a love sticker to the player
        holder.love.setOnClickListener(l ->{
            if(Amplify.Auth.getCurrentUser() != null) {
                //increase the number loved
                int x = celeb.getNoLoves() + 1;
                holder.tv_lv.setText(" " + x);

                String username = Amplify.Auth.getCurrentUser().getUsername();
                BirthdaySticker sticker = new BirthdaySticker();
                sticker.setCelebrant(celeb.getName());
                sticker.setUsername(username);
                sticker.setSticker("love");
                //show love
                loveDialog();
                //communicate online
                sendSticker(sticker);
            }
            else{
                Toast.makeText(context, "signing required.", Toast.LENGTH_SHORT).show();
            }
        });
        //sends a like sticker to the player
        holder.like.setOnClickListener(l ->{
            if(Amplify.Auth.getCurrentUser() != null) {
                //increase the number liked
                int x = celeb.getNoLikes() + 1;
                holder.tv_lk.setText(" " + x);

                String username = Amplify.Auth.getCurrentUser().getUsername();
                BirthdaySticker sticker = new BirthdaySticker();
                sticker.setCelebrant(celeb.getName());
                sticker.setUsername(username);
                sticker.setSticker("like");
                //show like
                likeDialog();
                //communicate online
                sendSticker(sticker);
            }
            else{
                Toast.makeText(context, "signing required.", Toast.LENGTH_SHORT).show();
            }
        });
        //share the celebrant birthday on social media
        holder.share.setOnClickListener(l ->{
            //build the shared body
            String body = "Join us @SPORTINGLINK  \n as we celebrate " + celeb.getName() + " \n on his special day.  \n" + applink;
            //performs share of the tips to social group
            Intent shareIntent =  new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shareIntent.putExtra(Intent.EXTRA_TITLE,"Happy Birthday " + celeb.getName());
            shareIntent.putExtra(Intent.EXTRA_TEXT, body);
            context.startActivity(shareIntent);
        });

    }

    private void populatesAds(adsHolder holder, UnifiedNativeAd nativeAd) {
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

    private void sendSticker(BirthdaySticker sticker){
        RestApi api = RestService.getInstance().create(RestApi.class);
        Call<DataResponse> call = api.birthDaySticker(sticker);
        call.enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {

            }

            @Override
            public void onFailure(Call<DataResponse> call, Throwable t) {

            }
        });
    }

    private void loveDialog(){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.love_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 500);
    }

    private void likeDialog(){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.like_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 500);
    }

}
