package com.autel.sdksample.evo.visual;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.autel.AutelNet2.aircraft.visual.VisualModelManager;
import com.autel.AutelNet2.aircraft.visual.tracking.entity.DetectionTargetArea;
import com.autel.AutelNet2.aircraft.visual.tracking.entity.ReportGoalArea;
import com.autel.common.CallbackWithNoParam;
import com.autel.common.CallbackWithOneParam;
import com.autel.common.CallbackWithOneParamProgress;
import com.autel.common.CallbackWithTwoParams;
import com.autel.common.camera.CameraProduct;
import com.autel.common.camera.visual.TrackMode;
import com.autel.common.camera.visual.TrackingArea;
import com.autel.common.camera.visual.TrackingTarget;
import com.autel.common.error.AutelError;
import com.autel.common.flycontroller.visual.DynamicTrackMode;
import com.autel.common.mission.RealTimeInfo;
import com.autel.common.mission.evo.EvoTrackingRealTimeInfo;
import com.autel.common.mission.evo.EvoTrackingMission;
import com.autel.common.video.OnRenderFrameInfoListener;
import com.autel.internal.sdk.mission.evo.EvoTrackingMissionWithUpdate;
import com.autel.sdk.camera.AutelBaseCamera;
import com.autel.sdk.camera.AutelCameraManager;
import com.autel.sdk.camera.AutelXB015;
import com.autel.sdk.mission.MissionManager;
import com.autel.sdk.product.BaseProduct;
import com.autel.sdk.widget.AutelCodecView;
import com.autel.sdksample.R;
import com.autel.sdksample.base.BaseActivity;
import com.autel.sdksample.evo.visual.view.DynamicTrackView;

import java.util.List;

import static com.autel.common.product.AutelProductType.EVO;


public class DynamicTrackActivity extends BaseActivity<AutelCameraManager> {

    AutelCameraManager autelCameraManager;
    AutelBaseCamera currentCamera;
    AutelXB015 autelXB015;
    private MissionManager missionManager;
    private EvoTrackingMission evoTrackingMission;
    private BaseProduct baseProduct;

