package org.kinecosystem.appsdiscovery.sender.discovery.presenter

import org.kinecosystem.appsdiscovery.base.BasePresenter
import org.kinecosystem.appsdiscovery.base.OperationResultCallback
import org.kinecosystem.appsdiscovery.sender.discovery.view.IAppsDiscoveryListView
import org.kinecosystem.appsdiscovery.sender.model.EcosystemApp
import org.kinecosystem.appsdiscovery.sender.server.Network

class AppsDiscoveryListPresenter : BasePresenter<IAppsDiscoveryListView>(), IAppsDiscoveryListPresenter {
    override fun loadData() {
        val network = Network()
        network.getDiscoveryApps(object : OperationResultCallback<List<EcosystemApp>?> {
            override fun onResult(apps: List<EcosystemApp>?) {
                apps?.let {
                    view?.updateData(apps)
                }
            }

            override fun onError(errorCode: Int) {
            }
        })
    }

    override fun onAppClicked(app: EcosystemApp) {
        view?.navigateToApp(app)
    }

    override fun onAttach(view: IAppsDiscoveryListView) {
        super.onAttach(view)
        loadData()
    }


}