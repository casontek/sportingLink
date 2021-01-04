package com.mycornership.betlink.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mycornership.betlink.R;
import com.mycornership.betlink.database.UserVModel;
import com.mycornership.betlink.fragments.tips.BasicTips;
import com.mycornership.betlink.fragments.tips.ControlledTips;
import com.mycornership.betlink.fragments.tips.SponsoredTips;
import com.mycornership.betlink.models.DataResponse;
import com.mycornership.betlink.models.User;
import com.mycornership.betlink.networks.RestApi;
import com.mycornership.betlink.networks.RestService;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NavHomeActivity extends AppCompatActivity {
    private BottomNavigationView navigationView;
    private static final int REQUEST_PERMISSION_CODE = 2;
    private String userToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_home);
        //sets up the bottom navigation
        setsUpBottomNavigation();
        //listen to the bottom navigation
        addBottomListener();
        //get intent
        Intent intent = getIntent();
        if(intent.hasExtra("login")){
            navigationView.setSelectedItemId(R.id.navigation_videos);
            Navigation.findNavController(this, R.id.fragment).navigate(R.id.profileFragment);
        }
        //fetch user data
        findUser();
        //login user
        dailyLogin();
        //register device identity
        firebaseNotify();
        //subscribe to news updates
        firebaseNews();
        //checks compactible play service
        playService();
    }

    private void setsUpBottomNavigation() {
        navigationView = findViewById(R.id.bottom_navigation);
        NavController controller = Navigation.findNavController(this, R.id.fragment);
        NavigationUI.setupWithNavController(navigationView, controller);
    }

    private void addBottomListener() {
        navigationView.setOnNavigationItemSelectedListener(item ->{
            if(item.getItemId() == R.id.navigation_tips){
                Navigation.findNavController(this, R.id.fragment).navigate(R.id.tipsFragment);
                return true;
            }
            else if(item.getItemId() == R.id.navigation_news){
                Navigation.findNavController(this, R.id.fragment).navigate(R.id.newsFragment);
                return true;
            }
            else if(item.getItemId() == R.id.navigation_matches){
                Navigation.findNavController(this, R.id.fragment).navigate(R.id.matchFragment);
                return true;
            }
            else if(item.getItemId() == R.id.navigation_videos){
                Navigation.findNavController(this, R.id.fragment).navigate(R.id.profileFragment);
                return true;
            }
            else if (item.getItemId() == R.id.navigation_celeb){
                Navigation.findNavController(this, R.id.fragment).navigate(R.id.celebrantFragment);
                return true;
            }
            else {
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //checks play service
        playService();
    }

    private void dailyLogin(){
        if(Amplify.Auth.getCurrentUser() != null){
            String username = Amplify.Auth.getCurrentUser().getUsername();
            RestApi api = RestService.getInstance().create(RestApi.class);
            Call<DataResponse> loginCall = api.dailyLogin(username);
            loginCall.enqueue(new Callback<DataResponse>() {
                @Override
                public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                    if(response.isSuccessful())
                        Log.d("login_", response.body().toString());
                }

                @Override
                public void onFailure(Call<DataResponse> call, Throwable t) {
                    Log.d("login_err_", t.toString());
                }
            });
        }
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
                            UserVModel vModel = ViewModelProviders.of(NavHomeActivity.this).get(UserVModel.class);
                            vModel.add(user);
                        }
                    }

                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                }
            });
        }
    }

    public void firebaseNotify() {
        //gets firebase token
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("notification_", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        userToken = token;
                        requestPermission();
                        // Log and toast
                        Log.d("notification_", token);
                    }
                });

    }

    private void firebaseNews(){
        FirebaseMessaging.getInstance().subscribeToTopic("sportingNews")
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(getBaseContext(), task.toString(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void playService(){
        //checks compactible play service
        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this);
    }

    private void requestPermission() {
        CoordinatorLayout layout = findViewById(R.id.layout);
        if (!(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
            //aske for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
                //show the user why you are requesting for permission
                Snackbar snackbar = Snackbar.make(layout, "BetLink needs access to your device identity to be able to give you latest in sports update and prediction tips.", 3000);
                snackbar.setActionTextColor(getResources().getColor(R.color.blue));
                snackbar.setAction("Accept", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActivityCompat.requestPermissions(NavHomeActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_PERMISSION_CODE);
                    }
                });
            } else {
                //continue request for the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_PERMISSION_CODE);
            }
        } else {
            TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String  device = manager.getImei();
                sendToken(device);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_PERMISSION_CODE){
            //checks whether the permission has been granted
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //then request for the device imei and send token by requesting permission check again
                requestPermission();
            }
        }
    }

    private void sendToken(String device) {
        RestApi api = RestService.getInstance().create(RestApi.class);
        Call<DataResponse> responseCall = api.sendToken(device, userToken);
        responseCall.enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {

            }

            @Override
            public void onFailure(Call<DataResponse> call, Throwable t) {

            }
        });
    }

}
