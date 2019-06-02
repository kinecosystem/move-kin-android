package org.kinecosystem.onewallet.view

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import org.kinecosystem.onewallet.R

/**
 * OneWallet progress bar
 */
class LinkingProgressBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr) {

    private var bodyTextView: TextView
    private var progressImg: ImageView
    private val greenIcon: Drawable?
    private val purpleIcon: Drawable?
    var flipAnimation: ObjectAnimator? = null
    var greenPurpleStopped: Boolean = false

    enum class BodyTextRes(val bodyTextResId: Int, val colorResId: Int) {
        GREEN(R.string.progress_linking_wallets_body_g, R.color.light_green),
        PURPLE(R.string.progress_linking_wallets_body_p, R.color.purple)
    }

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.link_progress_bar, this, true) as ConstraintLayout
        bodyTextView = view.findViewById<TextView>(R.id.progress_body) as TextView
        progressImg = view.findViewById<ImageView>(R.id.progress_img) as ImageView
        visibility = View.INVISIBLE
        updateText(BodyTextRes.GREEN)
        greenIcon = ContextCompat.getDrawable(context, R.drawable.wallet_ic_g)
        purpleIcon = ContextCompat.getDrawable(context, R.drawable.wallet_ic_p)
    }

    private fun updateText(bodyTextRes: BodyTextRes) {
        bodyTextView.text = context.getText(bodyTextRes.bodyTextResId)
        bodyTextView.setTextColor(ContextCompat.getColor(context, bodyTextRes.colorResId))
    }

    fun startFlipAnimation(uiHandler: Handler) {
        val numOfFlips = 40
        val duration = 10000L
        flipAnimation = ObjectAnimator.ofFloat(progressImg, "rotationY", 0f, numOfFlips * 180f)
        flipAnimation?.duration = duration
        flipAnimation?.interpolator = LinearInterpolator()
        flipAnimation?.start()

        // animate between green / purple icons on every flip
        var wasGreenIcon = true
        greenPurpleStopped = false
        animateGreenBlueIcon(wasGreenIcon, duration / numOfFlips, uiHandler)

        // update text & text color once in the middle of the duration
        uiHandler.postDelayed({
            updateText(BodyTextRes.PURPLE)
        }, duration / 2)
    }

    private fun animateGreenBlueIcon(wasGreenIcon: Boolean, frequency: Long, uiHandler: Handler) {
        uiHandler.postDelayed({
            if (!greenPurpleStopped) {
                if (wasGreenIcon) {
                    progressImg.setImageDrawable(purpleIcon)
                } else {
                    progressImg.setImageDrawable(greenIcon)
                }
                animateGreenBlueIcon(!wasGreenIcon, frequency, uiHandler)
            }
        }, frequency)
    }

    fun stopFlipAnimation() {
        flipAnimation?.end()
        greenPurpleStopped = true
    }
}