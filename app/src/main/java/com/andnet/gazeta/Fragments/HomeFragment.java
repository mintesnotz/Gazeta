package com.andnet.gazeta.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andnet.gazeta.Adapters.HomeFragmentPagerAdapter;
import com.andnet.gazeta.Connection;
import com.andnet.gazeta.MainActivity;
import com.andnet.gazeta.Preference.SettingActivity;
import com.andnet.gazeta.R;

import org.jetbrains.annotations.NotNull;

import cn.jzvd.JZVideoPlayer;


public class HomeFragment extends Fragment{

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private HomeFragmentPagerAdapter homeFragmentPagerAdapter;
    private View mainView;
    private SearchView searchView;
    private CoordinatorLayout homeCordinateLayout;
    private AppBarLayout appBarLayout;



    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         mainView = inflater.inflate(R.layout.home_fragment, container, false);
         homeCordinateLayout=mainView.findViewById(R.id.home_layout_coordinate);
         appBarLayout=mainView.findViewById(R.id.appBar);
         appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> ViewCompat.setElevation(appBarLayout, 20));
         viewPager =  mainView.findViewById(R.id.viewpager);
         tabLayout= mainView.findViewById(R.id.tabLayout);
         toolbar= mainView.findViewById(R.id.toolbar);
         toolbar.inflateMenu(R.menu.main_menu);
         toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
         homeFragmentPagerAdapter=new HomeFragmentPagerAdapter(getChildFragmentManager(),getActivity());

         setUpDrawer();
         setUpViewPager();
         checkConnection();
         return mainView;
    }

    private void checkConnection(){
        if(!Connection.isConnected(getContext())){
            Snackbar snackbar = Snackbar
                    .make(mainView.findViewById(R.id.home_layout_coordinate), getString(R.string.no_connation), Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    private void setUpViewPager(){
        viewPager.setAdapter(homeFragmentPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                JZVideoPlayer.releaseAllVideos();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setUpDrawer(){
        MainActivity activity=(MainActivity)getActivity();
        DrawerLayout drawerLayout = activity.getDrawerLayout();
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer){

            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

            }
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

    }

    private Toolbar.OnMenuItemClickListener onMenuItemClickListener = item -> {
        switch (item.getItemId()){
            case R.id.text_size:
                TextSizeChooserFragment simpleNewsTextSizeChooserFragment=new TextSizeChooserFragment();
                simpleNewsTextSizeChooserFragment.show(getChildFragmentManager(),"TEXT_SIZE_CHOOSER");
                return true;
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

    public ViewPager getViewPager(){
        return viewPager;
    }


}
