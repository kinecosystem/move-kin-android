package org.kinecosystem.appsdiscovery.view

import org.kinecosystem.transfer.base.IBaseView

interface IAmountChooserView : IBaseView{
    fun initViews(receiverAppIconUrl:String, balance:Int)
    fun sendKin(amount:Int)
    fun onCancel()
    fun setSendEnable(isEnabled: Boolean)
}