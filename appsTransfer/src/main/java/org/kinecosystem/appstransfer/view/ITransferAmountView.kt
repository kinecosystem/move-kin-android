package org.kinecosystem.appstransfer.view

import org.kinecosystem.common.base.IBaseView

interface ITransferAmountView : IBaseView {
    fun close()
    fun updateBalance(balance: Int)
    fun updateIconUrl(url: String)
    fun updateAmount(amount:String)
    fun onStartSendingKin(amount:Int, appName: String, appIconUrl:String, appPackage:String)
    fun setSendEnable(isEnabled: Boolean)
}