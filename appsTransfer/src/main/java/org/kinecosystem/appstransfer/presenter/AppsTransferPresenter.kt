package org.kinecosystem.appstransfer.presenter

import android.content.Intent
import android.util.Log
import org.kinecosystem.appstransfer.view.IAppsTransferView
import org.kinecosystem.appstransfer.view.customview.AppsTransferList
import org.kinecosystem.common.base.BasePresenter
import org.kinecosystem.transfer.model.*
import org.kinecosystem.transfer.sender.manager.TransferManager

class AppsTransferPresenter(private val transferManager: TransferManager) : BasePresenter<IAppsTransferView>(), IAppsTransferPresenter, AppsTransferListPresenter.LoadingListener, AppsTransferList.AppClickListener {

    private val TAG = AppsTransferPresenter::class.java.simpleName
    private val REMOTE_PUBLIC_ADDRESS_REQUEST_CODE = 200
    private var app: EcosystemApp? = null

    override fun onAppClicked(app: EcosystemApp, isInstalled:Boolean) {
        this.app = app
        if (isInstalled && app.canReceiveKin) {
            requestReceiverPublicAddress(app)
        } else {
            view?.navigateToAppStore(app.downloadUrl)
        }
    }

    private fun requestReceiverPublicAddress(app: EcosystemApp) {
        app.identifier?.let { pkg ->
            app.launchActivity?.let { activityFullPath ->
                val started = transferManager.startTransferRequestActivity(REMOTE_PUBLIC_ADDRESS_REQUEST_CODE, pkg, activityFullPath)
                if (!started) {
                    view?.onCantFindReceiverInfo(app.name)
                }
            } ?: kotlin.run { view?.onTransferError(app.name) }
        } ?: kotlin.run { view?.onTransferError(app.name) }
    }

    override fun processResponse(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == REMOTE_PUBLIC_ADDRESS_REQUEST_CODE) {
            parsePublicAddressData(resultCode, intent)
        }
    }

    override fun loading() {
        view?.showLoading()
    }

    override fun loadingComplete() {
        view?.showData()
    }

    override fun loadingFailed() {
        view?.showNoData()
    }

    override fun onResume() {
        view?.invalidateList()
    }

    override fun onCloseClicked() {
        view?.close()
    }

    private fun parsePublicAddressData(resultCode: Int, intent: Intent?) {
        transferManager.processResponse(resultCode, intent, object : TransferManager.AccountInfoResponseListener {
            override fun onCancel() {
                Log.d(TAG, "Operation cancelled, no public address received")
            }

            override fun onError(error: String) {
                app?.let {
                    view?.onTransferError(it.name)
                }
            }

            override fun onResult(address: String) {
                Log.d(TAG, "got address onResult $address")
                app?.let {
                    view?.startTransferAmountActivity(it, address)
                }
            }
        })
    }
}
