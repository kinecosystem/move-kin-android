package org.kinecosystem.appsdiscovery.sender.discovery.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import org.kinecosystem.appsdiscovery.R
import org.kinecosystem.appsdiscovery.sender.model.EcosystemApp
import org.kinecosystem.appsdiscovery.sender.repositories.DiscoveryAppsLocal
import org.kinecosystem.appsdiscovery.sender.repositories.DiscoveryAppsRemote
import org.kinecosystem.appsdiscovery.sender.repositories.DiscoveryAppsRepository
import org.kinecosystem.appsdiscovery.sender.repositories.OperationResultCallback

class AppsDiscoveryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.apps_discovery_activity)

        //testing
        findViewById<Button>(R.id.clear).setOnClickListener({
            DiscoveryAppsRepository(DiscoveryAppsLocal(this), DiscoveryAppsRemote(), Handler(Looper.getMainLooper())).clearLocalData()
            findViewById<AppsDiscoveryList>(R.id.list).updateData(listOf())
        })
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, AppsDiscoveryActivity::class.java)
    }
}