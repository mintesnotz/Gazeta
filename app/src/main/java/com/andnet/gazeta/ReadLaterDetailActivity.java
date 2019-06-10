package com.andnet.gazeta;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.andnet.gazeta.Adapters.ReadLaterDetailFragmentPagerAdapter;
import com.andnet.gazeta.Adapters.ReadLaterRvAdapter;
import com.andnet.gazeta.Databases.DatabaseDescription;
import com.andnet.gazeta.Preference.SettingActivity;

import java.util.ArrayList;
import java.util.Collections;

import static com.andnet.gazeta.MainActivity.THEM_PREF_NAME;


public class ReadLaterDetailActivity extends AppCompatActivity {

    private int posation;
    private ViewPager viewPager;
    private ReadLaterDetailFragmentPagerAdapter viewPagerAdapter;
    private ArrayList<String> keyList=new ArrayList<>();
    private Toolbar toolbar;
    private SharedPreferences bodyTextSizePref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_later_detiail);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.gray_white));
        }

        posation=getIntent().getIntExtra(ReadLaterRvAdapter.EXTRA_ID,0);

        viewPager=(ViewPager)findViewById(R.id.viewpager);
        viewPager.setPageTransformer(true,new HalfSlidePageTransformer(viewPager,findViewById(R.id.view_background)));

        toolbar=(Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.bookmark));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReadLaterDetailActivity.this.onBackPressed();
            }
        });
        toolbar.inflateMenu(R.menu.read_later_detial_menu);
        toolbar.setOnMenuItemClickListener(menuItemCliclListner);
        //get the key list
        Cursor cursor=getContentResolver().query(DatabaseDescription.BODY_TABLE.BODY_TABLE_CONTENT_URI,
                new String[]{DatabaseDescription.BODY_TABLE.key},null,null,null);
        if(cursor!=null){
            while (cursor.moveToNext()){
                keyList.add(cursor.getString(cursor.getColumnIndex(DatabaseDescription.BODY_TABLE.key)));
             }
        }else {

            Toast.makeText(this,"No Saved News",Toast.LENGTH_SHORT).show();
        }

        Collections.reverse(keyList);
        viewPagerAdapter=new ReadLaterDetailFragmentPagerAdapter(getSupportFragmentManager(),keyList);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(onPageChangeListener);
        viewPager.setCurrentItem(posation);
    }

    private ViewPager.OnPageChangeListener onPageChangeListener=new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int pos, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int pos) {
            posation=pos;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    private Toolbar.OnMenuItemClickListener menuItemCliclListner=new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){

                case R.id.go_to_orginal_website:
                      startChromeBrowser();
                    break;
                case R.id.setting:
                    Intent intent=new Intent(ReadLaterDetailActivity.this, SettingActivity.class);
                    startActivity(intent);
                    break;
                case R.id.text_size:
//                    changeTextSize();
                    break;
                case R.id.share:
                    share();
                    break;
            }

            return false;


        }
    };

//    public void changeTextSize(){
//        AlertDialog.Builder builder=new AlertDialog.Builder(this,R.style.MyDialogStyle);
//        builder.setTitle(getString(R.string.change_text_size));
//        View view= LayoutInflater.from(this).inflate(R.layout.text_size_change_seek_bar,null);
//        builder.setView(view);
//        bodyTextSizePref=getSharedPreferences(NewsDetailActivity.BODY_TEXT_SIZE_PREF_NAME,MODE_PRIVATE);
//        final SharedPreferences.Editor editor=bodyTextSizePref.edit();
//        SeekBar seekBar=view.findViewById(R.id.textSizeSeekBar);
//        final TextView percentTextView=view.findViewById(R.id.percent_textView);
//        final int i=(int)bodyTextSizePref.getFloat(NewsDetailActivity.BODY_TITLE_KEY_PREF,30);
//        seekBar.setProgress(i);
//        percentTextView.setText(i + "%");
//
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                if(b){
//                    percentTextView.setText(i + " %");
//                    editor.putFloat(NewsDetailActivity.BODY_TITLE_KEY_PREF,i);
//                    editor.putFloat(NewsDetailActivity.BODY_PARA_KEY_PREF,i/2+i/4);
//                    editor.putFloat(NewsDetailActivity.BODY_SOURCE_AUTHOR_PREF,i/2);
//                    editor.putFloat(NewsDetailActivity.BODY_SOURCE_NAME_PREF,i/2);
//                    editor.apply();
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//
//
//        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int k) {
//                editor.putFloat(NewsDetailActivity.BODY_TITLE_KEY_PREF,i);
//                editor.putFloat(NewsDetailActivity.BODY_PARA_KEY_PREF,i/2+i/4);
//                editor.putFloat(NewsDetailActivity.BODY_SOURCE_AUTHOR_PREF,i/3);
//                editor.putFloat(NewsDetailActivity.BODY_SOURCE_NAME_PREF,i/4);
//                editor.apply();
//                dialogInterface.cancel();
//            }
//        });
//        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.cancel();
//            }
//        });
//
//
//        builder.create().show();
//    }




    private void startChromeBrowser() {

       SharedPreferences themePreference=getSharedPreferences(THEM_PREF_NAME, MODE_PRIVATE);
        int color=themePreference.getInt(MainActivity.THEME_PREF_KEY,getResources().getColor(R.color.colorPrimary));
        Cursor cursor=getContentResolver().query(DatabaseDescription.SAVED_NEWS.NEWS_CONTENT_URI,new String[]{DatabaseDescription.SAVED_NEWS.NEWS_LINK},
                DatabaseDescription.SAVED_NEWS.KEY + "=? " ,new String[]{keyList.get(posation)},null);
        if(cursor==null){
            return;
        }
        cursor.moveToFirst();
        String link = cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.NEWS_LINK));
        try {
            Uri uri = Uri.parse(link);
            CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
            intentBuilder.setToolbarColor(color);
            intentBuilder.setSecondaryToolbarColor(color);
            intentBuilder.setExitAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            CustomTabsIntent customTabsIntent = intentBuilder.build();
            customTabsIntent.intent.setPackage("com.android.chrome");
            intentBuilder.setToolbarColor(color);
            intentBuilder.setShowTitle(true);
            intentBuilder.addDefaultShareMenuItem();
            customTabsIntent.launchUrl(this, uri);
        } catch (ActivityNotFoundException e) {
            Intent intent = new Intent(this, WebVeiwActivity.class);
            intent.putExtra("LINK", link);
            this.startActivity(intent);
        }
    }

