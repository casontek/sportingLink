package com.mycornership.betlink.fragments.match;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.mycornership.betlink.R;
import com.mycornership.betlink.adapters.MatchAdapter;
import com.mycornership.betlink.models.Match;
import com.mycornership.betlink.models.MatchResponse;
import com.mycornership.betlink.networks.RestApi;
import com.mycornership.betlink.networks.RestService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchTab extends Fragment {
    private static String ARG_PARAM = "position";
    private static String ARG_PARAM2 = "date_match";
    private RecyclerView recyclerView;
    private MatchAdapter adapter;
    private List<Object> objects = new ArrayList<>();
    private List<Match> matches = new ArrayList<>();
    private boolean isLoading = true;
    private ProgressBar loadinBar;
    private int page = 0;
    private String date,label;

    public MatchTab() {
        // Required empty public constructor
    }

    public static MatchTab newInstance(String title, String date){
        MatchTab fragment = new MatchTab();
        Bundle arg = new Bundle();
        arg.putString(ARG_PARAM, title);
        arg.putString(ARG_PARAM2, date);
        fragment.setArguments(arg);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_match_tab, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //initialize components
        init();
        //load news items
        Bundle b = getArguments();
        //sets date and label of the day
        label = b.getString("position");
        date = b.getString("date_match");
        //load match fixtures with respect to date
        loadMatches(page);
        //add listener to the recycler view
        recyclerListener();
    }

    private void init() {
        loadinBar = getView().findViewById(R.id.loading_bar);
        //initialize the recyclerView
        recyclerView = getView().findViewById(R.id.match_recycler);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        adapter = new MatchAdapter(getContext(), objects);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    private void loadMatches(int page) {
        //make a network communication
        RestApi api = RestService.getInstance().create(RestApi.class);
        Call<MatchResponse> matchCall = api.fetchMatches(date, page);
        matchCall.enqueue(new Callback<MatchResponse>() {
            @Override
            public void onResponse(Call<MatchResponse> call, Response<MatchResponse> response) {

                if(response.isSuccessful()){
                    if(response.body().getCode() == 200){
                        matches = getFormattedMatches(response.body().getData());
                        if(page == 0) {
                            objects.addAll(matches);
                            adapter = new MatchAdapter(getContext(), objects);
                            recyclerView.setAdapter(adapter);
                        }
                        else{
                            //subsequent pages
                            objects.remove(objects.size() - 1);
                            int x = objects.size();
                            adapter.notifyItemRemoved(x);

                            objects.addAll(matches);
                            adapter.notifyDataSetChanged();
                            //close network loading handles
                            isLoading = false;
                        }
                    }
                }
                //hides the progress bar
                isLoading = false;
                loadinBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<MatchResponse> call, Throwable t) {
                if (page == 0){
                    loadinBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "network error!", Toast.LENGTH_SHORT);
                }
                else{
                    //subsequent pages
                    objects.remove(objects.size() - 1);
                    int x = objects.size();
                    adapter.notifyItemRemoved(x);
                }
                isLoading = false;
            }
        });

    }

    private List<Match> getFormattedMatches(List<Match> matchList){
        List<Match> matches1 = new ArrayList<>();
        List<Match> eng = new ArrayList<>();
        List<Match> other = new ArrayList<>();
        List<Match> italy = new ArrayList<>();
        List<Match> spain = new ArrayList<>();

        String prev = "";
        for(int i= 0; i < matchList.size(); i++){
            Match match = matchList.get(i);
            String current = match.getLeague().getName();
            if(prev.equals(current) && i != 0){
                match.getLeague().setName("");
            }
            //set the previous
            prev = current;
            matches1.add(match);
        }

        for(int i = 0; i < matches1.size(); i++){
            if(matches1.get(i).getLeague().getCountry().equals("England")){
                Match match = matches1.get(i);
                eng.add(match);
            }
            else if(matches1.get(i).getLeague().getCountry().equals("Italy")){
                Match match = matches1.get(i);
                italy.add(match);
            }
            else if(matches1.get(i).getLeague().getCountry().equals("Spain")){
                Match match = matches1.get(i);
                spain.add(match);
            }
            else{
                Match match = matches1.get(i);
                other.add(match);
            }
        }
        List<Match> combine = new ArrayList<>();
        combine.addAll(eng);
        combine.addAll(italy);
        combine.addAll(spain);
        combine.addAll(other);

        return combine;
    }

    private void recyclerListener(){
        //listen to whenever the recycler view scroll changes
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (manager != null && manager.findLastCompletelyVisibleItemPosition() == objects.size() - 1) {
                    if (!isLoading) {
                        isLoading = true;
                        loadMore();
                    }
                }
            }
        });

    }

    private void loadMore(){
        page += 1;
        objects.add(null);
        //notify adapter for the item change
        adapter.notifyItemInserted(objects.size() - 1);
        //load the next data on the list
        loadMatches(page);
    }

}
