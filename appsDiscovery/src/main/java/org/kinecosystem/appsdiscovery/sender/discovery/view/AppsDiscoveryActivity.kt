package org.kinecosystem.appsdiscovery.sender.discovery.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import org.kinecosystem.appsdiscovery.R
import org.kinecosystem.appsdiscovery.sender.model.EcosystemApp
import org.kinecosystem.appsdiscovery.sender.repositories.DiscoveryAppsRepository
import org.kinecosystem.appsdiscovery.sender.repositories.OperationResultCallback

class AppsDiscoveryActivity : AppCompatActivity(){

    var repository:DiscoveryAppsRepository? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = DiscoveryAppsRepository(this)
        setContentView(R.layout.apps_discovery_activity)
        getData()
        findViewById<Button>(R.id.get).setOnClickListener({
            getData()
        })
//        findViewById<Button>(R.id.clear).setOnClickListener({
//            repository?.clearLocalData()
//            findViewById<AppsDiscoveryList>(R.id.list).updateData(listOf())
//        })
    }

    private fun getData() {
        repository?.getDiscoveryApps(object : OperationResultCallback<List<EcosystemApp>?>{
            override fun onResult(result: List<EcosystemApp>?) {
                result?.let {
                    findViewById<AppsDiscoveryList>(R.id.list).updateData(result)
                }
            }

            override fun onError(error: String) {
                //TODO
            }

        })
    }


    companion object {
        fun getIntent(context: Context) = Intent(context, AppsDiscoveryActivity::class.java)
    }
}