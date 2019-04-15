package org.kinecosystem.movekinlib.sender.discovery.presenter

import org.kinecosystem.movekinlib.base.IBasePresenter
import org.kinecosystem.movekinlib.sender.discovery.view.IAppsDiscoveryListView
import org.kinecosystem.movekinlib.sender.model.EcosystemApp

interface IAppsDiscoveryListPresenter : IBasePresenter<IAppsDiscoveryListView> {
    fun onAppClicked(app:EcosystemApp)
    fun loadData()
}