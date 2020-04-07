# <a> BiometricKit for Android </a>
<img src="https://img.shields.io/badge/API-23%2B-blue.svg?style=flat" style="max-width:100%;" alt="API" data-canonical-src="https://img.shields.io/badge/API-23%2B-blue.svg?style=flat" style="max-width:100%;">

This library provides an easy way to implement fingerprint authentication without having to deal with all the boilerplate stuff going on inside. You can choose between AndroidX BiometricPrompt (if you want latest and greatest) or FingerprintManagerCompat (if you want additional customization)

This library wrappes around <a href="https://developer.android.com/jetpack/androidx/releases/biometric">AndroidX Biometric 1.0.1</a> in case you want to use biometric dialog provided by Android system. However, Goodle didn't provide us with the prompt or dialog for devices with in-display fingerprint sensor (eg: OnePlus 6T, 7, 7T, Galaxy S10, S20, etc). AndroidX shows you fingerprint icon at the bottom of the display for such devices.

In case you want 

doesn't have  Additionally, this library provides <a href="https://developer.android.com/reference/android/support/v4/hardware/fingerprint/FingerprintManagerCompat">FingerprintManagerCompat</a> implementation, for cases where you need addtional customization, like you own-customized dialog. Although it's deprecated, it still has some advantages over AndroidX variant. 

