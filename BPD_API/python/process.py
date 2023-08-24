import cv2
import numpy as np
from scipy.signal import find_peaks
import matplotlib.pyplot as plt
from scipy.signal import savgol_filter
import json
import datetime
from logger import Logger


modes = {
    '0': {
        "name": "breathing rate",
        "unit": "breaths per minute",
        "smoothing_window": 2,
        "peak_finding_distance": 2,
    },
    '1': {
        "name": "heart rate",
        "unit": "beats per minute",
        "smoothing_window": 0.2,
        "peak_finding_distance": 0.2,
    }
}

# NOTE : Ideally, the peak_finding_distance should be as small as possible.
#        For this to work, the signal has to be very smooth.


def videoProcessing(fps, data, mode):
    time = datetime.datetime.now()

    # Mode constants
    smoothing_window = int(modes[mode]["smoothing_window"] * fps)
    peak_finding_distance = modes[mode]["peak_finding_distance"] * fps


    results = []
    roi_count = len(data)

    for i in range(0, roi_count):
        best_signal = choose_channel_using_StandardDeviation(data[i])
        smoothed_signal = savgol_filter(best_signal, smoothing_window, 0)
        peaks, _ = find_peaks(smoothed_signal, distance=peak_finding_distance)
        # Compute the rate in bpm
        rate = len(peaks) * 60 / (len(smoothed_signal) / fps)
        results.append({"rate":rate, "peaks":peaks, "signal":smoothed_signal})
    
    Logger.info("PROCESSING", "Processing time: " + str(datetime.datetime.now() - time), "videoProcessing")
    return results



def choose_channel_using_StandardDeviation(data):
    b_signalAverage = [row[0] for row in data]
    g_signalAverage  = [row[1] for row in data]
    r_signalAverage  = [row[2] for row in data]

    b_std = np.std(b_signalAverage)
    g_std = np.std(g_signalAverage)
    r_std = np.std(r_signalAverage)


    if b_std > g_std and b_std > r_std:
        return b_signalAverage
    elif g_std > b_std and g_std > r_std:
        return g_signalAverage
    else:
        return r_signalAverage
    
