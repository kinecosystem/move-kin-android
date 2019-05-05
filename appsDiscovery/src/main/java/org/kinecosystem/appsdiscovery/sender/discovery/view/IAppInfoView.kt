package org.kinecosystem.appsdiscovery.sender.discovery.view

import org.kinecosystem.appsdiscovery.base.IBaseView
import org.kinecosystem.appsdiscovery.sender.discovery.presenter.AppInfoPresenter
import org.kinecosystem.appsdiscovery.sender.model.EcosystemApp
import java.math.BigDecimal

interface IAppInfoView : IBaseView{
    fun initViews(app:EcosystemApp?)
    fun onRequestReceiverPublicAddressError(error:AppInfoPresenter.ReqeustReceiverPublicAddressError)
    fun onStartRequestReceiverPublicAddress()
    fun onRequestReceiverPublicAddressCanceled()
    fun startAmountChooserActivity(receiverAppIcon:String, balance:BigDecimal, requestCode:Int)
    fun onRequestAmountError()

    fun sendKin()
}