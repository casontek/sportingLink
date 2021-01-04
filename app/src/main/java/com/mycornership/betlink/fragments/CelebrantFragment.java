package com.mycornership.betlink.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.material.button.MaterialButton;
import com.mycornership.betlink.R;
import com.mycornership.betlink.adapters.CelebAdapter;
import com.mycornership.betlink.models.BirthdayResponse;
import com.mycornership.betlink.models.Celebrant;
import com.mycornership.betlink.networks.RestApi;
import com.mycornership.betlink.networks.RestService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CelebrantFragment extends Fragment {
    private RecyclerView recyclerView;
    private CelebAdapter adapter;
    private View no_celeb;
    private AdLoader adLoader;
    private List<Object> objects = new ArrayList<>();
    private List<Celebrant> celebs = new ArrayList<>();
    private ProgressBar loadinBar;
    private int count = 0, number = 1, inc = 1;
    private boolean isLoading = true;

    public CelebrantFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_celebrant, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //initialize components
        init();
        //load news items
        loadCelebs();
        //load native ads
        loadAds();
    }

    private void init() {
        loadinBar = getView().findViewById(R.id.loading_bar);
        no_celeb = getView().findViewById(R.id.no_celeb);
        //initialize the recyclerView
        recyclerView = getView().findViewById(R.id.celeb_recycler);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        adapter = new CelebAdapter(getContext(), objects);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    private void loadCelebs() {
        //load birthday celebrants
        RestApi api = RestService.getInstance().create(RestApi.class);
        Call<BirthdayResponse> dataCall = api.fetchBirthdays(getTodate());
        dataCall.enqueue(new Callback<BirthdayResponse>() {
            @Override
            public void onResponse(Call<BirthdayResponse> call, Response<BirthdayResponse> response) {
                if(response.isSuccessful()){
                    if(response.body().getCode() == 200){
                        celebs = response.body().getData();
                        objects.addAll(celebs);
                        adapter = new CelebAdapter(getContext(), objects);
                        recyclerView.setAdapter(adapter);

                        if(celebs.size() == 0)
                            no_celeb.setVisibility(View.VISIBLE);
                    }
                }
                //hides the progress bar
                isLoading = false;
                loadinBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<BirthdayResponse> call, Throwable t) {
                isLoading = false;
                loadinBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "network error!", Toast.LENGTH_SHORT);
            }
        });
    }

    private String getTodate(){
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int m = c.get(Calendar.MONTH) + 1;
        int d = c.get(Calendar.DAY_OF_MONTH);
        String mth, day;
        if(m < 10)
            mth = "0" + m;
        else
            mth = m + "";
        if(d < 10)
            day = "0" + d;
        else
            day = d + "";

        return mth + "-" + day;
    }

    private void loadAds(){
        adLoader = new AdLoader.Builder(getContext(), getString(R.string.ads_native_units))
                .forUnifiedNativeAd(unifiedNativeAd -> {
                    if(number == 1 && objects.size() >= 3){
                        objects.add(3, unifiedNativeAd);
                        adapter.notifyItemInserted(3);
                    }
                    //add at the last index of the list
                    if(objects.size() != 3  && objects.size() > 0 && number == 2) {
                        objects.add(objects.size(), unifiedNativeAd);
                        adapter.notifyItemChanged(objects.size());
                    }
                    number += 1;
                    inc += 1;
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdLeftApplication() {
                        super.onAdLeftApplication();
                        // notifyAdsAction("LeftApplication");
                    }

                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                        //notifyAdsAction("Clicked");
                    }

                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // Handle the failure by logging, altering the UI, and so on.
                        Log.d("ads_load_error", "error loading ads. " + errorCode);
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();
        adLoader.loadAds(new AdRequest.Builder().build(), 3);
    }

}
