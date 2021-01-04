package com.mycornership.betlink.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.ActivityNavigator;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.core.Action;
import com.amplifyframework.core.Amplify;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.mycornership.betlink.R;
import com.mycornership.betlink.activities.AddPredictionActivity;
import com.mycornership.betlink.activities.AuthActivity;
import com.mycornership.betlink.activities.NavHomeActivity;
import com.mycornership.betlink.activities.NotificationActivity;
import com.mycornership.betlink.activities.SubscriptionActivity;
import com.mycornership.betlink.database.UserVModel;
import com.mycornership.betlink.models.Cashout;
import com.mycornership.betlink.models.DataResponse;
import com.mycornership.betlink.models.Notification;
import com.mycornership.betlink.models.User;
import com.mycornership.betlink.networks.RestApi;
import com.mycornership.betlink.networks.RestService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    List<Notification> notifications = new ArrayList<>();
    MaterialButton btn_login, btn_reg, btn_predict, btn_logout;
    TextView  tv_pwin, tv_tpost, tv_name, tv_wallet;
    CircleImageView profile_pix;
    ProgressBar bar;
    View edit_pix, pContainer, sContainer;
    MaterialButton cash_out;
    String user, photo = null;
    private AdView adView;
    private View vw_not, dot_not;
    private static final int IMAGE_PERMISSION_CODE = 1;
    private static final int PICK_PROFILE_IMAGE = 2;
    private User account = new User();

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //initialize components
        init();
        //adds action listener to the buttons and views
        addListener();
        //checks if the user is currently logged in
        if(Amplify.Auth.getCurrentUser() == null){
            sContainer.setVisibility(View.VISIBLE);
            pContainer.setVisibility(View.GONE);
        }
        else {
            user = Amplify.Auth.getCurrentUser().getUsername();
            //load users info from local database
            getUser();
            sContainer.setVisibility(View.GONE);
            pContainer.setVisibility(View.VISIBLE);
            //load user info
             findUser();
        }
        //loads banner ads
        loadBanner();
        //check notification
        notificationCheck();

    }

    private void init(){
        btn_login = getView().findViewById(R.id.btn_login);
        btn_reg = getView().findViewById(R.id.btn_register);
        btn_predict = getView().findViewById(R.id.btn_predict);
        btn_logout = getView().findViewById(R.id.logout);

        bar = getView().findViewById(R.id.loading_bar);
        bar.setVisibility(View.GONE);

        vw_not = getView().findViewById(R.id.cashout_notify);
        dot_not = getView().findViewById(R.id.dot_notify);
        dot_not.setVisibility(View.GONE);

        tv_name = getView().findViewById(R.id.profile_name);
        tv_tpost = getView().findViewById(R.id.total_post);
        tv_pwin = getView().findViewById(R.id.total_win);
        tv_wallet = getView().findViewById(R.id.wallet_amt);

        profile_pix = getView().findViewById(R.id.profile_pix);
        edit_pix = getView().findViewById(R.id.pix_edit);
        pContainer = getView().findViewById(R.id.profile_container);
        pContainer.setVisibility(View.GONE);
        sContainer = getView().findViewById(R.id.sign_in_layout);
        sContainer.setVisibility(View.GONE);
        cash_out = getView().findViewById(R.id.btn_wallet);

        adView = getView().findViewById(R.id.adView2);

        MaterialButton btnSub = getView().findViewById(R.id.btn_subscribe);
        //navigates to the subscription activity when click subscribe button
        btnSub.setOnClickListener(v ->{
            Intent i = new Intent(getContext(), SubscriptionActivity.class);
            startActivity(i);
        });

        //add listener to the show notification view
        vw_not.setOnClickListener( v ->{
            if(notifications.size() > 0)
            showNotificationDialog();
            else
                Toast.makeText(getContext(), "notification not arrived", Toast.LENGTH_SHORT).show();
        });
    }

    private void addListener() {
        btn_predict.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), AddPredictionActivity.class);
            startActivity(i);
        });

        btn_login.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), AuthActivity.class);
            startActivity(i);
        });

        btn_reg.setOnClickListener(v ->{
            Intent i = new Intent(getContext(), AuthActivity.class);
            i.putExtra("register", true);
            startActivity(i);
        });

        btn_logout.setOnClickListener(v ->{
            //log the current user out
            Snackbar.make(getView(), "Do you want to Log-Out?", 1500).setAction("Yes", v1 -> {
                signOut();
            }).show();
        });
        //edit pix
        edit_pix.setOnClickListener(v ->{
            //change user profile pix
            runtimePermissionRequest();
        });
        //withdraw or user cash out
        cash_out.setOnClickListener(v ->{
            //try cash-out request for the user
            userCashOut();
        });
    }

    private void findUser(){
        //shows progress
        bar.setVisibility(View.VISIBLE);

        RestApi api = RestService.getInstance().create(RestApi.class);
        Call<User> userCall = api.getUser(user);
        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                //hides the progress
                bar.setVisibility(View.GONE);
                if(response.isSuccessful()){
                    User user = response.body();
                    account = user;
                    if(user != null){
                        UserVModel vModel = ViewModelProviders.of(ProfileFragment.this).get(UserVModel.class);
                        vModel.add(user);
                        //refresh user information
                        getUser();
                    }
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                //hides the progress
                bar.setVisibility(View.GONE);
            }
        });
    }

    private void userCashOut(){
        //checks if user wallet is up-to threshold
        double amt = Double.parseDouble(account.getWallet());
        if(amt >= 10.0){
            Dialog dialog = new Dialog(getActivity());
            dialog.setCanceledOnTouchOutside(true);
            dialog.setContentView(R.layout.cashout_layout);

            MaterialButton btn_submit = dialog.findViewById(R.id.btn_withdraw);
            TextInputEditText edt_amt = dialog.findViewById(R.id.cashout);
            btn_submit.setOnClickListener(v ->{
                String val = edt_amt.getText().toString();
                if(!TextUtils.isEmpty(val)){
                    dialog.dismiss();
                    submitWithdraw(val);
                }
            });
            //shows the dialog
            dialog.show();
        }
        else{
            new AlertDialog.Builder(getActivity())
                    .setMessage("Your wallet must reach  $10.0 to enable Cash-Out")
                    .setCancelable(true)
                    .setPositiveButton("Ok", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();

            Log.d("user_", "user cash out");
        }
    }

    private void signOut(){
        Amplify.Auth.signOut(() -> {
            Intent i = new Intent(getContext(), NavHomeActivity.class);
            startActivity(i);
        },
        error ->{
            Toast.makeText(getContext(), "error. try again!", Toast.LENGTH_SHORT).show();
        });
    }

    private void runtimePermissionRequest(){
        //checks if permission is already granted
        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //permission has not been granted before
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            //show the user the reason for requiring permission and continue
                Snackbar.make(getView(),"Allow SportingLink to access photos files on your device?", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Accept", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, IMAGE_PERMISSION_CODE);
                            }
                        }).show();
            }
            else{
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, IMAGE_PERMISSION_CODE);
            }
        }
        else {
            //continue to upload the image
            uploadPix();
        }
    }

    private void uploadPix(){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("image/*");
        startActivityForResult(i, PICK_PROFILE_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("permission_", "request permission result");
        //checks the kind of permission accepted
        if(requestCode == IMAGE_PERMISSION_CODE){
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                uploadPix();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_PROFILE_IMAGE && resultCode == Activity.RESULT_OK){
            Uri selected_img = data.getData();
            String[] image_path_column = {MediaStore.Images.Media.DATA};
            Cursor c = getContext().getContentResolver().query(selected_img, image_path_column, null, null, null);
            assert c != null;
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(image_path_column[0]);
            String image_path = c.getString(columnIndex);

            File image = new File(image_path);
            uploadAWS(image);
        }
    }

    private void uploadAWS(File image) {
        Amplify.Storage.uploadFile("user/profile-pix-"+ user + ".jpg", image,
                result ->{
                    Glide.with(getView()).load(Uri.parse(photo)).placeholder(R.drawable.profile_pix).into(profile_pix);
                    Toast.makeText(getContext(), "profile pix updated", Toast.LENGTH_SHORT).show();
                },
                error ->{
                    Toast.makeText(getContext(), "update failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void submitWithdraw(String val) {
        Cashout c = new Cashout();
        c.setAmount(val);
        c.setUsername(account.getUsername());
        c.setStatus("Pending");

        //shows progress
        bar.setVisibility(View.VISIBLE);

        RestApi api = RestService.getInstance().create(RestApi.class);
        Call<DataResponse> withdraw = api.withdraw(c);
        withdraw.enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                bar.setVisibility(View.GONE);
                if(response.isSuccessful())
                    if(response.body() != null){
                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        notificationCheck();
                    }
            }

            @Override
            public void onFailure(Call<DataResponse> call, Throwable t) {
                bar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "failed. try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBanner(){
        AdRequest request = new AdRequest.Builder().build();
        adView.loadAd(request);
        adView.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdClicked() {
                //notifyAdsAction();
            }
        });
    }

    private void getUser(){
        if(Amplify.Auth.getCurrentUser() != null){
            String username = Amplify.Auth.getCurrentUser().getUsername();
            UserVModel vModel = ViewModelProviders.of(this).get(UserVModel.class);
            User currentUser = vModel.get(username);

            if(currentUser != null){
                //display information of the user
                tv_tpost.setText("" + currentUser.getPost());
                tv_pwin.setText("" + currentUser.getWon());
                tv_name.setText("" + currentUser.getUsername());
                String amt = currentUser.getWallet();
                if(amt.contains(".")){
                    int x = amt.indexOf('.') + 4;
                    amt = amt.substring(0, x);
                }
                tv_wallet.setText("$" + amt);
                photo = currentUser.getPhoto();
                //display profile pix
                if(photo != null)
                Glide.with(getView()).load(Uri.parse(currentUser.getPhoto())).placeholder(R.drawable.profile_pix).into(profile_pix);
            }
        }
    }

    private void showNotificationDialog(){
        Intent i = new Intent(getContext(), NotificationActivity.class);
        startActivity(i);
    }

    private void notificationCheck(){
        if(Amplify.Auth.getCurrentUser() != null){
            String username = Amplify.Auth.getCurrentUser().getUsername();
            RestApi api = RestService.getInstance().create(RestApi.class);
            Call<List<Notification>> notCall = api.getNotification(username);
            notCall.enqueue(new Callback<List<Notification>>() {
                @Override
                public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                    if(response.isSuccessful()){
                        List<Notification> notificationList = response.body();
                        if(notificationList.size() > 0){
                            dot_not.setVisibility(View.VISIBLE);
                            notifications = notificationList;
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Notification>> call, Throwable t) {
                }
            });
        }
    }

}
