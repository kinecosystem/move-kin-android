package org.kinecosystem.appsdiscovery.view.customView

import org.kinecosystem.common.base.IBaseView

interface ITransferBarView : IBaseView{
    fun updateViews(transferInfo: TransferInfo)
    fun updateAmount(amount: Int)
    fun updateStatus(status: TransferBarView.TransferStatus)

}