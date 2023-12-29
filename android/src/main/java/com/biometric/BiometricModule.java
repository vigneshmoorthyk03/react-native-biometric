package com.biometric;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;
import static android.provider.Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED;
import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.facebook.react.ReactActivity;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Executor;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class BiometricModule extends ReactContextBaseJavaModule {
  private static ReactApplicationContext reactContext;
  private Executor executor;
  String KEYSTORE_PROVIDER_ANDROID = "AndroidKeyStore";

  BiometricModule(ReactApplicationContext context) {
    super(context);
    reactContext = context;
    executor = ContextCompat.getMainExecutor(context);
  }

  BiometricPrompt.PromptInfo.Builder biometricDialogBuilder(String title, String subTitle) {
    //Show prompt dialog
    return new BiometricPrompt.PromptInfo.Builder()
      .setTitle(title)
      .setSubtitle(subTitle);
  }

  @ReactMethod
  public void enableBioMetric(String title, String subTitle, Callback callback) {
    BiometricPrompt biometricPrompt = new BiometricPrompt((ReactActivity) (Objects.requireNonNull(reactContext.getCurrentActivity())), executor, new BiometricPrompt.AuthenticationCallback() {
      @Override
      public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
        super.onAuthenticationError(errorCode, errString);
        if ( errorCode == 10 || errorCode == 13){
          callback.invoke("ERROR_USER_CANCELED");}
        // handle authentication error
        else if(errorCode == 11){
          callback.invoke("ERROR_NO_BIOMETRICS");
        }
      }


      @Override
      public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
        // handle authentication success here
        callback.invoke("BIOMETRICS_SUCCESS");
      }

      @Override
      public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
      }
    });
    try {
      new Handler(Looper.getMainLooper()).post(() -> {
        BiometricPrompt.PromptInfo.Builder promptInfo = biometricDialogBuilder(title,subTitle);
        promptInfo.setDeviceCredentialAllowed(true);
        promptInfo.setConfirmationRequired(true);
        biometricPrompt.authenticate(promptInfo.build());
      });

    }catch (Exception e){

    }
  }

  @ReactMethod
  public void checkBiometricEnrolledStatus(Callback callback) {
    int errorCode  = checkBiometricSupport(false);
    if (errorCode == -1 || errorCode == 11) {

      callback.invoke("TO_DISABLE_USE_BIOMETRICS");
    }
    else if(errorCode == 12){
      callback.invoke("T0_HIDE_BIOMETRICS");
    }
    else if (errorCode == 0) {
      callback.invoke("SUCCESS");
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  private Cipher createCipher() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
    BiometricManager biometricManager = BiometricManager.from(reactContext);
    int canAuth = biometricManager.canAuthenticate(BIOMETRIC_STRONG);
    Cipher cipher = null;
    if(canAuth == BiometricManager.BIOMETRIC_SUCCESS) {
      cipher = getCipher();
      SecretKey secretKey = getSecretKey();
      cipher.init(Cipher.ENCRYPT_MODE, secretKey);
    }
    return cipher;
  }

  private Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
    return Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
      + KeyProperties.BLOCK_MODE_CBC + "/"
      + KeyProperties.ENCRYPTION_PADDING_PKCS7);
  }
  private SecretKey generateSecretKey(KeyGenParameterSpec keyGenParameterSpec) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
    KeyGenerator keyGenerator = KeyGenerator.getInstance(
      KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      keyGenerator.init(keyGenParameterSpec);
    }
    return keyGenerator.generateKey();
  }
  @RequiresApi(api = Build.VERSION_CODES.M)
  private SecretKey getSecretKey() {
    try {
      KeyStore ks = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID);
      ks.load(null);
      SecretKey secretKey = (SecretKey) ks.getKey("BiometricNextExample", null);

      if (secretKey != null) {
        return secretKey;
      }

      return generateSecretKey(
        new KeyGenParameterSpec.Builder(
          "BiometricNextExample",
          KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
        )
          .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
          .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
          .setUserAuthenticationRequired(true)
          .build()
      );
    } catch (UnrecoverableKeyException | NoSuchAlgorithmException |
             InvalidAlgorithmParameterException | NoSuchProviderException |
             CertificateException | KeyStoreException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  @ReactMethod
  public void checkNewFingerPrintAdded(Callback callback) {
    runOnUiThread(() -> {
      try {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
          Cipher cipher = createCipher();
          callback.invoke("CONTINUE");
        } else {
          callback.invoke("BIOMETRIC_NOT_SUPPORTED");
        }
      } catch (NoSuchPaddingException | NoSuchAlgorithmException |
               InvalidKeyException e) {
        callback.invoke("NEW_FINGERPRINT_ADDED");
      }
    });
  }

  int checkBiometricSupport(Boolean enroll) {
    BiometricManager manager = BiometricManager.from(reactContext);
    int Bmcode = manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK | BIOMETRIC_STRONG);
    String status = "";
    switch (Bmcode) {
      case BiometricManager.BIOMETRIC_SUCCESS:
        status = "BIOMETRIC_SUCCESS";
        break;
      case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
        status = "BIOMETRIC_ERROR_NO_HARDWARE";
        break;
      case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
        status = "BIOMETRIC_ERROR_HW_UNAVAILABLE";
        break;
      case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
        status = "BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED";
        break;
      case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
        status = "BIOMETRIC_ERROR_NONE_ENROLLED";
        if (enroll)
          enrollUserToEnableCredential();
        break;
      case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:
        status = "BIOMETRIC_STATUS_UNKNOWN";
        if (enroll)
          enrollUserToEnableCredential();
        break;
      default:
        status = "BIOMETRIC_UNKNOWN";
    }
    return Bmcode;
  }

  void enrollUserToEnableCredential() {
    Intent intent;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
      intent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL).putExtra(EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, BIOMETRIC_STRONG);
    else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
      intent = new Intent(Settings.ACTION_FINGERPRINT_ENROLL);
    else intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M || Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    reactContext.startActivity(intent);

  }


  @NonNull
  @Override
  public String getName() {
    return "Biometric";
  }

}
