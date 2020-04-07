package kz.seidalin.biometrickit.android

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.core.content.ContextCompat
import kz.seidalin.biometrickit.BiometricKit
import kz.seidalin.biometrickit.R

class BiometricDialog(
    context: Context,
    private val authenticationCallback: BiometricKit.AuthenticationCallback
) : Dialog(context, R.style.AlertTheme) {

    private lateinit var titleLabel: TextView
    private lateinit var subtitleLabel: TextView
    private lateinit var negativeButton: TextView
    private lateinit var descriptionLabel: TextView

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        window?.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.biometric_kit_bg_dialog))
        setDialogView()
    }

    private fun setDialogView() {
        val dialogView = layoutInflater.inflate(R.layout.biometric_kit_dialog_fingerprint, null)
        setContentView(dialogView)

        titleLabel = findViewById(R.id.titleLabel)
        subtitleLabel = findViewById(R.id.subtitleLabel)
        negativeButton = findViewById(R.id.negativeButton)
        descriptionLabel = findViewById(R.id.descriptionLabel)

        negativeButton.setOnClickListener {
            dismiss()
            authenticationCallback.onAuthenticationCancelled()
        }
    }

    fun setTitle(title: String?) {
        titleLabel.text = title
    }

    fun setSubtitle(subtitle: String?) {
        subtitleLabel.text = subtitle
    }

    fun setNegativeButtonText(negativeButtonText: String?) {
        negativeButton.text = negativeButtonText
    }

    fun setDescriptionText(descriptionText: String? = null) {
        if (descriptionText == null) {
            descriptionLabel.visibility = View.GONE
        } else {
            descriptionLabel.visibility = View.VISIBLE
            descriptionLabel.text = descriptionText
        }
    }

    fun setSuccessText(successText: String?) {
        if (successText != null) {
            titleLabel.text = successText
            subtitleLabel.visibility = View.GONE
            descriptionLabel.visibility = View.GONE
            negativeButton.visibility = View.GONE
        }
    }

    fun setErrorText(title: String?, subtitle: String?) {
        if (title != null) {
            titleLabel.text = title
        }

        if (subtitle != null) {
            subtitleLabel.text = subtitle
        }
    }
}
