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

    /**
     * Checks that the move kin launching activity in the given application exists and if it does,
     * starts it using startActivityForResult
     *
     * @param applicationId          The application id of the destination application
     * @param launchActivityFullPath The full path to the activity in the destination app that is responsible for
     * returning a result with the destination app public address
     * @return boolean true if the destination activity has been started
     */
    fun startTransferRequestActivity(requestCode: Int, applicationId: String,
                                     launchActivityFullPath: String): Boolean {
        activity?.let {
            val packageManager = it.packageManager
            val intent = Intent()
            intent.component = ComponentName(applicationId, launchActivityFullPath)
            val resolveInfos = packageManager.queryIntentActivities(intent, 0)
            if (!resolveInfos.isEmpty()) {
                val exported = resolveInfos[0].activityInfo.exported
                if (exported) {
                    val appName = it.applicationInfo.loadLabel(packageManager).toString()
                    intent.putExtra(TransferIntent.EXTRA_SOURCE_APP_NAME, appName)
                    try {
                        it.startActivityForResult(intent, requestCode)
                    } catch (e: Exception) {
                        return false
                    }
                    return true
                }
            }
        }
        return false
    }

    /**
     * Method should be called from onActivityResult
     *
     * @param context                     The activity context is needed to process the result
     * @param resultCode                  The resultCode received in onActivityResult
     * @param intent                      The intent received in onActivityResult
     * @param accountInfoResponseListener A listener that can handle the response
     */
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
            val data: String? = reader.readLine()
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