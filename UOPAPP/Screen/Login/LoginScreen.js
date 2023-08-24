import React, { useState } from "react";
import { Button, Text, View, StyleSheet, TextInput } from "react-native";
import UOPLogo from "../Components/UOPLogo";
import { Colors, Constants } from "../../constants/index";

const LoginScreen = ({ navigation }) => {
  const [id, setId] = useState("");
  const handleLogin = () => {
    //TODO
    navigation.navigate("HomeScreen");
  };
  return (
    <View style={styles.container}>
      <UOPLogo height={150} />
      <Text style={styles.title}>Login</Text>
      <TextInput
        style={styles.input}
        placeholder="Your patient ID"
        value={id}
        onChangeText={setId}
      />
      <Button title="Login" onPress={handleLogin} />
      <Text style={styles.noId}>
        If you don't have an ID, please contact the administrator
      </Text>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: "#FFFFFF",
    padding: 20,
  },
  title: {
    color: Colors.text,
    fontSize: 25,
    textAlign: "center",
    fontWeight: "bold",
  },
  noId: {
    color: Colors.text,
    fontSize: 15,
    textAlign: "center",
    position: "absolute",
    bottom: 20,
  },
  input: {
    width: "80%",
    height: 40,
    margin: 12,
    borderWidth: 1,
    padding: 10,
    borderRadius: 10,
    borderColor: Colors.dark_purple,
    color: Colors.text,
  },
});

export default LoginScreen;
