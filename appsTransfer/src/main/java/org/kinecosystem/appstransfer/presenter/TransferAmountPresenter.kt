package org.kinecosystem.appstransfer.presenter

import android.os.Handler
import android.os.Looper
import org.kinecosystem.appstransfer.view.ITransferAmountView
import org.kinecosystem.common.base.BasePresenter
import org.kinecosystem.common.base.Consts
import org.kinecosystem.common.base.Consts.TRANSACTION_TIMEOUT
import org.kinecosystem.transfer.model.EcosystemApp
import org.kinecosystem.transfer.model.iconUrl
import org.kinecosystem.transfer.model.name
import org.kinecosystem.transfer.receiver.presenter.IErrorActionClickListener
import org.kinecosystem.transfer.repositories.EcosystemAppsRepository
import org.kinecosystem.transfer.sender.service.SendKinServiceBase
import org.kinecosystem.transfer.sender.view.TransferBarView
import org.kinecosystem.transfer.sender.view.TransferInfo
import java.util.*

class TransferAmountPresenter(receiverAppName: String, private val receiverPublicAddress: String,
                              private val repository: EcosystemAppsRepository,
                              private val senderServiceBinder: SenderServiceBinder)
    : BasePresenter<ITransferAmountView>(), ITransferAmountPresenter, SenderServiceBinder.BinderListener {

    private val DOUBLE_ZEROE = "00"
    private val ZEROE = "0"
    private val SENDIONG_TIMEOUT = 5 * 60 * 1000L //5 min

    private var amount: Int = 0
    private var amountStr: String = ZEROE
    private var balance = Consts.NO_BALANCE
    private var app: EcosystemApp? = null
    private var balanceRequested = false
    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private val sendingTimeoutTimer: Timer = Timer()
    private var afterTimeout = false
    @Volatile
    private var transferResponseReceived = false
    private val updateTimeout = {
        if (!transferResponseReceived) {
            afterTimeout = true
            view?.updateTransferBar(TransferBarView.TransferStatus.Timeout)
        }
    }
    private lateinit var showErrorTask: ShowErrorTask

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
        stopSendingTimeoutCounter()
        app?.let {
            it.appPackage?.let { receiverPackage ->
                senderServiceBinder.startSendKin(it.appId, it.name, receiverPublicAddress, amount, repository.getCurrentMemo())
                startTransferTimeOutCounter()
                view?.initTransferBar(TransferInfo(repository.getSenderAppIcon(), it.iconUrl, it.name, receiverPackage, amount))
                view?.updateTransferBar(TransferBarView.TransferStatus.Started)
                resetAmount()
            }
        }
    }

    override fun onTransferFailed(errorMessage: String, senderAddress: String, transactionMemo: String) {
        if (!afterTimeout) {
            transferResponseReceived = true
            view?.updateTransferBar(TransferBarView.TransferStatus.Failed)
            app?.let {
                it.appPackage?.let { receiverPackage ->
                    view?.notifyReceiverTransactionFailed(receiverPackage, errorMessage, senderAddress,
                            repository.getSenderAppName(), receiverPublicAddress, amount, transactionMemo)
                }
            }
        }
    }

    override fun onTransferComplete(kinTransferComplete: SendKinServiceBase.KinTransferComplete) {
        if (!afterTimeout) {
            transferResponseReceived = true
            view?.updateTransferBar(TransferBarView.TransferStatus.Complete)
            app?.let { application ->
                application.appPackage?.let { receiverPackage ->
                    view?.notifyReceiverTransactionSuccess(receiverPackage, kinTransferComplete.senderAddress,
                            repository.getSenderAppName(), receiverPublicAddress, amount, kinTransferComplete.transactionId, kinTransferComplete.transactionMemo)
                }
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
        showErrorTask = ShowErrorTask(view)
        onAmountModified()
        startSendingTimeoutCounter()
    }

    override fun onDetach() {
        sendingTimeoutTimer.cancel()
        showErrorTask.onDetach()
        mainThreadHandler.removeCallbacks { updateTimeout }
        super.onDetach()

    }

    private fun startSendingTimeoutCounter() {
        sendingTimeoutTimer.schedule(showErrorTask, SENDIONG_TIMEOUT)
    }

    private fun stopSendingTimeoutCounter() {
        sendingTimeoutTimer.cancel()
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

    private fun startTransferTimeOutCounter() {
        afterTimeout = false
        transferResponseReceived = false
        mainThreadHandler.postDelayed(updateTimeout, TRANSACTION_TIMEOUT)
    }
}

private class ShowErrorTask(var view: ITransferAmountView?) : TimerTask() {
    val handler = Handler(Looper.getMainLooper())
    override fun run() {
        handler.post {
            view?.showErrorDialog(object : IErrorActionClickListener {
                override fun onOkClicked(errorType: IErrorActionClickListener.ActionType) {
                    view?.close()
                }
            })
        }
    }

    fun onDetach() {
        view = null
    }
}