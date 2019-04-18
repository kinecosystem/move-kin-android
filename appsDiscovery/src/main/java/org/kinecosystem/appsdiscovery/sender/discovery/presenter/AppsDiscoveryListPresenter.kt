package org.kinecosystem.appsdiscovery.sender.discovery.presenter

import org.kinecosystem.appsdiscovery.base.BasePresenter
import org.kinecosystem.appsdiscovery.sender.discovery.view.IAppsDiscoveryListView
import org.kinecosystem.appsdiscovery.sender.model.EcosystemApp

class AppsDiscoveryListPresenter : BasePresenter<IAppsDiscoveryListView>(), IAppsDiscoveryListPresenter {

    override fun updateData(apps:List<EcosystemApp>) {
        view?.updateData(apps)
    }

    override fun onAppClicked(app: EcosystemApp) {
        view?.navigateToApp(app)
    }
}