package org.kinecosystem.movekin.ecoappsdiscovery.discovery.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.kinecosystem.movekinlib.R

class AppsDiscoveryActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.apps_discovery_activity)
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, AppsDiscoveryActivity::class.java)
    }
}