//    public void changeTextSize(){
//        AlertDialog.Builder builder=new AlertDialog.Builder(this,R.style.MyDialogStyle);
//        builder.setTitle(getString(R.string.change_text_size));
//        View view= LayoutInflater.from(this).inflate(R.layout.text_size_change_seek_bar,null);
//        builder.setView(view);
//        SharedPreferences textSizePreference=(getSharedPreferences(MainActivity.TEXT_SIZE_FILE_NAME, MODE_PRIVATE);
//        final SharedPreferences.Editor editor=textSizePreference.edit();
//        SeekBar seekBar=view.findViewById(R.id.textSizeSeekBar);
//        final TextView percentTextView=view.findViewById(R.id.percent_textView);
//        final int i=(int)textSizePreference.getFloat(MainActivity.PREF_KEY_TITLE,30);
//        seekBar.setProgress(i);
//        percentTextView.setText(i + "%");
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {char
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                if(b){
//                    percentTextView.setText(i + " %");
//                    editor.putFloat(MainActivity.PREF_KEY_TITLE,i);
//                    editor.putFloat(MainActivity.PREF_KEY_SYNOP,i/2+i/4);
//                    editor.putFloat(MainActivity.PREF_KEY_BODY,i/2);
//                    editor.apply();
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//
//
//        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int k) {
//                editor.putFloat(MainActivity.PREF_KEY_TITLE,i);
//                editor.putFloat(MainActivity.PREF_KEY_SYNOP,i/2 + i/4);
//                editor.putFloat(MainActivity.PREF_KEY_BODY,i/2);
//                editor.apply();
//                dialogInterface.cancel();
//            }
//        });
//        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.cancel();
//            }
//        });
//
//
//        builder.create().show();
//    }





    private void share(){

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.setType("text/plain");
        String key=keyList.get(posation);
        Cursor cursor=getContentResolver().query(DatabaseDescription.SAVED_NEWS.NEWS_CONTENT_URI,new String[]{
                DatabaseDescription.SAVED_NEWS.TITLE,DatabaseDescription.SAVED_NEWS.NEWS_LINK
        },DatabaseDescription.SAVED_NEWS.KEY + " = ?",new String[]{key},null);

        if(cursor!=null){
            if(cursor.moveToFirst()){

                String title=cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.TITLE));
                String link=cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.NEWS_LINK));
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, title+ " -> " +  link);
                startActivity(shareIntent);
            }

        }


    }


    public class HalfSlidePageTransformer implements ViewPager.PageTransformer {
        private final ViewPager viewPager;
        private final View pagerDropShadow;

        public HalfSlidePageTransformer(ViewPager viewPager, View view) {
            this.viewPager = viewPager;
            this.pagerDropShadow = view;
        }

        public boolean isRtl() {
            if (Build.VERSION.SDK_INT < 18 || viewPager.getLayoutDirection() != View.LAYOUT_DIRECTION_RTL) {
                return false;
            }
            return true;
        }
        public void transformPage(View view, float f) {
            if (!(isRtl())) {
                int width = view.getWidth();
                if (f < -1.0f) {
                    return;
                }
                if (f <= 0.0f) {
                    view.setTranslationX(0.0f);
                } else if (f > 1.0f) {
                } else {
                    if (f == 1.0f) {
                        view.setTranslationX(0.0f);
                        this.pagerDropShadow.setTranslationX((float) width);
                        return;
                    }
                    view.setTranslationX(0.5f * (((float) width) * (-f)));
                    if (this.pagerDropShadow.getVisibility() != View.VISIBLE) {
                        this.pagerDropShadow.setVisibility(View.VISIBLE);
                    }
                    float f2 = (((float) width) - ((1.0f - f) * ((float) width))) - 1.0f;
                    if (f2 == 0.0f) {
                        f2 = (float) width;
                    }
                    this.pagerDropShadow.setTranslationX(f2);
                    this.pagerDropShadow.getBackground().setAlpha(Math.round(255.0f * f));
                }
            }
        }
    }



}
