package org.kinecosystem.appsdiscovery.sender.discovery.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import org.kinecosystem.appsdiscovery.R
import org.kinecosystem.appsdiscovery.sender.discovery.presenter.AmountChooserPresenter

class AmountChooserActivity : AppCompatActivity(), IAmountChooserView {

    override fun initViews(receiverAppIconUrl: String, balance: Int) {
        findViewById<TextView>(R.id.appName).setText(receiverAppIconUrl)
        //findViewById<TextView>(R.id.availableBalance).setText("NNNO BBAKANCE")
       // findViewById<TextView>(R.id.availableBalance).setText(balance)

        findViewById<TextView>(R.id.send).setOnClickListener {
            presenter?.onSendKinClicked()
        }
    }


    override fun sendKin(amount:Int) {
        val returnIntent = Intent()
        returnIntent.putExtra(AmountChooserPresenter.PARAM_AMOUNT, amount)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    override fun onCancel() {
        val returnIntent = Intent()
        setResult(Activity.RESULT_CANCELED, returnIntent)
        finish()
    }

    private var presenter: AmountChooserPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appIconUrl = intent.getStringExtra(PARAM_APP_ICON_URL)
        if (appIconUrl.isNullOrBlank()) {
            finish()
        }
        val balance = intent.getIntExtra(PARAM_BALANCE, 0)
        setContentView(R.layout.amount_chooser_activity)
        presenter = AmountChooserPresenter(appIconUrl, balance)
        presenter?.onAttach(this)
    }

    companion object {
        const val PARAM_APP_ICON_URL = "PARAM_APP_ICON_URL"
        const val PARAM_BALANCE = "PARAM_BALANCE"

        fun getIntent(context: Context, appIconUrl: String, balance: Int): Intent {
            val intent = Intent(context, AmountChooserActivity::class.java)
            intent.putExtra(PARAM_APP_ICON_URL, appIconUrl)
            intent.putExtra(PARAM_BALANCE, balance.toInt())
            return intent
        }
    }

}