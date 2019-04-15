package org.kinecosystem.appsdiscovery.sender.server

import com.google.gson.annotations.SerializedName
import org.kinecosystem.appsdiscovery.sender.model.EcosystemApp
import retrofit2.Call
import retrofit2.http.GET

interface DiscoverAppsApi{
    data class AppsResponds(@SerializedName("apps") val apps: List<EcosystemApp>)


    @GET("discovery_apps_android.json")
    fun apps(): Call<AppsResponds>
}