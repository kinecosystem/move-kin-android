package org.kinecosystem.appsdiscovery.view.customView

import org.kinecosystem.common.base.IBaseView

interface ITransferBarView : IBaseView{
    fun updateViews(transferInfo: TransferInfo)
    fun updateStatus(status: TransferBarView.TransferStatus)

}