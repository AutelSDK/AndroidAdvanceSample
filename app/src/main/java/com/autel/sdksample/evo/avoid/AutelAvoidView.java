package com.autel.sdksample.evo.avoid;

import android.graphics.drawable.LevelListDrawable;
import android.widget.ImageView;

/**
 * Created by A15387 on 2017/7/18.
 */

public class AutelAvoidView {

    public static final int STATE_NULL = 0;
    public static final int STATE_ONE = 1;
    public static final int STATE_TWO = 2;
    public static final int STATE_THREE = 3;
    public static final int STATE_FOUR = 4;
    public static final int STATE_FIVE = 5;
    public static final int STATE_SIX = 6;
    public static final int STATE_SEVEN = 7;


    public AutelAvoidView(){

    }


    public void setAvoidImg(int state,ImageView img){
        LevelListDrawable levelListDrawable = (LevelListDrawable) img.getDrawable();
        levelListDrawable.setLevel(state);
    }

}
