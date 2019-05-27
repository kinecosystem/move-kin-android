package org.kinecosystem.appstransfer.presenter

import org.kinecosystem.appstransfer.view.customview.IAppsTransferListView
import org.kinecosystem.common.base.BasePresenter
import org.kinecosystem.transfer.model.EcosystemApp
import org.kinecosystem.transfer.model.canTransferKin
import org.kinecosystem.transfer.model.downloadUrl
import org.kinecosystem.transfer.repositories.DiscoveryAppsRepository
import org.kinecosystem.transfer.repositories.OperationResultCallback


class AppsTransferListPresenter(private val discoveryAppsRepository: DiscoveryAppsRepository) : BasePresenter<IAppsTransferListView>(), IAppsTransferListPresenter {

    private var loadingListener: LoadingListener? = null

    interface LoadingListener {
        fun loading()
        fun loadingComplete()
        fun loadingFailed()
    }

    override fun setLoadingListener(loadingListener: LoadingListener) {
        this.loadingListener = loadingListener
    }

    override fun updateApps(apps: List<EcosystemApp>) {
        view?.updateData(apps)
    }

    override fun onAppClicked(app: EcosystemApp) {
        if (app.canTransferKin) {
            //TODO start transfer
            view?.transferToApp(app)
        } else {
            view?.navigateToUrl(app.downloadUrl)
        }

    }

    override fun onDetach() {
        super.onDetach()
        loadingListener = null
    }

    override fun onAttach(view: IAppsTransferListView) {
        super.onAttach(view)
        loadingListener?.loading()
        discoveryAppsRepository.loadDiscoveryApps(object : OperationResultCallback<List<EcosystemApp>> {
            override fun onResult(result: List<EcosystemApp>) {
                updateApps(result)
                loadingListener?.loadingComplete()
            }

            override fun onError(error: String) {
                loadingListener?.loadingFailed()
            }
        })
    }

}