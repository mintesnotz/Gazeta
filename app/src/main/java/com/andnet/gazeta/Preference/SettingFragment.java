package com.andnet.gazeta.Preference;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.andnet.gazeta.R;


/**
 * the setting fragment
 *
 */

public class SettingFragment extends PreferenceFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //add fragment from specified resource
        addPreferencesFromResource(R.xml.preferences);

        Preference myPref=(Preference) findPreference("pref_key_send_feedback");

        myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent emailIntent=new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","berhanzak3799@gmail.com",null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT,"FeedBack From Gazeta");
                startActivity(Intent.createChooser(emailIntent,null));
                return true;
            }
        });


    }
}
