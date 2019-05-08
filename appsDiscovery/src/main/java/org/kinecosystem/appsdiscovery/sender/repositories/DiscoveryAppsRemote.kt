package org.kinecosystem.appsdiscovery.sender.repositories

import com.google.gson.Gson
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.kinecosystem.appsdiscovery.BuildConfig
import org.kinecosystem.appsdiscovery.sender.model.EcosystemAppResponse
import java.io.IOException
import java.util.concurrent.TimeUnit

class DiscoveryAppsRemote {

    private val httpClient: OkHttpClient
    private val gson = Gson()

    companion object {
        //TODO make a nicer url without the kinit
        private const val BASE_CDN_URL = "https://cdn.kinitapp.com"
        private const val GET_DISCOVERY_APPS_PROD_URL = "$BASE_CDN_URL/discovery_apps_android.json"
        private const val GET_DISCOVERY_APPS_STAGE_URL = "$BASE_CDN_URL/discovery_apps_android_stageb.json"
        private const val SERVER_TIMEOUT = 30L
    }

    init {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val httpClientBuilder = OkHttpClient.Builder()
        with(httpClientBuilder) {
            connectTimeout(SERVER_TIMEOUT, TimeUnit.SECONDS)
            networkInterceptors().add(httpLoggingInterceptor)
        }
        httpClient = httpClientBuilder.build()
    }

    fun getDiscoveryAppsServerData(callback: OperationResultCallback<EcosystemAppResponse>) {
        var url = GET_DISCOVERY_APPS_PROD_URL
        if (BuildConfig.DEBUG) {
            url = GET_DISCOVERY_APPS_STAGE_URL
        }
        httpClient.newCall(getRequest(url)).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError("server failed get discoveryApps ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful && response.body() != null) {
                    try {
                        val responseData = gson.fromJson(response.body()!!.string(), EcosystemAppResponse::class.java)
                        callback.onResult(responseData)
                    } catch (e: JSONException) {
                        callback.onError("wrong json format ${e.message}")
                    }
                } else {
                    callback.onError("server response is not successful or no body")
                }
            }
        })
    }

    private fun getRequest(url: String): Request = Request.Builder().url(url).build()
}