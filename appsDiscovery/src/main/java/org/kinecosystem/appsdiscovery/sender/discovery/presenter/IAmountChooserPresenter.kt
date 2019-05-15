package org.kinecosystem.appsdiscovery.sender.discovery.presenter

import org.kinecosystem.common.base.IBasePresenter
import org.kinecosystem.appsdiscovery.sender.discovery.view.IAmountChooserView

interface IAmountChooserPresenter : IBasePresenter<IAmountChooserView> {
    fun onSendKinClicked()
    fun onXClicked()
}