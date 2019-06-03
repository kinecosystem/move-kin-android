package org.kinecosystem.appstransfer.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import org.kinecosystem.appstransfer.R
import org.kinecosystem.appstransfer.presenter.SenderServiceBinder
import org.kinecosystem.appstransfer.presenter.TransferAmountPresenter
import org.kinecosystem.common.base.Consts
import org.kinecosystem.common.utils.load
import org.kinecosystem.transfer.repositories.EcosystemAppsLocalRepo
import org.kinecosystem.transfer.repositories.EcosystemAppsRemoteRepo
import org.kinecosystem.transfer.repositories.EcosystemAppsRepository

class TransferAmountActivity : AppCompatActivity(), ITransferAmountView {

    private var presenter: TransferAmountPresenter? = null
    private var amount:TextView? = null
    private var send:TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appName = intent.getStringExtra(PARAM_APP_NAME)
        val receiverPublicAddress = intent.getStringExtra(PARAM_RECEIVER_ADDRESS)

        if (appName.isNullOrBlank() || receiverPublicAddress.isNullOrBlank()) {
            finish()
        }
        setContentView(R.layout.transfer_amount_activity)

        presenter = TransferAmountPresenter(appName, receiverPublicAddress, EcosystemAppsRepository.getInstance(packageName, EcosystemAppsLocalRepo(this), EcosystemAppsRemoteRepo(), Handler(Looper.getMainLooper())), SenderServiceBinder(this))
        presenter?.onAttach(this)
        findViewById<ImageView>(R.id.close_x).setOnClickListener {
            presenter?.onCloseClicked()
        }

        findViewById<View>(R.id.del).setOnLongClickListener {
            presenter?.onFullDel()
            return@setOnLongClickListener true
        }

        send = findViewById(R.id.send)
        send?.setOnClickListener {
            presenter?.onSendKinClicked()
        }
        amount = findViewById(R.id.amount)
    }

    override fun onStartSendingKin(amount: Int, appName: String, appIconUrl: String, appPackage: String) {
        //TODO update transfer bar
    }

    override fun setSendEnable(isEnabled: Boolean) {
        send?.isEnabled = isEnabled
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
