package com.autel.sdksample.evo;

import android.os.Bundle;
import android.view.View;

import com.autel.common.CallbackWithOneParam;
import com.autel.common.battery.evo.EvoBatteryInfo;
import com.autel.common.error.AutelError;
import com.autel.sdk.battery.AutelBattery;
import com.autel.sdk.battery.EvoBattery;
import com.autel.sdk.product.BaseProduct;
import com.autel.sdk.product.EvoAircraft;
import com.autel.sdksample.R;
import com.autel.sdksample.base.BatteryActivity;


public class G2BatteryActivity extends BatteryActivity {
    private AutelBattery mXStarEvoBattery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Battery");
    }

    @Override
    protected AutelBattery initController(BaseProduct product) {
        mXStarEvoBattery = (product).getBattery();
        return mXStarEvoBattery;
    }

    @Override
    protected int getCustomViewResId() {
        return R.layout.activity_g2_battery;
    }

    @Override
    protected void initUi() {
        super.initUi();
        findViewById(R.id.getHistoryState).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mXStarEvoBattery.getHistoryState(new CallbackWithOneParam<int[]>() {
//                    @Override
//                    public void onSuccess(int[] data) {
//                        logOut("getHistoryState  onSuccess :  " + data);
//                    }
//
//                    @Override
//                    public void onFailure(AutelError error) {
//                        logOut("getHistoryState  error :  " + error.getDescription());
//                    }
//                });
            }
        });
        findViewById(R.id.resetBatteryRealTimeDataListener).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                test3();
                if (mXStarEvoBattery instanceof EvoBattery)
                    ((EvoBattery) mXStarEvoBattery).setBatteryStateListener(null);


            }
        });

        findViewById(R.id.setBatteryRealTimeDataListener).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mXStarEvoBattery instanceof EvoBattery)
                    ((EvoBattery)mXStarEvoBattery).setBatteryStateListener(new CallbackWithOneParam<EvoBatteryInfo>() {
                    @Override
                    public void onFailure(AutelError error) {
                        logOut("setBatteryStateListener  error :  " + error.getDescription());
                    }

                    @Override
                    public void onSuccess(EvoBatteryInfo data) {
                        logOut("setBatteryStateListener  data current battery :  " + data.toString());
                    }
                });
            }
        });

    }

//    private void test() {
//        RxEvoBattery rxEvoBattery = mXStarEvoBattery.toRx();
//        rxEvoBattery.getDischargeCount().subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread()).subscribe(new DisposableObserver<Integer>() {
//            @Override
//            public void onNext(Integer o) {
//                Toast.makeText(G2BatteryActivity.this, "getDischargeCount success", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Toast.makeText(G2BatteryActivity.this, "getDischargeCount failed " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });
//        rxEvoBattery.getDischargeDay().subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread()).subscribe(new DisposableObserver<Integer>() {
//            @Override
//            public void onNext(Integer o) {
//                Toast.makeText(G2BatteryActivity.this, "getDischargeDay success", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Toast.makeText(G2BatteryActivity.this, "getDischargeDay failed " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });
//        rxEvoBattery.getFullChargeCapacity().subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread()).subscribe(new DisposableObserver<Integer>() {
//            @Override
//            public void onNext(Integer o) {
//                Toast.makeText(G2BatteryActivity.this, "getFullChargeCapacity success", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Toast.makeText(G2BatteryActivity.this, "getFullChargeCapacity failed " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });
//        rxEvoBattery.getCriticalBatteryNotifyThreshold().subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread()).subscribe(new DisposableObserver<Float>() {
//            @Override
//            public void onNext(Float o) {
//                Toast.makeText(G2BatteryActivity.this, "getCriticalBatteryNotifyThreshold success", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Toast.makeText(G2BatteryActivity.this, "getCriticalBatteryNotifyThreshold failed " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });
//        rxEvoBattery.getLowBatteryNotifyThreshold().subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread()).subscribe(new DisposableObserver<Float>() {
//            @Override
//            public void onNext(Float o) {
//                Toast.makeText(G2BatteryActivity.this, "getLowBatteryNotifyThreshold success", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Toast.makeText(G2BatteryActivity.this, "getLowBatteryNotifyThreshold failed " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });
//    }


}
