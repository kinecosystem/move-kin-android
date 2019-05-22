package org.kinecosystem.appsdiscovery.view

import org.kinecosystem.transfer.base.IBaseView
import org.kinecosystem.appsdiscovery.view.customView.AppStateView
import org.kinecosystem.appsdiscovery.view.customView.TransferBarView
import org.kinecosystem.appsdiscovery.view.customView.TransferInfo
import org.kinecosystem.appsdiscovery.model.EcosystemApp

interface IAppInfoView : IBaseView {
    fun initViews(app: EcosystemApp?)
    fun initTransfersInfo(transferInfo: TransferInfo)
    fun startAmountChooserActivity(receiverAppIcon: String, balance: Int, requestCode: Int)
    fun bindToSendService()
    fun unbindToSendService()
    fun startSendKin(receiverAddress: String, senderAppName:String, amount: Int, memo: String, receiverPackage: String)
    fun updateAppState(state: AppStateView.State)
    fun navigateTo(downloadUrl: String)
    fun updateTransferStatus(status: TransferBarView.TransferStatus)
    fun requestCurrentBalance()
}