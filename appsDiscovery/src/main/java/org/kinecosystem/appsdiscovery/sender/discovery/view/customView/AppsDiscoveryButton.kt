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

    var hasIcon: Boolean = false
    var theme:ButtonTheme = Companion.ButtonTheme.DARK
    private var isEnabledInternal = isEnabled
    private val drawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.explorekin_icon_white)

    init {
        attrs?.let {
            val typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.AppsDiscoveryButton)
            hasIcon = typedArray.getBoolean(R.styleable.AppsDiscoveryButton_has_drawable, false)
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
        compoundDrawablePadding = resources.getDimensionPixelSize(R.dimen.button_drawable_padding)
        setOnClickListener {
            startAppsDiscoveryActivity()
        }
        onEnabledChanged()

        viewTreeObserver.addOnDrawListener {
            if (isEnabledInternal != isEnabled) {
                isEnabledInternal = isEnabled
                onEnabledChanged()
            }
        }
    }

    fun showIcon(hasIcon:Boolean){
        this.hasIcon = hasIcon
    }

    private fun onEnabledChanged() {
        updateView()
    }

    private fun updateView(){
        updateIconColor()
        invalidate()
    }

    private fun updateIconColor() {
        if (hasIcon) {
            val normalColorRes = if(theme == Companion.ButtonTheme.DARK) R.color.white else R.color.purple
            val res = if (isEnabled) normalColorRes else R.color.grey_dark
            val color = ContextCompat.getColor(context, res)

            if(normalColorRes == R.color.white){
                val currentDrawable: Drawable? = drawable?.mutate()
                currentDrawable?.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SCREEN)
                setCompoundDrawablesWithIntrinsicBounds(currentDrawable, null, null, null)

            }else {
                val currentDrawable: Drawable? = drawable?.mutate()
                currentDrawable?.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY)
                setCompoundDrawablesWithIntrinsicBounds(currentDrawable, null, null, null)
            }
        }else{
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }
    }

}