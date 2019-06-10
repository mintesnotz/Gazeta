package com.andnet.gazeta.CustomeFragments;

import com.google.android.youtube.player.YouTubePlayerSupportFragment;


public class VideoPlayerFragment extends YouTubePlayerSupportFragment {

    private static final VideoPlayerFragment ourInstance = new VideoPlayerFragment();

    public static VideoPlayerFragment getInstance() {
        return ourInstance;
    }

    public VideoPlayerFragment() {
    }

}
