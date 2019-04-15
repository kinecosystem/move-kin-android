package org.kinecosystem.appsdiscovery.sender.server

import org.kinecosystem.appsdiscovery.base.OperationResultCallback
import org.kinecosystem.appsdiscovery.sender.model.EcosystemApp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiscoverAppsService(private val discoveryAppsApi: DiscoverAppsApi) {

    fun getApps(callback: OperationResultCallback<List<EcosystemApp>?>) {
        discoveryAppsApi.apps().enqueue(object : Callback<DiscoverAppsApi.AppsResponds> {
            override fun onFailure(call: Call<DiscoverAppsApi.AppsResponds>, t: Throwable) {
                callback.onError(0)
            }

            override fun onResponse(call: Call<DiscoverAppsApi.AppsResponds>, response: Response<DiscoverAppsApi.AppsResponds>) {
                if (response.isSuccessful) {
                    callback.onResult(response.body()?.apps)
                } else callback.onError(0)
            }
        })
    }
}