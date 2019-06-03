package org.kinecosystem.onewallet.model

import org.kinecosystem.common.base.LocalStore
import org.kinecosystem.onewallet.R

class OneWalletActionModel(private var localStore: LocalStore) {
    private val EXTRA_ACTION_TYPE = "EXTRA_ACTION_TYPE"
    private var errorCount = 0

    enum class Type(val textResId: Int, val styleResId: Int, val backgroundResId: Int) {
        LINK(R.string.btn_link_wallets, R.style.kinTextButtonRounded_Hollow,
                R.drawable.kin_button_rounded_hollow_bg),
        TOP_UP(R.string.btn_top_up, R.style.kinTextButtonRounded_Purple,
                R.drawable.kin_button_rounded_bg),
        UNDEFINED(-1, -1, -1)
    }

    var type: Type = Type.UNDEFINED
        set(value) {
            if (field != value) {
                field = value
                storeButtonType(value)
            }
        }
        get() {
            if (field == Type.UNDEFINED) {
                field = getButtonTypeFromStore()
            }
            return field
        }

    fun shouldTryAgain(): Boolean{
        val tryAgain = errorCount == 0
        errorCount ++
        return tryAgain
    }

    fun isLinkingButton(): Boolean {
        return type == Type.LINK
    }

    fun isTopupButton(): Boolean {
        return type == Type.TOP_UP
    }

    private fun getButtonTypeFromStore(): Type {
        val buttonTypeString = localStore.getString(EXTRA_ACTION_TYPE, Type.LINK.toString())

        return if (buttonTypeString == Type.LINK.toString())
            Type.LINK
        else Type.TOP_UP

    }

    private fun storeButtonType(type: Type) {
        localStore.updateString(EXTRA_ACTION_TYPE, type.toString())
    }
}
