# <a> BiometricKit for Android </a>

This library provides an easy way to implement fingerprint authentication without having to deal with all the boilerplate stuff going on inside. You can choose between <b>AndroidX BiometricPrompt</b> (if you want latest and greatest) or <b>FingerprintManagerCompat</b> (if you want additional customization)

This library wraps around <a href="https://developer.android.com/jetpack/androidx/releases/biometric">AndroidX Biometric 1.0.1</a> in case you want to use biometric dialog provided by Android system. It will use biometrics according to user's settings (it could be fingerprint, iris, etc), whatever manufacturer provided as SECURE. For example, on some devices FaceID is flagged as NOT SECURE by manufacturer (they can be tricked using your photo). However, Goodle didn't provide us with the prompt or dialog for devices with in-display fingerprint sensor (eg: OnePlus 6T, 7, 7T, Galaxy S10, S20, etc). AndroidX shows you fingerprint icon at the bottom of the display for such devices.

This is AndroidX Biometric Prompt looks:
<p><a href="https://github.com/Sultan1993/Android-BiometricKit/blob/master/screenshots/1.png" target="_blank"><img src="https://github.com/Sultan1993/Android-BiometricKit/blob/master/screenshots/1.png" width="250" style="max-width:100%;"></a></p>

This is AndroidX Biometric Prompt looks on devices with in-display fingerprint scanner:
<p><a href="https://github.com/Sultan1993/Android-BiometricKit/blob/master/screenshots/2.jpg" target="_blank"><img src="https://github.com/Sultan1993/Android-BiometricKit/blob/master/screenshots/2.jpg" width="250" style="max-width:100%;"></a></p>

In case you want full control over the biometrics prompt and have consistency across devices, please you <a href="https://developer.android.com/reference/android/support/v4/hardware/fingerprint/FingerprintManagerCompat">FingerprintManagerCompat</a> option. Although deprecated, it still has some advantages until AndroidX has minor problems with in-display sensors.

This is how FingerprintManagerCompat custom dialog looks (in case you obtained full source code of this library, you can customize this dialog as you want. It's just a subclass of Android Dialog):
<p><a href="https://github.com/Sultan1993/Android-BiometricKit/blob/master/screenshots/3.png" target="_blank"><img src="https://github.com/Sultan1993/Android-BiometricKit/blob/master/screenshots/3.png" width="250" style="max-width:100%;"></a></p>

## Requirements
- AndroidX artifacts
- You can use this library on from API16. However, biometric authentication is only supported from API23 (Marshmallow). Before API23 your dialog won't be displayed and you will get a callback

## Usage

1. Add jitpack.io to your project's build.gradle repositories:

```gradle
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

2. Add this line your app's gradle dependencies:

```gradle
dependencies {
	...
	implementation 'com.github.Sultan1993:Android-BiometricKit:1.0.1'
	...
}
```

Alternatively, you can downlaod the project and customize you dialog as you want. Download the source code and import it to your project. In case you want to import it as a module: 
1. Download the project
2. Click "Import module.."
3. Choose <b>biometrickit</b> folder
4. Add this line to your app's build gradle.

```gradle
dependencies {
	implementation project(":biometrickit")
}
```

#### Android X option

To create Android X Biometric Prompt you need to create <b>BiometricXBuilder</b> class instance. Here you define properties of the prompt. Most of the methods are self-explanatory, however, you want to read more, open to <a href="https://developer.android.com/reference/androidx/biometric/BiometricPrompt.PromptInfo.Builder?hl=ru">AndroidX BiometricPrompt documentation</a>

```kotlin
val biometricXManager = BiometricXManager.BiometricXBuilder(context)
		.setTitle("Dialog title. It's required")
		.setNegativeButtonText("Dialog dismiss button text. It's required")
		.setSubtitle("Dialog subtitle. It's optional")
		.setDescription("Description for user. It's optional")
        .setDeviceCredentialsAllowed(false)
		.setConfirmationRequired(false)
		.build()
```

To show your prompt in Activity, you have to call authenticate (see below for callback's definition):
```kotlin
biometricXManager.authenticate(activity, authenticationCallback, compatibilityCallback)
```

To show your prompt in Fragment:

```kotlin
biometricXManager.authenticate(fragment, authenticationCallback, compatibilityCallback)
```

#### FingerprintManagerCompat option

To create custom biometrics dialog you need to create <b>BiometricBuilder</b> class instance. Here you define properties of the dialog.

```kotlin
val biometricManager = BiometricManager.BiometricBuilder(this)
		.setTitle("Dialog title. It's required")
		.setNegativeButtonText("Dismiss button. It's required")
		.setSubtitle("Dialog subtitle. It' optional")

		//success text will be shown if auth succeeds
		.setSuccessText("Success authentication text. It's optional")

		//error title and subtitle will be shown if auth fails for some reason
		//if you don't provide them, system error text will be shown
		.setErrorTitle("Error authentication title. It's optional")
		.setErrorSubtitle("Error authentication subtitle. It's optional")
		.build()
```

To show your dialog:

```kotlin
biometricManager.authenticate(authenticationCallback, compatibilityCallback)
```

#### Authentication & Compatibility Callbacks

Both of them are interfaces which return callbacks. Authentication callback as it's name suggests returns all events corresponding to authentication. Similarly, Compatibility callback returns events for compatibility checks (eg: illegal arguments, SDK not supported, Fingerprint not available).


```kotlin
object : BiometricKit.AuthenticationCallback {

	override fun onAuthenticationFailed() {
		/*  
		*  Will be called if the fingerprint doesn’t match with any of the fingerprints registered on the device
		*/        
	}

	override fun onAuthenticationCancelled() {
        /*  
		*  Will be called if the authentication was cancelled by the user
		*/          
	}

	override fun onAuthenticationSuccessful() {
        /*  
		*  Will be called if the fingerprint has been successfully matched with one of the fingerprints in the device
		*/           
	}

	override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
		/*  
		*  This method is called when a non-fatal error has occurred during the authentication 
		* process. The callback will be provided with an help code to identify the cause of the 
		* error, along with a help message.
		*/         
	}

	override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
		/*  
		* When an unrecoverable error has been encountered and the authentication process has 
		* completed without success, then this callback will be triggered. The callback is provided 
		* with an error code to identify the cause of the error, along with the error message. 
		*/           
	}
}
```


```kotlin
object : BiometricKit.CompatibilityCallback {

	override fun onBiometricAuthenticationNotSupported() {
		/*  
		*  Will be called if the device SDK version does not support Biometric authentication or 
		*  if the device does not contain any fingerprint sensors 
		*/
	}

	override fun onBiometricAuthenticationNotAvailable() {
        /*  
		*  Will be called if the device does not have any biometrics registered in the device 
		*/  
	}

	override fun onBiometricAuthenticationPermissionNotGranted() {
        /*  
		*  Will be called if biometrics permissions were not granted to the app
		*/      
	}
}
```
