package org.kinecosystem.appsdiscovery.sender.discovery.presenter

import org.kinecosystem.appsdiscovery.base.BasePresenter
import org.kinecosystem.appsdiscovery.sender.discovery.view.IAppsDiscoveryListView
import org.kinecosystem.appsdiscovery.sender.model.EcosystemApp
import org.kinecosystem.appsdiscovery.sender.repositories.DiscoveryAppsRepository

class AppsDiscoveryListPresenter(private val discoveryAppsRepository: DiscoveryAppsRepository) : BasePresenter<IAppsDiscoveryListView>(), IAppsDiscoveryListPresenter {

    override fun updateData(apps: List<EcosystemApp>) {
        view?.updateData(apps)
    }

    override fun onAppClicked(app: EcosystemApp) {
        view?.navigateToApp(app)
    }

    override fun onAttach(view: IAppsDiscoveryListView) {
        super.onAttach(view)
        //every attached it loads data
        discoveryAppsRepository.getDiscoveryApps(object : org.kinecosystem.appsdiscovery.sender.repositories.OperationResultCallback<List<EcosystemApp>?> {
            override fun onResult(result: List<EcosystemApp>?) {
                result?.let {
                    updateData(result)
                }
            }

            override fun onError(error: String) {
                //TODO
            }

        })

    }
}