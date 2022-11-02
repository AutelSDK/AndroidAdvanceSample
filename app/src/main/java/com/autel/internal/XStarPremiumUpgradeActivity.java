package com.autel.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.autel.AutelNet2.aircraft.firmware.bean.UpgradeStatus;
import com.autel.common.CallbackWithOneParam;
import com.autel.common.error.AutelError;
import com.autel.downloader.HttpDownloadConfig;
import com.autel.downloader.HttpDownloadManager;
import com.autel.downloader.bean.DownloadTask;
import com.autel.downloader.bean.HttpDownloadCallback;
import com.autel.internal.sdk.product.datapost.MsgPostManager;
import com.autel.internal.sdk.product.datapost.PostRunnable;
import com.autel.internal.sdk.upgrade.CallBackWithOneParamProgressFirmwareUpgrade;
import com.autel.internal.utils.Utils;
import com.autel.libupdrage.upgrade.UpgradeManager;
import com.autel.libupdrage.upgrade.entity.DownloadBeanInfo;
import com.autel.libupdrage.upgrade.entity.HardwareUpgradeResultBean;
import com.autel.sdksample.R;
import com.autel.util.log.AutelLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.autel.common.error.AutelError.COMMON_TIMEOUT;

/**
 * Created by A15209 on 2018/2/7.
 */

public class XStarPremiumUpgradeActivity extends FragmentActivity implements HttpDownloadCallback {

