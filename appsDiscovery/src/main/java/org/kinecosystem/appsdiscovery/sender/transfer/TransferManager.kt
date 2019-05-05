package org.kinecosystem.appsdiscovery.sender.transfer

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import java.io.BufferedReader
import java.io.InputStreamReader


class TransferManager(var activity: Activity?) {
    private val REQUEST_CODE = 77
    private val EXTRA_SOURCE_APP_NAME = "EXTRA_SOURCE_APP_NAME"
    private val EXTRA_HAS_ERROR = "EXTRA_HAS_ERROR"
    private val EXTRA_ERROR_MESSAGE = "EXTRA_ERROR_MESSAGE"

    interface AccountInfoResponseListener {
        fun onCancel()

        fun onError(error: String)

        fun onAddressReceived(address: String)
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
    fun startTransferRequestActivity(applicationId: String, launchActivityFullPath: String): Boolean {
        activity?.let {
            val packageManager = it.packageManager
            val intent = Intent()
            intent.setPackage(applicationId)
            intent.component = ComponentName(applicationId, launchActivityFullPath)
            val resolveInfos = packageManager.queryIntentActivities(intent, 0)
            if (!resolveInfos.isEmpty()) {
                val exported = resolveInfos[0].activityInfo.exported
                if (exported) {
                    val appName = it.applicationInfo.loadLabel(packageManager).toString()
                    intent.putExtra(EXTRA_SOURCE_APP_NAME, appName)
                    try {
                        it.startActivityForResult(intent, REQUEST_CODE)
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
     * @param requestCode                 The requestCode received in onActivityResult
     * @param resultCode                  The resultCode received in onActivityResult
     * @param intent                      The intent received in onActivityResult
     * @param accountInfoResponseListener A listener that can handle the response
     */
    fun processResponse(
            requestCode: Int,
            resultCode: Int,
            intent: Intent?,
            accountInfoResponseListener: AccountInfoResponseListener
    ) {
        if (requestCode == REQUEST_CODE) {
            if (intent != null) {
                if (resultCode == Activity.RESULT_OK) {
                    processResultOk(intent, accountInfoResponseListener)
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    processResultCanceled(intent, accountInfoResponseListener)
                }
            }else{
                accountInfoResponseListener.onError("no data")
            }
        }
    }


    private fun processResultOk(
            intent: Intent,
            accountInfoResponseListener: AccountInfoResponseListener
    ) {
        try {
            val uri = intent.data
            val inputStream = activity?.contentResolver?.openInputStream(uri!!)

            val reader = BufferedReader(InputStreamReader(inputStream!!))
            val stringBuilder = StringBuilder()

            var data: String? = reader.readLine()
            while (data != null) {
                stringBuilder.append(data).append('\n')
                data = reader.readLine()
            }
            val address = stringBuilder.toString()
            if (address.isNotEmpty()) {
                accountInfoResponseListener.onAddressReceived(address)
            } else {
                accountInfoResponseListener.onError("unable to retrieve public address, input stream contained no data")
            }
        } catch (e: Exception) {
            var message =  ""
            e.message?.let {
                message = it
            }
            accountInfoResponseListener.onError(message)
        }

    }

    private fun processResultCanceled(intent: Intent?, accountInfoResponseListener: AccountInfoResponseListener) {
        if (intent != null) {
            if (intent.getBooleanExtra(EXTRA_HAS_ERROR, false)) {
                accountInfoResponseListener.onError(intent.getStringExtra(EXTRA_ERROR_MESSAGE))
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