    TextView cameraType;
    DynamicTrackView dynamicTrackView;
    AutelCodecView mAutelCodecView;
    Button trackingModeBtn;
    Spinner spinner;
    TextView tvStop;
    int flightMode;
    TrackingTarget trackArea;
    private int screenWidth;
    private int screenHeight;
    private int realWidth;
    private int realHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();
        screenHeight = wm.getDefaultDisplay().getHeight();
    }

    @Override
    protected AutelCameraManager initController(BaseProduct product) {
        missionManager = product.getMissionManager();
        this.baseProduct = product;
        return autelCameraManager = product.getCameraManager();
    }

    @Override
    protected int getCustomViewResId() {
        //return R.layout.activity_visual_track;
        return 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initListener();
    }

    @Override
    protected void initUi() {
 /*       cameraType = (TextView) findViewById(R.id.cameraStatus);
        dynamicTrackView = (DynamicTrackView) findViewById(R.id.intelTrack);
        mAutelCodecView = (AutelCodecView) findViewById(R.id.codeView);
        trackingModeBtn = (Button) findViewById(R.id.setTrackingFlightMode);
        spinner = (Spinner) findViewById(R.id.trackingModeSpinner);
        tvStop = (TextView) findViewById(R.id.tvStop);
        dynamicTrackView.setCameraPreview(mAutelCodecView);
        setListener();
        AutelCodecView.setOnRenderFrameInfoListener(new OnRenderFrameInfoListener() {
            @Override
            public void onRenderFrameTimestamp(long pts) {

            }

            @Override
            public void onRenderFrameSizeChanged(int width, int height) {
                realWidth = width;
                realHeight = height;
                AutelLog.b().d("width:"+width+"  height:"+height);
//                updateVideoFrameViewsSize(width,height);
            }

        });*/
    }

    private void updateVideoFrameViewsSize(int width, int height) {
        Rect rect = new Rect();
        mAutelCodecView.getGlobalVisibleRect(rect);

        int vWidth = rect.width();
        int vHeight = rect.height();

        ViewGroup.LayoutParams lp =  dynamicTrackView.getLayoutParams();

        if (vWidth * height >= width * vHeight) {
            lp.width = width * vHeight / height;
            lp.height = vHeight;
        } else {
            lp.width = vWidth;
            lp.height = vWidth * height / width;
        }

        dynamicTrackView.setLayoutParams(lp);

    }

    private void setListener() {
        VisualModelManager.instance().setTrackingReportListener(TAG, new CallbackWithOneParam<ReportGoalArea>() {
            @Override
            public void onSuccess(ReportGoalArea data) {
                List<DetectionTargetArea> detectionTargetAreas = data.getParams().getArealist();
                if(null == detectionTargetAreas) return;
                dynamicTrackView.setPaintColor(Color.BLUE);
                dynamicTrackView.setCanReport(true);
                dynamicTrackView.setDetectionTargetArea(detectionTargetAreas);

            }

            @Override
            public void onFailure(AutelError error) {

            }
        });
        if(null != missionManager)
        missionManager.setRealTimeInfoListener(new CallbackWithOneParam<RealTimeInfo>() {
            @Override
            public void onSuccess(RealTimeInfo info) {
                if (info instanceof EvoTrackingRealTimeInfo) {
                    TrackingArea trackingArea = ((EvoTrackingRealTimeInfo) info).getTrackingArea();
                    if (trackingArea.getStatus() == 1) {
                        dynamicTrackView.setPaintColor(Color.GREEN);
                        dynamicTrackView.setCanReceiveRegData(true);
                        dynamicTrackView.setRegRegion(trackingArea.getAreaXRatio()* screenWidth, trackingArea.getAreaYRatio()* screenHeight, trackingArea.getWidthRatio()* screenWidth, trackingArea.getHeightRatio()* screenHeight);
                    }
                }
            }

            @Override
            public void onFailure(AutelError error) {

            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                flightMode = ++position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        trackingModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == evoTrackingMission) return;
                evoTrackingMission.switchFollowMode(DynamicTrackMode.findMode(flightMode), new CallbackWithNoParam() {
                    @Override
                    public void onFailure(AutelError error) {
                        Toast.makeText(DynamicTrackActivity.this, "setTrackingModeEnable onFailure ", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onSuccess() {
                        Toast.makeText(DynamicTrackActivity.this, "setTrackingModeEnable onSuccess ", Toast.LENGTH_LONG).show();

                    }
                });
            }
        });
        tvStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                missionManager.cancelMission(new CallbackWithNoParam() {
                    @Override
                    public void onSuccess() {
                        dynamicTrackView.clearForStop();
                    }

                    @Override
                    public void onFailure(AutelError error) {

                    }
                });

            }
        });

        dynamicTrackView.setTrackCallback(new DynamicTrackView.TrackCallback() {
            @Override
            public void onDrawCompleted(float x, float y, float width, float height) {
                trackArea = new TrackingTarget();
                trackArea.widthRatio = width / screenWidth;
                trackArea.heightRatio = height / screenHeight;
                trackArea.xRatio = x / screenWidth;
                trackArea.yRatio = y / screenHeight;
                startMission();
            }

            @Override
            public void onPointClick() {

            }


            @Override
            public void onFingerDown() {

            }
        });
    }


    private void initListener() {
//        VisualModelManager.instance().setTrackingMode(TrackMode.ENTER_TRACK, new CallbackWithOneParam<Boolean>() {
//            @Override
//            public void onSuccess(Boolean data) {
//                if (data) {
//                    Toast.makeText(DynamicTrackActivity.this, "setTrackingMode onSuccess "+data, Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onFailure(AutelError error) {
//
//            }
//        });
        if (null == autelCameraManager) {
            return;
        }
        autelCameraManager.setCameraChangeListener(new CallbackWithTwoParams<CameraProduct, AutelBaseCamera>() {
            @Override
            public void onSuccess(final CameraProduct data1, final AutelBaseCamera data2) {
                Log.v(TAG, "initListener onSuccess connect " + data1);
                if (currentCamera == data2) {
                    return;
                }
                currentCamera = data2;
                cameraType.setText(data1.toString());
                switch (data1) {

                    case XB015:
                        initXB015Camera();
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void onFailure(AutelError error) {
                Log.v(TAG, "initListener onFailure error " + error.getDescription());
                cameraType.setText("currentCamera connect broken  " + error.getDescription());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VisualModelManager.instance().removeTrackingReportListener(TAG);
    }

    private void initXB015Camera() {
        autelXB015 = (AutelXB015) currentCamera;
        //enter tracking
        autelXB015.setTrackingModeEnable(true, new CallbackWithNoParam() {
            @Override
            public void onSuccess() {
                Toast.makeText(DynamicTrackActivity.this, "setTrackingModeEnable onSuccess ", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(AutelError error) {
                Toast.makeText(DynamicTrackActivity.this, "setTrackingModeEnable onFailure ", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void prepareMission() {
        evoTrackingMission = EvoTrackingMissionWithUpdate.create();
        missionManager.prepareMission(evoTrackingMission, new CallbackWithOneParamProgress<Boolean>() {
            @Override
            public void onProgress(float var) {

            }

            @Override
            public void onSuccess(Boolean data) {
                if (data) {
                    setTrackingGoalArea(trackArea);
                }
            }

            @Override
            public void onFailure(AutelError error) {

            }

        });
    }

    public void setTrackingGoalArea(final TrackingTarget trackingGoalArea) {
        if (!isEVOProductInited() || null == evoTrackingMission) {
            return;
        }

        evoTrackingMission.lockTarget(trackingGoalArea, null);
    }


    private boolean isEVOProductInited() {
        return baseProduct != null && baseProduct.getType() == EVO;
    }

    private void startMission() {
        prepareMission();

    }

    private void goTracking() {
        evoTrackingMission.switchFollowMode(DynamicTrackMode.findMode(flightMode), new CallbackWithNoParam() {
            @Override
            public void onFailure(AutelError error) {
                Toast.makeText(DynamicTrackActivity.this, "setTrackingModeEnable onFailure ", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onSuccess() {
                Toast.makeText(DynamicTrackActivity.this, "setTrackingModeEnable onSuccess ", Toast.LENGTH_LONG).show();

            }
        });
    }

}
