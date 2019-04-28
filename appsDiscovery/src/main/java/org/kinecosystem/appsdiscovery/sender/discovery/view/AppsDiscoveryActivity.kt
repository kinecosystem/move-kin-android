package org.kinecosystem.appsdiscovery.sender.discovery.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.kinecosystem.appsdiscovery.R

class AppsDiscoveryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.apps_discovery_activity)
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, AppsDiscoveryActivity::class.java)
    }
}