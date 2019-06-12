package org.kinecosystem.appstransfer.view.customview

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.widget.TextViewCompat
import android.util.AttributeSet
import android.widget.TextView
import org.kinecosystem.appstransfer.R
import org.kinecosystem.appstransfer.view.AppsTransferActivity

//public use
class AppsTransferButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        TextView(context, attrs, defStyleAttr) {

    private fun startAppsDiscoveryActivity() {
        context.startActivity(AppsTransferActivity.getIntent(context))
    }

    init {
        text = resources.getString(R.string.transfer_kin)
        TextViewCompat.setTextAppearance(this, R.style.KinTextButtonRounded_Hollow)
        background = ContextCompat.getDrawable(context, R.drawable.kin_button_rounded_hollow_drawable)
        setOnClickListener {
            startAppsDiscoveryActivity()
        }
    }
}