    private final int TIME = 1000;
    private final int H_T_TIME = 0x00;
    private final int H_T_URL = 0x06;
    private final int H_T_FILE_PROGRESS = 0x08;
    private final int H_T_ERROR_TOAST = 0x10;
    private final int H_T_CAMERA_PROGRESS = 0x12;
    private final int H_T_DSP_PROGRESS = 0x14;
    private final int H_T_DSP_RC_PROGRESS = 0x16;
    private final int H_T_RC_PLAYER_PROGRESS = 0x18;
    private final int H_T_DOWNLOAD_PROGRESS = 0x20;
    private final int H_T_DOWNLOAD_STATUS = 0x22;
    private final int H_T_DOWNLOAD_ERROR = 0x24;
    private final int H_T_DOWNLOAD_SUCCESS = 0x26;
    private final int UPGRADE_START = 0x28;
    private final int UPGRADE_SEND_FILE = 0x2A;
    private final int UPGRADE_EMPTY_START = 0x2C;
    private final int ERROR_COUNT = 2;
    private final int RESPON_COUNT = 4;
    private TimeHandler timeHandler;
    private int timeCount = 0;
    private TextView cameraText, dspText, dsp_rcText, rc_playerText, urlText, downLoadProgressText;
    private LinearLayout downloadLayout;
    private static String UPGRADE_FILE_DIR = Environment.getExternalStorageDirectory().getPath() + File.separator + "explorer"
            + File.separator + "upgrade";
    //    private static String FILE_NAME = "gt900_MODB_FW_1.0.1.01.bin";
    private static String FILE_NAME = "EVO_FW_V1.3.3.25.bin";
    private static String FILE_DIR = "MODB_FW_V1.0.9";
    private static String JSON_HEAD = "header.json";
    private static String testJsonHeadStr =
            "{\"XB015_MODB_FW_\":{\"filepath\":\"XB015_MODB_FW_1.0.1.01.bin\",\"filesize\":\"26377713\",\"md5sum\":\"\"},\"at900_MODB_FW_\":{\"filepath\":\"at900_MODB_FW_1.0.1.01.bin\",\"filesize\":\"31536763\",\"md5sum\":\"\"},\"gt900_MODB_FW_\":{\"filepath\":\"gt900_MODB_FW_1.0.1.01.bin\",\"filesize\":\"28413253\",\"md5sum\":\"\"},\"rc_player_MODB_FW_\":{\"filepath\":\"rc_player_MODB_FW_1.0.1.01.bin\",\"filesize\":\"17926540\",\"md5sum\":\"\"}}";
   private static String testJsonHeadStr1 =
            "{\"XB015_MODB_FW_\":{\"filepath\":\"XB015_MODB_FW_V1.3.3.25.bin\",\"filesize\":\"24815244\",\"md5sum\":\"\"},\"at900_MODB_FW_\":{\"filepath\":\"at900_MODB_FW_V1.3.3.25.bin\",\"filesize\":\"30693857\",\"md5sum\":\"\"},\"gt900_MODB_FW_\":{\"filepath\":\"gt900_MODB_FW_V1.3.3.25.bin\",\"filesize\":\"27230407\",\"md5sum\":\"\"},\"rc_player_MODB_FW_\":{\"filepath\":\"rc_player_MODB_FW_V1.3.3.25.bin\",\"filesize\":\"17845063\",\"md5sum\":\"\"}}";
    private File upgradeFile;
    private List<DownloadBeanInfo> downloadBeanInfos = new ArrayList<>();
    private List<File> dirFiles = new ArrayList<>();
    private List<DownloadTask> downloadTaskList = new ArrayList<>();
    private static HttpDownloadManager mHttpDownloadManager = null;
    private static Context mContext;
    //    private Map<Integer, String> progressMap = new HashMap<>();
    private SparseArray<DownloadBeanInfo> downLoadMap = new SparseArray<>();
    private Map<String, TextView> textViewMap = new HashMap<>();
    private Button btn;
    private boolean isEmpty = false;
    private int errorCount = ERROR_COUNT;
    private int startErrorCount = ERROR_COUNT;
    private int responeCount = RESPON_COUNT;
    private long totalSize = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);
        init();
    }

    private static HttpDownloadManager getHttpDownloadManager(Context context) {
        if (null == mHttpDownloadManager) {
            mContext = context;
            mHttpDownloadManager = new HttpDownloadManager();
            mHttpDownloadManager.init(context, new HttpDownloadConfig.Builder().setMaxThread(1).build());
        }

        return mHttpDownloadManager;
    }

    private void init() {
        final Context context = XStarPremiumUpgradeActivity.this;
        timeHandler = new TimeHandler();
        upgradeFile = new File(UPGRADE_FILE_DIR, FILE_NAME);
        AutelLog.e("CYK:init:", "FILE_LENGTH:" + upgradeFile.length());

        Button buttonGetVersion = (Button) findViewById(R.id.getFirmwareVersion);
        buttonGetVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String language = getResources().getConfiguration().locale.getLanguage();

                UpgradeManager.getInstance().requestServer(language, Utils.getVersionName(XStarPremiumUpgradeActivity.this), new CallbackWithOneParam<HardwareUpgradeResultBean>() {
                    @Override
                    public void onSuccess(HardwareUpgradeResultBean resultBean) {
                        List<DownloadBeanInfo> data = resultBean.getDownloadList();
                        downloadBeanInfos.clear();
                        downloadTaskList.clear();
//                        progressMap.clear();
                        downLoadMap.clear();
                        AutelLog.e("CYK", "getUpgradeInfo:version:" + data.get(0).getPackage_version());
                        downloadBeanInfos.addAll(data);
                        StringBuilder urlStr = new StringBuilder();
                        if (data.size() > 0) {
                            for (DownloadBeanInfo d : data) {
                                String url = d.getItemurl();
//                                urlStr.append(d.getItemurl() + "\n");
                                urlStr.append(d.getHeader_info());
                                DownloadTask downloadTask = new DownloadTask(url, getSavePath(url));
                                downloadTaskList.add(downloadTask);
//                                progressMap.put(downloadTask.getId(), url);
                                downLoadMap.put(downloadTask.getId(), d);
                                AutelLog.e("CYK", "getUpgradeInfo:version:" + d.getPackage_version());
                            }
                        }
                        AutelLog.e("CYK:XStarPremiumUpgradeActivity.requestServer:", urlStr.toString());
                        Message msg = new Message();
                        msg.what = H_T_URL;
                        msg.obj = urlStr.toString();
                        timeHandler.sendMessage(msg);
                    }

                    @Override
                    public void onFailure(AutelError error) {
                        Message msg = new Message();
                        msg.what = H_T_ERROR_TOAST;
                        msg.obj = error.getDescription();
                        timeHandler.sendMessage(msg);
                    }
                });
            }
        });

        btn = (Button) findViewById(R.id.downloadFile);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHttpDownloadManager(context).addDownloadCallback(XStarPremiumUpgradeActivity.this);
                downloadLayout.removeAllViews();
                textViewMap.clear();
                if (downloadBeanInfos.size() > 0) {
                    for (DownloadBeanInfo d : downloadBeanInfos) {
                        TextView t = new TextView(context);
                        t.setTag(d.getItemurl());
                        t.setText(getName(d.getItemurl()) + ": 0%");
                        textViewMap.put(d.getItemurl(), t);
                        downloadLayout.addView(t);
                    }
                }

                if (downloadTaskList.size() > 0) {
                    for (DownloadTask d : downloadTaskList) {
                        AutelLog.e("CYK:DownloadTask:" + d.toContentValues().toString());
                        getHttpDownloadManager(context).start(d);
                    }
                }
            }
        });

        Button buttonStart = (Button) findViewById(R.id.startupgrade);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                isEmpty = false;
