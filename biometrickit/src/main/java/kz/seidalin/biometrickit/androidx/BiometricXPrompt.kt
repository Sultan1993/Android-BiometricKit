package kz.seidalin.biometrickit.androidx

import androidx.biometric.BiometricPrompt
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import kz.seidalin.biometrickit.BiometricKit
import java.util.concurrent.Executor

class BiometricXPrompt: BiometricPrompt {

    constructor(
        fragment: Fragment,
        executor: Executor,
        authenticationCallback: BiometricKit.AuthenticationCallback
    ): super(fragment, executor, object : AuthenticationCallback() {

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            if (errorCode == ERROR_NEGATIVE_BUTTON) {
                authenticationCallback.onAuthenticationCancelled()
            } else {
                authenticationCallback.onAuthenticationError(errorCode, errString)
            }
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            authenticationCallback.onAuthenticationFailed()
        }

        override fun onAuthenticationSucceeded(result: AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            authenticationCallback.onAuthenticationSuccessful()
        }
    })

    constructor(
        fragmentActivity: FragmentActivity,
        executor: Executor,
        authenticationCallback: BiometricKit.AuthenticationCallback
    ): super(fragmentActivity, executor, object : AuthenticationCallback() {

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            if (errorCode == ERROR_NEGATIVE_BUTTON) {
                authenticationCallback.onAuthenticationCancelled()
            } else {
                authenticationCallback.onAuthenticationError(errorCode, errString)
            }
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            authenticationCallback.onAuthenticationFailed()
        }

        override fun onAuthenticationSucceeded(result: AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            authenticationCallback.onAuthenticationSuccessful()
        }
    })
}