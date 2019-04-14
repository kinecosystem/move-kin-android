package org.kinecosystem.movekinlib.utils

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.util.DisplayMetrics

class TextUtils {
    companion object {

        const val FONT_BANGERS = "Bangers"
        const val FONT_CAVEAT = "Caveat"
        const val FONT_SIGNPAINTER = "SignPainter"
        const val FONT_AMERICANTYPEWRITER = "AmericanTypewriter"
        const val FONT_FJALLAONE = "FjallaOne"
        const val FONT_LIMELIGHT = "Limelight"
        const val FONT_MARKETDECO = "MarketDeco"
        const val FONT_TAHU = "Tahu"
        const val FONT_SAILEC = "sailec"


        fun getFontTypeForType(context: Context, fontTypeName: String): Typeface {
            val fontName = when (fontTypeName) {
                FONT_BANGERS -> "Bangers.ttf"
                FONT_CAVEAT -> "Caveat.ttf"
                FONT_SIGNPAINTER -> "SignPainter.ttc"
                FONT_AMERICANTYPEWRITER -> "AmericanTypewriter.ttc"
                FONT_FJALLAONE -> "FjallaOne.ttf"
                FONT_LIMELIGHT -> "Limelight.ttf"
                FONT_MARKETDECO -> "MarketDeco.ttf"
                FONT_TAHU -> "Tahu.ttf"
                else -> "sailec.otf"
            }
            return Typeface.createFromAsset(context.assets, "fonts/$fontName")
        }
    }
}