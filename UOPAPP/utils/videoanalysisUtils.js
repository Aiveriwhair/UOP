import { Constants } from "../constants/index";
import * as expoDP from "expo-document-picker";
import { Video } from 'expo-av';


// Returns the URI of the selected video
export async function pickVideo() {
    try {
        const result = await expoDP.getDocumentAsync({ type: 'video/*' });

        if (result.type === 'success')
            return result.uri;

    } catch (error) {
        console.log('Error selecting video:', error);
    }
    return null;
}


function getRange(processType, processResult){

    const ranges = Constants.ranges;
    let rangeName = null;

    function getRangeName(value, ranges){
        for(let range in ranges){
            if(value >= range.min && value <= range.max){
                return range.name;
            }
        }
        return null;
    }
    
    switch(processType){
        case 'breathingRate':
            const breahtingRanges = ranges.breathingRate;
            rangeName = getRangeName(processResult, breahtingRanges);
            break;
        case 'heartRate':
            const heartRanges = ranges.heartRate;
            rangeName = getRangeName(processResult, heartRanges);
            break;
        default:
            break;
    }

    return rangeName;
}


export const processVideo = async (videoURI) => {
  try {
    const videoObject = await loadVideo(videoURI);
    console.log('Sucessfuly loaded video:');

    // For each frame, process the image
    // const data = [];
    // frames.forEach(element => {
    //     const frameExtractedData = computeFrame(element);
    //     data.push(frameExtractedData);
    // });

    // return result;
  } catch (error) {
    console.log('Error during video processing:', error);
    throw error;
  }
};


export const loadVideo = async (videoUri) => {
  try {

    console.log('Loading video:', videoUri);

    // Initialize a new video object
    const videoObject = new Video();
    console.log('Video object created.')

    // Load the video
    await videoObject.loadAsync({ uri: videoUri }, {}, false);
    console.log('Video loaded.')

    // Set the video status to paused
    await videoObject.setStatusAsync({ shouldPlay: false });
    console.log('Video status set.')

    // Get the duration of the video in milliseconds
    const videoDurationMillis = videoObject.getDurationMillis();
    console.log('Video duration:', videoDurationMillis);

    // Get the number of frames per second
    const framesPerSecond = videoObject.getPreferredFrameRate();
    console.log('Video FPS:', framesPerSecond);

    // Get the size of the frame
    const frameSize = videoObject.getVideoWidth() + 'x' + videoObject.getVideoHeight();
    console.log('Video frame size:', frameSize);

    // Return an object with the video object and its properties
    return {
      videoObject,
      videoDurationMillis,
      framesPerSecond,
      frameSize,
    };
  } catch (error) {
    console.log('Error while loading video:', error);
    throw error;
  }
};


const computeFrame = async (frame) => {
};
