import React, { useState } from 'react';
import { View, Text, TouchableOpacity, Alert } from 'react-native';
import { utils } from "../../utils/index";


export default function VideoAnalysis() {
  const [videoURI, setVideoURI] = useState("");

  const handlePickVideo = async () => {
    try {
      const uri = await utils.pickVideo();
      setVideoURI(uri);
    } catch (error) {
      console.log('Error during video picking:', error);
    }
  };

  const handleAnalyzeVideo = () => {
    if (videoURI) {
      utils.processVideo(videoURI);
    } else {
      Alert.alert('Please pick a video first.');
    }
  };

  return (
    <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
      <TouchableOpacity onPress={handlePickVideo} style={{ padding: 16, backgroundColor: 'green', marginBottom: 10 }}>
        <Text style={{ color: 'white' }}>
        {
          videoURI ? 'Change Video' : 'Pick Video'
        }
        </Text>
      </TouchableOpacity>
      {
        videoURI && <Text style={{ marginBottom: 10 }}>Video URI: {videoURI}</Text> || <Text style={{ marginBottom: 10 }}>No video selected</Text> 
      }

      <TouchableOpacity onPress={handleAnalyzeVideo} style={{ padding: 16, backgroundColor: 'blue' }}>
        <Text style={{ color: 'white' }}>Analyze Video</Text>
      </TouchableOpacity>
    </View>
  );
};

