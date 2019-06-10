package com.andnet.gazeta.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.RadioGroup;

import com.andnet.gazeta.MainActivity;
import com.andnet.gazeta.R;

import static android.content.Context.MODE_PRIVATE;


public class TextSizeChooserFragment extends DialogFragment {

    private SharedPreferences textSizePrefrence;
    private SharedPreferences.Editor texTizeEditor;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.text_size_change_seek_bar, null);
        builder.setView(view);
        builder.setTitle(R.string.text_size);
        textSizePrefrence=getActivity().getSharedPreferences(MainActivity.TEXT_SIZE_FILE_NAME, MODE_PRIVATE);
        RadioGroup radioGroup=view.findViewById(R.id.textSizeRadioGroup);

        float value=textSizePrefrence.getFloat(MainActivity.PREF_KEY_TITLE,14);

        if(value==getActivity().getResources().getDimension(R.dimen.title_text_size_small)){
            radioGroup.check(R.id.small_text_view);
        }else if(value==getActivity().getResources().getDimension(R.dimen.title_text_size_med)){
            radioGroup.check(R.id.meidum_text_view);
        }else if(value==getActivity().getResources().getDimension(R.dimen.title_text_size_large)){
            radioGroup.check(R.id.large_text_view);
        }else if(value==getActivity().getResources().getDimension(R.dimen.title_text_size_extra_large)){
            radioGroup.check(R.id.extra_large_text_view);
        }else if(value==getActivity().getResources().getDimension(R.dimen.title_text_size_tiny)){
            radioGroup.check(R.id.tiny_text_view);
        }


        radioGroup.setOnCheckedChangeListener((radioGroup1, i) -> {
            switch (i){
                case R.id.small_text_view:
                    setSmallTextSize();
                    break;
                case R.id.meidum_text_view:
                    setMediaTextSize();
                    break;
                case R.id.large_text_view:
                    setLargeTextSize();
                    break;
                 case R.id.extra_large_text_view:
                     setExtraLargeTextSize();
                     break;
                case R.id.tiny_text_view:
                    setTintyTextViewSize();
                    break;
            }

        });
        builder.setNegativeButton(R.string.cancel, (dialogInterface, k) -> {
        });
        builder.setPositiveButton(R.string.ok, (dialogInterface, i) -> dialogInterface.cancel());
        return builder.create();
    }

    private void setSmallTextSize() {
        texTizeEditor=textSizePrefrence.edit();
        texTizeEditor.putFloat(MainActivity.PREF_KEY_TITLE,getActivity().getResources().getDimension(R.dimen.title_text_size_small));
        texTizeEditor.putFloat(MainActivity.PREF_KEY_SYNOP,getActivity().getResources().getDimension(R.dimen.synop_text_size_small));
        texTizeEditor.putFloat(MainActivity.PREF_BODY_TITLE,getActivity().getResources().getDimension(R.dimen.body_title_text_size_small));
        texTizeEditor.putFloat(MainActivity.PREF_BODY_TEXT,getActivity().getResources().getDimension(R.dimen.body_text_text_size_small));
        texTizeEditor.putFloat(MainActivity.PREF_AUTHOR_DATE,getActivity().getResources().getDimension(R.dimen.author_date_text_size_small));
        texTizeEditor.apply();
    }

    private void setMediaTextSize(){
        texTizeEditor=textSizePrefrence.edit();
        texTizeEditor.putFloat(MainActivity.PREF_KEY_TITLE,getActivity().getResources().getDimension(R.dimen.title_text_size_med));
        texTizeEditor.putFloat(MainActivity.PREF_KEY_SYNOP,getActivity().getResources().getDimension(R.dimen.synop_text_size_med));
        texTizeEditor.putFloat(MainActivity.PREF_BODY_TITLE,getActivity().getResources().getDimension(R.dimen.body_title_text_size_med));
        texTizeEditor.putFloat(MainActivity.PREF_BODY_TEXT,getActivity().getResources().getDimension(R.dimen.body_text_text_size_med));
        texTizeEditor.putFloat(MainActivity.PREF_AUTHOR_DATE,getActivity().getResources().getDimension(R.dimen.author_date_text_size_med));
        texTizeEditor.apply();

    }

    private void setLargeTextSize(){
        texTizeEditor=textSizePrefrence.edit();
        texTizeEditor.putFloat(MainActivity.PREF_KEY_TITLE,getActivity().getResources().getDimension(R.dimen.title_text_size_large));
        texTizeEditor.putFloat(MainActivity.PREF_KEY_SYNOP,getActivity().getResources().getDimension(R.dimen.synop_text_size_large));
        texTizeEditor.putFloat(MainActivity.PREF_BODY_TITLE,getActivity().getResources().getDimension(R.dimen.body_title_text_size_large));
        texTizeEditor.putFloat(MainActivity.PREF_BODY_TEXT,getActivity().getResources().getDimension(R.dimen.body_text_text_size_large));
        texTizeEditor.putFloat(MainActivity.PREF_AUTHOR_DATE,getActivity().getResources().getDimension(R.dimen.author_date_text_size_large));
        texTizeEditor.apply();

    }

    private void setExtraLargeTextSize(){
        texTizeEditor=textSizePrefrence.edit();
        texTizeEditor.putFloat(MainActivity.PREF_KEY_TITLE,getActivity().getResources().getDimension(R.dimen.title_text_size_extra_large));
        texTizeEditor.putFloat(MainActivity.PREF_KEY_SYNOP,getActivity().getResources().getDimension(R.dimen.synop_text_size_extra_large));
        texTizeEditor.putFloat(MainActivity.PREF_BODY_TITLE,getActivity().getResources().getDimension(R.dimen.body_title_text_size_extra_large));
        texTizeEditor.putFloat(MainActivity.PREF_BODY_TEXT,getActivity().getResources().getDimension(R.dimen.body_text_text_size_extra_large));
        texTizeEditor.putFloat(MainActivity.PREF_AUTHOR_DATE,getActivity().getResources().getDimension(R.dimen.author_date_text_size_extra_large));
        texTizeEditor.apply();

    }

    private void setTintyTextViewSize(){

        texTizeEditor=textSizePrefrence.edit();
        texTizeEditor.putFloat(MainActivity.PREF_KEY_TITLE,getActivity().getResources().getDimension(R.dimen.title_text_size_tiny));
        texTizeEditor.putFloat(MainActivity.PREF_KEY_SYNOP,getActivity().getResources().getDimension(R.dimen.synop_text_size_tiny));
        texTizeEditor.putFloat(MainActivity.PREF_BODY_TITLE,getActivity().getResources().getDimension(R.dimen.body_title_text_size_tiny));
        texTizeEditor.putFloat(MainActivity.PREF_BODY_TEXT,getActivity().getResources().getDimension(R.dimen.body_text_text_size_tiny));
        texTizeEditor.putFloat(MainActivity.PREF_AUTHOR_DATE,getActivity().getResources().getDimension(R.dimen.author_date_text_size_tiny));
        texTizeEditor.apply();

    }
}
