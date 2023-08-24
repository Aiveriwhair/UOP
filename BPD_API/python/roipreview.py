import datetime
import cv2
import sys
from logger import Logger
import json
import os

def draw_roi_on_frame(frame, roi_coords):
    x, y, w, h = roi_coords

    # if ROI is too big, resize it
    if frame.shape[0] < y + h:
        h = frame.shape[0] - y
    if frame.shape[1] < x + w:
        w = frame.shape[1] - x
        

    cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 2) # Green, 2 pixels border

def main(video_path, roi_coords):
    cap = cv2.VideoCapture(video_path)

    if not cap.isOpened():
        Logger.error("CAPTURING", "Cannot open video file", "roiPreview")
        return

    ret, frame = cap.read()
    if not ret:
        Logger.error("CAPTURING", "Cannot read video file", "roiPreview")
        return
    draw_roi_on_frame(frame, roi_coords)

    cap.release()
    cv2.destroyAllWindows()
    Logger.info("ROI PREVIEW", "ROI capture released", "roiPreview")

    # Save the image in ./tempImage folder, and return the absolute path
    abs_path = os.path.abspath(os.path.join(os.path.dirname(__file__), '..', 'tempImage', f'{datetime.datetime.now()}.jpg'))
    Logger.info("ROI PREVIEW", f"ROI preview saved in ", "roiPreview")
    cv2.imwrite(abs_path, frame)

    Logger.debug("ROI PREVIEW", f"ROI preview saved to {abs_path}", "roiPreview")
    
    print(abs_path)


# # prints the size width x height of the video
# def main(videopath):
#     cap = cv2.VideoCapture(videopath)
#     if not cap.isOpened():
#         Logger.error("CAPTURING", "Cannot open video file", "videoSize")
#         return

#     width = cap.get(cv2.CAP_PROP_FRAME_WIDTH)
#     height = cap.get(cv2.CAP_PROP_FRAME_HEIGHT)
#     Logger.info("VIDEOSIZE", f"Video size is {width}px x {height}px", "videoSize")
#     cap.release()

#     output = {"width": width,"height": height}
#     Logger.debug("PROGRAM STATE", f"output: {json.dumps(output)}", "videoSize")
#     print(json.dumps(output))



def parse_roi(roi_str):
    roi_parts = roi_str.split(':')
    if len(roi_parts) != 4:
        raise ValueError("ROI must be in the format 'x:y:width:height'")
    
    x, y, width, height = map(int, roi_parts)
    return (x, y, width, height)

def parseargs():
    if len(sys.argv) < 2:
        Logger.error("PARSING", "No filepath provided", "roiPreview")
        return
    
    filepath = sys.argv[1]
    try:
        roi = parse_roi(sys.argv[2])
    except ValueError as e:
        Logger.error("PARSING", f"Invalid ROI argument: {sys.argv[2]}. {e}", "roiPreview")
    
    return (filepath, roi)

if __name__ == '__main__':
    Logger.debug("PROGRAM STATE", "Program start with args: " + str(sys.argv), "roiPreview")

    filepath, roi = parseargs()

    main(filepath, roi)
    Logger.debug("PROGRAM STATE", "Program complete --------------------------", "roiPreview")
    