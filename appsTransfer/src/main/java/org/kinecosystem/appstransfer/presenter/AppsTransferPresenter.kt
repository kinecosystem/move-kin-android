package org.kinecosystem.appstransfer.presenter

import org.kinecosystem.appstransfer.view.IAppsTransferView
import org.kinecosystem.appstransfer.view.customview.AppsTransferList
import org.kinecosystem.common.base.BasePresenter
import org.kinecosystem.transfer.model.EcosystemApp
import org.kinecosystem.transfer.model.canTransferKin
import org.kinecosystem.transfer.model.downloadUrl

class AppsTransferPresenter : BasePresenter<IAppsTransferView>(), IAppsTransferPresenter, AppsTransferListPresenter.LoadingListener, AppsTransferList.AppClickListener {

    override fun onAppClicked(app: EcosystemApp) {
        if (app.canTransferKin) {
            view?.transferToApp(app)
        } else {
            view?.navigateToAppStore(app.downloadUrl)
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


}
