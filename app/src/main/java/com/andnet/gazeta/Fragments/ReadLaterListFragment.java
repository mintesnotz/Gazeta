package com.andnet.gazeta.Fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.andnet.gazeta.Adapters.DetailRecycleViewAdapter;
import com.andnet.gazeta.Adapters.NewsListRecycleViewAdapter;
import com.andnet.gazeta.Databases.DatabaseDescription;
import com.andnet.gazeta.Models.DetailModel;
import com.andnet.gazeta.R;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


public class ReadLaterListFragment extends Fragment {

    public static final String AUDIO_UNIQUE_IDENTIFIER = "+++";
    public static final String VIDEO_UNIQUE_IDENTIFIER = "---";
    public static final String IMAGE_UNIQUE_IDENTIFIER = "***";
    public static final String NULL_SPACE = "";
    public static final String[] AUDIO_FORMATS = {".mp3"};
    public static final String[] VIDEO_FORMATS = {".mp4"};
    public static final String[] IMAGE_FORMATS = {".jpg", ".gif", ".webp", ".jpeg", ".png"};

    private static final String ARG_PARAM = "param";
    private RecyclerView recyclerView;
    private RelativeLayout progressLayout;
    private String key;
    private DetailRecycleViewAdapter adapter;
    private List<DetailModel> detailModelList =new ArrayList<>();

    private String origianlImage;



    public ReadLaterListFragment() {
    }

