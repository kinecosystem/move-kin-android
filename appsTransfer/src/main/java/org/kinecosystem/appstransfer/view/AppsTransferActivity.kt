package org.kinecosystem.appstransfer.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.constraint.Group
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import org.kinecosystem.appstransfer.R
import org.kinecosystem.appstransfer.presenter.AppsTransferPresenter
import org.kinecosystem.appstransfer.view.customview.AppsTransferList
import org.kinecosystem.common.utils.navigateToUrl
import org.kinecosystem.transfer.model.EcosystemApp

class AppsTransferActivity : AppCompatActivity(), IAppsTransferView {


    private lateinit var dataGroup: Group
    private lateinit var noDataGroup: Group
    private lateinit var loader: ProgressBar
    private lateinit var list: AppsTransferList
    private var presenter: AppsTransferPresenter? = null

    override fun transferToApp(app: EcosystemApp) {
         Log.d("####", "#### AppsTransferActivity  start transferToApp")
    }

    override fun navigateToAppStore(url: String) {
        navigateToUrl(url)
    }

    override fun showLoading() {
        loader.visibility = View.VISIBLE
        noDataGroup.visibility = View.GONE
        dataGroup.visibility = View.GONE
    }

    override fun showData() {
        dataGroup.visibility = View.VISIBLE
        loader.visibility = View.GONE
        noDataGroup.visibility = View.GONE
    }

    override fun showNoData() {
        noDataGroup.visibility = View.VISIBLE
        dataGroup.visibility = View.GONE
        loader.visibility = View.GONE
    }

    override fun invalidateList() {
        list.invalidateApps()
    }

    override fun close() {
        finish()
    }

    override fun startAmountChooserActivity(receiverAppIcon: String, balance: Int, requestCode: Int) {
        //TODO

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.apps_transfer_activity)
        dataGroup = findViewById(R.id.data)
        noDataGroup = findViewById(R.id.noData)
        loader = findViewById(R.id.loader)
        list = findViewById(R.id.list)
        list.clickListener = presenter
        presenter = AppsTransferPresenter()

        findViewById<ImageView>(R.id.close_x).setOnClickListener {
            presenter?.onCloseClicked()
        }
        presenter?.onAttach(this)
        presenter?.let {
            findViewById<AppsTransferList>(R.id.list).setLoadingListener(it)
            list.clickListener = it
        }
    }

    override fun onResume() {
        super.onResume()
        presenter?.onResume()
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, AppsTransferActivity::class.java)
    }
}