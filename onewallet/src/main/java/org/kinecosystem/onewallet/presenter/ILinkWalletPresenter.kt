package org.kinecosystem.onewallet.presenter

import org.kinecosystem.common.base.IBasePresenter
import org.kinecosystem.onewallet.view.LinkWalletViewHolder

interface ILinkWalletPresenter : IBasePresenter<LinkWalletViewHolder> {

    fun onLinkWalletStarted()
    fun onLinkWalletCancelled()
    fun onLinkWalletSucceeded()
    fun onLinkWalletError(errorMessage: String)

}
