package org.kinecosystem.appstransfer.presenter

import org.kinecosystem.appstransfer.view.ITransferAmountView
<<<<<<< HEAD
=======
import org.kinecosystem.appstransfer.view.customview.TransferBarView
>>>>>>> KINNO-550_build_calc
import org.kinecosystem.common.base.BasePresenter
import org.kinecosystem.common.base.Consts
import org.kinecosystem.transfer.model.EcosystemApp
import org.kinecosystem.transfer.model.getTransactionMemo
import org.kinecosystem.transfer.model.iconUrl
import org.kinecosystem.transfer.model.name
import org.kinecosystem.transfer.repositories.EcosystemAppsRepository

class TransferAmountPresenter(appName: String, private val receiverPublicAddress: String, private val repository: EcosystemAppsRepository, private val senderServiceBinder: SenderServiceBinder) : BasePresenter<ITransferAmountView>(), ITransferAmountPresenter, SenderServiceBinder.BinderListener {

    private val DOUBLE_ZEROE = "00"
    private val ZEROE = "0"

    private var amount: Int = 0
    private var amountStr: String = ZEROE
    private var balance = 0
    private var app: EcosystemApp? = null
    private var requestBalance = false

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

<<<<<<< HEAD
=======

    override fun onTransferFailed() {
        view?.updateTransferBar(TransferBarView.TransferStatus.Failed)
    }

    override fun onTransferComplete() {
        view?.updateTransferBar(TransferBarView.TransferStatus.Complete)
    }

>>>>>>> KINNO-550_build_calc
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

    override fun startSendKin(receiverAddress: String, senderAppName: String, amount: Int, memo: String, receiverPackage: String) {
        //TODO update transfer bar
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
                application.identifier?.let {
                    view?.onStartSendingKin(amount, application.name, application.iconUrl, it)
                }
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
}
