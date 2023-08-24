import cv2
import numpy as np
from scipy.signal import find_peaks
import matplotlib.pyplot as plt


def displayROI(frame, roi_size):
    x, y, w, h = roi_size
    cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 0, 255), thickness=2)


def displayData(data, fps):
    frame_counts = data[0]
    signal = data[1]
    plt.plot(frame_counts, signal, label="Blue")

    plt.xlabel("Frame Count")
    plt.ylabel("Average Value")
    plt.legend()

    plt.show()
