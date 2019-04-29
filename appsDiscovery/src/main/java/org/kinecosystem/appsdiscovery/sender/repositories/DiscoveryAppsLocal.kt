package org.kinecosystem.appsdiscovery.sender.repositories

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import org.kinecosystem.appsdiscovery.base.LocalStore
import org.kinecosystem.appsdiscovery.sender.model.EcosystemApp


class DiscoveryAppsLocal(context: Context) {

    private val localStore = LocalStore(context, APPS_DISCOVERY_STORE)
    private val gson = Gson()
    private val appsListType = object : TypeToken<List<EcosystemApp>>() {}.type

    companion object {
        private const val APPS_DISCOVERY_STORE = "APPS_DISCOVERY_STORE"
        private const val APPS_DISCOVERY_VERSION_KEY = "APPS_DISCOVERY_VERSION_KEY"
        private const val APPS_DISCOVERY_KEY = "APPS_DISCOVERY_KEY"
    }


    var discoveryAppVersion: Int
        set(version) = localStore.updateInt(APPS_DISCOVERY_VERSION_KEY, version)
        get() = localStore.getInt(APPS_DISCOVERY_VERSION_KEY, -1)



    fun getDiscoveryApps(callback: OperationResultCallback<List<EcosystemApp>?>) {
        localStore.getString(APPS_DISCOVERY_KEY)?.let {
            try {
                val apps: List<EcosystemApp> = gson.fromJson<List<EcosystemApp>>(it, appsListType)
                callback.onResult(apps)
            } catch (error: JsonSyntaxException) {
                callback.onError("no valid data")
            }
        } ?: run {
            callback.onError("no data")
        }
    }

    fun updateDiscoveryApps(apps: List<EcosystemApp>) {
        val json = gson.toJson(apps, appsListType)
        localStore.updateString(APPS_DISCOVERY_KEY, json)
    }

    fun clearAll() {
        localStore.clearAll()
    }

}