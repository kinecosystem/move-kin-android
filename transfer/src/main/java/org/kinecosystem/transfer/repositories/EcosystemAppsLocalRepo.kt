package org.kinecosystem.transfer.repositories

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import org.kinecosystem.common.base.LocalStore
import org.kinecosystem.transfer.model.EcosystemApp
import org.kinecosystem.common.base.Consts


class EcosystemAppsLocalRepo(context: Context) {

    private val localStore = LocalStore(context, APPS_DISCOVERY_STORE)
    private val gson = Gson()
    private val appsListType = object : TypeToken<List<EcosystemApp>>() {}.type

    companion object {
        private const val APPS_DISCOVERY_STORE = "APPS_DISCOVERY_STORE"
        private const val APPS_DISCOVERY_VERSION_KEY = "APPS_DISCOVERY_VERSION_KEY"
        private const val APPS_DISCOVERY_KEY = "APPS_DISCOVERY_KEY"

        private const val APP_ICON_URL = "APP_ICON_URL"
        private const val APP_ID = "APP_ID"
        private const val APP_NAME = "APPNAME"
        private const val MEMO = "MEMO"
        private const val RECEIVER_APP_ADDRESS = "RECEIVER_APP_ADDRESS"
        private const val CURRENT_BALANCE = "CURRENT_BALANCE"
        private const val APP_SEND_SERVICE_FULL_PATH = "APP_SEND_SERVICE_FULL_PATH"
        private const val APP_RECEIVE_SERVICE_FULL_PATH = "APP_RECEIVE_SERVICE_FULL_PATH"
    }

    var discoveryAppVersion: Int
        set(version) = localStore.updateInt(APPS_DISCOVERY_VERSION_KEY, version)
        get() = localStore.getInt(APPS_DISCOVERY_VERSION_KEY, -1)

    var appIconUrl: String
        set(iconUrl) = localStore.updateString(APP_ICON_URL, iconUrl)
        get() = localStore.getString(APP_ICON_URL, "")

    var appId: String
        set(id) = localStore.updateString(APP_ID, id)
        get() = localStore.getString(APP_ID, "")

    var appName: String
        set(appName) = localStore.updateString(APP_NAME, appName)
        get() = localStore.getString(APP_NAME, "")

    var memoWithRandom: String
        set(memo) = localStore.updateString(MEMO, memo)
        get() = localStore.getString(MEMO, "")

    var receiverAppPublicAddress: String
        set(address) = localStore.updateString(RECEIVER_APP_ADDRESS, address)
        get() = localStore.getString(RECEIVER_APP_ADDRESS, "")

    var currentBalance: Int
        set(balance) = localStore.updateInt(CURRENT_BALANCE, balance)
        get() = localStore.getInt(CURRENT_BALANCE, Consts.NO_BALANCE)

    var appSendServiceFullPath: String
        set(path) = localStore.updateString(APP_SEND_SERVICE_FULL_PATH, path)
        get() = localStore.getString(APP_SEND_SERVICE_FULL_PATH, "")

    var appReceiveServiceFullPath: String
        set(path) = localStore.updateString(APP_RECEIVE_SERVICE_FULL_PATH, path)
        get() = localStore.getString(APP_RECEIVE_SERVICE_FULL_PATH, "")

    fun getDiscoveryApps(callback: OperationResultCallback<List<EcosystemApp>>) {
        val apps = localStore.getString(APPS_DISCOVERY_KEY, "")
        if (apps.isNotEmpty()) {
            try {
                val resultApps: List<EcosystemApp> = gson.fromJson<List<EcosystemApp>>(apps, appsListType)
                callback.onResult(resultApps)
            } catch (error: JsonSyntaxException) {
                callback.onError("no valid data")
            }
        } else {
            callback.onError("no data")
        }
    }

    fun updateDiscoveryApps(apps: List<EcosystemApp>) {
        val json = gson.toJson(apps, appsListType)
        localStore.updateString(APPS_DISCOVERY_KEY, json)
    }

    fun clearAll() {
        localStore.clearAll()
    }

}