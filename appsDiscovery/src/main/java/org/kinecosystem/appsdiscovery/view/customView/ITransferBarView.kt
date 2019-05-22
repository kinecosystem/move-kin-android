package org.kinecosystem.appsdiscovery.view.customView

import org.kinecosystem.transfer.base.IBaseView

interface ITransferBarView : IBaseView{
    fun updateViews(transferInfo: TransferInfo)
    fun updateStatus(status: TransferBarView.TransferStatus)

}