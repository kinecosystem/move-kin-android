package org.kinecosystem.appsdiscovery.view.customView

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.constraint.Group
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.ImageView
import android.widget.TextView
import org.kinecosystem.appsdiscovery.R
import org.kinecosystem.common.utils.launchApp
import org.kinecosystem.common.utils.load


class TransferBarView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr), ITransferBarView {

    enum class TransferStatus {
        Started,
        Complete,
        Failed,
        Timeout,
        FailedReceiverError,
        FailedConnectionError
    }

    private var hideY = 0f
    private var showY = 0f
    private val HIDE_ANIMATION_DURATION = 300L
    private val SHOW_ANIMATION_DURATION = 450L
    private val transferringGroup: Group
    private val transferCompleteGroup: Group
    private val transferFailedGroup: Group
    private val errorTitle: TextView
    private var receiverServiceError = ""

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.transfer_bar_view, this, true)
        transferCompleteGroup = findViewById(R.id.transfer_complete_group)
        transferringGroup = findViewById(R.id.transferring_group)
        transferFailedGroup = findViewById(R.id.transfer_failed_group)
        errorTitle = findViewById(R.id.errorTitle)
        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                hideY = y + height * 2
                showY = y
                y = hideY
                requestLayout()
            }
        })

        findViewById<TextView>(R.id.completeClose).setOnClickListener {
            hide()
        }
        findViewById<TextView>(R.id.completeMessage).setOnClickListener {
            hide()
        }
        findViewById<TextView>(R.id.errorClose).setOnClickListener {
            hide()
        }
    }

    override fun updateViews(transferInfo: TransferInfo) {
        findViewById<ImageView>(R.id.receiverAppIcon).load(transferInfo.receiverIconUrl)
        findViewById<ImageView>(R.id.senderAppIcon).load(transferInfo.senderIconUrl)
        val receiverAppIcon = findViewById<ImageView>(R.id.completeAppIcon)
        receiverAppIcon.load(transferInfo.receiverIconUrl)
        receiverAppIcon.setOnClickListener {
            context.launchApp(transferInfo.receiverPackage)
        }
        findViewById<TextView>(R.id.gotoApp).setOnClickListener {
            context.launchApp(transferInfo.receiverPackage)
        }
        findViewById<TextView>(R.id.gotoApp).text = context.getString(R.string.go_to_app, transferInfo.receiverAppName)
        receiverServiceError = context.resources.getString(R.string.transfer_receiver_service_error, transferInfo.receiverAppName)
    }

    override fun updateAmount(amount: Int) {
        findViewById<TextView>(R.id.message).text = context.resources.getString(R.string.transfer_sending_message, amount)
        findViewById<TextView>(R.id.completeMessage).text = context.resources.getString(R.string.transfer_complete_message, amount)
    }

    override fun updateStatus(status: TransferStatus) {
        when (status) {
            TransferStatus.Started -> {
                transferCompleteGroup.visibility = View.GONE
                transferFailedGroup.visibility = View.GONE
                transferringGroup.visibility = View.VISIBLE
                show()
            }
            TransferStatus.Complete -> {
                hideAndShow {
                    transferringGroup.visibility = View.GONE
                    transferFailedGroup.visibility = View.GONE
                    transferCompleteGroup.visibility = View.VISIBLE
                }
            }
            TransferStatus.Failed -> {
                hideAndShow {
                    transferringGroup.visibility = View.GONE
                    transferCompleteGroup.visibility = View.GONE
                    errorTitle.text = context.getString(R.string.transfer_failed_error)
                    transferFailedGroup.visibility = View.VISIBLE
                }
            }
            TransferStatus.Timeout -> {
                hideAndShow {
                    transferringGroup.visibility = View.GONE
                    transferCompleteGroup.visibility = View.GONE
                    errorTitle.text = context.getString(R.string.transfer_failed_timeout)
                    transferFailedGroup.visibility = View.VISIBLE
                }
            }
            TransferStatus.FailedReceiverError -> {
                hideAndShow {
                    transferringGroup.visibility = View.GONE
                    transferCompleteGroup.visibility = View.GONE
                    errorTitle.text = receiverServiceError
                    transferFailedGroup.visibility = View.VISIBLE
                }
            }
            TransferStatus.FailedConnectionError -> {
                hideAndShow {
                    transferringGroup.visibility = View.GONE
                    transferCompleteGroup.visibility = View.GONE
                    errorTitle.text = context.getString(R.string.transfer_connection_error)
                    transferFailedGroup.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun hide() {
        ValueAnimator.ofFloat(y, hideY).apply {
            addUpdateListener { animation ->
                y = (animation.animatedValue as Float)
                requestLayout()
            }
            duration = HIDE_ANIMATION_DURATION
            start()
        }
    }

    private fun hideAndShow(updateLayout: () -> Unit) {
        clearAnimation()
        ValueAnimator.ofFloat(y, hideY).apply {
            addUpdateListener { animation ->
                y = (animation.animatedValue as Float)
                requestLayout()
            }
            duration = (HIDE_ANIMATION_DURATION * (hideY - y) / (hideY - showY)).toLong()
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator?) {
                }

                override fun onAnimationEnd(p0: Animator?) {
                    updateLayout.invoke()
                    show()
                }

                override fun onAnimationCancel(p0: Animator?) {}

                override fun onAnimationStart(p0: Animator?) {}

            })
            start()
        }
    }


    private fun show() {
        ValueAnimator.ofFloat(hideY, showY).apply {
            addUpdateListener { animation ->
                y = (animation.animatedValue as Float)
                requestLayout()
            }
            duration = SHOW_ANIMATION_DURATION
            start()
        }
    }

}

data class TransferInfo(val senderIconUrl: String, val receiverIconUrl: String, val receiverAppName: String, val receiverPackage: String)