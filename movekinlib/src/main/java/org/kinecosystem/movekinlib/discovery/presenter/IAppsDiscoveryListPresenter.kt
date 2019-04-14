package org.kinecosystem.movekinlib.discovery.presenter

import org.kinecosystem.movekinlib.base.IBasePresenter
import org.kinecosystem.movekinlib.discovery.view.IAppsDiscoveryListView

interface IAppsDiscoveryListPresenter : IBasePresenter<IAppsDiscoveryListView> {
    fun onAppClicked(app:String)
    fun loadData()
}