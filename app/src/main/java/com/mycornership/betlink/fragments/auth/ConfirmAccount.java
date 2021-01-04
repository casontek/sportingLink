package com.mycornership.betlink.fragments.auth;

import android.os.Bundle;
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

import com.amplifyframework.core.Amplify;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mycornership.betlink.R;

public class ConfirmAccount extends Fragment {
    TextInputEditText edt_code;
    ProgressBar bar;
    private String username;
    MaterialButton btn_confirm;

    public ConfirmAccount() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reg_confirm, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //initialize components
        init();
        Bundle b = getArguments();
        username = b.getString("username");
        //listen to confirm button clicks
        btn_confirm.setOnClickListener(v ->{
            if(edt_code.getText().length() > 0)
                confirm(edt_code.getText().toString());
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void init(){
        edt_code = getView().findViewById(R.id.code);
        bar = getView().findViewById(R.id.loading_bar);
        bar.setVisibility(View.GONE);

        btn_confirm = getView().findViewById(R.id.btn_confirm);
    }

    private void confirm(String code){
        Amplify.Auth.confirmSignUp(username, code,
                result ->{
                    //navigate to login form
                    Navigation.findNavController(getView()).navigate(R.id.login);
                },
                error ->{
                    getActivity().runOnUiThread(() ->{
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("confirm_error", error.getCause().getMessage());
                    });
                }
                );
    }

}
