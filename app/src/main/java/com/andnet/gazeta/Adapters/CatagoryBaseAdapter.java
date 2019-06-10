package com.andnet.gazeta.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andnet.gazeta.Models.Category;
import com.andnet.gazeta.R;

import java.util.ArrayList;


public class CatagoryBaseAdapter extends RecyclerView.Adapter<CatagoryBaseAdapter.CatViewHolder> {

    private ArrayList<Category> categoryList =new ArrayList<>();
    private Context context;

    private ArrayList<String> selectedCat=new ArrayList<>();


    public CatagoryBaseAdapter(Context context){
       this.context=context;
        insertDefulatSelectedCat();
    }

    private void insertDefulatSelectedCat() {
        selectedCat.add(context.getString(R.string.top_stories));
        selectedCat.add(context.getString(R.string.technology));
        selectedCat.add(context.getString(R.string.world));

    }

    public void setCategoryList(ArrayList<Category> categoryList) {
        this.categoryList = categoryList;
        notifyDataSetChanged();
    }


    @Override
    public CatagoryBaseAdapter.CatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View mainView=LayoutInflater.from(parent.getContext()).inflate(R.layout.catagory_list_item,parent,false);
        return new CatViewHolder(mainView);
    }

    @Override
    public void onBindViewHolder(CatagoryBaseAdapter.CatViewHolder holder, int position) {


        Category cat= categoryList.get(position);
        holder.catNameTextView.setText(cat.getName());
        holder.imageView.setImageResource(cat.getImage());
        holder.imageView.setBackgroundResource(cat.getBackDrawbale());
        holder.imageView.setPadding(20,20,20,20);

        if(selectedCat.contains(cat.getName())){
            holder.checkedView.setVisibility(View.VISIBLE);
        }else {
            holder.checkedView.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class CatViewHolder extends RecyclerView.ViewHolder {


        ImageView imageView;
        TextView catNameTextView;
        ImageView checkedView;

        public CatViewHolder(View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.cat_image_veiw);
            catNameTextView=itemView.findViewById(R.id.cat_text_view);
            checkedView=itemView.findViewById(R.id.checkedView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Category cat=categoryList.get(getAdapterPosition());

                    if(checkedView.getVisibility()==View.VISIBLE){
                        checkedView.setVisibility(View.GONE);
                        if(selectedCat.contains(cat.getName())){
                            selectedCat.remove(cat.getName());
                        }

                    }else {
                        checkedView.setVisibility(View.VISIBLE);
                        if(!selectedCat.contains(cat.getName())){
                            selectedCat.add(cat.getName());
                        }

                    }
                }
            });
        }
    }

    public ArrayList<String> getSelectedCat() {
        return selectedCat;
    }
}
