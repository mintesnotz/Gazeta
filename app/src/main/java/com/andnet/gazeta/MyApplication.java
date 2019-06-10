package com.andnet.gazeta;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.database.FirebaseDatabase;


public class MyApplication extends Application {


    public static final String SAMPLE_APP_ID="ca-app-pub-3940256099942544~3347511713";
    public static final String APP_ID="ca-app-pub-9793090354879392~9745927731";


    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }
}
