import * as React from 'react';

import { StyleSheet, View, Text, Alert, Platform } from 'react-native';
import { enableBioMetric,  checkBiometricSupport, checkNewFingerPrintAdded} from 'react-native-biometric';

export default function App() {

  React.useEffect(() => {
    console.log("use effect")
    checkNewFingerPrintAdded((res:any)=>{
      // IOS - It will give new token if any new biometrics added in settings, otherwise it will give the same token.
      if(res==="NEW_FINGERPRINT_ADDED"){
        Alert.alert("Alert",res);
      }
    })
    if(Platform.OS === "ios"){
      enableBioMetric("Use passcode","Enter phone screen lock pattern, PIN, password or fingerprint",(res : any)=>{
        if(res == 1){
          Alert.alert("Alert","Biometric authentication not available on the device");
        }else if(res == 2){
          Alert.alert("Alert","Biometric authentication is locked due to too many failed attempts");
        }else if(res == 3){
          Alert.alert("Alert","Biometric authentication is not enrolled");
        }else if(res == 4){
          Alert.alert("Alert","BIOMETRIC_STATUS_UNKNOWN");
        }else if(res == 5){
          Alert.alert("Success","Verified successfully");
        }else{
          Alert.alert("Error",`${res}`);
        }
      })
      return
    }
    checkBiometricSupport((res:string)=>{
      if(res === "SUCCESS"){
        enableBioMetric("Bio metric ","Enter phone screen lock pattern, PIN, password or fingerprint",(res : any)=>{
          Alert.alert("Status",`${res}`);
        })
      }else{
        Alert.alert("Alert",res);
      }
    })
  }, []);

  return (
    <View style={styles.container}>
      <Text>Result</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
});