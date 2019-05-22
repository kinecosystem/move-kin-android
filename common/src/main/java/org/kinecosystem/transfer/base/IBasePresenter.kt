package org.kinecosystem.transfer.base

interface IBasePresenter<T : IBaseView> {

    fun onAttach(view: T)

    fun onDetach()
}
