package org.kinecosystem.appsdiscovery.sender.discovery.presenter

import org.kinecosystem.appsdiscovery.base.IBasePresenter
import org.kinecosystem.appsdiscovery.sender.discovery.view.IDiscoverButtonView

interface IDiscoverButtonPresenter : IBasePresenter<IDiscoverButtonView> {
    fun onClicked()
}