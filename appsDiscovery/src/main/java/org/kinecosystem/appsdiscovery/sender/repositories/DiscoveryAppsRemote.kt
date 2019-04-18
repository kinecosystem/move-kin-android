package org.kinecosystem.appsdiscovery.sender.repositories

import com.google.gson.Gson
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.kinecosystem.appsdiscovery.sender.model.EcosystemAppResponse
import java.io.IOException
import java.util.concurrent.TimeUnit


//TODO make a nicer url without the kinit
private const val BASE_CDN_URL = "https://cdn.kinitapp.com"
private const val GET_DISCOVERY_APPS_PROD_URL = "$BASE_CDN_URL/discovery_apps_android.json"
private const val GET_DISCOVERY_APPS_STAGE_URL = "$BASE_CDN_URL/discovery_apps_android_stage.json"


class DiscoveryAppsRemote {

    private val httpClient: OkHttpClient
    private val gson = Gson()

    init {
        val httpClientBuilder = OkHttpClient.Builder()
        httpClientBuilder.connectTimeout(30, TimeUnit.SECONDS)
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        httpClientBuilder.networkInterceptors().add(httpLoggingInterceptor)
        httpClient = httpClientBuilder.build()
    }

    fun getDiscoveryAppsServerData(callback: OperationResultCallback<EcosystemAppResponse>) {
        httpClient.newCall(getRequest(GET_DISCOVERY_APPS_PROD_URL)).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError("server failed get discoveryApps ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    try {
                        val responseData = gson.fromJson(response.body()?.charStream(), EcosystemAppResponse::class.java)
                        callback.onResult(responseData)
                    } catch (e: JSONException) {
                        callback.onError("wrong json format ${e.message}")
                    }

                } else {
                    //TODO error with response
                    callback.onError("server response is not successful")
                }
            }
        })
    }

    private fun getRequest(url: String): Request = Request.Builder().url(url).build()
}