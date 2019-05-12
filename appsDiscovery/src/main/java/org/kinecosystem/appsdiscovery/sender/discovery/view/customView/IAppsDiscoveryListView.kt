package org.kinecosystem.appsdiscovery.sender.discovery.view.customView

import org.kinecosystem.appsdiscovery.base.IBaseView
import org.kinecosystem.appsdiscovery.sender.discovery.presenter.AppsDiscoveryListPresenter
import org.kinecosystem.appsdiscovery.sender.model.EcosystemApp

interface IAppsDiscoveryListView : IBaseView{
    fun updateData(apps:List<EcosystemApp>)
    fun navigateToApp(app:EcosystemApp)
    fun setLoadingListener(loadingListener: AppsDiscoveryListPresenter.LoadingListener)
}