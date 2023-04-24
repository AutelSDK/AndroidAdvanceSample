package com.autel.sdksample;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.autel.AutelNet2.dsp.controller.DspRFManager2;
import com.autel.AutelNet2.dsp.data.MatchStatusEnum;
import com.autel.AutelNet2.dsp.data.PairReportBean;
import com.autel.common.CallbackWithOneParam;
import com.autel.common.dsp.evo.AircraftRole;
import com.autel.common.error.AutelError;
import com.autel.common.product.AutelProductType;
import com.autel.sdk.Autel;
import com.autel.sdk.ProductConnectListener;
import com.autel.sdk.product.BaseProduct;
import com.autel.sdksample.evo.G2Layout;
import com.autel.sdksample.premium.XStarPremiumLayout;
import com.autel.sdksample.util.FileUtils;
import com.autel.sdksample.xstar.XStarLayout;
import com.autel.util.log.AutelLog;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.autel.internal.sdk.AutelBaseApplication.getAppContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


public class ProductActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    private int index;
    private long timeStamp;
    static AtomicBoolean hasInitProductListener = new AtomicBoolean(false);
    private String fileConfig1 = "/sdcard/anddev/autel288_7.cfg";
    private String fileConfig2 = "/sdcard/anddev/autel288_7_final.weights";
    private String fileConfig3 = "/sdcard/anddev/autel13.cfg";
    private String fileConfig4 = "/sdcard/anddev/autel13.backup";
    private AutelProductType currentType = AutelProductType.UNKNOWN;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        setContentView(createView(AutelProductType.EVO));
        Log.v("productType", "ProductActivity onCreate ");
        //*/
        Autel.setProductConnectListener(new ProductConnectListener() {
            @Override
            public void productConnected(BaseProduct product) {
                Log.v("productType", "product " + product.getType());
                currentType = product.getType();
                setTitle(currentType.toString());
                setContentView(createView(currentType));
                /**
                 * 避免从WiFi切换到USB时，重新弹起MainActivity界面
                 */
                hasInitProductListener.compareAndSet(false, true);

                BaseProduct previous = ((TestApplication) getApplicationContext()).getCurrentProduct();
                ((TestApplication) getApplicationContext()).setCurrentProduct(product);
                /**
                 * 如果产品类型发生变化，退出到该界面下
                 */
                if (null != previous) {
                    if (previous.getType() != product.getType()) {
                        startActivity(new Intent(getApplicationContext(), ProductActivity.class));
                    }
                }
            }


            @Override
            public void productDisconnected() {
                Log.v("productType", "productDisconnected ");
                currentType = AutelProductType.UNKNOWN;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setTitle(currentType.toString());
                    }
                });
            }
        });
        /*/
        productSelector.productConnected(AutelProductType.X_STAR);
        //*/

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
        }
        FileUtils.Initialize(getAppContext(),fileConfig1,"autel288_7.cfg");
        FileUtils.Initialize(getAppContext(),fileConfig2,"autel288_7_final.weights");
        FileUtils.Initialize(getAppContext(),fileConfig3,"autel13.cfg");
        FileUtils.Initialize(getAppContext(),fileConfig4,"autel13.backup");
        DspRFManager2.getInstance().addPairReportListener(pairReportListener);
        Log.v("lifeTest", "onCreate");
    }

    private View createView(AutelProductType productType) {
        switch (productType) {
            case X_STAR:
                return new XStarLayout(this).getLayout();
            case EVO:
            case EVO_2:
                return new G2Layout(this).getLayout();
            case PREMIUM:
                return new XStarPremiumLayout(this).getLayout();

        }
        return new XStarLayout(this).getLayout();
    }

    public void onResume() {
        super.onResume();
        setTitle(currentType.toString());
        Log.v("lifeTest", "onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (needExit) {
            hasInitProductListener.set(false);
        }
        Log.v("lifeTest", "onDestroy");
    }

    protected void onPause(){
        super.onPause();
        Log.v("lifeTest", "onPause");
    }

    protected void onRestart(){
        super.onRestart();
        Log.v("lifeTest", "onRestart");
    }

    protected void onStart(){
        super.onStart();
        Log.v("lifeTest", "onStart");
    }

    protected void onStop(){
        super.onStop();
        Log.v("lifeTest", "onStop");
    }


    public void finish() {
        super.finish();
        Log.v("productType", "activity finish ");
    }

    private boolean needExit = false;

    public void onBackPressed() {
        if (System.currentTimeMillis() - timeStamp < 1500) {
            needExit = true;
            super.onBackPressed();
        } else {
            timeStamp = System.currentTimeMillis();
        }
    }

    public static void receiveUsbStartCommand(Context context) {
        if (hasInitProductListener.compareAndSet(false, true)) {
            Intent i = new Intent();
            i.setClass(context, ProductActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }

    public void setMasterFequency(View view) {
        DspRFManager2.getInstance().bingAircraftToRemote(AircraftRole.MASTER);
    }

    public void setFrequency(View view) {
        DspRFManager2.getInstance().bingAircraftToRemote(AircraftRole.SLAVER);
    }

    private final CallbackWithOneParam<PairReportBean> pairReportListener = new CallbackWithOneParam<PairReportBean>() {
        @Override
        public void onSuccess(PairReportBean data) {
            AutelLog.debug_i("lifeTest", "PairReport -> " + data.toString());
            MatchStatusEnum statusEnum = MatchStatusEnum.find(data.Status);
            runOnUiThread(() -> {
                if (statusEnum == MatchStatusEnum.SUCCESS) {
                    Toast.makeText(getAppContext(), "frequency matching success", Toast.LENGTH_LONG).show();
                } else if (statusEnum == MatchStatusEnum.TIMEOUT) {
                    Toast.makeText(getAppContext(), "frequency matching fail", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onFailure(AutelError error) {
            Toast.makeText(getAppContext(), "frequency matching error" + error.getDescription(), Toast.LENGTH_LONG).show();
        }
    };
}
