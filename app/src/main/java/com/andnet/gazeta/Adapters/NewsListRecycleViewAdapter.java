package com.andnet.gazeta.Adapters;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andnet.gazeta.ChromeBrowser;
import com.andnet.gazeta.Constants;
import com.andnet.gazeta.Databases.DatabaseDescription;
import com.andnet.gazeta.MainActivity;
import com.andnet.gazeta.Models.News;
import com.andnet.gazeta.Models.Source;
import com.andnet.gazeta.NewsDetailActivity;
import com.andnet.gazeta.R;
import com.facebook.drawee.view.SimpleDraweeView;


import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.jzvd.JZVideoPlayerStandard;

import static android.content.Context.MODE_PRIVATE;

public class NewsListRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private android.support.v4.app.FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    public static final String YOUTUBE_API_KEY = "AIzaSyBpKQhNoay5m0WUg5lc4jf0s03SDf8meYk";
    public static final String YOUTUBE_IMAGE_URL = "http://img.youtube.com/vi/";
    public static final String YOUTUBE_BASE_LINK = "https://www.youtube.com/embed/";
    public static final String DETAIL_VIEW_SETTING_KEY = "pref_key_open_news_in_web_browser";
    public static final String DETAIL_VIDEO_VIEW_SETTING = "pref_key_enable_video_loading";
    public static final String KEY_LIST = "EXTRA_LIST";
    public static final String PAGE_TITLE = "PAGE_TITLE";
    public boolean videoLoading = true;

    private static final int NEWS_LIST_ITEM_NO_IMAGE_VIEW = 1;
    private static final int NEWS_LIST_ITEM_NORMAL_IMAGE_VIEW = 2;
    private static final int NEWS_LIST_ITEM_AUDIO = 3;
    private static final int NEWS_LIST_ITEM_VIDEO = 4;
    private static final int PROGRESS_LIST_ITEM = 5;
    private static final int NEWS_LIST_ITEM_YOUTUBE = 6;
    private static final int BANNER_ADD = 7;

    private SharedPreferences textSizePreference;
    private SharedPreferences defaultSharedPref;
    private SharedPreferences miniCardPrefrecne;
    private Typeface nyalaTypeFace;
    private Typeface robotoLight;
    private Typeface robotoRegular;
    private Typeface geezTypeNet;
    private List<Object> objectList = new ArrayList<>();
    private List<TextView> textViewForSize = new ArrayList<>();
    private boolean imageLoading = true;
    private Context context;
    private String pageTitle;
    private FirebaseDatabase firebaseDatabase;

    private YouTubePlayer youTubePlayer;

    public NewsListRecycleViewAdapter(Context context, String pageTitle) {
        this.context = context;
        this.pageTitle = pageTitle;
        if(context instanceof MainActivity){
            MainActivity mainActivity=(MainActivity)context;
            fragmentManager=mainActivity.getSupportFragmentManager();
            fragmentTransaction=fragmentManager.beginTransaction();
        }else if(context instanceof NewsDetailActivity){
            NewsDetailActivity newsDetailActivity=(NewsDetailActivity)context;
            fragmentManager=newsDetailActivity.getSupportFragmentManager();
            fragmentTransaction=fragmentManager.beginTransaction();

        }
        init();

    }

    private void init() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        defaultSharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        nyalaTypeFace = Typeface.createFromAsset(context.getAssets(), "NYALA.TTF");
        robotoLight = Typeface.createFromAsset(context.getAssets(), "Roboto-Light.ttf");
        geezTypeNet = Typeface.createFromAsset(context.getAssets(), "GeezTypeNet.ttf");
        videoLoading = defaultSharedPref.getBoolean(DETAIL_VIDEO_VIEW_SETTING, true);
        robotoRegular = Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf");
        imageLoading = defaultSharedPref.getBoolean(MainActivity.PREF_KEY_ENABLE_IMAGE_LOADING, true);
        textSizePreference = context.getSharedPreferences(MainActivity.TEXT_SIZE_FILE_NAME, MODE_PRIVATE);
        textSizePreference.registerOnSharedPreferenceChangeListener(textSizePreferenceListener);
        miniCardPrefrecne = context.getSharedPreferences(MainActivity.PREF_MINI_CARD_FILE_NAME, MODE_PRIVATE);
    }

    private void goToDetailActivity(int pos) {
        try {
            if (getNewsModel(pos) == null) return;
            if (getNewsModel(pos).getSourceVal() == null) return;

            if (getNewsModel(pos).getSourceVal().isAllowed()) {
                if (defaultSharedPref.getBoolean(DETAIL_VIEW_SETTING_KEY, false)) {
                    startChromeBrowser(pos);
                } else {
                    Intent intent = new Intent(context, NewsDetailActivity.class);

                    intent.putExtra(ReadLaterRvAdapter.EXTRA_ID, pos);
                    intent.putExtra(KEY_LIST, getKeyList());
                    intent.putExtra(PAGE_TITLE, pageTitle);
                    context.startActivity(intent);
                }

            } else {
                startChromeBrowser(pos);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void shareNews(int pos) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, getNewsModel(pos).getTitle() + "-> " + getNewsModel(pos).getLink());
        context.startActivity(shareIntent);

    }

    private void saveNews(int pos, final ImageView saveImageView) {

        try {
            final News news = getNewsModel(pos);
            if(news==null){
                Toast.makeText(context,"Error Saving News!",Toast.LENGTH_SHORT).show();
                return;
            }
            Source source = news.getSourceVal();
            final ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseDescription.SAVED_NEWS.COVER_IMAGE, news.getCover_image());
            contentValues.put(DatabaseDescription.SAVED_NEWS.IMAGE, news.getThumbnail());
            contentValues.put(DatabaseDescription.SAVED_NEWS.TITLE, news.getTitle());
            contentValues.put(DatabaseDescription.SAVED_NEWS.SYNOP, news.getSynop());
            contentValues.put(DatabaseDescription.SAVED_NEWS.DATE, news.getDate());
            contentValues.put(DatabaseDescription.SAVED_NEWS.TIME_STAMP, news.getTimestamp());
            contentValues.put(DatabaseDescription.SAVED_NEWS.AUTHOR, news.getAuthor());
            contentValues.put(DatabaseDescription.SAVED_NEWS.NEWS_LINK, news.getLink());
            contentValues.put(DatabaseDescription.SAVED_NEWS.SOURCE_NAME, source.getName());
            contentValues.put(DatabaseDescription.SAVED_NEWS.SOURCE_LINK, source.getLink());
            contentValues.put(DatabaseDescription.SAVED_NEWS.SOURCE_LOGO, source.getLogo());
            contentValues.put(DatabaseDescription.SAVED_NEWS.COVER_AUDIO, news.getCover_audio());
            contentValues.put(DatabaseDescription.SAVED_NEWS.COVER_VIDEO, news.getCover_video());
            contentValues.put(DatabaseDescription.SAVED_NEWS.COVER_Y_EMBED, news.getCover_y_embed());
            contentValues.put(DatabaseDescription.SAVED_NEWS.COVER_CAPTION, news.getCover_caption());
            contentValues.put(DatabaseDescription.SAVED_NEWS.O_COVER_PREVIEW, news.getOriginal_image());
            contentValues.put(DatabaseDescription.SAVED_NEWS.KEY, news.getKey());
            contentValues.put(DatabaseDescription.SAVED_NEWS.O_COVER_V_PREVIEW, news.getO_cover_v_prev());
            contentValues.put(DatabaseDescription.SAVED_NEWS.O_COVER_A_PREVIEW, news.getO_cover_a_prev());
            if (news.getSourceVal().isAllowed()) {
                contentValues.put(DatabaseDescription.SAVED_NEWS.IS_ALLOWED, 1);
            } else {
                contentValues.put(DatabaseDescription.SAVED_NEWS.IS_ALLOWED, 0);

            }

            Cursor myCur = context.getContentResolver().query(DatabaseDescription.SAVED_NEWS.NEWS_CONTENT_URI, new String[]{DatabaseDescription.SAVED_NEWS._ID},
                    DatabaseDescription.SAVED_NEWS.TITLE + "= ?", new String[]{news.getTitle()}, null);
            if (myCur != null && myCur.moveToFirst()) {
                //news has already saved so remove the news
                context.getContentResolver().delete(DatabaseDescription.SAVED_NEWS.buildContactUriForeId(myCur.getLong(0)), null, null);
                context.getContentResolver().delete(DatabaseDescription.BODY_TABLE.buildContactUriForeId(myCur.getLong(0)), null, null);
                saveImageView.setImageResource(R.drawable.ic_bookmark_outline_white);
                Toast.makeText(context, R.string.removed_from_read_later, Toast.LENGTH_SHORT).show();
            } else {
                firebaseDatabase.getReference(Constants.ETHIOPIA).child(Constants.NEWS_B).child(news.getKey()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        JSONArray jsonArray = new JSONArray();
                        for (DataSnapshot dataSnapshot1 : children) {
                            jsonArray.put(dataSnapshot1.getValue());
                        }
                        ContentValues contentValues1 = new ContentValues();
                        contentValues1.put(DatabaseDescription.BODY_TABLE.key, dataSnapshot.getRef().getKey());
                        contentValues1.put(DatabaseDescription.BODY_TABLE.body, jsonArray.toString());
                        context.getContentResolver().insert(DatabaseDescription.SAVED_NEWS.NEWS_CONTENT_URI, contentValues);
                        context.getContentResolver().insert(DatabaseDescription.BODY_TABLE.BODY_TABLE_CONTENT_URI, contentValues1);
                        saveImageView.setImageResource(R.drawable.ic_bookmark);
                        Toast.makeText(context, R.string.added_to_read_later, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            if (myCur != null) myCur.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public int getItemViewType(int position) {

        if(position%6==0 && position!=0)return BANNER_ADD;

        Object object = getObjectList().get(position);
        if (object instanceof News) {
            News news = (News) object;
            if (news.getCover_y_embed() != null) {
                if (!videoLoading) return 0;
                if (news.getCover_y_embed().contains(YOUTUBE_BASE_LINK)) {
                    return NEWS_LIST_ITEM_YOUTUBE;
                } else {
                    return NEWS_LIST_ITEM_VIDEO;
                }
            }
            if (news.getCover_audio() != null) {
                if (!videoLoading) return 0;
                return NEWS_LIST_ITEM_AUDIO;
            }
            if (news.getCover_video() != null) {
                if (!videoLoading) return 0;
                return NEWS_LIST_ITEM_VIDEO;
            }
            if (news.getThumbnail() == null || news.getThumbnail().isEmpty())
                return NEWS_LIST_ITEM_NO_IMAGE_VIEW;
            if (imageLoading) {
                if (news.getThumbnail() == null || news.getThumbnail().isEmpty()) {
                    if (news.getCover_image() != null && !news.getCover_image().isEmpty()) {
                        return NEWS_LIST_ITEM_NORMAL_IMAGE_VIEW;
                    } else {
                        return NEWS_LIST_ITEM_NO_IMAGE_VIEW;
                    }
                } else {
                    return NEWS_LIST_ITEM_NORMAL_IMAGE_VIEW;
                }
            } else
                return NEWS_LIST_ITEM_NO_IMAGE_VIEW;
        } else if (object == null) {
            return PROGRESS_LIST_ITEM;
        }
        return BANNER_ADD;

    }

    public int getItemCount() {
        return objectList.size();
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = null;
        switch (viewType) {
            case NEWS_LIST_ITEM_NO_IMAGE_VIEW:
                view = inflater.inflate(R.layout.news_list_item_no_image_view, parent, false);
                return new NewsNoImageViewHolder(view);
            case NEWS_LIST_ITEM_NORMAL_IMAGE_VIEW:
                    view = inflater.inflate(R.layout.news_list_item_normal_image_view, parent, false);
                return new NewsNormalImageViewHolder(view);
            case PROGRESS_LIST_ITEM:
                view = inflater.inflate(R.layout.progress_list_item, parent, false);
                return new ProgressViewHolder(view);
            case NEWS_LIST_ITEM_AUDIO:
                view = inflater.inflate(R.layout.news_list_item_audio, parent, false);
                return new NewsAudioViewHolder(view);
            case NEWS_LIST_ITEM_VIDEO:
                view = inflater.inflate(R.layout.news_list_item_video, parent, false);
                return new NewsVideoViewHolder(view);
            case NEWS_LIST_ITEM_YOUTUBE:
                view = inflater.inflate(R.layout.youtube_video_player_frame, parent, false);
                return new NewsYoutubeViewHolder(view);
            case BANNER_ADD:
                view = inflater.inflate(R.layout.emety_layout, parent, false);
                return new NativeExpressAdViewHolder(view);
            default:
                view = inflater.inflate(R.layout.emety_layout, parent, false);
                return new NativeExpressAdViewHolder(view);
        }

    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        News news = null;
        if (getObjectList().get(position) instanceof News) {
            news = (News) getObjectList().get(position);
        }
        if (news == null) return;

        switch (getItemViewType(position)) {
            case NEWS_LIST_ITEM_NO_IMAGE_VIEW: {
                NewsNoImageViewHolder noImageViewHolder = (NewsNoImageViewHolder) holder;
                setTypeFace(holder, news.getTitle());
                noImageViewHolder.titleTextView.setText(news.getTitle().trim());
                if (news.getSynop() != null)


                noImageViewHolder.synopTextView.setText(news.getSynop().trim().replace("\u00a0", ""));
                noImageViewHolder.sourceTextView.setText((news.getSourceVal().getName()));
                noImageViewHolder.sourceImageView.setImageURI(news.getSourceVal().getLogo());
                noImageViewHolder.dateTextView.setText(getDateDiff(news.getTimestamp()));
                if (isSaved(news.getKey())) {
                    noImageViewHolder.saveImageView.setImageResource(R.drawable.ic_bookmark);
                } else {
                    noImageViewHolder.saveImageView.setImageResource(R.drawable.ic_bookmark_outline_white);
                }
                noImageViewHolder.sourceImageView.setBackgroundColor(Color.parseColor("#00ffffff"));
                noImageViewHolder.sourceImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                noImageViewHolder.sourceImageView.setImageURI(news.getSourceVal().getLogo());
                break;
            }
            case NEWS_LIST_ITEM_NORMAL_IMAGE_VIEW:
                NewsNormalImageViewHolder normalImageView = (NewsNormalImageViewHolder) holder;
                setTypeFace(normalImageView, news.getTitle());
                if (news.getSynop() != null) {
                        setRobotoRegular(normalImageView.synopTextView);
                    normalImageView.synopTextView.setText(news.getSynop().trim());
                }
                if (news.getThumbnail() == null || news.getThumbnail().isEmpty()) {
                    normalImageView.newsImageView.setImageURI(news.getCover_image().trim());
                } else {
                    normalImageView.newsImageView.setImageURI(news.getThumbnail().trim());
                }
                normalImageView.titleTextView.setText(news.getTitle().trim());
                normalImageView.sourceTextView.setText(news.getSourceVal().getName());
                normalImageView.dateTextView.setText(getDateDiff(news.getTimestamp()));
                if (isSaved(news.getKey())) {
                    normalImageView.saveImageView.setImageResource(R.drawable.ic_bookmark);
                } else {
                    normalImageView.saveImageView.setImageResource(R.drawable.ic_bookmark_outline_white);
                }
                normalImageView.sourceImageView.setBackgroundColor(Color.parseColor("#00ffffff"));
                normalImageView.sourceImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                normalImageView.sourceImageView.setImageURI(news.getSourceVal().getLogo());
                break;
            case NEWS_LIST_ITEM_AUDIO:
                final NewsAudioViewHolder newsAudioViewHolder = (NewsAudioViewHolder) holder;
                setTypeFace(newsAudioViewHolder, news.getTitle());
                newsAudioViewHolder.titleTextView.setText(news.getTitle().trim());
                newsAudioViewHolder.sourceTextView.setText(news.getSourceVal().getName());
                newsAudioViewHolder.sourceImageView.setImageURI(news.getSourceVal().getLogo());
                newsAudioViewHolder.dateTextView.setText(getDateDiff(news.getTimestamp()));
                if (isSaved(news.getKey())) {
                    newsAudioViewHolder.saveImageView.setImageResource(R.drawable.ic_bookmark);
                } else {
                    newsAudioViewHolder.saveImageView.setImageResource(R.drawable.ic_bookmark_outline_white);
                }
                if (news.getO_cover_a_prev() != null)
                    newsAudioViewHolder.youTubeThumbnailView.setImageURI(news.getO_cover_a_prev());
                else if (news.getCover_image() != null) {
                    newsAudioViewHolder.youTubeThumbnailView.setImageURI(news.getCover_image());
                } else if (news.getThumbnail() != null) {
                    newsAudioViewHolder.youTubeThumbnailView.setImageURI(news.getCover_image());
                }
                break;
            case NEWS_LIST_ITEM_VIDEO: {
                final NewsVideoViewHolder newsVideoViewHolder = (NewsVideoViewHolder) holder;
                setTypeFace(newsVideoViewHolder, news.getTitle());
                newsVideoViewHolder.titleTextView.setText(news.getTitle().trim());
                newsVideoViewHolder.sourceTextView.setText(news.getSourceVal().getName());
                newsVideoViewHolder.sourceImageView.setImageURI(news.getSourceVal().getLogo());
                newsVideoViewHolder.dateTextView.setText(getDateDiff(news.getTimestamp()));
                if (isSaved(news.getKey())) {
                    newsVideoViewHolder.saveImageView.setImageResource(R.drawable.ic_bookmark);
                } else {
                    newsVideoViewHolder.saveImageView.setImageResource(R.drawable.ic_bookmark_outline_white);
                }
                if (news.getO_cover_v_prev() != null)
                    newsVideoViewHolder.youTubeThumbnailView.setImageURI(news.getO_cover_v_prev());
                else if (news.getCover_image() != null)
                    newsVideoViewHolder.youTubeThumbnailView.setImageURI(news.getCover_image());
                break;
            }
            case NEWS_LIST_ITEM_YOUTUBE:
                final NewsYoutubeViewHolder youtubeViewHolder = (NewsYoutubeViewHolder) holder;
                setTypeFace(youtubeViewHolder, news.getTitle());
                youtubeViewHolder.titleTextView.setText(news.getTitle());
                youtubeViewHolder.dateTextView.setText(getDateDiff(news.getTimestamp()));
                youtubeViewHolder.sourceTextView.setText(news.getSourceVal().getName());
                youtubeViewHolder.sourceImageView.setImageURI(news.getSourceVal().getLogo());
                if (isSaved(news.getKey())) {
                    youtubeViewHolder.saveImageView.setImageResource(R.drawable.ic_bookmark);
                } else {
                    youtubeViewHolder.saveImageView.setImageResource(R.drawable.ic_bookmark_outline_white);
                }
                String key = news.getCover_y_embed().replace(YOUTUBE_BASE_LINK, "");
                Uri imageUri = Uri.parse(YOUTUBE_IMAGE_URL).buildUpon()
                        .appendPath(key)
                        .appendPath("hqdefault.jpg")
                        .build();
                    youtubeViewHolder.youTubeThumbnailView.setImageURI(imageUri);
//                    youtubeViewHolder.frameLayout.setId(100*position+100);
//                    youtubeViewHolder.playButton.setOnClickListener(view -> {
//                    youtubeViewHolder.frameLayout.setVisibility(View.VISIBLE);
//                    youtubeViewHolder.playButton.setVisibility(View.GONE);
//                    youtubeViewHolder.youTubeThumbnailView.setVisibility(View.GONE);
//                    YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
//                    youTubePlayerFragment.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
//                        @Override
//                        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
//
//                             player.setShowFullscreenButton(false);
//                            if(youTubePlayer!=null){
//                                youTubePlayer.release();
//                            }
//                            if (!wasRestored) {
//                                youTubePlayer = player;
//                                youTubePlayer.setFullscreen(false);
//                                youTubePlayer.loadVideo(key);
//                                youTubePlayer.play();
//                            }
//                        }
//                        @Override
//                        public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {
//
//                        }
//                    });
//
//                    if(context instanceof MainActivity){
//                        MainActivity mainActivity=(MainActivity)context;
//                        fragmentManager=mainActivity.getSupportFragmentManager();
//                        fragmentTransaction=fragmentManager.beginTransaction();
//                    }else if(context instanceof NewsDetailActivity){
//                        NewsDetailActivity newsDetailActivity=(NewsDetailActivity)context;
//                        fragmentManager=newsDetailActivity.getSupportFragmentManager();
//                        fragmentTransaction=fragmentManager.beginTransaction();
//
//                    }
//                    fragmentTransaction.add(100*position+100, youTubePlayerFragment);
//                    fragmentTransaction.commit();
//
//                    if (!youtubeViewHolder.frameLayout.isShown()) {
//                        youtubeViewHolder.playButton.setVisibility(View.VISIBLE);
//                        youtubeViewHolder.youTubeThumbnailView.setVisibility(View.VISIBLE);
//                    }
//                });
                break;


        }
    }

    private SharedPreferences.OnSharedPreferenceChangeListener textSizePreferenceListener = (sharedPreferences, s) -> setTexSize();

    private ArrayList<String> getKeyList() {
        ArrayList<String> keyList = new ArrayList<>();

        try {

            for (Object o : objectList) {
                if (o != null)
                    if (o instanceof News) {
                        keyList.add(((News) o).getKey());
                    } else {
                        keyList.add("[}]");
                    }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return keyList;
    }

    public List<Object> getObjectList() {
        return objectList;
    }

    public void add(Object o) {
       objectList.add(o);
       notifyItemInserted(objectList.size()-1);
    }

    public void addProgress() {
        this.objectList.add(null);
        this.notifyItemInserted(getObjectList().size() - 1);
    }

    public void removeProgress() {
         if(objectList.isEmpty()){
             return;
         }
        if(objectList.get(objectList.size()-1)==null){
            objectList.remove(objectList.size()-1);
            notifyItemRemoved(objectList.size()-1);
        }
    }

    public void setObjectList(List<Object> objectList) {
        this.objectList = objectList;
    }

    private News getNewsModel(int pos) {
        try {
            return ((News) objectList.get(pos));
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setTexSize() {

        try {
            for (TextView textView : textViewForSize)
                if (textView.getId() == R.id.title_textView) {
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSizePreference.getFloat(MainActivity.PREF_KEY_TITLE, context.getResources().getDimension(R.dimen.title_text_size_med)));
                } else if (textView.getId() == R.id.synop_textView) {
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSizePreference.getFloat(MainActivity.PREF_KEY_SYNOP, textSizePreference.getFloat(MainActivity.PREF_KEY_TITLE, context.getResources().getDimension(R.dimen.title_text_size_med))));
                }
        } catch (Exception e) {

        }

    }

    private void setNyalaTypeFace(TextView textView) {
        textView.setTypeface(nyalaTypeFace);
    }

    private void setRobotoRegular(TextView textView) {
        textView.setTypeface(robotoRegular);

    }

    private boolean isEn(String title) {
        try {
            int k = title.charAt(1);
            if (Character.isDigit(k)){
                 k = title.charAt(2);
            }
            return k < 200;
        } catch (Exception e) {
            return true;
        }

    }

    private boolean isSaved(String key) {
        if (key == null) return false;
        Cursor cur = context.getContentResolver().query(DatabaseDescription.SAVED_NEWS.NEWS_CONTENT_URI, new String[]{DatabaseDescription.SAVED_NEWS._ID}, DatabaseDescription.SAVED_NEWS.KEY + " = ? ", new String[]{key}, null);

        if (cur != null && cur.moveToFirst()) {
            cur.close();
            return true;
        }
        {
            return false;
        }
    }

    private void startChromeBrowser(int pos) {
        String link = ((News) getObjectList().get(pos)).getLink();
        ChromeBrowser.openUrl(context, link);
    }

    private String getDateDiff(long timeStamp) {
        long def = System.currentTimeMillis() - timeStamp;
        long days = TimeUnit.MILLISECONDS.toDays(def);
        if (days < 1) {
            long hours = TimeUnit.MILLISECONDS.toHours(def);

            if (hours < 1) {
                long minutes = TimeUnit.MILLISECONDS.toMinutes(def);
                return minutes + " minutes ago";
            } else {
                return hours + " hours ago";
            }
        } else {
            return days + " days ago";
        }
    }

    private void setTypeFace(RecyclerView.ViewHolder holder, String title) {
        if (holder instanceof NewsNoImageViewHolder) {
            NewsNoImageViewHolder noImageViewHolder = (NewsNoImageViewHolder) holder;
            if (isEn(title)) {
                setRobotoRegular(noImageViewHolder.titleTextView);
                setRobotoRegular(noImageViewHolder.synopTextView);
                setRobotoRegular(noImageViewHolder.sourceTextView);
                setRobotoRegular(noImageViewHolder.dateTextView);
            } else {
                setNyalaTypeFace(noImageViewHolder.titleTextView);
                setNyalaTypeFace(noImageViewHolder.synopTextView);
                setRobotoRegular(noImageViewHolder.sourceTextView);
                setRobotoRegular(noImageViewHolder.dateTextView);
            }
        } else if (holder instanceof NewsNormalImageViewHolder) {
            NewsNormalImageViewHolder newsNormalImageViewHolder = (NewsNormalImageViewHolder) holder;
            if (isEn(title)) {
                setRobotoRegular(newsNormalImageViewHolder.titleTextView);
                setRobotoRegular(newsNormalImageViewHolder.sourceTextView);
                setRobotoRegular(newsNormalImageViewHolder.dateTextView);
            } else {
                setNyalaTypeFace(newsNormalImageViewHolder.titleTextView);
                setRobotoRegular(newsNormalImageViewHolder.sourceTextView);
                setRobotoRegular(newsNormalImageViewHolder.dateTextView);
            }

        } else if (holder instanceof NewsAudioViewHolder) {
            NewsAudioViewHolder newsAudioViewHolder = (NewsAudioViewHolder) holder;
            if (isEn(title)) {
                setRobotoRegular(newsAudioViewHolder.titleTextView);
                setRobotoRegular(newsAudioViewHolder.sourceTextView);
                setRobotoRegular(newsAudioViewHolder.dateTextView);
            } else {
                setNyalaTypeFace(newsAudioViewHolder.titleTextView);
                setRobotoRegular(newsAudioViewHolder.sourceTextView);
                setRobotoRegular(newsAudioViewHolder.dateTextView);
            }


        } else if (holder instanceof NewsVideoViewHolder) {
            NewsVideoViewHolder newsVideoViewHolder = (NewsVideoViewHolder) holder;
            if (isEn(title)) {
                setRobotoRegular(newsVideoViewHolder.titleTextView);
                setRobotoRegular(newsVideoViewHolder.sourceTextView);
                setRobotoRegular(newsVideoViewHolder.dateTextView);
            } else {
                setNyalaTypeFace(newsVideoViewHolder.titleTextView);
                setRobotoRegular(newsVideoViewHolder.sourceTextView);
                setRobotoRegular(newsVideoViewHolder.dateTextView);
            }


        } else if (holder instanceof NewsYoutubeViewHolder) {
            NewsYoutubeViewHolder newsYoutubeViewHolder = (NewsYoutubeViewHolder) holder;
            if (isEn(title)) {
                setRobotoRegular(newsYoutubeViewHolder.titleTextView);
                setRobotoRegular(newsYoutubeViewHolder.sourceTextView);
                setRobotoRegular(newsYoutubeViewHolder.dateTextView);
            } else {
                setNyalaTypeFace(newsYoutubeViewHolder.titleTextView);
                setRobotoRegular(newsYoutubeViewHolder.sourceTextView);
                setRobotoRegular(newsYoutubeViewHolder.dateTextView);
            }

        }


    }

    private class NewsNoImageViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView sourceImageView;
        ImageView shareImageView;
        ImageView saveImageView;
        TextView titleTextView;
        TextView synopTextView;
        TextView dateTextView;
        TextView sourceTextView;

        public NewsNoImageViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_textView);
            synopTextView = itemView.findViewById(R.id.synop_textView);
            dateTextView = itemView.findViewById(R.id.date_textView);
            sourceTextView = itemView.findViewById(R.id.source_name_textView);
            sourceImageView = itemView.findViewById(R.id.source_imageView);
            shareImageView = itemView.findViewById(R.id.share_imageView);
            saveImageView = itemView.findViewById(R.id.save_imageView);
            List<TextView> textViewList = new ArrayList<>();
            textViewList.add(titleTextView);
            textViewList.add(synopTextView);
            textViewList.add(dateTextView);
            textViewList.add(sourceTextView);
            textViewForSize.add(titleTextView);
            textViewForSize.add(synopTextView);
            setTexSize();
            itemView.setOnClickListener(view -> {
                checkBody(getAdapterPosition());
            });
            shareImageView.setOnClickListener(view -> shareNews(getAdapterPosition()));
            saveImageView.setOnClickListener(view -> saveNews(getAdapterPosition(), saveImageView));
        }
    }

    private class NewsNormalImageViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView newsImageView;
        SimpleDraweeView sourceImageView;
        ImageView shareImageView;
        ImageView saveImageView;
        TextView titleTextView;
        TextView synopTextView;
        TextView dateTextView;
        TextView sourceTextView;

        public NewsNormalImageViewHolder(final View itemView) {
            super(itemView);
            newsImageView = itemView.findViewById(R.id.news_imageView);
            titleTextView = itemView.findViewById(R.id.title_textView);
            synopTextView = itemView.findViewById(R.id.synop_textView);
            dateTextView = itemView.findViewById(R.id.date_textView);
            sourceTextView = itemView.findViewById(R.id.source_name_textView);
            sourceImageView = itemView.findViewById(R.id.source_imageView);
            shareImageView = itemView.findViewById(R.id.share_imageView);
            saveImageView = itemView.findViewById(R.id.save_imageView);

            List<TextView> textViewList = new ArrayList<>();
            textViewList.add(titleTextView);
            textViewList.add(synopTextView);
            textViewList.add(dateTextView);
            textViewList.add(sourceTextView);
            textViewForSize.add(titleTextView);
            textViewForSize.add(synopTextView);
            setTexSize();
            itemView.setOnClickListener(view -> checkBody(getAdapterPosition()));
            shareImageView.setOnClickListener(view -> shareNews(getAdapterPosition()));
            saveImageView.setOnClickListener(view -> saveNews(getAdapterPosition(), saveImageView));
        }
    }

    private class NewsYoutubeViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout relativeLayoutOverYouTubeThumbnailView;
        SimpleDraweeView youTubeThumbnailView;
        ImageView playButton;
        SimpleDraweeView sourceImageView;
        ImageView shareImageView;
        ImageView saveImageView;
        TextView titleTextView;
        TextView synopTextView;
        TextView dateTextView;
        TextView sourceTextView;
        FrameLayout frameLayout;


        public NewsYoutubeViewHolder(final View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_textView);
            synopTextView = itemView.findViewById(R.id.synop_textView);
            dateTextView = itemView.findViewById(R.id.date_textView);
            sourceTextView = itemView.findViewById(R.id.source_name_textView);
            sourceImageView = itemView.findViewById(R.id.source_imageView);
            shareImageView = itemView.findViewById(R.id.share_imageView);
            saveImageView = itemView.findViewById(R.id.save_imageView);
//            frameLayout=itemView.findViewById(R.id.videoFrameHolder);
            playButton = itemView.findViewById(R.id.btnYoutube_player);
            relativeLayoutOverYouTubeThumbnailView =itemView.findViewById(R.id.relativeLayout_over_youtube_thumbnail);
            youTubeThumbnailView = itemView.findViewById(R.id.youtube_thumbnail_view);

            List<TextView> textViewList = new ArrayList<>();
            textViewList.add(titleTextView);
            textViewList.add(synopTextView);
            textViewList.add(dateTextView);
            textViewList.add(sourceTextView);

            textViewForSize.add(titleTextView);
            textViewForSize.add(synopTextView);
            setTexSize();


            itemView.setOnClickListener(view -> {
                News news =getNewsModel(getAdapterPosition());
                if(news ==null)return;
                if(news.getCover_y_embed()==null)return;
                Intent intent = YouTubeStandalonePlayer.createVideoIntent((Activity) context, YOUTUBE_API_KEY, news.getCover_y_embed().replace("https://www.youtube.com/embed/",""));
                context.startActivity(intent);
            });
            shareImageView.setOnClickListener(view -> shareNews(getAdapterPosition()));
            saveImageView.setOnClickListener(view -> saveNews(getAdapterPosition(), saveImageView));
        }
    }

    private class ProgressViewHolder extends RecyclerView.ViewHolder {

        public ProgressViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class NewsVideoViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout relativeLayoutOverYouTubeThumbnailView;
        SimpleDraweeView youTubeThumbnailView;
        ImageView playButton;
        SimpleDraweeView sourceImageView;
        ImageView shareImageView;
        ImageView saveImageView;
        TextView titleTextView;
        TextView synopTextView;
        TextView dateTextView;
        TextView sourceTextView;
        JZVideoPlayerStandard videoPlayerSimple;

        public NewsVideoViewHolder(final View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_textView);
            synopTextView = itemView.findViewById(R.id.synop_textView);
            dateTextView = itemView.findViewById(R.id.date_textView);
            sourceTextView = itemView.findViewById(R.id.source_name_textView);
            sourceImageView = itemView.findViewById(R.id.source_imageView);
            shareImageView = itemView.findViewById(R.id.share_imageView);
            saveImageView = itemView.findViewById(R.id.save_imageView);
            videoPlayerSimple = itemView.findViewById(R.id.videoplayer);

            List<TextView> textViewList = new ArrayList<>();
            textViewList.add(titleTextView);
            textViewList.add(synopTextView);
            textViewList.add(dateTextView);
            textViewList.add(sourceTextView);

            textViewForSize.add(titleTextView);
            textViewForSize.add(synopTextView);
            setTexSize();

            playButton = itemView.findViewById(R.id.btnYoutube_player);
            relativeLayoutOverYouTubeThumbnailView = (RelativeLayout) itemView.findViewById(R.id.relativeLayout_over_youtube_thumbnail);
            youTubeThumbnailView = itemView.findViewById(R.id.youtube_thumbnail_view);

//            if(getSimpleNewsModel(getAdapterPosition())==null)return;

            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playButton.setVisibility(View.GONE);
                    youTubeThumbnailView.setVisibility(View.GONE);
                    videoPlayerSimple.setVisibility(View.VISIBLE);
                    videoPlayerSimple.setUp(getNewsModel(getAdapterPosition()).getCover_video()
                            , JZVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");

                    videoPlayerSimple.startButton.performClick();


                }
            });


            //go to the detail activity
            itemView.setOnClickListener(view -> checkBody(getAdapterPosition()));

            //share the current news
            shareImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shareNews(getAdapterPosition());
                }
            });

            //add news to read later
            saveImageView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    saveNews(getAdapterPosition(), saveImageView);
                }
            });

        }


    }

    private class NewsAudioViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout relativeLayoutOverYouTubeThumbnailView;
        SimpleDraweeView youTubeThumbnailView;
        ImageView playButton;
        SimpleDraweeView sourceImageView;
        ImageView shareImageView;
        ImageView saveImageView;
        TextView titleTextView;
        TextView synopTextView;
        TextView dateTextView;
        TextView sourceTextView;
        JZVideoPlayerStandard videoPlayerSimple;

        public NewsAudioViewHolder(View itemView) {
            super(itemView);
            videoPlayerSimple = itemView.findViewById(R.id.videoplayer);
            titleTextView = itemView.findViewById(R.id.title_textView);
            synopTextView = itemView.findViewById(R.id.synop_textView);
            dateTextView = itemView.findViewById(R.id.date_textView);
            sourceTextView = itemView.findViewById(R.id.source_name_textView);
            sourceImageView = itemView.findViewById(R.id.source_imageView);
            shareImageView = itemView.findViewById(R.id.share_imageView);
            saveImageView = itemView.findViewById(R.id.save_imageView);

            List<TextView> textViewList = new ArrayList<>();
            textViewList.add(titleTextView);
            textViewList.add(synopTextView);
            textViewList.add(dateTextView);
            textViewList.add(sourceTextView);

            textViewForSize.add(titleTextView);
            textViewForSize.add(synopTextView);
            setTexSize();

            playButton = itemView.findViewById(R.id.btnYoutube_player);
            relativeLayoutOverYouTubeThumbnailView = itemView.findViewById(R.id.relativeLayout_over_youtube_thumbnail);
            youTubeThumbnailView = itemView.findViewById(R.id.youtube_thumbnail_view);

            playButton.setOnClickListener(v -> {
                playButton.setVisibility(View.GONE);
                youTubeThumbnailView.setVisibility(View.GONE);
                videoPlayerSimple.setVisibility(View.VISIBLE);

                videoPlayerSimple.setUp(getNewsModel(getAdapterPosition()).getCover_audio()
                        , JZVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
            });
            itemView.setOnClickListener(view -> checkBody(getAdapterPosition()));
            shareImageView.setOnClickListener(view -> shareNews(getAdapterPosition()));
            saveImageView.setOnClickListener(view -> saveNews(getAdapterPosition(), saveImageView));

        }
    }

    public class EmetyVeiwHolder extends RecyclerView.ViewHolder {
        public EmetyVeiwHolder(View itemView) {
            super(itemView);
        }
    }

    public class NativeExpressAdViewHolder extends RecyclerView.ViewHolder {
        NativeExpressAdViewHolder(View view) {
            super(view);
        }
    }

    private void checkBody(int pos){

        try {
            firebaseDatabase.getReference(Constants.ETHIOPIA).child(Constants.NEWS_B).child(getNewsModel(pos).getKey()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        startChromeBrowser(pos);
                    } else {
                        goToDetailActivity(pos);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    startChromeBrowser(pos);

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
