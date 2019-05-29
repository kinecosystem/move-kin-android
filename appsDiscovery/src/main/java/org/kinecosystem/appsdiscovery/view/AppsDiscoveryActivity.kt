package org.kinecosystem.appsdiscovery.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.constraint.Group
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ProgressBar
import org.kinecosystem.appsdiscovery.R
import org.kinecosystem.appsdiscovery.presenter.AppsDiscoveryListPresenter
import org.kinecosystem.appsdiscovery.view.customView.AppsDiscoveryList

class AppsDiscoveryActivity : AppCompatActivity() {

    lateinit var dataGroup: Group
    lateinit var noDataGroup: Group
    lateinit var loader: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.apps_discovery_module_activity)
        dataGroup = findViewById(R.id.data)
        noDataGroup = findViewById(R.id.noData)
        loader = findViewById(R.id.loader)
        findViewById<AppsDiscoveryList>(R.id.list).setLoadingListener(object : AppsDiscoveryListPresenter.LoadingListener {
            override fun loading() {
                updateLoading()
            }

            override fun loadingComplete() {
                updateLoadingComplete()
            }

            override fun loadingFailed() {
                updateLoadingFailed()
            }
        })
    }

    private fun updateLoading() {
        loader.visibility = View.VISIBLE
        noDataGroup.visibility = View.GONE
        dataGroup.visibility = View.GONE
    }

    private fun updateLoadingComplete() {
        dataGroup.visibility = View.VISIBLE
        loader.visibility = View.GONE
        noDataGroup.visibility = View.GONE
    }

    private fun updateLoadingFailed() {
        noDataGroup.visibility = View.VISIBLE
        dataGroup.visibility = View.GONE
        loader.visibility = View.GONE
    }

    fun onCloseClicked(view: View?) {
        finish()
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, AppsDiscoveryActivity::class.java)
    }
}