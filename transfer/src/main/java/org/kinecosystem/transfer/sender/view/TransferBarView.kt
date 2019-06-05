package org.kinecosystem.transfer.sender.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.support.constraint.ConstraintLayout
import android.support.constraint.Group
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import org.kinecosystem.common.utils.launchApp
import org.kinecosystem.common.utils.load
import org.kinecosystem.transfer.R

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
    private var mainView: View
    private val errorTitle: TextView
    private var receiverServiceError = ""
    private val uiHandler = Handler(Looper.getMainLooper())
    private var barView: View? = null
    private var closeX:ImageView? = null

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.transfer_bar_view, this, true)
        transferCompleteGroup = findViewById(R.id.transfer_complete_group)
        transferringGroup = findViewById(R.id.transferring_group)
        transferFailedGroup = findViewById(R.id.transfer_failed_group)
        errorTitle = findViewById(R.id.errorTitle)
        barView = findViewById(R.id.bar)
        mainView = findViewById(R.id.bar)
        mainView.alpha = 0f
        closeX = findViewById(R.id.x_close_light)
        closeX?.setOnClickListener {
            hide()
        }
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                hideY = y + height * 2
                showY = y
                y = hideY
                mainView.alpha = 1f
                requestLayout()
            }
        })
    }

    override fun updateViews(transferInfo: TransferInfo) {
        findViewById<ImageView>(R.id.receiverAppIcon).load(transferInfo.receiverIconUrl)
        findViewById<ImageView>(R.id.senderAppIcon).load(transferInfo.senderIconUrl)
        findViewById<TextView>(R.id.gotoApp).setOnClickListener {
            context.launchApp(transferInfo.receiverPackage)
        }

        receiverServiceError = context.resources.getString(R.string.transfer_receiver_service_error, transferInfo.receiverAppName)
        findViewById<TextView>(R.id.message).text = context.resources.getString(R.string.transfer_sending_message, transferInfo.amount, transferInfo.receiverAppName)
        findViewById<TextView>(R.id.completeMessage).text = context.resources.getString(R.string.transfer_complete_message, transferInfo.amount)
        with(findViewById<TextView>(R.id.gotoApp)) {
            text = context.resources.getString(R.string.go_to_app, transferInfo.receiverAppName)
            paintFlags = Paint.UNDERLINE_TEXT_FLAG
        }
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
                    transferringGroup.visibility = View.VISIBLE
                    closeX?.visibility = View.GONE
                    barView?.setBackgroundColor(ContextCompat.getColor(context, R.color.dark))
                    show()
                }
                TransferStatus.Complete -> {
                    hideAndShow {
                        barView?.setBackgroundColor(ContextCompat.getColor(context, R.color.purple))
                        transferringGroup.visibility = View.GONE
                        transferFailedGroup.visibility = View.GONE
                        transferCompleteGroup.visibility = View.VISIBLE
                        closeX?.visibility = View.VISIBLE
                    }
                }
                TransferStatus.Failed -> {
                    hideAndShow {
                        barView?.setBackgroundColor(ContextCompat.getColor(context, R.color.dark))
                        transferringGroup.visibility = View.GONE
                        transferCompleteGroup.visibility = View.GONE
                        errorTitle.text = context.getString(R.string.transfer_failed_error)
                        transferFailedGroup.visibility = View.VISIBLE
                        closeX?.visibility = View.VISIBLE
                    }
                }
                TransferStatus.Timeout -> {
                    hideAndShow {
                        barView?.setBackgroundColor(ContextCompat.getColor(context, R.color.dark))
                        transferringGroup.visibility = View.GONE
                        transferCompleteGroup.visibility = View.GONE
                        errorTitle.text = context.getString(R.string.transfer_failed_timeout)
                        transferFailedGroup.visibility = View.VISIBLE
                        closeX?.visibility = View.VISIBLE
                    }
                }
                TransferStatus.FailedReceiverError -> {
                    hideAndShow {
                        barView?.setBackgroundColor(ContextCompat.getColor(context, R.color.dark))
                        transferringGroup.visibility = View.GONE
                        transferCompleteGroup.visibility = View.GONE
                        errorTitle.text = receiverServiceError
                        transferFailedGroup.visibility = View.VISIBLE
                        closeX?.visibility = View.VISIBLE
                    }
                }
                TransferStatus.FailedConnectionError -> {
                    hideAndShow {
                        barView?.setBackgroundColor(ContextCompat.getColor(context, R.color.dark))
                        transferringGroup.visibility = View.GONE
                        transferCompleteGroup.visibility = View.GONE
                        errorTitle.text = context.getString(R.string.transfer_connection_error)
                        transferFailedGroup.visibility = View.VISIBLE
                        closeX?.visibility = View.VISIBLE
                    }
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