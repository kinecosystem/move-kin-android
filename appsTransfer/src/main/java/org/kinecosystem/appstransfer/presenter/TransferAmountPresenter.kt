package org.kinecosystem.appstransfer.presenter

import android.os.Handler
import org.kinecosystem.appstransfer.view.ITransferAmountView
import org.kinecosystem.common.base.BasePresenter
import org.kinecosystem.common.base.Consts
import org.kinecosystem.common.base.Consts.TRANSACTION_TIMEOUT
import org.kinecosystem.transfer.model.EcosystemApp
import org.kinecosystem.transfer.model.getTransactionMemo
import org.kinecosystem.transfer.model.iconUrl
import org.kinecosystem.transfer.model.name
import org.kinecosystem.transfer.repositories.EcosystemAppsRepository
import org.kinecosystem.transfer.sender.view.TransferBarView
import org.kinecosystem.transfer.sender.view.TransferInfo

class TransferAmountPresenter(appName: String, private val receiverPublicAddress: String, private val repository: EcosystemAppsRepository, private val senderServiceBinder: SenderServiceBinder) : BasePresenter<ITransferAmountView>(), ITransferAmountPresenter, SenderServiceBinder.BinderListener {
    private val DOUBLE_ZEROE = "00"
    private val ZEROE = "0"

    private var amount: Int = 0
    private var amountStr: String = ZEROE
    private var balance = 0
    private var app: EcosystemApp? = null
    private var requestBalance = false
    private val handler = Handler()
    private var afterTimeout = false
    @Volatile
    private var gotTransferResponse = false

    init {
        app = repository.getAppByName(appName)
    }

    override fun onZerosClicked() {
        if (amount != 0) {
            amountStr += DOUBLE_ZEROE
            try {
                amount = amountStr.toInt()
            } catch (error: NumberFormatException) {
                amountStr = amountStr.removeRange(amountStr.length - 3, amountStr.length)
                amountStr += ZEROE
                amount = amountStr.toInt()
            }
            onAmountModified()
        }
    }


    override fun onFullDel() {
        amountStr = ZEROE
        amount = 0
        onAmountModified()
    }

    private fun onAmountModified() {
        view?.updateAmount(amountStr)
        view?.setSendEnable(amount in 1..balance || balance == Consts.NO_BALANCE)
    }

    override fun onDellClicked() {
        if (amount > 0) {
            if (amount < 10) {
                amount = 0
                amountStr = ZEROE
            } else {
                amountStr = amountStr.removeRange(amountStr.length - 1, amountStr.length)
                amount = amountStr.toInt()
            }
        }
        onAmountModified()
    }

    override fun onDigitClicked(number: String) {
        if (amount == 0) {
            if (number != ZEROE) {
                amountStr = number
                amount = amountStr.toInt()
            }
        } else {
            amountStr += number
            try {
                amount = amountStr.toInt()
            } catch (error: NumberFormatException) {
                amountStr = amountStr.removeRange(amountStr.length - 2, amountStr.length)
                amountStr += number
                amount = amountStr.toInt()
            }
        }
        onAmountModified()
    }


    override fun onServiceConnected() {
        if (requestBalance) {
            senderServiceBinder.requestCurrentBalance()
            requestBalance = false
        }
    }

    override fun onServiceDisconnected() {
        //TODO
    }

    override fun onPause() {
        senderServiceBinder.unbind()
    }

    override fun onBalanceReceived(balance: Int) {
        this.balance = balance
        view?.updateBalance(balance)
        onAmountModified()
        //store last balance - in order to show something while loading
        repository.storeCurrentBalance(balance)
    }

    override fun onBalanceFailed() {
        //TODO ?
    }

    override fun onSendKinClicked() {
        app?.let { application ->
            application.identifier?.let { receiverPackage ->
                senderServiceBinder.startSendKin(receiverPublicAddress, application.name, amount, application.getTransactionMemo(), receiverPackage)
                view?.enableSend(false)
                view?.initTransferBar(TransferInfo(application.iconUrl, application.iconUrl, application.name, receiverPackage, amount))
                view?.updateTransferBar(TransferBarView.TransferStatus.Started)
            }
        }
        startTimeOutCounter()
    }

    override fun onTransferFailed() {
        if (!afterTimeout) {
            gotTransferResponse = false
            view?.enableSend(true)
            view?.updateTransferBar(TransferBarView.TransferStatus.Failed)
        }
    }

    override fun onTransferComplete() {
        if (!afterTimeout) {
            gotTransferResponse = false
            view?.enableSend(true)
            view?.updateTransferBar(TransferBarView.TransferStatus.Complete)
        }
    }

    override fun onAttach(view: ITransferAmountView) {
        super.onAttach(view)
        app?.iconUrl?.let {
            view.updateIconUrl(it)
        }
        view.updateBalance(repository.getCurrentBalance())
        senderServiceBinder.setListener(this)
        onAmountModified()
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

    private fun startTimeOutCounter() {
        afterTimeout = false
        gotTransferResponse = false
        handler.postDelayed({
            if (!gotTransferResponse) {
                afterTimeout = true
                view?.updateTransferBar(TransferBarView.TransferStatus.Timeout)
            }
        }, TRANSACTION_TIMEOUT)
    }
}
