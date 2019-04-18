package org.kinecosystem.appsdiscovery.sender.server

import android.util.Log
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


class Network {
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

    private fun getRequest(url: String): Request = Request.Builder().url(url).build()

    fun getDiscoveryApps() {
        httpClient.newCall(getRequest(GET_DISCOVERY_APPS_STAGE_URL)).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //TODO
                Log.d("", "response failed")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    try {
                        val responseData = gson.fromJson(response.body()?.charStream(), EcosystemAppResponse::class.java)
                        Log.d("", "####response version  ${responseData.version}")
                        Log.d("", "##### app size ${responseData.apps?.size} app  ${responseData.apps?.get(0)}")
                    } catch (e: JSONException) {
                        Log.d("####", "##### response json not valid ${e.message}")
                    }

                } else {
                    //TODO error with response
                    Log.d("", "response not successful")
                }
            }
        })
    }
}