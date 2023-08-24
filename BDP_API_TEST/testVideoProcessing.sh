# "$ chmod +x testall.sh" to make this file executable
# "$ ./testall.sh" to run this file


# Testing HR upload performance
node videoprocessing.js "upload" "HR_30.mp4" "0:0:2000:2000" 1
node videoprocessing.js "upload" "HR_61.mp4" "0:0:2000:2000" 1
node videoprocessing.js "upload" "HR_94.mp4" "0:0:2000:2000" 1
node videoprocessing.js "upload" "HR_120.mp4" "0:0:2000:2000" 1

# Testing BR upload performance
node videoprocessing.js "upload" "BR_9.mp4" "175:350:100:100" 0
node videoprocessing.js "upload" "BR_12.mp4" "175:350:100:100" 0
node videoprocessing.js "upload" "BR_15.mp4" "175:350:100:100" 0

