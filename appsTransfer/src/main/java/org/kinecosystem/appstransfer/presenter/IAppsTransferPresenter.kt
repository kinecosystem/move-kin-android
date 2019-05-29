package org.kinecosystem.appstransfer.presenter

import android.content.Intent
import org.kinecosystem.appstransfer.view.IAppsTransferView
import org.kinecosystem.common.base.IBasePresenter

interface IAppsTransferPresenter : IBasePresenter<IAppsTransferView> {
    fun onCloseClicked()
    fun onResume()
    fun processResponse(requestCode: Int, resultCode: Int, intent: Intent?)
}