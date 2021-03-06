package org.kinecosystem.appstransfer.presenter

import org.kinecosystem.appstransfer.view.ITransferAmountView
import org.kinecosystem.common.base.IBasePresenter

interface ITransferAmountPresenter : IBasePresenter<ITransferAmountView> {
    fun onSendKinClicked()
    fun onCloseClicked()
    fun onResume()
    fun onPause()
    fun onDigitClicked(number: String)
    fun onZerosClicked()
    fun onDellClicked()
    fun resetAmount()
}