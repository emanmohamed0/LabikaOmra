package com.app.emaneraky.omrati.constans;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomTextViewEnglish extends TextView {
    public CustomTextViewEnglish(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/Hacen-Saudi-Arabia.ttf");
        this.setTypeface(face);
    }
}
