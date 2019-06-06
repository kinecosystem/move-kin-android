package org.kinecosystem.transfer.sender.view

import org.kinecosystem.common.base.IBaseView

interface ITransferBarView : IBaseView{
    fun updateViews(transferInfo: TransferInfo)
    fun updateStatus(status: TransferBarView.TransferStatus)

}