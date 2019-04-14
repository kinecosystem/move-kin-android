package org.kinecosystem.movekinlib.discovery.presenter

import org.kinecosystem.movekinlib.base.BasePresenter
import org.kinecosystem.movekinlib.discovery.view.IAppsDiscoveryListView

class AppsDiscoveryListPresenter : BasePresenter<IAppsDiscoveryListView>(), IAppsDiscoveryListPresenter{
    override fun loadData() {
       view?.updateData(listOf("abba", "amma", "kin"))
    }

    override fun onAppClicked(app:String) {
        view?.navigateToApp(app)
    }

    override fun onAttach(view: IAppsDiscoveryListView) {
        super.onAttach(view)
        loadData()
    }


}