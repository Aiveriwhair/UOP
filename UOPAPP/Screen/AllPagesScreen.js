import SplashScreen from "./Splash/SplashScreen";
import LoginScreen from "./Login/LoginScreen";
import RecordScreen from "./Record/RecordScreen";

import {NavigationContainer} from '@react-navigation/native';
import {createNativeStackNavigator} from '@react-navigation/native-stack';

const Stack = createNativeStackNavigator();

const AllPagesScreen = ({}) => {
    const showHeader = false;
    return (
        <NavigationContainer>
        <Stack.Navigator initialRouteName="RecordScreen">
            <Stack.Screen name="SplashScreen" component={SplashScreen} options={{headerShown: showHeader}} />
            <Stack.Screen name="LoginScreen" component={LoginScreen} options={{headerShown: showHeader}} />
            <Stack.Screen name="RecordScreen" component={RecordScreen} options={{headerShown: showHeader}} />
        </Stack.Navigator>
        </NavigationContainer>
    );
};

export default AllPagesScreen;