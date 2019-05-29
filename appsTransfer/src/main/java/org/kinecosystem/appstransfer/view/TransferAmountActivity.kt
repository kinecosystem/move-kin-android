package org.kinecosystem.appstransfer.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import org.kinecosystem.transfer.repositories.EcosystemAppsLocalRepo
import org.kinecosystem.transfer.repositories.EcosystemAppsRemoteRepo
import org.kinecosystem.transfer.repositories.EcosystemAppsRepository
import org.kinecosystem.transfer.sender.manager.TransferManager

class TransferAmountActivity : AppCompatActivity(), ITransferAmountView {
    override fun startSendKin(receiverAddress: String, senderAppName: String, amount: Int, memo: String, receiverPackage: String) {
      //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initTransfersInfo() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

    fun onDigitClicked(view:View){

        Log.d("####", "##### ${view.tag}")
        //presenter?.onDigitClicked(view.tag.toString())
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

    private var presenter: TransferAmountPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appName = intent.getStringExtra(PARAM_APP_NAME)
        if (appName.isNullOrBlank()) {
            finish()
        }
        setContentView(R.layout.transfer_amount_activity)

        presenter = TransferAmountPresenter(appName, EcosystemAppsRepository.getInstance(packageName, EcosystemAppsLocalRepo(this), EcosystemAppsRemoteRepo(), Handler(Looper.getMainLooper())), TransferManager(this), SenderServiceBinder(this), Handler(Looper.getMainLooper())
        )
        presenter?.onAttach(this)
        findViewById<ImageView>(R.id.close_x).setOnClickListener {
            presenter?.onCloseClicked()
        }
        findViewById<TextView>(R.id.send).setOnClickListener {
            presenter?.onSendKinClicked()
        }
    }

    companion object {
        private const val PARAM_APP_NAME= "PARAM_APP_NAME"

        fun getIntent(context: Context, appName: String): Intent {
            val intent = Intent(context, TransferAmountActivity::class.java)
            intent.putExtra(PARAM_APP_NAME, appName)
            return intent
        }
    }
}
