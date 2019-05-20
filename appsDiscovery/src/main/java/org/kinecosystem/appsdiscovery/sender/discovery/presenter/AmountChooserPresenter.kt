package org.kinecosystem.appsdiscovery.sender.discovery.presenter

import android.text.Editable
import android.text.TextWatcher
import org.kinecosystem.common.base.BasePresenter
import org.kinecosystem.appsdiscovery.sender.discovery.view.IAmountChooserView
import org.kinecosystem.common.base.Consts

class AmountChooserPresenter(private val appIconUrl: String, private val balance: Int) : BasePresenter<IAmountChooserView>(), IAmountChooserPresenter, TextWatcher {

    companion object {
        const val PARAM_AMOUNT = "PARAM_AMOUNT"
    }

    var amount = 0

    override fun onXClicked() {
        view?.onCancel()
    }

    override fun onAttach(view: IAmountChooserView) {
        super.onAttach(view)
        view.initViews(appIconUrl, balance)
    }

    override fun onSendKinClicked() {
        view?.sendKin(amount)
    }

    override fun afterTextChanged(editable: Editable?) {
        editable?.let {
            if (editable.length >= 2) {
                //remove any leading zero
                if (editable[0] == '0') {
                    editable.delete(0, 1)
                }
            }
            amount = 0
            if (editable.isNotEmpty()) {
                amount = try {
                    editable.toString().toInt()
                } catch (e: NumberFormatException) {
                    //the last input was not in int range so take the last input and trim the last number and show it to the result to the user
                    //so NumberFormatException cant accord again
                    editable.delete(editable.length - 2, editable.length - 1)
                    editable.toString().toInt()
                }
            }
            //if balance is zero hence the balance was not initialized with real value
            view?.setSendEnable(amount in 1..balance || balance == Consts.NO_BALANCE)
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
}