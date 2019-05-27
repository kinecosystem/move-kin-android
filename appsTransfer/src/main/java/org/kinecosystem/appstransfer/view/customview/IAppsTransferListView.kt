package org.kinecosystem.appstransfer.view.customview

import org.kinecosystem.appstransfer.presenter.AppsTransferListPresenter
import org.kinecosystem.common.base.IBaseView
import org.kinecosystem.transfer.model.EcosystemApp

interface IAppsTransferListView : IBaseView {
    fun updateData(apps: List<EcosystemApp>)
    fun transferToApp(app: EcosystemApp)
    fun navigateToUrl(googlePlayUrl: String)
    fun setLoadingListener(loadingListener: AppsTransferListPresenter.LoadingListener)
}