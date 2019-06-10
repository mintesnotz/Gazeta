package com.andnet.gazeta.Preference;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.andnet.gazeta.AboutActivity;
import com.andnet.gazeta.LocaleHelper;



public class SettingActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{


      SharedPreferences appPreference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appPreference= PreferenceManager.getDefaultSharedPreferences(this);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingFragment())
                .commit();

    }


    @Override
    protected void onResume() {
        super.onResume();
        appPreference.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        appPreference.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        if("pref_key_app_language".equals(s)){
            LocaleHelper.setLocale(this,sharedPreferences.getString("pref_key_app_language","en"));
            recreate();

        }else if("pref_key_enable_image_loading".equals(s)){
            boolean imageLoading=sharedPreferences.getBoolean("pref_key_enable_image_loading",true);
            if(imageLoading){}

        }else if("pref_key_about".equals(s)){
            Toast.makeText(SettingActivity.this,"hell",Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(SettingActivity.this,AboutActivity.class);
            startActivity(intent);
        }

    }
}
