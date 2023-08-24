// Import React and Component
import React, {useState, useEffect} from 'react';
import {
  ActivityIndicator,
  View,
  StyleSheet,
  Image,
} from 'react-native';

import UOPLogo from '../Components/UOPLogo';

import colors from '../../constants/colors';


import AsyncStorage from '@react-native-async-storage/async-storage';

const SplashScreen = ({navigation}) => {
  const [loading, setLoading] = useState(true);


  useEffect(() => {
    setTimeout(() => {
      setLoading(false);
      AsyncStorage.getItem('user_id').then((value) =>
        navigation.replace(
          value === null ? 'LoginScreen' : 'UserScreen'
        ),
      );
    }, 5000);
  }, []);

  return (
    <View style={styles.container}>
      <UOPLogo height={250} />
      <ActivityIndicator
        animating={loading}
        color={colors.dark_purple}
        size="large"
        style={styles.activityIndicator}
      />
    </View>
  );
};

export default SplashScreen;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#ffffff',
  },
  activityIndicator: {
  },
});