package org.kinecosystem.appsdiscovery.sender.model

import com.google.gson.annotations.SerializedName

data class EcosystemAppResponse(
        @SerializedName("version")
        val version: Int,
        @SerializedName("apps")
        val apps: List<EcosystemApp>?
)

fun EcosystemAppResponse.hasNewData(localVersion : Int) :Boolean = version != localVersion