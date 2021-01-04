package com.mycornership.betlink.networks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkAvailability {
    public static boolean isConnected(Context c){
        ConnectivityManager manager = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean connected = false;
        if(manager != null){
            NetworkInfo info = manager.getActiveNetworkInfo();
            connected = (info != null) && (info.isConnected());
        }

        return connected;
    }
}
