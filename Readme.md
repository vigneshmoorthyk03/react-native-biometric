# react-native-biometric-check

This package will be usefull to authenticate the user using biometric (Finger print, Face Id, default device authentication)

## Installation

```sh
npm install react-native-biometric-check
```

or

```sh
yarn add react-native-biometric-check
```

## Permission
```
For IOS Add this below permission in info.plist
  <key>NSFaceIDUsageDescription</key>
  <string>We use Face ID to secure your account.</string>

For Android Add this below line on AndroidManifest.xml 
For API 28 and above:

  <uses-permission android:name=" android.permission.USE_BIOMETRIC" />

Before API28:

  <uses-permission android:name="android.permission.USE_FINGERPRINT" />
  ```
  
## Usage


```js
import { enableBioMetric,  checkBiometricSupport, checkNewFingerPrintAdded} from 'react-native-biometric';

  useEffect(() => {
    checkNewFingerPrintAdded((res:any)=>{
      // IOS - It will give new token if any new biometrics added in settings, otherwise it will give the same token.
      // ANDROID - It will return CONTINUE,NEW_FINGERPRINT_ADDED and BIOMETRIC_NOT_SUPPORTED messages.
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
    //checkBiometricSupport - only for Android
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
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
