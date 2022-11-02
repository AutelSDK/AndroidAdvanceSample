package com.autel.starlink.aircraft.camera.edgebox;

/**
 * Created by A13087 on 2017/7/27.
 */

public class Detection {
    static {
        System.loadLibrary("Detection");
    }
    public static native void Detect(int[] buf,int w,int h,float x,float y,float thresh,int[] jarr);

    public static native int Classify(int[] buf,int w,int h,float thresh);
}
