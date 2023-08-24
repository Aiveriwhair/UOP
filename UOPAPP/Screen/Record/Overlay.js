import {StyleSheet, TouchableOpacity, View} from 'react-native'
import FlipIcon from './FlipIcon'
import QuitIcon from './QuitIcon'
export default function Overlay(props){
    return(
        <View style={styles.container}>
            <TouchableOpacity style={styles.flipIcon} onPress={props.handleFlipCamera}>
                <FlipIcon 
                width={30}
                height={30}
                />
            </TouchableOpacity>
            <TouchableOpacity style={styles.shotButton} onPress={props.handleShotPress}>
                <View style={{width: 40, height: 40, borderRadius: 45, backgroundColor: props.recording ? 'red' : 'white'}}></View>
            </TouchableOpacity>
            <TouchableOpacity style={styles.quitIcon} onPress={props.handleQuitRecorder}>
                <QuitIcon 
                width={25}
                height={25}
                />
            </TouchableOpacity>
        </View>
    )
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        display: 'flex',
        justifyContent: 'space-evenly',
        alignItems: 'center',
        flexDirection: 'row',
    },
    shotButton: {
        width: 60,
        height: 60,
        borderRadius: 50,
        backgroundColor: 'white',
        borderColor: 'grey',
        borderWidth: 2,
        display: 'flex',
        justifyContent: 'space-evenly',
        alignItems: 'center',
    },
    flipButton: {
    },
})