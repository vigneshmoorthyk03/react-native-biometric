#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(Biometric, NSObject)

RCT_EXTERN_METHOD(enableBioMetric:(NSString *)title subtitle:(NSString *)subtitle callback:(RCTResponseSenderBlock)callback)

RCT_EXTERN_METHOD(checkNewFingerPrintAdded:(RCTResponseSenderBlock)callback)


+ (BOOL)requiresMainQueueSetup
{
  return NO;
}

@end
