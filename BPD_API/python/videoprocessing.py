import cv2
import numpy as np
import matplotlib.pyplot as plt
from process import *
import json
import sys
import datetime


# Constants
maxVideoDuration = 60 # seconds
minVideoDuration = 8 # seconds



# Compute average pixel value for each channel in ROI of frame
def computeAverage(frame, roi_size):
    x, y, w, h = roi_size

    ## Image processing
    # crop image

    # resize roi if frame is too small
    if frame.shape[0] < y + h:
        h = frame.shape[0] - y
    if frame.shape[1] < x + w:
        w = frame.shape[1] - x

    roi = frame[y : y + h, x : x + w]
    # compute average pixel value for each channel
    b, g, r = cv2.split(roi)
    b_avg = np.average(b)
    g_avg = np.average(g)
    r_avg = np.average(r)

    return (b_avg, g_avg, r_avg)


# data 
# [
# [(avg_b, avg_g, avg_r), (avg_b, avg_g, avg_r), ...], # ROI 1
# [(avg_b, avg_g, avg_r), (avg_b, avg_g, avg_r), ...], # ROI 2
# ...
# ]

def videoCapturing(filepath, rois):
    time = datetime.datetime.now()

    cap = cv2.VideoCapture(filepath)
    if not cap.isOpened():
        Logger.error("CAPTURING", "Cannot open video file", "videoProcessing")
        return

    # NB: We consider that the whole video has the same frame rate as the first frame
    fps = cap.get(cv2.CAP_PROP_FPS)

    # Check video duration
    frame_count = int(cap.get(cv2.CAP_PROP_FRAME_COUNT))
    duration = frame_count / fps
    if duration > maxVideoDuration:
        Logger.error("CAPTURING", "Video duration is too long", "videoProcessing")
        return
    if duration < minVideoDuration:
        Logger.error("CAPTURING", "Video duration is too short", "videoProcessing")
        return
    
    Logger.info("CAPTURING", f"Video duration: {duration} seconds", "videoProcessing")
    Logger.info("CAPTURING", f"Video total frame: {cap.get(cv2.CAP_PROP_FRAME_COUNT)}", "videoProcessing")

    # Video capturing
    frame_count = 0
    data = []
    while True:
        ret, frame = cap.read()
        if not ret:
            break
        
        index = 0
        for roi in rois:
            if data.__len__() <= index:
                data.append([])
            data[index].append(computeAverage(frame, roi))
            index = index + 1 % rois.__len__()


        frame_count += 1
        videoDuration = cap.get(cv2.CAP_PROP_POS_MSEC)
        cv2.waitKey(1)

    cap.release()
    cv2.destroyAllWindows()

    Logger.info("CAPTURING", f"Video capturing complete in {datetime.datetime.now() - time} seconds for {len(rois)} ROIs", "videoProcessing")
    return (fps, data, videoDuration)



def parse_roi(roi_str):
    roi_parts = roi_str.split(':')
    if len(roi_parts) != 4:
        raise ValueError("ROI must be in the format 'x:y:width:height'")
    
    x, y, width, height = map(int, roi_parts)
    return (x, y, width, height)

def parseargs():
    if len(sys.argv) < 4:
        Logger.error("PARSING", "Missing arguments for the script to execute", "videoProcessing")
        return
    
    filepath = sys.argv[1]
    mode = sys.argv[2]
    rois = []
    for roi_arg in sys.argv[3:]:
        try:
            roi = parse_roi(roi_arg)
            rois.append(roi)
        except ValueError as e:
            Logger.error("PARSING", f"Invalid ROI argument: {roi_arg}. {e}", "videoProcessing")
    
    return (filepath, mode, rois)



def main():
    filepath, mode, rois = parseargs()
    if filepath is None:
        Logger.error("PARSING", "No filepath provided", "videoProcessing")
        return
    if mode is None:
        Logger.error("PARSING", "No mode provided", "videoProcessing")
        return
    if rois is None:
        Logger.error("PARSING", "No ROIs provided", "videoProcessing")
        return

    (fps, data, videoDuration) = videoCapturing(filepath, rois)
    Logger.debug("CAPTURING", "Video capturing complete", "videoProcessing")

    result = videoProcessing(fps, data, mode)
    Logger.debug("PROCESSING", "Data processing complete", "videoProcessing")
    

    Logger.save(datetime.datetime.now().__str__(), "videoprocessing", json.dumps(rois) , result)


    # Output for JS server
    rates = []
    for i in range(len(rois)):
        rates.append(result[i]["rate"])

    output = {
        "fps": fps,
        "duration": videoDuration,
        "rates": rates
    }

    print(json.dumps(output))



if __name__ == "__main__":
    Logger.debug("PROGRAM STATE", "Program start with args: " + str(sys.argv), "videoProcessing")
    main()
    Logger.debug("PROGRAM STATE", "Program complete --------------------------", "videoProcessing")
