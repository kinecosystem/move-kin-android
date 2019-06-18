package org.kinecosystem.onewallet.view

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import org.kinecosystem.common.base.IBaseView
import org.kinecosystem.onewallet.R

/**
 * OneWallet progress bar
 */
class UnifiedBalanceBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr), IBaseView {

    val balanceView: TextView
    val manageWalletsView: TextView

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.unified_balance_bar, this, true) as ConstraintLayout
        manageWalletsView = view.findViewById<TextView>(R.id.unified_manage_wallets)
        balanceView = view.findViewById<TextView>(R.id.unified_balance)
    }

    fun updateBalance(amount: Int) {
        balanceView.text = "$amount"
    }

    fun setManageWalletsListener(clickListener: () -> Unit) {
        manageWalletsView.setOnClickListener {
            clickListener.invoke()
        }
    }
}