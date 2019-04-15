package org.kinecosystem.appsdiscovery.sender.discovery.presenter

import org.kinecosystem.appsdiscovery.base.IBasePresenter
import org.kinecosystem.appsdiscovery.sender.discovery.view.IAppsDiscoveryListView
import org.kinecosystem.appsdiscovery.sender.model.EcosystemApp

interface IAppsDiscoveryListPresenter : IBasePresenter<IAppsDiscoveryListView> {
    fun onAppClicked(app:EcosystemApp)
    fun loadData()
}