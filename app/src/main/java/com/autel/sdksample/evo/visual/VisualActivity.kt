package com.autel.sdksample.evo.visual

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.FragmentActivity
import com.autel.AutelNet2.aircraft.visual.VisualModelManager
import com.autel.AutelNet2.aircraft.visual.obstacleAvoidance.entity.VisualSettingAckInfo
import com.autel.AutelNet2.dsp.controller.DspRFManager2
import com.autel.AutelNet2.utils.CacheUtils
import com.autel.bean.dsp.ReportBertInfo
import com.autel.bean.dsp.SignalStrengthReport
import com.autel.bean.dsp.VideoRateInfoImpl
import com.autel.common.CallbackWithNoParam
import com.autel.common.CallbackWithOneParam
import com.autel.common.CallbackWithTwoParams
import com.autel.common.camera.CameraProduct
import com.autel.common.camera.base.MediaMode
import com.autel.common.camera.visual.TrackingArea
import com.autel.common.camera.visual.TrackingTarget
import com.autel.common.error.AutelError
import com.autel.common.video.OnRenderFrameInfoListener
import com.autel.internal.sdk.camera.data.model.CameraXB015Data
import com.autel.sdk.camera.AutelBaseCamera
import com.autel.sdk.camera.AutelCameraManager
import com.autel.sdk.camera.AutelXB015
import com.autel.sdk.widget.AutelCodecView
import com.autel.sdksample.R
import com.autel.sdksample.TestApplication
import com.autel.sdksample.evo.visual.view.IntelTrackView
import com.autel.sdksample.visual.utils.TrackingUtils
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingDeque

class VisualActivity : FragmentActivity() {

    val TAG = "VisualActivity"

    internal var blackWidth16 = 0f//图传黑边长宽
    internal var blackHeight9 = 0f
    internal var realWidth: Float = 0f
    internal var realHeight: Float = 0f
    internal var xb015: AutelXB015? = null
    internal var autelCameraManager: AutelCameraManager? = null
    internal var camera: AutelBaseCamera? = null
    val mTimer = Timer()


    var isConnectSuccess: Boolean = false
    var isFirst: Boolean = false

