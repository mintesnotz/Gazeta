package com.andnet.gazeta;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.andnet.gazeta.Adapters.NewsDetialFragmentPagerAdapter;
import com.andnet.gazeta.Adapters.ReadLaterRvAdapter;
import com.andnet.gazeta.Models.News;
import com.andnet.gazeta.ViewPackage.HalfSlidePageTransformer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import cn.jzvd.JZVideoPlayer;


public class NewsDetailActivity extends AppCompatActivity {


    public static final String BODY_TEXT_SIZE_PREF_NAME="boyd_text_size_pref_name";

    public static final int DEFAULT_BODY_TITLE_TEXT_SIZE=25;
    public static final int DEFAULT_BODY_PARA_TEXT_SIZE=20;
    public static final int DEFAULT_BODY_SOURCE_NAME_TEXT_SIZE=14;
    public static final int DEFAULT_BODY_SOURCE_AUTHOR_TEXT_SIZE=16;

    public static final String BODY_TITLE_KEY_PREF="body_titile_pref";
    public static final String BODY_PARA_KEY_PREF="body_para_pref";
    public static final String BODY_SOURCE_NAME_PREF="body_source_name_pref";
    public static final String BODY_SOURCE_AUTHOR_PREF="body_source_author_pref";

    private SharedPreferences bodyTextSizePref;

    private int posation;
    private ViewPager viewPager;
    private NewsDetialFragmentPagerAdapter viewPagerAdapter;
    private ArrayList<String> keyList=new ArrayList<>();
    private Toolbar toolbar;
    private String pageTitle;
    private AppBarLayout appBarLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail_actvity);

        keyList=getIntent().getStringArrayListExtra("EXTRA_LIST");
        posation=getIntent().getIntExtra(ReadLaterRvAdapter.EXTRA_ID,0);
        pageTitle =getIntent().getStringExtra("PAGE_TITLE");
        viewPager=findViewById(R.id.viewpager);
        viewPager.setPageTransformer(true,new HalfSlidePageTransformer(viewPager,findViewById(R.id.view_background)));
        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle(pageTitle);
        toolbar.setNavigationOnClickListener(view -> NewsDetailActivity.this.onBackPressed());
        toolbar.inflateMenu(R.menu.detail_menu);
        toolbar.setOnMenuItemClickListener(menuItemClickListener);
        viewPagerAdapter=new NewsDetialFragmentPagerAdapter(getSupportFragmentManager(),keyList);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(onPageChangeListener);
        viewPager.setCurrentItem(posation);
        appBarLayout=findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> ViewCompat.setElevation(appBarLayout, 20));

    }

    private ViewPager.OnPageChangeListener onPageChangeListener=new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int pos, float positionOffset, int positionOffsetPixels) {

        }
        @Override
        public void onPageSelected(int pos) {
            posation=pos;
            JZVideoPlayer.releaseAllVideos();

        }
        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    private Toolbar.OnMenuItemClickListener menuItemClickListener = item -> {
        switch (item.getItemId()){
            case R.id.go_to_orginal_website:
                goToOrginalWeb();
                break;
            case R.id.share:
                share();
                break;
        }
        return false;
    };

    private void goToOrginalWeb() {

        FirebaseDatabase.getInstance().getReference(Constants.ETHIOPIA).child(Constants.NEWS_LIST).child(keyList.get(posation)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final News newsModel = dataSnapshot.getValue(News.class);
                ChromeBrowser.openUrl(NewsDetailActivity.this, newsModel.getLink());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void share(){
      FirebaseDatabase.getInstance().getReference(Constants.ETHIOPIA).child(Constants.NEWS_LIST).child(keyList.get(posation)).addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
              final News newsModel = dataSnapshot.getValue(News.class);
              share(newsModel.getTitle(),newsModel.getLink());
          }

          @Override
          public void onCancelled(DatabaseError databaseError) {

          }
      });
    }


    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }

    private void share(String title,String link){

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, title+ " -> " +  link);
        startActivity(shareIntent);
    }

}







