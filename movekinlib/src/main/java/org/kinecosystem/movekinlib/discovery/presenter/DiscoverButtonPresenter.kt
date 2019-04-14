package org.kinecosystem.movekinlib.discovery.presenter

import org.kinecosystem.movekinlib.base.BasePresenter
import org.kinecosystem.movekinlib.discovery.view.IDiscoverButtonView

class DiscoverButtonPresenter : BasePresenter<IDiscoverButtonView>(), IDiscoverButtonPresenter{
    override fun onClicked() {
        view?.startAppsDiscoveryActivity()
    }

}