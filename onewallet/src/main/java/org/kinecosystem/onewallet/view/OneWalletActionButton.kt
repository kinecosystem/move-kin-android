package org.kinecosystem.onewallet.view

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.widget.TextViewCompat
import android.util.AttributeSet
import android.widget.Button
import org.kinecosystem.onewallet.model.OneWalletActionModel


/**
 * OneWallet action button
 * Action can be 'Link wallets' or 'Top up'
 */
class OneWalletActionButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        Button(context, attrs, defStyleAttr) {

    fun update(model: OneWalletActionModel) {
        text = context.getText(model.type.textResId)
        TextViewCompat.setTextAppearance(this, model.type.styleResId)
        background = ContextCompat.getDrawable(context, model.type.backgroundResId)
    }
}