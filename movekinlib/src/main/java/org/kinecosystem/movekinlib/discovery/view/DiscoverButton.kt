package org.kinecosystem.movekinlib.discovery.view

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.widget.TextViewCompat
import android.util.AttributeSet
import android.widget.TextView
import org.kinecosystem.movekinlib.R
import org.kinecosystem.movekinlib.discovery.presenter.DiscoverButtonPresenter
import org.kinecosystem.movekinlib.discovery.presenter.IDiscoverButtonPresenter

class DiscoverButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        TextView(context, attrs, defStyleAttr), IDiscoverButtonView {

    override fun startAppsDiscoveryActivity() {
        context.startActivity(AppsDiscoveryActivity.getIntent(context))
    }

    private var presenter: IDiscoverButtonPresenter? = null

    init {
        text = resources.getString(R.string.discover)
        TextViewCompat.setTextAppearance(this, R.style.kinTextButtonRounded)
        background = ContextCompat.getDrawable(context, R.drawable.kin_button_rounded_drawable)
        presenter = DiscoverButtonPresenter()
        setOnClickListener {
            presenter?.onClicked()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        presenter?.onAttach(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        presenter?.onDetach()
    }
}