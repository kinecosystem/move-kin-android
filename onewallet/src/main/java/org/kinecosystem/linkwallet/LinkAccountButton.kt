package org.kinecosystem.linkwallet

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.TextView


class LinkAccountButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        Button(context, attrs, defStyleAttr) {

    init {
        text = "Link Account" //resources.getString(R.string.explore_kin)
        //TextViewCompat.setTextAppearance(this, R.style.kinTextButtonRounded_Purple)
        //background = ContextCompat.getDrawable(context, R.drawable.kin_button_rounded_drawable)
    }
}