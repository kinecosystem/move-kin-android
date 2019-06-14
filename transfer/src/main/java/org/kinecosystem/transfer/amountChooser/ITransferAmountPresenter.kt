package org.kinecosystem.transfer.amountChooser

import org.kinecosystem.common.base.IBasePresenter

interface ITransferAmountPresenter : IBasePresenter<ITransferAmountView> {
    fun onCloseClicked()
    fun onDigitClicked(number: String)
    fun onZerosClicked()
    fun onDellClicked()
    fun resetAmount()
    fun onBalanceReceived(balance: Int)
    fun onBalanceFailed()
    fun getKinAmount(): Int
}