package org.kinecosystem.appstransfer.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import org.kinecosystem.appstransfer.R
import org.kinecosystem.appstransfer.presenter.SenderServiceBinder
import org.kinecosystem.appstransfer.presenter.TransferAmountPresenter
import org.kinecosystem.common.base.Consts
import org.kinecosystem.common.utils.load
import org.kinecosystem.transfer.receiver.presenter.AccountInfoError
import org.kinecosystem.transfer.receiver.presenter.IErrorActionClickListener
import org.kinecosystem.transfer.receiver.service.ReceiveKinNotifier
import org.kinecosystem.transfer.receiver.service.ServiceConfigurationException
import org.kinecosystem.transfer.receiver.view.AccountInfoErrorDialog
import org.kinecosystem.transfer.repositories.EcosystemAppsRepository
import org.kinecosystem.transfer.sender.view.TransferBarView
import org.kinecosystem.transfer.sender.view.TransferInfo

class TransferAmountActivity : AppCompatActivity(), ITransferAmountView {

    private val TAG = TransferAmountActivity::class.java.simpleName
    private var presenter: TransferAmountPresenter? = null
    private var amount: TextView? = null
    private var send: TextView? = null
    private var transferBarView: TransferBarView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appName = intent.getStringExtra(PARAM_APP_NAME)
        val receiverPublicAddress = intent.getStringExtra(PARAM_RECEIVER_ADDRESS)

        if (appName.isNullOrBlank() || receiverPublicAddress.isNullOrBlank()) {
            finish()
        }
        setContentView(R.layout.transfer_amount_activity)
        presenter = TransferAmountPresenter(appName, receiverPublicAddress,
                EcosystemAppsRepository.getInstance(this), SenderServiceBinder(this))
        presenter?.onAttach(this)
        transferBarView = findViewById(R.id.transferBar)
        findViewById<ImageView>(R.id.close_x).setOnClickListener {
            presenter?.onCloseClicked()
        }

        findViewById<View>(R.id.del).setOnLongClickListener {
            presenter?.resetAmount()
            return@setOnLongClickListener true
        }

        send = findViewById(R.id.send)
        send?.setOnClickListener {
            presenter?.onSendKinClicked()
        }
        amount = findViewById(R.id.amount)
    }

    override fun updateTransferBar(status: TransferBarView.TransferStatus) {
        transferBarView?.updateStatus(status)
    }

    override fun initTransferBar(transferInfo: TransferInfo) {
        transferBarView?.updateViews(transferInfo)
    }

    override fun enableSend(enable: Boolean) {
        send?.isEnabled = enable
    }

    override fun notifyReceiverTransactionFailed(receiverPackageName: String, errorMessage: String, senderAddress: String, senderAppName: String, receiverAddress: String, amount: Int, transactionMemo: String) {
        try {
            ReceiveKinNotifier.notifyTransactionFailed(baseContext, receiverPackageName, errorMessage, senderAddress, senderAppName, receiverAddress, amount, transactionMemo)
            Log.d(TAG, "Receiver was notified of transaction failed")

        } catch (e: ServiceConfigurationException) {
            Log.d(TAG, "Error notifying the receiver of transaction failed ${e.message}")
            e.printStackTrace()
        }
    }

    override fun notifyReceiverTransactionSuccess(receiverPackageName: String, senderAddress: String, senderAppName: String, receiverAddress: String, amount: Int, transactionId: String, transactionMemo: String) {
        try {
            ReceiveKinNotifier.notifyTransactionCompleted(baseContext, receiverPackageName, senderAddress, senderAppName, receiverAddress, amount, transactionId, transactionMemo)
            Log.d(TAG, "Receiver was notified of transaction complete")
        } catch (e: ServiceConfigurationException) {
            Log.d(TAG, "Error notifying the receiver of transaction complete ${e.message}")
            e.printStackTrace()
        }
    }

    override fun showErrorDialog(listener: IErrorActionClickListener) {
        val dialog = AccountInfoErrorDialog(this, AccountInfoError(IErrorActionClickListener.ActionType.None, resources.getString(R.string.timeout_occurred)), false, listener)
        dialog.show()
    }

    override fun updateAmount(amount: String) {
        this.amount?.text = amount
    }

    override fun updateIconUrl(url: String) {
        findViewById<ImageView>(R.id.appIcon).load(url)
    }

    override fun updateBalance(balance: Int) {
        if (balance == Consts.NO_BALANCE) {
            findViewById<TextView>(R.id.availableBalance).visibility = View.INVISIBLE
            findViewById<ImageView>(R.id.currency).visibility = View.INVISIBLE
            findViewById<TextView>(R.id.postBalance).visibility = View.INVISIBLE
        } else {
            findViewById<TextView>(R.id.availableBalance).text = balance.toString()
        }
    }

    fun onDigitClicked(view: View) {
        presenter?.onDigitClicked(view.tag.toString())
    }

    fun onZerosClicked(view: View) {
        presenter?.onZerosClicked()
    }

    fun onDellClicked(view: View) {
        presenter?.onDellClicked()
    }

    override fun close() {
        finish()
    }

    override fun onResume() {
        super.onResume()
        presenter?.onResume()
    }

    override fun onPause() {
        presenter?.onPause()
        super.onPause()
    }

    companion object {
        private const val PARAM_APP_NAME = "PARAM_APP_NAME"
        private const val PARAM_RECEIVER_ADDRESS = "PARAM_RECEIVER_ADDRESS"


        fun getIntent(context: Context, appName: String, receiverPublicAddress: String): Intent {
            val intent = Intent(context, TransferAmountActivity::class.java)
            intent.putExtra(PARAM_APP_NAME, appName)
            intent.putExtra(PARAM_RECEIVER_ADDRESS, receiverPublicAddress)
            return intent
        }
    }
}
