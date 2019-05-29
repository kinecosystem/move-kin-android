package org.kinecosystem.appstransfer.presenter

import org.kinecosystem.appstransfer.view.IAppsTransferView
import org.kinecosystem.appstransfer.view.customview.IAppsTransferListView
import org.kinecosystem.common.base.IBasePresenter
import org.kinecosystem.transfer.model.EcosystemApp

interface IAppsTransferPresenter : IBasePresenter<IAppsTransferView> {
    fun onCloseClicked()
    fun onResume()
}