package com.andnet.gazeta.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.andnet.gazeta.Fragments.ReadLaterListFragment;

import java.util.ArrayList;


public class ReadLaterDetailFragmentPagerAdapter extends FragmentStatePagerAdapter{

        private ArrayList<String> myKeyList=new ArrayList<>();

        public ReadLaterDetailFragmentPagerAdapter(FragmentManager fm, ArrayList<String> myKeyList) {
            super(fm);
            this.myKeyList=myKeyList;
        }

        @Override
        public Fragment getItem(int position) {
           return ReadLaterListFragment.newInstance(myKeyList.get(position));
        }

        @Override
        public int getCount() {
            return myKeyList.size();
        }



    }

