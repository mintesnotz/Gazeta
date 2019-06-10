package com.andnet.gazeta;


import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;

import com.andnet.gazeta.Databases.DatabaseDescription;

import java.util.ArrayList;
import java.util.List;


public class ChooseSourceActivity extends AppCompatActivity {

    //sharf pref file  name for storing

    private ListView catListView;
    private Toolbar toolbar;
    private List<String> sourceList=new ArrayList<>();
    private SourceChooseBaseAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_choose_source);

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        adapter=new SourceChooseBaseAdapter();
        toolbar.setTitle(getString(R.string.choose_source));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseSourceActivity.this.onBackPressed();
            }
        });

        catListView=(ListView)findViewById(R.id.categoryList);
        catListView.setAdapter(adapter);



    }


    public class SourceChooseBaseAdapter extends BaseAdapter{

        List<String> sourceList=new ArrayList<>();

        public SourceChooseBaseAdapter(){

        }

        public void addSource(String source){
            sourceList.add(source);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return sourceList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {


            View mainView=LayoutInflater.from(parent.getContext()).inflate(R.layout.source_list_item,null);

            CheckBox checkBox=(CheckBox)mainView.findViewById(R.id.checkbox);
            checkBox.setText(sourceList.get(position));
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if(isChecked){

                        setBanned(sourceList.get(position));
                    }else {

                        removeBanned(sourceList.get(position));
                    }

                }
            });

            if(isBanned(sourceList.get(position))){
                checkBox.setChecked(false);
            }else {
                checkBox.setChecked(true);
            }

            return mainView;
        }
    }

    private void removeBanned(String name) {

        Cursor cursor= getContentResolver().query(DatabaseDescription.SOURCE_TABLE.BODY_TABLE_CONTENT_URI,
                new String[]{DatabaseDescription.SOURCE_TABLE._ID},DatabaseDescription.SOURCE_TABLE.NAME
                        + "=?",new String[]{name},null);
        if(cursor!=null && cursor.moveToFirst()){

            getContentResolver().delete(DatabaseDescription.SOURCE_TABLE.buildContactUriForeId(cursor.getInt(0)),null,null);
        }

        ContentValues contentValues=new ContentValues();
        contentValues.put(DatabaseDescription.SOURCE_TABLE.NAME,name);
        contentValues.put(DatabaseDescription.SOURCE_TABLE.BANNED,0);
        getContentResolver().insert(DatabaseDescription.SOURCE_TABLE.BODY_TABLE_CONTENT_URI,contentValues);

    }
    private boolean isBanned(String name){

        boolean banned=false;

       Cursor cursor= getContentResolver().query(DatabaseDescription.SOURCE_TABLE.BODY_TABLE_CONTENT_URI,
                new String[]{DatabaseDescription.SOURCE_TABLE.BANNED},DatabaseDescription.SOURCE_TABLE.NAME
         + "=?",new String[]{name},null);

        if(cursor!=null && cursor.moveToFirst()){

            long val=cursor.getLong(cursor.getColumnIndex(DatabaseDescription.SOURCE_TABLE.BANNED));

            if(val==1){

                banned=true;
            }else if(val==0){
                banned=false;
            }
        }
        return banned;
    }
    private void setBanned(String name){

        Cursor cursor= getContentResolver().query(DatabaseDescription.SOURCE_TABLE.BODY_TABLE_CONTENT_URI,
                new String[]{DatabaseDescription.SOURCE_TABLE._ID},DatabaseDescription.SOURCE_TABLE.NAME
                        + "=?",new String[]{name},null);
        if(cursor!=null && cursor.moveToFirst()){

            getContentResolver().delete(DatabaseDescription.SOURCE_TABLE.buildContactUriForeId(cursor.getInt(0)),null,null);
        }

        ContentValues contentValues=new ContentValues();
        contentValues.put(DatabaseDescription.SOURCE_TABLE.NAME,name);
        contentValues.put(DatabaseDescription.SOURCE_TABLE.BANNED,1);
        getContentResolver().insert(DatabaseDescription.SOURCE_TABLE.BODY_TABLE_CONTENT_URI,contentValues);



    }


}
