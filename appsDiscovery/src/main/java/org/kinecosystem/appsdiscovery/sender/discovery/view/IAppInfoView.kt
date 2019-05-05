package org.kinecosystem.appsdiscovery.sender.discovery.view

import org.kinecosystem.appsdiscovery.base.IBaseView
import org.kinecosystem.appsdiscovery.sender.discovery.presenter.AppInfoPresenter
import org.kinecosystem.appsdiscovery.sender.model.EcosystemApp

interface IAppInfoView : IBaseView {
    fun initViews(app: EcosystemApp?)
    fun onRequestReceiverPublicAddressError(error: AppInfoPresenter.RequestReceiverPublicAddressError)
    fun onStartRequestReceiverPublicAddress()
    fun onRequestReceiverPublicAddressCanceled()
    fun startAmountChooserActivity(receiverAppIcon: String, balance: Int, requestCode: Int)
    fun onRequestAmountError()
    fun bindToSendService(identifier: String?)
    fun unbindToSendService()
    fun onServiceError(serviceNotFound: AppInfoPresenter.ServiceError)
    fun startSendKin(receiverAddress: String, amount: Int, memo: String, receiverPackage: String)
}