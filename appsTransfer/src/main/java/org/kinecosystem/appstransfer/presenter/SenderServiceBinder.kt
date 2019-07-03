package org.kinecosystem.appstransfer.presenter

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ResolveInfo
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import org.kinecosystem.common.base.Consts
import org.kinecosystem.transfer.receiver.service.ServiceConfigurationException
import org.kinecosystem.transfer.repositories.EcosystemAppsRepository
import org.kinecosystem.transfer.repositories.KinTransferCallback
import org.kinecosystem.transfer.sender.service.SendKinServiceBase
import java.util.concurrent.Executors

class SenderServiceBinder(private val context: Context?) {
    private val TAG = SenderServiceBinder::class.java.simpleName

    interface BinderListener {
        fun onBalanceReceived(balance: Int)
        fun onBalanceFailed()
        fun onServiceConnected()
        fun onServiceDisconnected()
        fun onTransferFailed(errorMessage: String, senderAddress: String, transactionMemo:String)
        fun onTransferComplete(kinTransferComplete: SendKinServiceBase.KinTransferComplete)
    }

    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private var executorService = Executors.newCachedThreadPool()
    @Volatile
    var isBounded = false
        private set
    private var transferService: SendKinServiceBase? = null
    private var listener: BinderListener? = null
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as SendKinServiceBase.KinTransferServiceBinder
            transferService = binder.service
            isBounded = true
            updateServiceConnection(true)
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBounded = false
            updateServiceConnection(false)
        }
    }

    fun setListener(transferListener: BinderListener) {
        this.listener = transferListener
    }

    fun requestCurrentBalance() {
        if (isBounded) {
            executorService.execute {
                try {
                    transferService?.let {
                        updateBalance(true, it.currentBalance.toInt())
                    }
                } catch (balanceException: SendKinServiceBase.BalanceException) {
                    updateBalance(false)
                }
            }
        } else {
            Log.e(TAG, "service must call bind first and wait until its bounded")
        }
    }

    fun startSendKin(receiverAppId:String, receiverAppName:String, receiverAddress: String, amount: Int, memo: String) {
        if (isBounded) {
            executorService.execute {
                try {
                    val kinTransferComplete: SendKinServiceBase.KinTransferComplete? = transferService?.transferKin(receiverAppId, receiverAppName, receiverAddress, amount, memo)
                    kinTransferComplete?.let {
                        updateTransferCompleted(it)
                    } ?: kotlin.run {
                        startSendKinAsync(receiverAppId, receiverAppName, receiverAddress, amount, memo, object : KinTransferCallback {
                            override fun onSuccess(kinTransferComplete: SendKinServiceBase.KinTransferComplete) {
                                updateTransferCompleted(kinTransferComplete)
                            }

                            override fun onError(e: SendKinServiceBase.KinTransferException) {
                                updateTransferFailed(e, memo)
                            }
                        })
                    }
                } catch (e: SendKinServiceBase.KinTransferException) {
                    updateTransferFailed(e, memo)
                    Log.d(TAG, "Exception while transferring Kin,  SendKinServiceBase.KinTransferException ${e.message}")
                }
            }
        } else {
            Log.e(TAG, "service must call bind first and wait until its bounded")
        }
    }

    fun startSendKinAsync(receiverAppId: String, receiverAppName: String, receiverAddress: String, amount: Int, memo: String, callback: KinTransferCallback) {
        if (isBounded) {
            executorService.execute {
                transferService?.transferKinAsync(receiverAppId, receiverAppName, receiverAddress, amount, memo, callback)
            }
        }
    }

    @Throws(ServiceConfigurationException::class)
    fun bind() {
        if (!isBounded) {
            context?.let {
                val senderPackageName = it.packageName
                var serviceFullPath = EcosystemAppsRepository.getInstance(it).getSenderServiceFullPath()
                if (serviceFullPath.isNullOrEmpty())
                    serviceFullPath = "$senderPackageName.${Consts.SERVICE_DEFAULT_PACKAGE}.${Consts.SENDER_SERVICE_NAME}"
                val intent = Intent()
                intent.component = ComponentName(senderPackageName, serviceFullPath)
                intent.`package` = senderPackageName
                it.packageManager?.let { packageManager ->
                    val resolveInfos: MutableList<ResolveInfo> = packageManager.queryIntentServices(intent, 0)
                    if (!resolveInfos.any()) {
                        throw ServiceConfigurationException(serviceFullPath, "Service not found - Service must be implemented")
                    }
                    if (resolveInfos.filter { it.serviceInfo.exported }.any()) {
                        throw ServiceConfigurationException(serviceFullPath, "Service should not be exported")
                    }
                } ?: kotlin.run {
                    throw ServiceConfigurationException(serviceFullPath, "packageManager not found")
                }
                it.bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
        } else {
            updateServiceConnection(true)
        }
    }

    fun unbind() {
        if (isBounded) {
            context?.unbindService(connection)
            isBounded = false
        }
    }

    private fun updateBalance(recieved: Boolean, balance: Int = 0) {
        mainThreadHandler.post {
            if (recieved) {
                listener?.onBalanceReceived(balance)
            } else {
                listener?.onBalanceFailed()
            }
        }
    }

    private fun updateTransferCompleted(kinTransferComplete: SendKinServiceBase.KinTransferComplete) {
        mainThreadHandler.post {
            listener?.onTransferComplete(kinTransferComplete)
        }
    }

    private fun updateTransferFailed(e: SendKinServiceBase.KinTransferException, transactionMemo: String) {
        mainThreadHandler.post {
            val msg = e.message ?: ""
            listener?.onTransferFailed(msg, e.senderAddress, transactionMemo)
        }
    }

    private fun updateServiceConnection(isConnected: Boolean) {
        mainThreadHandler.post {
            if (isConnected) {
                listener?.onServiceConnected()
            } else {
                listener?.onServiceDisconnected()
            }
        }
    }
}