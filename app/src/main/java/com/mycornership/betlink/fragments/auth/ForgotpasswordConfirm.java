package com.mycornership.betlink.fragments.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.amplifyframework.core.Action;
import com.amplifyframework.core.Amplify;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mycornership.betlink.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgotpasswordConfirm extends Fragment {
    TextInputEditText edt_code, edt_pass;
    TextInputLayout layout1, layout2;
    MaterialButton btn_verify;
    ProgressBar bar;
    TextView pass_info;

    public ForgotpasswordConfirm() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forgot_confirm, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //initialize the widgets
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void init(){
        edt_code = getView().findViewById(R.id.code);
        edt_pass = getView().findViewById(R.id.password);

        layout1 = getView().findViewById(R.id.layout1);
        layout2 = getView().findViewById(R.id.layout2);

        btn_verify = getView().findViewById(R.id.btn_recover);
        bar = getView().findViewById(R.id.loading_bar);
        bar.setVisibility(View.GONE);

        btn_verify.setOnClickListener( v -> {
            String c = edt_code.getText().toString();
            String  p = edt_pass.getText().toString();
            if(valid(c, p)){
                confirm(c, p);
            }
        });
    }

    private boolean valid(String c, String p) {
        if(TextUtils.isEmpty(c)){
            layout1.setError("enter your code");
            return false;
        }

        if(TextUtils.isEmpty(p)){
            layout2.setError("enter new password");
            return  false;
        }

        String patterns = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}";
        Pattern pattern = Pattern.compile(patterns);
        Matcher matcher = pattern.matcher(p);
        if(!matcher.matches()){
            edt_pass.setError("invalid password");
            pass_info.setText("password must contain at least 1 Capital later, 1 small later, 1 number and 8 characters in number.");
            return false;
        }

        return true;
    }

    private void confirm(String c, String p) {
        Amplify.Auth.confirmResetPassword(p, c, new Action() {
                    @Override
                    public void call() {
                        //hides the progress
                        bar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Password successfully changed", Toast.LENGTH_SHORT).show();
                        //navigates to login page
                        Navigation.findNavController(getView()).navigate(R.id.login);
                    }
                },
                error -> {
                    //hides the progress
                    bar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
