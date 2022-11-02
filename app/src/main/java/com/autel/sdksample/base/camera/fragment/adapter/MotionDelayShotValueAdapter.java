package com.autel.sdksample.base.camera.fragment.adapter;

import android.content.Context;

import com.autel.common.camera.media.ExposureCompensation;
import com.autel.common.camera.media.PhotoTimelapseInterval;
import com.autel.sdksample.base.adapter.SelectorAdapter;

public class MotionDelayShotValueAdapter extends SelectorAdapter<Integer> {


    public MotionDelayShotValueAdapter(Context context) {
        super(context);
        elementList.add(1);
        elementList.add(2);
        elementList.add(3);
        elementList.add(5);
        elementList.add(7);
    }
}
