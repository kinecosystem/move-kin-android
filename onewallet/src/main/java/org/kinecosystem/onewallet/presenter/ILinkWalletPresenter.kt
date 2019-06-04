package org.kinecosystem.onewallet.presenter

import org.kinecosystem.common.base.IBasePresenter
import org.kinecosystem.onewallet.model.OneWalletActionModel
import org.kinecosystem.onewallet.view.LinkWalletViewHolder

interface ILinkWalletPresenter : IBasePresenter<LinkWalletViewHolder> {

    fun onLinkWalletStarted()
    fun onLinkWalletCancelled()
    fun onLinkWalletSucceeded(oneWalletActionModel: OneWalletActionModel)
    fun onLinkWalletError(errorMessage: String)
    fun onLinkWalletTimeout()

}
