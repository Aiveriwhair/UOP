import React, {useState, useEffect} from 'react'
import { StyleSheet, Text, View, TouchableOpacity, ImageBackground} from 'react-native'
import { Camera, CameraType } from 'expo-camera'
import { useWindowDimensions } from "react-native";
import { StatusBar } from 'expo-status-bar';
import Cam from './Cam';
import Overlay from './Overlay';


const TAG = '[CAMERA]'
const VIDEO_DURATION = 10000; // Time in milliseconds

/*
  Component description :
  This component is used to record a video of a defined duration.
  The user can decide to record the video again or not.
  When the video is recorded and validated, the video is sent to the server.
*/

export default function RecordScreen({navigation}) {

  const [type, setType] = useState(CameraType.front);
  const [cameraRef, setCameraRef] = useState(null);

  const [video, setVideo] = useState(null);
  const [recording, setRecording] = useState(false);

  const [hasCameraPermission, setHasCameraPermission] = useState(false);
  const [hasMicPermission, setHasMicPermission] = useState(false);

  useEffect(() => {
    (async () => {
      const cameraStatus = await Camera.requestCameraPermissionsAsync();
      setHasCameraPermission(cameraStatus.status === 'granted');
      const audioStatus = await Camera.requestMicrophonePermissionsAsync();
      setHasMicPermission(audioStatus.status === 'granted');
      })();
    }, []);

  const handleShotPress = async () => {
    if (recording) {
      setRecording(false);
      await cancelRecording();
    } else {
      setRecording(true);
      await startRecording();
      await sendVideoToServer(video);
    }
  };

  const handleQuitRecorder = () => {
    // TODO: Handle quit recorder
    // Should navigate to the previous page
    navigation.navigate('LoginScreen')
  };

  const flipCamera = () => {
    setType(
      type === CameraType.front
        ? CameraType.back
        : CameraType.front
    );
  };


  const startRecording = async () => {
    if(cameraRef){
        const data = await cameraRef.recordAsync({
            maxDuration: VIDEO_DURATION / 1000,
        });
        setRecording(false);
        console.log(data.uri);
        setVideo(data.uri);
    }
  }

  const cancelRecording = async () => {
      cameraRef.stopRecording();
  };
  if (hasCameraPermission === null || hasMicPermission === null ) {
    return <View />;
  }
  if (hasCameraPermission === false || hasMicPermission === false) {
    return <Text>No access to camera</Text>;
  }

  const sendVideoToServer = async (video) => {
    try {
      console.log(TAG, 'Sending video to server')
      // TODO: Send video to server
      // https://axios-http.com/docs/multipart
      // TODO: Handle response
    } catch (error) {
      // TODO: Handle error
    }
  };


  return (
    <View style={styles.container}>
      <StatusBar translucent={true} style="light" />
      <Cam
          cameraDirection={type}
          handleCameraRef={setCameraRef} 
      />
      <Overlay
        handleShotPress={handleShotPress}
        recording={recording}
        handleFlipCamera={flipCamera}
        handleQuitRecorder={handleQuitRecorder}
      />
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: 'black',
  },
});
