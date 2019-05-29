package org.kinecosystem.transfer.repositories

import android.os.Handler
import org.kinecosystem.transfer.model.*

class EcosystemAppsRepository
private constructor(private val currentPackage: String, private val local: EcosystemAppsLocalRepo,
                    private val remote: EcosystemAppsRemoteRepo, private val uiHandler: Handler) {


    companion object {
        private var instance: EcosystemAppsRepository? = null

        fun getInstance(currentPackage: String, local: EcosystemAppsLocalRepo, remote: EcosystemAppsRemoteRepo, uiHandler: Handler): EcosystemAppsRepository {
            if (instance == null) {
                instance = EcosystemAppsRepository(currentPackage, local, remote, uiHandler)
            }
            return instance!!
        }
    }

    private var hasLocalData = false
    private var discoveryApps: List<EcosystemApp> = listOf()

    fun storeReceiverAppPublicAddress(address: String) {
        local.receiverAppPublicAddress = address
    }

    fun storeCurrentBalance(balance: Int) {
        local.currentBalance = balance
    }

    fun getCurrentBalance() = local.currentBalance

    fun getReceiverAppPublicAddress() = local.receiverAppPublicAddress

    fun clearReceiverAppPublicAddress() {
        local.receiverAppPublicAddress = ""
    }

    fun getStoredMemo() = local.memo

    fun getStoredAppIcon() = local.appIconUrl


    fun loadDiscoveryApps(listener: OperationResultCallback<List<EcosystemApp>>) {
        hasLocalData = false
        //get first data from cache
        local.getDiscoveryApps(object : OperationResultCallback<List<EcosystemApp>> {
            override fun onResult(result: List<EcosystemApp>) {
                hasLocalData = true
                discoveryApps = result
                //updateViews ui with cached data
                uiHandler.post {
                    listener.onResult(result)
                }
                //check server data and updateViews local if needed
                checkRemoteData(listener)
            }

            //no data on cache
            override fun onError(error: String) {
                hasLocalData = false
                //check server data
                checkRemoteData(listener)
            }
        })

    }

    private fun checkRemoteData(listener: OperationResultCallback<List<EcosystemApp>>) {
        remote.getDiscoveryAppsServerData(object : OperationResultCallback<EcosystemAppResponse> {
            override fun onResult(result: EcosystemAppResponse) {
                if (result.hasNewData(local.discoveryAppVersion)) {
                    result.apps?.let { serverApps ->
                        var filterApps: List<EcosystemApp> = serverApps
                        val currentApp: EcosystemApp? = serverApps.firstOrNull { it.identifier == currentPackage }
                        //if finds this app in the list - remove it from the list
                        currentApp?.let { app ->
                            local.appIconUrl = app.iconUrl
                            local.memo = app.memo
                            filterApps = serverApps.filter { it != app }
                        }
                        discoveryApps = filterApps
                        local.updateDiscoveryApps(discoveryApps)
                        local.discoveryAppVersion = result.version
                        //updateViews ui with new data from server
                        uiHandler.post {
                            listener.onResult(discoveryApps)
                        }
                        //perform image caching
                        serverApps.forEach {
                            it.preFetch()
                        }
                    }
                } else {
                    if (!hasLocalData) {
                        uiHandler.post {
                            listener.onError("no data from server and no data in cache")
                        }
                    }
                }
            }

            override fun onError(error: String) {
                if (!hasLocalData) {
                    uiHandler.post {
                        listener.onError("error data from server and no data in cache")
                    }
                }
            }
        })
    }

    fun getAppByName(appName: String?): EcosystemApp? {
        if (appName.isNullOrBlank()) {
            return null
        }
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