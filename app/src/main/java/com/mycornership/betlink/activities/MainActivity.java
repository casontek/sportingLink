package com.mycornership.betlink.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mycornership.betlink.R;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialize and pass intent data
        Intent intent = getIntent();
        //initialize the AWS account
        initAWS();
        try {
            //initialize firebase
            FirebaseApp.initializeApp(this);
            //initialize Ads
            MobileAds.initialize(this,"ca-app-pub-9133642311191277/8195639276");
        }
        catch (Exception e){
            Log.d("error_", e.toString());
        }
        //time the loading of home page
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(intent.getExtras() != null) {
                    if(intent.hasExtra("news_id")) {
                        newsPage(intent);
                    }
                    else{
                        navigateToMain();
                    }
                }
                else{
                    navigateToMain();
                }
            }
        }, 2500);

    }

    private void navigateToMain() {
        Intent i = new Intent(this, NavHomeActivity.class);
        startActivity(i);
        finish();
    }

    private void initAWS(){
        try {
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSS3StoragePlugin());
            Amplify.configure(getApplicationContext());
        } catch (AmplifyException e) {
            e.printStackTrace();
            Log.d("aws_error", e.getMessage());
        }
    }

    private void newsPage(Intent data){
        String body = data.getStringExtra("body");
        String title = data.getStringExtra("title");
        String url  = data.getStringExtra("url");
        String editor = data.getStringExtra("editor");
        String date = data.getStringExtra("date");
        String newsId = data.getStringExtra("id");
        String cat = data.getStringExtra("category");
        //initiate intent for news activity navigation
        Intent intent = new Intent(this, NewsActivity.class);
        intent.putExtra("body", body);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        intent.putExtra("editor", editor);
        intent.putExtra("date", date);
        intent.putExtra("category", cat);
        intent.putExtra("news_id", newsId);
        //navigates to news activity
        startActivity(intent);
    }

}
