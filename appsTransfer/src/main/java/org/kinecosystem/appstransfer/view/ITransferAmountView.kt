package org.kinecosystem.appstransfer.view

import org.kinecosystem.common.base.IBaseView

interface ITransferAmountView : IBaseView {
    fun close()
    fun updateBalance(balance: Int)
    fun updateIconUrl(url: String)
    //fun initTransfersInfo(transferInfo: TransferInfo)
    fun initTransfersInfo()

    fun startSendKin(receiverAddress: String, senderAppName:String, amount: Int, memo: String, receiverPackage: String)
}