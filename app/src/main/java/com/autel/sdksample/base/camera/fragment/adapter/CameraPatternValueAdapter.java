package com.autel.sdksample.base.camera.fragment.adapter;

import android.content.Context;

import com.autel.common.camera.base.CameraPattern;
import com.autel.sdksample.base.adapter.SelectorAdapter;

public class CameraPatternValueAdapter extends SelectorAdapter<CameraPattern> {


    public CameraPatternValueAdapter(Context context) {
        super(context);
        elementList.add(CameraPattern.FREE_FLIGHT);
        elementList.add(CameraPattern.INTELLIGENT_FLIGHT);
        elementList.add(CameraPattern.MISSION_FLIGHT);
        elementList.add(CameraPattern.DELAYED_PHOTOGRAPHY);
    }
}