//                timeHandler.sendEmptyMessageDelayed(H_T_TIME, 0);
//                List<File> files = new ArrayList<>();
//                for (DownloadBeanInfo d : downloadBeanInfos) {
//                    String url = d.getItemurl();
//                    File f = new File(UPGRADE_FILE_DIR, getName(url));
//                    if (f.exists() && f.isFile() && Long.compare(f.length(), d.getItemsize()) == 0) {
//                        files.add(f);
//                    }
//                }
//
//                if (files.size() == 0) {
//                    files.add(upgradeFile);
//                }

//                if (upgradeFile.exists()) {
//                    UpgradeManager.getInstance().startUpgrade(files, new CallBackWithOneParamProgressFirmwareUpgrade<UpgradeStatus>() {
//                        @Override
//                        public void onProgress(UpgradeStatus upgradeStatus) {
//                            dispatchProgress(upgradeStatus);
//                        }
//
//                        @Override
//                        public void onSuccess(UpgradeStatus data) {
//                            AutelLog.e("CYK", "startUpgrade:onSuccess:" + data.getUcDevUpGradeFlag());
//                        }
//
//                        @Override
//                        public void onFailure(AutelError error) {
//                            AutelLog.e("CYK", "startUpgrade:onFailure:" + error.getDescription());
//                        }
//                    });
//                }

