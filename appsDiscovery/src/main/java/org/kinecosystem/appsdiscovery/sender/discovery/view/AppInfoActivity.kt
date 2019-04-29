package org.kinecosystem.appsdiscovery.sender.discovery.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import org.kinecosystem.appsdiscovery.R
import org.kinecosystem.appsdiscovery.sender.model.name
import org.kinecosystem.appsdiscovery.sender.repositories.DiscoveryAppsLocal
import org.kinecosystem.appsdiscovery.sender.repositories.DiscoveryAppsRemote
import org.kinecosystem.appsdiscovery.sender.repositories.DiscoveryAppsRepository

class AppInfoActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appName = intent.getStringExtra(PARAM_APP_NAME)
        if(appName.isNullOrBlank()){
            finish()
        }
        val discoveryAppsRepository = DiscoveryAppsRepository.getInstance(DiscoveryAppsLocal(this), DiscoveryAppsRemote(), Handler(Looper.getMainLooper()))
        val app = discoveryAppsRepository.getAppByName(appName)
        if(app == null){
            finish()
        }
        setContentView(R.layout.app_info_activity)
        findViewById<TextView>(R.id.name).setText(app?.name)
        findViewById<TextView>(R.id.pkg).setText(app?.identifier)


    }

    companion object {
        private const val PARAM_APP_NAME = "PARAM_APP_NAME"

        fun getIntent(context: Context, appName:String):Intent{
            val intent = Intent(context, AppInfoActivity::class.java)
            intent.putExtra(PARAM_APP_NAME, appName)
            return intent
        }
    }
}