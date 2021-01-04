package com.mycornership.betlink.fragments.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mycornership.betlink.R;
import com.mycornership.betlink.activities.NavHomeActivity;

public class Login extends Fragment {
    TextInputEditText edt_user, edt_pass;
    TextInputLayout layout1, layout2;
    MaterialButton btn_login, btn_reg, btn_forgot;
    ProgressBar bar;

    public Login(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //initialize widgets
        init();
        //listen to button clicks
        addListener();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void init(){
        edt_user = getView().findViewById(R.id.username);
        edt_pass = getView().findViewById(R.id.password);

        bar = getView().findViewById(R.id.loading_bar);
        bar.setVisibility(View.GONE);

        layout1 = getView().findViewById(R.id.layout1);
        layout2 = getView().findViewById(R.id.layout2);

        btn_login = getView().findViewById(R.id.btn_login);
        btn_forgot = getView().findViewById(R.id.forget_btn);
        btn_reg = getView().findViewById(R.id.btn_acct_new);
    }

    private void addListener(){
        //listen to login button
        btn_login.setOnClickListener(v ->{
            String username = edt_user.getText().toString();
            String password = edt_pass.getText().toString();
            //validate the input
            if(valid(username, password)){
                login(username, password);
            }
        });
        //listen to register button
        btn_reg.setOnClickListener(v ->{
            Navigation.findNavController(v).navigate(R.id.register);
        });
        //listen to forgot button
        btn_forgot.setOnClickListener(v ->{
            Navigation.findNavController(v).navigate(R.id.forgotPassword);
        });
    }

    private boolean valid(String username, String password) {
        //checks username
        if(TextUtils.isEmpty(username)){
            layout1.setError("enter username");
            return false;
        }
        //checks password
        if(TextUtils.isEmpty(password)){
            layout2.setError("enter password");
            return false;
        }

        return true;
    }

    private void login(String username, String password) {
        //shows progress
        bar.setVisibility(View.VISIBLE);
        Amplify.Auth.signIn(username, password,
              result ->{
                  getActivity().runOnUiThread(() ->{
                //hide progress
                bar.setVisibility(View.GONE);
                //result type
                  if(result.isSignInComplete()){
                      Intent i = new Intent(getContext(), NavHomeActivity.class);
                      startActivity(i);
                      getActivity().finish();
                  }
                  });
              },
              error ->{
                  getActivity().runOnUiThread(() ->{
                      //hide progress
                      bar.setVisibility(View.GONE);
                      if(error.getCause().getMessage().contains("User does not exist"))
                          Toast.makeText(getContext(), "User not exist", Toast.LENGTH_SHORT).show();
                      else if(error.getCause().getMessage().contains("Password not correct"))
                          Toast.makeText(getContext(), "invalid username or password", Toast.LENGTH_SHORT).show();
                      else if(error.getCause().getMessage().contains("User is not confirmed")){
                          Bundle b = new Bundle();
                          b.putString("username", username);
                          Navigation.findNavController(getView()).navigate(R.id.confirmAccount, b);
                      }
                      else
                          Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                  });
              });
    }

}
