package org.kinecosystem.appstransfer.presenter

import android.os.Handler
import android.util.Log
import org.kinecosystem.appstransfer.view.ITransferAmountView
import org.kinecosystem.common.base.BasePresenter
import org.kinecosystem.transfer.model.EcosystemApp
import org.kinecosystem.transfer.model.getTransactionMemo
import org.kinecosystem.transfer.model.iconUrl
import org.kinecosystem.transfer.model.name
import org.kinecosystem.transfer.repositories.EcosystemAppsRepository
import org.kinecosystem.transfer.sender.manager.TransferManager

class TransferAmountPresenter(appName: String, private val repository: EcosystemAppsRepository, private val transferManager: TransferManager, private val senderServiceBinder: SenderServiceBinder, private val uiHandler: Handler) : BasePresenter<ITransferAmountView>(), ITransferAmountPresenter, SenderServiceBinder.BinderListener {
    private var requestBalance = false

    override fun onServiceConnected() {
        Log.d("####", "onServiceConnected")
        if (requestBalance) {
            senderServiceBinder.requestCurrentBalance()
            requestBalance = false
        }
    }

    override fun onServiceDisconnected() {
        Log.d("####", "onServiceDisconnected")

        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startSendKin(receiverAddress: String, senderAppName: String, amount: Int, memo: String, receiverPackage: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var app: EcosystemApp? = null


    override fun onPause() {
        senderServiceBinder.unbind()
    }

    override fun onBalanceReceived(balance: Int) {
        Log.d("####", "onBalanceReceived $balance")
        uiHandler.post {
            view?.updateBalance(balance)
        }
        repository.storeCurrentBalance(balance)
    }

    override fun onBalanceFailed() {
        //TODO ?
    }

    override fun onSendKinClicked() {
        //TODO
        val amountToSend = 5
        app?.let {
            it.identifier?.let { pkg ->
                //view?.initTransfersInfo(TransferInfo(repository.getStoredAppIcon(), it.iconUrl, it.name, pkg, amountToSend))
            }
        }

        app?.let { application ->
            application.identifier?.let { receiverPackage ->
                senderServiceBinder.startSendKin(repository.getReceiverAppPublicAddress(), application.name, amountToSend, application.getTransactionMemo(), receiverPackage)

                // view?.startSendKin(repository.getReceiverAppPublicAddress(),  application.name, amountToSend, application.getTransactionMemo(), receiverPackage)
            }
        }

        //startTimeOutCounter()
    }

    override fun onAttach(view: ITransferAmountView) {
        super.onAttach(view)
        app?.iconUrl?.let {
            view.updateIconUrl(it)
        }
        view.updateBalance(repository.getCurrentBalance())
        senderServiceBinder.setListener(this)
    }


    init {
        app = repository.getAppByName(appName)
    }

    override fun onCloseClicked() {
        view?.close()
    }

    override fun onResume() {
        if (senderServiceBinder.isBounded) {
            senderServiceBinder.requestCurrentBalance()
        } else {
            requestBalance = true
            senderServiceBinder.bind()
        }
    }


}
