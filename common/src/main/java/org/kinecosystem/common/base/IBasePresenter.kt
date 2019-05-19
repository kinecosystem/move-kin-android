package org.kinecosystem.common.base

interface IBasePresenter<T : IBaseView> {

    fun onAttach(view: T)

    fun onDetach()
}
