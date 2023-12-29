import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-biometric' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const Biometric = NativeModules.Biometric
  ? NativeModules.Biometric
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function enableBioMetric(title : string, subtitle: string, callback : (res:any)=>void) {
  return Biometric.enableBioMetric(title,subtitle,callback);
}

export function checkBiometricSupport(callback : (res : any) => void){
  return Biometric.checkBiometricEnrolledStatus(callback)
}

export function checkNewFingerPrintAdded(callback : (res : any)=>void){
  return Biometric.checkNewFingerPrintAdded(callback)
}
