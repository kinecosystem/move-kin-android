package org.kinecosystem.appstransfer.presenter

import org.kinecosystem.appstransfer.view.customview.IAppsTransferListView
import org.kinecosystem.common.base.IBasePresenter
import org.kinecosystem.transfer.model.EcosystemApp

interface IAppsTransferListPresenter : IBasePresenter<IAppsTransferListView> {
    fun updateApps(apps:List<EcosystemApp>)
    fun setLoadingListener(loadingListener: AppsTransferListPresenter.LoadingListener)
}