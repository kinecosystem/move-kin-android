package org.kinecosystem.onewallet.presenter

import android.view.View
import org.kinecosystem.common.base.BasePresenter
import org.kinecosystem.onewallet.view.UnifiedBalanceBar


class BalanceBarPresenter : BasePresenter<UnifiedBalanceBar>(), IBalanceBarPresenter {


    override fun onBalanceReceived(amount: Int) {
        view?.updateBalance(amount)
    }

    override fun show() {
        view?.visibility = View.VISIBLE
    }
}