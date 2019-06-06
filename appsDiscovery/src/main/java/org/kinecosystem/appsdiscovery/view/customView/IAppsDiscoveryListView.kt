package org.kinecosystem.appsdiscovery.view.customView

import org.kinecosystem.common.base.IBaseView
import org.kinecosystem.appsdiscovery.presenter.AppsDiscoveryListPresenter
import org.kinecosystem.transfer.model.EcosystemApp

interface IAppsDiscoveryListView : IBaseView{
    fun updateData(apps:List<EcosystemApp>)
    fun navigateToApp(app:EcosystemApp)
    fun setLoadingListener(loadingListener: AppsDiscoveryListPresenter.LoadingListener)
}