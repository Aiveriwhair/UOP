package com.example.bpd

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.graphics.drawable.Drawable
import android.media.Image
import android.os.Bundle
import android.os.CountDownTimer
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.bpd.videoprocessing.ROI
import com.example.bpd.videoprocessing.VideoProcessor
import java.io.ByteArrayOutputStream

class CameraActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private var showOverlay: Boolean = false // Default value

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var previewView: PreviewView

    // Overlay elements
    private lateinit var redDot: View
    private lateinit var processingTextView: TextView

    private lateinit var roi: ROI
    private lateinit var overlayView: View
    private var countDownTimer: CountDownTimer? = null
    private lateinit var countdownText: TextView

    // Video processor
    private lateinit var videoProcessor: VideoProcessor
    private lateinit var startAnalysisButton: ImageView
    private var isReadyForAnalysis: Boolean = false

    // IMAGE ANALYZER VARIABLES
    private var firstFrameTimestamp = 0L
    private var totalElapsed = 0L
    private var lastAnalyzedTimestamp = 0L
    private var lastProcessedRateTimestamp = 0L
    private var frameCounter = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)


        // LOAD THE SHARED PREFERENCES
        val sharedPref: SharedPreferences = getSharedPreferences(getString(R.string.settings_shared_pref), MODE_PRIVATE)

        val isOverlayEnabled = sharedPref.getBoolean(getString(R.string.settings_overlay_enabled), false)
        val roi1X = sharedPref.getInt(getString(R.string.settings_roi1_x), 180)
        val roi1Y = sharedPref.getInt(getString(R.string.settings_roi1_y), 320)
        val roi1Size = sharedPref.getInt(getString(R.string.settings_roi1_size), 100)
        this.roi = ROI(roi1Size, roi1Size, roi1X, roi1Y)
        overlayView = findViewById(R.id.roiOverlay)

        // RETRIEVE OVERLAY ELEMENTS FROM XML
        previewView = findViewById(R.id.preview)
        redDot = findViewById(R.id.redDot)
        startAnalysisButton = findViewById(R.id.startAnalysisButton)
        countdownText = findViewById(R.id.countdownText)
        processingTextView = findViewById(R.id.processingTextView)

        // DRAW THE ROI
        if(isOverlayEnabled) {
            val layoutParams = ConstraintLayout.LayoutParams(roi.width, roi.height)
            layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams.marginStart = roi.x
            layoutParams.topMargin = roi.y
            overlayView.layoutParams = layoutParams
            overlayView.visibility = View.VISIBLE
        }

        startAnalysisButton.setOnClickListener {
            videoProcessor = VideoProcessor(1800, roi)
            countDownTimer?.cancel()
            startAnalysisButton.visibility = View.INVISIBLE
            countdownText.visibility = View.VISIBLE
            countDownTimer = object : CountDownTimer(5000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val secondsRemaining = millisUntilFinished / 1000 + 1
                    countdownText.text = secondsRemaining.toString()
                }
                override fun onFinish() {
                    countdownText.visibility = View.INVISIBLE
                    redDot.visibility = View.VISIBLE
                    isReadyForAnalysis = true
                    processingTextView.visibility = View.VISIBLE
                }
            }.start()

            overlayView.setOnClickListener { event ->
                Log.i("CAMERA ACTIVITY", "Preview clicked")
                videoProcessor.setROIPos(event.x.toInt(), event.y.toInt())
                val layoutParams = ConstraintLayout.LayoutParams(roi.width, roi.height)
                layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                layoutParams.marginStart = roi.x
                layoutParams.topMargin = roi.y
                overlayView.layoutParams = layoutParams
            }
        }


        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, ImageAnalyzer())
                }

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer
                )

            } catch (exc: Exception) {
                // Handle camera binding exceptions
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        resetImageAnalyzer()
        cameraExecutor.shutdown()
    }


    private inner class ImageAnalyzer : ImageAnalysis.Analyzer {

        @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
        override fun analyze(imageProxy: ImageProxy) {

            if (!isReadyForAnalysis) {
                imageProxy.close()
                return
            }

            val currentTimestamp = System.currentTimeMillis()
            if (firstFrameTimestamp == 0L) {
                firstFrameTimestamp = currentTimestamp
                lastAnalyzedTimestamp = firstFrameTimestamp
                lastProcessedRateTimestamp = firstFrameTimestamp
                videoProcessor.setVideoDimensions(imageProxy.width, imageProxy.height)
                videoProcessor.addFrame(0L, getRGBfromYUV(imageProxy))
                frameCounter++
                imageProxy.close()
                return
            }



            // VIDEO RECORDING FINISHED
            if (currentTimestamp - firstFrameTimestamp >= 20000) {
                runOnUiThread {
                    processingTextView.visibility = View.INVISIBLE
                    redDot.visibility = View.INVISIBLE

                    val rate = videoProcessor.computeRate()


                    val intent = Intent(this@CameraActivity, ResultsActivity::class.java)
                    intent.putExtra("foundRate", rate)
                    startActivity(intent)
                    finish()
                    resetImageAnalyzer()
                }

                imageProxy.close()
                return
            }

            // OVERLAY UPDATES
            if (showOverlay && currentTimestamp - lastAnalyzedTimestamp >= 1000) {
                val framesPerSecond = frameCounter.toDouble()


                frameCounter = 0
                lastAnalyzedTimestamp = currentTimestamp
            }


            totalElapsed = currentTimestamp - firstFrameTimestamp
            videoProcessor.addFrame(totalElapsed, getRGBfromYUV(imageProxy))

            frameCounter++
            imageProxy.close()
        }
    }

    private fun getRGBfromYUV(image: ImageProxy): ByteArray {
        val planes = image.planes

        val height = image.height
        val width = image.width

        val yArr = planes[0].buffer
        val uArr = planes[1].buffer
        val vArr = planes[2].buffer

        val rgbBytes = ByteArray(height * width * 3)
        var rgbIndex = 0

        for (y in 0 until height) {
            for (x in 0 until width) {
                val yValue =
                    yArr.get(y * planes[0].rowStride + x * planes[0].pixelStride).toInt() and 0xFF
                val uValue =
                    uArr.get((y / 2) * planes[1].rowStride + (x / 2) * planes[1].pixelStride)
                        .toInt() and 0xFF - 128
                val vValue =
                    vArr.get((y / 2) * planes[2].rowStride + (x / 2) * planes[2].pixelStride)
                        .toInt() and 0xFF - 128

                val r = yValue + 1.370705 * vValue
                val g = yValue - 0.698001 * vValue - 0.337633 * uValue
                val b = yValue + 1.732446 * uValue

                rgbBytes[rgbIndex] = (clamp(r, 0, 255).toInt() and 0xFF).toByte()
                rgbBytes[rgbIndex + 1] = (clamp(g, 0, 255).toInt() and 0xFF).toByte()
                rgbBytes[rgbIndex + 2] = (clamp(b, 0, 255).toInt() and 0xFF).toByte()

                rgbIndex += 3
            }
        }
        return rgbBytes
    }
    fun bitmapToByteArray(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(format, quality, stream)
        return stream.toByteArray()
    }

    // Clamp function to ensure a value is within a range
    private fun clamp(value: Double, min: Int, max: Int): Double {
        return when {
            value < min -> min.toDouble()
            value > max -> max.toDouble()
            else -> value
        }
    }
    private fun resetImageAnalyzer() {
        countDownTimer?.cancel()
        isReadyForAnalysis = false

        firstFrameTimestamp = 0L
        totalElapsed = 0L
        lastAnalyzedTimestamp = 0L
        lastProcessedRateTimestamp = 0L
        frameCounter = 0

        redDot.visibility = View.INVISIBLE
        startAnalysisButton.visibility = View.VISIBLE
        countdownText.visibility = View.INVISIBLE
        processingTextView.visibility = View.INVISIBLE
    }


    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}
