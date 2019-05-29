package org.kinecosystem.appstransfer.presenter

import org.kinecosystem.appstransfer.view.ITransferAmountView
import org.kinecosystem.appstransfer.view.customview.IAppsTransferListView
import org.kinecosystem.common.base.IBasePresenter
import org.kinecosystem.transfer.model.EcosystemApp

interface ITransferAmountPresenter : IBasePresenter<ITransferAmountView> {
    fun onSendKinClicked()
    fun onCloseClicked()
    fun onResume()
    fun onPause()
    fun startSendKin(receiverAddress: String, senderAppName: String, amount: Int, memo: String, receiverPackage: String)


    }