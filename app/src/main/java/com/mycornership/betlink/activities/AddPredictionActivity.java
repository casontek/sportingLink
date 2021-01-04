package com.mycornership.betlink.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.core.Amplify;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mycornership.betlink.R;
import com.mycornership.betlink.adapters.ComboAdapter;
import com.mycornership.betlink.database.UserVModel;
import com.mycornership.betlink.models.DataResponse;
import com.mycornership.betlink.models.Game;
import com.mycornership.betlink.models.GameResponse;
import com.mycornership.betlink.models.Match;
import com.mycornership.betlink.models.Prediction;
import com.mycornership.betlink.models.User;
import com.mycornership.betlink.networks.RestApi;
import com.mycornership.betlink.networks.RestService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPredictionActivity extends AppCompatActivity {
    ProgressBar bar;
    Spinner lg, mtch, tps, cat_spn;
    EditText edt_hint;
    TextView cat_tv;
    MaterialButton btn_submit;
    View rlt;
    TextInputEditText edt_hs, edt_as;
    List<Game> games = new ArrayList<>();
    List<String> leagues = new ArrayList<>();
    List<String> league_countries = new ArrayList<>();
    List<String> matches = new ArrayList<>();
    List<String> fixtureIds = new ArrayList<>();
    private String fixtureId;
    private String match_tip;
    private String category = "basic";
    private String role = "USER";
    private String username;
    private String user_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_prediction);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //initialize widgets
        init();
        //load user
        //getUserInfo();
        userData();
        //loads the available games
        loadGames();
        //add listener to the spinners
        addListerToSpinner();
        //add listener to button submit
        btn_submit.setOnClickListener( v ->{
            if(!TextUtils.isEmpty(fixtureId) && !TextUtils.isEmpty(match_tip)){
                String hint = edt_hint.getText().toString();
                predict(hint);
            }
        });
    }

    private void init(){
        //initialize components
        bar = findViewById(R.id.loading_bar);
        bar.setVisibility(View.GONE);

        lg = findViewById(R.id.spinner_league);
        mtch = findViewById(R.id.spinner_match);
        tps = findViewById(R.id.spinner_tip);
        tps.setAdapter(new ComboAdapter(this, Arrays.asList(getResources().getStringArray(R.array.tips))));

        edt_hint = findViewById(R.id.tip_hint);
        btn_submit = findViewById(R.id.btn_submit);

        cat_tv = findViewById(R.id.cat);
        cat_spn = findViewById(R.id.spinner_cat);

        rlt = findViewById(R.id.rlt);
        edt_hs = findViewById(R.id.h_score);
        edt_as = findViewById(R.id.a_score);
        rlt.setVisibility(View.GONE);

    }

    private void loadGames(){
        //show progress bar
        bar.setVisibility(View.VISIBLE);
        //makes network communication
        RestApi api = RestService.getInstance().create(RestApi.class);
        Call<GameResponse> gameData = api.fetchGames();
        gameData.enqueue(new Callback<GameResponse>() {
            @Override
            public void onResponse(Call<GameResponse> call, Response<GameResponse> response) {
                //hides the progress bar
                bar.setVisibility(View.GONE);
                if(response.isSuccessful()){
                    if(response.body().getCode() == 200){
                        games = response.body().getData();
                        if(games.size() > 0) {
                            for (int i = 0; i < games.size(); i++) {
                                Match match = games.get(i).getMatch();
                                String lc = match.getLeague().getName() + " - " + match.getLeague().getCountry();
                                if (!league_countries.contains(lc)) {
                                    league_countries.add(lc);
                                    leagues.add(match.getLeague().getName());
                                }
                            }
                            //bind leagues with the spinner
                            bindUI();
                        }
                        else{
                            lg.setAdapter(new ComboAdapter(getBaseContext(), Arrays.asList("empty game")));
                        }
                    }
                    else{
                        Toast.makeText(getBaseContext(), "games failed to load", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<GameResponse> call, Throwable t) {
                Log.d("game", t.getMessage());
                //hides the progress bar
                bar.setVisibility(View.GONE);
                Toast.makeText(getBaseContext(), "network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindUI(){
        //Collections.sort(league_countries, (a, b) -> a.compareTo(b));
        lg.setAdapter(new ComboAdapter(getBaseContext(),league_countries));
    }

    private void addListerToSpinner(){
        //listen to league spinner
        lg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int x = lg.getSelectedItemPosition();
                if(leagues.size() > 0) {
                    String item = leagues.get(x);
                    populateMatch(item);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //listen to match spinner
        mtch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(fixtureIds.size() > 0)
                fixtureId = fixtureIds.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //listen to tip spinner
        tps.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                match_tip = tps.getSelectedItem().toString();
                if(match_tip.equals("Correct Score")){
                    rlt.setVisibility(View.VISIBLE);
                }
                else{
                    rlt.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void populateMatch(String item) {
        //recreates the match list
        matches = new ArrayList<>();
        fixtureIds = new ArrayList<>();
        //iterate over the games to get matches belonging to a particular league
        for(int i=0; i < games.size(); i++){
            Match match = games.get(i).getMatch();
            String league = match.getLeague().getName();
            if(item.equals(league)){
                String m = match.getHomeTeam().getName() + " - " + match.getAwayTeam().getName();
                matches.add(m);
                long id = match.getMatchId();
                fixtureIds.add(String.valueOf(id));
            }
        }
        //show match spinner
        mtch.setAdapter(new ComboAdapter(getBaseContext(), matches));
    }

    private void predict(String hint) {
        //iterate over the games and use the fixture id to get the match you are predicting for
        for(Game gm: games){
            Match m = gm.getMatch();
            String fId = String.valueOf(m.getMatchId());
            if(fId.equals(fixtureId)){
                //halt the loop and continue with the prediction process
                Prediction p = new Prediction();
                p.setMatch(m);
                p.setPredictionHint(hint);
                if(match_tip.equals("Correct Score")){
                    String hs = edt_hs.getText().toString();
                    String as = edt_as.getText().toString();
                    String result  = hs + " - " + as + " (Correct Score)";
                    p.setPredictionTip(result);
                }
                else{
                    p.setPredictionTip(match_tip);
                }
                p.setCategory(category);
                p.setUsername(username);
                p.setUserPhoto(user_photo);
                if(!role.equals("USER")){
                    p.setCategory(cat_spn.getSelectedItem().toString());
                }
                //commit prediction to network
                commit(p);
                //return after predicting
                return;
            }
        }
    }

    private void commit(Prediction p) {
        //shows progress
        bar.setVisibility(View.VISIBLE);
        //make network communication
        RestApi api = RestService.getInstance().create(RestApi.class);
        Call<DataResponse> predict = api.addTip(p);
        predict.enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                //hides the progress
                bar.setVisibility(View.GONE);
                if(response.isSuccessful())
                    Toast.makeText(getBaseContext(), "prediction added", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<DataResponse> call, Throwable t) {
                //hides the progress
                bar.setVisibility(View.GONE);

                Log.d("predict", t.getMessage());
                Toast.makeText(getBaseContext(), "prediction failed. try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserInfo(){
        String u = Amplify.Auth.getCurrentUser().getUsername();
        if(!TextUtils.isEmpty(u)){
            RestApi api = RestService.getInstance().create(RestApi.class);
            Call<User> userCall = api.getUser(u);
            userCall.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if(response.isSuccessful()){
                        User user = response.body();
                        if(user != null){
                            username = user.getUsername();
                            user_photo = user.getPhoto();
                            role = user.getRole();
                            if(!user.getRole().equals("USER")){
                                cat_tv.setVisibility(View.VISIBLE);
                                cat_spn.setVisibility(View.VISIBLE);
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

    private void userData(){
        if(Amplify.Auth.getCurrentUser() != null) {
            String user = Amplify.Auth.getCurrentUser().getUsername();

            UserVModel vModel = ViewModelProviders.of(this).get(UserVModel.class);
            User userInfo = vModel.get(user);
            if(userInfo != null){
                username = userInfo.getUsername();
                user_photo = userInfo.getPhoto();
                role = userInfo.getRole();
                if(!userInfo.getRole().equals("USER")){
                    cat_tv.setVisibility(View.VISIBLE);
                    cat_spn.setVisibility(View.VISIBLE);
                }
            }
            else{
                getUserInfo();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

}
