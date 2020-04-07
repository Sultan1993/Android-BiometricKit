package kz.seidalin.biometrickit


interface BiometricKit {

    interface CompatibilityCallback {
        fun onBiometricAuthenticationNotSupported()
        fun onBiometricAuthenticationNotAvailable()
        fun onBiometricAuthenticationPermissionNotGranted()
    }

    interface AuthenticationCallback {
        fun onAuthenticationFailed()
        fun onAuthenticationCancelled()
        fun onAuthenticationSuccessful()
        fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?)
        fun onAuthenticationError(errorCode: Int, errString: CharSequence?)
    }
}