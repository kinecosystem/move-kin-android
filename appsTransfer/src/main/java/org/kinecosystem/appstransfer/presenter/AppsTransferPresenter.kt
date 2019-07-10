package org.kinecosystem.appstransfer.presenter

import android.content.Intent
import android.util.Log
import org.kinecosystem.appstransfer.view.IAppsTransferView
import org.kinecosystem.appstransfer.view.customview.AppsTransferList
import org.kinecosystem.common.base.BasePresenter
import org.kinecosystem.common.base.Consts
import org.kinecosystem.transfer.model.*
import org.kinecosystem.transfer.repositories.EcosystemAppsLocalRepo
import org.kinecosystem.transfer.sender.manager.TransferManager
import org.kinecosystem.transfer.sender.service.SendKinServiceBase

class AppsTransferPresenter(private val transferManager: TransferManager, private val repository: EcosystemAppsLocalRepo, private val senderServiceBinder: SenderServiceBinder) : BasePresenter<IAppsTransferView>(), IAppsTransferPresenter, AppsTransferListPresenter.LoadingListener, AppsTransferList.AppClickListener, SenderServiceBinder.BinderListener {


    override fun onServiceConnected() {
        if (balanceRequested) {
            senderServiceBinder.requestCurrentBalance()
            balanceRequested = false
        }
        if(addressRequested){
            senderServiceBinder.requestAddress()
            addressRequested = false
        }
    }

    override fun onServiceDisconnected() {
    }

    override fun onPause() {
        senderServiceBinder.unbind()
    }

    override fun onTransferFailed(errorMessage: String, senderAddress: String, transactionMemo: String) {
    }

    override fun onTransferComplete(kinTransferComplete: SendKinServiceBase.KinTransferComplete) {
    }

    override fun onBalanceReceived(balance: Int) {
        repository.currentBalance = balance
    }

    override fun onAddressReceived(address: String) {
        repository.address = address
    }

    override fun onBalanceFailed() {
        repository.currentBalance = Consts.NO_BALANCE
    }

    override fun onAddressFailed() {
        repository.address = ""
    }

    private val TAG = AppsTransferPresenter::class.java.simpleName
    private val REMOTE_PUBLIC_ADDRESS_REQUEST_CODE = 200
    private var app: EcosystemApp? = null
    private var balanceRequested = false
    private var addressRequested = false

    override fun onResume() {
        view?.invalidateList()
        if (senderServiceBinder.isBounded) {
            senderServiceBinder.requestCurrentBalance()
            senderServiceBinder.requestAddress()
        } else {
            requestBalance()
            requestAddress()
        }
    }

    override fun onAppClicked(app: EcosystemApp, isInstalled: Boolean) {
        this.app = app
        if (isInstalled && app.canReceiveKin) {
            requestReceiverPublicAddress(app)
        } else {
            view?.navigateToAppStore(app.downloadUrl)
        }
    }

    private fun requestBalance() {
        balanceRequested = true
        senderServiceBinder.bind()
    }

    private fun requestAddress() {
        addressRequested = true
        senderServiceBinder.bind()
    }

    private fun requestReceiverPublicAddress(app: EcosystemApp) {
        app.appPackage?.let { pkg ->
            app.launchActivity?.let { activityFullPath ->
                repository.memoWithRandom = app.createTransactionMemoWithRandom()
                val started = transferManager.startTransferRequestActivity(REMOTE_PUBLIC_ADDRESS_REQUEST_CODE, pkg, activityFullPath, repository.address, repository.appName, repository.memoWithRandom, repository.appId, app.appId)
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
