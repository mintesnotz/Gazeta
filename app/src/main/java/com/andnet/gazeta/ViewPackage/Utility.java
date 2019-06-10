package com.andnet.gazeta.ViewPackage;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;

public class Utility {
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }

    public static class columnQty {
        private int width, height, remaining;
        private DisplayMetrics displayMetrics;

        public columnQty(Context context, int viewId) {

            View view = View.inflate(context, viewId, null);
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            width = view.getMeasuredWidth();
            height = view.getMeasuredHeight();
            displayMetrics = context.getResources().getDisplayMetrics();
        }
        public int calculateNoOfColumns() {

            int numberOfColumns = displayMetrics.widthPixels / width;
            remaining = displayMetrics.widthPixels - (numberOfColumns * width);
//        System.out.println("\nRemaining\t" + remaining + "\nNumber Of Columns\t" + numberOfColumns);
            if (remaining / (2 * numberOfColumns) < 15) {
                numberOfColumns--;
                remaining = displayMetrics.widthPixels - (numberOfColumns * width);
            }
            return numberOfColumns;
        }
        public int calculateSpacing() {

            int numberOfColumns = calculateNoOfColumns();
//        System.out.println("\nNumber Of Columns\t"+ numberOfColumns+"\nRemaining Space\t"+remaining+"\nSpacing\t"+remaining/(2*numberOfColumns)+"\nWidth\t"+width+"\nHeight\t"+height+"\nDisplay DPI\t"+displayMetrics.densityDpi+"\nDisplay Metrics Width\t"+displayMetrics.widthPixels);
            return remaining / (2 * numberOfColumns);
        }
    }
}