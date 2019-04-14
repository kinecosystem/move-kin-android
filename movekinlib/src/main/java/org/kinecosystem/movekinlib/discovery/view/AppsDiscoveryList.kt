package org.kinecosystem.movekinlib.discovery.view

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.kinecosystem.movekinlib.R
import org.kinecosystem.movekinlib.discovery.presenter.AppsDiscoveryListPresenter
import org.kinecosystem.movekinlib.discovery.presenter.IAppsDiscoveryListPresenter
import org.kinecosystem.movekinlib.model.EcosystemApp
import org.kinecosystem.movekinlib.model.iconUrl
import org.kinecosystem.movekinlib.model.name
import org.kinecosystem.movekinlib.utils.load

const val COLUMNS_COUNT = 2

class AppsDiscoveryList @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        RecyclerView(context, attrs, defStyleAttr), IAppsDiscoveryListView {
    override fun updateData(apps: List<EcosystemApp>) {
        (adapter as AppsDiscoveryAdapter).updateData(apps)
    }

    override fun navigateToApp(app: EcosystemApp) {
        Log.d("###", "#### navaigte to app $app")
    }

    private var presenter: IAppsDiscoveryListPresenter? = null

    init {
        layoutManager = GridLayoutManager(context, COLUMNS_COUNT)
        presenter = AppsDiscoveryListPresenter()
        presenter?.let {
            adapter = AppsDiscoveryAdapter(context, it)
        }

//        adapter = AppDiscoveryViewAdapter(context, model)
//        model.selectedAppObservable.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
//            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
//                val app: EcosystemApp? = (sender as ObservableField<EcosystemApp>).get()
//                app?.let {
//                    context.startActivity(AppActivity.getIntent(context, it))
//                }
//            }
//        })
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

class AppsDiscoveryAdapter(private val context: Context, private val presenter: IAppsDiscoveryListPresenter) : RecyclerView.Adapter<AppsDiscoveryAdapter.ViewHolder>() {

    var apps: List<EcosystemApp> = listOf()

    fun updateData(apps: List<EcosystemApp>) {
        this.apps = apps
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): AppsDiscoveryAdapter.ViewHolder {
        val view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.app_discovery_item, parent, false)
        return ViewHolder(context, view)
    }

    override fun getItemCount(): Int {
        return apps.size
    }

    override fun onBindViewHolder(holder: AppsDiscoveryAdapter.ViewHolder?, position: Int) {
        val app: EcosystemApp = apps[position]
        holder?.bind(app)
        holder?.view?.setOnClickListener {
            presenter.onAppClicked(app)
        }
    }

    class ViewHolder(private val context: Context, val view: View) : RecyclerView.ViewHolder(view) {
        private val icon: ImageView = view.findViewById(R.id.appIcon)
        private val appName: TextView = view.findViewById(R.id.appName)
        private val colorBg: View = view.findViewById(R.id.colorBg)
        //private val actionText: TextView = view.findViewById(R.id.actionText)
        //private val category: TextView = view.findViewById(R.id.category)

        fun bind(app: EcosystemApp) {
            appName.text = app.name
            icon.load(app.iconUrl)
//               with(actionText){
//                setTextSize(TypedValue.COMPLEX_UNIT_SP, app.cardFontSize)
//                typeface = GeneralUtils.getFontTypeForType(context, app.cardFontName)
//                setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, app.fontLineSpacing,  context.resources.displayMetrics), 1.0f)
//                text = app.cardTitle
//                setTextSize(TypedValue.COMPLEX_UNIT_SP, app.cardFontSize)
//                typeface = GeneralUtils.getFontTypeForType(context, app.cardFontName)
//                setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, app.fontLineSpacing,  context.resources.displayMetrics), 1.0f)
//                text = app.cardTitle
//            }
//            colorBg.setBackgroundColor(app.cardColor)
//            category.text = app.category
//            ImageUtils.getInstance(context).load(app.iconUrl, icon)
        }
    }

}