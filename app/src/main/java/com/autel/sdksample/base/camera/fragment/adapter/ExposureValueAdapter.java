package com.autel.sdksample.base.camera.fragment.adapter;

import android.content.Context;

import com.autel.common.camera.media.ExposureCompensation;
import com.autel.sdksample.base.adapter.SelectorAdapter;

public class ExposureValueAdapter extends SelectorAdapter<ExposureCompensation> {


    public ExposureValueAdapter(Context context) {
        super(context);
        elementList.add(ExposureCompensation.POSITIVE_3p0);
        elementList.add(ExposureCompensation.POSITIVE_2p7);
        elementList.add(ExposureCompensation.POSITIVE_2p3);
        elementList.add(ExposureCompensation.POSITIVE_2p0);
        elementList.add(ExposureCompensation.POSITIVE_1p7);
        elementList.add(ExposureCompensation.POSITIVE_1p3);
        elementList.add(ExposureCompensation.POSITIVE_1p0);
        elementList.add(ExposureCompensation.POSITIVE_0p7);
        elementList.add(ExposureCompensation.POSITIVE_0p3);
        elementList.add(ExposureCompensation.POSITIVE_0);
        elementList.add(ExposureCompensation.NEGATIVE_0p3);
        elementList.add(ExposureCompensation.NEGATIVE_0p7);
        elementList.add(ExposureCompensation.NEGATIVE_1p0);
        elementList.add(ExposureCompensation.NEGATIVE_1p3);
        elementList.add(ExposureCompensation.NEGATIVE_1p7);
        elementList.add(ExposureCompensation.NEGATIVE_2p0);
        elementList.add(ExposureCompensation.NEGATIVE_2p3);
        elementList.add(ExposureCompensation.NEGATIVE_2p7);
        elementList.add(ExposureCompensation.NEGATIVE_3p0);
    }
}
