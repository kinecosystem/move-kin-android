package org.kinecosystem.onewallet.view

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import org.kinecosystem.onewallet.R

/**
 * OneWallet progress bar
 */
class OneWalletProgressBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr) {

    var bodyTextView: TextView
    var text: String = ""
        set(value) {
            bodyTextView.text = value
        }

    init {
        // text = "Linking status will be displayed here"
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.link_progress_bar, this, true) as ConstraintLayout
        bodyTextView = view.findViewById<TextView>(R.id.progress_body) as TextView
        visibility = View.INVISIBLE
    }


}