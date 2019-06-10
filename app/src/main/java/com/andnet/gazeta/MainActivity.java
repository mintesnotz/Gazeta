package com.andnet.gazeta;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.andnet.gazeta.Fragments.HomeFragment;
import com.andnet.gazeta.Fragments.LibraryFragment;
import com.andnet.gazeta.Fragments.ReadLaterFragment;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import cn.jzvd.JZVideoPlayer;



public class MainActivity extends AppCompatActivity {

    public static final String THEM_PREF_NAME = "them_name";
    public static final String THEME_PREF_KEY = "pref_theme_color_key";
    public static final String PREF_KEY_ENABLE_IMAGE_LOADING = "pref_key_enable_image_loading";
    public static final String TEXT_SIZE_FILE_NAME = "text_size_file_name";
    public static final String PREF_KEY_TITLE = "pref_key_title";
    public static final String PREF_KEY_SYNOP = "pref_key_synop";
    public static final String PREF_BODY_TITLE = "pref_key_body_title";
    public static final String PREF_BODY_TEXT = "pref_key_body_text";
    public static final String PREF_AUTHOR_DATE = "pref_author_date_text";
    public static final String PREF_MINI_CARD_FILE_NAME = "pref_mini_card_file";
    public static final String PREF_MINI_CARD_KEY = "pref_mini_card_key";
    public static final String HOME_TAG = "home";
    public static final String MY_NEWS_TAG = "my_news_tag";
    public static final String READ_LATER_TAG = "read_later";
    public static final String MY_CHOICE_TAG = "my_choice";

    private int baseContentId = R.id.content;

    private BottomNavigationView bottomNavigation;

    private HomeFragment homeFragment;

    private LibraryFragment libraryFragment;

    private ReadLaterFragment readLaterFragment;

    private SharedPreferences sharedPreferences;

    private String currentLang;

    private String contentLang;

    private DrawerLayout drawerLayout;

    private NavigationView navigationView;

    private FrameLayout frameLayout;
    private List<String> categoryList;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        categoryList = Arrays.asList(getResources().getStringArray(R.array.new_catagory_list_entries));
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (getSupportFragmentManager().findFragmentById(R.id.content) instanceof HomeFragment) {
            //the content frame holds the home fragment
            //get the home fragment from the support fragment manger if it exist else return null
            homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(HOME_TAG);
            if (homeFragment != null) {
                //replace whatever on the @id=R.id.content with the home fragment
                ft.replace(baseContentId, homeFragment);
            } else {
                //create a new home fragment and replace it with thi new fragment
                homeFragment = new HomeFragment();
                ft.replace(baseContentId, homeFragment, HOME_TAG);
            }

        } else if (getSupportFragmentManager().findFragmentById(R.id.content) instanceof LibraryFragment) {
            //the content frame holds the LibraryFragment
            //get the my_news_fragment from the fragment manager if it exist else return null
            libraryFragment = (LibraryFragment) getSupportFragmentManager().findFragmentByTag(MY_NEWS_TAG);
            if (libraryFragment != null) {
                ft.replace(baseContentId, libraryFragment);
            } else {
                libraryFragment = new LibraryFragment();
                ft.replace(baseContentId, libraryFragment, MY_NEWS_TAG);
            }
        } else if (getSupportFragmentManager().findFragmentById(R.id.content) instanceof ReadLaterFragment) {
            //the content frame holds the ReadLaterFragment
            readLaterFragment = (ReadLaterFragment) getSupportFragmentManager().findFragmentByTag(READ_LATER_TAG);
            if (readLaterFragment != null) {
                ft.replace(baseContentId, readLaterFragment);
            } else {
                readLaterFragment = new ReadLaterFragment();
                ft.replace(baseContentId, readLaterFragment, READ_LATER_TAG);
            }

        } else {
            //the content frame does'nt hold any thing
            homeFragment = new HomeFragment();
            ft.add(baseContentId, homeFragment, HOME_TAG);
        }
        ft.commit();

