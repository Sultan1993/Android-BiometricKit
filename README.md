# <a> BiometricKit for Android </a>

This library provides an easy way to implement fingerprint authentication without having to deal with all the boilerplate stuff going on inside. You can choose between AndroidX BiometricPrompt (if you want latest and greatest) or FingerprintManagerCompat (if you want additional customization)

This library wrappes around <a href="https://developer.android.com/jetpack/androidx/releases/biometric">AndroidX Biometric 1.0.1</a> in case you want to use biometric dialog provided by Android system. It will use biometrics according to user's settings (it could be fingerprint, iris, etc), whatever manufacturer provided as SECURE. For example, on some devices FaceID is flagged as NOT SECURE by manufacturer (they can be tricked using your photo). However, Goodle didn't provide us with the prompt or dialog for devices with in-display fingerprint sensor (eg: OnePlus 6T, 7, 7T, Galaxy S10, S20, etc). AndroidX shows you fingerprint icon at the bottom of the display for such devices.

//screenshots here

In case you want full control over the biometrics prompt and have consistency across devices, please you <a href="https://developer.android.com/reference/android/support/v4/hardware/fingerprint/FingerprintManagerCompat">FingerprintManagerCompat</a> option. Although deprecated, it still has some advantages until AndroidX has minor problems with in-display sensors.

//screen shots here

## Requirements
- AndroidX artifacts
- You can use this library on from API16. However, biometric authentication is only supported from API23 (Marshmallow). Before API23 your dialog won't be displayed and you will get a callback

## Usage

This library is super easy to use. Download it and import it to your project. If you want to import it as a module, just add this line to your app's build.gradle.

```gradle
dependencies {
    ...
    implementation project(":biometrickit")
    ...
}
```

