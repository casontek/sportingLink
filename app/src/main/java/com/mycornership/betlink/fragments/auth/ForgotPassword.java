package com.mycornership.betlink.fragments.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.amplifyframework.core.Amplify;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mycornership.betlink.R;

public class ForgotPassword extends Fragment {
    TextInputEditText edt_ph;
    MaterialButton btn_continue;
    TextInputLayout layout;
    ProgressBar bar;

    public ForgotPassword() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //initialize components
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void init(){
        edt_ph = getView().findViewById(R.id.phone);
        layout = getView().findViewById(R.id.layout1);

        bar = getView().findViewById(R.id.loading_bar);
        bar.setVisibility(View.GONE);

        btn_continue = getView().findViewById(R.id.btn_recover);
        btn_continue.setOnClickListener(v ->{
            String user = edt_ph.getText().toString();
            if(!TextUtils.isEmpty(user)){
                forgot(user);
            }
        });
    }

    private void forgot(String user){
        //shows progress
        bar.setVisibility(View.VISIBLE);
        Amplify.Auth.resetPassword(user,
                result ->{
                    //hides progress
                    bar.setVisibility(View.GONE);
                    Bundle b = new Bundle();
                    b.putString("username", user);
                    Navigation.findNavController(getView()).navigate(R.id.forgotpasswordConfirm, b);
                },
                error ->{
                    //hides progress
                    bar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
