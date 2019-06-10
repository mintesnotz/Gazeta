package com.andnet.gazeta.Background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NewsBroadCastReciver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
              if(intent.getAction()=="android.net.conn.CONNECTIVITY_CHANGE"){
                  ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                  NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                  boolean isConnected = activeNetwork != null &&
                          activeNetwork.isConnectedOrConnecting();
                  if(isConnected){
                      Intent myIntent=new Intent(context,NewsReaderService.class);
                      context.startService(myIntent);
                  }

              }

    }
}
