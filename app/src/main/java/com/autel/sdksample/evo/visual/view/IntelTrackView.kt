package com.autel.sdksample.evo.visual.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.autel.sdk.widget.AutelCodecView
import com.autel.sdksample.visual.utils.EdgeBoxOverlay

/**
 * Created by A13087 on 2017/7/24.
 */


class IntelTrackView : View {

    constructor(context: Context) : super(context) {
        initPath()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initPath()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initPath()
    }

    internal var regLeft: Float = 0f
    internal var regRight: Float = 0f
    internal var regTop: Float = 0f
    internal var regBottom: Float = 0f
    internal var currentX: Float = 0f
    internal var currentY: Float = 0f
    internal var lastX: Float = 0f
    internal var lastY: Float = 0f
    internal var state: Int = 0
    val FINGER_IDLE = 0
    val FINGER_MOVE = 1
    val FINGER_UP = 2
    internal var time: Long = 0
    internal var regionSelected = false  //是否已经选择区域，clearForStop()与ACTION_DOWN时置为false，ACTION_UP时如果所选区域符合要求置为true
    internal var canReceiveRegData = false //是否可以接收传来的位置信息，发送confirm成功后置为true，发送stop成功后置为false
    internal var regDataReceived = false  //是否接收到传来的位置信息，如果接收到数据并且canReceiveRegData为true则置为true，否则置为false

    private var callback: TrackCallback? = null

    private var color = Color.GREEN

    internal var paint = Paint()
    internal var path = Path()
    internal var shinePath1 = Path()
    internal var shinePath2 = Path()
    internal var shinePath3 = Path()
    internal var shinePath4 = Path()


    init {
        initPath()
    }

    fun getPaint(): Paint {
        return paint
    }

    /**
     * 初始化状态
     */
    private fun initPath() {
        edgeBoxOverlay = EdgeBoxOverlay(this)
        paint.strokeWidth = 5f
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true
        paint.color = Color.BLUE
        state = FINGER_IDLE
    }

    /**
     * 返回初始状态
     */
    fun resetState() {
        regBottom = 0f
        regTop = regBottom
        regRight = regTop
        regLeft = regRight
        path.reset()
        shinePath1.reset()
        shinePath2.reset()
        shinePath3.reset()
        shinePath4.reset()
        paint.color = Color.BLUE
        regionSelected = false
        regDataReceived = false
        canReceiveRegData = false
        state = FINGER_IDLE
    }

    fun setCanReceiveRegData(canRcv: Boolean) {
        canReceiveRegData = canRcv
        regDataReceived = canRcv
    }

    fun clearForStop() {
        resetState()
        invalidate()
    }

    fun getRegionSelected(): Boolean {
        return regionSelected
    }

    fun getRegLeft(): Float {
        return regLeft
    }

    fun getRegRight(): Float {
        return regRight
    }

    fun getRegTop(): Float {
        return regTop
    }

    fun getRegBottom(): Float {
        return regBottom
    }

    fun setPaintColor(color: Int) {
        if (this.color != color) {
            this.color = color
            invalidate()
        }
    }

    fun setRegRegion(x: Float, y: Float, width: Float, height: Float) {
        regLeft = x
        regTop = y
        regRight = regLeft + width
        regBottom = regTop + height
        if (canReceiveRegData) {
            regDataReceived = true
        }
        invalidate()
    }

