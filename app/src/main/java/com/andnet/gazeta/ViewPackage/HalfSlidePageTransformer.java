package com.andnet.gazeta.ViewPackage;

import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

public class HalfSlidePageTransformer implements PageTransformer {
    private final ViewPager viewPager;
    private final View pagerDropShadow;

    public HalfSlidePageTransformer(ViewPager viewPager, View view) {
        this.viewPager = viewPager;
        this.pagerDropShadow = view;
    }

    public boolean isRtl() {
        if (Build.VERSION.SDK_INT < 18 || viewPager.getLayoutDirection() != View.LAYOUT_DIRECTION_RTL) {
            return false;
        }
        return true;
    }

    public void transformPage(View view, float f) {
        if (!(isRtl())) {
            int width = view.getWidth();
            if (f < -1.0f) {
                return;
            }
            if (f <= 0.0f) {
                view.setTranslationX(0.0f);
            } else if (f > 1.0f) {
            } else {
                if (f == 1.0f) {
                    view.setTranslationX(0.0f);
                    this.pagerDropShadow.setTranslationX((float) width);
                    return;
                }
                view.setTranslationX(0.5f * (((float) width) * (-f)));
                if (this.pagerDropShadow.getVisibility() != View.VISIBLE) {
                    this.pagerDropShadow.setVisibility(View.VISIBLE);
                }
                float f2 = (((float) width) - ((1.0f - f) * ((float) width))) - 1.0f;
                if (f2 == 0.0f) {
                    f2 = (float) width;
                }
                this.pagerDropShadow.setTranslationX(f2);
                this.pagerDropShadow.getBackground().setAlpha(Math.round(255.0f * f));
            }
        }
    }
}
