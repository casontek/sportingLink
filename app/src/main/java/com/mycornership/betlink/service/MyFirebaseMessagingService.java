package com.mycornership.betlink.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mycornership.betlink.R;
import com.mycornership.betlink.activities.MainActivity;
import com.mycornership.betlink.activities.NewsActivity;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static String TAG = "MyFirebaseMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if(remoteMessage.getData().size() > 0){
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            Map<String, String> data = remoteMessage.getData();
            String body = data.get("body");
            String title = data.get("title");
            String url  = data.get("url");
            String editor = data.get("editor");
            String date = data.get("date");
            String newsId = data.get("id");
            String cat = data.get("category");
            //send message
            sendDataNotification(body, title, url, editor, date, newsId, cat);
        }

        if(remoteMessage.getNotification() != null){
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            String body = remoteMessage.getNotification().getBody();
            String title = remoteMessage.getNotification().getTitle();
            Uri uri = remoteMessage.getNotification().getImageUrl();
            if(uri != null)
                sendNotification(title, body, uri.toString());
            else
                sendNotification(title, body, null);
        }

    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d(TAG, "Token: " + s);

    }

    private void sendNotification(String ttl, String msg, String url) {
        Intent intent = new Intent(this, MainActivity.class);
        //build the notification to display
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);
        String channelId = getString(R.string.default_notification_chanel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setLargeIcon(BitmapFactory.decodeFile(url))
                        .setSmallIcon(R.drawable.ic_notifications_push)
                        .setContentTitle(ttl)
                        .setContentText(msg)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "sportingLink",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(1, notificationBuilder.build());
    }

    private void sendDataNotification(String body, String title, String url, String editor, String date, String newsId, String cat) {
        //send notification with data
        Intent intent = new Intent(this, NewsActivity.class);
        intent.putExtra("body", body);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        intent.putExtra("editor", editor);
        intent.putExtra("date", date);
        intent.putExtra("category", cat);
        intent.putExtra("news_id", newsId);


        //build the notification to display
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String channelId = getString(R.string.default_notification_chanel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setLargeIcon(BitmapFactory.decodeFile(url))
                        .setSmallIcon(R.drawable.ic_notifications_push)
                        .setContentTitle(title)
                        .setContentText(body.substring(0, 200))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "sportingNews",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }

}
