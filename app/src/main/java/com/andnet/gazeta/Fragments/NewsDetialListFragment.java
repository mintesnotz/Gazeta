package com.andnet.gazeta.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andnet.gazeta.Adapters.DetailRecycleViewAdapter;
import com.andnet.gazeta.Adapters.NewsListRecycleViewAdapter;
import com.andnet.gazeta.Constants;
import com.andnet.gazeta.Models.DetailModel;
import com.andnet.gazeta.Models.News;
import com.andnet.gazeta.Models.Source;
import com.andnet.gazeta.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;



public class NewsDetialListFragment extends Fragment {
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
    private RelativeLayout no_offline_layout;
    private RelativeLayout webview_layout;
    private String key;
    private FirebaseDatabase firebaseDatabase;
    private DetailRecycleViewAdapter adapter;
    private List<DetailModel> detailModelList = new ArrayList<>();
    private News newsModel;

    public static NewsDetialListFragment newInstance(String key) {
        NewsDetialListFragment fragment = new NewsDetialListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, key);
        fragment.setArguments(args);
        return fragment;
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            key = getArguments().getString(ARG_PARAM);
         }
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.news_detial_list_fragment, container, false);
        firebaseDatabase = FirebaseDatabase.getInstance();
        recyclerView = mainView.findViewById(R.id.rv);
        progressLayout = mainView.findViewById(R.id.progressLayout);
        no_offline_layout = mainView.findViewById(R.id.no_offline_news_layout);
        webview_layout=mainView.findViewById(R.id.webview_layout);
        TextView retry=no_offline_layout.findViewById(R.id.try_agian_text_view);
        retry.setOnClickListener(view -> {
            getSimpleNewsDataModel();
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DetailRecycleViewAdapter(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                View view = recyclerView.getChildAt(0);
                if (view != null && recyclerView.getChildAdapterPosition(view) == 0) {
                    view.setTranslationY(-view.getTop() / 2);
                }
            }
        });
        getSimpleNewsDataModel();
        return mainView;
    }
    private void getSimpleNewsDataModel() {

        firebaseDatabase.getReference(Constants.ETHIOPIA).child(Constants.NEWS_LIST).child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firebaseDatabase.getReference().removeEventListener(this);
                if (!dataSnapshot.exists()) {
                    showNoNewsFound();
                    return;
                }
                final News newsModel = dataSnapshot.getValue(News.class);
                if (newsModel == null) {
                    showNoNewsFound();
                    return;
                }
                if (newsModel.getTitle() == null) {
                    showNoNewsFound();
                    return;
                }
                if(newsModel.getSource()==null){
                    showNoNewsFound();
                    return;
                }
                newsModel.setKey(key);

                firebaseDatabase.getReference(Constants.SOURCE).child(newsModel.getSource()).addValueEventListener(new ValueEventListener() {
                    public void onDataChange(DataSnapshot sourceSnopShot) {
                        if (!sourceSnopShot.exists()) {
                            showNoNewsFound();
                            return;
                        }
                        Source source = sourceSnopShot.getValue(Source.class);
                        if (source == null) {
                            showNoNewsFound();
                            return;
                        }
                        newsModel.setSourceVal(source);
                        NewsDetialListFragment.this.newsModel = newsModel;
                        getData();
                    }
                    public void onCancelled(DatabaseError databaseError) {
                        showNoNewsFound();
                        showError(databaseError);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showNoNewsFound();
                showError(databaseError);
            }
        });
    }
    private void getData() {
        if (getContext() == null)return;
        if(newsModel.getSourceVal()==null)return;
        if(!newsModel.getSourceVal().isAllowed()){
            detailModelList.clear();
            detailModelList.add(new DetailModel(DetailRecycleViewAdapter.VIEW_TYPE_WEB_VIEW, newsModel.getLink()));
            refresh();
            return;
        }

        if (newsModel.getCover_image() != null)
            detailModelList.add(new DetailModel(DetailRecycleViewAdapter.VIEW_TYPE_FIRST_IMAGE_LAYOUT, newsModel.getCover_image()));
            if(newsModel.getCover_caption()!=null){
                detailModelList.add(new DetailModel(DetailRecycleViewAdapter.VIEW_TYPE_IMAGE_DESC, newsModel.getCover_caption()));
            }
            detailModelList.add(new DetailModel(DetailRecycleViewAdapter.VIEW_TYPE_SOURCE_IMAGE, newsModel.getSourceVal().getLogo()));
            detailModelList.add(new DetailModel(DetailRecycleViewAdapter.VIEW_TYPE_SOURCE_NAME, newsModel.getSourceVal().getName()));
        if(newsModel.getTitle()==null){
            detailModelList.clear();
            detailModelList.add(new DetailModel(DetailRecycleViewAdapter.VIEW_TYPE_WEB_VIEW, newsModel.getLink()));
            refresh();
            return;
        }
        String title = newsModel.getTitle().replaceAll("“", "\"").replaceAll("”", "\"");
        detailModelList.add(new DetailModel(DetailRecycleViewAdapter.VIEW_TYPE_TITLE, title));
        String authorDate;
        if (newsModel.getAuthor() != null && newsModel.getDate() != null) {
            authorDate = newsModel.getAuthor() + " " + newsModel.getDate();
            detailModelList.add(new DetailModel(DetailRecycleViewAdapter.VIEW_TYPE_AUTHOR_DATE, authorDate));
        }
        getBody();

    }
    private void getBody() {
        firebaseDatabase.getReference(Constants.ETHIOPIA).child(Constants.NEWS_B).child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firebaseDatabase.getReference().removeEventListener(this);
                if (!dataSnapshot.exists()) {
                    detailModelList.clear();
                    detailModelList.add(new DetailModel(DetailRecycleViewAdapter.VIEW_TYPE_WEB_VIEW, newsModel.getLink()));
                    refresh();
                    return;
                }
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                if(dataSnapshot.getChildrenCount()==0){
                           addElement(dataSnapshot.getValue() + "");
                    }else {
                        for (DataSnapshot dataSnapshot1 : children) {
                            addElement(dataSnapshot1.getValue() + "");

                        }
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                detailModelList.add(new DetailModel(DetailRecycleViewAdapter.VIEW_TYPE_IMAGE_DESC, newsModel.getLink()));
                showError(databaseError);
            }
        });

    }
    private void showError(DatabaseError databaseError) {
        switch (databaseError.getCode()) {
            case DatabaseError.OPERATION_FAILED:
                Toast.makeText(getContext(), "Operation Failed", Toast.LENGTH_SHORT).show();
                break;
            case DatabaseError.DISCONNECTED:
                Toast.makeText(getContext(), "Disconnected: try again", Toast.LENGTH_SHORT).show();
                break;
            case DatabaseError.NETWORK_ERROR:
                Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show();
                break;
            case DatabaseError.UNKNOWN_ERROR:
                Toast.makeText(getContext(), "An Error occurred", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(getContext(), "An Error occurred", Toast.LENGTH_SHORT).show();

        }


    }


    private void addElement(String body){
        if(body.length()<=4)return;
          String token=body.substring(0,3);

        if(token.equals(AUDIO_UNIQUE_IDENTIFIER) && !body.contains(".mp3")){
            token=VIDEO_UNIQUE_IDENTIFIER;
            body=body.replace(AUDIO_UNIQUE_IDENTIFIER,VIDEO_UNIQUE_IDENTIFIER);
        }

        if(token.equals(VIDEO_UNIQUE_IDENTIFIER)){

            Log.i("VIDEO",body);
            body=body.replace(token,NULL_SPACE);
            if(!body.contains(",")){
                if(body.contains(NewsListRecycleViewAdapter.YOUTUBE_BASE_LINK)){
                    detailModelList.add(new DetailModel(DetailRecycleViewAdapter.VIEW_TYPE_YOUTUBE_VIDEO, body));
                    return;
                }else if(body.contains("http://www.youtube.com/embed/")){
                    detailModelList.add(new DetailModel(DetailRecycleViewAdapter.VIEW_TYPE_YOUTUBE_VIDEO, body));
                    return;
                }
                detailModelList.add(new DetailModel(DetailRecycleViewAdapter.VIEW_TYPE_VIDEO, body));
                return;
            }

            String video_link = body.substring(0, body.indexOf(","));
            DetailModel detailModel=new DetailModel();
            if(video_link.contains(NewsListRecycleViewAdapter.YOUTUBE_BASE_LINK)){
                detailModel.setValue(video_link);
                detailModel.setType(DetailRecycleViewAdapter.VIEW_TYPE_YOUTUBE_VIDEO);
            }else {
                detailModel.setType(DetailRecycleViewAdapter.VIEW_TYPE_VIDEO);
                detailModel.setValue(video_link);
            }

            String sub_video_link=body.replace(video_link,"");
            String curFormat=NULL_SPACE;
            for(String format:IMAGE_FORMATS){

                if(sub_video_link.contains(format)){
                    curFormat=format;
                }
            }
            if(curFormat.isEmpty()){
                detailModelList.add(detailModel);
                if(sub_video_link.length()>3)
                detailModelList.add(new DetailModel(DetailRecycleViewAdapter.VIEW_TYPE_IMAGE_DESC, sub_video_link));
            }else {
                detailModel.setAudio_cover_image(sub_video_link);
                detailModelList.add(detailModel);
            }

                    }else if(token.equals(IMAGE_UNIQUE_IDENTIFIER)){
            body=body.replace(token,NULL_SPACE);
            if(!body.contains("http")){
                detailModelList.add(new DetailModel(DetailRecycleViewAdapter.VIEW_TYPE_HEADER_LAYOUT, body));
            }else {
                try {
                    if(body.contains(",")){
                        int position=body.indexOf(",");
                        String imageLink = body.substring(0,position);
                        if(!imageLink.equals(newsModel.getOriginal_image())){
                            detailModelList.add(new DetailModel(DetailRecycleViewAdapter.VIEW_TYPE_IMAGE, imageLink));
                            if( body.replace(imageLink,"").length()>3)
                                detailModelList.add(new DetailModel(DetailRecycleViewAdapter.VIEW_TYPE_IMAGE_DESC, body.replace(imageLink,"")));
                        }
                    }else {
                        if(!body.equals(newsModel.getOriginal_image())){
                            detailModelList.add(new DetailModel(DetailRecycleViewAdapter.VIEW_TYPE_IMAGE, body));
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }else if(token.equals(AUDIO_UNIQUE_IDENTIFIER)){
            body=body.replace(token,NULL_SPACE);
            if(!body.contains(",")){
                detailModelList.add(new DetailModel(DetailRecycleViewAdapter.VIEW_TYPE_AUDIO, body));
                return;
            }
            String audio_link = body.substring(0, body.indexOf(","));
            DetailModel detailModel=new DetailModel();
            detailModel.setType(DetailRecycleViewAdapter.VIEW_TYPE_AUDIO);
            detailModel.setValue(audio_link);
            String sub_audio_link=body.replace(audio_link,"");
            String curFormat=NULL_SPACE;
            for(String format:IMAGE_FORMATS){
                if(sub_audio_link.contains(format)){
                    curFormat=format;
                }
            }
            if(curFormat.isEmpty()){
                detailModelList.add(detailModel);
                detailModelList.add(new DetailModel(DetailRecycleViewAdapter.VIEW_TYPE_IMAGE_DESC, sub_audio_link));
            }else {
                detailModel.setAudio_cover_image(sub_audio_link);
                detailModelList.add(detailModel);
            }
        }else {
                try {
                    if(!detailModelList.get(detailModelList.size()-1).getValue().equals(body)){
                           if(body.startsWith("\"")){
                               detailModelList.add(new DetailModel(DetailRecycleViewAdapter.VIEW_TYPE_LINE, body));
                           }else {
                               detailModelList.add(new DetailModel(DetailRecycleViewAdapter.VIEW_TYPE_TEXT, body));
                           }
                    }
                }catch (Exception e){
                    if(body.startsWith("\"")){
                        detailModelList.add(new DetailModel(DetailRecycleViewAdapter.VIEW_TYPE_LINE, body));
                    }else {
                        detailModelList.add(new DetailModel(DetailRecycleViewAdapter.VIEW_TYPE_TEXT, body));
           }  }
        }

        refresh();
    }
    private void refresh() {
        adapter.setDetailModelList(detailModelList);
        adapter.notifyDataSetChanged();

        if (progressLayout.getVisibility() == View.VISIBLE) {

            recyclerView.setAlpha(0f);
            recyclerView.setVisibility(View.VISIBLE);

            recyclerView.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .setListener(null);

            progressLayout.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            progressLayout.setVisibility(View.GONE);
                        }

                    });
        }
    }
    private void showNoNewsFound() {


        if (progressLayout.getVisibility() == View.VISIBLE) {

            no_offline_layout.setAlpha(0f);
            no_offline_layout.setVisibility(View.VISIBLE);

            no_offline_layout.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .setListener(null);

            progressLayout.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            progressLayout.setVisibility(View.GONE);
                        }

                    });
        }
    }

}