    fun setEdgeBox(x: Float, y: Float, width: Float, height: Float) {
        regLeft = x
        regTop = y
        regRight = width
        regBottom = height
        if (canReceiveRegData) {
            regDataReceived = true
        }
        invalidate()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (regDataReceived) {
            paint.color = color
            canvas.drawRect(regLeft, regTop, regRight, regBottom, paint)
        } else {
            when (state) {
                FINGER_IDLE -> {
                }
                FINGER_MOVE -> canvas.drawPath(path, paint)
                FINGER_UP -> {
                    path.reset()
                    if (regionSelected) {
                        paint.color = Color.BLUE
                        canvas.drawRect(regLeft, regTop, regRight, regBottom, paint)
                    }
                    state = FINGER_IDLE
                }
            }
        }

    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (regDataReceived) {
            return true
        }
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isEdgeBox) {
                    mBitmap = mAutelCodecView?.bitmap
                    imageWidth = mBitmap?.width!!
                    imageHeight = mBitmap?.height!!
                    picture = IntArray(imageWidth * imageHeight)
                    mBitmap?.getPixels(picture, 0, imageWidth, 0, 0, imageWidth, imageHeight)
                }
                callback?.onFingerDown()
                resetState()
                regRight = ev.x
                regLeft = regRight
                regTop = ev.y
                regBottom = regTop
                lastX = ev.x
                currentX = lastX
                lastY = ev.y
                currentY = lastY
                path.moveTo(currentX, currentY)
            }
            MotionEvent.ACTION_MOVE -> {
                state = FINGER_MOVE
                regLeft = if (regLeft > ev.x) ev.x else regLeft
                regRight = if (regRight < ev.x) ev.x else regRight
                regTop = if (regTop > ev.y) ev.y else regTop
                regBottom = if (regBottom < ev.y) ev.y else regBottom
                currentX = ev.x
                currentY = ev.y
                if (Math.abs(currentX - lastX) > 3 || Math.abs(currentY - lastY) > 3) {
                    val midX = (currentX + lastX) / 2
                    val midY = (currentY + lastY) / 2
                    path.quadTo(midX, midY, currentX, currentY)
                } else {
                    path.lineTo(currentX, currentY)
                }
                lastX = currentX
                lastY = currentY
                val timeInterval = System.currentTimeMillis() - time
                if (timeInterval > 3) {
                    time = System.currentTimeMillis()
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                state = FINGER_UP
                if (regRight - regLeft < 20 || regBottom - regTop < 20) {
                    regionSelected = true
                    if (isEdgeBox) {//需要识别物体后
                        val x: Float = (regRight + regLeft) / 2
                        val y: Float = (regBottom + regTop) / 2
                        edgeBoxOverlay?.clickPointForTouch(picture!!, imageWidth, imageHeight, x, y)
                    } else {//直接点击下发
                        invalidate()
                        if (null != callback) {
                            callback?.onPointClick()
                        }
                    }
                } else {
                    if (isEdgeBox) {
                        val width = regRight - regLeft
                        val height = regBottom - regTop
                        val x_center = (regRight + regLeft) / 2.0f
                        val y_center = (regBottom + regTop) / 2.0f
                        if (width / height < 0.5f) {
                            regRight = x_center + height * 0.25f
                            regLeft = x_center - height * 0.25f
                        }
                        if (height / width < 0.5f) {
                            regTop = y_center - width * 0.25f
                            regBottom = y_center + width * 0.25f
                        }
                        if (regLeft < 1) regLeft = 1f
                        if (regTop < 1) regTop = 1f
                        if (regRight > imageWidth - 1) regRight = imageWidth.toFloat() - 1
                        if (regBottom > imageHeight - 1) regBottom = imageHeight.toFloat() - 1
                        edgeBoxOverlay?.classification(mBitmap!!, regLeft, regTop, regRight, regBottom)
                    }
                    regionSelected = true
                    invalidate()
                }
            }
            MotionEvent.ACTION_CANCEL -> {
            }
        }
        if(null != callback) {
            callback?.onDrawCompleted()
        }
        return true
    }
    fun setTrackCallback(callback : TrackCallback){
        this.callback = callback
    }

    interface TrackCallback {
        fun onFingerDown()
        fun onDrawCompleted()
        fun onPointClick()
    }


    //--------------------------识别 RUNNING------------------------
    var isEdgeBox: Boolean = false
    var edgeBoxOverlay: EdgeBoxOverlay? = null
    var picture: IntArray? = null
    var imageWidth: Int = 0
    var imageHeight: Int = 0
    var mAutelCodecView: AutelCodecView? = null
    var mBitmap: Bitmap? = null
    var mIdentifyCallback : IdentifyCallback? = null
    fun setCameraPreview(view: AutelCodecView) {
        this.mAutelCodecView = view
    }

    fun setIsEdgeBox(isFlag: Boolean) {
        this.isEdgeBox = isFlag
    }

    fun setIdentifyCallback(callback: IdentifyCallback){
        this.mIdentifyCallback = callback
        edgeBoxOverlay?.setIdentifyCallback(mIdentifyCallback!!)
    }

    interface IdentifyCallback {
        fun startIdentify()
        fun finishPointIdentify(classId: Int)
        fun finishDrawIdentify(classId: Int)
    }

    //--------------------------识别 END------------------------
}