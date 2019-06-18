package org.kinecosystem.onewallet.presenter

import org.kinecosystem.common.base.IBasePresenter
import org.kinecosystem.onewallet.view.UnifiedBalanceBar

interface IBalanceBarPresenter : IBasePresenter<UnifiedBalanceBar> {

    fun onBalanceReceived(amount: Int)

    fun show()

}