package com.andnet.gazeta.Databases;


import android.content.ContentUris;
import android.net.Uri;

public class DatabaseDescription {

    public static final String LIBRARY_DATABASE="gazeta.db";
    public static final String AUTHORITY ="com.thegazeta.news.Databases";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String KEY_DATABASE="key.db";
    public static final String AUTHORITY_KEY ="com.thegazeta.news.Databases.key";
    private static final Uri KEY_CONTENT_URI = Uri.parse("content://" + AUTHORITY_KEY);


        public static class SAVED_NEWS{

        public static final String TABLE_NAME = "SAVED_NEWS_TABLE";

        public static final String TITLE="titile";
        public static final String SYNOP="synop";
        public static final String IMAGE="image";
        public static final String DATE="date";
        public static final String TIME_STAMP="times_tamp";
        public static final String COVER_IMAGE="cover_image";
        public static final String AUTHOR="author";
        public static final String SOURCE_NAME="source_name";
        public static final String SOURCE_LOGO="source_logo";
        public static final String SOURCE_LINK="source_link";
        public static final String NEWS_LINK="news_link";
        public static final String _ID="rowid";
        public static final String KEY="key";
        public static final String IS_ALLOWED="allowed";
        public static final String COVER_AUDIO="cover_audio";
        public static final String COVER_VIDEO="cover_video";
        public static final String O_COVER_PREVIEW="o_cover_prev";
        public static final String ORIGINAL_IMAGE="original_image";
        public static final String COVER_Y_EMBED="cover_y_embed";
        public static final String COVER_CAPTION="cover_caption";
        public static final String O_COVER_V_PREVIEW="o_cover_v_preview";
        public static final String O_COVER_A_PREVIEW="o_cover_a_preview";

        public static final Uri NEWS_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
        public static Uri buildContactUriForeId(long id) {
            return ContentUris.withAppendedId(NEWS_CONTENT_URI, id) ;
        }

    }
    public static class SEARCH_HISTORY{


        public static final String TABLE_NAME = "SEARCH_HISTORY_TABLE";

        public static final String SEARCH_TERM="search_word";
        public static final String _ID="rowid";
        public static final String TIME_STAMP="time_stamp";


        public static final Uri SEARCH_HISTORY_CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        public static Uri buildContactUriForeId(long id) {
            return ContentUris.withAppendedId(SEARCH_HISTORY_CONTENT_URI, id) ;
        }

    }
    public static class BODY_TABLE{


        public static final String TABLE_NAME = "BODY_TABLE";

        public static final String key="key";
        public static final String body="body";
        public static final String _ID="rowid";


        public static final Uri BODY_TABLE_CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        public static Uri buildContactUriForeId(long id) {
            return ContentUris.withAppendedId(BODY_TABLE_CONTENT_URI, id) ;
        }

    }
    public static class CACHED_KEY_TABLE {

        public static final String TABLE_NAME = "CACHED_KEY_TABLE";
        public static final String KEY="key";
        public static final String CAT="cat";
        public static final String _ID="rowid";

        public static final Uri BODY_TABLE_CONTENT_URI =
                KEY_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
        public static Uri buildContactUriForeId(long id) {
            return ContentUris.withAppendedId(BODY_TABLE_CONTENT_URI, id) ;
        }

    }

    public static class SOURCE_TABLE{

        //1 meanse banned
        //0 means not banned

        public static final String TABLE_NAME = "BANNED_SOURCE_TABLE";

        public static final String NAME="NAME";
        public static final String BANNED="BANNED";
        public static final String _ID="rowid";

        public static final Uri BODY_TABLE_CONTENT_URI =
                KEY_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
        public static Uri buildContactUriForeId(long id) {
            return ContentUris.withAppendedId(BODY_TABLE_CONTENT_URI, id) ;
        
        }


    }
}
