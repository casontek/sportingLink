package com.mycornership.betlink.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.core.Amplify;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.material.button.MaterialButton;
import com.mycornership.betlink.R;
import com.mycornership.betlink.activities.PredictionViewActivity;
import com.mycornership.betlink.models.DataResponse;
import com.mycornership.betlink.models.Like;
import com.mycornership.betlink.models.Prediction;
import com.mycornership.betlink.models.User;
import com.mycornership.betlink.networks.RestApi;
import com.mycornership.betlink.networks.RestService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TipsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_LOAD = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private static final int VIEW_TYPE_ADS = 2;
    private List<Object> predictionList = new ArrayList<>();
    private Context context;
    private String category;
    private String applink = "https://sportinglinks.page.link/tip";
    private boolean isAdmin = false;

    public TipsAdapter(Context c, List<Object> objs, String cat) {
        this.predictionList = objs;
        this.context = c;
        this.category = cat;
        //initialize the user
        findUser();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_ITEM){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bet_inflator, parent, false);
            return new myHolder(view);
        }
        else if(viewType == VIEW_TYPE_ADS) {
            View adsView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ads_inflator, parent, false); //layout_adds_view
            return new adsHolder1(adsView);
        }
        else {
            View loadView = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_inflator, parent, false); //load_layout_inflator
            return new loadHolder(loadView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof  myHolder){
            //displays the tip item
            populateViews((myHolder)holder , position);
        }
        else if(holder instanceof adsHolder1){
            //displays the ads widget
            UnifiedNativeAd nativeAd = (UnifiedNativeAd)predictionList.get(position);
            displayAds((adsHolder1)holder, nativeAd);
        }
        else {
            //displays the loading progress bar
            ((loadHolder) holder).bar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return predictionList == null ? 0 : predictionList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object object = predictionList.get(position);
        if( object instanceof Prediction){

            return VIEW_TYPE_ITEM;
        }
        else if(object instanceof UnifiedNativeAd){ //replace Integer with 'UnifiedNativeAd'

            return VIEW_TYPE_ADS;
        }
        else {

            return VIEW_TYPE_LOAD;
        }
    }

    private class myHolder extends RecyclerView.ViewHolder {
        TextView tv_postDate, tv_teams, tv_league, tv_tip, tv_hint, tv_mtime, tv_vote;
        View v_status, v_share, v_vote;
        CircleImageView img_profile;
        public myHolder(View view) {
            super(view);
            tv_hint = view.findViewById(R.id.hint);
            tv_postDate = view.findViewById(R.id.post_date);
            tv_league = view.findViewById(R.id.league);
            tv_tip = view.findViewById(R.id.tip);
            tv_mtime = view.findViewById(R.id.match_time);
            tv_teams = view.findViewById(R.id.teams);
            v_share = view.findViewById(R.id.share);
            v_status = view.findViewById(R.id.tip_status);
            v_vote = view.findViewById(R.id.vote);
            tv_vote = view.findViewById(R.id.no_liked);
            img_profile = view.findViewById(R.id.profile_pix);
        }
    }

    private class adsHolder1 extends RecyclerView.ViewHolder {
        private UnifiedNativeAdView adView;

        public adsHolder1(View adsView) {
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

    private class loadHolder extends RecyclerView.ViewHolder {
        ProgressBar bar;
        public loadHolder(View loadView) {
            super(loadView);
            bar = loadView.findViewById(R.id.loading_bar);
        }
    }

    private void populateViews(myHolder holder, int position) {
        Prediction tip = (Prediction)predictionList.get(position);
        holder.tv_teams.setText(tip.getMatch().getHomeTeam().getName() + " - " + tip.getMatch().getAwayTeam().getName());
        holder.tv_tip.setText(tip.getPredictionTip());
        holder.tv_hint.setText(tip.getPredictionHint());
        holder.tv_postDate.setText(getPostDate(tip.getPredictionDate()));
        holder.tv_league.setText(tip.getMatch().getLeague().getName() + " " + tip.getMatch().getLeague().getCountry());
        //sets the match date and time
        long unix_time = tip.getMatch().getMatchTime();
        holder.tv_mtime.setText(getMatcheDateTime(unix_time));
        //sets the like number
        holder.tv_vote.setText("" + tip.getTipslike().size());
        //display the user photo
        Glide.with(context).load(tip.getUserPhoto()).diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.profile_pix).into(holder.img_profile);
        //sets the status of the prediction
        if(tip.getStatus().equals("won")){
            //sets the label to win
            holder.v_status.setBackground(context.getDrawable(R.drawable.ic_done));
        }
        else if(tip.getStatus().equals("lost")){
            //sets the label to lose
            holder.v_status.setBackground(context.getDrawable(R.drawable.lost));
        }
        else{
            //set it to pending
            holder.v_status.setBackground(context.getDrawable(R.drawable.pending010));
        }
        holder.v_vote.setOnClickListener( v ->{
            //vote for the tip
            if(Amplify.Auth.getCurrentUser() != null){
                String username = Amplify.Auth.getCurrentUser().getUsername();
                boolean hasLiked = false;
                List<Like> likes = tip.getTipslike();
                for(int i=0; i < likes.size(); i++){
                    Like l = likes.get(i);
                    if(l.getUsername().equals(username)){
                        hasLiked = true;
                        return;
                    }
                }
                if(!hasLiked){
                    //like now
                    int x = tip.getTipslike().size() + 1;
                    holder.tv_vote.setText("" + x);
                    holder.v_vote.setBackground(context.getResources().getDrawable(R.drawable.liked_ico01));
                    Like lk = new Like();
                    lk.setUsername(username);
                    lk.setPredictionId(tip.getId());
                    tip.getTipslike().add(lk);
                    //make a network like
                    commitLike(lk);
                }
            }
            else{
                Toast.makeText(context, "you are not logged-In", Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnClickListener( v ->{
            //opens the detail screen
            Intent i = new Intent(context, PredictionViewActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("photo", tip.getUserPhoto());
            i.putExtra("poster", tip.getUsername());
            i.putExtra("category", category);
            i.putExtra("homeTeam", tip.getMatch().getHomeTeam().getName());
            i.putExtra("awayTeam", tip.getMatch().getAwayTeam().getName());
            i.putExtra("homeLogo",tip.getMatch().getHomeTeam().getLogo());
            i.putExtra("awayLogo", tip.getMatch().getAwayTeam().getLogo());
            i.putExtra("status", tip.getStatus());
            i.putExtra("matchDate", tip.getMatch().getMatchDate());
            String tm = String.valueOf(tip.getMatch().getMatchTime());
            String match_status = tip.getMatch().getStatus();
            i.putExtra("matchTime", tm);
            i.putExtra("matchStatus", match_status);
            i.putExtra("score", tip.getMatch().getHomeGoal() + " - " + tip.getMatch().getAwayGoal());
            i.putExtra("tip", tip.getPredictionTip());
            i.putExtra("id", tip.getId());

            //starts the preview activities
            context.startActivity(i);

        });

        //checks if user has liked before
        if(Amplify.Auth.getCurrentUser() != null) {
            String username = Amplify.Auth.getCurrentUser().getUsername();
            List<Like> likes = tip.getTipslike();
            boolean isLiked = false;
            for (Like l : likes) {
                if (l.getUsername().equals(username)) {
                    isLiked = true;
                }
            }
            if (isLiked == true)
                holder.v_vote.setBackground(context.getResources().getDrawable(R.drawable.liked_ico01));
            else
                holder.v_vote.setBackground(context.getResources().getDrawable(R.drawable.like_ico02));

        }
        //status listener
        holder.v_status.setOnClickListener(v ->{
            if(isAdmin){
                //update the tip status
                updateTip(tip.getId(), tip.getUsername());
            }
        });
        //share button listener
        //add action listener to the share and like buttons
        holder.v_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cnt = "";
                if(!tip.getMatch().getLeague().getCountry().equals("World"))
                    cnt = tip.getMatch().getLeague().getCountry();
                //build the shared body
                String body = "*"+tip.getMatch().getHomeTeam().getName() + " Vs "
                        + tip.getMatch().getAwayTeam().getName() + "*" +
                        '\n' + getMatcheDateTime(tip.getMatch().getMatchTime())
                        + '\n' + tip.getMatch().getLeague().getName() + " " + cnt
                         + '\n' + "prediction tip: " + tip.getPredictionTip()
                        + '\n' + applink;
                //performs share of the tips to social group
                Intent shareIntent =  new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.putExtra(Intent.EXTRA_TITLE,"Sporting Community");
                shareIntent.putExtra(Intent.EXTRA_TEXT, body);
                context.startActivity(shareIntent);
            }
        });

    }

    private void displayAds(adsHolder1 holder, UnifiedNativeAd nativeAd) {
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
        //sets the hour of the day
        if(h < 10)
            hour = "0" + h;
        else
            hour = h + "";
        //sets the minutes of the hour
        if(m < 10)
            min = "0" + m;
        else
            min = x + "";
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

    private String getPostDate(String dt){
        String month = dt.substring(0, dt.indexOf('/'));
        String m2 = month;
        String day = dt.substring(dt.indexOf('/') + 1, dt.lastIndexOf('/'));
        int day1 = Integer.parseInt(day);
        //sets the month label
        int m = Integer.parseInt(month);
        switch (m){
            case 1: month = "Jan"; break;
            case 2: month = "Feb"; break;
            case 3: month = "Mar"; break;
            case 4: month = "Apr"; break;
            case 5: month = "May"; break;
            case 6: month = "Jun"; break;
            case 7: month = "Jul"; break;
            case 8: month = "Aug"; break;
            case 9: month = "Sep"; break;
            case 10:month = "Oct"; break;
            case 11:month = "Nov"; break;
            case 12: month = "Dec";break;
        }

        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int d = c.get(Calendar.DAY_OF_MONTH);
        int mt = c.get(Calendar.MONTH) + 1;
        String dd = d +"";
        String mm = mt +"";
        String f = day + "/" + m2;
        String f2 = dd + "/" + mm;

        if(f.equals(f2)){
            return "Today";
        }
        else if(d == (day1 + 1) && m == mt){
            return "Yesterday";
        }
        else if(d == 1 && (mt == 1 || mt == 2 || mt == 4 || mt == 6 || mt == 8 || mt == 9 || mt == 10 || mt == 12) && day1 == 31){
            return  "Yesterday";
        }
        else if(d == 1 && (mt == 3 || mt == 5 || mt == 7 || mt == 11) && day1 == 30){
            return "Yesterday";
        }
        else{
            return month + ", " + day;
        }
    }

    private void commitLike(Like lk){
        RestApi api = RestService.getInstance().create(RestApi.class);
        Call<DataResponse> likeCall = api.likeTip(lk);
        likeCall.enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                if(response.isSuccessful())
                Log.d("tip_like", response.body().toString());
            }

            @Override
            public void onFailure(Call<DataResponse> call, Throwable t) {
                Log.d("tip_like", t.getMessage());
            }
        });
    }

    private void findUser(){
        if(Amplify.Auth.getCurrentUser() != null) {
            String username = Amplify.Auth.getCurrentUser().getUsername();

            RestApi api = RestService.getInstance().create(RestApi.class);
            Call<User> userCall = api.getUser(username);
            userCall.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()) {
                        User user = response.body();
                        if (user != null) {
                            if(user.getRole().equals("ADMIN")){
                                isAdmin = true;
                            }
                            else{
                                Log.d("user_", "user not an admin" + user.getRole());
                            }
                        }
                    }

                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                }
            });
        }
    }

    private void updateTip(long id, String user){
        String tipId = Long.toString(id);
        Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.prediction_update_dialog);

        Spinner spn_status = dialog.findViewById(R.id.combo_status);
        Spinner spn_grade = dialog.findViewById(R.id.combo_amount);
        ProgressBar prg = dialog.findViewById(R.id.progress);
        prg.setVisibility(View.GONE);
        MaterialButton btn_update = dialog.findViewById(R.id.btn_update);
        btn_update.setOnClickListener(l ->{
            String status = spn_status.getSelectedItem().toString();
            String grd = spn_grade.getSelectedItem().toString();
            updatePrediction(status, tipId, user, grd, prg, dialog);
        });
        dialog.show();
    }

    private void updatePrediction(String status, String id, String user, String grade, ProgressBar prg, Dialog dialog){
        //show progress
        prg.setVisibility(View.VISIBLE);
        RestApi api = RestService.getInstance().create(RestApi.class);
        Call<DataResponse> call = api.updateStatus(status, id, user, grade);
        call.enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                prg.setVisibility(View.GONE);
                if(response.isSuccessful()){
                    Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                Log.d("res_", response.toString());
            }

            @Override
            public void onFailure(Call<DataResponse> call, Throwable t) {
                prg.setVisibility(View.GONE);
                Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
