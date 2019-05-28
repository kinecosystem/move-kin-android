package org.kinecosystem.appstransfer.view

import org.kinecosystem.common.base.IBaseView
import org.kinecosystem.transfer.model.EcosystemApp

interface IAppsTransferView: IBaseView {
    fun close()
    fun startAmountChooserActivity(receiverAppIcon: String, balance: Int, requestCode: Int)
    fun invalidateList()
    fun showLoading()
    fun showData()
    fun showNoData()
    fun navigateToAppStore(url:String)
    fun transferToApp(app:EcosystemApp)
}