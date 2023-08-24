package com.example.bpd.videoprocessing

import kotlin.math.pow
import kotlin.math.sqrt

class SignalAveraging {
    companion object {
        private fun standardDeviation(numArray: DoubleArray): Double {
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

        private fun <T> List<T>.slidingWindow(size: Int): List<List<T>> {
            if (size < 1) {
                throw IllegalArgumentException("Size must be > 0, but is $size.")
            }
            return this.mapIndexed { index, _ ->
                this.subList(maxOf(index - size + 1, 0), index + 1)
            }
        }
        private fun sumTo(n: Int): Int = n * (n + 1) / 2

        private fun Iterable<Double>.weightedMean(): Double {
            val sum: Double = this
                .mapIndexed { index, t -> t * (index + 1) }
                .sum()
            return sum / sumTo(this.count())
        }
        public fun movingAverage(entries: List<Double>,
                                 window: Int = 3,
                                 averageCalc: Iterable<Double>.() -> Double = { weightedMean() })
        : List<Double> {
            val result = entries.slidingWindow(size = window)
                .filter { it.isNotEmpty() }
                .map { it -> it.averageCalc() }
                .toList()
            return result
        }

        fun smoothSignal(signal: DoubleArray): DoubleArray {
            val smoothed = DoubleArray(signal.size)

            for (index in 0 until signal.size) {
                val startIndex = maxOf(index - 2, 0)
                val endIndex = minOf(index + 2, signal.size - 1)
                smoothed[index] = signal.slice(startIndex..endIndex).average()
            }

            return smoothed
        }

    }


}