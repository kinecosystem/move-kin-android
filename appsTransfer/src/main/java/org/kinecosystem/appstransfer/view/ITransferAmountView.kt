package org.kinecosystem.appstransfer.view

import org.kinecosystem.common.base.IBaseView
import org.kinecosystem.transfer.sender.view.TransferBarView
import org.kinecosystem.transfer.sender.view.TransferInfo

interface ITransferAmountView : IBaseView {
    fun close()
    fun updateBalance(balance: Int)
    fun updateIconUrl(url: String)
    fun updateAmount(amount: String)
    fun setSendEnable(isEnabled: Boolean)
    fun updateTransferBar(status: TransferBarView.TransferStatus)
    fun initTransferBar(transferInfo: TransferInfo)
    fun enableSend(enable:Boolean)
}