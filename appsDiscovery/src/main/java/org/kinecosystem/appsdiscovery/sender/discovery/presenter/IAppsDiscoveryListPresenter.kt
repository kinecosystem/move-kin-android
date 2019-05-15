package org.kinecosystem.appsdiscovery.sender.discovery.presenter

import org.kinecosystem.common.base.IBasePresenter
import org.kinecosystem.appsdiscovery.sender.discovery.view.customView.IAppsDiscoveryListView
import org.kinecosystem.appsdiscovery.sender.model.EcosystemApp

interface IAppsDiscoveryListPresenter : IBasePresenter<IAppsDiscoveryListView> {
    fun onAppClicked(app:EcosystemApp)
    fun updateApps(apps:List<EcosystemApp>)
    fun setLoadingListener(loadingListener: AppsDiscoveryListPresenter.LoadingListener)
}