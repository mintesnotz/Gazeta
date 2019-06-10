package com.andnet.gazeta;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;


public class ChromeBrowser {

    public static boolean openUrl(Context context,String link){

        try {
            Uri uri = Uri.parse(link);
            CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
            intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            intentBuilder.setExitAnimations(context, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            intentBuilder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary));
            intentBuilder.setShowTitle(true);
            intentBuilder.addDefaultShareMenuItem();
            CustomTabsIntent customTabsIntent = intentBuilder.build();
            customTabsIntent.intent.setPackage("com.android.chrome");
            customTabsIntent.launchUrl(context, uri);
        }catch (ActivityNotFoundException | NullPointerException e){
            return false;
        }
        return true;
    }

}
