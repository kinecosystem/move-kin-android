package org.kinecosystem.onewallet.presenter

import android.util.Log
import android.view.View
import org.kinecosystem.common.base.BasePresenter
import org.kinecosystem.onewallet.model.OneWalletActionModel
import org.kinecosystem.onewallet.view.LinkWalletViewHolder

// This Presenter presents 2 views (an actionButton & a progresBar) held together
// by a simple object: the LinkWalletViewHolder.
// Important Note: The "view" that the presenter gets attached to, is not a view but
// a LinkWalletViewHolder which is an object that holds 2 views)
class LinkWalletPresenter(model: OneWalletActionModel) : BasePresenter<LinkWalletViewHolder>(), ILinkWalletPresenter {

    var oneWalletActionModel: OneWalletActionModel = model
        private set

    override fun onAttach(view: LinkWalletViewHolder) {
        super.onAttach(view)
        // As noted above, "view" is a view holder holding the progressBar and actionButton views
        view.actionButton.update(oneWalletActionModel)
        view.actionButton.isEnabled = true
        view.progressBar.visibility = View.INVISIBLE
    }

    override fun onLinkWalletStarted() {
        // As noted above, "view" is a view holder holding the progressBar and actionButton views
        view?.progressBar?.startFlipAnimation()
        view?.actionButton?.isEnabled = false
        view?.progressBar?.visibility = View.VISIBLE
    }

    override fun onLinkWalletSucceeded(oneWalletActionModel: OneWalletActionModel) {
        // As noted above, "view" is a view holder holding the progressBar and actionButton views
        view?.progressBar?.displaySuccess()
        view?.actionButton?.isEnabled = true
        view?.actionButton?.update(oneWalletActionModel)
    }

    override fun onLinkWalletCancelled() {
        // As noted above, "view" is a view holder holding the progressBar and actionButton views
        view?.progressBar?.dismiss()
        view?.actionButton?.isEnabled = true
    }

    override fun onLinkWalletError(errorMessage: String) {
        // As noted above, "view" is a view holder holding the progressBar and actionButton views
        Log.e("LinkingModule", "The following message received " + errorMessage)
        view?.progressBar?.displayError(false)
        view?.actionButton?.isEnabled = true
    }

    override fun onLinkWalletTimeout() {
        // As noted above, "view" is a view holder holding the progressBar and actionButton views
        view?.progressBar?.displayError(true)
        view?.actionButton?.isEnabled = true
    }


    override fun onDetach() {
        // As noted above, "view" is a view holder holding the progressBar and actionButton views
        view?.progressBar?.stopFlipAnimation()
        super.onDetach()
    }
}