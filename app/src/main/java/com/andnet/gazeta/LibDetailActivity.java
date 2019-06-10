package com.andnet.gazeta;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.andnet.gazeta.Adapters.SourceDetailPagerAdapter;
import com.andnet.gazeta.Fragments.TextSizeChooserFragment;
import com.andnet.gazeta.Models.Library;
import com.andnet.gazeta.Preference.SettingActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import cn.jzvd.JZVideoPlayer;

import static com.andnet.gazeta.MainActivity.THEM_PREF_NAME;


public class LibDetailActivity extends AppCompatActivity{


    public static final String LIBRARY_CONST="library";
    private Toolbar toolbar;
    private ViewPager viewpager;
    private Library library;
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences themePreference;
    private ArrayList<String> keyList=new ArrayList<>();
    private TabLayout tabLayout;
    private SourceDetailPagerAdapter sourceDetailPagerAdapter;
    private FirebaseDatabase firebaseDatabase;
    private AppBarLayout appBarLayout;

    private List<String> sourceKeyList=new ArrayList<>();
    private List<String> sourceCatNameList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        setContentView(R.layout.activity_lib_detial);

        firebaseDatabase=FirebaseDatabase.getInstance();
        toolbar =findViewById(R.id.toolbar);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        themePreference = getSharedPreferences(THEM_PREF_NAME, MODE_PRIVATE);
        viewpager =  findViewById(R.id.viewpager);
        library = (Library) getIntent().getSerializableExtra(LibDetailActivity.LIBRARY_CONST);
        this.context = this;
        toolbar.setOnMenuItemClickListener(menuItemClickListener);
        toolbar.setNavigationOnClickListener(view -> LibDetailActivity.this.onBackPressed());
        toolbar.setTitle(library.getName());
        toolbar.inflateMenu(R.menu.source_menu);
        tabLayout=findViewById(R.id.tabLayout);
        appBarLayout=findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> ViewCompat.setElevation(appBarLayout, 20));
        getSourceCatFromFirebase();
 }

    public void getSourceCatFromFirebase(){
        DatabaseReference databaseReference=firebaseDatabase.getReference(Constants.ETHIOPIA).child("sourses").child(library.getSearch_term());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> dataSnapshots=dataSnapshot.getChildren();
                for(DataSnapshot shots: dataSnapshots){
                        sourceKeyList.add(shots.getKey());
                        sourceCatNameList.add(shots.getValue() + "");
                }
                if(sourceCatNameList.isEmpty()){
                    addSource();
                }else {
                    addTabs();

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void addSource() {

//        tabLayout.setVisibility(View.GONE);
        toolbar.setTitle(library.getName());
        sourceDetailPagerAdapter =new SourceDetailPagerAdapter(getSupportFragmentManager(),sourceKeyList,sourceCatNameList,false);
        viewpager.setAdapter(sourceDetailPagerAdapter);
        tabLayout.setupWithViewPager(viewpager);

    }

    public void addTabs(){
     sourceDetailPagerAdapter =new SourceDetailPagerAdapter(getSupportFragmentManager(),sourceKeyList,sourceCatNameList,true);
     viewpager.setAdapter(sourceDetailPagerAdapter);
     viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
         @Override
         public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
         @Override
         public void onPageSelected(int position){JZVideoPlayer.releaseAllVideos();}
         @Override
         public void onPageScrollStateChanged(int state) {}
     });
     tabLayout.setupWithViewPager(viewpager);



 }

//    SharedPreferences.OnSharedPreferenceChangeListener sharedPreferencesListner=new SharedPreferences.OnSharedPreferenceChangeListener() {
//        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
//            if("pref_key_enable_image_loading".equals(s)){
//                newsListRecycleViewAdapter = null;
//                newsListRecycleViewAdapter=new NewsListRecycleViewAdapter(context,"");
//                recyclerView.setAdapter(newsListRecycleViewAdapter);
//                newsListRecycleViewAdapter.setObjectList(newsList);
//                newsListRecycleViewAdapter.notifyDataSetChanged();
//            }
//
//
//        }
//    };

    Toolbar.OnMenuItemClickListener menuItemClickListener=new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){
                case R.id.go_to_orginal_website:
                    ChromeBrowser.openUrl(LibDetailActivity.this,library.getLink());
                    break;
                case R.id.text_size:
                    TextSizeChooserFragment simpleNewsTextSizeChooserFragment=new TextSizeChooserFragment();
                    simpleNewsTextSizeChooserFragment.show(getSupportFragmentManager(),"TEXT_SIZE_CHOOSER");
                    break;
                case R.id.setting:
                    Intent intent=new Intent(LibDetailActivity.this, SettingActivity.class);
                     startActivity(intent);
                    break;

            }
            return true;
        }
    };


    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }

    public Library getLibrary(){
        return library;
    }

}
