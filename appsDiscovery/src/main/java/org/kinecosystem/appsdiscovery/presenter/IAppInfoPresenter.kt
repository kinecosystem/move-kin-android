package org.kinecosystem.appsdiscovery.presenter

import android.content.Context
import android.content.Intent
import org.kinecosystem.transfer.base.IBasePresenter
import org.kinecosystem.appsdiscovery.view.IAppInfoView

interface IAppInfoPresenter : IBasePresenter<IAppInfoView> {
    fun onRequestReceiverPublicAddress()
    fun processResponse(requestCode: Int, resultCode: Int, intent: Intent?)
    fun onStart()
    fun onDestroy()
    fun updateBalance(currentBalance: Int)
    fun onResume(context: Context)
    fun onActionButtonClicked()
    fun onTransferComplete()
    fun onTransferFailed()
}