package org.kinecosystem.appsdiscovery.sender.discovery.view.customView

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.support.constraint.ConstraintLayout
import android.support.constraint.Group
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.TextView
import org.kinecosystem.appsdiscovery.R


class TransferBarView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr), ITransferBarView {

    interface HideListener {
        fun onHideAnimComplete()
    }

    enum class TransferStatus {
        Started,
        Complete,
        Failed,
        FailedServiceNotFount,
        FailedServiceExported,
        FailedAmountNotFound,
        FailedReceiverAddressNotFound,
        Canceled
    }

    private var hideY = 24f
    private var showY = 0f
    private val HIDE_ANIMATION_DURATION = 300L
    private val SHOW_ANIMATION_DURATION = 450L
    private val transferringGroup: Group
    private val transferCompleteGroup: Group
    private val transferFailedGroup: Group
    private val errorTitle: TextView
    private val uiHandler = Handler(Looper.getMainLooper())

    private var toogle: Boolean = false

    init {
        val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.transfer_bar_view, this, true)

        transferCompleteGroup = findViewById(R.id.transfer_complete_group)
        transferringGroup = findViewById(R.id.transferring_group)
        transferFailedGroup = findViewById(R.id.transfer_failed_group)
        errorTitle = findViewById(R.id.errorTitle)
        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                hideY = y + height * 2
                //showY = y

                // y = hideY
                requestLayout()
            }
        })

        findViewById<TextView>(R.id.completeClose).setOnClickListener {
            if (toogle) {
                show()
            } else {
                hide()
            }
            toogle = !toogle
        }
    }

    override fun update(transferInfo: TransferInfo) {
    }

    override fun update(status: TransferStatus) {
        uiHandler.post {


            transferringGroup.visibility = View.GONE
            transferCompleteGroup.visibility = View.GONE
            transferFailedGroup.visibility = View.GONE
            when (status) {
                TransferStatus.Started -> {
                    transferringGroup.visibility = View.VISIBLE
                }
                TransferStatus.Complete -> {
                    transferCompleteGroup.visibility = View.VISIBLE
                }
                TransferStatus.Failed -> {
                    errorTitle.text = "transfer failed blockchain error"
                    transferFailedGroup.visibility = View.VISIBLE
                }
                TransferStatus.FailedServiceNotFount -> {
                    errorTitle.text = "transfer failed no service"

                    transferFailedGroup.visibility = View.VISIBLE
                }
                TransferStatus.FailedServiceExported -> {
                    errorTitle.text = "transfer not valid transfer exported"

                    transferFailedGroup.visibility = View.VISIBLE
                }
                TransferStatus.FailedAmountNotFound -> {
                    errorTitle.text = "cant get amount to transfer - update app and try again"

                    transferFailedGroup.visibility = View.VISIBLE
                }
                TransferStatus.FailedReceiverAddressNotFound -> {
                    errorTitle.text = "cant get receiver address"

                    transferFailedGroup.visibility = View.VISIBLE
                }
                TransferStatus.Canceled ->{
                    //TODO Hide

                }
            }


        }

    }

    override fun hide() {
        ValueAnimator.ofFloat(showY, hideY).apply {
            addUpdateListener { animation ->
                y = (animation.animatedValue as Float)
                requestLayout()
            }
            duration = HIDE_ANIMATION_DURATION
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator?) {
                }

                override fun onAnimationEnd(p0: Animator?) {
                    //TODO
                    // model.onHideAnimComplete()
                }

                override fun onAnimationCancel(p0: Animator?) {
                }

                override fun onAnimationStart(p0: Animator?) {
                }

            })
            start()
        }
    }

    fun show(updateLayout: () -> Unit) {
        updateLayout.invoke()
        ValueAnimator.ofFloat(hideY, showY).apply {
            addUpdateListener { animation ->
                y = (animation.animatedValue as Float)
                requestLayout()
            }
            duration = SHOW_ANIMATION_DURATION
            start()
        }
    }


    override fun show() {
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

data class TransferInfo(val senderIconUrl: String, val receiverIconUrl: String, val receiverAppName: String, val receiverPackage: String, val amount: Int)