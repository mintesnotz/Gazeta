package com.andnet.gazeta.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.andnet.gazeta.Adapters.LibraryAdapter;
import com.andnet.gazeta.Connection;
import com.andnet.gazeta.MainActivity;
import com.andnet.gazeta.Models.Library;
import com.andnet.gazeta.Preference.SettingActivity;
import com.andnet.gazeta.R;
import com.andnet.gazeta.ViewPackage.ColumnQty;
import com.andnet.gazeta.ViewPackage.GridItemDividerDecoration;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import cn.jzvd.JZVideoPlayer;


public class LibraryFragment extends Fragment{

    private View mainView;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ProgressBar progressLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Library> libraryList=new ArrayList<>();
    private LibraryAdapter libraryAdapter;
    private DrawerLayout drawerLayout;
    private FirebaseDatabase firebaseDatabase;
    private AppBarLayout appBarLayout;

    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  final Bundle savedInstanceState) {
        firebaseDatabase=FirebaseDatabase.getInstance();
        mainView=inflater.inflate(R.layout.my_news_layout,container,false);
        progressLayout=mainView.findViewById(R.id.item_progress_bar);
        recyclerView=mainView.findViewById(R.id.rv);
        toolbar=mainView.findViewById(R.id.toolbar);
        appBarLayout=mainView.findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> ViewCompat.setElevation(appBarLayout, 20));
        swipeRefreshLayout=mainView.findViewById(R.id.swipeToRefresh);
        swipeRefreshLayout.setOnRefreshListener(this::getLibrariesFromFiresBase);
        ColumnQty columnQty=new ColumnQty(getContext(),R.layout.lib_layout);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(getContext(),columnQty.calculateNoOfColumns());
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new GridItemDividerDecoration(10, Color.WHITE));
        toolbar.inflateMenu(R.menu.lib_main_menu);
        toolbar.setOnMenuItemClickListener(onMenuItemClickListner);
        toolbar.setTitle(getString(R.string.my_news));

        MainActivity activity=(MainActivity)getActivity();
        drawerLayout=activity.getDrawerLayout();

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(),drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer){
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

            }
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        //set the listener to the drawer and sync the state
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        libraryAdapter=new LibraryAdapter(getContext());
        recyclerView.setAdapter(libraryAdapter);
        setOrientation();
            if(!Connection.isConnected(getContext())){
                Snackbar snackbar = Snackbar
                        .make(mainView.findViewById(R.id.library_cordinate), getString(R.string.no_connation), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        getLibrariesFromFiresBase();
        return mainView;
    }

    private void getLibrariesFromFiresBase() {
        libraryList.clear();
        DatabaseReference databaseReference= firebaseDatabase.getReference("source");
        databaseReference.keepSynced(false);
        databaseReference.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                databaseReference.removeEventListener(this);
                Iterable<DataSnapshot> snapshotList=dataSnapshot.getChildren();
                for(DataSnapshot data: snapshotList){
                    Library myLib=data.getValue(Library.class);
                    if(myLib!=null){
                        myLib.setSearch_term(data.getKey());
                        libraryList.add(myLib);
                    }
                }
                libraryAdapter.setLibraryList(libraryList);
                libraryAdapter.notifyDataSetChanged();
                if(swipeRefreshLayout.isRefreshing())swipeRefreshLayout.setRefreshing(false);
                crossFade();
            }
            public void onCancelled(DatabaseError databaseError) {
                if(swipeRefreshLayout.isRefreshing())swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void crossFade(){

        recyclerView.setAlpha(0f);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.animate()
                .alpha(1f)
                .setDuration(300)
                .setListener(null);

        progressLayout.animate()
                .alpha(0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        progressLayout.setVisibility(View.GONE);

                    }
                });

    }

    private void setOrientation(){

        int screenSize=getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        int orientation=getResources().getConfiguration().orientation;

        if(orientation==Configuration.ORIENTATION_PORTRAIT){

            if(screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE)
            {
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(),5));


            }else if(screenSize==Configuration.SCREENLAYOUT_SIZE_NORMAL){
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));


            }else if(screenSize==Configuration.SCREENLAYOUT_SIZE_SMALL){
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));


            }else if(screenSize==Configuration.SCREENLAYOUT_SIZE_LARGE){

                recyclerView.setLayoutManager(new GridLayoutManager(getContext(),5));


            }

        }else {

            if(screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE)
            {
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(),6));


            }else if(screenSize==Configuration.SCREENLAYOUT_SIZE_NORMAL){
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(),4));


            }else if(screenSize==Configuration.SCREENLAYOUT_SIZE_SMALL){
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));


            }else if(screenSize==Configuration.SCREENLAYOUT_SIZE_LARGE){

                recyclerView.setLayoutManager(new GridLayoutManager(getContext(),5));


            }
        }


    }

    private Toolbar.OnMenuItemClickListener onMenuItemClickListner= item -> {
        switch (item.getItemId()){
            case R.id.setting:
                Intent intent=new Intent(getActivity(), SettingActivity.class);
                getContext().startActivity(intent);
                return true;

        }
        return false;
    };


    public void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();

    }
}
