import React from 'react';
import { View, StyleSheet } from 'react-native';
import { SvgXml } from 'react-native-svg';

export default function FlipButton(props) {
  const svgContent = `
  <svg fill="#ffffff" height="800px" width="800px" version="1.1" id="Layer_1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" 
  viewBox="0 0 512 512" xml:space="preserve">
<g>
 <g>
   <polygon points="512,59.076 452.922,0 256,196.922 59.076,0 0,59.076 196.922,256 0,452.922 59.076,512 256,315.076 452.922,512 
     512,452.922 315.076,256 		"/>
 </g>
</g>
</svg>
  `;

  return (
    <View style={styles.container}>
      <SvgXml xml={svgContent} width={props.width} height={props.height} />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
});