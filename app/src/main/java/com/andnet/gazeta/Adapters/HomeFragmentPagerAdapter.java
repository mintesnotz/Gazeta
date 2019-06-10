package com.andnet.gazeta.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.andnet.gazeta.Fragments.HomeListFragment;
import com.andnet.gazeta.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *fragment pager adapter of the home fragment
 */

public class HomeFragmentPagerAdapter extends FragmentStatePagerAdapter {

    //array list for holding list of category
    private List<String> categoryList =new ArrayList<>();
    private List<String> cat_main=new ArrayList<>();


    public HomeFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        //getting the category list from the resource array file
        categoryList= Arrays.asList(context.getResources().getStringArray(R.array.new_catagory_list_entries_values));
        cat_main= Arrays.asList(context.getResources().getStringArray(R.array.categories));
//

////        SharedPreferences sharedPreferences=context.getSharedPreferences(ChooseCategoryActivity.CAT_PREF,Context.MODE_PRIVATE);
////        Set<String> catSet=sharedPreferences.getStringSet(ChooseCategoryActivity.CAT_PREF,null);
//
//        assert catSet != null;
//        for(String cat:catSet){
//            categoryList.add(cat);
//            cat_main.add(cat);
//        }



    }

    @Override
    public Fragment getItem(int position) {
        return HomeListFragment.getInstance(categoryList.get(position));
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return cat_main.get(position);
    }


}
