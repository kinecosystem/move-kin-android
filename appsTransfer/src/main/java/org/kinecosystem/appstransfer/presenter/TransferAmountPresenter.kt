package org.kinecosystem.appstransfer.presenter

import org.kinecosystem.appstransfer.view.ITransferAmountView
import org.kinecosystem.common.base.BasePresenter
import org.kinecosystem.common.base.Consts
import org.kinecosystem.transfer.model.EcosystemApp
import org.kinecosystem.transfer.model.getTransactionMemo
import org.kinecosystem.transfer.model.iconUrl
import org.kinecosystem.transfer.model.name
import org.kinecosystem.transfer.repositories.EcosystemAppsRepository
import org.kinecosystem.transfer.sender.service.SendKinServiceBase
import org.kinecosystem.transfer.sender.view.TransferBarView
import org.kinecosystem.transfer.sender.view.TransferInfo

class TransferAmountPresenter(receiverAppName: String, private val senderAppName: String, private val receiverPublicAddress: String, private val repository: EcosystemAppsRepository, private val senderServiceBinder: SenderServiceBinder) : BasePresenter<ITransferAmountView>(), ITransferAmountPresenter, SenderServiceBinder.BinderListener {
    private val DOUBLE_ZEROE = "00"
    private val ZEROE = "0"

    private var amount: Int = 0
    private var amountStr: String = ZEROE
    private var balance = Consts.NO_BALANCE
    private var app: EcosystemApp? = null
    private var balanceRequested = false

    init {
        app = repository.getAppByName(receiverAppName)
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


    override fun resetAmount() {
        amountStr = ZEROE
        amount = 0
        onAmountModified()
    }

    private fun onAmountModified() {
        view?.updateAmount(amountStr)
        view?.enableSend(amount in 1..balance || (balance == Consts.NO_BALANCE && amount > 0))
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
        if (balanceRequested) {
            senderServiceBinder.requestCurrentBalance()
            balanceRequested = false
        }
    }

    override fun onServiceDisconnected() {
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
        balance = Consts.NO_BALANCE
    }

    override fun onSendKinClicked() {
        app?.let { application ->
            application.identifier?.let { receiverPackage ->
                senderServiceBinder.startSendKin(receiverPublicAddress, amount, application.getTransactionMemo())
                view?.initTransferBar(TransferInfo(repository.getStoredAppIcon(), application.iconUrl, application.name, receiverPackage, amount))
                view?.updateTransferBar(TransferBarView.TransferStatus.Started)
                resetAmount()
            }
        }
    }

    override fun onTransferFailed(errorMessge: String, senderAddress: String) {
        view?.updateTransferBar(TransferBarView.TransferStatus.Failed)
        app?.let { application ->
            application.identifier?.let { receiverPackage ->
                view?.notifyReceiverTransactionFailed(receiverPackage, errorMessge, senderAddress, senderAppName, receiverPublicAddress, amount, application.memo)
            }
        }
    }

    override fun onTransferTimeout() {
        view?.updateTransferBar(TransferBarView.TransferStatus.Timeout)
    }

    override fun onTransferComplete(kinTransferComplete: SendKinServiceBase.KinTransferComplete) {
        view?.updateTransferBar(TransferBarView.TransferStatus.Complete)
        app?.let { application ->
            application.identifier?.let { receiverPackage ->
                view?.notifyReceiverTransactionSuccess(receiverPackage, kinTransferComplete.senderAddress, senderAppName, receiverPublicAddress, amount, kinTransferComplete.transactionId, kinTransferComplete.transactionMemo)
            }
        }
        resetAmount()
        requestBalance()
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
            requestBalance()
        }
    }

    private fun requestBalance() {
        balanceRequested = true
        senderServiceBinder.bind()
    }
}
