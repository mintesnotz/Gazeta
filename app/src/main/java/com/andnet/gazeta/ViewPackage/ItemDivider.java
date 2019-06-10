package com.andnet.gazeta.ViewPackage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;


public class ItemDivider extends RecyclerView.ItemDecoration {

    private final Drawable divider;

    public ItemDivider(Context context) {
        int[] attrs = {android.R.attr.listDivider};
        divider = context.obtainStyledAttributes(attrs).getDrawable(0);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        // for every item but the last, draw a line below it
        for (int i = 0; i < parent.getChildCount() - 1; ++i) {
            View item = parent.getChildAt(i); // get ith list item

            // calculate top/bottom y-coordinates for current divider
            int top = item.getBottom() + ((RecyclerView.LayoutParams)
                    item.getLayoutParams()).bottomMargin;
            int bottom = top + divider.getIntrinsicHeight();
            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
        }
    }
}