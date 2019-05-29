package org.kinecosystem.appstransfer.view

import org.kinecosystem.common.base.IBaseView
import org.kinecosystem.transfer.model.EcosystemApp

interface ITransferAmountView : IBaseView {
    fun sendKin(amount:Int)
    fun onCancel()
    fun setSendEnable(isEnabled: Boolean)
}