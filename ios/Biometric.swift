import LocalAuthentication
@objc(Biometric)
class Biometric: NSObject {

    @objc func enableBioMetric(_ title: String,subtitle: String, callback: @escaping RCTResponseSenderBlock) -> Void {
            let localAuthenticationContext = LAContext()
            localAuthenticationContext.localizedFallbackTitle = title
            var authError: NSError?
            let reasonString = subtitle
            if localAuthenticationContext.canEvaluatePolicy(.deviceOwnerAuthentication, error: &authError) {
              
              localAuthenticationContext.evaluatePolicy(.deviceOwnerAuthentication, localizedReason: reasonString) { success, evaluateError in
                if success {
                    callback([5])
                } else {
                  guard let error = evaluateError else {
                    return
                  }
                  callback([error])
                }
              }
            } else {
                if let laError = authError as? LAError {
                    switch laError.code {
                    case .biometryNotAvailable:
                        callback([1])
                        break
                    case .biometryLockout: 
                        callback([2])
                        break
                    case .biometryNotEnrolled:
                        //
                        callback([3])
                        break
                    default:
                        callback([4])
                    }
                }
                callback([authError ?? "Something went wrong"])
            }
        }
    
        @objc func checkNewFingerPrintAdded(_ callback: RCTResponseSenderBlock) {
            var biometricAuth:String = ""
            let context = LAContext()
            if context.canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: nil) {
              if let domainState = context.evaluatedPolicyDomainState {
                let bData = domainState.base64EncodedData()
                if let decodedString = String(data: bData, encoding: .utf8) {
                  biometricAuth = decodedString
                }
              }
            }
            callback([biometricAuth])
          }
}
