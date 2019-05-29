package org.kinecosystem.onewallet

import android.content.Context
import android.util.AttributeSet
import android.widget.Button

/**
 * OneWallet progress bar
 */
class OneWalletProgressBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        Button(context, attrs, defStyleAttr) {


    init {
        text = "Linking status will be displayed here"
    }


}