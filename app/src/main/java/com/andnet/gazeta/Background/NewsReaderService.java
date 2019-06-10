package com.andnet.gazeta.Background;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.andnet.gazeta.Databases.DatabaseDescription;
import com.andnet.gazeta.MainActivity;
import com.andnet.gazeta.Models.News;
import com.andnet.gazeta.R;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * service for feaching data in the background
 */

public class NewsReaderService extends Service {

    //constant for notification
    private static final String NOTIFICATION_ENABLED_KEY="pref_key_enable_notification";
    private static final String CUSTOMISE_NOTIFICATION_KEY ="pref_key_cat_notification";
    private static final String RINGTONE_PREF_KEY="pref_key_ringtone";
    private static final String CONTENT_LANG_PREF="pref_key_content_language";

    private int NOT_ID=0;
    private int MAX_NUMBER_NOT=2;


    private static final int MAX_CATCH_KEY=1;

    private Timer timer;
    private SharedPreferences sharedPreferences;
    private News notNews;
    private Intent intent;

    private String lang;



    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        lang=sharedPreferences.getString(CONTENT_LANG_PREF,"am");
         intent=new Intent(this, MainActivity.class);
        startTimer();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        intent=new Intent(this, MainActivity.class);
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        stopTimer();
    }

    private void startTimer(){

        TimerTask timerTask=new TimerTask() {
            @Override
            public void run() {
                if(sharedPreferences.getBoolean(NOTIFICATION_ENABLED_KEY,true)){
                    Set<String> stringSet=sharedPreferences.getStringSet(CUSTOMISE_NOTIFICATION_KEY,null);
                    if(stringSet==null)return;
                    MAX_NUMBER_NOT=stringSet.size();


                }

            }
        };

        timer=new Timer(true);
        int delay=1000*10;  //ten sec
        int interval=1000*3600;// 1 hour interval
        timer.schedule(timerTask,delay,interval);
    }

    private void stopTimer(){
        if(timer!=null){
            timer.cancel();
        }
    }

    private boolean isKexExist(String key){
        Cursor cursor=getContentResolver().query(DatabaseDescription.CACHED_KEY_TABLE.BODY_TABLE_CONTENT_URI,new String[]{DatabaseDescription.CACHED_KEY_TABLE._ID},
                DatabaseDescription.CACHED_KEY_TABLE.KEY + "= ?",new String[]{key},null,null);
        if(cursor==null)return true;
        if(cursor.moveToFirst()){
            return true;
        }else {
            writeKeyToDatabase(key);
            return false;
        }
    }


    private void writeKeyToDatabase(String key){
        ContentValues contentValues=new ContentValues();
        contentValues.put(DatabaseDescription.CACHED_KEY_TABLE.KEY,key);
        contentValues.put(DatabaseDescription.CACHED_KEY_TABLE.CAT,"");
        getContentResolver().insert(DatabaseDescription.CACHED_KEY_TABLE.BODY_TABLE_CONTENT_URI,contentValues);

    }

    private void sendNotification(){

        Uri ringtone_uri = Uri.parse(sharedPreferences.getString(RINGTONE_PREF_KEY, Settings.System.DEFAULT_RINGTONE_URI.toString()));
        PendingIntent pIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                .setTicker(notNews.getTitle())
                .setAutoCancel(true)
                .setSound(ringtone_uri)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder = builder.setContent(getComplexNotificationView());
        } else {

            if(getComplexNotificationView()==null){
                builder = builder.setContentTitle(notNews.getTitle()).setSmallIcon(android.R.drawable.ic_menu_gallery);

            }else {

                builder = builder.setContent(getComplexNotificationView());

            }
        }
        if(NOT_ID<=MAX_NUMBER_NOT){

            NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(NOT_ID, builder.build());
            NOT_ID++;

        }


    }

    private RemoteViews getComplexNotificationView() {
        RemoteViews notificationView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification_layout);
        if(notNews ==null) return null;
            notificationView.setImageViewResource(R.id.logo_image_view, R.mipmap.ic_launcher);
            notificationView.setTextViewText(R.id.title, notNews.getTitle());
            notificationView.setTextViewText(R.id.source_name, notNews.getSourceVal().getName());

        return notificationView;
    }


}
