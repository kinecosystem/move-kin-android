package org.kinecosystem.movekinlib.sender.discovery.presenter

import org.kinecosystem.movekinlib.base.BasePresenter
import org.kinecosystem.movekinlib.sender.discovery.view.IDiscoverButtonView

class DiscoverButtonPresenter : BasePresenter<IDiscoverButtonView>(), IDiscoverButtonPresenter{
    override fun onClicked() {
        view?.startAppsDiscoveryActivity()
    }

}