        int[][] states = new int[][]{
                new int[]{android.R.attr.state_checked},
                new int[]{-android.R.attr.state_checked},
        };
        int[] colors = new int[]{

                getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.black)
        };
        ColorStateList colorStateList = new ColorStateList(states, colors);
        bottomNavigation.setItemTextColor(colorStateList);
        bottomNavigation.setItemIconTintList(colorStateList);
    }

    private void init() {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        currentLang = sharedPreferences.getString("pref_key_app_language", "en");
        contentLang = sharedPreferences.getString("pref_key_content_language", "am");
        frameLayout = findViewById(baseContentId);
        navigationView = findViewById(R.id.sideNavationView);
        navigationView.setNavigationItemSelectedListener(onNavigationItemSelectedListener);
        drawerLayout = findViewById(R.id.drawer_layout);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(onBottomNavigationItemSelectedListener);
        removeBottomNavBackground();

    }

    @SuppressLint("RestrictedApi")
    private void removeBottomNavBackground() {
        try {
            BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) bottomNavigation.getChildAt(0);
            Field field = bottomNavigationMenuView.getClass().getDeclaredField("mShiftingMode");
            field.setAccessible(true);
            field.setBoolean(bottomNavigationMenuView, false);
            field.setAccessible(false);
            for (int i = 0; i < bottomNavigationMenuView.getChildCount(); i++) {
                BottomNavigationItemView itemView = (BottomNavigationItemView) bottomNavigationMenuView.getChildAt(i);
                itemView.setShiftingMode(false);
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                    itemView.setItemBackground(R.color.white);

                }
                itemView.setChecked(itemView.getItemData().isChecked());


            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener onBottomNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            switch (item.getItemId()) {

                case R.id.bottom_nav_home:
                    homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(HOME_TAG);
                    if (homeFragment != null) {
                        //replace whatever on the @id=R.id.content with the home fragment
                        ft.replace(baseContentId, homeFragment);
                    } else {
                        //create a new home fragment and replace it with thi new fragment
                        homeFragment = new HomeFragment();
                        ft.replace(baseContentId, homeFragment, HOME_TAG);
                    }
                    break;
                case R.id.bottom_nav_my_news:
                    libraryFragment = (LibraryFragment) getSupportFragmentManager().findFragmentByTag(MY_NEWS_TAG);
                    if (libraryFragment != null) {
                        ft.replace(baseContentId, libraryFragment);
                    } else {
                        libraryFragment = new LibraryFragment();
                        ft.replace(baseContentId, libraryFragment, MY_NEWS_TAG);
                    }
                    break;
                case R.id.bottom_nav_read_later:
                    readLaterFragment = (ReadLaterFragment) getSupportFragmentManager().findFragmentByTag(READ_LATER_TAG);
                    if (readLaterFragment != null) {
                        ft.replace(baseContentId, readLaterFragment);
                    } else {
                        readLaterFragment = new ReadLaterFragment();
                        ft.replace(baseContentId, readLaterFragment, READ_LATER_TAG);

                    }
                    break;

            }
            ft.commit();
            return true;

        }
    };

    public Fragment getCurentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.content);
    }

    protected void onResume() {
        super.onResume();
        if (!sharedPreferences.getString("pref_key_app_language", "en").equals(currentLang)) {
            recreate();
        } else if (!sharedPreferences.getString("pref_key_content_language", "am").equals(contentLang)) {
            recreate();
        }
    }

    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }

    public void setSelectedItem(int id) {
        bottomNavigation.setSelectedItemId(id);
    }


    NavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            if (getSupportFragmentManager().findFragmentById(baseContentId) instanceof HomeFragment) {
                homeFragment.getViewPager().setCurrentItem(getItemPosition(item));
                drawerLayout.closeDrawer(Gravity.START);
            } else {

                HomeFragment homeFragment;
                homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(MainActivity.HOME_TAG);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                if (homeFragment != null) {
                    ft.replace(R.id.content, homeFragment);
                } else {
                    homeFragment = new HomeFragment();
                    ft.replace(R.id.content, homeFragment, MainActivity.HOME_TAG);
                }
                getDrawerLayout().closeDrawer(Gravity.START);
                ft.commit();
                final HomeFragment finalLibraryFragment = homeFragment;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finalLibraryFragment.getViewPager().setCurrentItem(getItemPosition(item), true);
                        setSelectedItem(R.id.bottom_nav_home);
                    }
                }, 300);


            }
            return false;
        }
    };

    private int getItemPosition(MenuItem item) {
        return categoryList.indexOf(item.getTitle());
    }


}