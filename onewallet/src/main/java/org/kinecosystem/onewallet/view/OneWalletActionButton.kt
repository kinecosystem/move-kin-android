package org.kinecosystem.onewallet.view

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.widget.TextViewCompat
import android.util.AttributeSet
import android.util.Log
import android.widget.Button
import org.kinecosystem.onewallet.R
import org.kinecosystem.onewallet.model.OneWalletDataModel


/**
 * OneWallet action button
 * Action can be 'Link wallets' or 'Top up'
 */

enum class ButtonType(val textResId: Int, val styleResId: Int, val backgroundResId: Int) {
    LINK(R.string.btn_link_wallets, R.style.KinTextButtonRounded_Hollow,
            R.drawable.kin_button_rounded_hollow_bg),
    TOP_UP(R.string.btn_top_up, R.style.KinTextButtonRounded_Purple,
            R.drawable.kin_button_rounded_bg),
    UNDEFINED(-1, -1, -1)
}


class OneWalletActionButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        Button(context, attrs, defStyleAttr) {

    fun update(model: OneWalletDataModel) {
        val buttonType = if (model.isWalletLinked()) ButtonType.TOP_UP else ButtonType.LINK
        text = context.getText(buttonType.textResId)
        TextViewCompat.setTextAppearance(this, buttonType.styleResId)
        background = ContextCompat.getDrawable(context, buttonType.backgroundResId)
    }
}