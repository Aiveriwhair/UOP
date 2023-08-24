const axios = require('axios');
const fs = require('fs');
const FormData = require('form-data');

const BASE_API_URL = 'http://localhost:3000/';
const BASE_VIDEO_PATH = './assets/';


function parseArgs(){
  // Parse url
  let url = process.argv[2];
  if (!url) {
    console.error('Missing url parameter');
    process.exit(1);
  }
  url = BASE_API_URL + url;

  // Parse video path
  let videoPath = process.argv[3];
  if (!videoPath) {
    console.error('Missing videoPath parameter');
    process.exit(1);
  }
  videoPath = BASE_VIDEO_PATH + videoPath;

  // Parse rois
  let roi = {};
  let roi_vals = process.argv[4].split(':');
  roi.x = parseInt(roi_vals[0]);
  roi.y = parseInt(roi_vals[1]);
  roi.width =  parseInt(roi_vals[2]);
  roi.height = parseInt(roi_vals[3]);
  let rois = [roi];
  if (!rois) {
    console.error('Missing rois parameter');
    process.exit(1);
  }

  // Parse mode
  let mode = parseInt(process.argv[5]);
  if (mode == undefined) {
    console.error('Missing mode parameter');
    process.exit(1);
  }
  return [url, videoPath, rois, mode];
}




async function uploadAndProcessVideo(apiRoute, videoPath, rois, mode) {
  try {
    const formdata = new FormData();
    formdata.append('video', fs.createReadStream(videoPath));
    formdata.append('rois', JSON.stringify(rois));
    formdata.append('mode', mode);

    const response = await axios.post(apiRoute, formdata, {
      headers: {
        ...formdata.getHeaders(),
      },
    });
    response.data.video = videoPath;
    console.log(response.data);
  } catch (error) {
    console.error('Une erreur s\'est produite :', error.message);
  }
}

const [url, videoPath, rois, mode] = parseArgs();
uploadAndProcessVideo(url, videoPath, rois, mode);
