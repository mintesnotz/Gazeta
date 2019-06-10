package com.andnet.gazeta.Adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andnet.gazeta.ChromeBrowser;
import com.andnet.gazeta.Databases.DatabaseDescription;
import com.andnet.gazeta.Fragments.ReadLaterFragment;
import com.andnet.gazeta.MainActivity;
import com.andnet.gazeta.Models.News;
import com.andnet.gazeta.Models.Source;
import com.andnet.gazeta.R;
import com.andnet.gazeta.ReadLaterDetailActivity;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ReadLaterRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{



    public static final String YOUTUBE_API_KEY="AIzaSyBpKQhNoay5m0WUg5lc4jf0s03SDf8meYk";
    public static final String YOUTUBE_IMAGE_URL="http://img.youtube.com/vi/";
    public static final String YOUTUBE_BASE_LINK="https://www.youtube.com/embed/";

    //constant for view typs
    private static final int NEWS_LIST_ITEM_NO_IMAGE_VIEW = 1;
    private static final int NEWS_LIST_ITEM_NORMAL_IMAGE_VIEW = 2;
    private static final int NEWS_LIST_ITEM_AUDIO=3;
    private static final int NEWS_LIST_ITEM_VIDEO=4;
    private static final int NEWS_LIST_ITEM_YOUTUBE=5;


    private Typeface nyalaTypeFace;
    private Typeface geezMahtemUnicodeTypeFace;
    private Typeface robotoLight;
    private Typeface robotoRegular;



    private Context context;
    private Cursor cursor = null;
    private SharedPreferences sharedPreferences;
    private SharedPreferences textSizePreference;
    private boolean imageLoading = true;
    private Typeface typeface;
    private List<TextView> textViewForSize = new ArrayList<>();
    private ReadLaterFragment readLaterFragment;

    public static final String EXTRA_ID="extra_id";


    //con--------
    public ReadLaterRvAdapter(Context context,ReadLaterFragment readLaterFragment){
        this.readLaterFragment=readLaterFragment;
        this.context=context;
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        imageLoading = sharedPreferences.getBoolean(MainActivity.PREF_KEY_ENABLE_IMAGE_LOADING, true);
        textSizePreference = context.getSharedPreferences(MainActivity.TEXT_SIZE_FILE_NAME, Context.MODE_PRIVATE);
        textSizePreference.registerOnSharedPreferenceChangeListener(textSizeChangeLatiner);
        nyalaTypeFace = Typeface.createFromAsset(context.getAssets(), "NYALA.TTF");
        geezMahtemUnicodeTypeFace = Typeface.createFromAsset(context.getAssets(), "GS GeezMahtemUnicode.ttf");
        robotoLight = Typeface.createFromAsset(context.getAssets(), "Roboto-Light.ttf");
        robotoRegular = Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf");

    }


    private News getNewsModel(int pos){

        try {
            cursor.moveToPosition(pos);
            News news =new News();
            Source source=new Source();
            news.setThumbnail(cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.IMAGE)));
            news.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.TITLE)));
            news.setKey(cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.KEY)));
            news.setAuthor(cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.AUTHOR)));
            news.setCover_audio(cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.COVER_AUDIO)));
            news.setCover_video(cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.COVER_VIDEO)));
            news.setCover_caption(cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.COVER_CAPTION)));
            news.setDate(cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.DATE)));
            news.setLink(cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.NEWS_LINK)));
            news.setSynop(cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.SYNOP)));
            news.setCover_image(cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.COVER_IMAGE)));
            news.setCover_y_embed(cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.COVER_Y_EMBED)));
            news.setOriginal_image(cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.ORIGINAL_IMAGE)));
            news.setTimestamp(cursor.getLong(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.TIME_STAMP)));
            news.setO_cover_a_prev(cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.O_COVER_A_PREVIEW)));
            news.setO_cover_v_prev(cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.O_COVER_V_PREVIEW)));
            source.setName(cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.SOURCE_NAME)));
            source.setLogo(cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.SOURCE_LOGO)));
            source.setLink(cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.SOURCE_LINK)));
            int allowed=cursor.getInt(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.IS_ALLOWED));
            if(allowed==1){
                source.setAllowed(true);
            }else {

                source.setAllowed(false);

            }
            news.setSourceVal(source);
            return news;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //share the news
    private void shareNews(int pos){
        cursor.moveToPosition(pos);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.TITLE))+ "-> " +   cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.NEWS_LINK)));
        context.startActivity(shareIntent);

    }

    private void removeNews(int pos){
        cursor.moveToPosition(pos);
        final String title=cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.TITLE));
        final Cursor myCur= context.getContentResolver().query(DatabaseDescription.SAVED_NEWS.NEWS_CONTENT_URI,new String[]{"rowid"},
                DatabaseDescription.SAVED_NEWS.TITLE + "= ?",new String[]{title},null);
        if(myCur!=null && myCur.moveToFirst()) {
            final Uri itemUri = DatabaseDescription.SAVED_NEWS.buildContactUriForeId(myCur.getLong(myCur.getColumnIndex(DatabaseDescription.SAVED_NEWS._ID)));
            final Uri bodyItemUri=DatabaseDescription.BODY_TABLE.buildContactUriForeId(myCur.getLong(myCur.getColumnIndex(DatabaseDescription.SAVED_NEWS._ID)));
            MainActivity mainActivity=(MainActivity)context;
            Snackbar snackbar=Snackbar.make(mainActivity.findViewById(R.id.home_layout_coordinate),context.getString(R.string.removed_from_read_later),
                    Snackbar.LENGTH_SHORT);
            snackbar.show();
            context.getContentResolver().delete(itemUri,null,null);
            context.getContentResolver().delete(bodyItemUri,null,null);
        }
        Cursor hello=context.getContentResolver().query(DatabaseDescription.SAVED_NEWS.NEWS_CONTENT_URI,null,null,null,null);
        if(hello!=null && hello.moveToFirst()){
        }else {
            readLaterFragment.changeVisibility();

    }

    }

    //start activity
    private void startActivity(int pos){

        try {
            if(getNewsModel(pos).getSourceVal().isAllowed()){

                if(sharedPreferences.getBoolean(NewsListRecycleViewAdapter.DETAIL_VIEW_SETTING_KEY,false)){
                }else {
                    Intent intent=new Intent(context, ReadLaterDetailActivity.class);
                    intent.putExtra(EXTRA_ID,pos);
                    context.startActivity(intent);
                }
            }else {
                startChromeBrowser(pos);


            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }


    public int getItemViewType(int position) {
            cursor.moveToPosition(position);

        if(cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.COVER_Y_EMBED))!=null){
            if(cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.COVER_Y_EMBED)).contains(YOUTUBE_BASE_LINK)){
                return NEWS_LIST_ITEM_YOUTUBE;
            }else {
                return NEWS_LIST_ITEM_VIDEO;
            }
        }
        if(cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.COVER_AUDIO)) !=null) return NEWS_LIST_ITEM_AUDIO;
        if(cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.COVER_VIDEO))!=null)return NEWS_LIST_ITEM_VIDEO;
            if(cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.IMAGE))==null ||
        cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.IMAGE)).isEmpty())
                return NEWS_LIST_ITEM_NO_IMAGE_VIEW;

        if (imageLoading) return NEWS_LIST_ITEM_NORMAL_IMAGE_VIEW;else return NEWS_LIST_ITEM_NO_IMAGE_VIEW;

    }


    @Override
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
            case NEWS_LIST_ITEM_AUDIO:
                view=inflater.inflate(R.layout.news_list_item_audio,parent,false);
                return new NewsAudioViewHolder(view);
            case NEWS_LIST_ITEM_VIDEO:
                view=inflater.inflate(R.layout.news_list_item_video,parent,false);
                return new NewsVideoViewHolder(view);
            case NEWS_LIST_ITEM_YOUTUBE:
                view=inflater.inflate(R.layout.new_list_item_youtube_embed,parent,false);
                return new NewsYoutubeViewHolder(view);

        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final News news =getNewsModel(position);
        if(news ==null)return;
        switch (getItemViewType(position)) {
            case NEWS_LIST_ITEM_NO_IMAGE_VIEW:{
                NewsNoImageViewHolder noImageViewHolder = (NewsNoImageViewHolder) holder;

                if(isEn(news.getTitle().charAt(0))){
                    setRobotoRegular(noImageViewHolder.titleTextView);
                    setRobotoLight( noImageViewHolder.synopTextView);
                    setRobotoLight( noImageViewHolder.sourceTextView);
                    setRobotoLight(noImageViewHolder.dateTextView);
                }else{
                    setNyalaTypeFace(noImageViewHolder.titleTextView);
                    setNyalaTypeFace( noImageViewHolder.synopTextView);
                    setRobotoRegular( noImageViewHolder.sourceTextView);
                    setRobotoRegular(noImageViewHolder.dateTextView);

                }
                noImageViewHolder.saveImageView.setImageResource(R.drawable.ic_bookmark);
                noImageViewHolder.titleTextView.setText(news.getTitle().trim());
                if(news.getSynop()!=null)
                    noImageViewHolder.synopTextView.setText(news.getSynop().trim().replace("\u00a0", ""));
                noImageViewHolder.sourceTextView.setText(news.getSourceVal().getName());
                noImageViewHolder.sourceImageView.setImageURI(news.getSourceVal().getLogo());
                if(news.getDate()==null) noImageViewHolder.dateTextView.setText(getDateDiff(news.getTimestamp()));else noImageViewHolder.dateTextView.setText(news.getDate());

            }

            break;
            case NEWS_LIST_ITEM_NORMAL_IMAGE_VIEW:
                NewsNormalImageViewHolder normalImageView = (NewsNormalImageViewHolder) holder;
                if(isEn(news.getTitle().charAt(0))){
                    setRobotoRegular(normalImageView.titleTextView);
                    setRobotoLight( normalImageView.sourceTextView);
                    setRobotoLight(normalImageView.dateTextView);
                }else{
                    setNyalaTypeFace(normalImageView.titleTextView);
                    setRobotoRegular( normalImageView.sourceTextView);
                    setRobotoRegular(normalImageView.dateTextView);

                }
                normalImageView.newsImageView.setImageURI(news.getThumbnail().trim());
                normalImageView.titleTextView.setText(news.getTitle().trim());
                if(news.getSynop()==null){
                    normalImageView.synopTextView.setText(news.getTitle().trim().replace("\u00a0", ""));
                }else{
                    normalImageView.synopTextView.setText(news.getSynop().trim().replace("\u00a0", ""));
                }
                normalImageView.sourceTextView.setText(news.getSourceVal().getName());
                normalImageView.sourceImageView.setImageURI(news.getSourceVal().getLogo());
                normalImageView.saveImageView.setImageResource(R.drawable.ic_bookmark);
                if(news.getDate()==null) normalImageView.dateTextView.setText(getDateDiff(news.getTimestamp()));else normalImageView.dateTextView.setText(news.getDate());


                break;
            case NEWS_LIST_ITEM_AUDIO:
                final NewsAudioViewHolder newsAudioViewHolder = (NewsAudioViewHolder) holder;
                if(isEn(news.getTitle().charAt(0))){
                    setRobotoRegular(newsAudioViewHolder.titleTextView);
                    setRobotoLight( newsAudioViewHolder.sourceTextView);
                    setRobotoLight(newsAudioViewHolder.dateTextView);
                }else{
                    setNyalaTypeFace(newsAudioViewHolder.titleTextView);
                    setRobotoRegular( newsAudioViewHolder.sourceTextView);
                    setRobotoRegular(newsAudioViewHolder.dateTextView);
                }

                newsAudioViewHolder.titleTextView.setText(news.getTitle().trim());
                newsAudioViewHolder.sourceTextView.setText(news.getSourceVal().getName());
                newsAudioViewHolder.sourceImageView.setImageURI(news.getSourceVal().getLogo());
                if(news.getDate()==null) newsAudioViewHolder.dateTextView.setText(getDateDiff(news.getTimestamp()));else newsAudioViewHolder.dateTextView.setText(news.getDate());

                newsAudioViewHolder.saveImageView.setImageResource(R.drawable.ic_bookmark);

                if(news.getO_cover_a_prev()!=null)
                    newsAudioViewHolder.youTubeThumbnailView.setImageURI(news.getO_cover_a_prev());
                else if(news.getCover_image()!=null)
                    newsAudioViewHolder.youTubeThumbnailView.setImageURI(news.getCover_image());
                else if(news.getOriginal_image()!=null)
                    newsAudioViewHolder.youTubeThumbnailView.setImageURI(news.getOriginal_image());



                break;
            case NEWS_LIST_ITEM_VIDEO:{
                final NewsVideoViewHolder newsVideoViewHolder = (NewsVideoViewHolder) holder;
                if(isEn(news.getTitle().charAt(0))){
                    setRobotoRegular(newsVideoViewHolder.titleTextView);
                    setRobotoLight( newsVideoViewHolder.sourceTextView);
                    setRobotoLight(newsVideoViewHolder.dateTextView);
                }else{
                    setNyalaTypeFace(newsVideoViewHolder.titleTextView);
                    setRobotoRegular( newsVideoViewHolder.sourceTextView);
                    setRobotoRegular(newsVideoViewHolder.dateTextView);

                }


                newsVideoViewHolder.titleTextView.setText(news.getTitle().trim());
                newsVideoViewHolder.sourceTextView.setText(news.getSourceVal().getName());
                newsVideoViewHolder.sourceImageView.setImageURI(news.getSourceVal().getLogo());
                newsVideoViewHolder.saveImageView.setImageResource(R.drawable.ic_bookmark);

                if(news.getDate()==null) newsVideoViewHolder.dateTextView.setText(getDateDiff(news.getTimestamp()));else newsVideoViewHolder.dateTextView.setText(news.getDate());


                if(news.getO_cover_v_prev()!=null)
                    newsVideoViewHolder.youTubeThumbnailView.setImageURI(news.getO_cover_v_prev());
                else if(news.getCover_image()!=null)
                    newsVideoViewHolder.youTubeThumbnailView.setImageURI(news.getCover_image());
                else if(news.getOriginal_image()!=null)
                    newsVideoViewHolder.youTubeThumbnailView.setImageURI(news.getOriginal_image());





                break;
            }
            case NEWS_LIST_ITEM_YOUTUBE:{
                final NewsYoutubeViewHolder youtubeViewHolder = (NewsYoutubeViewHolder) holder;
                if(isEn(news.getTitle().charAt(0))){
                    setRobotoRegular(youtubeViewHolder.titleTextView);
                    setRobotoLight( youtubeViewHolder.sourceTextView);
                    setRobotoLight(youtubeViewHolder.dateTextView);
                }else{
                    setNyalaTypeFace(youtubeViewHolder.titleTextView);
                    setRobotoRegular( youtubeViewHolder.sourceTextView);
                    setRobotoRegular(youtubeViewHolder.dateTextView);

                }

                youtubeViewHolder.titleTextView.setText(news.getTitle().trim());
                youtubeViewHolder.sourceTextView.setText(news.getSourceVal().getName());
                youtubeViewHolder.sourceImageView.setImageURI(news.getSourceVal().getLogo());
                youtubeViewHolder.saveImageView.setImageResource(R.drawable.ic_bookmark);

                if(news.getDate()==null) youtubeViewHolder.dateTextView.setText(getDateDiff(news.getTimestamp()));else youtubeViewHolder.dateTextView.setText(news.getDate());

                String key= news.getCover_y_embed().replace(YOUTUBE_BASE_LINK,"");
                Uri imageUri = Uri.parse(YOUTUBE_IMAGE_URL).buildUpon()
                        .appendPath(key)
                        .appendPath("default.jpg")
                        .build();
                youtubeViewHolder.youTubeThumbnailView.setImageURI(imageUri);
                break;
            }

        }


    }


    @Override
    public int getItemCount() {
        return (cursor != null) ? cursor.getCount() : 0;
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
        public NewsYoutubeViewHolder(final View itemView) {
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
            setTypeFace(textViewList);

            textViewForSize.add(titleTextView);
            textViewForSize.add(synopTextView);
            setTexSize();
            playButton=itemView.findViewById(R.id.btnYoutube_player);
            relativeLayoutOverYouTubeThumbnailView = itemView.findViewById(R.id.relativeLayout_over_youtube_thumbnail);
            youTubeThumbnailView = itemView.findViewById(R.id.youtube_thumbnail_view);
            playButton.setOnClickListener(view -> {
                try {
                    News news =getNewsModel(getAdapterPosition());
                    if(news ==null)return;
                    if(news.getCover_y_embed()==null)return;
                    Intent intent = YouTubeStandalonePlayer.createVideoIntent((Activity) context, YOUTUBE_API_KEY, news.getCover_y_embed().replace("https://www.youtube.com/embed/",""));
                    context.startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }

            });
            youTubeThumbnailView.setOnClickListener(view -> {
                    News news =getNewsModel(getAdapterPosition());
                    if(news ==null)return;
                    if(news.getCover_y_embed()==null)return;
                    Intent intent = YouTubeStandalonePlayer.createVideoIntent((Activity) context, YOUTUBE_API_KEY, news.getCover_y_embed().replace("https://www.youtube.com/embed/",""));
                    context.startActivity(intent);

            });
            itemView.setOnClickListener(view -> startActivity(getAdapterPosition()));
            shareImageView.setOnClickListener(view -> shareNews(getAdapterPosition()));
            saveImageView.setOnClickListener(view -> removeNews(getAdapterPosition()));




        }
    }

    //view holder for news with not image view display
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
            titleTextView =itemView.findViewById(R.id.title_textView);
            synopTextView =itemView.findViewById(R.id.synop_textView);
            dateTextView =itemView.findViewById(R.id.date_textView);
            sourceTextView =itemView.findViewById(R.id.source_name_textView);
            sourceImageView =itemView.findViewById(R.id.source_imageView);
            shareImageView =itemView.findViewById(R.id.share_imageView);
            saveImageView = itemView.findViewById(R.id.save_imageView);

            //setting the type face
            List<TextView> textViewList = new ArrayList<>();
            textViewList.add(titleTextView);
            textViewList.add(synopTextView);
            textViewList.add(dateTextView);
            textViewList.add(sourceTextView);
            setTypeFace(textViewList);

            //setting the text size
            textViewForSize.add(titleTextView);
            textViewForSize.add(synopTextView);
            setTexSize();

            //go to the detail activity
            itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    startActivity(getAdapterPosition());}
            });

            //share the current news
            shareImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {shareNews(getAdapterPosition());
                }
            });

            //add news to read later

            saveImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeNews(getAdapterPosition());
                }
            });
        }
    }

    //view holder for news with image display
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
            setTypeFace(textViewList);

            textViewForSize.add(titleTextView);
            textViewForSize.add(synopTextView);
            setTexSize();

            itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    startActivity(getAdapterPosition());}
            });

            //share the current news
            shareImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {shareNews(getAdapterPosition());
                }
            });

            //add news to read later

            saveImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeNews(getAdapterPosition());
                }
            });
        }
    }

    //view holder for video news
    private class NewsVideoViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout relativeLayoutOverYouTubeThumbnailView;
        SimpleDraweeView youTubeThumbnailView;
        ImageView playButton;

        SimpleDraweeView newsImageView;
        SimpleDraweeView sourceImageView;
        TextView titleTextView;
        TextView dateTextView;
        TextView sourceTextView;
        ImageView shareImageView;
        ImageView saveImageView;

        public NewsVideoViewHolder(View itemView) {
            super(itemView);
            newsImageView = itemView.findViewById(R.id.news_imageView);
            titleTextView = itemView.findViewById(R.id.title_textView);
            dateTextView = itemView.findViewById(R.id.date_textView);
            sourceTextView = itemView.findViewById(R.id.source_name_textView);
            sourceImageView = itemView.findViewById(R.id.source_imageView);
            shareImageView = itemView.findViewById(R.id.share_imageView);
            saveImageView = itemView.findViewById(R.id.save_imageView);
            List<TextView> textViewList = new ArrayList<>();
            textViewList.add(titleTextView);
            textViewList.add(dateTextView);
            textViewList.add(sourceTextView);
            setTypeFace(textViewList);
            textViewForSize.add(titleTextView);
            setTexSize();

            playButton=itemView.findViewById(R.id.btnYoutube_player);
            relativeLayoutOverYouTubeThumbnailView = (RelativeLayout) itemView.findViewById(R.id.relativeLayout_over_youtube_thumbnail);
            youTubeThumbnailView = itemView.findViewById(R.id.youtube_thumbnail_view);



            itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    startActivity(getAdapterPosition());}
            });

            //share the current news
            shareImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {shareNews(getAdapterPosition());
                }
            });

            //add news to read later

            saveImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeNews(getAdapterPosition());
                }
            });


        }
    }

    //view holder for audio news
    private class NewsAudioViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout relativeLayoutOverYouTubeThumbnailView;
        SimpleDraweeView youTubeThumbnailView;
        ImageView playButton;

        SimpleDraweeView newsImageView;
        SimpleDraweeView sourceImageView;
        TextView titleTextView;
        TextView synopTextView;
        TextView dateTextView;
        TextView sourceTextView;
        ImageView shareImageView;
        ImageView saveImageView;

        public NewsAudioViewHolder(View itemView) {
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
            setTypeFace(textViewList);

            textViewForSize.add(titleTextView);
            textViewForSize.add(synopTextView);
            setTexSize(); playButton=itemView.findViewById(R.id.btnYoutube_player);
            relativeLayoutOverYouTubeThumbnailView = (RelativeLayout) itemView.findViewById(R.id.relativeLayout_over_youtube_thumbnail);
            youTubeThumbnailView = itemView.findViewById(R.id.youtube_thumbnail_view);




            itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    startActivity(getAdapterPosition());}
            });

            //share the current news
            shareImageView.setOnClickListener(view -> shareNews(getAdapterPosition()));

            //add news to read later

            saveImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeNews(getAdapterPosition());
                }
            });
        }
    }


    private long getId(int pos){
        cursor.moveToPosition(pos);
        return cursor.getLong(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS._ID));
    }

    private void startChromeBrowser(int pos) {
        cursor.moveToPosition(pos);
        String link = cursor.getString(cursor.getColumnIndex(DatabaseDescription.SAVED_NEWS.NEWS_LINK));
        ChromeBrowser.openUrl(context,link);
    }

    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    private void setTexSize() {
        for (TextView textView : textViewForSize) {
            if (textView.getId() == R.id.title_textView) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSizePreference.getFloat(MainActivity.PREF_KEY_TITLE,16));
            } else if (textView.getId() == R.id.synop_textView) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSizePreference.getFloat(MainActivity.PREF_KEY_SYNOP,16));
            }
        }
    }

    private void setTypeFace(List<TextView> textViewList) {
        typeface = Typeface.createFromAsset(context.getAssets(), "NYALA.TTF");
        for (TextView textView : textViewList) {
            textView.setTypeface(typeface);
        }
    }

    private SharedPreferences.OnSharedPreferenceChangeListener textSizeChangeLatiner = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                setTexSize();
        }
        };

    private String getDateDiff(long timeStamp){
        long deff=System.currentTimeMillis()-timeStamp;
        long days = TimeUnit.MILLISECONDS.toDays(deff);
        if(days<1){
            long hours= TimeUnit.MILLISECONDS.toHours(deff);

            if(hours<1){
                long minutes= TimeUnit.MILLISECONDS.toMinutes(deff);
                return minutes + " minutes ago";
            }else {
                return hours + " hours ago";
            }
        }else {
            return days + " days ago";
        }
    }
    public void setRobotoLight(TextView textView) {
        textView.setTypeface(robotoLight);
    }
    private void setNyalaTypeFace(TextView textView){
        textView.setTypeface(nyalaTypeFace);
    }

    private void setRobotoRegular(TextView textView){
        textView.setTypeface(robotoRegular);

    }

    public boolean isEn(char c){
        int val=(int)c;
        return val >= 0&& val <= 200;
    }
}
