package org.kinecosystem.movekinlib.sender.server

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.kinecosystem.movekinlib.base.OperationResultCallback
import org.kinecosystem.movekinlib.sender.model.EcosystemApp
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val PROD_CDN_BASE_URL = "https://cdn.kinitapp.com/"
private const val STAGE_S3_BASE_URL = "https://s3.amazonaws.com/kinapp-static/" // TODO: move to BuildConfig (Requires to update Travis!)

class Network{

    private var service:DiscoverAppsService

    init {
        //TODO
        val serverUrl = STAGE_S3_BASE_URL// if (BuildConfig.DEBUG) STAGE_S3_BASE_URL else PROD_CDN_BASE_URL
        val interceptor = HttpLoggingInterceptor()
        //TODO
        interceptor.level = HttpLoggingInterceptor.Level.BODY// if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        val client = OkHttpClient.Builder().readTimeout(20, TimeUnit.SECONDS).writeTimeout(20, TimeUnit.SECONDS).addInterceptor(interceptor).build()
        val retrofit = Retrofit.Builder()
                .baseUrl(serverUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        service = DiscoverAppsService(retrofit.create<DiscoverAppsApi>(DiscoverAppsApi::class.java))
    }

    fun getDiscoveryApps(callback: OperationResultCallback<List<EcosystemApp>?>) {
        service.getApps(object : OperationResultCallback<List<EcosystemApp>?> {
            override fun onResult(result: List<EcosystemApp>?) {
                result?.let { apps ->
                    for(app in apps){
                        Log.d("####", "#### app ${app.identifier}")
                    }
                    //filter the current one
                    val filter = apps.filterNot { it.identifier.equals("org.kinecosystem.kinit") }
                    callback.onResult(filter)
                }
            }

            override fun onError(errorCode: Int) {
                callback.onError(errorCode)
            }
        })
    }

}