package org.kinecosystem.appsdiscovery.sender.discovery.view

import org.kinecosystem.appsdiscovery.base.IBaseView
import org.kinecosystem.appsdiscovery.sender.discovery.presenter.AppInfoPresenter
import org.kinecosystem.appsdiscovery.sender.model.EcosystemApp
import java.math.BigDecimal

interface IAmountChooserView : IBaseView{
    fun initViews(receiverAppIconUrl:String, balance:Int)
    fun sendKin(amount:Int)
    fun onCancel()
}