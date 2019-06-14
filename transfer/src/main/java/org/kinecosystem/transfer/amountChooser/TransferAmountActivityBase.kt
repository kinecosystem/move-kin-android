package org.kinecosystem.transfer.amountChooser

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import org.kinecosystem.common.base.Consts
import org.kinecosystem.common.utils.load
import org.kinecosystem.transfer.R

abstract class TransferAmountActivityBase : AppCompatActivity(), ITransferAmountView {

    private val TAG = TransferAmountActivityBase::class.java.simpleName
    private var presenter: TransferAmountPresenter? = null
    private var amount: TextView? = null
    private var send: TextView? = null

    abstract fun onTransferKinButtonClicked(amount: Int)
    abstract fun titleStringRes(): Int
    abstract fun buttonStringRes(): Int

    protected fun layoutId(): Int {
        return R.layout.transfer_amount_layout
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId())
        presenter = TransferAmountPresenter(null)
        presenter?.onAttach(this)

        findViewById<View>(R.id.del).setOnLongClickListener {
            presenter?.resetAmount()
            return@setOnLongClickListener true
        }

        findViewById<TextView>(R.id.to).setText(titleStringRes())

        amount = findViewById(R.id.amount)
        send = findViewById<TextView>(R.id.send)
        send?.setText(buttonStringRes())
        send?.setOnClickListener {
            presenter?.let {
                onTransferKinButtonClicked(it.getKinAmount())
            }
        }
    }

    override fun enableSend(enable: Boolean) {
        send?.isEnabled = enable
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
            findViewById<TextView>(R.id.availableBalance).setText(balance.toString())
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

}
