package kz.seidalin.biometrickit.androidx

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import kz.seidalin.biometrickit.BiometricKit
import java.util.concurrent.Executors

class BiometricXManager(private val biometricBuilder: BiometricXBuilder) {

    private var context: Context
    private var biometricManager: BiometricManager
    private val executor = Executors.newSingleThreadExecutor()

    private lateinit var biometricXPrompt: BiometricXPrompt

    init {
        this.context = biometricBuilder.context
        this.biometricManager = BiometricManager.from(context)
    }

    fun authenticate(
        fragment: Fragment,
        authenticationCallback: BiometricKit.AuthenticationCallback,
        biometricCallback: BiometricKit.CompatibilityCallback? = null
    ) {
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> auth(fragment, authenticationCallback)
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> biometricCallback?.onBiometricAuthenticationNotSupported()
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> biometricCallback?.onBiometricAuthenticationNotAvailable()
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> biometricCallback?.onBiometricAuthenticationNotAvailable()
        }
    }

    fun authenticate(
        fragmentActivity: FragmentActivity,
        authenticationCallback: BiometricKit.AuthenticationCallback,
        biometricCallback: BiometricKit.CompatibilityCallback? = null
    ) {
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> auth(fragmentActivity, authenticationCallback)
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> biometricCallback?.onBiometricAuthenticationNotSupported()
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> biometricCallback?.onBiometricAuthenticationNotAvailable()
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> biometricCallback?.onBiometricAuthenticationNotAvailable()
        }
    }

    fun cancelAuthentication() {
        if (::biometricXPrompt.isInitialized) {
            biometricXPrompt.cancelAuthentication()
        }
    }

    private fun auth(
        fragment: Fragment,
        authenticationCallback: BiometricKit.AuthenticationCallback
    ) {
        biometricXPrompt = BiometricXPrompt(fragment, executor, authenticationCallback)
        displayBiometricPrompt()
    }

    private fun auth(
        fragmentActivity: FragmentActivity,
        authenticationCallback: BiometricKit.AuthenticationCallback
    ) {
        if (biometricBuilder.title == null) {
            throw IllegalArgumentException("BiometricPrompt: Title must be set and non-empty")
        }

        if (biometricBuilder.negativeButtonText == null) {
            throw IllegalArgumentException("BiometricPrompt: Negative button text must be set and non-empty")
        }

        biometricXPrompt = BiometricXPrompt(fragmentActivity, executor, authenticationCallback)
        displayBiometricPrompt()
    }

    private fun displayBiometricPrompt() {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(biometricBuilder.title.toString())
            .setSubtitle(biometricBuilder.subtitle)
            .setDescription(biometricBuilder.description)
            .setNegativeButtonText(biometricBuilder.negativeButtonText.toString())
            .setDeviceCredentialAllowed(biometricBuilder.deviceCredentialAllowed)
            .setConfirmationRequired(biometricBuilder.confirmationRequired)
            .build()

        biometricXPrompt.authenticate(promptInfo)
    }

    class BiometricXBuilder(val context: Context) {

        //required fields
        internal var title: String? = null
        internal var negativeButtonText: String? = null

        //optional fields
        internal var subtitle: String? = null
        internal var description: String? = null
        internal var deviceCredentialAllowed: Boolean = false
        internal var confirmationRequired: Boolean = false

        fun setTitle(title: String): BiometricXBuilder {
            this.title = title
            return this
        }

        fun setSubtitle(subtitle: String): BiometricXBuilder {
            this.subtitle = subtitle
            return this
        }

        fun setNegativeButtonText(negativeButtonText: String): BiometricXBuilder {
            this.negativeButtonText = negativeButtonText
            return this
        }

        fun setDescription(descriptionText: String): BiometricXBuilder {
            this.description = descriptionText
            return this
        }

        fun setDeviceCredentialsAllowed(deviceCredentialAllowed: Boolean): BiometricXBuilder {
            this.deviceCredentialAllowed = deviceCredentialAllowed
            return this
        }

        fun setConfirmationRequired(confirmationRequired: Boolean): BiometricXBuilder {
            this.confirmationRequired = confirmationRequired
            return this
        }

        fun build(): BiometricXManager {
            return BiometricXManager(this)
        }
    }
}
