package org.kinecosystem.appstransfer.view.customview

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.support.v4.content.ContextCompat
import android.support.v4.widget.TextViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
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
import org.kinecosystem.common.utils.navigateToUrl
import org.kinecosystem.transfer.model.EcosystemApp
import org.kinecosystem.transfer.model.canTransferKin
import org.kinecosystem.transfer.model.iconUrl
import org.kinecosystem.transfer.model.name
import org.kinecosystem.transfer.repositories.DiscoveryAppsLocal
import org.kinecosystem.transfer.repositories.DiscoveryAppsRemote
import org.kinecosystem.transfer.repositories.DiscoveryAppsRepository


class AppsTransferList @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        RecyclerView(context, attrs, defStyleAttr), IAppsTransferListView {

    private var presenter: IAppsTransferListPresenter? = null

    init {
        layoutManager = LinearLayoutManager(context)
        val discoveryAppsRepository = DiscoveryAppsRepository.getInstance(context.packageName, DiscoveryAppsLocal(context), DiscoveryAppsRemote(), Handler(Looper.getMainLooper()))
        presenter = AppsTransferListPresenter(discoveryAppsRepository)
        presenter?.let {
            adapter = AppsTransferAdapter(context, it)
        }

        addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
            override fun onViewDetachedFromWindow(v: View?) {
                presenter = null
            }

            override fun onViewAttachedToWindow(v: View?) {
            }

        })
    }

    override fun navigateToUrl(googlePlayUrl: String) {
        context.navigateToUrl(googlePlayUrl)
    }

    override fun updateData(apps: List<EcosystemApp>) {
        (adapter as AppsTransferAdapter).updateData(apps)
    }

    override fun setLoadingListener(loadingListener: AppsTransferListPresenter.LoadingListener) {
        presenter?.setLoadingListener(loadingListener)
    }

    override fun transferToApp(app: EcosystemApp) {
        //TODO
        Log.d("###", "### start transfer to app ${app.identifier}")
        // context.startActivity(AppInfoActivity.getIntent(context, app.name))
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (hasWindowFocus) {
            Log.d("###", "### list on resumed")
            (adapter as AppsTransferAdapter).invalidateData()
            Log.d("###", "###  after notifyDataSetChanged")
        } else {
            Log.d("###", "### list on puaused")

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

class AppsTransferAdapter(private val context: Context, private val presenter: IAppsTransferListPresenter) : RecyclerView.Adapter<AppsTransferAdapter.ViewHolder>() {

    var apps: List<EcosystemApp> = listOf()

    fun updateData(apps: List<EcosystemApp>) {
        this.apps = apps.sortedByDescending { context.isAppInstalled(it.identifier!!) }
        notifyDataSetChanged()
    }

    fun invalidateData() {
        apps = apps.sortedByDescending { context.isAppInstalled(it.identifier!!) }
        notifyDataSetChanged()
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
            presenter.onAppClicked(app)
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
                if (context.isAppInstalled(it)) {
                    if (app.canTransferKin) {
                        actionText.background = ContextCompat.getDrawable(context, R.drawable.kin_button_rounded_drawable)
                        actionText.text = context.getString(R.string.transfer_kin)
                        TextViewCompat.setTextAppearance(actionText, R.style.kinTextButtonRounded)
                        actionText.setTextColor(ContextCompat.getColor(context, R.color.kin_btn_text))
                        actionText.visibility = View.VISIBLE
                        status.visibility = View.GONE
                        appName.gravity = Gravity.CENTER_VERTICAL
                    } else {
                        appName.gravity = Gravity.TOP
                        status.visibility = View.VISIBLE
                        status.text = context.getString(R.string.coming_soon)
                        actionText.visibility = View.GONE
                    }
                } else {
                    appName.gravity = Gravity.TOP
                    TextViewCompat.setTextAppearance(actionText, R.style.kinTextButtonRounded_Hollow)
                    actionText.background = ContextCompat.getDrawable(context, R.drawable.kin_button_rounded_hollow_drawable)
                    actionText.text = context.getString(R.string.learn_more)
                    status.text = context.getString(R.string.not_installed)
                    actionText.setTextColor(ContextCompat.getColor(context, R.color.kin_btn_hollow_text))
                    status.visibility = View.VISIBLE
                    actionText.visibility = View.VISIBLE
                }
            }
        }
    }
}