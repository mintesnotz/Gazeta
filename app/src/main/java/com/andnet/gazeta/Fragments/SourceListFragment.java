package com.andnet.gazeta.Fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.andnet.gazeta.Adapters.NewsListRecycleViewAdapter;
import com.andnet.gazeta.EndlessRecyclerViewScrollListener;
import com.andnet.gazeta.HelperClass.NoAnimationItemAnimator;
import com.andnet.gazeta.LibDetailActivity;
import com.andnet.gazeta.MainActivity;
import com.andnet.gazeta.Models.News;
import com.andnet.gazeta.Models.Source;
import com.andnet.gazeta.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.jzvd.JZVideoPlayer;


public class SourceListFragment extends Fragment {

    private static final String ARG_PARAM2 = "param2";
    private static final String HAS_TABS="hasTabs";
    private String sourceCatId;

    private View mainView;
    private SwipeRefreshLayout swipeToRefresh;
    private RelativeLayout progressLayout;
    private RecyclerView recyclerView;


    private FirebaseDatabase firebaseDatabase;
    public static final int MAX_CATCH_KEY=200;
    private int startPos=0;
    private int pageSize=2;
    private int trackPos=0;
    private ArrayList<String> keyList=new ArrayList<>();
    private List<Object> newsList = new ArrayList<>();
    private NewsListRecycleViewAdapter newsListRecycleViewAdapter;
    private Context context;
    private boolean hasTabs;

    public static SourceListFragment newInstance(String param,boolean hasTabs) {
        SourceListFragment fragment = new SourceListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM2, param);
        args.putBoolean(HAS_TABS,hasTabs);
        fragment.setArguments(args);
        return fragment;
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sourceCatId = getArguments().getString(ARG_PARAM2);
            hasTabs=getArguments().getBoolean(HAS_TABS);
        }
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        firebaseDatabase=FirebaseDatabase.getInstance();
        mainView= inflater.inflate(R.layout.fragment_source_list, container, false);
        swipeToRefresh=mainView.findViewById(R.id.swipeToRefresh);
        progressLayout=mainView.findViewById(R.id.progressLayout);
        recyclerView=mainView.findViewById(R.id.rv);
        recyclerView.setItemAnimator(new NoAnimationItemAnimator());
        LibDetailActivity libDetailActivity=(LibDetailActivity)getActivity();
        newsListRecycleViewAdapter = new NewsListRecycleViewAdapter(getContext(),libDetailActivity.getLibrary().getName());
        recyclerView.setAdapter(newsListRecycleViewAdapter);
        setOrientation();
        context=getContext();
        swipeToRefresh.setOnRefreshListener(() -> {
        });
        if(hasTabs){
            refrence();
        }else {
            referenceWithNoTabs();
        }
        return mainView;
    }

    private void referenceWithNoTabs() {
        LibDetailActivity libDetailActivity=(LibDetailActivity)getActivity();
        String sourceName=libDetailActivity.getLibrary().getSearch_term();
        DatabaseReference databaseReference = firebaseDatabase.getReference("ethiopia").child("source").child(sourceName).getRef();
        Query  news_query = databaseReference.orderByValue().limitToLast(MAX_CATCH_KEY);
        createValueEventListener(news_query);
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
    private void refrence(){
        LibDetailActivity libDetailActivity=(LibDetailActivity)getActivity();
        String sourceName=libDetailActivity.getLibrary().getSearch_term();
        DatabaseReference databaseReference = firebaseDatabase.getReference("ethiopia").child("subcat").child(sourceName).child(sourceCatId).getRef();
        Query  news_query = databaseReference.orderByValue().limitToLast(MAX_CATCH_KEY);
        createValueEventListener(news_query);
    }
    private void createValueEventListener(Query query){
        query.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    return;
                }

                Iterable<DataSnapshot> dataSnapshots=dataSnapshot.getChildren();
                for (DataSnapshot keySnapShot: dataSnapshots){
                    keyList.add(keySnapShot.getKey());
                }
                Collections.reverse(keyList);
                getNews();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }
    private void getNews() {
        if(trackPos==keyList.size()){
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
        DatabaseReference databaseReference=firebaseDatabase.getReference("ethiopia").child("newsL").child(key);
        databaseReference.keepSynced(false);
        databaseReference.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot firstSnapShot){
                final News newsModel =firstSnapShot.getValue(News.class);
                if(newsModel ==null) return;
                if(newsModel.getTitle()==null)return;
                newsModel.setKey(key);
                firebaseDatabase.getReference("source").child(newsModel.getSource()).addValueEventListener(new ValueEventListener() {
                    public void onDataChange(DataSnapshot secSnapShot) {
                        Source source=secSnapShot.getValue(Source.class);
                        if(source==null)return;
                        newsModel.setSourceVal(source);
                        newsListRecycleViewAdapter.removeProgress();
                        newsListRecycleViewAdapter.add(newsModel);
                        newsList.add(newsModel);
                        newsListRecycleViewAdapter.addProgress();
                        if(progressLayout.getVisibility()==View.VISIBLE)crossFade();
                        if(swipeToRefresh.isRefreshing())swipeToRefresh.setRefreshing(false);

                    }
                    public void onCancelled(DatabaseError databaseError) {
                        if(swipeToRefresh.isRefreshing())swipeToRefresh.setRefreshing(false);

                    }});}
            public void onCancelled(DatabaseError databaseError) {
                if(swipeToRefresh.isRefreshing())swipeToRefresh.setRefreshing(false);

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
    public void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }
    public void  refreshRec(){
        newsListRecycleViewAdapter = null;
        newsListRecycleViewAdapter = new NewsListRecycleViewAdapter(context,"");
        recyclerView = null;
        recyclerView=mainView.findViewById(R.id.rv);
        recyclerView.setAdapter(newsListRecycleViewAdapter);
        newsListRecycleViewAdapter.setObjectList(newsList);
        newsListRecycleViewAdapter.notifyDataSetChanged();
    }
    private SharedPreferences.OnSharedPreferenceChangeListener onMiniCardChanged= (sharedPreferences, s) -> {
        if(sharedPreferences.getBoolean(MainActivity.PREF_MINI_CARD_KEY,false)){
            refreshRec();
        }
    };

}
