package com.andnet.gazeta;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.andnet.gazeta.Adapters.CatagoryBaseAdapter;
import com.andnet.gazeta.Models.Category;
import com.andnet.gazeta.ViewPackage.ColumnQty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ChooseCategoryActivity extends AppCompatActivity {

    public static final String CAT="cat";
    public static final String CAT_PREF="cat_pref_file";

    private RecyclerView recyclerView;
    private CatagoryBaseAdapter adapter;
    private ArrayList<Category> catagoryList=new ArrayList<>();
    private Button contButton;


    private SharedPreferences catPrefrence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_catagory);
        catPrefrence=getSharedPreferences(CAT_PREF,MODE_PRIVATE);
        recyclerView=(RecyclerView) findViewById(R.id.rv);
        contButton=(Button)findViewById(R.id.contButton);
        adapter=new CatagoryBaseAdapter(this);
        recyclerView.setAdapter(adapter);

        catagoryList.add(new Category(getString(R.string.top_stories),R.drawable.ic_top_stories,R.drawable.circle_background));
        catagoryList.add(new Category(getString(R.string.technology),R.drawable.ic_technology,R.drawable.tech_back_ground));
        catagoryList.add(new Category(getString(R.string.world),R.drawable.ic_world,R.drawable.circle_background));
        catagoryList.add(new Category(getString(R.string.sport),R.drawable.ic_sport,R.drawable.circle_background));
        catagoryList.add(new Category(getString(R.string.politics),R.drawable.ic_politics,R.drawable.circle_background));
        catagoryList.add(new Category(getString(R.string.business),R.drawable.ic_business,R.drawable.circle_background));
        catagoryList.add(new Category(getString(R.string.entertainment),R.drawable.ic_entertainment,R.drawable.circle_background));
        catagoryList.add(new Category(getString(R.string.health),R.drawable.ic_health,R.drawable.circle_background));
        catagoryList.add(new Category(getString(R.string.social),R.drawable.ic_social,R.drawable.circle_background));
        catagoryList.add(new Category(getString(R.string.art_and_culture),R.drawable.ic_art_and_culture,R.drawable.circle_background));

        adapter.setCategoryList(catagoryList);

        ColumnQty columnQty=new ColumnQty(this,R.layout.catagory_list_item);
        recyclerView.setLayoutManager(new GridLayoutManager(this,columnQty.calculateNoOfColumns()));

        contButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<String> catList=adapter.getSelectedCat();
                if(catList.isEmpty()){
                    Toast.makeText(ChooseCategoryActivity.this,"Select at list one Category",Toast.LENGTH_SHORT).show();
                }else {

                    SharedPreferences firstTimePreference = getSharedPreferences(SplashScreen.FIRST_TIME_KEY,MODE_PRIVATE);
                    SharedPreferences.Editor editor=firstTimePreference.edit();
                    editor.putBoolean(SplashScreen.FIRST_TIME_KEY,false);
                    editor.apply();
                    Set<String> stringSet=new HashSet<>();
                    for(int i=0;i<catList.size();i++){
                        stringSet.add(catList.get(i));
                    }
                    SharedPreferences.Editor catEditor=catPrefrence.edit();
                    catEditor.putStringSet(CAT_PREF,stringSet);
                    catEditor.apply();

                    Intent intent=new Intent(ChooseCategoryActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();

                }

            }
        });

    }


}
