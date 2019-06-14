package org.kinecosystem.transfer.amountChooser

import org.kinecosystem.common.base.BasePresenter
import org.kinecosystem.common.base.Consts


class TransferAmountPresenter(private val appIconUrl: String?) : BasePresenter<ITransferAmountView>(), ITransferAmountPresenter {
    private val DOUBLE_ZEROE = "00"
    private val ZEROE = "0"

    private var amount: Int = 0
    private var amountStr: String = ZEROE
    private var balance = Consts.NO_BALANCE

    override fun getKinAmount(): Int {
        return amount
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

    override fun onBalanceReceived(balance: Int) {
        this.balance = balance
        view?.updateBalance(balance)
        onAmountModified()
    }

    override fun onBalanceFailed() {
        balance = Consts.NO_BALANCE
    }

    override fun onAttach(view: ITransferAmountView) {
        super.onAttach(view)
        appIconUrl?.let {
            view.updateIconUrl(it)
        }
        onAmountModified()
    }

    override fun onCloseClicked() {
        view?.close()
    }


}
