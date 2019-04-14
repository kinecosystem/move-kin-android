package org.kinecosystem.movekinlib.discovery.presenter

import org.kinecosystem.movekinlib.base.IBasePresenter
import org.kinecosystem.movekinlib.discovery.view.IAppsDiscoveryListView
import org.kinecosystem.movekinlib.model.EcosystemApp

interface IAppsDiscoveryListPresenter : IBasePresenter<IAppsDiscoveryListView> {
    fun onAppClicked(app:EcosystemApp)
    fun loadData()
}