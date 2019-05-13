package org.kinecosystem.appsdiscovery.sender.discovery.presenter

import android.content.Context
import android.content.Intent
import org.kinecosystem.appsdiscovery.base.IBasePresenter
import org.kinecosystem.appsdiscovery.sender.discovery.view.IAppInfoView

interface IAppInfoPresenter : IBasePresenter<IAppInfoView> {
    fun onRequestReceiverPublicAddress()
    fun processResponse(requestCode: Int, resultCode: Int, data: Intent?)
    fun onStart()
    fun onDestroy()
    fun updateBalance(currentBalance: Int)
    fun onResume(context: Context)
    fun onActionButtonClicked()
}