package org.kinecosystem.movekinlib.discovery.presenter

import org.kinecosystem.movekinlib.base.BasePresenter
import org.kinecosystem.movekinlib.base.OperationResultCallback
import org.kinecosystem.movekinlib.discovery.view.IAppsDiscoveryListView
import org.kinecosystem.movekinlib.model.EcosystemApp
import org.kinecosystem.movekinlib.server.Network

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