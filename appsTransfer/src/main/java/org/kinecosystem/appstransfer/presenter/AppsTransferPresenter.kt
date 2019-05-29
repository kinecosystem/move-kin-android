package org.kinecosystem.appstransfer.presenter

import org.kinecosystem.appstransfer.view.IAppsTransferView
import org.kinecosystem.appstransfer.view.customview.AppsTransferList
import org.kinecosystem.common.base.BasePresenter
import org.kinecosystem.transfer.model.EcosystemApp
import org.kinecosystem.transfer.model.canTransferKin
import org.kinecosystem.transfer.model.downloadUrl
import org.kinecosystem.transfer.model.launchActivity
import org.kinecosystem.transfer.repositories.EcosystemAppsRepository
import org.kinecosystem.transfer.sender.manager.TransferManager

class AppsTransferPresenter(private val repository: EcosystemAppsRepository, private val transferManager: TransferManager) : BasePresenter<IAppsTransferView>(), IAppsTransferPresenter, AppsTransferListPresenter.LoadingListener, AppsTransferList.AppClickListener {

    private val REMOTE_PUBLIC_ADDRESS_REQUEST_CODE = 200

    override fun onAppClicked(app: EcosystemApp) {
        if (app.canTransferKin) {
            requestReceiverPublicAddress(app)
            //TODO is needed????
            //view?.transferToApp(app)
        } else {
            view?.navigateToAppStore(app.downloadUrl)
        }
    }

    private fun requestReceiverPublicAddress(app: EcosystemApp) {
        repository.clearReceiverAppPublicAddress()
        app.identifier?.let { pkg ->
            app.launchActivity?.let { activityFullPath ->
                val started = transferManager.startTransferRequestActivity(REMOTE_PUBLIC_ADDRESS_REQUEST_CODE, pkg, activityFullPath)
                if (!started) {
                    view?.onTransferError()
                }
            } ?: kotlin.run { view?.onTransferError() }
        } ?: kotlin.run { view?.onTransferError() }
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


}
