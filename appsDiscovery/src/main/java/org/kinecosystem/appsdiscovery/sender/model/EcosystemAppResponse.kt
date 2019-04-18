package org.kinecosystem.appsdiscovery.sender.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class EcosystemAppResponse(
        @SerializedName("version")
        val version: String?,
        @SerializedName("apps")
        val apps: List<EcosystemApp>?
) : Parcelable