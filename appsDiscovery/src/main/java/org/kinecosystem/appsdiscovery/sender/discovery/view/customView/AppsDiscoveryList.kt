package org.kinecosystem.appsdiscovery.sender.discovery.view.customView

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.kinecosystem.appsdiscovery.R
import org.kinecosystem.appsdiscovery.sender.discovery.presenter.AppsDiscoveryListPresenter
import org.kinecosystem.appsdiscovery.sender.discovery.presenter.IAppsDiscoveryListPresenter
import org.kinecosystem.appsdiscovery.sender.discovery.view.AppInfoActivity
import org.kinecosystem.appsdiscovery.sender.model.*
import org.kinecosystem.appsdiscovery.sender.repositories.DiscoveryAppsLocal
import org.kinecosystem.appsdiscovery.sender.repositories.DiscoveryAppsRemote
import org.kinecosystem.appsdiscovery.sender.repositories.DiscoveryAppsRepository
import org.kinecosystem.common.utils.TextUtils
import org.kinecosystem.common.utils.load


const val COLUMNS_COUNT = 2

class AppsDiscoveryList @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        RecyclerView(context, attrs, defStyleAttr), IAppsDiscoveryListView {
    private var presenter: IAppsDiscoveryListPresenter? = null

    init {
        layoutManager = GridLayoutManager(context, COLUMNS_COUNT)
        val discoveryAppsRepository = DiscoveryAppsRepository.getInstance(context.packageName, DiscoveryAppsLocal(context), DiscoveryAppsRemote(), Handler(Looper.getMainLooper()))
        presenter = AppsDiscoveryListPresenter(discoveryAppsRepository)
        presenter?.let {
            adapter = AppsDiscoveryAdapter(it)
        }

        addOnAttachStateChangeListener(object :OnAttachStateChangeListener{
            override fun onViewDetachedFromWindow(v: View?) {
                presenter = null
            }

            override fun onViewAttachedToWindow(v: View?) {
            }

        })
    }



    override fun updateData(apps: List<EcosystemApp>) {
        (adapter as AppsDiscoveryAdapter).updateData(apps)
    }

    override fun setLoadingListener(loadingListener: AppsDiscoveryListPresenter.LoadingListener) {
        presenter?.setLoadingListener(loadingListener)
    }

    override fun navigateToApp(app: EcosystemApp) {
        context.startActivity(AppInfoActivity.getIntent(context, app.name))
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

class AppsDiscoveryAdapter(private val presenter: IAppsDiscoveryListPresenter) : RecyclerView.Adapter<AppsDiscoveryAdapter.ViewHolder>() {

    var apps: List<EcosystemApp> = listOf()

    fun updateData(apps: List<EcosystemApp>) {
        this.apps = apps
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.app_discovery_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return apps.size
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val app: EcosystemApp = apps[position]
        holder?.bind(app)
        holder?.view?.setOnClickListener {
            presenter.onAppClicked(app)
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val icon: ImageView = view.findViewById(R.id.appIcon)
        private val appName: TextView = view.findViewById(R.id.appName)
        private val colorBg: View = view.findViewById(R.id.colorBg)
        private val actionText: TextView = view.findViewById(R.id.actionText)
        private val category: TextView = view.findViewById(R.id.category)

        fun bind(app: EcosystemApp) {
            appName.text = app.name
            category.text = app.category
            colorBg.setBackgroundColor(app.cardColor)
            icon.load(app.iconUrl)
            with(actionText) {
                setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, app.fontLineSpacing, context.resources.displayMetrics), 1.0f)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, app.cardFontSize)
                typeface = TextUtils.getFontTypeForType(context, app.cardFontName)
                setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, app.fontLineSpacing, context.resources.displayMetrics), 1.0f)
                text = app.cardTitle
            }
        }
    }

}