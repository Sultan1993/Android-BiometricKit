package kz.seidalin.biometrickit.android

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Handler
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.core.os.CancellationSignal
import kz.seidalin.biometrickit.BiometricUtils
import kz.seidalin.biometrickit.BiometricKit
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey

class BiometricManager(private val biometricBuilder: BiometricBuilder) {

    companion object {
        private val KEY_NAME = UUID.randomUUID().toString()
    }

    private var context: Context
    private var mCancellationSignal = CancellationSignal()

    private lateinit var cipher: Cipher
    private lateinit var keyStore: KeyStore
    private lateinit var keyGenerator: KeyGenerator
    private lateinit var cryptoObject: FingerprintManagerCompat.CryptoObject
    private lateinit var biometricDialog: BiometricDialog

    init {
        this.context = biometricBuilder.context
    }

    fun authenticate(
        authenticationCallback: BiometricKit.AuthenticationCallback,
        biometricCallback: BiometricKit.CompatibilityCallback? = null
    ) {
        if (biometricBuilder.title == null) {
            throw IllegalArgumentException("Biometric Dialog title cannot be null")
        }

        if (biometricBuilder.negativeButtonText == null) {
            throw IllegalArgumentException("Biometric Dialog negative button text cannot be null")
        }

        if (!BiometricUtils.isSdkVersionSupported) {
            biometricCallback?.onBiometricAuthenticationNotSupported()
            return
        }

        if (!BiometricUtils.isPermissionGranted(context)) {
            biometricCallback?.onBiometricAuthenticationPermissionNotGranted()
            return
        }

        if (!BiometricUtils.isHardwareSupported(context)) {
            biometricCallback?.onBiometricAuthenticationNotSupported()
            return
        }

        if (!BiometricUtils.isFingerprintAvailable(context)) {
            biometricCallback?.onBiometricAuthenticationNotAvailable()
            return
        }

        displayBiometricPrompt(authenticationCallback)
    }

    fun cancelAuthentication() {
        mCancellationSignal.cancel()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun displayBiometricPrompt(authenticationCallback: BiometricKit.AuthenticationCallback) {
        generateKey()

        mCancellationSignal.setOnCancelListener {
            dismissDialog()
        }

        if (initCipher()) {
            cryptoObject = FingerprintManagerCompat.CryptoObject(cipher)
            val fingerprintManagerCompat = FingerprintManagerCompat.from(context)

            fingerprintManagerCompat.authenticate(cryptoObject, 0, mCancellationSignal,
                object : FingerprintManagerCompat.AuthenticationCallback() {

                    override fun onAuthenticationError(errMsgId: Int, errString: CharSequence) {
                        super.onAuthenticationError(errMsgId, errString)
                        setErrorStatus(errString.toString())
                        authenticationCallback.onAuthenticationError(errMsgId, errString)
                    }

                    override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence) {
                        super.onAuthenticationHelp(helpMsgId, helpString)
                        setErrorStatus(helpString.toString())
                        authenticationCallback.onAuthenticationHelp(helpMsgId, helpString)
                    }

                    override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        setSuccessStatus()

                        Handler().postDelayed({
                            dismissDialog()
                            authenticationCallback.onAuthenticationSuccessful()
                        }, 1000)
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        setErrorStatus()
                        authenticationCallback.onAuthenticationFailed()
                    }
                }, null
            )

            displayBiometricDialog(authenticationCallback)
        }
    }

    private fun displayBiometricDialog(authenticationCallback: BiometricKit.AuthenticationCallback) {
        biometricDialog = BiometricDialog(context, authenticationCallback)
        biometricDialog.setTitle(biometricBuilder.title)
        biometricDialog.setSubtitle(biometricBuilder.subtitle)
        biometricDialog.setNegativeButtonText(biometricBuilder.negativeButtonText)
        biometricDialog.setDescriptionText(biometricBuilder.description)
        biometricDialog.show()
    }

    private fun dismissDialog() {
        biometricDialog.dismiss()
    }

    private fun setSuccessStatus() {
        biometricDialog.setSuccessText(biometricBuilder.successText)
    }

    private fun setErrorStatus(errorSubtitle: String? = null) {
        if (biometricBuilder.errorSubtitle != null) {
            biometricDialog.setErrorText(biometricBuilder.errorTitle, biometricBuilder.errorSubtitle)
        } else {
            biometricDialog.setErrorText(biometricBuilder.errorTitle, errorSubtitle)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            keyGenerator.init(
                KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build()
            )
            keyGenerator.generateKey()
        } catch (exc: KeyStoreException) {
            exc.printStackTrace()
        } catch (exc: NoSuchAlgorithmException) {
            exc.printStackTrace()
        } catch (exc: NoSuchProviderException) {
            exc.printStackTrace()
        } catch (exc: InvalidAlgorithmParameterException) {
            exc.printStackTrace()
        } catch (exc: CertificateException) {
            exc.printStackTrace()
        } catch (exc: IOException) {
            exc.printStackTrace()
        }
    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun initCipher(): Boolean {
        cipher = try {
            Cipher.getInstance(
                KeyProperties.KEY_ALGORITHM_AES + "/"
                        + KeyProperties.BLOCK_MODE_CBC + "/"
                        + KeyProperties.ENCRYPTION_PADDING_PKCS7)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to get Cipher", e)
        } catch (e: NoSuchPaddingException) {
            throw RuntimeException("Failed to get Cipher", e)
        }
        return try {
            keyStore.load(null)
            val key = keyStore.getKey(KEY_NAME, null) as SecretKey
            cipher.init(Cipher.ENCRYPT_MODE, key)
            true
        } catch (e: KeyPermanentlyInvalidatedException) {
            false
        } catch (e: KeyStoreException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: CertificateException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: UnrecoverableKeyException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: IOException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: InvalidKeyException) {
            throw RuntimeException("Failed to init Cipher", e)
        }
    }

    class BiometricBuilder(val context: Context) {

        //required fields
        internal var title: String? = null
        internal var negativeButtonText: String? = null

        //optional fields
        internal var subtitle: String? = null
        internal var description: String? = null
        internal var successText: String? = null
        internal var errorTitle: String? = null
        internal var errorSubtitle: String? = null

        fun setTitle(title: String): BiometricBuilder {
            this.title = title
            return this
        }

        fun setSubtitle(subtitle: String): BiometricBuilder {
            this.subtitle = subtitle
            return this
        }

        fun setDescription(descriptionText: String): BiometricBuilder {
            this.description = descriptionText
            return this
        }

        fun setNegativeButtonText(negativeButtonText: String): BiometricBuilder {
            this.negativeButtonText = negativeButtonText
            return this
        }

        fun setSuccessText(successText: String): BiometricBuilder {
            this.successText = successText
            return this
        }

        fun setErrorTitle(errorTitle: String): BiometricBuilder {
            this.errorTitle = errorTitle
            return this
        }

        fun setErrorSubtitle(errorSubtitle: String): BiometricBuilder {
            this.errorSubtitle = errorSubtitle
            return this
        }

        fun build(): BiometricManager {
            return BiometricManager(this)
        }
    }
}