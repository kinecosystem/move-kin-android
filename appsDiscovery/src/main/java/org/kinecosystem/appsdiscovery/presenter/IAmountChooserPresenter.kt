package org.kinecosystem.appsdiscovery.presenter

import org.kinecosystem.transfer.base.IBasePresenter
import org.kinecosystem.appsdiscovery.view.IAmountChooserView

interface IAmountChooserPresenter : IBasePresenter<IAmountChooserView> {
    fun onSendKinClicked()
    fun onXClicked()
}