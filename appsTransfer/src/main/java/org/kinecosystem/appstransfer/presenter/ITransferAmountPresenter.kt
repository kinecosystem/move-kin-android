package org.kinecosystem.appstransfer.presenter

import org.kinecosystem.appstransfer.view.ITransferAmountView
import org.kinecosystem.common.base.IBasePresenter

interface ITransferAmountPresenter : IBasePresenter<ITransferAmountView> {
    fun onCloseClicked()
    fun onResume()
}