package org.kinecosystem.movekinlib.base

interface IBasePresenter<T : IBaseView> {

    fun onAttach(view: T)

    fun onDetach()
}
