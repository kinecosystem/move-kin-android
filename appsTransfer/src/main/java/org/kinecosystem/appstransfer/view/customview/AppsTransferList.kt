package org.kinecosystem.appstransfer.view.customview

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.kinecosystem.appstransfer.R
import org.kinecosystem.appstransfer.presenter.AppsTransferListPresenter
import org.kinecosystem.appstransfer.presenter.IAppsTransferListPresenter
import org.kinecosystem.common.utils.isAppInstalled
import org.kinecosystem.common.utils.load
import org.kinecosystem.transfer.model.*
import org.kinecosystem.transfer.repositories.EcosystemAppsLocalRepo
import org.kinecosystem.transfer.repositories.EcosystemAppsRemoteRepo
import org.kinecosystem.transfer.repositories.EcosystemAppsRepository


class AppsTransferList @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        RecyclerView(context, attrs, defStyleAttr), IAppsTransferListView {

    interface AppClickListener {
        fun onAppClicked(app: EcosystemApp, isInstalled: Boolean)
    }

    private var presenter: IAppsTransferListPresenter? = null
    var clickListener: AppClickListener? = null
        set(value) {
            (adapter as AppsTransferAdapter).clickListener = value
            field = value
        }

    init {
        layoutManager = LinearLayoutManager(context)
        val repository = EcosystemAppsRepository.getInstance(context.packageName, EcosystemAppsLocalRepo(context), EcosystemAppsRemoteRepo(), Handler(Looper.getMainLooper()))
        presenter = AppsTransferListPresenter(repository)
        adapter = AppsTransferAdapter(context)

        addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
            override fun onViewDetachedFromWindow(v: View?) {
                presenter = null
            }

            override fun onViewAttachedToWindow(v: View?) {
            }

        })
    }

    fun invalidateApps() {
        (adapter as AppsTransferAdapter).invalidateData()
    }

    override fun updateData(apps: List<EcosystemApp>) {
        (adapter as AppsTransferAdapter).updateData(apps)
    }

    override fun setLoadingListener(loadingListener: AppsTransferListPresenter.LoadingListener) {
        presenter?.setLoadingListener(loadingListener)
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

private class AppsTransferAdapter(private val context: Context) : RecyclerView.Adapter<AppsTransferAdapter.ViewHolder>() {

    var apps: List<EcosystemApp> = listOf()
    var clickListener: AppsTransferList.AppClickListener? = null


    fun updateData(apps: List<EcosystemApp>) {
        this.apps = apps
        invalidateData()
    }

    fun invalidateData() {
        sort()
        notifyDataSetChanged()
    }

    fun sort() {
        apps = apps.sortedWith(compareBy({ !it.canSendAndReceiveKin() }, { !context.isAppInstalled(it.identifier!!) }, { it.name }))
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.app_transfer_item, parent, false)
        return ViewHolder(context, view)
    }

    override fun getItemCount(): Int {
        return apps.size
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val app: EcosystemApp = apps[position]
        holder?.bind(app)
        holder?.actionText?.setOnClickListener {
            clickListener?.onAppClicked(app, context.isAppInstalled(app.identifier!!))
        }
    }

    class ViewHolder(private val context: Context, val view: View) : RecyclerView.ViewHolder(view) {
        private val icon: ImageView = view.findViewById(R.id.appIcon)
        private val appName: TextView = view.findViewById(R.id.appName)
        private val status: TextView = view.findViewById(R.id.status)
        val actionText: TextView = view.findViewById(R.id.actionText)

        fun bind(app: EcosystemApp) {
            appName.text = app.name
            icon.load(app.iconUrl)
            app.identifier?.let {
                if (!app.canSendAndReceiveKin()) {
                    appName.gravity = Gravity.TOP
                    status.visibility = View.VISIBLE
                    status.text = context.getString(R.string.apps_transfer_coming_soon)
                    actionText.visibility = View.GONE
                } else if (context.isAppInstalled(it)) {
                    actionText.background = ContextCompat.getDrawable(context, R.drawable.kin_button_rounded_drawable)
                    actionText.text = context.getString(R.string.apps_transfer_kin)
                    actionText.setTextColor(ContextCompat.getColor(context, R.color.kin_transfer_btn_text))
                    actionText.visibility = View.VISIBLE
                    status.visibility = View.GONE
                    appName.gravity = Gravity.CENTER_VERTICAL
                } else {
                    appName.gravity = Gravity.TOP
                    actionText.background = ContextCompat.getDrawable(context, R.drawable.kin_button_rounded_hollow_drawable)
                    actionText.text = context.getString(R.string.apps_transfer_learn_more)
                    status.text = context.getString(R.string.apps_transfer_not_installed)
                    actionText.setTextColor(ContextCompat.getColor(context, R.color.kin_transfer_btn_hollow_text))
                    status.visibility = View.VISIBLE
                    actionText.visibility = View.VISIBLE
                }
            }
        }
    }
}