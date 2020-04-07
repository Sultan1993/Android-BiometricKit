package kz.seidalin.biometrickit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kz.seidalin.biometrickit.android.BiometricManager
import kz.seidalin.biometrickit.androidx.BiometricXManager

class MainActivity : AppCompatActivity(), BiometricKit.CompatibilityCallback, BiometricKit.AuthenticationCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        authButton.setOnClickListener {
            val biometricManager = BiometricManager.BiometricBuilder(this)
                    .setTitle("Here goes title. It's required")
                    .setNegativeButtonText("Here goes dismiss button text. It's required")
                    .setSuccessText("Title after authentication succeeds. It's optional")
                    .setErrorTitle("Title after authentication fails. It's optional")
                    .setErrorSubtitle("Subtitle after authentication fails. It's optional")
                    .build()

            biometricManager.authenticate(this, this)
        }

        authXButton.setOnClickListener {
            val biometricXManager = BiometricXManager.BiometricXBuilder(this)
                    .setTitle("Here goes title. It's required")
                    .setSubtitle("Here goes subtitle. It's required")
                    .setNegativeButtonText("Here goes dismiss button text. It's required")
                    .setDescription("Description for user. It's optional")
                    //if true, system will ask for additional confirmation (pattern or system pin code)
                    //if false, you have to provide you own way of authentication (eg: in app pin code)
                    .setDeviceCredentialsAllowed(false)

                    .setConfirmationRequired(false)
                    .build()

            biometricXManager.authenticate(this, this, this)
        }
    }

    override fun onBiometricAuthenticationNotSupported() {

    }

    override fun onBiometricAuthenticationNotAvailable() {

    }

    override fun onBiometricAuthenticationPermissionNotGranted() {

    }

    override fun onAuthenticationFailed() {

    }

    override fun onAuthenticationCancelled() {

    }

    override fun onAuthenticationSuccessful() {

    }

    override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {

    }

    override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {

    }
}
