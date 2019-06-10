package com.andnet.gazeta.Adapters;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andnet.gazeta.LibDetailActivity;
import com.andnet.gazeta.Models.Library;
import com.andnet.gazeta.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.LibraryViewHolder>{


    private Context context;
    private List<Library> libraryList = new ArrayList<>();
    public static final String POSITION ="position";
    private Typeface robotoCondensed;

    public LibraryAdapter(Context context) {
        this.context=context;
        robotoCondensed=Typeface.createFromAsset(context.getResources().getAssets(),"Roboto-Regular.ttf");
    }


    @Override
    public LibraryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View  itemView= LayoutInflater.from(context).inflate(R.layout.lib_layout,null);
        return new LibraryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LibraryViewHolder holder, int i) {
        Library library=libraryList.get(i);
        holder.libTextView.setText(library.getName());
        holder.libBackImage.setImageURI(library.getImage());

    }

    @Override
    public int getItemCount() {
        return libraryList.size();
    }

    public class LibraryViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView libBackImage;
        TextView libTextView;

        public LibraryViewHolder(View itemView) {
            super(itemView);
            libBackImage=itemView.findViewById(R.id.lib_back_image);
            libTextView=itemView.findViewById(R.id.lib_text_view);
            libTextView.setTypeface(robotoCondensed);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                     Intent intent=new Intent(context,LibDetailActivity.class);
                     intent.putExtra(LibDetailActivity.LIBRARY_CONST,libraryList.get(getAdapterPosition()));
                     context.startActivity(intent);
                }
            });

        }
    }

    public void setLibraryList(List<Library> libraryList) {
        this.libraryList = libraryList;
    }
}
