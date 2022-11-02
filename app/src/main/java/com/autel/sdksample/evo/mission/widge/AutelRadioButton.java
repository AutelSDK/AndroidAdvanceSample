package com.autel.sdksample.evo.mission.widge;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatRadioButton;

/**
 * Created by A17164 on 2017/11/29.
 */

public class AutelRadioButton extends AppCompatRadioButton {
    private Typeface bold_Typeface;

    public AutelRadioButton(Context context) {
        super(context);
        init(context);
    }

    public AutelRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AutelRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        AssetManager regular_manager = context.getAssets();
        setTypeface(bold_Typeface);
    }
}
