package com.andnet.gazeta.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.andnet.gazeta.Fragments.NewsDetialListFragment;

import java.util.ArrayList;


public class NewsDetialFragmentPagerAdapter extends FragmentStatePagerAdapter{

    private ArrayList<String> myKeyList=new ArrayList<>();

    public NewsDetialFragmentPagerAdapter(FragmentManager fm, ArrayList<String> myKeyList) {
        super(fm);
        this.myKeyList=myKeyList;
    }

    @Override
    public Fragment getItem(int position) {

        return NewsDetialListFragment.newInstance(myKeyList.get(position));

    }

    @Override
    public int getCount() {
        return myKeyList.size();
    }



}

