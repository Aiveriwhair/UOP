const path = require('path');

const port = 3000

const videoUploadDir = path.join(__dirname, '/uploads/');
const deteleVideoAfterProcessing = true

const breathingRateRanges = {
    low: [0, 12],
    normal: [12, 20],
    high: [20, 10000]
}
const heartRateRanges = {
    low: [0, 60],
    normal: [60, 100],
    high: [100, 10000]
}
const modes = {
    0: breathingRateRanges,
    1: heartRateRanges
}
const modesNames = {
    0: "breathing",
    1: "heart"
}


module.exports = {
    port,
    modes,
    modesNames,
    deteleVideoAfterProcessing,
    breathingRateRanges,
    heartRateRanges
}
