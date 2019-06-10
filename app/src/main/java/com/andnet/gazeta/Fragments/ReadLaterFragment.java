package com.andnet.gazeta.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.andnet.gazeta.Adapters.ReadLaterRvAdapter;
import com.andnet.gazeta.Databases.DatabaseDescription;
import com.andnet.gazeta.MainActivity;
import com.andnet.gazeta.Preference.SettingActivity;
import com.andnet.gazeta.R;

import cn.jzvd.JZVideoPlayer;

public class ReadLaterFragment extends Fragment  implements  android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {
    private View mainView;
    private SharedPreferences sharedPreferences;
    private Toolbar toolbar;
    private static final int LIBRARY_LOADER = 1;
    private ReadLaterRvAdapter readLaterRvAdapter;
    private RecyclerView recyclerView;
    private RelativeLayout no_read_later_layout;
    private DrawerLayout drawerLayout;
    private AppBarLayout appBarLayout;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LIBRARY_LOADER, null, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView=inflater.inflate(R.layout.read_later_fragment,container,false);
        no_read_later_layout=mainView.findViewById(R.id.no_saved_relative_layout);
        sharedPreferences = getContext().getSharedPreferences(MainActivity.THEM_PREF_NAME, 0);
        toolbar=mainView.findViewById(R.id.toolbar);
        recyclerView=mainView.findViewById(R.id.rv);
        toolbar.inflateMenu(R.menu.read_later_menu);
        toolbar.setOnMenuItemClickListener(onMenuItemClickListner);
        toolbar.setTitle(getString(R.string.bookmark));
        appBarLayout=mainView.findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> ViewCompat.setElevation(appBarLayout, 20));
        int orientation=getResources().getConfiguration().orientation;
        if(orientation== Configuration.ORIENTATION_LANDSCAPE){
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,1));
        }else if(orientation== Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
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

        readLaterRvAdapter=new ReadLaterRvAdapter(getContext(),this);
        recyclerView.setAdapter(readLaterRvAdapter);

        Cursor myCur=getContext().getContentResolver().query(DatabaseDescription.SAVED_NEWS.NEWS_CONTENT_URI,null,null,null,null);
        if(myCur!=null && myCur.moveToFirst()){

        }else{
            recyclerView.setVisibility(View.GONE);
            no_read_later_layout.setVisibility(View.VISIBLE);
        }

        return mainView;
    }



    private Toolbar.OnMenuItemClickListener onMenuItemClickListner= item -> {
        switch (item.getItemId()){
            case R.id.clear_all:
                clearAll();
                break;
            case R.id.text_size:
                TextSizeChooserFragment simpleNewsTextSizeChooserFragment=new TextSizeChooserFragment();
                simpleNewsTextSizeChooserFragment.show(getFragmentManager(),"TEXT_SIZE_CHOOSER");
                return true;
            case R.id.setting:
                Intent intent=new Intent(getActivity(), SettingActivity.class);
                getContext().startActivity(intent);
                return true;

        }
        return false;
    };

    private void clearAll() {

        Cursor myCur=getContext().getContentResolver().query(DatabaseDescription.SAVED_NEWS.NEWS_CONTENT_URI,null,null,null,null);
        if(myCur!=null && myCur.moveToFirst()){

            AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
            builder.setMessage(("Delete all saved news"));
            builder.setTitle("Delete");
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int k) {

                    dialogInterface.cancel();
                }
            });


            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    getContext().getContentResolver().delete(DatabaseDescription.SAVED_NEWS.NEWS_CONTENT_URI,null,null);
                    getContext().getContentResolver().delete(DatabaseDescription.BODY_TABLE.BODY_TABLE_CONTENT_URI,null,null);
                    changeVisibility();
                    Snackbar snackbar = Snackbar.make(mainView.findViewById(R.id.home_layout_coordinate), R.string.succ_delete_all_news_msg, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    dialogInterface.cancel();
                }
            });


            builder.create().show();


        } else {

           changeVisibility();

        }

    }

    public void changeVisibility(){
        no_read_later_layout.setAlpha(0f);
        no_read_later_layout.setVisibility(View.VISIBLE);

        no_read_later_layout.animate()
                .alpha(1f)
                .setDuration(1000)
                .setListener(null);

        recyclerView.animate()
                .alpha(0f)
                .setDuration(1000)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        recyclerView.setVisibility(View.GONE);
                    }
                });

    }



    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {


        Log.i("CUR","ON LOADER CREATE");

        switch (id) {
            case LIBRARY_LOADER:
                return new android.support.v4.content.CursorLoader(getContext(),
                        DatabaseDescription.SAVED_NEWS.NEWS_CONTENT_URI,
                        null,
                        null,
                        null,
                        DatabaseDescription.SAVED_NEWS._ID + " DESC");
            default:
                return null;
        }
    }
    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        Log.i("CUR","ON swap to new data");
        readLaterRvAdapter.swapCursor(data);
    }
    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        Log.i("CUR","ON swap to new null");
        readLaterRvAdapter.swapCursor(null);
    }

    @Override
    public void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();

    }
}
