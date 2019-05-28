package org.kinecosystem.transfer.model

import com.google.gson.annotations.SerializedName


data class DiscoveryAppsList(
        @SerializedName("apps")
        val apps: List<EcosystemApp>?
)