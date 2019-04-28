package org.kinecosystem.appsdiscovery.sender.repositories

import android.os.Handler
import org.kinecosystem.appsdiscovery.sender.model.EcosystemApp
import org.kinecosystem.appsdiscovery.sender.model.EcosystemAppResponse
import org.kinecosystem.appsdiscovery.sender.model.hasNewData

class DiscoveryAppsRepository(private val local: DiscoveryAppsLocal, private val remote: DiscoveryAppsRemote, private val uiHandler: Handler) {

    private var hasLocalData = false

    fun getDiscoveryApps(discoveryAppsCallback: OperationResultCallback<List<EcosystemApp>?>) {
        hasLocalData = false
        //get first data from cache
        local.getDiscoveryApps(object : OperationResultCallback<List<EcosystemApp>?> {
            override fun onResult(cachedApps: List<EcosystemApp>?) {
                if (cachedApps != null) {
                    hasLocalData = true
                    //update ui with cached data
                    uiHandler.post {
                        discoveryAppsCallback.onResult(cachedApps)
                    }
                    //check server data and update local if needed
                    checkRemoteData(discoveryAppsCallback)
                }
            }

            //no data on cache
            override fun onError(error: String) {
                hasLocalData = false
                //check server data
                checkRemoteData(discoveryAppsCallback)
            }
        })

    }

    private fun checkRemoteData(discoveryAppsCallback: OperationResultCallback<List<EcosystemApp>?>) {
        remote.getDiscoveryAppsServerData(object : OperationResultCallback<EcosystemAppResponse> {
            override fun onResult(result: EcosystemAppResponse) {
                if (result.apps != null) {
                    if (result.hasNewData(local.discoveryAppVersion)) {
                        //update cache with new data from server
                        local.updateDiscoveryApps(result.apps)
                        local.discoveryAppVersion = result.version
                        //update ui with new data from server
                        uiHandler.post {
                            discoveryAppsCallback.onResult(result.apps)
                        }
                    }
                } else {
                    if (!hasLocalData) {
                        uiHandler.post {
                            discoveryAppsCallback.onError("no data from server and no data in cache")
                        }
                    }
                }
            }

            override fun onError(error: String) {
                if (!hasLocalData) {
                    uiHandler.post {
                        discoveryAppsCallback.onError("error data from server and no data in cache")
                    }
                }
            }
        })
    }

    fun clearLocalData() {
        local.clearAll()
    }
}