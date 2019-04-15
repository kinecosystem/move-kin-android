package org.kinecosystem.movekinlib.sender.discovery.presenter

import org.kinecosystem.movekinlib.base.IBasePresenter
import org.kinecosystem.movekinlib.sender.discovery.view.IDiscoverButtonView

interface IDiscoverButtonPresenter : IBasePresenter<IDiscoverButtonView> {
    fun onClicked()
}