    var mIntelTrackView: IntelTrackView? = null
    var mConfirm: TextView? = null
    var mStop: TextView? = null
    var mExit: TextView? = null
    var timeStampTv: TextView? = null
    var timeCount: TextView? = null
    var trackingGoalInfoTv: TextView? = null
    var mProgress: View? = null
    var mSwitch: Switch? = null
    var mSwitchCamera: Switch? = null
    var mGoBtn: Button? = null
    var mGoBtnRl: View? = null
    var mAutelCodecView: AutelCodecView? = null
    var mCameraStatusTv: TextView? = null
    var mIdentityResultTv: TextView? = null
    var mRecordingTv: TextView? = null
    var mCaptureTv: TextView? = null
    var mVideoInfo: TextView? = null
    var mVideoRateInfo: TextView? = null
    var mReportBandwidthInfo: TextView? = null
    var mFlightModeBtn: Button? = null
    var mOpenVideoTV: TextView? = null
    var mCloseVideoTV: TextView? = null
    var mTrackingModeSpinner: Spinner? = null
    private val dspCacheList = LinkedBlockingDeque<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_visual)
 /*       val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        realWidth = wm.defaultDisplay.width.toFloat()
        realHeight = wm.defaultDisplay.height.toFloat()
        mAutelCodecView = findViewById(R.id.codeView) as AutelCodecView
        mFlightModeBtn = findViewById(R.id.setTrackingFlightMode) as Button
        mOpenVideoTV = findViewById(R.id.openVideo) as TextView
        mCloseVideoTV = findViewById(R.id.closeVideo) as TextView
        mTrackingModeSpinner = findViewById(R.id.trackingModeSpinner) as Spinner
        initView()
        val product = (applicationContext as TestApplication).currentProduct
        if (null != product) {
            autelCameraManager = product.cameraManager
        }

        if (null != autelCameraManager) {
            autelCameraManager!!.setCameraChangeListener(object : CallbackWithTwoParams<CameraProduct, AutelBaseCamera> {
                override fun onFailure(error: AutelError) {

                }

                override fun onSuccess(data1: CameraProduct, data2: AutelBaseCamera?) {
                    camera = data2
                    isConnectSuccess = true
                    Handler(Looper.getMainLooper()).post {
                        mCameraStatusTv?.text = "Camera->" + data1
                        when (data1) {
                            CameraProduct.R12 -> {

                            }
                            CameraProduct.XB015 -> {
                                initCamera()
                            }
                        }

                    }
                }
            })
        }
        setListener()*/
    }

    private fun initView() {

/*        mIntelTrackView = findViewById(R.id.intelTrack) as IntelTrackView
        mConfirm = findViewById(R.id.confirm) as TextView
        mStop = findViewById(R.id.stop) as TextView
        mExit = findViewById(R.id.exit) as TextView
        timeStampTv = findViewById(R.id.timeStamp) as TextView
        timeCount = findViewById(R.id.timeCount) as TextView
        trackingGoalInfoTv = findViewById(R.id.goalInfo) as TextView
        mProgress = findViewById(R.id.progress_container)
        mCameraStatusTv = findViewById(R.id.cameraStatus) as TextView
        mIdentityResultTv = findViewById(R.id.identityResult) as TextView
        mRecordingTv = findViewById(R.id.mRecording) as TextView
        mCaptureTv = findViewById(R.id.capture) as TextView
        mVideoInfo = findViewById(R.id.signstrength) as TextView
        mVideoRateInfo = findViewById(R.id.videoRateInfo) as TextView
        mReportBandwidthInfo = findViewById(R.id.ReportBandwidthInfo) as TextView
        mSwitch = findViewById(R.id.mSwitch) as Switch
        mSwitchCamera = findViewById(R.id.mSwitchCamera) as Switch
        mGoBtnRl = findViewById(R.id.confirmation_container)
        mGoBtn = findViewById(R.id.yes_button) as Button
        mIntelTrackView?.setCameraPreview(mAutelCodecView!!)*/
    }

    private fun changePage(page: Class<*>) {
   /*     try {
            supportFragmentManager.beginTransaction().replace(R.id.content_layout, page.newInstance() as android.support.v4.app.Fragment).commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
*/
    }

    fun getCamera(): AutelBaseCamera? {
        return camera
    }

    fun initCamera() {

        xb015 = camera as AutelXB015
        if (null != mTimer) {
            mTimer.schedule(object : TimerTask() {
                override fun run() {
                    Handler(Looper.getMainLooper()).post {
                        timeCount?.text = " 接收到的数据/s: " + trackCount + " 次"
                        trackCount = 0
                    }
                }
            }, 1000, 1000)
        }
        (xb015 as AutelXB015).setTrackingModeEnable(true, object : CallbackWithNoParam {
            override fun onSuccess() {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(this@VisualActivity, "setTrackingModeEnable onSuccess ", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(error: AutelError?) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(this@VisualActivity, "setTrackingModeEnable onFailure ", Toast.LENGTH_LONG).show()
                }
            }

        })
    }

    var flightMode = 0
    fun setListener() {
        mTrackingModeSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                flightMode = position
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
        mFlightModeBtn?.setOnClickListener {
            VisualModelManager.instance().setVisualTrackingFightMode(flightMode + 1, object : CallbackWithOneParam<VisualSettingAckInfo> {
                override fun onFailure(error: AutelError?) {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(this@VisualActivity, "setVisualTrackingFightMode onFailure ", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onSuccess(data: VisualSettingAckInfo?) {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(this@VisualActivity, "setVisualTrackingFightMode onSuccess ", Toast.LENGTH_LONG).show()
                    }
                }
            })
        }
        mOpenVideoTV?.setOnClickListener {
            DspRFManager2.getInstance().setVideoLinkStatus(true, object : CallbackWithOneParam<Boolean> {
                override fun onFailure(error: AutelError?) {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(this@VisualActivity, "setVideoLinkStatus onFailure ", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onSuccess(data: Boolean?) {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(this@VisualActivity, "setVideoLinkStatus " + data, Toast.LENGTH_LONG).show()
                    }
                }
            })
        }
        mCloseVideoTV?.setOnClickListener {
            DspRFManager2.getInstance().setVideoLinkStatus(false, object : CallbackWithOneParam<Boolean> {
                override fun onFailure(error: AutelError?) {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(this@VisualActivity, "setVideoLinkStatus onFailure ", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onSuccess(data: Boolean?) {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(this@VisualActivity, "setVideoLinkStatus " + data, Toast.LENGTH_LONG).show()
                    }
                }
            })
        }

        mCaptureTv?.setOnClickListener {
            when (camera) {
                is AutelXB015 -> {
                    val currentMode = CameraXB015Data.instance().currentMode
                    when (currentMode) {
                        "Single" -> {
                            capture()
                        }
                        "Record" -> {
                            setMediaMode(MediaMode.SINGLE)
                        }
                    }
                }
            }
        }

        mRecordingTv?.setOnClickListener {
            when (camera) {
                is AutelXB015 -> {
                    val currentMode = CameraXB015Data.instance().currentMode
                    when (currentMode) {
                        "Single" -> {
                            setMediaMode(MediaMode.VIDEO)
                        }
                        "Burst" -> {
                            setMediaMode(MediaMode.VIDEO)
                        }
                        "Timelapse" -> {
                            setMediaMode(MediaMode.VIDEO)
                        }
                        "AEB" -> {
                            setMediaMode(MediaMode.VIDEO)
                        }
                    }
                    val workStatue = CameraXB015Data.instance().systemStatus
                    when (workStatue) {
                        "Recording" -> {
                            stopRecording()
                        }
                        "Idle" -> {
                            startRecording()
                        }
                    }

                }
            }
        }
        mIntelTrackView!!.setTrackCallback(object : IntelTrackView.TrackCallback {
            override fun onFingerDown() {
            }

            override fun onDrawCompleted() {
                //处理界面一些显示
            }

            override fun onPointClick() {
                dealPointClickEvent(-1)
            }

        })
        mIntelTrackView?.setIdentifyCallback(object : IntelTrackView.IdentifyCallback {
            override fun finishDrawIdentify(classId: Int) {
                mProgress?.visibility = View.GONE
                mGoBtnRl?.visibility = View.VISIBLE
                mIdentityResultTv?.text = "识别结果：" + classId
            }

            override fun startIdentify() {
                mProgress?.visibility = View.VISIBLE
            }

            override fun finishPointIdentify(classId: Int) {
                mProgress?.visibility = View.GONE
                mIdentityResultTv?.text = "识别结果：" + classId
                dealPointClickEvent(classId)
            }

        })
        mConfirm?.setOnClickListener {
            dealTrackGo()
        }
        mStop?.setOnClickListener {
            when (camera) {
                is AutelXB015 -> {
//                    (xb015 as AutelXB015).cancelTracking(object : CallbackWithNoParam {
//                        override fun onSuccess() {
//                            mIntelTrackView?.setCanReceiveRegData(false)
//                            mIntelTrackView?.clearForStop()
//                            mIntelTrackView?.setRegRegion(0f, 0f, 0f, 0f)
//                            trackingDataMap.clear()
//                            ptsMap.clear()
//                            mNumberOfPacketLoss = 0
//                            isFirst = false
////                            if (null != mTimer) mTimer.cancel()
//                            mAutelCodecView?.setOnRenderFrameInfoListener(null)
//
//                        }
//
//                        override fun onFailure(error: AutelError?) {
//                        }
//
//                    })
                }
            }
        }
        mSwitch?.setOnCheckedChangeListener { buttonView, isChecked ->
            when (isChecked) {
                true -> {
                    mIntelTrackView?.setIsEdgeBox(true)
                    Toast.makeText(this@VisualActivity, "打开识别", Toast.LENGTH_SHORT).show()
                }
                false -> {
                    mIntelTrackView?.setIsEdgeBox(false)
                    Toast.makeText(this@VisualActivity, "关闭识别", Toast.LENGTH_SHORT).show()
                }
            }
        }
        mSwitchCamera?.setOnCheckedChangeListener { buttonView, isChecked ->
            //            when (isChecked) {
//                true -> {
//                    mIntelTrackView?.setIsEdgeBox(true)
//                    when(camera){
//                        is AutelXB015->{
//                            changePage(CameraXB015Fragment::class.java)
//                        }
//                    }
//
//                    Toast.makeText(this@VisualActivity, "添加控制相机界面", Toast.LENGTH_SHORT).show()
//                }
//                false -> {
//                    mIntelTrackView?.setIsEdgeBox(false)
//                    changePage(CameraNotConnectFragment::class.java)
//                    Toast.makeText(this@VisualActivity, "关闭控制相机界面", Toast.LENGTH_SHORT).show()
//                }
//            }
        }
        mGoBtn?.setOnClickListener {
            mGoBtnRl?.visibility = View.GONE
            mIntelTrackView?.setCanReceiveRegData(false)
            dealTrackGo()
        }
        DspRFManager2.getInstance().addSignalStregthListener("setSignalStregthListener", object : CallbackWithOneParam<SignalStrengthReport> {
            override fun onSuccess(data: SignalStrengthReport?) {
                if (CacheUtils.isDspCache()) {
                    dspCacheList.offer(data.toString())
                }
                Handler(Looper.getMainLooper()).post {
                    //mVideoInfo?.text = "信号强度信息-> " + data.toString()+" role.state:"+data?.aircraftRoleState+" role:"+data?.aircraftRole
//                    Log.d(TAG, "setRFTaskListener data " + data.toString())
                }
            }

            override fun onFailure(error: AutelError) {
                Log.d(TAG, "setReportBertInfoListener " + error.description)
            }
        })

        if (CacheUtils.isDspCache()) {
            CacheUtils.isFlag = true
            val thread = Thread(Runnable {
                while (CacheUtils.isFlag) {
                    try {
                        val area = dspCacheList.take()
                        CacheUtils.writeLog(area)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }

                }
            })
            thread.start()
        }
        DspRFManager2.getInstance().setReportBertInfoListener(object : CallbackWithOneParam<ReportBertInfo> {
            override fun onSuccess(data: ReportBertInfo?) {
                Handler(Looper.getMainLooper()).post {
                    mReportBandwidthInfo?.text = "带宽信息-> " + data.toString()
                    Log.d(TAG, "setReportBertInfoListener data " + data.toString())
                }
            }

            override fun onFailure(error: AutelError?) {

            }
        })
        DspRFManager2.getInstance().setVideoRateInfoListener(object : CallbackWithOneParam<VideoRateInfoImpl> {
            override fun onSuccess(data: VideoRateInfoImpl?) {
                Handler(Looper.getMainLooper()).post {
                    mVideoRateInfo?.text = "帧率信息-> " + data.toString()
                    Log.d(TAG, "setVideoRateInfoListener data " + data.toString())
                }

            }

            override fun onFailure(error: AutelError?) {

            }
        })
    }


    var trackCount: Int = 0 //用于计算每秒收到的跟踪数据
    var mNumberOfPacketLoss: Int = 0 //用于计算每秒丢包娄
    var streamPtsTime: Long = 0 //用于计录视频当前帧的时间戳
    var trackingDataMapSize: Int = 0
    val trackingDataMap = ConcurrentHashMap<Long, TrackingArea>()
    val ptsMap = ConcurrentHashMap<Long, Long>()
    fun dealRecvData(trackingData: TrackingArea) {
        if (TrackingUtils.checkValidValue(trackingData.areaXRatio) && TrackingUtils.checkValidValue(trackingData.areaYRatio)
                && TrackingUtils.checkValidValue(trackingData.widthRatio) && TrackingUtils.checkValidValue(trackingData.heightRatio)) {
            trackingGoalInfoTv?.setTextColor(Color.BLUE)
            trackingGoalInfoTv?.text = "x:" + trackingData.areaXRatio + " y:" + trackingData.areaYRatio + " width:" + trackingData.widthRatio + " height:" + trackingData.heightRatio + " id:" + trackingData.id
            val timeStamp = trackingData.timestamp
            trackingDataMap.put(timeStamp, trackingData)
            trackCount++
            trackingDataMapSize += 1
            if (streamPtsTime != timeStamp) {
                if (!ptsMap.containsValue(timeStamp)) {
                    ptsMap.put(System.currentTimeMillis(), timeStamp)
                } else {
                    mNumberOfPacketLoss++
                    for (time in ptsMap.keys) {
                        val currentTime = ptsMap[time]
                        if (currentTime == timeStamp) {
                            timeStampTv?.setTextColor(Color.RED)
                            timeStampTv?.text = "时间戳相差值->> " + (System.currentTimeMillis() - time) + " 丢帧数：" + mNumberOfPacketLoss
                            break
                        }
                    }
                    ptsMap.clear()
                }
            } else {
                timeStampTv?.text = "同时为当前帧数据 ->丢帧数：" + mNumberOfPacketLoss
            }

            containsToDraw(streamPtsTime)
        }
    }

    private fun containsToDraw(timestamp: Long) {
        if (trackingDataMapSize > 500) {
            trimTrackingMap(timestamp)
        }
        val mCameraGoalAre: TrackingArea? = trackingDataMap[timestamp]
        if (null != mCameraGoalAre) {
            val x = mCameraGoalAre.areaXRatio * (realWidth - 2 * blackWidth16) + blackWidth16
            val y = mCameraGoalAre.areaYRatio * (realHeight - 2 * blackHeight9) + blackHeight9
            val width = mCameraGoalAre.widthRatio * (realWidth - 2 * blackWidth16)
            val height = mCameraGoalAre.heightRatio * (realHeight - 2 * blackHeight9)
            when (mCameraGoalAre.status) {
                1 -> {
                    mIntelTrackView?.setPaintColor(Color.GREEN)
                    mIntelTrackView?.setRegRegion(x, y, width, height)
                }
                0 -> {
                    mIntelTrackView?.setPaintColor(Color.RED)
                }
                5 -> {
                    mIntelTrackView?.setPaintColor(Color.YELLOW)
                }
            }
            trackingDataMap.remove(timestamp)
            trackingDataMapSize -= 1
        }
    }

    private fun trimTrackingMap(t: Long) {
        for (l in trackingDataMap.keys) {
            if (l < t) {
                trackingDataMap.remove(l)
                trackingDataMapSize -= 1
            }
        }
    }


    private val ptsListener = object : OnRenderFrameInfoListener {

        override fun onRenderFrameTimestamp(l: Long) {
            Handler(Looper.getMainLooper()).post {
                streamPtsTime = l
                if (!ptsMap.containsValue(l)) {
                    ptsMap.put(System.currentTimeMillis(), l)
                } else {
                    for (time in ptsMap.keys) {
                        val currentTime = ptsMap[time]
                        if (currentTime == l) {
                            timeStampTv?.setTextColor(Color.GREEN)
                            timeStampTv?.text = "时间戳相差值->> " + (System.currentTimeMillis() - time) + " 丢帧数：" + mNumberOfPacketLoss
                            break
                        }
                    }
                    ptsMap.clear()
                }
            }
        }

        override fun onRenderFrameSizeChanged(width: Int, height: Int) {

        }
    }

    fun dealPointClickEvent(classType: Int) {
        when (camera) {
            is AutelXB015 -> {
                val target = TrackingTarget()
                target.xRatio = (mIntelTrackView!!.getRegLeft() - blackWidth16) / (realWidth - 2 * blackWidth16)
                target.yRatio = (mIntelTrackView!!.getRegTop() - blackHeight9) / (realHeight - 2 * blackHeight9)
                target.widthRatio = 0f//(mIntelTrackView!!.getRegRight() - mIntelTrackView!!.getRegLeft()) / (realWidth - 2 * blackWidth16)
                target.heightRatio = 0f // (mIntelTrackView!!.getRegBottom() - mIntelTrackView!!.getRegTop()) / (realHeight - 2 * blackHeight9)
                if (-1 != classType) {
//                    target.targetType = classType
                }
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(this@VisualActivity, "onPointClick success", Toast.LENGTH_SHORT).show()
                }
//                (xb015 as AutelXB015).setTrackingListener(target, object : CallbackWithOneParam<TrackingArea> {
//                    override fun onSuccess(data: TrackingArea) {
//                        mIntelTrackView?.setCanReceiveRegData(true)
//                        dealRecvData(data)
//                        if (!isFirst) {
//                            isFirst = true
//                            mAutelCodecView?.setOnRenderFrameInfoListener(ptsListener)
//                        }
//                    }
//
//                    override fun onFailure(error: AutelError) {
//                        mIntelTrackView!!.clearForStop()
//                    }
//                })
            }
        }
    }

    fun dealTrackGo() {
        when (camera) {
            is AutelXB015 -> {
                val target = TrackingTarget()
                target.xRatio = (mIntelTrackView!!.getRegLeft() - blackWidth16) / (realWidth - 2 * blackWidth16)
                target.yRatio = (mIntelTrackView!!.getRegTop() - blackHeight9) / (realHeight - 2 * blackHeight9)
                target.widthRatio = (mIntelTrackView!!.getRegRight() - mIntelTrackView!!.getRegLeft()) / (realWidth - 2 * blackWidth16)
                target.heightRatio = (mIntelTrackView!!.getRegBottom() - mIntelTrackView!!.getRegTop()) / (realHeight - 2 * blackHeight9)
//                target.targetType = 1
//                (xb015 as AutelXB015).setTrackingListener(target, object : CallbackWithOneParam<TrackingArea> {
//                    override fun onSuccess(data: TrackingArea) {
//                        mIntelTrackView?.setCanReceiveRegData(true)
//                        dealRecvData(data)
//                        if (!isFirst) {
//                            isFirst = true
//                            mAutelCodecView?.setOnRenderFrameInfoListener(ptsListener)
//                        }
//                    }
//
//                    override fun onFailure(error: AutelError) {
//                        mIntelTrackView!!.clearForStop()
//                    }
//                })
            }
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        CacheUtils.isFlag = false
       // AutelCodecView.setOnRenderFrameInfoListener(null)
        DspRFManager2.getInstance().setVideoRateInfoListener(null)
        DspRFManager2.getInstance().setReportBertInfoListener(null)
        DspRFManager2.getInstance().removeSignalStregthListener("setSignalStregthListener")
    }

    fun setMediaMode(mode: MediaMode) {
        xb015?.setMediaMode(mode, object : CallbackWithNoParam {
            override fun onSuccess() {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(this@VisualActivity, "setMediaMode " + mode + " success", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(error: AutelError?) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(this@VisualActivity, "setMediaMode " + mode + " failed", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun capture() {
        xb015?.startTakePhoto(object : CallbackWithNoParam {
            override fun onFailure(error: AutelError?) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(this@VisualActivity, "take capture failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onSuccess() {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(this@VisualActivity, "take capture success", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    fun stopRecording() {
        xb015?.stopRecordVideo(object : CallbackWithNoParam {
            override fun onSuccess() {
                Handler(Looper.getMainLooper()).post {
                    mRecordingTv?.setTextColor(Color.BLACK)
                    mRecordingTv?.text = "StartRecord"
                    Toast.makeText(this@VisualActivity, "stop recording success", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(error: AutelError?) {
                Toast.makeText(this@VisualActivity, "stop recording failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun startRecording() {
        xb015?.startRecordVideo(object : CallbackWithNoParam {
            override fun onSuccess() {
                Handler(Looper.getMainLooper()).post {
                    mRecordingTv?.setTextColor(Color.RED)
                    mRecordingTv?.text = "Recording..."
                    Toast.makeText(this@VisualActivity, "fire recording success", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(error: AutelError?) {
                Toast.makeText(this@VisualActivity, "fire recording failed", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
