package com.autel.sdksample.base.camera.fragment.adapter;

import android.content.Context;

import com.autel.common.camera.media.ShutterMode;
import com.autel.sdksample.base.adapter.SelectorAdapter;

import java.util.Arrays;


public class ShutterModeAdapter extends SelectorAdapter<ShutterMode> {

    public ShutterModeAdapter(Context context) {
        super(context);
        elementList.addAll(Arrays.asList(ShutterMode.values()));
    }
}
