package org.kinecosystem.appsdiscovery.base

interface IBasePresenter<T : IBaseView> {

    fun onAttach(view: T)

    fun onDetach()
}
