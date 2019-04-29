package org.kinecosystem.appsdiscovery.sender.discovery.presenter

import org.kinecosystem.appsdiscovery.base.BasePresenter
import org.kinecosystem.appsdiscovery.sender.discovery.view.IAppsDiscoveryListView
import org.kinecosystem.appsdiscovery.sender.model.EcosystemApp
import org.kinecosystem.appsdiscovery.sender.repositories.DiscoveryAppsRepository
import java.util.*

class AppsDiscoveryListPresenter(private val discoveryAppsRepository: DiscoveryAppsRepository) : BasePresenter<IAppsDiscoveryListView>(), IAppsDiscoveryListPresenter, Observer {

    override fun update(observable: Observable?, arg: Any?) {
        if (observable is DiscoveryAppsRepository) {
            updateApps(observable.discoveryApps)
            //TODO how to listen to errors
        }
    }

    override fun updateApps(apps: List<EcosystemApp>) {
        view?.updateData(apps)
    }

    override fun onAppClicked(app: EcosystemApp) {
        //val appSelected = discoveryAppsRepository.getAppByName(app.name)
       // Log.d("#####", "#####app clicked found by repositiory $appSelected" )
        view?.navigateToApp(app)
    }

    override fun onAttach(view: IAppsDiscoveryListView) {
        super.onAttach(view)
        //every attached it loads data
        discoveryAppsRepository.addObserver(this)
        discoveryAppsRepository.loadDiscoveryApps()
    }

    override fun onDetach() {
        super.onDetach()
        discoveryAppsRepository.deleteObserver(this)
    }
}