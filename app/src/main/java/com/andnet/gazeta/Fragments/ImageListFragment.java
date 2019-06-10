package com.andnet.gazeta.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;

import com.andnet.gazeta.R;
import com.facebook.drawee.view.SimpleDraweeView;



/**
 * A simple {@link Fragment} subclass.
 */
public class ImageListFragment extends Fragment {

    public static final String PAGE_ARGS="page";
    public static final String PAGE_ARGS_POS="pos";
    public static final String PAGE_ARGS_TOTAL="total";

    private String link;
    private int pos;
    private int total;
    private ScaleGestureDetector scaleGestureDetector;


    public static ImageListFragment getInstance(String link,int pos,int total) {
        Bundle bundle = new Bundle();
        bundle.putString(PAGE_ARGS, link);
        bundle.putInt(PAGE_ARGS_POS, pos);
        bundle.putInt(PAGE_ARGS_TOTAL,total);
        ImageListFragment listFragment = new ImageListFragment();
        listFragment.setArguments(bundle);
        return listFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        link=bundle.getString(PAGE_ARGS);
        pos=bundle.getInt(PAGE_ARGS_POS);
        total=bundle.getInt(PAGE_ARGS_TOTAL);

    }

    public ImageListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_image_list, container, false);


        SimpleDraweeView simpleDraweeView=view.findViewById(R.id.simpleImageViewer);
        simpleDraweeView.setImageURI(link);
        scaleGestureDetector = new ScaleGestureDetector(
                getContext(), new MySimpleOnScaleGestureListener(simpleDraweeView));

        simpleDraweeView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                scaleGestureDetector.onTouchEvent(motionEvent);
                return true;
            }
        });




        return view;
    }

    private class MySimpleOnScaleGestureListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener{

        SimpleDraweeView viewMyImage;

        float factor;

        public MySimpleOnScaleGestureListener(SimpleDraweeView iv) {
            super();

            viewMyImage = iv;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            factor = 1.0f;
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            float scaleFactor = detector.getScaleFactor() - 1;
            factor += scaleFactor;
            viewMyImage.setScaleX(factor);
            viewMyImage.setScaleY(factor);
            return true;
        }
    }



}
