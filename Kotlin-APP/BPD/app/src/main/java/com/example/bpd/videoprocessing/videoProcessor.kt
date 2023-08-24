package com.example.bpd.videoprocessing

import android.util.Log
import java.io.Serializable
import java.nio.DoubleBuffer
import java.util.Vector
import java.util.function.DoubleToLongFunction
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt


data class ROI(val width:Int, val height: Int, val x:Int, val y:Int)
data class FrameDescriptor(val averageRGB: List<Double>, val timeInVideo: Long)

class VideoProcessor() {

    private var frames: Vector<FrameDescriptor> = Vector()
    private lateinit var roi: ROI
    private var videoWidth: Int = 0
    private var videoHeight: Int = 0

    private var maxFramesNumber: Int = 1800


    constructor(maxFramesNumber: Int = 1800, roi: ROI) : this() {
        this.maxFramesNumber = maxFramesNumber
        this.roi = roi
    }

    fun addFrame(timeInVideo: Long, rgbBytes: ByteArray) {

        // If the max number of frames we can store in reached, delete one before adding the new one
        if (frames.size >= maxFramesNumber){
            frames.removeFirst()
        }

        val (averageRed, averageGreen, averageBlue) = calculateAverageColors(rgbBytes, roi)
        val frameDescriptor = FrameDescriptor(
            listOf(averageRed, averageGreen, averageBlue), timeInVideo
        )
        frames.add(frameDescriptor)
    }

    fun computeRate(): Double {
        val processingStartTime = System.currentTimeMillis()

        Log.i("VIDEO PROCESSOR", "Computing rate for ${frames.size} frames.")
        if(frames.size <= 0){
            throw Error("Rate processing needs at least 1 frame")
        }

        val bestChannelIndex = chooseChannelUsingSD()

        val rawSignal:List<Double> = frames.map { it.averageRGB[bestChannelIndex] }
        logSignal<Double>(rawSignal.toDoubleArray().toTypedArray(), "RAW SIGNAL")


        // Smooth signals for every ROI
        val smoothSignal: DoubleArray = SignalAveraging.movingAverage(rawSignal, 30).toDoubleArray()
        logSignal<Double>(smoothSignal.toTypedArray(), "SMOOTHED SIGNAL")

        // Find peaks
        val peaks: IntArray = findPeaks(smoothSignal)
        logSignal<Int>(peaks.toTypedArray(), "PEAKS")

        // compute rate
        val rate: Double = peaks.size / (getTotalVideoDuration() / 60000.0)


        // Measure elapsed time to compute the video
        val processingEndTime = System.currentTimeMillis()
        val elapsedTime = processingEndTime - processingStartTime

        Log.d(
            "VIDEO PROCESSOR",
            "Processing time : $elapsedTime \n${peaks.size} were found \nFinal rate is ${rate})"
        )

        return rate
    }

    private fun chooseChannelUsingSD(): Int {

            val rStd = standardDeviation(frames.map { it.averageRGB[0] })
            val gStd = standardDeviation(frames.map { it.averageRGB[1] })
            val bStd = standardDeviation(frames.map { it.averageRGB[2] })

            val bestChannelIndex = when {
                bStd > gStd && bStd > rStd -> 2 // Blue channel
                gStd > bStd && gStd > rStd -> 1 // Green channel
                else -> 0 // Red channel
            }

        return bestChannelIndex
    }

//    fun findPeakIndices(signal: DoubleArray, baseline: Double): List<Int> {
//        val smoothed = SignalAveraging.smoothSignal(signal)
//        val peakIndices = mutableListOf<Int>()
//        var peakIndex: Int? = null
//        var peakValue: Double? = null
//
//        for ((index, value) in smoothed.withIndex()) {
//            if (value > baseline) {
//                if (peakValue == null || value > peakValue) {
//                    peakIndex = index
//                    peakValue = value
//                }
//            } else if (value < baseline && peakIndex != null) {
//                peakIndices.add(peakIndex)
//                peakIndex = null
//                peakValue = null
//            }
//        }
//
//        if (peakIndex != null) {
//            peakIndices.add(peakIndex)
//        }
//
//        return peakIndices
//    }

    private fun standardDeviation(numArray: List<Double>): Double {
        var sum = 0.0
        var standardDeviation = 0.0

        for (num in numArray) {
            sum += num
        }

        val mean = sum / 10

        for (num in numArray) {
            standardDeviation += (num - mean).pow(2.0)
        }

        return sqrt(standardDeviation / 10)
    }

    private fun findPeaks(signal: DoubleArray): IntArray {
        val peaks = mutableListOf<Int>()

        for (i in 1 until signal.size - 1) {
            val derivativeSignChange = (signal[i] - signal[i - 1]) * (signal[i] - signal[i + 1]) < 0

            if (derivativeSignChange) {
                peaks.add(i)
            }
        }

        return peaks.toIntArray()
    }

    private fun calculateAverageColors(rgbBytes: ByteArray, roi: ROI): Triple<Double, Double, Double> {
        var totalRed = 0.0
        var totalGreen = 0.0
        var totalBlue = 0.0
        var pixelCount = 0

        val imageWidth = this.videoWidth
        val imageHeight = this.videoHeight

        for (row in roi.y until (roi.y + roi.height)) {
            if (row < 0 || row >= imageHeight) {
                continue
            }

            for (col in roi.x until (roi.x + roi.width)) {
                if (col < 0 || col >= imageWidth) {
                    continue
                }

                val pixelIndex = (row * imageWidth + col) * 3
                val r = rgbBytes[pixelIndex].toInt()
                val g = rgbBytes[pixelIndex + 1].toInt()
                val b = rgbBytes[pixelIndex + 2].toInt()
                Log.d("VIDEO PROCESSOR:", "$r $g $b")

                totalRed += r
                totalGreen += g
                totalBlue += b
                pixelCount++
            }
        }

        val averageRed = totalRed / pixelCount
        val averageGreen = totalGreen / pixelCount
        val averageBlue = totalBlue / pixelCount

        Log.d("VIDEO PROCESSOR:", "FRAME ${frames.size} Average Red: $averageRed, Average Green: $averageGreen, Average Blue: $averageBlue")


        return Triple(averageRed, averageGreen, averageBlue)
    }


    fun getFrameNumber(): Int {
        return frames.size
    }

    fun getTotalVideoDuration(): Long {
        return frames[frames.size - 1].timeInVideo - frames[0].timeInVideo
    }

    fun setROI(roi: ROI){
        this.roi = roi
    }
    fun setROIPos(x: Int, y: Int){
        this.roi = ROI(x, y, this.roi.width, this.roi.height)
    }
    fun setMaxFramesNumber(newMax: Int){
        this.maxFramesNumber = newMax
    }

    fun setVideoDimensions(videoWidth: Int, videoHeight: Int) {
        this.videoWidth = videoWidth
        this.videoHeight = videoHeight
    }

    fun <T> logSignal(signal: Array<T>, message: String){
        val signalString = signal.joinToString(", ")
        Log.d("VIDEO PROCESSING", "$message\n$signalString")
    }

}