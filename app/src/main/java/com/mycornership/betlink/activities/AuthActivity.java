package com.mycornership.betlink.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.mycornership.betlink.R;

public class AuthActivity extends AppCompatActivity {
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        //initialize the aws authentication plugin
        intent = getIntent();
        if(intent.hasExtra("register")){
            boolean reg = intent.getBooleanExtra("register", false);
            if(reg)
                Navigation.findNavController(this, R.id.fragment3).navigate(R.id.register);
        }
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init(){
        try {
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.configure(getApplicationContext());
        } catch (AmplifyException e) {
            e.printStackTrace();
            Log.d("aws_error", e.getMessage());
        }
    }
}
