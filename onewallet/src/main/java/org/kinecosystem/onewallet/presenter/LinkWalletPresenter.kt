package org.kinecosystem.onewallet.presenter

import android.view.View
import org.kinecosystem.common.base.BasePresenter
import org.kinecosystem.common.base.LocalStore
import org.kinecosystem.onewallet.model.OneWalletActionModel
import org.kinecosystem.onewallet.view.LinkWalletViewHolder

class LinkWalletPresenter(localStore: LocalStore) : BasePresenter<LinkWalletViewHolder>(), ILinkWalletPresenter {

    var oneWalletActionModel: OneWalletActionModel = OneWalletActionModel(localStore)
        private set

    override fun onAttach(view: LinkWalletViewHolder) {
        super.onAttach(view)
        view.actionButton.update(oneWalletActionModel)
        view.actionButton.isEnabled = true
        view.progressBar.visibility = View.INVISIBLE
    }

    override fun onLinkWalletStarted() {
        view?.progressBar?.startFlipAnimation()
        view?.actionButton?.isEnabled = false
        view?.progressBar?.visibility = View.VISIBLE
    }

    override fun onLinkWalletSucceeded() {
        view?.progressBar?.displaySuccess()
        view?.actionButton?.isEnabled = true
        oneWalletActionModel.type = OneWalletActionModel.Type.TOP_UP
        view?.actionButton?.update(oneWalletActionModel)
    }

    override fun onLinkWalletCancelled() {
        view?.progressBar?.displayError(0)
        view?.actionButton?.isEnabled = true
    }

    override fun onLinkWalletError(errorMessage: String) {
        view?.progressBar?.displayError(0)
        view?.actionButton?.isEnabled = true
    }

    override fun onDetach() {
        view?.progressBar?.stopFlipAnimation()
        super.onDetach()
    }

}