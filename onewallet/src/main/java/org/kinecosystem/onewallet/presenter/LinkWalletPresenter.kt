package org.kinecosystem.onewallet.presenter

import android.util.Log
import android.view.View
import org.kinecosystem.common.base.BasePresenter
import org.kinecosystem.common.base.LocalStore
import org.kinecosystem.onewallet.model.OneWalletActionModel
import org.kinecosystem.onewallet.view.LinkWalletViewHolder
import org.kinecosystem.onewallet.view.LinkingBarActionListener

class LinkWalletPresenter(model: OneWalletActionModel) : BasePresenter<LinkWalletViewHolder>(), ILinkWalletPresenter {

    var oneWalletActionModel: OneWalletActionModel = model
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

    override fun onLinkWalletSucceeded(oneWalletActionModel: OneWalletActionModel) {
        view?.progressBar?.displaySuccess()
        view?.actionButton?.isEnabled = true
        view?.actionButton?.update(oneWalletActionModel)
    }

    override fun onLinkWalletCancelled() {
        view?.progressBar?.dismiss()
        view?.actionButton?.isEnabled = true
    }

    override fun onLinkWalletError(errorMessage: String) {
        Log.e("LinkingModule", "The following message received " + errorMessage)
        view?.progressBar?.displayError(false)
        view?.actionButton?.isEnabled = true
    }

    override fun onLinkWalletTimeout() {
        view?.progressBar?.displayError(true)
        view?.actionButton?.isEnabled = true
    }


    override fun onDetach() {
        view?.progressBar?.stopFlipAnimation()
        super.onDetach()
    }
}