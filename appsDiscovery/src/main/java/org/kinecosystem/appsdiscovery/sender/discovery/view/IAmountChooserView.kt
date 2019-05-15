package org.kinecosystem.appsdiscovery.sender.discovery.view

import org.kinecosystem.common.base.IBaseView

interface IAmountChooserView : IBaseView{
    fun initViews(receiverAppIconUrl:String, balance:Int)
    fun sendKin(amount:Int)
    fun onCancel()
    fun setSendEnable(isEnabled: Boolean)
}