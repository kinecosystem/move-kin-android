package org.kinecosystem.appsdiscovery.sender.discovery.presenter

import android.content.Intent
import org.kinecosystem.appsdiscovery.base.IBasePresenter
import org.kinecosystem.appsdiscovery.sender.discovery.view.IAppInfoView

interface IAppInfoPresenter : IBasePresenter<IAppInfoView> {
    fun onRequestReceiverPublicAddress()
    fun processResponse(requestCode: Int, resultCode: Int, data: Intent?)
    fun onServiceNotFound()
    fun onServiceShouldNotBeExported()
    fun onStart()
    fun onDestroy()
    fun updateBalance(currentBalance: Int)
}