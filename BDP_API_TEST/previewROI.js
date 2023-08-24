const axios = require('axios');
const fs = require('fs');
const FormData = require('form-data');


// PARAMETERS EXAMPLE
// The function should be called with the following parameters:
const API_URL = 'http://localhost:3000/previewROI';
const VIDEO_PATH = './assets/videotest.mp4';
const ROI = { x: 175, y: 350, width: 100, height: 100 }
const PREVIEW_SAVE_PATH = "./previewROI.jpg";


async function uploadVideoAndPreviewRoi(apiRoute, previewImagePath, roi, videoPath) {
  try {
    const formdata = new FormData();
    formdata.append('video', fs.createReadStream(videoPath));
    formdata.append('roi', JSON.stringify(roi));

    const response = await axios.post(apiRoute, formdata, {
      headers: {
        ...formdata.getHeaders(),
        'Accept': 'image/jpg',
      },
      responseType: 'arraybuffer',
    });

    // Save the binary image data to the preview image file
    fs.writeFileSync(previewImagePath, response.data);
    console.log('Preview image saved:', previewImagePath);
  } catch (error) {
    console.error('An error occurred:', error.message);
  }
}

uploadVideoAndPreviewRoi(API_URL, PREVIEW_SAVE_PATH, ROI, VIDEO_PATH);
