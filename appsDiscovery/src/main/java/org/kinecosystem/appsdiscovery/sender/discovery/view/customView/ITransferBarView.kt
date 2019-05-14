package org.kinecosystem.appsdiscovery.sender.discovery.view.customView

import org.kinecosystem.appsdiscovery.base.IBaseView

interface ITransferBarView : IBaseView{
    fun hide()
    fun show()
    fun update(transferInfo: TransferInfo)
    fun update(status: TransferBarView.TransferStatus)

}