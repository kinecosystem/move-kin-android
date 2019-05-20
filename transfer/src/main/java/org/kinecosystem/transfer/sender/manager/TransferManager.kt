package org.kinecosystem.transfer.sender.manager

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import org.kinecosystem.transfer.TransferIntent
import java.io.BufferedReader
import java.io.InputStreamReader

class TransferManager(var activity: Activity?) {

    interface AccountInfoResponseListener {
        fun onCancel()

        fun onError(error: String)

        fun onResult(data: String)
    }

    fun requestBuilder(applicationId: String, launchActivityFullPath: String): RequestBuilder {
        return RequestBuilder(activity, applicationId, launchActivityFullPath)
    }

    class RequestBuilder constructor(private val activity:Activity?,
                                     private val applicationId: String,
                                     private val launchActivityFullPath: String) {
        val intent: Intent = Intent()


        fun addParam(key: String, value: String): RequestBuilder {
            intent.putExtra(key, value)
            return this
        }

        fun start(requestCode: Int): Boolean {
            activity?.let {
                val packageManager = it.packageManager
                intent.component = ComponentName(applicationId, launchActivityFullPath)
                val resolveInfos = packageManager.queryIntentActivities(intent, 0)
                if (!resolveInfos.isEmpty()) {
                    if (resolveInfos[0].activityInfo.exported) {
                        val appName = activity.applicationInfo.loadLabel(activity.packageManager).toString()
                        intent.putExtra(TransferIntent.EXTRA_SOURCE_APP_NAME, appName)
                        activity.startActivityForResult(intent, requestCode)
                        return true
                    }
                }
            }
            return false
        }
    }

    fun processResponse(resultCode: Int, intent: Intent?,
                        accountInfoResponseListener: AccountInfoResponseListener) {

        if (intent != null) {
            if (resultCode == Activity.RESULT_OK) {
                processResultOk(intent, accountInfoResponseListener)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                processResultCanceled(intent, accountInfoResponseListener)
            }
        } else {
            accountInfoResponseListener.onError("no data")
        }

    }


    private fun processResultOk(intent: Intent,
            accountInfoResponseListener: AccountInfoResponseListener) {

        try {
            val uri = intent.data
            val inputStream = activity?.contentResolver?.openInputStream(uri!!)
            val reader = BufferedReader(InputStreamReader(inputStream!!))
            var data: String? = reader.readLine()
            data?.let {
                accountInfoResponseListener.onResult(it)
            } ?: run {
                accountInfoResponseListener.onError("unable to retrieve account info, input stream contained no data")
            }
        } catch (e: Exception) {
            accountInfoResponseListener.onError("Exception $e with message ${e.message.orEmpty()}")
        }
    }

    private fun processResultCanceled(intent: Intent?, accountInfoResponseListener: AccountInfoResponseListener) {
        if (intent != null) {
            if (intent.getBooleanExtra(TransferIntent.EXTRA_HAS_ERROR, false)) {
                val errorMessage = if (intent.hasExtra(TransferIntent.EXTRA_ERROR_MESSAGE)) {
                    intent.getStringExtra(TransferIntent.EXTRA_ERROR_MESSAGE)
                } else ""
                accountInfoResponseListener.onError(errorMessage)
            } else {
                accountInfoResponseListener.onCancel()
            }
        } else {
            accountInfoResponseListener.onCancel()
        }
    }

    fun reset() {
        activity = null
    }
}