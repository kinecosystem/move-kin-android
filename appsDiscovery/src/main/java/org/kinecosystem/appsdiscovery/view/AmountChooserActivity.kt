package org.kinecosystem.appsdiscovery.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import org.kinecosystem.appsdiscovery.R
import org.kinecosystem.appsdiscovery.presenter.AmountChooserPresenter
import org.kinecosystem.transfer.base.Consts
import org.kinecosystem.transfer.utils.load

class AmountChooserActivity : AppCompatActivity(), IAmountChooserView {

    private var presenter: AmountChooserPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appIconUrl = intent.getStringExtra(PARAM_APP_ICON_URL)
        if (appIconUrl.isNullOrBlank()) {
            finish()
        }
        val balance = intent.getIntExtra(PARAM_BALANCE, Consts.NO_BALANCE)
        setContentView(R.layout.amount_chooser_activity)
        presenter = AmountChooserPresenter(appIconUrl, balance)
        presenter?.onAttach(this)
    }

    override fun initViews(receiverAppIconUrl: String, balance: Int) {
        if (balance == Consts.NO_BALANCE) {
            findViewById<TextView>(R.id.availableBalance).visibility = View.INVISIBLE
            findViewById<ImageView>(R.id.currency).visibility = View.INVISIBLE
            findViewById<TextView>(R.id.postBalance).visibility = View.INVISIBLE
        } else {
            findViewById<TextView>(R.id.availableBalance).text = balance.toString()
        }
        findViewById<ImageView>(R.id.appIcon).load(receiverAppIconUrl)
        findViewById<TextView>(R.id.send).isEnabled = false
        findViewById<ImageView>(R.id.close_x).setOnClickListener {
            presenter?.onXClicked()
        }

        val amountView = findViewById<EditText>(R.id.amount)
        amountView.setOnClickListener {
            amountView.setSelection(amountView.text.length)
        }
        amountView.addTextChangedListener(presenter)

        findViewById<TextView>(R.id.send).setOnClickListener {
            presenter?.onSendKinClicked()
        }
    }

    override fun setSendEnable(isEnabled: Boolean) {
        findViewById<TextView>(R.id.send).isEnabled = isEnabled
    }


    override fun sendKin(amount: Int) {
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

    companion object {
        const val PARAM_APP_ICON_URL = "PARAM_APP_ICON_URL"
        const val PARAM_BALANCE = "PARAM_BALANCE"

        fun getIntent(context: Context, appIconUrl: String, balance: Int): Intent {
            val intent = Intent(context, AmountChooserActivity::class.java)
            intent.putExtra(PARAM_APP_ICON_URL, appIconUrl)
            intent.putExtra(PARAM_BALANCE, balance)
            return intent
        }
    }
}