package org.kinecosystem.appsdiscovery.sender.discovery.view.customView

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.widget.TextViewCompat
import android.util.AttributeSet
import android.widget.TextView
import org.kinecosystem.appsdiscovery.R
import org.kinecosystem.appsdiscovery.sender.discovery.view.AppsDiscoveryActivity
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable


class AppsDiscoveryButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        TextView(context, attrs, defStyleAttr) {

    private fun startAppsDiscoveryActivity() {
        context.startActivity(AppsDiscoveryActivity.getIntent(context))
    }

    companion object{
        enum class ButtonTheme{
            DARK,
            LIGHT
        }
    }

    var theme:ButtonTheme = Companion.ButtonTheme.DARK

    init {
        attrs?.let {
            val typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.AppsDiscoveryButton)
            theme = ButtonTheme.values()[typedArray.getInt(R.styleable.AppsDiscoveryButton_button_theme, 0)]
            typedArray.recycle()
        }
        text = resources.getString(R.string.explore_kin)
        if(theme == Companion.ButtonTheme.DARK){
            TextViewCompat.setTextAppearance(this, R.style.kinTextButtonRounded_Purple)
            background = ContextCompat.getDrawable(context, R.drawable.kin_button_rounded_drawable)
        }else if(theme == Companion.ButtonTheme.LIGHT){
            TextViewCompat.setTextAppearance(this, R.style.kinTextButtonRounded_White)
            background = ContextCompat.getDrawable(context, R.drawable.kin_button_rounded_drawable_light)
        }
        setOnClickListener {
            startAppsDiscoveryActivity()
        }
    }
}