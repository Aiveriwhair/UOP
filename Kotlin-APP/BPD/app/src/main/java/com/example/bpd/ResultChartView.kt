package com.example.bpd

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class ResultChartView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val dataPoints = mutableListOf<Pair<Long, Double>>() // Changed dataPoints type
    private val padding = 32f
    private var minX = 0f
    private var maxX = 0f
    private var minY = 0.0 // Changed minY type
    private var maxY = 0.0 // Changed maxY type

    private val axisPaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 4f
    }

    private val signalXDataPointPaint = Paint().apply {
        color = Color.RED // Changed color for signal X data points
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }

    private val signalYDataPointPaint = Paint().apply {
        color = Color.BLUE // Changed color for signal Y data points
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }

    fun setSignalData(signalX: LongArray, signalY: DoubleArray, rate: Double, totalDuration: Long) {
        dataPoints.clear()
        // dataPoints.addAll(signalX.zip(signalY))
        calculateBounds()
        invalidate()
    }

    private fun calculateBounds() {
        if (dataPoints.isNotEmpty()) {
            minX = dataPoints.minByOrNull { it.first }?.first?.toFloat() ?: 0f
            maxX = dataPoints.maxByOrNull { it.first }?.first?.toFloat() ?: 0f
            minY = dataPoints.minByOrNull { it.second }?.second ?: 0.0
            maxY = dataPoints.maxByOrNull { it.second }?.second ?: 0.0
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (dataPoints.isNotEmpty()) {
            val width = width.toFloat() - padding * 2
            val height = height.toFloat() - padding * 2

            val scaleX = width / (maxX - minX)
            val scaleY = height / (maxY - minY)

            val translatedPoints = dataPoints.map {
                Pair(
                    ((it.first - minX) * scaleX + padding),
                    height - ((it.second - minY) * scaleY) + padding
                )
            }

            // Draw signal X data points
            for (point in translatedPoints) {
                canvas.drawPoint(point.first, point.second.toFloat(), signalXDataPointPaint)
            }

            // Draw x-axis
            canvas.drawLine(padding, height + padding, width + padding, height + padding, axisPaint)

            // Draw y-axis
            canvas.drawLine(padding, padding, padding, height + padding, axisPaint)
        }
    }
}
