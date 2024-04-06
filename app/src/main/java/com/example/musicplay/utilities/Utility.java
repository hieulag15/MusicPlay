package com.example.musicplay.utilities;

import android.text.TextUtils;
import android.widget.TextView;

public class Utility {
    public static void setScrollText(TextView tv) {
        tv.setSelected(true); // Set the TextView as selected to enable marquee
        tv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tv.setMarqueeRepeatLimit(-1); // Set the marquee repeat limit to infinite
        tv.setFocusable(true);
        tv.setFocusableInTouchMode(true);
    }
}
