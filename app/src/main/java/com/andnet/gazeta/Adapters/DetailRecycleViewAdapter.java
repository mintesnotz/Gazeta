package com.andnet.gazeta.Adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andnet.gazeta.Models.DetailModel;
import com.andnet.gazeta.NewsDetailActivity;
import com.andnet.gazeta.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;
import java.util.List;


import cn.jzvd.JZVideoPlayerStandard;

public class DetailRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    //fonts
    private Typeface nyalaTypeFace;
    private Typeface geezMahtemUnicodeTypeFace;
    private Typeface robotoLight;
    private Typeface robotoRegular;

    private SharedPreferences bodyTextSizePref;

    //view types
    public static final int VIEW_TYPE_TEXT=1;
    public static final int VIEW_TYPE_IMAGE=2;
    public static final int VIEW_TYPE_VIDEO=3;
    public static final int VIEW_TYPE_AUDIO=4;
    public static final int VIEW_TYPE_LINE=5;
    public static final int VIEW_TYPE_AUTHOR_DATE=6;
    public static final int VIEW_TYPE_SOURCE_NAME=7;
    public static final int VIEW_TYPE_SOURCE_IMAGE=8;
    public static final int VIEW_TYPE_TITLE=9;
    public static final int VIEW_TYPE_IMAGE_DESC=10;
    public static final int VIEW_TYPE_FIRST_IMAGE_LAYOUT=11;
    public static final int VIEW_TYPE_HEADER_LAYOUT=12;
    public static final int VIEW_TYPE_YOUTUBE_VIDEO=13;
    public static final int VIEW_TYPE_WEB_VIEW=14;


    //list of detial model
    private List<DetailModel> detailModelList =new ArrayList<>();
    private Context context;
    private List<TextView> textViewList=new ArrayList<>();

    public DetailRecycleViewAdapter(Context context) {
        this.context = context;
        nyalaTypeFace = Typeface.createFromAsset(context.getAssets(), "NYALA.TTF");
        geezMahtemUnicodeTypeFace = Typeface.createFromAsset(context.getAssets(), "MYRIADAT.TTF");
        robotoLight = Typeface.createFromAsset(context.getAssets(), "MYRIADAM.TTF");
        robotoRegular = Typeface.createFromAsset(context.getAssets(), "MYRIADAM.TTF");
        bodyTextSizePref=context.getSharedPreferences(NewsDetailActivity.BODY_TEXT_SIZE_PREF_NAME,Context.MODE_PRIVATE);
        bodyTextSizePref.registerOnSharedPreferenceChangeListener(onsharedPrefListner);

    }

    public void setDetailModelList(List<DetailModel> detailModelList) {
        this.detailModelList = detailModelList;
    }

    private SharedPreferences.OnSharedPreferenceChangeListener onsharedPrefListner=new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            setTexSize();
        }
    };

    private void setTexSize() throws ArrayIndexOutOfBoundsException{
        for (TextView textView : textViewList)
            if (textView.getId() == R.id.title_textView) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, bodyTextSizePref.getFloat(NewsDetailActivity.BODY_TITLE_KEY_PREF,NewsDetailActivity.DEFAULT_BODY_TITLE_TEXT_SIZE));
            } else if (textView.getId() == R.id.paragraph_text) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, bodyTextSizePref.getFloat(NewsDetailActivity.BODY_PARA_KEY_PREF,NewsDetailActivity.DEFAULT_BODY_PARA_TEXT_SIZE));
            }else  if(textView.getId()==R.id.author_date_text_view){
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, bodyTextSizePref.getFloat(NewsDetailActivity.BODY_SOURCE_AUTHOR_PREF,NewsDetailActivity.DEFAULT_BODY_SOURCE_AUTHOR_TEXT_SIZE));
            }else  if(textView.getId()==R.id.source_name_textView){
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, bodyTextSizePref.getFloat(NewsDetailActivity.BODY_SOURCE_NAME_PREF,NewsDetailActivity.DEFAULT_BODY_SOURCE_NAME_TEXT_SIZE));

            }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view;

        switch (viewType){
            case VIEW_TYPE_TEXT:
                view=inflater.inflate(R.layout.z_text_list_item,parent,false);
                return new TextHolder(view);
            case VIEW_TYPE_IMAGE:
                view=inflater.inflate(R.layout.z_image_list_item,parent,false);
                return new ImageHolder(view);
            case VIEW_TYPE_VIDEO:
                view=inflater.inflate(R.layout.z_video_list_item,parent,false);
                return new VideoHolder(view);
            case VIEW_TYPE_AUDIO:
                view=inflater.inflate(R.layout.z_audio_list_item,parent,false);
                return new AudioHolder(view);
            case VIEW_TYPE_LINE:
                view=inflater.inflate(R.layout.z_line_list_item,parent,false);
                return new LineHolder(view);
            case VIEW_TYPE_AUTHOR_DATE:
                view=inflater.inflate(R.layout.z_author_date_list_item,parent,false);
                return new AuthorDateHolder(view);
            case VIEW_TYPE_SOURCE_NAME:
                view=inflater.inflate(R.layout.z_source_name_list_item,parent,false);
                return new SourceNameHolder(view);
            case VIEW_TYPE_SOURCE_IMAGE:
                view=inflater.inflate(R.layout.z_source_image_list_item,parent,false);
                return new SourceImageHolder(view);
            case VIEW_TYPE_TITLE:
                view=inflater.inflate(R.layout.z_title_list_item,parent,false);
                return new TitleViewHolder(view);
            case VIEW_TYPE_IMAGE_DESC:
                view=inflater.inflate(R.layout.z_image_desc_text_view,parent,false);
                return new ImageDescViewHolder(view);
            case VIEW_TYPE_FIRST_IMAGE_LAYOUT:
                view=inflater.inflate(R.layout.z_first_image_item,parent,false);
                return new FirstImageViewHolder(view);
            case VIEW_TYPE_HEADER_LAYOUT:
                view=inflater.inflate(R.layout.z_header_layout,parent,false);
                return new HeaderViewHolder(view);
            case VIEW_TYPE_YOUTUBE_VIDEO:
                view=inflater.inflate(R.layout.z_video_list_item,parent,false);
                return new YoutubeViewHolder(view);
            case VIEW_TYPE_WEB_VIEW:
                view=inflater.inflate(R.layout.z_web_view_layout,parent,false);
                return new WebViewHolder(view);


        }
        return null;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DetailModel detailModel = detailModelList.get(position);
        switch (getItemViewType(position)) {
            case VIEW_TYPE_TEXT:{
                TextHolder textHolder=(TextHolder)holder;
                String body=detailModel.getValue();
                textHolder.textView.setMovementMethod(LinkMovementMethod.getInstance());

               if(body.isEmpty())return;
                if(isEn(body.charAt(1))){
                    setRobotoRegular(textHolder.textView);
                }else{
                    setNyalaTypeFace(textHolder.textView);
                }
                SpannableStringBuilder spannableStringBuilder=style(body);
                if(spannableStringBuilder==null){
                    textHolder.textView.setText(body);
                }else {
                    textHolder.textView.setText(spannableStringBuilder);
                }
                break;
            }
            case VIEW_TYPE_IMAGE:
                ImageHolder imageHolder=(ImageHolder) holder;
                imageHolder.simpleDraweeView.setImageURI(detailModel.getValue());
                break;
            case VIEW_TYPE_VIDEO:
                VideoHolder videoHolder=(VideoHolder) holder;
                if(detailModel.getAudio_cover_image()!=null){
                    videoHolder.youTubeThumbnailView.setImageURI(detailModel.getAudio_cover_image());
                }
                break;
            case VIEW_TYPE_AUDIO:
                AudioHolder audioHolder=(AudioHolder) holder;
                if(detailModel.getAudio_cover_image()!=null){
                   audioHolder.youTubeThumbnailView.setImageURI(detailModel.getAudio_cover_image());
                }
                break;
            case VIEW_TYPE_AUTHOR_DATE:
                AuthorDateHolder dateHolder=(AuthorDateHolder) holder;
                dateHolder.textView.setText(detailModel.getValue());
                if(isEn(detailModel.getValue().charAt(0))){
                    setRobotoLight(dateHolder.textView);
                }else{
                    setNyalaTypeFace(dateHolder.textView);
                }
                break;
            case VIEW_TYPE_SOURCE_NAME:
                SourceNameHolder sourceNameHolder=(SourceNameHolder) holder;
                sourceNameHolder.sourceName.setText(detailModel.getValue());
                if(isEn(detailModel.getValue().charAt(0))){
                    setRobotoLight(  sourceNameHolder.sourceName);
                }else{
                    setNyalaTypeFace(sourceNameHolder.sourceName);
                }
                break;
            case VIEW_TYPE_SOURCE_IMAGE:
                SourceImageHolder sourceImageHolder=(SourceImageHolder) holder;
                sourceImageHolder.simpleDraweeView.setImageURI(detailModel.getValue());
                break;
            case VIEW_TYPE_TITLE:
                TitleViewHolder titleViewHOlder=(TitleViewHolder) holder;
                titleViewHOlder.textView.setText(detailModel.getValue());
                break;
            case VIEW_TYPE_IMAGE_DESC:
                ImageDescViewHolder imageDescViewHolder=(ImageDescViewHolder) holder;
                SpannableStringBuilder spannableStringBuilder=style(detailModel.getValue());
                if(detailModel.getValue().length()<=2){
                    return;
                }
                if(isEn(detailModel.getValue().charAt(1))){
                    setRobotoRegular(imageDescViewHolder.imageDescTextView);
                }else {
                    setNyalaTypeFace(imageDescViewHolder.imageDescTextView);
                }
                if(spannableStringBuilder==null){
                    imageDescViewHolder.imageDescTextView.setText(detailModel.getValue());
                }else {
                    imageDescViewHolder.imageDescTextView.setText(spannableStringBuilder);
                }
                break;
            case VIEW_TYPE_FIRST_IMAGE_LAYOUT:
                FirstImageViewHolder firstImageViewHolder=(FirstImageViewHolder) holder;
                firstImageViewHolder.simpleDraweeView.setImageURI(detailModel.getValue());
                break;
            case VIEW_TYPE_HEADER_LAYOUT:
                HeaderViewHolder headerViewHolder=(HeaderViewHolder) holder;
                headerViewHolder.headerTextView.setText(detailModel.getValue());
                break;
            case VIEW_TYPE_YOUTUBE_VIDEO:
                 YoutubeViewHolder youtubeViewHolder=(YoutubeViewHolder) holder;
                String key="";
                 if(detailModel.getValue().contains(NewsListRecycleViewAdapter.YOUTUBE_BASE_LINK)){
                     key=detailModel.getValue().replace(NewsListRecycleViewAdapter.YOUTUBE_BASE_LINK,"").trim();
                 }else if(detailModel.getValue().contains("http://www.youtube.com/embed/")){
                     key=detailModel.getValue().replace("http://www.youtube.com/embed/","").trim();
                 }
                 if(key.contains("?")){
                     int pos=key.indexOf("?");
                     key=key.substring(0,pos);
                 }
                 Uri imageUri = Uri.parse(NewsListRecycleViewAdapter.YOUTUBE_IMAGE_URL).buildUpon()
                        .appendPath(key)
                        .appendPath("hqdefault.jpg")
                        .build();
                youtubeViewHolder.youTubeThumbnailView.setImageURI(imageUri);
                break;
            case VIEW_TYPE_WEB_VIEW:
                final WebViewHolder webViewHolder=(WebViewHolder)holder;
                webViewHolder.webView.loadUrl(detailModel.getValue());
                webViewHolder.webView.setWebViewClient(new WebViewClient(){
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        webViewHolder.progressBar.setVisibility(View.GONE);
                        webViewHolder.webView.setVisibility(View.VISIBLE);
                    }
                });
                break;
            case VIEW_TYPE_LINE:
                LineHolder lineHolder=(LineHolder)holder;
                String body=detailModel.getValue();
                if(body.isEmpty())return;
                if(isEn(body.charAt(1))){
                    setRobotoRegular(lineHolder.textView);
                }else{
                    setNyalaTypeFace(lineHolder.textView);
                }
                SpannableStringBuilder myspannableStringBuilder=style(body);
                if(myspannableStringBuilder==null){
                    lineHolder.textView.setText(body);
                }else {
                    lineHolder.textView.setText(myspannableStringBuilder);
                }
                break;

        }
    }

    @Override
    public int getItemViewType(int position) {
        return detailModelList.get(position).getType();


    }

    @Override
    public int getItemCount() {
        return detailModelList.size();
    }



    public class WebViewHolder extends RecyclerView.ViewHolder{

        WebView webView;
        ProgressBar progressBar;
        public WebViewHolder(View itemView) {
            super(itemView);
            webView=itemView.findViewById(R.id.webview);
            progressBar=itemView.findViewById(R.id.webViewProgressBar);
            webView.getSettings().setJavaScriptEnabled(true);
        }
    }

    public class YoutubeViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout relativeLayoutOverYouTubeThumbnailView;
        SimpleDraweeView youTubeThumbnailView;
        ImageView playButton;

        public YoutubeViewHolder(View itemView) {
            super(itemView);

            playButton=itemView.findViewById(R.id.btnYoutube_player);
            relativeLayoutOverYouTubeThumbnailView =itemView.findViewById(R.id.relativeLayout_over_youtube_thumbnail);
            youTubeThumbnailView = itemView.findViewById(R.id.youtube_thumbnail_view);

            playButton.setOnClickListener(view -> {

                String youtubeLink=detailModelList.get(getAdapterPosition()).getValue();

                if(youtubeLink.contains(NewsListRecycleViewAdapter.YOUTUBE_BASE_LINK)){
                    youtubeLink=youtubeLink.replace(NewsListRecycleViewAdapter.YOUTUBE_BASE_LINK,"");

                }else if(youtubeLink.contains("http://www.youtube.com/embed/")){
                    youtubeLink=youtubeLink.replace("http://www.youtube.com/embed/","");

                }
                Intent intent = YouTubeStandalonePlayer.createVideoIntent((Activity) context, NewsListRecycleViewAdapter.YOUTUBE_API_KEY,youtubeLink);
                context.startActivity(intent);
            });

            youTubeThumbnailView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String youtubeLink=detailModelList.get(getAdapterPosition()).getValue();

                    if(youtubeLink.contains(NewsListRecycleViewAdapter.YOUTUBE_BASE_LINK)){
                        youtubeLink=youtubeLink.replace(NewsListRecycleViewAdapter.YOUTUBE_BASE_LINK,"");

                    }else if(youtubeLink.contains("http://www.youtube.com/embed/")){
                        youtubeLink=youtubeLink.replace("http://www.youtube.com/embed/","");

                    }
                    Intent intent = YouTubeStandalonePlayer.createVideoIntent((Activity) context, NewsListRecycleViewAdapter.YOUTUBE_API_KEY,youtubeLink);
                    context.startActivity(intent);
                }
            });

        }


    }


    //header view holder
    public class HeaderViewHolder extends RecyclerView.ViewHolder{
        TextView headerTextView;
        public HeaderViewHolder(View itemView) {
            super(itemView);
            headerTextView=itemView.findViewById(R.id.header_text_view);
            textViewList.add(headerTextView);
        }
    }

    //detial video view holder
    public class VideoHolder extends RecyclerView.ViewHolder{

        RelativeLayout relativeLayoutOverYouTubeThumbnailView;
        SimpleDraweeView youTubeThumbnailView;
        ImageView playButton;
        JZVideoPlayerStandard videoPlayerSimple;


        public VideoHolder(View itemView) {
            super(itemView);

            playButton=itemView.findViewById(R.id.btnYoutube_player);
            relativeLayoutOverYouTubeThumbnailView =itemView.findViewById(R.id.relativeLayout_over_youtube_thumbnail);
            youTubeThumbnailView = itemView.findViewById(R.id.youtube_thumbnail_view);
            videoPlayerSimple=itemView.findViewById(R.id.videoplayer);


            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playButton.setVisibility(View.GONE);
                    youTubeThumbnailView.setVisibility(View.GONE);
                    videoPlayerSimple.setVisibility(View.VISIBLE);
                    DetailModel detailModel = detailModelList.get(getAdapterPosition());

                    videoPlayerSimple.setUp(detailModel.getValue()
                            , JZVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");

                    videoPlayerSimple.startButton.performClick();


                }
            });


        }
    }

    //audio holder
    public class AudioHolder extends RecyclerView.ViewHolder{
        RelativeLayout relativeLayoutOverYouTubeThumbnailView;
        SimpleDraweeView youTubeThumbnailView;
        ImageView playButton;
        JZVideoPlayerStandard videoPlayerSimple;


        public AudioHolder(View itemView) {
            super(itemView);

            playButton=itemView.findViewById(R.id.btnYoutube_player);
            relativeLayoutOverYouTubeThumbnailView =itemView.findViewById(R.id.relativeLayout_over_youtube_thumbnail);
            youTubeThumbnailView = itemView.findViewById(R.id.youtube_thumbnail_view);
            videoPlayerSimple=itemView.findViewById(R.id.videoplayer);


            playButton.setOnClickListener(v -> {
                playButton.setVisibility(View.GONE);
                youTubeThumbnailView.setVisibility(View.GONE);
                videoPlayerSimple.setVisibility(View.VISIBLE);
                DetailModel detailModel = detailModelList.get(getAdapterPosition());

                videoPlayerSimple.setUp(detailModel.getValue()
                        , JZVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");

                videoPlayerSimple.startButton.performClick();


            });


        }
    }

    //text holder
    public class TextHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public TextHolder(View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.paragraph_text);
            textViewList.add(textView);
            setTexSize();

        }
    }

    //image holder
    public class ImageHolder extends RecyclerView.ViewHolder{

        SimpleDraweeView simpleDraweeView;

        public ImageHolder(View itemView) {
            super(itemView);
            simpleDraweeView=itemView.findViewById(R.id.simpleDrawView);
            simpleDraweeView.setAspectRatio(1.33f);
            simpleDraweeView.setOnClickListener(view -> {
                ArrayList<String> imageList=new ArrayList<>();
                int myPOs=0;
                for(int i = 0; i< detailModelList.size(); i++){
                    if(detailModelList.get(i).getType()==VIEW_TYPE_IMAGE){
                        imageList.add(detailModelList.get(i).getValue());
                    }
                }
                for(int j=0;j<imageList.size();j++){
                    if(detailModelList.get(getAdapterPosition()).getValue().equals(imageList.get(j))){
                        myPOs=j;
                    }
                }

                for(int i = 0; i< detailModelList.size(); i++){
                    if(detailModelList.get(i).getType()==VIEW_TYPE_FIRST_IMAGE_LAYOUT){
                        imageList.add(0,detailModelList.get(i).getValue());
                        myPOs++;

                    }
                }


                new ImageViewer.Builder(context, imageList)
                        .setStartPosition(myPOs)
                        .hideStatusBar(true)
                        .show();



            });

        }
    }


    //line holder
    public class LineHolder extends RecyclerView.ViewHolder{
        TextView textView;

        public LineHolder(View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.paragraph_text);
            textViewList.add(textView);
            setTexSize();
        }
    }

    //author date holder
    public class AuthorDateHolder extends RecyclerView.ViewHolder{

        TextView textView;

        public AuthorDateHolder(View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.author_date_text_view);
            textViewList.add(textView);
            setTexSize();



        }
    }

    //source_Name_holder date holder
    public class SourceNameHolder extends RecyclerView.ViewHolder{

        TextView sourceName;

        public SourceNameHolder(View itemView) {
            super(itemView);
            sourceName=itemView.findViewById(R.id.source_name_textView);
            textViewList.add(sourceName);
            setTexSize();


        }
    }

    //source_Name_holder date holder
    public class SourceImageHolder extends RecyclerView.ViewHolder{

        SimpleDraweeView simpleDraweeView;

        public SourceImageHolder(View itemView) {
            super(itemView);
            simpleDraweeView=itemView.findViewById(R.id.source_imageView);

        }
    }

    //source_Name_holder date holder
    public class TitleViewHolder extends RecyclerView.ViewHolder{

        TextView textView;

        public TitleViewHolder(View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.title_textView);
            textViewList.add(textView);
            setTexSize();
            setGeezMahtemUnicodeTypeFace(textView);


        }
    }

    public class ImageDescViewHolder extends RecyclerView.ViewHolder{

        TextView imageDescTextView;
        public ImageDescViewHolder(View itemView) {
            super(itemView);
            imageDescTextView=itemView.findViewById(R.id.imageDesc);
        }
    }

    public class FirstImageViewHolder extends RecyclerView.ViewHolder{

        SimpleDraweeView simpleDraweeView;
        public FirstImageViewHolder(View itemView) {
            super(itemView);
            simpleDraweeView = itemView.findViewById(R.id.simpleDrawView);
            simpleDraweeView.setAspectRatio(1.5f);

            simpleDraweeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<String> imageList=new ArrayList<>();
                    int myPOs=0;
                    for(int i = 0; i< detailModelList.size(); i++){
                        if(detailModelList.get(i).getType()==VIEW_TYPE_IMAGE){
                            imageList.add(detailModelList.get(i).getValue());
                        }
                    }
                    for(int j=0;j<imageList.size();j++){
                        if(detailModelList.get(getAdapterPosition()).getValue().equals(imageList.get(j))){
                            myPOs=j;
                        }
                    }

                    for(int i = 0; i< detailModelList.size(); i++){
                        if(detailModelList.get(i).getType()==VIEW_TYPE_FIRST_IMAGE_LAYOUT){
                            imageList.add(0,detailModelList.get(i).getValue());

                        }
                    }


                    new ImageViewer.Builder(context, imageList)
                            .setStartPosition(myPOs)
                            .hideStatusBar(true)
                            .show();


                }
            });


        }
    }

    private void setNyalaTypeFace(TextView textView){
        textView.setTypeface(nyalaTypeFace);
    }

    private void setRobotoRegular(TextView textView){
        textView.setTypeface(robotoRegular);

    }

    public void setGeezMahtemUnicodeTypeFace(TextView textView) {
        textView.setTypeface(geezMahtemUnicodeTypeFace);
    }

    public boolean isEn(char c){
        int val=(int)c;
        return val >= 0&& val <= 120;
    }

    public void setRobotoLight(TextView textView) {
        textView.setTypeface(robotoLight);
    }

    private SpannableStringBuilder style(String text){
            String [] s = text.split("\\%(A|(EM)|B)");
            if(s.length==1) return null;
            SpannableStringBuilder span = new SpannableStringBuilder();
            String ends = "";
            try {
                int pos = 0;
                int add = 0;
                for (String i : s) {
                    int end = i.indexOf(";%");
                    if (end == -1) {
                        span.append(i);
                        span.setSpan(null, pos, i.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        pos += i.length();
                        continue;
                    }
                    char type = i.charAt(end - 1);
                    switch (type) {
                        case 'A':
                            int ls = i.indexOf("%");
                            String anchor = i.substring(0, ls);
                            boolean okay = span.charAt(span.length()-1) == ' ';

                            span.append((okay ? "":' ')+ anchor);
                            final URLSpan link = new URLSpan(i.substring(ls +1, end - 1));
                            //
                            int adjust = !okay ? 1: 0;
                            span.setSpan(link, pos + adjust+add, pos + anchor.length() + adjust+add, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            if(!okay) add++;
                            pos += anchor.length();
                            ends = i.substring(end + 2).replace(anchor,"") + " ";
                            break;
                        case 'M':
                            String emph = i.substring(1, end - 2);
                            span.append(emph);
                            span.setSpan(new StyleSpan(Typeface.ITALIC), pos, pos + emph.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            pos += emph.length();
                            ends = i.substring(end + 2).replace(emph,"") + " ";
                            break;
                        default:
                            if ("B".equals(type)) {
                                String sth = i.substring(1, end - 1);
                                span.append(sth);
                                span.setSpan(new StyleSpan(Typeface.BOLD), pos, pos + sth.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                pos += sth.length();
                                ends = i.substring(end + 2).replace(sth,"") + " ";
                            }
                    }

                    span.append(ends);
                    span.setSpan(null, pos, pos + ends.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    pos += ends.length();
                }
            }catch(Exception e){ return  null;}
            return span;
        }
    }


