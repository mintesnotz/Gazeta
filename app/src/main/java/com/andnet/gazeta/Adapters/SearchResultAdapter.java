package com.andnet.gazeta.Adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andnet.gazeta.MainActivity;
import com.andnet.gazeta.Models.News;
import com.andnet.gazeta.NewsDetailActivity;
import com.andnet.gazeta.R;
import com.andnet.gazeta.WebVeiwActivity;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.andnet.gazeta.MainActivity.THEM_PREF_NAME;


public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder> {

    private Context context;
    private SharedPreferences sharedPreferences;


    private List<News> newsList =new ArrayList<>();

    public SearchResultAdapter(Context context){
        this.context=context;
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);

    }

    public void setNewsList(List<News> newsList) {
        this.newsList = newsList;
    }

    public void add(News news){
        newsList.add(news);
        notifyDataSetChanged();
    }

    @Override
    public SearchResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_model,parent,false);
        return new SearchResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchResultViewHolder holder, int position) {
        SearchResultViewHolder searchResultViewHolder=(SearchResultViewHolder)holder;
        News news = newsList.get(position);
        if(news ==null)return;
            if(news.getThumbnail()!=null)searchResultViewHolder.newsImgeView.setImageURI(news.getThumbnail());
        else
            searchResultViewHolder.newsImgeView.setImageURI(news.getSourceVal().getLogo());
        searchResultViewHolder.titleTextView.setText(news.getTitle());
        searchResultViewHolder.dateTextView.setText(news.getDate());
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public class SearchResultViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView newsImgeView;
        TextView titleTextView;
        TextView dateTextView;


        public SearchResultViewHolder(View itemView) {
            super(itemView);
            newsImgeView=itemView.findViewById(R.id.news_imageView);
            titleTextView=itemView.findViewById(R.id.title_textView);
            dateTextView=itemView.findViewById(R.id.date_textView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    goToDetailActivity(getAdapterPosition());
                }
            });

        }
    }
    private void goToDetailActivity(int pos){
        if(sharedPreferences.getBoolean(NewsListRecycleViewAdapter.DETAIL_VIEW_SETTING_KEY,false)){
            startChromeBrowser(pos);}
        else {
            ArrayList<String> arrayList=new ArrayList<>();
            arrayList.add(newsList.get(pos).getKey());
            Intent intent=new Intent(context, NewsDetailActivity.class);
            intent.putExtra(ReadLaterRvAdapter.EXTRA_ID,pos);
            intent.putExtra(NewsListRecycleViewAdapter.KEY_LIST,arrayList);
            intent.putExtra(NewsListRecycleViewAdapter.PAGE_TITLE, newsList.get(pos).getSourceVal().getName());
            context.startActivity(intent);
        }
    }

    private void startChromeBrowser(int pos){
        SharedPreferences themePreference=context.getSharedPreferences(THEM_PREF_NAME, MODE_PRIVATE);
        int color=themePreference.getInt(MainActivity.THEME_PREF_KEY,context.getResources().getColor(R.color.colorPrimary));
        String link= newsList.get(pos).getLink();
        try {
            Uri uri = Uri.parse(link);
            CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
            intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            intentBuilder.setExitAnimations(context, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            CustomTabsIntent customTabsIntent = intentBuilder.build();
            customTabsIntent.intent.setPackage("com.android.chrome");
            intentBuilder.setToolbarColor(color);
            intentBuilder.setShowTitle(true);
            intentBuilder.addDefaultShareMenuItem();
            customTabsIntent.launchUrl(context, uri);
        }catch (ActivityNotFoundException e){
            Intent intent=new Intent(context,WebVeiwActivity.class);
            intent.putExtra("LINK",link);
            context.startActivity(intent);
        }

    }


}
