package org.kinecosystem.appsdiscovery.sender.discovery.view.customView

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import org.kinecosystem.appsdiscovery.R

class AppStateView  @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var listener: IActionClickListener? = null
    private var actionBtn:TextView
    private var title:TextView

    enum class State{
        NotInstalled,
        ReceiveKinNotSupported,
        ReceiveKinSupported
    }

    interface IActionClickListener{
        fun onActionButtonClicked()
    }

    init {
        val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.app_state_view, this, true)
        actionBtn = findViewById(R.id.actionBtn)
        title = findViewById(R.id.title)

        findViewById<TextView>(R.id.actionBtn).setOnClickListener {
            listener?.onActionButtonClicked()
        }
        update(State.ReceiveKinNotSupported)
    }

    fun setListener(listener: IActionClickListener){
        this.listener = listener
    }

    fun update(state: State){
        when(state){
            State.NotInstalled ->{
                title.text = resources.getString(R.string.app_not_installed)
                actionBtn.text = resources.getString(R.string.app_action_install)
                actionBtn.isEnabled = true
            }
            State.ReceiveKinNotSupported ->{
                title.text = resources.getString(R.string.app_kin_receive_not_supported)
                actionBtn.text = resources.getString(R.string.app_action_send_kin)
                actionBtn.isEnabled = false
            }
            State.ReceiveKinSupported ->{
                title.text = resources.getString(R.string.app_receive_kin)
                actionBtn.text = resources.getString(R.string.app_action_send_kin)
                actionBtn.isEnabled = true
            }
        }
    }
}