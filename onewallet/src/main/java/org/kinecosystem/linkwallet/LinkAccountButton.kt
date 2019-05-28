package org.kinecosystem.linkwallet

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.TextView


class LinkAccountButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        Button(context, attrs, defStyleAttr) {

    init {
        text = "Link Account"
        // TODO create customized button
    }
}