//                totalSize = 0;
//                File f = new File(UPGRADE_FILE_DIR + File.separator + FILE_DIR);
//                if (f.isDirectory()) {
//                    dirFiles.clear();
//                    File[] fileArray = f.listFiles();
//                    if (fileArray.length > 0) {
//                        for (File file : fileArray) {
//                            totalSize += file.length();
//                            dirFiles.add(file);
//                        }
//                    }
//                    Message msg = new Message();
//                    msg.what = UPGRADE_START;
//                    msg.arg1 = 0;
//                    msg.arg2 = dirFiles.size();
//                    msg.obj = dirFiles.get(0);
//                    timeHandler.sendMessage(msg);
//                }
                String s = "";
                try {
                    s = readTextFromSDcard(new FileInputStream(new File(UPGRADE_FILE_DIR + File.separator + JSON_HEAD)));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                File file = new File(UPGRADE_FILE_DIR + File.separator + FILE_NAME);
                UpgradeManager.getInstance().startUpgrade(file, testJsonHeadStr1, new CallBackWithOneParamProgressFirmwareUpgrade<UpgradeStatus>() {
                    @Override
                    public void onProgress(UpgradeStatus var) {
                        dispatchProgress(var);
                    }

                    @Override
                    public void onSuccess(UpgradeStatus data) {

                    }

                    @Override
                    public void onFailure(AutelError error) {

                    }
                });
            }
        });

        cameraText = (TextView) findViewById(R.id.camera_progress);
        dspText = (TextView) findViewById(R.id.dsp_progress);
        dsp_rcText = (TextView) findViewById(R.id.dsp_rc_progress);
        rc_playerText = (TextView) findViewById(R.id.rc_player_progress);
        urlText = (TextView) findViewById(R.id.firmwareVersion);
        downloadLayout = (LinearLayout) findViewById(R.id.downloadLayout);
    }

    private String readTextFromSDcard(InputStream is) throws Exception {
        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuffer buffer = new StringBuffer();
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
        }
        return buffer.toString();
    }

    private String getSavePath(String url) {
        return UPGRADE_FILE_DIR + File.separator + getName(url);
    }

    private String getName(String url) {
        String[] name = url.split(File.separator);
        return name[name.length - 1];
    }

    public static String byteArrayToHex(byte[] byteArray) {
        // 首先初始化一个字符数组，用来存放每个16进制字符
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        // new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））
        char[] resultCharArray = new char[byteArray.length * 2];

        // 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }

        // 字符数组组合成字符串返回
        return new String(resultCharArray);
    }

    public static String fileMD5(String inputFile) throws IOException {

        // 缓冲区大小（这个可以抽出一个参数）
        int bufferSize = 256 * 1024;

        FileInputStream fileInputStream = null;
        DigestInputStream digestInputStream = null;

        try {
            // 拿到一个MD5转换器（同样，这里可以换成SHA1）
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");


            // 使用DigestInputStream
            fileInputStream = new FileInputStream(inputFile);
            digestInputStream = new DigestInputStream(fileInputStream, messageDigest);


            // read的过程中进行MD5处理，直到读完文件
            byte[] buffer = new byte[bufferSize];
            while (digestInputStream.read(buffer) > 0) ;

            // 获取最终的MessageDigest
            messageDigest = digestInputStream.getMessageDigest();
            // 拿到结果，也是字节数组，包含16个元素
            byte[] resultByteArray = messageDigest.digest();
            // 同样，把字节数组转换成字符串
            return byteArrayToHex(resultByteArray);
        } catch (NoSuchAlgorithmException e) {
            return null;
        } finally {
            try {
                digestInputStream.close();
            } catch (Exception e) {
            }
            try {
                fileInputStream.close();
            } catch (Exception e) {
            }
        }
    }

    private void getMD5Time(String path) {
        long startTime = System.currentTimeMillis();
        try {
            AutelLog.e("CYK:getMD5Time:MD5String", fileMD5(path));
        } catch (IOException e) {
            AutelLog.e("CYK:getMD5Time:IOException", e.getMessage());
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        long t = (endTime - startTime) / 1000;

        AutelLog.e("CYK:getMD5Time:", "endTime:" + endTime + "  startTime:" + startTime +
                "  t:" + t);
    }

    private void dispatchProgress(UpgradeStatus upgradeStatus) {
        int devInt = upgradeStatus.getEnUpdateDev();
        int progress = upgradeStatus.getUcPercent();
        int status = upgradeStatus.getEnUpdateSts();
        Message msg = new Message();
        switch (devInt) {
            case 33:
                msg.what = H_T_CAMERA_PROGRESS;
                msg.arg1 = progress;
                msg.arg2 = status;
                break;
            case 2:
                msg.what = H_T_DSP_PROGRESS;
                msg.arg1 = progress;
                msg.arg2 = status;
                break;
            case 3:
                msg.what = H_T_DSP_RC_PROGRESS;
                msg.arg1 = progress;
                msg.arg2 = status;
                break;
            case 4:
                msg.what = H_T_RC_PLAYER_PROGRESS;
                msg.arg1 = progress;
                msg.arg2 = status;
                break;
        }
        timeHandler.sendMessage(msg);
    }

    private String getState(int status) {
        String s = "";
        switch (status) {
            case 0:
                s = "未升级";
                break;
            case 1:
                s = "等待升级";
                break;
            case 2:
                s = "升级中";
                break;
            case 3:
                s = "升级成功";
                break;
            case 4:
                s = "升级失败";
                break;
        }
        return s;
    }

    private String getJsonHead() {
        return testJsonHeadStr.replaceAll("\\\\\"", "\"");
    }

    private int getMin() {
        return dirFiles.size() > 4 ? 4 : dirFiles.size();
    }

    @Override
    public void createdTask(DownloadTask task, Object object) {

    }

    @Override
    public void waitting(int task_id, long receive_length, long total_length) {

    }

    @Override
    public void started(int task_id) {
        AutelLog.e("CYK:HttpDownloadCallback:started:" + "task_id:" + task_id);
    }

    @Override
    public void progress(int task_id, long receive_length, long total_length) {
        AutelLog.e("CYK:HttpDownloadCallback:progress:" + "task_id:" + task_id + "  receive_length:" + receive_length + "  total_length:" + total_length);
//        String url = progressMap.get(task_id);
        String url = downLoadMap.get(task_id).getItemurl();
        Message msg = new Message();
        msg.what = H_T_DOWNLOAD_PROGRESS;
        msg.arg1 = (int) ((receive_length * 100) / total_length);
        msg.obj = url;
        timeHandler.sendMessage(msg);
    }

    @Override
    public void completed(int task_id, String path) {
        AutelLog.e("CYK:HttpDownloadCallback:completed:" + "task_id:" + task_id);
//        String url = progressMap.get(task_id);
        String url = downLoadMap.get(task_id).getItemurl();
        String md5 = downLoadMap.get(task_id).getItemmd5();
        getMD5Time(path);
        AutelLog.e("CYK:HttpDownloadCallback:completed:md5" + "md5:" + md5);
        Message msg = new Message();
        msg.what = H_T_DOWNLOAD_ERROR;
        msg.obj = url;
        timeHandler.sendMessage(msg);
//        if (f.exists() &&) {
//
//        } else {
//            Message msg = new Message();
//            msg.what = H_T_DOWNLOAD_ERROR;
//            msg.obj = url;
//            timeHandler.sendMessage(msg);
//        }
    }

    @Override
    public void paused(int task_id, long receive_length, long total_length) {
        AutelLog.e("CYK:HttpDownloadCallback:paused:" + "task_id:" + task_id + "  receive_length:" + receive_length + "  total_length:" + total_length);
    }

    @Override
    public void error(int task_id, Throwable e) {
        AutelLog.e("CYK:HttpDownloadCallback:error:" + "task_id:" + task_id);
//        String url = progressMap.get(task_id);
        String url = downLoadMap.get(task_id).getItemurl();
        Message msg = new Message();
        msg.what = H_T_DOWNLOAD_ERROR;
        msg.obj = url;
        timeHandler.sendMessage(msg);
        getHttpDownloadManager(XStarPremiumUpgradeActivity.this).pauseAll();
    }

    private class TimeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Object obj;
            int progress;
            int state;
            switch (msg.what) {
                case H_T_TIME:
                    AutelLog.e("CYK", "H_T_TIME");
                    break;
                case H_T_URL:
                    obj = msg.obj;
                    if (obj instanceof String) {
                        urlText.setText((String) obj);
                    }
                    break;
                case H_T_FILE_PROGRESS:
                    downLoadProgressText.setText(msg.arg1 + "");
                    break;
                case H_T_ERROR_TOAST:
                    obj = msg.obj;
                    if (obj instanceof String) {
                        Toast.makeText(XStarPremiumUpgradeActivity.this, (String) obj, Toast.LENGTH_LONG).show();
                    }
                    break;
                case H_T_CAMERA_PROGRESS:
                    progress = msg.arg1;
                    state = msg.arg2;
                    cameraText.setText("Camera:" + progress + "%  " + getState(state));
                    break;
                case H_T_DSP_PROGRESS:
                    progress = msg.arg1;
                    state = msg.arg2;
                    dspText.setText("Dsp:" + progress + "%  " + getState(state));
                    break;
                case H_T_DSP_RC_PROGRESS:
                    progress = msg.arg1;
                    state = msg.arg2;
                    dsp_rcText.setText("Dsp_Rc:" + progress + "%  " + getState(state));
                    break;
                case H_T_RC_PLAYER_PROGRESS:
                    progress = msg.arg1;
                    state = msg.arg2;
                    rc_playerText.setText("Rc_Player:" + progress + "%  " + getState(state));
                    break;
                case H_T_DOWNLOAD_PROGRESS:
                    String url;
                    if (msg.obj instanceof String) {
                        url = (String) msg.obj;
                    } else {
                        break;
                    }

                    if (url.length() == 0) {
                        break;
                    }

                    TextView t = textViewMap.get(url);
                    t.setText(getName(url) + ":" + msg.arg1 + "%");
                    break;
                case H_T_DOWNLOAD_ERROR:
                    String errorUrl;
                    if (msg.obj instanceof String) {
                        errorUrl = (String) msg.obj;
                    } else {
                        break;
                    }
                    btn.setText(getName(errorUrl) + ":ERROR");
                    break;
                case H_T_DOWNLOAD_SUCCESS:
                    String successUrl;
                    if (msg.obj instanceof String) {
                        successUrl = (String) msg.obj;
                    } else {
                        break;
                    }
                    btn.setText(getName(successUrl) + ":SUCCESS");
                    break;
                case UPGRADE_START:
                    final int startIndex = msg.arg1;
                    final int fileSize = msg.arg2;
                    File f = dirFiles.get(startIndex);
                    final AtomicBoolean hasCallback = new AtomicBoolean(false);
                    final CallbackWithOneParam<Integer> callBack = new CallbackWithOneParam<Integer>() {
                        @Override
                        public void onSuccess(Integer data) {
                            AutelLog.e("CYK:startUpgrade:onSuccess" + "data:" + data);
                            if (hasCallback.compareAndSet(false, true)) {
                                responeCount--;
                                if (data == 1) {
                                    Message startMsg = new Message();
                                    startMsg.what = UPGRADE_SEND_FILE;
                                    startMsg.arg1 = startIndex;
                                    startMsg.arg2 = fileSize;
                                    timeHandler.sendMessage(startMsg);
                                    startErrorCount = ERROR_COUNT;
                                } else {
                                    if (startErrorCount > 0) {
                                        Message startMsg = new Message();
                                        startMsg.what = UPGRADE_START;
                                        startMsg.arg1 = startIndex;
                                        startMsg.arg2 = fileSize;
                                        timeHandler.sendMessage(startMsg);
                                        startErrorCount--;
                                    } else if (startIndex + 1 < getMin()) {
                                        Message startMsg = new Message();
                                        startMsg.what = UPGRADE_START;
                                        startMsg.arg1 = startIndex + 1;
                                        startMsg.arg2 = fileSize;
                                        timeHandler.sendMessage(startMsg);
                                        startErrorCount = ERROR_COUNT;
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(AutelError error) {
                            if (error != COMMON_TIMEOUT) {
                                responeCount--;
                                if (hasCallback.compareAndSet(false, true)) {
                                    AutelLog.e("CYK:startUpgrade:onFailure" + "error:" + error.getDescription());
                                    if (startErrorCount > 0) {
                                        Message startMsg = new Message();
                                        startMsg.what = UPGRADE_START;
                                        startMsg.arg1 = startIndex;
                                        startMsg.arg2 = fileSize;
                                        timeHandler.sendMessage(startMsg);
                                        startErrorCount--;
                                    } else if (startIndex + 1 < getMin()) {
                                        Message startMsg = new Message();
                                        startMsg.what = UPGRADE_START;
                                        startMsg.arg1 = startIndex + 1;
                                        startMsg.arg2 = fileSize;
                                        timeHandler.sendMessage(startMsg);
                                        startErrorCount = ERROR_COUNT;
                                    }
                                }
                            } else {
                                AutelLog.e("CYK:startUpgrade:onFailure" + "error:" + error.getDescription());
                                if (startErrorCount > 0) {
                                    Message startMsg = new Message();
                                    startMsg.what = UPGRADE_START;
                                    startMsg.arg1 = startIndex;
                                    startMsg.arg2 = fileSize;
                                    timeHandler.sendMessage(startMsg);
                                    startErrorCount--;
                                } else if (startIndex + 1 < getMin()) {
                                    Message startMsg = new Message();
                                    startMsg.what = UPGRADE_START;
                                    startMsg.arg1 = startIndex + 1;
                                    startMsg.arg2 = fileSize;
                                    timeHandler.sendMessage(startMsg);
                                    startErrorCount = ERROR_COUNT;
                                }
                            }
                        }
                    };
//                    UpgradeManager.getInstance().startUpgrade(f, totalSize, callBack);
                    /**
                     * 20秒 任务未上传完毕， 任务失败
                     */
                    MsgPostManager.instance().postDelayed(new PostRunnable() {
                        @Override
                        protected void task() {
                            if (hasCallback.compareAndSet(false, true)) {
                                callBack.onFailure(AutelError.COMMON_TIMEOUT);
                            }
                        }
                    }, 20 * 1000);
                    break;
                case UPGRADE_SEND_FILE:
                    final int startIndex1 = msg.arg1;
                    final int fileSize1 = msg.arg2;
                    File sendFile = dirFiles.get(startIndex1);
//                    UpgradeManager.getInstance().sendUpgradeFile(sendFile, new CallBackWithOneParamProgressFirmwareUpgrade<UpgradeStatus>() {
//                        @Override
//                        public void onProgress(UpgradeStatus var) {
//                            AutelLog.e("CYK:sendUpgradeFile:" + "UpgradeStatus:" + var.toString());
//                            dispatchProgress(var);
//                        }
//
//                        @Override
//                        public void onSuccess(UpgradeStatus data) {
//                            AutelLog.e("CYK:sendUpgradeFile:" + "onSuccess:index:" + startIndex1 + "  fileSize:" + fileSize1);
//                            if (startIndex1 + 1 < getMin()) {
//                                int index = startIndex1;
//                                index++;
//                                Message msg = new Message();
//                                msg.what = UPGRADE_START;
//                                msg.arg1 = index;
//                                msg.arg2 = fileSize1;
//                                timeHandler.sendMessage(msg);
//                                errorCount = ERROR_COUNT;
//                            } else if (!isEmpty) {
//                                Message msg = new Message();
//                                msg.what = UPGRADE_EMPTY_START;
//                                msg.arg1 = startIndex1 + 1;
//                                timeHandler.sendMessage(msg);
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(AutelError error) {
//                            AutelLog.e("CYK:sendUpgradeFile:" + "onFailure:" + error.getDescription());
//                            if (startIndex1 + 1 < getMin() && errorCount > 0) {
//                                Message msg = new Message();
//                                msg.what = UPGRADE_START;
//                                msg.arg1 = startIndex1;
//                                msg.arg2 = fileSize1;
//                                timeHandler.sendMessage(msg);
//                                errorCount--;
//                            }
//                        }
//                    });
                    break;
                case UPGRADE_EMPTY_START:
                    isEmpty = true;
                    final int emptyIndex = msg.arg1;
                    if (emptyIndex < 4) {
                        final AtomicBoolean emptyHasCallback = new AtomicBoolean(false);
                        final CallbackWithOneParam<Integer> emptyCallBack = new CallbackWithOneParam<Integer>() {
                            @Override
                            public void onSuccess(Integer data) {
                                AutelLog.e("CYK:UPGRADE_EMPTY_START:" + "index:" + emptyIndex);
                                if (emptyHasCallback.compareAndSet(false, true)) {
                                    if (data == 1) {
                                        Message startMsg = new Message();
                                        startMsg.what = UPGRADE_EMPTY_START;
                                        startMsg.arg1 = emptyIndex + 1;
                                        timeHandler.sendMessage(startMsg);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(AutelError error) {
                                if (error != COMMON_TIMEOUT) {
                                    if (emptyHasCallback.compareAndSet(false, true)) {
                                        AutelLog.e("CYK:UPGRADE_EMPTY_START:onFailure" + "error:" + error.getDescription());
                                    }
                                } else {
                                    AutelLog.e("CYK:UPGRADE_EMPTY_START:onFailure" + "error:" + error.getDescription());
                                }
                            }
                        };

//                        UpgradeManager.getInstance().startEmptyUpgrade(totalSize, emptyCallBack);

                        /**
                         * 30秒 任务未上传完毕， 任务失败
                         */
                        MsgPostManager.instance().postDelayed(new PostRunnable() {
                            @Override
                            protected void task() {
                                if (emptyHasCallback.compareAndSet(false, true)) {
                                    emptyCallBack.onFailure(AutelError.COMMON_TIMEOUT);
                                }
                            }
                        }, 20 * 1000);
                    }
                    break;
            }
        }
    }
}
