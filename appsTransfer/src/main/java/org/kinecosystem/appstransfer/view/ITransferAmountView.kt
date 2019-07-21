package org.kinecosystem.appstransfer.view

import org.kinecosystem.common.base.IBaseView
import org.devapp.transfercomponents.receiver.presenter.IErrorActionClickListener
import org.kinecosystem.transfer.sender.view.TransferBarView
import org.kinecosystem.transfer.sender.view.TransferInfo

interface ITransferAmountView : IBaseView {
    fun close()
    fun updateBalance(balance: Int)
    fun updateIconUrl(url: String)
    fun updateAmount(amount: String)
    fun updateTransferBar(status: TransferBarView.TransferStatus)
    fun initTransferBar(transferInfo: TransferInfo)
    fun enableSend(enable: Boolean)
    fun showErrorDialog(listener: org.devapp.transfercomponents.receiver.presenter.IErrorActionClickListener)
    fun notifyReceiverTransactionFailed(receiverPackageName: String, errorMessage: String, senderAddress: String, senderAppName: String, receiverAddress: String, amount: Int, transactionMemo: String)
    fun notifyReceiverTransactionSuccess(receiverPackageName: String, senderAddress: String, senderAppName: String, receiverAddress: String, amount: Int, transactionId: String, transactionMemo: String)
}