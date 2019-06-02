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
        view.actionButton.setAppearance(oneWalletActionModel)
        view.actionButton.isEnabled = true
        view.progressBar.visibility = View.INVISIBLE
    }

    override fun onLinkWalletStarted() {
        view?.actionButton?.isEnabled = false
        view?.progressBar?.visibility = View.VISIBLE
    }

    override fun onLinkWalletSucceeded() {
        view?.progressBar?.text = "Linking Success !!"
        view?.actionButton?.isEnabled = true
        oneWalletActionModel.type = OneWalletActionModel.Type.TOP_UP
        view?.actionButton?.setAppearance(oneWalletActionModel)
    }

    override fun onLinkWalletCancelled() {
        view?.progressBar?.text = "Linking Cancelled"
        view?.actionButton?.isEnabled = true
    }

    override fun onLinkWalletError(errorMessage: String) {
        view?.progressBar?.text = "Linking Error $errorMessage"
        view?.actionButton?.isEnabled = true
    }

}