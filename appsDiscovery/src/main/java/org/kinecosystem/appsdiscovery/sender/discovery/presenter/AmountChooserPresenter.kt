package org.kinecosystem.appsdiscovery.sender.discovery.presenter

import org.kinecosystem.appsdiscovery.base.BasePresenter
import org.kinecosystem.appsdiscovery.sender.discovery.view.IAmountChooserView

class AmountChooserPresenter(private val appIconUrl: String, private val balance: Int) : BasePresenter<IAmountChooserView>(), IAmountChooserPresenter {
    var amount = 0
    override fun onAmountChanged(amount: Int) {
        this.amount = amount
        view?.setSendEnable(amount in 1..balance)
    }

    override fun onXClicked() {
        view?.onCancel()
    }

    companion object{
       val PARAM_AMOUNT = "PARAM_AMOUNT"
    }


    override fun onAttach(view: IAmountChooserView) {
        super.onAttach(view)
        view.initViews(appIconUrl, balance)
    }


    override fun onSendKinClicked() {
        view?.sendKin(amount)
    }



}