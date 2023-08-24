import { Image } from 'react-native';


const UOPLogo = (props) => {
    return (
        <Image
            source={require('../../assets/image/logo.jpeg')}
            style={{
                resizeMode: 'contain',
                height: props.height,
            }}
      />
    );
};

export default UOPLogo;