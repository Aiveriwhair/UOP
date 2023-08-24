const { execSync } = require('child_process');
const { modes, modesNames } = require('./constants.js');

class VideoProcessor {

    computeRates(videoUrl, rois, mode){
        for(let i = 0; i < rois.length; i++){
            if(rois[i].x < 0 || rois[i].y < 0 || rois[i].width < 0 || rois[i].height < 0){
                throw new Error("ROI coordinates must be positive.");
            }
        }
        
        
        let scriptableRois = ""
        for(const roi of rois){
            scriptableRois += `${roi.x}:${roi.y}:${roi.width}:${roi.height} `;
        }

        const command = `python python/videoprocessing.py ${videoUrl} ${mode} ${scriptableRois}`;
        let scriptResult = execSync(command).toString().trim();

        try{
            scriptResult = JSON.parse(scriptResult);
        }
        catch(err){
            throw new Error("Script result is not valid JSON.");
        }

        let results = [];
        for(let i  = 0 ; i < rois.length; i++){
            try {
                let generatedResult = this.generateResult(rois[i], scriptResult.fps, scriptResult.rates[i], scriptResult.duration, mode);
                results.push(generatedResult);
            }
            catch(err){
                throw err;
            }
        }
        return results;
    }

    computeRateClass(mode, rate){
        const rateEvaluationRanges = modes[mode];
        for(let key in rateEvaluationRanges){
            if(rate >= rateEvaluationRanges[key][0] && rate < rateEvaluationRanges[key][1]){
                return key;
            }
        }
        return "unknown";
    }

    generateResult(roi, fps, rate, duration, mode) {
        return {
            roi: roi,
            fps: fps,
            rate: rate,
            duration: duration,
            evaluationMode: modesNames[mode],
            rateClass: this.computeRateClass(mode, rate)
        }
    }

    previewRoiPosition(inputPath, x, y, w, h){
        const command = `python python/roipreview.py ${inputPath} ${x}:${y}:${w}:${h}`;
        // Script returns the image preview path
        let scriptOutput = execSync(command).toString().trim();
        return scriptOutput;
    }
}


module.exports = VideoProcessor;