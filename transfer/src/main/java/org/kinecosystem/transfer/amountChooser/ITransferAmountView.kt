package org.kinecosystem.transfer.amountChooser

import org.kinecosystem.common.base.IBaseView

interface ITransferAmountView : IBaseView {
    fun close()
    fun updateBalance(balance: Int)
    fun updateIconUrl(url: String)
    fun updateAmount(amount: String)
    fun enableSend(enable: Boolean)
}