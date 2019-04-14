package org.kinecosystem.movekinlib.discovery.presenter

import org.kinecosystem.movekinlib.base.IBasePresenter
import org.kinecosystem.movekinlib.discovery.view.IDiscoverButtonView

interface IDiscoverButtonPresenter : IBasePresenter<IDiscoverButtonView> {
    fun onClicked()
}