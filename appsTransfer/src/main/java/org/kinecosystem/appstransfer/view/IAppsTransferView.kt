package org.kinecosystem.appstransfer.view

import org.kinecosystem.common.base.IBaseView
import org.kinecosystem.transfer.model.EcosystemApp

interface IAppsTransferView : IBaseView {
    fun close()
    fun startTransferAmountActivity(app: EcosystemApp, receiverPublicAddress: String)
    fun invalidateList()
    fun showLoading()
    fun showData()
    fun showNoData()
    fun navigateToAppStore(url: String)
    fun onTransferError(appName: String)
    fun onCantFindReceiverInfo(appName: String)
}