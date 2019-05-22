package org.kinecosystem.appsdiscovery.view.customView

import org.kinecosystem.transfer.base.IBaseView
import org.kinecosystem.appsdiscovery.presenter.AppsDiscoveryListPresenter
import org.kinecosystem.appsdiscovery.model.EcosystemApp

interface IAppsDiscoveryListView : IBaseView{
    fun updateData(apps:List<EcosystemApp>)
    fun navigateToApp(app:EcosystemApp)
    fun setLoadingListener(loadingListener: AppsDiscoveryListPresenter.LoadingListener)
}