package org.kinecosystem.appsdiscovery.sender.discovery.view

import org.kinecosystem.appsdiscovery.base.IBaseView
import org.kinecosystem.appsdiscovery.sender.discovery.presenter.AppInfoPresenter
import org.kinecosystem.appsdiscovery.sender.discovery.view.customView.AppStateView
import org.kinecosystem.appsdiscovery.sender.discovery.view.customView.TransferBarView
import org.kinecosystem.appsdiscovery.sender.model.EcosystemApp

interface IAppInfoView : IBaseView {
    fun initViews(app: EcosystemApp?)
    fun startAmountChooserActivity(receiverAppIcon: String, balance: Int, requestCode: Int)
    fun bindToSendService()
    fun unbindToSendService()
    fun startSendKin(receiverAddress: String, amount: Int, memo: String, receiverPackage: String)
    fun updateAppState(state: AppStateView.State)
    fun navigateTo(downloadUrl: String)
    fun updateTransferStatus(status: TransferBarView.TransferStatus)
}