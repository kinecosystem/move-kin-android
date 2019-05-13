package org.kinecosystem.appsdiscovery.sender.discovery.presenter

import android.text.Editable
import android.text.TextWatcher
import org.kinecosystem.appsdiscovery.base.BasePresenter
import org.kinecosystem.appsdiscovery.sender.discovery.view.IAmountChooserView

class AmountChooserPresenter(private val appIconUrl: String, private val balance: Int) : BasePresenter<IAmountChooserView>(), IAmountChooserPresenter, TextWatcher {

    companion object {
        val PARAM_AMOUNT = "PARAM_AMOUNT"
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
                if (editable[0] == '0') {
                    editable.delete(0, 1)
                }
            }
            amount = 0
            if (editable.isNotEmpty()) {
                amount = try {
                    editable.toString().toInt()
                } catch (e: NumberFormatException) {
                    editable.delete(editable.length - 2, editable.length - 1)
                    editable.toString().toInt()
                }
            }
            view?.setSendEnable(amount in 1..balance || balance == 0)
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
}