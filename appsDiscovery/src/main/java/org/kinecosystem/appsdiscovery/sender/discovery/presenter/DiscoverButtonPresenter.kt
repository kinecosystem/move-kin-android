package org.kinecosystem.appsdiscovery.sender.discovery.presenter

import org.kinecosystem.appsdiscovery.base.BasePresenter
import org.kinecosystem.appsdiscovery.sender.discovery.view.IDiscoverButtonView

class DiscoverButtonPresenter : BasePresenter<IDiscoverButtonView>(), IDiscoverButtonPresenter{
    override fun onClicked() {
        view?.startAppsDiscoveryActivity()
    }

}