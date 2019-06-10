package com.andnet.gazeta.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.andnet.gazeta.Fragments.SourceListFragment;

import java.util.ArrayList;
import java.util.List;


public class SourceDetailPagerAdapter extends FragmentStatePagerAdapter {

    private List<String> categoryList =new ArrayList<>();
    private List<String> catagoryNameList=new ArrayList<>();
    private boolean hasTabs;


    public SourceDetailPagerAdapter(FragmentManager fm, List<String> categoryList, List<String> catagoryNameList,boolean hasTabs) {
        super(fm);
        this.categoryList=categoryList;
        this.catagoryNameList=catagoryNameList;
        this.hasTabs=hasTabs;
    }


    @Override
    public Fragment getItem(int position) {
        if(!hasTabs)return SourceListFragment.newInstance("",hasTabs);
        return SourceListFragment.newInstance(categoryList.get(position),hasTabs);
    }


    @Override
    public int getCount() {
        if(!hasTabs)return 1;
        return categoryList.size();
    }


    @Override
    public CharSequence getPageTitle(int position) {
        if(!hasTabs)return "News";
        return catagoryNameList.get(position);
    }


}
