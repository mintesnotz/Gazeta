package com.andnet.gazeta.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andnet.gazeta.ChromeBrowser;
import com.andnet.gazeta.Models.News;
import com.andnet.gazeta.NewsDetailActivity;
import com.andnet.gazeta.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;


public class RelatedAdapter extends RecyclerView.Adapter<RelatedAdapter.RelatedViewHolder> {

    private Context context;
    private SharedPreferences sharedPreferences;


    private List<News> newsList = new ArrayList<>();

    public RelatedAdapter(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

    }

    public void setNewsList(List<News> newsList) {
        this.newsList = newsList;
    }

    public void add(News news) {
        newsList.add(news);
        notifyDataSetChanged();
    }

    @Override
    public RelatedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_model, parent, false);
        return new RelatedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RelatedViewHolder holder, int position) {
        RelatedViewHolder relatedViewHolder = (RelatedViewHolder) holder;
        News news = newsList.get(position);
        if (news == null) return;
        if (news.getThumbnail() != null)
            relatedViewHolder.newsImgeView.setImageURI(news.getThumbnail());
        else
            relatedViewHolder.newsImgeView.setImageURI(news.getSourceVal().getLogo());
        relatedViewHolder.titleTextView.setText(news.getTitle());
        relatedViewHolder.dateTextView.setText(news.getDate());
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public class RelatedViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView newsImgeView;
        TextView titleTextView;
        TextView dateTextView;

        public RelatedViewHolder(View itemView) {
            super(itemView);
            newsImgeView = itemView.findViewById(R.id.news_imageView);
            titleTextView = itemView.findViewById(R.id.title_textView);
            dateTextView = itemView.findViewById(R.id.date_textView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    goToDetailActivity(getAdapterPosition());
                }
            });


        }
    }

    private void goToDetailActivity(int pos) {
        if (sharedPreferences.getBoolean(NewsListRecycleViewAdapter.DETAIL_VIEW_SETTING_KEY, false)) {
            startChromeBrowser(pos);
        } else {
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add(newsList.get(pos).getKey());
            Intent intent = new Intent(context, NewsDetailActivity.class);
            intent.putExtra(ReadLaterRvAdapter.EXTRA_ID, pos);
            intent.putExtra(NewsListRecycleViewAdapter.KEY_LIST, arrayList);
            intent.putExtra(NewsListRecycleViewAdapter.PAGE_TITLE, newsList.get(pos).getSourceVal().getName());
            context.startActivity(intent);
        }
    }

    private void startChromeBrowser(int pos) {
        String link = newsList.get(pos).getLink();
        ChromeBrowser.openUrl(context, link);
    }
}