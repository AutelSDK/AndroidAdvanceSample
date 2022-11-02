package com.autel.sdksample;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.autel.AutelNet2.dsp.controller.DspRFManager2;
import com.autel.AutelNet2.utils.ZteLogUtils;
import com.autel.common.CallbackWithOneParam;
import com.autel.common.error.AutelError;

public class ZteLogActivity extends AppCompatActivity implements Handler.Callback{
    private Switch aSwitch;
    private HandlerThread mHandlerTheard;
    private Handler mHandler;
    boolean isReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zte_log);
        mHandlerTheard = new HandlerThread("zteLog Thread");
        mHandlerTheard.start();
        mHandler = new Handler(this);
        aSwitch = (Switch) findViewById(R.id.switch1);
        ZteLogUtils.deleteBeforeFile();
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    DspRFManager2.getInstance().openZteNormalLog(new CallbackWithOneParam<Boolean>() {
                        @Override
                        public void onSuccess(final Boolean data) {
                            ZteLogUtils.mkdirNormalPath();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ZteLogActivity.this, "openZteNormalLog onSuccess " + data, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onFailure(AutelError error) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ZteLogActivity.this, "openZteNormalLog onFailure ", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }, new CallbackWithOneParam<byte[]>() {
                        @Override
                        public void onSuccess(byte[] data) {
                            ZteLogUtils.saveLocalLog(data);
                        }

                        @Override
                        public void onFailure(AutelError error) {

                        }
                    });
                } else {
                    DspRFManager2.getInstance().stopZteNormalLog(new CallbackWithOneParam<Boolean>() {
                        @Override
                        public void onSuccess(final Boolean data) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ZteLogActivity.this, "openZteNormalLog onSuccess " + data, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onFailure(AutelError error) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ZteLogActivity.this, "onFailure onSuccess ", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what){
            case 1:

                break;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DspRFManager2.getInstance().stopZteNormalLog(new CallbackWithOneParam<Boolean>() {
            @Override
            public void onSuccess(final Boolean data) {
            }

            @Override
            public void onFailure(AutelError error) {
            }
        });
    }
}
