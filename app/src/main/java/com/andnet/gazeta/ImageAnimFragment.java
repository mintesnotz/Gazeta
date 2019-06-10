package com.andnet.gazeta;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;

public class ImageAnimFragment extends Fragment{

    private String imageUri;


    public static final String IMAGE_ARGS="IMAGE";

    public static ImageAnimFragment getInstance(String imageUri){

        Bundle bundle=new Bundle();
        bundle.putString(IMAGE_ARGS,imageUri);
        ImageAnimFragment imageAnimFragment=new ImageAnimFragment();
        imageAnimFragment.setArguments(bundle);
        return imageAnimFragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments().getString(IMAGE_ARGS)!=null){

           imageUri=getArguments().getString(IMAGE_ARGS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SimpleDraweeView simpleDraweeView=new SimpleDraweeView(getContext());
        simpleDraweeView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,250));
        simpleDraweeView.setImageURI(imageUri);
        return simpleDraweeView;
    }
}
