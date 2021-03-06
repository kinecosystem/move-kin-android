package org.kinecosystem.appsdiscovery.presenter

import org.kinecosystem.appsdiscovery.view.customView.IAppsDiscoveryListView
import org.kinecosystem.transfer.model.EcosystemApp
import org.kinecosystem.transfer.repositories.EcosystemAppsRepository
import org.kinecosystem.transfer.repositories.OperationResultCallback
import org.kinecosystem.common.base.BasePresenter

class AppsDiscoveryListPresenter(private val repository: EcosystemAppsRepository) : BasePresenter<IAppsDiscoveryListView>(), IAppsDiscoveryListPresenter {

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
        view?.navigateToApp(app)
    }

    override fun onDetach() {
        super.onDetach()
        loadingListener = null
    }

    override fun onAttach(view: IAppsDiscoveryListView) {
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

}