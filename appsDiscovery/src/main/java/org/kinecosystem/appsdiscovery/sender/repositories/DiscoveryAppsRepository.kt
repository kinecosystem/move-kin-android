package org.kinecosystem.appsdiscovery.sender.repositories

import android.os.Handler
import org.kinecosystem.appsdiscovery.sender.model.EcosystemApp
import org.kinecosystem.appsdiscovery.sender.model.EcosystemAppResponse
import org.kinecosystem.appsdiscovery.sender.model.hasNewData
import org.kinecosystem.appsdiscovery.sender.model.name
import java.util.*

class DiscoveryAppsRepository private constructor(private val local: DiscoveryAppsLocal, private val remote: DiscoveryAppsRemote, private val uiHandler: Handler) : Observable() {


    companion object {
        private var instance: DiscoveryAppsRepository? = null

        fun getInstance(local: DiscoveryAppsLocal, remote: DiscoveryAppsRemote, uiHandler: Handler): DiscoveryAppsRepository {
            if (instance == null) {
                instance = DiscoveryAppsRepository(local, remote, uiHandler)
            }
            return instance!!
        }
    }

    //TODO
    //how to notify errors

    private var hasLocalData = false
    var discoveryApps: List<EcosystemApp> = listOf()
        private set


    fun loadDiscoveryApps() {
        hasLocalData = false
        //get first data from cache
        local.getDiscoveryApps(object : OperationResultCallback<List<EcosystemApp>?> {
            override fun onResult(cachedApps: List<EcosystemApp>?) {
                if (cachedApps != null) {
                    hasLocalData = true
                    //update ui with cached data
                    uiHandler.post {
                        discoveryApps = cachedApps
                        setChanged()
                        notifyObservers()
                    }
                    //check server data and update local if needed
                    checkRemoteData()
                }
            }

            //no data on cache
            override fun onError(error: String) {
                hasLocalData = false
                //check server data
                checkRemoteData()
            }
        })

    }

    private fun checkRemoteData() {
        remote.getDiscoveryAppsServerData(object : OperationResultCallback<EcosystemAppResponse> {
            override fun onResult(result: EcosystemAppResponse) {
                if (result.apps != null) {
                    if (result.hasNewData(local.discoveryAppVersion)) {
                        //update cache with new data from server
                        local.updateDiscoveryApps(result.apps)
                        local.discoveryAppVersion = result.version
                        //update ui with new data from server
                        uiHandler.post {
                            discoveryApps = result.apps
                            setChanged()
                            notifyObservers()
                        }
                    }
                } else {
                    if (!hasLocalData) {
                        uiHandler.post {
                            //  discoveryAppsCallback.onError("no data from server and no data in cache")
                        }
                    }
                }
            }

            override fun onError(error: String) {
                if (!hasLocalData) {
                    uiHandler.post {
                        //  discoveryAppsCallback.onError("error data from server and no data in cache")
                    }
                }
            }
        })
    }

    fun getAppByName(appName: String): EcosystemApp? {
        val sublist = discoveryApps.filter { app ->
            app.name == appName
        }
        if (sublist.isNotEmpty()) {
            return sublist[0]
        }
        return null
    }

    fun clearLocalData() {
        local.clearAll()
    }
}