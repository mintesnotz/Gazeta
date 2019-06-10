package com.andnet.gazeta;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Connection {


    public static boolean isConnected(Context context){
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null) {
            return false;
        }

        if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
            if (activeNetwork.isConnected())
                haveConnectedWifi = true;
        } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
            if (activeNetwork.isConnected())
                haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;

    }



}
