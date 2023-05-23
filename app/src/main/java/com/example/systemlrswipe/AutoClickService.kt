package com.example.systemlrswipe

import android.Manifest
import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.content.pm.PackageManager
import android.graphics.Path
import android.os.Handler
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.MultiProcessor
import com.google.android.gms.vision.face.FaceDetector
import java.io.IOException

class AutoClickService : AccessibilityService() {


    override fun onCreate() {if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED
    ) {


        Toast.makeText(
            this,
            "Permission not granted!\n Grant permission and restart app",
            Toast.LENGTH_SHORT
        ).show()
    } else {
        init()
    }
    }



    var flag = false
    var cameraSource: CameraSource? = null

    private fun init() {
        flag = true
        initCameraSource()
    }


    private fun initCameraSource() {
        val detector = FaceDetector.Builder(this)
            .setTrackingEnabled(true)
            .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
            .setMode(FaceDetector.FAST_MODE)
            .build()


        detector.setProcessor(
            MultiProcessor.Builder( FaceTrackerDaemon(this@AutoClickService)).build()
        )

        val width = DisplayUtils.getDisplayWidth(this)
        val height = DisplayUtils.getDisplayHeight(this)
        cameraSource = CameraSource.Builder(this, detector)
            .setRequestedPreviewSize(height, width)
            .setFacing(CameraSource.CAMERA_FACING_FRONT)
            .setRequestedFps(30.0f)
            .build()

        try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {



                return
            }
            cameraSource?.start()
        } catch (e: IOException) {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (cameraSource != null) {
            cameraSource!!.release()
        }


    }

    fun update(condition: Condition) {
        Log.d("HAV", condition.toString())
        when (condition){
            Condition.LEFT -> {
                autoClick(1)
                Log.d("D","LEFT")
            }
            Condition.RIGHT -> {
                autoClick(-1)
                Log.d("D","RIGHT")
            }
            Condition.NF -> {

                Log.d("D","NF")
            }
            else -> {
                Log.d("D","NOTHING")}
        }
    }


    override fun onAccessibilityEvent(accessibilityEvent: AccessibilityEvent) {
    }



        private val handler = Handler()
        private lateinit var runnable: Runnable

        override fun onServiceConnected() {
            super.onServiceConnected()
//            autoClick(-1)
        }

    private fun startPeriodicTask() {
            runnable = Runnable {

                handler.postDelayed(runnable, 1000)

            }

            handler.post(runnable)
        }


        override fun onInterrupt() {
            handler.removeCallbacks(runnable)
        }

    fun autoClick(swipe: Int =-1) {
        Log.d("f",swipe.toString())

        dispatchGesture(gestureDescription(200, 200, swipe), null, null)
    }



    private fun gestureDescription(startTimeMs: Int, durationMs: Int, swipe: Int=1): GestureDescription {
        val path = Path()
        val width = DisplayUtils.getDisplayWidth(this)
        val height = DisplayUtils.getDisplayHeight(this)

        path.moveTo((width/2).toFloat(),(height/2).toFloat())
        path.lineTo((1+swipe)*(width/2).toFloat(),(height/2).toFloat())

        return createGestureDescription(
            StrokeDescription(
                path,
                startTimeMs.toLong(),
                durationMs.toLong()
            )
        )
    }

    private fun createGestureDescription(vararg strokes: StrokeDescription?): GestureDescription {
        val builder = GestureDescription.Builder()
        for (stroke in strokes) {
            builder.addStroke(stroke!!)
        }
        return builder.build()
    }






}