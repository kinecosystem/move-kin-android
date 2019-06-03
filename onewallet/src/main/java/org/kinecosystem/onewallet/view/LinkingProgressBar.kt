package org.kinecosystem.onewallet.view

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import org.kinecosystem.onewallet.OneWalletClient
import org.kinecosystem.onewallet.R

/**
 * OneWallet progress bar
 */
class LinkingProgressBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr) {

    private var bodyTextView: TextView
    private var progressImg: ImageView
    private var tryAgainButton: TextView
    private var dismissButton: ImageView
    private var progressTitle: TextView
    private var completionTextView: TextView
    private val greenIcon: Drawable?
    private val purpleIcon: Drawable?
    private var flipAnimation: ObjectAnimator? = null
    private var greenPurpleStopped: Boolean = false
    private var startAnimationTime = 0L
    private val view: View
    private var barActionListener: LinkingBarActionListener? = null

    enum class BodyTextRes(val bodyTextResId: Int, val colorResId: Int) {
        LINKING_GREEN(R.string.progress_linking_wallets_body_g, R.color.light_green),
        LINKING_PURPLE(R.string.progress_linking_wallets_body_p, R.color.purple)
    }

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.link_progress_bar, this, true) as ConstraintLayout
        dismissButton = view.findViewById<ImageView>(R.id.progress_close_x)
        progressTitle = view.findViewById<TextView>(R.id.progress_title)
        tryAgainButton = view.findViewById<TextView>(R.id.progress_try_again)
        bodyTextView = view.findViewById<TextView>(R.id.progress_body) as TextView
        completionTextView = view.findViewById<TextView>(R.id.progress_completion_text) as TextView
        progressImg = view.findViewById<ImageView>(R.id.progress_img) as ImageView
        visibility = View.INVISIBLE
        greenIcon = ContextCompat.getDrawable(context, R.drawable.wallet_ic_g)
        purpleIcon = ContextCompat.getDrawable(context, R.drawable.wallet_ic_p)
    }

    fun setBarActionListener(listener: LinkingBarActionListener) {
        barActionListener = listener
    }

    fun startFlipAnimation() {
        initViews()
        startAnimationTime = System.currentTimeMillis()
        val numOfFlips = 30
        val duration = OneWalletClient.TIMEOUT_IN_MILLIS
        flipAnimation = ObjectAnimator.ofFloat(progressImg, "rotationY", 0f, numOfFlips * 180f)
        flipAnimation?.duration = duration
        flipAnimation?.interpolator = LinearInterpolator()
        flipAnimation?.start()

        // animate between green / purple icons on every flip
        greenPurpleStopped = false
        animateGreenPurpleIcon(true, duration / numOfFlips)

        // update text & text color once in the middle of the duration
        animateToPurpleText(duration)
    }

    private fun initViews() {
        progressTitle.visibility = View.VISIBLE
        bodyTextView.visibility = View.VISIBLE
        progressImg.visibility = View.VISIBLE
        dismissButton.visibility = View.GONE
        tryAgainButton.visibility = View.GONE
        completionTextView.visibility = View.GONE
        updateText(BodyTextRes.LINKING_GREEN)
        progressTitle.setText(R.string.progress_linking_wallets_title)
    }

    fun displaySuccess() {
        stopFlipAnimation()
        prepareCompletionLayout()
        addDismissButton()
        progressImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_linkwallets_success))
        completionTextView.setText(R.string.progress_linking_wallets_body_success)
        view.postDelayed({
            displayFinalScreen()
        }, 5000)
    }

    fun displayError(timeout: Boolean = false) {
        stopFlipAnimation()
        prepareCompletionLayout()
        addDismissButton()
        when {
            timeout -> completionTextView.setText(R.string.linking_error_timeout)
            else -> completionTextView.setText(R.string.linking_error_something_went_wrong)
        }
        progressImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.error_ic))
    }

    private fun prepareCompletionLayout() {
        progressTitle.visibility = View.GONE
        bodyTextView.visibility = View.GONE
        completionTextView.setTextColor(ContextCompat.getColor(context, R.color.dark))
        completionTextView.visibility = View.VISIBLE
    }

    private fun addDismissButton() {
        dismissButton.visibility = View.VISIBLE
        dismissButton.setOnClickListener({
            visibility = View.GONE
        })
    }

    private fun addTryAgainButton() {
        tryAgainButton.visibility = View.VISIBLE
        tryAgainButton.setOnClickListener({
            barActionListener?.onLinkingRetryClicked()
        })
    }

    fun stopFlipAnimation() {
        greenPurpleStopped = true
        flipAnimation?.end()
    }

    fun dismiss() {
        visibility = View.GONE
    }

    private fun animateToPurpleText(duration: Long) {
        view.postDelayed({
            if (!greenPurpleStopped) {
                updateText(BodyTextRes.LINKING_PURPLE)
            }
        }, duration / 2)
    }

    private fun animateGreenPurpleIcon(wasGreenIcon: Boolean, frequency: Long) {
        view.postDelayed({
            if (!greenPurpleStopped) {
                if (wasGreenIcon) {
                    progressImg.setImageDrawable(purpleIcon)
                } else {
                    progressImg.setImageDrawable(greenIcon)
                }
                animateGreenPurpleIcon(!wasGreenIcon, frequency)
            }
        }, frequency)
    }

    private fun displayFinalScreen() {
        completionTextView.text = context.getText(R.string.progress_linking_wallets_end)
        completionTextView.setTextColor(ContextCompat.getColor(context, R.color.purple))
        completionTextView.setOnClickListener({
            barActionListener?.onOpenKinitClicked()
        })
        progressImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.logo_kinit))
    }

    private fun updateText(bodyTextRes: BodyTextRes) {
        bodyTextView.text = context.getText(bodyTextRes.bodyTextResId)
        bodyTextView.setTextColor(ContextCompat.getColor(context, bodyTextRes.colorResId))
    }
}