import {Camera} from 'expo-camera';
import {useWindowDimensions} from 'react-native';

export default function Cam(props) {

    const cameraDirection = props.cameraDirection;
    const handleCameraRef = props.handleCameraRef;

    const {width} = useWindowDimensions();
    const height = Math.round((width * 16) / 9);

    return (
            <Camera
            ratio="16:9"
            style={{
                height: height,
                width: "100%",
            }}
            type={cameraDirection}
            ref={handleCameraRef}
            ></Camera>
    )
}