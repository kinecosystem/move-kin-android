package org.kinecosystem.appstransfer.view.customview

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.widget.TextViewCompat
import android.util.AttributeSet
import android.widget.TextView
import org.kinecosystem.appstransfer.R
import org.kinecosystem.appstransfer.view.AppsTransferActivity

class AppsTransferButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        TextView(context, attrs, defStyleAttr) {

    init {
        text = resources.getString(R.string.apps_transfer_kin)
        TextViewCompat.setTextAppearance(this, R.style.KinTextButtonRounded_Hollow)
        background = ContextCompat.getDrawable(context, R.drawable.kin_button_rounded_hollow_drawable)
        val debugMode = getDebugModeAttribute(attrs)
        setOnClickListener {
            context.startActivity(AppsTransferActivity.getIntent(context, debugMode))
        }
    }


    private fun getDebugModeAttribute(attrs: AttributeSet?) :Boolean {
        var debug = false
        attrs?.let {
            context.theme.obtainStyledAttributes(
                    it,
                    R.styleable.AppsTransferButton,
                    0, 0).apply {

                try {
                    debug = getBoolean(R.styleable.AppsTransferButton_debug, false)
                } finally {
                    recycle()
                }
            }
        }
        return debug
    }
}