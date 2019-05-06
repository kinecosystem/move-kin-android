package org.kinecosystem.appsdiscovery.sender.repositories

import android.os.Handler
import org.kinecosystem.appsdiscovery.sender.model.EcosystemApp
import org.kinecosystem.appsdiscovery.sender.model.EcosystemAppResponse
import org.kinecosystem.appsdiscovery.sender.model.hasNewData
import org.kinecosystem.appsdiscovery.sender.model.name

class DiscoveryAppsRepository private constructor(private val local: DiscoveryAppsLocal, private val remote: DiscoveryAppsRemote, private val uiHandler: Handler) {


    companion object {
        private var instance: DiscoveryAppsRepository? = null

        fun getInstance(local: DiscoveryAppsLocal, remote: DiscoveryAppsRemote, uiHandler: Handler): DiscoveryAppsRepository {
            if (instance == null) {
                instance = DiscoveryAppsRepository(local, remote, uiHandler)
            }
            return instance!!
        }
    }

    private var hasLocalData = false
    private var discoveryApps: List<EcosystemApp> = listOf()

    fun storeReceiverAppPublicAddress(address:String){
        local.receiverAppPublicAddress = address
    }

    fun storeCurrentBalance(balance:Int){
        local.currentBalance = balance
    }

    fun getCurrentBalance() = local.currentBalance

    fun getReceiverAppPublicAddress() = local.receiverAppPublicAddress

    fun clearReceiverAppPublicAddress(){
        local.receiverAppPublicAddress = ""
    }


    fun loadDiscoveryApps(listener:OperationResultCallback<List<EcosystemApp>>) {
        hasLocalData = false
        //get first data from cache
        local.getDiscoveryApps(object : OperationResultCallback<List<EcosystemApp>?> {
            override fun onResult(cachedApps: List<EcosystemApp>?) {
                if (cachedApps != null) {
                    hasLocalData = true
                    discoveryApps = cachedApps
                    //update ui with cached data
                    uiHandler.post {
                        listener.onResult(cachedApps)
                    }
                    //check server data and update local if needed
                    checkRemoteData(listener)
                }
            }

            //no data on cache
            override fun onError(error: String) {
                hasLocalData = false
                //check server data
                checkRemoteData(listener)
            }
        })

    }

    private fun checkRemoteData(listener:OperationResultCallback<List<EcosystemApp>>) {
        remote.getDiscoveryAppsServerData(object : OperationResultCallback<EcosystemAppResponse> {
            override fun onResult(result: EcosystemAppResponse) {
                if (result.apps != null) {
                    if (result.hasNewData(local.discoveryAppVersion)) {

                        //TODO filter

                        //update cache with new data from server
                        local.updateDiscoveryApps(result.apps)
                        local.discoveryAppVersion = result.version
                        //update ui with new data from server
                        discoveryApps = result.apps
                        uiHandler.post {
                            listener.onResult(result.apps)
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
        if(appName.isNullOrBlank()) {
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