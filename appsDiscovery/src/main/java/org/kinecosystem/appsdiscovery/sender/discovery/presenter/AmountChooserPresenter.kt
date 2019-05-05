package org.kinecosystem.appsdiscovery.sender.discovery.presenter

import org.kinecosystem.appsdiscovery.base.BasePresenter
import org.kinecosystem.appsdiscovery.sender.discovery.view.IAmountChooserView

class AmountChooserPresenter(private val appIconUrl: String, private val balance: Int) : BasePresenter<IAmountChooserView>(), IAmountChooserPresenter {

    companion object{
       val PARAM_AMOUNT = "PARAM_AMOUNT"
    }

    //TODO update
    var amountChosen = 89

    override fun onAttach(view: IAmountChooserView) {
        super.onAttach(view)
        view.initViews(appIconUrl, balance)
    }


    override fun onSendKinClicked() {
        view?.sendKin(amountChosen)
    }



}