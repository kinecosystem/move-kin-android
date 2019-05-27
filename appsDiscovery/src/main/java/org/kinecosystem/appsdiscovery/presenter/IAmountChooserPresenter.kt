package org.kinecosystem.appsdiscovery.presenter

import org.kinecosystem.common.base.IBasePresenter
import org.kinecosystem.appsdiscovery.view.IAmountChooserView

interface IAmountChooserPresenter : IBasePresenter<IAmountChooserView> {
    fun onSendKinClicked()
    fun onXClicked()
}