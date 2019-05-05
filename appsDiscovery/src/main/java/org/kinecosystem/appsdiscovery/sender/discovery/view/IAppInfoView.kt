package org.kinecosystem.appsdiscovery.sender.discovery.view

import org.kinecosystem.appsdiscovery.base.IBaseView
import org.kinecosystem.appsdiscovery.sender.discovery.presenter.AppInfoPresenter
import org.kinecosystem.appsdiscovery.sender.model.EcosystemApp

interface IAppInfoView : IBaseView{
    fun initViews(app:EcosystemApp?)
    fun onRequestReceiverPublicAddressError(error:AppInfoPresenter.ReqeustReceiverPublicAddressError)
    fun onStartRequestReceiverPublicAddress()
    fun onRequestReceiverPublicAddressCanceled()
    fun onStartAmountChooser()

    fun sendKin()
}