    public static ReadLaterListFragment newInstance(String key) {
        ReadLaterListFragment fragment = new ReadLaterListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            key = getArguments().getString(ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView= inflater.inflate(R.layout.fragment_read_later_list, container, false);
        recyclerView=mainView.findViewById(R.id.rv);
        progressLayout=mainView.findViewById(R.id.progressLayout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter=new DetailRecycleViewAdapter(getContext());
        recyclerView.setAdapter(adapter);
        new Handler().postDelayed(this::getData, 100);
        return mainView;
    }




    private void getData(){
        if(getContext()==null){
            return;
        }
        Cursor cursor=getContext().getContentResolver().query(DatabaseDescription.SAVED_NEWS.NEWS_CONTENT_URI,
                null,DatabaseDescription.SAVED_NEWS.KEY + " = ?",new String[]{key},null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                String cover_image=cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.COVER_IMAGE));
                if(cover_image!=null)
                 detailModelList.add(new DetailModel(DetailRecycleViewAdapter.VIEW_TYPE_IMAGE,cover_image));
                 detailModelList.add(new DetailModel(DetailRecycleViewAdapter.VIEW_TYPE_SOURCE_IMAGE,cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.SOURCE_LOGO))));
                 detailModelList.add(new DetailModel(DetailRecycleViewAdapter.VIEW_TYPE_SOURCE_NAME,cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.SOURCE_NAME))));
                 detailModelList.add(new DetailModel(DetailRecycleViewAdapter.VIEW_TYPE_TITLE,cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.TITLE))));
                 String authorDate;
                 if(cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.AUTHOR)) ==null){
                    authorDate=cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.SOURCE_NAME)) + "   " + cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.DATE));
                }else {
                    authorDate=cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.AUTHOR)) + "   " + cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.DATE));
                }
                 detailModelList.add(new DetailModel(DetailRecycleViewAdapter.VIEW_TYPE_AUTHOR_DATE,authorDate));
                 origianlImage=cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.ORIGINAL_IMAGE));

            }
        }
        Cursor body_cur=getContext().getContentResolver().query(DatabaseDescription.BODY_TABLE.BODY_TABLE_CONTENT_URI,
                null,DatabaseDescription.BODY_TABLE.key + " = ?",new String[]{key},null);
        if(body_cur!=null){
            if(body_cur.moveToFirst()){
                String body=body_cur.getString(body_cur.getColumnIndex(DatabaseDescription.BODY_TABLE.body));
                try {
                    JSONArray bodyArray=new JSONArray(body);
                    for(int i=0;i<bodyArray.length();i++) {
                        String value = bodyArray.get(i).toString();
                        addElement(value);
                    }}catch (Exception e) {e.printStackTrace();}
                }

        }

    }

    private void addElement(String body){

        if(body.length()<=3){
            return;
        }
        String token=body.substring(0,3);

        if(token.equals(VIDEO_UNIQUE_IDENTIFIER)){
            body=body.replace(token,"");
            String currentFormat = "";
            for (String format : VIDEO_FORMATS) {
                if (body.contains(format)) {
                    currentFormat = format;
                }
            }
            String video_link = body.substring(0, body.indexOf(currentFormat)) + currentFormat;
            if(video_link.contains(NewsListRecycleViewAdapter.YOUTUBE_BASE_LINK)){

                //youtube embed video

            }else {

            }

        }else if(token.equals(IMAGE_UNIQUE_IDENTIFIER)){
            body=body.replace(token,"");

            if(!body.contains("http")){
                body=body.trim();
                detailModelList.add(new DetailModel(DetailRecycleViewAdapter.VIEW_TYPE_HEADER_LAYOUT, body));
            }else {

                //body contain image
                String currentFormat = "";
                for (String format : IMAGE_FORMATS) {
                    if (body.contains(format)) {
                        currentFormat = format;
                    }
                }
                String image_link_with_unique_identifier = body.substring(0, body.indexOf(currentFormat));
                String link = image_link_with_unique_identifier.replace(IMAGE_UNIQUE_IDENTIFIER, NULL_SPACE).trim() + currentFormat;
                String imageDesc = body.replaceAll(link,"");

                if(link.equals(origianlImage)){
                    detailModelList.add(new DetailModel(DetailRecycleViewAdapter.VIEW_TYPE_IMAGE, link));
                    detailModelList.add(new DetailModel(DetailRecycleViewAdapter.VIEW_TYPE_IMAGE_DESC, imageDesc));
                }
            }
        }else if(token.equals(AUDIO_UNIQUE_IDENTIFIER)){

            body=body.replace(token,NULL_SPACE);
            try {
                String currentFormat = NULL_SPACE;
                for (String format : AUDIO_FORMATS) {
                    if (body.contains(format)) {
                        currentFormat = format;
                    }
                }
                if(currentFormat.isEmpty()){
                    return;
                }
                int first=0;
                int last=body.indexOf(currentFormat);
                String auidoLink=body.substring(first,last) + currentFormat;
                DetailModel detailModel=new DetailModel(DetailRecycleViewAdapter.VIEW_TYPE_AUDIO, auidoLink);


                String cover_image_format=NULL_SPACE;
                for (String format : IMAGE_FORMATS) {
                    if (body.contains(format)) {
                        cover_image_format = format;
                    }
                }

                if(!cover_image_format.equals(NULL_SPACE)){

                    int i=body.indexOf(currentFormat);
                    int j=body.indexOf(cover_image_format);
                    String imageLink=body.substring(i,j).replace(currentFormat,NULL_SPACE).trim() + cover_image_format;
                    detailModel.setAudio_cover_image(imageLink);

                }

                detailModelList.add(detailModel);

            }catch (Exception e){
                e.printStackTrace();
            }


        }else {

            detailModelList.add(new DetailModel(DetailRecycleViewAdapter.VIEW_TYPE_TEXT,body));

        }
        refresh();
    }

    private void refresh(){

        adapter.setDetailModelList(detailModelList);
        adapter.notifyDataSetChanged();

            if(progressLayout.getVisibility()==View.VISIBLE){

                recyclerView.setAlpha(0f);
                recyclerView.setVisibility(View.VISIBLE);

                recyclerView.animate()
                        .alpha(1f)
                        .setDuration(500)
                        .setListener(null);

                progressLayout.animate()
                        .alpha(0f)
                        .setDuration(500)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                progressLayout.setVisibility(View.GONE);
                            }
                        });
            }
       }






}
