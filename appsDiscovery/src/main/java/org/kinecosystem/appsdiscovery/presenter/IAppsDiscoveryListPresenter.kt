package org.kinecosystem.appsdiscovery.presenter

import org.kinecosystem.common.base.IBasePresenter
import org.kinecosystem.appsdiscovery.view.customView.IAppsDiscoveryListView
import org.kinecosystem.appsdiscovery.model.EcosystemApp

interface IAppsDiscoveryListPresenter : IBasePresenter<IAppsDiscoveryListView> {
    fun onAppClicked(app:EcosystemApp)
    fun updateApps(apps:List<EcosystemApp>)
    fun setLoadingListener(loadingListener: AppsDiscoveryListPresenter.LoadingListener)
}