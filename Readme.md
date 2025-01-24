# react-native-biometric-check

## Introduction

The integration of biometric authentication, such as fingerprint and Face ID, adds a layer of security and convenience to mobile applications. This package streamlines the implementation process across iOS and Android platforms, ensuring a consistent user experience. By following the provided instructions, developers can easily leverage biometric authentication to enhance the security and usability of their applications. With tailored permissions and platform-specific logic, this solution caters to the diverse needs of modern mobile development.

## Permission


#### For iOS, please ensure that you add the following permission to your info.plist file:

```
<key>NSFaceIDUsageDescription</key>
<string>We use Face ID to secure your account.</string>

```

#### For Android, in your AndroidManifest.xml file, include the following permissions:

#### For API 28 and above:

```
<uses-permission android:name="android.permission.USE_BIOMETRIC" />

```

#### For versions before API 28:

```
<uses-permission android:name="android.permission.USE_FINGERPRINT" />

```


#### These configurations ensure proper permissions for using biometric authentication on both iOS and Android platforms.

### Usage

To use the provided code snippet with the `react-native-biometric-check` library, you'll need to follow these steps:

1. Install the library:
   ```
   npm i react-native-biometric-check 
   ```
   

2. Import the necessary functions and modules in your component:
   ```javascript
   import { enableBioMetric, checkBiometricSupport, checkNewFingerPrintAdded } from 'react-native-biometric-check';
   ```

3. Implement the authentication logic in your component, preferably within a `useEffect` hook:
   ```javascript
   useEffect(() => {
       checkNewFingerPrintAdded((res) => {
           if (res === "NEW_FINGERPRINT_ADDED") {
               Alert.alert("Alert", res);
           }
       });
       
       if (Platform.OS === "ios") {
           enableBioMetric("Use passcode", "Enter phone screen lock pattern, PIN, password or fingerprint", (res) => {
               switch (res) {
                   case 1:
                       Alert.alert("Alert", "Biometric authentication not available on the device");
                       break;
                   case 2:
                       Alert.alert("Alert", "Biometric authentication is locked due to too many failed attempts");
                       break;
                   case 3:
                       Alert.alert("Alert", "Biometric authentication is not enrolled");
                       break;
                   case 4:
                       Alert.alert("Alert", "BIOMETRIC_STATUS_UNKNOWN");
                       break;
                   case 5:
                       Alert.alert("Success", "Verified successfully");
                       break;
                   default:
                       Alert.alert("Error", `${res}`);
               }
           });
           return;
       }
       
       checkBiometricSupport((res) => {
           if (res === "SUCCESS") {
               enableBioMetric("Biometric", "Enter phone screen lock pattern, PIN, password or fingerprint", (res) => {
                   Alert.alert("Status", `${res}`);
               });
           } else {
               Alert.alert("Alert", res);
           }
       });
   }, []);
   ```

4. Ensure your `info.plist` for iOS and `AndroidManifest.xml` for Android are properly configured with the required permissions as mentioned earlier.

5. Test your application to verify the functionality of biometric authentication on both iOS and Android devices.

Remember to handle any errors or edge cases specific to your application's requirements.

### Notes

Certainly! Here's the combined explanation with all the points included:

1. **Usage of `checkNewFingerPrintAdded` Function**:
   - The `checkNewFingerPrintAdded` function is utilized to verify whether new biometric data has been added at the device level.
   - On Android, when new biometric data is detected, it returns a response string `"NEW_FINGERPRINT_ADDED"`.
   - Conversely, on iOS, it provides a different token or response to indicate the addition of new biometric data.

2. **Platform Check (iOS)**:
   - The code first checks if the platform is iOS using `Platform.OS === "ios"`.
   - If the platform is iOS, it proceeds to enable biometric authentication using the `enableBioMetric` function.
   - It provides a message to the user requesting them to use their phone's screen lock pattern, PIN, password, or fingerprint for authentication.

3. **Handling Response (iOS)**:
   - Upon receiving a response from the `enableBioMetric` function, it uses a switch statement to handle different cases.
   - Case 1: Indicates that biometric authentication is not available on the device.
   - Case 2: Indicates that biometric authentication is locked due to too many failed attempts.
   - Case 3: Indicates that biometric authentication is not enrolled (i.e., not set up).
   - Case 4: Indicates an unknown biometric status.
   - Case 5: Indicates successful verification.
   - Default case: Shows an error message with the received response.

4. **Return Statement (iOS)**:
   - After handling the iOS-specific biometric authentication, the `return;` statement ensures that the function exits early if the platform is iOS, as there's no need to proceed with the Android-specific logic.

5. **Biometric Support Check (Android)**:
   - If the platform is not iOS (presumed to be Android at this point), it checks for biometric support using the `checkBiometricSupport` function.
   - If biometric support is confirmed (`res === "SUCCESS"`), it proceeds to enable biometric authentication similarly to iOS.

6. **Handling Response (Android)**:
   - Upon receiving a response from the `enableBioMetric` function on Android, it shows the status of biometric verification in an alert dialog.

7. **Error Handling (Android)**:
   - If biometric support is not confirmed on Android, it shows an alert with the received response.

This combined explanation covers the usage of the `checkNewFingerPrintAdded` function and the implementation of biometric authentication for both iOS and Android platforms, including handling different scenarios and responses accordingly.

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)