package org.kinecosystem.movekinlib.model

import android.content.Context
import android.graphics.Color
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import org.kinecosystem.movekinlib.utils.ImageUtils
import org.kinecosystem.movekinlib.utils.TextUtils.Companion.FONT_SAILEC


@Parcelize
data class EcosystemApp(
        @SerializedName("category_name")
        val category: String?,
        @SerializedName("identifier") //package
        val identifier: String?,
        @SerializedName("meta_data")
        val metaData: MetaData?,
        @SerializedName("transfer_data")
        val transferData: TransferData?
) : Parcelable


@Parcelize
data class TransferData(
        @SerializedName("launch_activity")
        val launchActivityFullPath: String?
) : Parcelable


@Parcelize
data class MetaData(
        @SerializedName("about_app")
        val about: String?,
        @SerializedName("app_name")
        val name: String?,
        @SerializedName("icon_url")
        val iconUrl: String?,
        @SerializedName("app_url")//url to google play
        val downloadUrl: String?,
        @SerializedName("images")
        val images: List<String>?,
        @SerializedName("experience_data")
        val experienceData: ExperienceData?,
        @SerializedName("card_data")
        val cardData: CardData?
) : Parcelable


@Parcelize
data class ExperienceData(
        @SerializedName("about")
        val about: String?,
        @SerializedName("howto")
        val howTo: String?,
        @SerializedName("title")
        val name: String?
) : Parcelable

@Parcelize
data class CardData(
        @SerializedName("font_line_spacing")
        val fontLineSpacing: String?,
        @SerializedName("font_name")
        val fontName: String?,
        @SerializedName("font_size")
        val fontSize: String?,
        @SerializedName("background_color")
        val bgColor: String?,
        @SerializedName("title")
        val title: String?
) : Parcelable


fun EcosystemApp.preFetch(context: Context) {
    ImageUtils.fetch(metaData?.iconUrl)
    metaData?.images?.let {
        for (url: String in it) {
            ImageUtils.fetch(url)
        }
    }
}

val EcosystemApp.name: String
    get() {
        return metaData?.name ?: ""
    }

val EcosystemApp.cardFontSize: Float
    get() {
        return metaData?.cardData?.fontSize?.toFloatOrNull() ?: 12f
    }

val EcosystemApp.fontLineSpacing: Float
    get() {
        return metaData?.cardData?.fontLineSpacing?.toFloatOrNull() ?: 0f
    }

val EcosystemApp.cardFontName: String
    get() {
        return metaData?.cardData?.fontName ?: FONT_SAILEC
    }

val EcosystemApp.cardTitle: String
    get() {
        return metaData?.cardData?.title ?: ""
    }

val EcosystemApp.iconUrl: String
    get() = metaData?.iconUrl ?: ""


val EcosystemApp.cardColor: Int
    get() = Color.parseColor(metaData?.cardData?.bgColor ?: "#a6a6a6")

val EcosystemApp.launchActivity: String?
    get() = transferData?.launchActivityFullPath

val EcosystemApp.canTransferKin: Boolean
    get() {
        transferData?.launchActivityFullPath?.let {
            return !it.isEmpty()
        } ?: return false
    }