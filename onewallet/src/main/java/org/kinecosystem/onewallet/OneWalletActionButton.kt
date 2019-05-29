package org.kinecosystem.onewallet

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.widget.TextViewCompat
import android.util.AttributeSet
import android.widget.Button


/**
 * OneWallet action button
 * Action can be 'Link wallets' or 'Top up'
 */
class OneWalletActionButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        Button(context, attrs, defStyleAttr) {

    enum class Type {
        LINK, TOP_UP
    }

    var type: Type = Type.LINK
        set(value) {
            if (value == Type.LINK) {
                Type.LINK
                text = context.getText(R.string.btn_link_wallets)
                TextViewCompat.setTextAppearance(this, R.style.kinTextButtonRounded_Hollow)
                background = ContextCompat.getDrawable(context, R.drawable.kin_button_rounded_hollow_bg)
            } else if (value == Type.TOP_UP) {
                Type.TOP_UP
                text = context.getText(R.string.btn_top_up)
                TextViewCompat.setTextAppearance(this, R.style.kinTextButtonRounded_Purple)
                background = ContextCompat.getDrawable(context, R.drawable.kin_button_rounded_bg)
            }
        }


}