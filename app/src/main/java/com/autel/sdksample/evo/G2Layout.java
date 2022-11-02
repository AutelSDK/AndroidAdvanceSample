package com.autel.sdksample.evo;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.autel.internal.XStarPremiumUpgradeActivity;
import com.autel.sdksample.R;
import com.autel.sdksample.TestActivity;
import com.autel.sdksample.base.CodecActivity;
import com.autel.sdksample.base.album.AlbumActivity;
import com.autel.sdksample.base.camera.CameraActivity;
import com.autel.sdksample.base.mission.MissionActivity;
import com.autel.sdksample.evo.visual.DynamicTrackActivity;

/**
 * Created by A16343 on 2017/9/5.
 */

public class G2Layout {
    private View mLayout;
    private Context mContext;

    public G2Layout(Context context) {
        mContext = context;
        mLayout = View.inflate(mContext, R.layout.activity_xstar, null);
        initUI();
    }

    public View getLayout() {
        return mLayout;
    }

    private void initUI() {
        mLayout.findViewById(R.id.rcTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new RxLock<String>(30_000){
//
//                    @Override
//                    protected void run() {
//                        Log.v("timeouttest","begin time " + System.currentTimeMillis());
//                        setData("aa");
//                    }
//
//
//                }.fire().subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<String>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(String s) {
//                        Log.v("timeouttest","end time "+s);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.v("timeouttest","end time "+e.getMessage()+"  time "+System.currentTimeMillis());
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
                mContext.startActivity(new Intent(mContext, G2RemoteControllerActivity.class));
            }
        });
        mLayout.findViewById(R.id.fcTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, G2FlyControllerActivity.class));
            }
        });
        mLayout.findViewById(R.id.cameraTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, CameraActivity.class));
            }
        });
        mLayout.findViewById(R.id.codecTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, CodecActivity.class));
            }
        });
        mLayout.findViewById(R.id.DSPTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, G2DspActivity.class));
            }
        });
        mLayout.findViewById(R.id.missionTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, MissionActivity.class));
            }
        });
        mLayout.findViewById(R.id.BatteryTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, G2BatteryActivity.class));
            }
        });
        mLayout.findViewById(R.id.GimbalTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, G2GimbalActivity.class));
            }
        });
        mLayout.findViewById(R.id.AlbumTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, AlbumActivity.class));
            }
        });
        mLayout.findViewById(R.id.avoidTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext,TestActivity.class));
            }
        });
//        mLayout.findViewById(R.id.visualTest).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mContext.startActivity(new Intent(mContext, DynamicTrackActivity.class));
//            }
//        });
        mLayout.findViewById(R.id.upgradeTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext,XStarPremiumUpgradeActivity.class));
            }
        });
    }
}
