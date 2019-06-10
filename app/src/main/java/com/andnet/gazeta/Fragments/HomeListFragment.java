package com.andnet.gazeta.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.andnet.gazeta.Adapters.NewsListRecycleViewAdapter;
import com.andnet.gazeta.Constants;
import com.andnet.gazeta.Databases.DatabaseDescription;
import com.andnet.gazeta.EndlessRecyclerViewScrollListener;
import com.andnet.gazeta.HelperClass.NoAnimationItemAnimator;
import com.andnet.gazeta.Models.News;
import com.andnet.gazeta.Models.Source;
import com.andnet.gazeta.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.jzvd.JZVideoPlayer;



public class HomeListFragment extends Fragment {

    public static final String PREF_CONTENT_LANG="pref_key_content_language";
    public static final String PAGE_ARGS = "PAGE_ARGS";
    private String pageTitle;
    private String content_lang="am";
    private View mainView;
    private RecyclerView recyclerView;
    private NewsListRecycleViewAdapter newsListRecycleViewAdapter;
    private RelativeLayout progressLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Context context;
    private SharedPreferences sharedPreferences;
    private FirebaseDatabase firebaseDatabase;
    public static final int MAX_CATCH_KEY=300;
    private int startPos=0;
    private int pageSize=2;
    private int trackPos=0;
    private  ArrayList<String> keyList=new ArrayList<>();
    private List<Object> newsList = new ArrayList<>();

    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }

    public static HomeListFragment getInstance(String title) {
        Bundle bundle = new Bundle();
        bundle.putString(PAGE_ARGS, title);
        HomeListFragment listFragment = new HomeListFragment();
        listFragment.setArguments(bundle);
        return listFragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null)
           pageTitle=getArguments().getString(PAGE_ARGS);

    }

    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.home_list_fragment, container, false);
        progressLayout=mainView.findViewById(R.id.progressLayout);
        recyclerView = mainView.findViewById(R.id.rv);
        progressLayout=mainView.findViewById(R.id.progressLayout);
        swipeRefreshLayout=mainView.findViewById(R.id.swipeToRefresh);
        swipeRefreshLayout.setOnRefreshListener(this::reference);
        newsListRecycleViewAdapter = new NewsListRecycleViewAdapter(getContext(),pageTitle);
        recyclerView.setAdapter(newsListRecycleViewAdapter);
        recyclerView.setItemAnimator(new NoAnimationItemAnimator());
        init();
        setOrientation();
        reference();
        return mainView;
    }

    private void init(){
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getContext());
        content_lang = sharedPreferences.getString(PREF_CONTENT_LANG, content_lang);
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferencesListener);
        firebaseDatabase=FirebaseDatabase.getInstance();
    }

    private void setOrientation(){
            int orientation=getResources().getConfiguration().orientation;
            int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
            if(orientation==Configuration.ORIENTATION_LANDSCAPE) {
                StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
                recyclerView.setLayoutManager(staggeredGridLayoutManager);
                recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
                    public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                        getNews();

                    }
                });
            } else if(orientation== Configuration.ORIENTATION_PORTRAIT) {
                if(screenSize==Configuration.SCREENLAYOUT_SIZE_XLARGE){
                    StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
                    recyclerView.setLayoutManager(staggeredGridLayoutManager);
                    recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
                        public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                            getNews();

                        }
                    });

                }else {
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
                        public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                            getNews();


                        }});}}}

    private void reference(){

        DatabaseReference databaseReference=firebaseDatabase.getReference(Constants.ETHIOPIA).child(content_lang).child(pageTitle);
        Query news_query=databaseReference.orderByValue().limitToLast(MAX_CATCH_KEY);
        createValueEventListener(news_query);
    }

    private void createValueEventListener(Query query){
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    if(swipeRefreshLayout.isRefreshing())swipeRefreshLayout.setRefreshing(false);
                    return;
                }
                keyList.clear();
                Iterable<DataSnapshot> dataSnapshots=dataSnapshot.getChildren();
                for (DataSnapshot keySnapShot: dataSnapshots){
                    keyList.add(keySnapShot.getKey());
                }
                Collections.reverse(keyList);
                getNews();
                writeKeyToDatabase();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                if(swipeRefreshLayout.isRefreshing())swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void writeKeyToDatabase(){

        try {

            if(keyList.isEmpty())return;
            final ArrayList<String> myKeyList=new ArrayList<>();
            for(int i=0;i<=5;i++){
                myKeyList.add(keyList.get(i ));
            }
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        for(int i=0;i<myKeyList.size();i++){
                            ContentValues contentValues=new ContentValues();
                            contentValues.put(DatabaseDescription.CACHED_KEY_TABLE.KEY,keyList.get(i));
                            contentValues.put(DatabaseDescription.CACHED_KEY_TABLE.CAT,pageTitle);
                            if(getContext()==null)return;
                            Cursor cursor=getContext().getContentResolver().query(DatabaseDescription.CACHED_KEY_TABLE.BODY_TABLE_CONTENT_URI,new String[]{DatabaseDescription.CACHED_KEY_TABLE._ID},
                                    DatabaseDescription.CACHED_KEY_TABLE.KEY + "= ?",new String[]{keyList.get(i)},null,null);
                            if(cursor==null)return;
                            if(!cursor.moveToFirst()){
                                getContext().getContentResolver().insert(DatabaseDescription.CACHED_KEY_TABLE.BODY_TABLE_CONTENT_URI,contentValues);
                            }
                        }
                    }catch (IndexOutOfBoundsException | NullPointerException e){
                        e.printStackTrace();
                    }
                }
            }).start();

        }catch (Exception e ){
            e.printStackTrace();
        }

    }

    private void getNews() {
        if(trackPos>=keyList.size()){
            newsListRecycleViewAdapter.removeProgress();
            return;
        }
        for(int i=startPos;i<pageSize;i++){
            if(keyList.size()>i)
                getNewsByKey(keyList.get(i));
            trackPos++;
        }
        startPos=pageSize;
        pageSize+=2;
    }

    private void getNewsByKey(final String key) {
        DatabaseReference databaseReference=firebaseDatabase.getReference(Constants.ETHIOPIA).child("newsL").child(key);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot firstSnapShot){
                databaseReference.removeEventListener(this);
                final News newsModel =firstSnapShot.getValue(News.class);
                if(newsModel ==null)return;
                if(newsModel.getTitle()==null)return;
                if(newsModel.getSource()==null)return;
                   newsModel.setKey(firstSnapShot.getKey());
                DatabaseReference secReference=firebaseDatabase.getReference(Constants.SOURCE).child(newsModel.getSource());
                secReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot secSnapShot) {
                        Source source=secSnapShot.getValue(Source.class);
                        if(source==null)return;
                        newsModel.setSourceVal(source);
                        newsListRecycleViewAdapter.removeProgress();
                        newsListRecycleViewAdapter.add(newsModel);
                        newsListRecycleViewAdapter.addProgress();
                        crossFade();
                        if(swipeRefreshLayout.isRefreshing())swipeRefreshLayout.setRefreshing(false);
                        secReference.removeEventListener(this);
                        Log.i("KEY", newsModel.getKey() + ": " + newsModel.getTitle() + pageTitle);

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        if(swipeRefreshLayout.isRefreshing())swipeRefreshLayout.setRefreshing(false);
                           Toast.makeText(getContext(),"Error: Try again!",Toast.LENGTH_SHORT).show();
                    }});}
            public void onCancelled(DatabaseError databaseError) {
                if(swipeRefreshLayout.isRefreshing())swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(),"Error: Try again!",Toast.LENGTH_SHORT).show();
            }});
    }

    private void crossFade(){

        if(recyclerView.getVisibility()==View.VISIBLE)return;
        progressLayout.animate()
                .alpha(0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        progressLayout.setVisibility(View.GONE);
                    }
                });
                recyclerView.setAlpha(0f);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.animate()
                        .alpha(1f)
                        .setDuration(300)
                        .setListener(null);

      }

    private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferencesListener = (sharedPreferences, s) -> {
        if("pref_key_enable_image_loading".equals(s) || "pref_key_enable_video_loading".equals(s)) {
            refreshRec();
        }
    };

    public void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }

    public void  refreshRec(){
        newsListRecycleViewAdapter = null;
        newsListRecycleViewAdapter = new NewsListRecycleViewAdapter(context,pageTitle);
        recyclerView = null;
        recyclerView=mainView.findViewById(R.id.rv);
        recyclerView.setAdapter(newsListRecycleViewAdapter);
        newsListRecycleViewAdapter.setObjectList(newsList);
        newsListRecycleViewAdapter.notifyDataSetChanged();
    }


}



