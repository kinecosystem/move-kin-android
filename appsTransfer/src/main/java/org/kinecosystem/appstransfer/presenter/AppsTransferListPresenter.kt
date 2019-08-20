package org.kinecosystem.appstransfer.presenter

import org.kinecosystem.appstransfer.view.customview.IAppsTransferListView
import org.kinecosystem.common.base.BasePresenter
import org.kinecosystem.transfer.model.EcosystemApp
import org.kinecosystem.transfer.repositories.EcosystemAppsRepository
import org.kinecosystem.transfer.repositories.OperationResultCallback


class AppsTransferListPresenter(private val repository: EcosystemAppsRepository) : BasePresenter<IAppsTransferListView>(), IAppsTransferListPresenter {

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

    override fun onDetach() {
        super.onDetach()
        loadingListener = null
    }

    override fun onAttach(view: IAppsTransferListView) {
        super.onAttach(view)
        loadingListener?.loading()
        repository.loadDiscoveryApps(object : OperationResultCallback<List<EcosystemApp>> {
            override fun onResult(result: List<EcosystemApp>) {
                updateApps(result)
                loadingListener?.loadingComplete()
            }

            override fun onError(error: String) {
                loadingListener?.loadingFailed()
            }
        })
    }

    private fun test(){}

}