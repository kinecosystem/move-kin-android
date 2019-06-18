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
        fun onTransferFailed(errorMessage: String, senderAddress: String)
        fun onTransferComplete(kinTransferComplete: SendKinServiceBase.KinTransferComplete)
        fun onTransferTimeout()
    }

    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private var executorService = Executors.newCachedThreadPool()
    @Volatile
    var isBounded = false
        private set
    private var transferService: SendKinServiceBase? = null
    private var listener: BinderListener? = null
    private val handler = Handler()
    private var afterTimeout = false
    @Volatile
    private var transferResponseReceived = false

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

    fun startSendKin(receiverAddress: String, amount: Int, memo: String) {
        if (isBounded) {
            executorService.execute {
                try {
                    startTimeoutCounter()
                    val kinTransferComplete: SendKinServiceBase.KinTransferComplete? = transferService?.transferKin(receiverAddress, amount, memo)
                    kinTransferComplete?.let {
                        updateTransferCompleted(it)
                    } ?: kotlin.run {
                        startSendKinAsync(receiverAddress, amount, memo, object : KinTransferCallback {
                            override fun onSuccess(kinTransferComplete: SendKinServiceBase.KinTransferComplete) {
                                updateTransferCompleted(kinTransferComplete)
                            }

                            override fun onError(e: SendKinServiceBase.KinTransferException) {
                                updateTransferFailed(e)
                            }
                        })
                    }
                } catch (e: SendKinServiceBase.KinTransferException) {
                    updateTransferFailed(e)
                    Log.d(TAG, "Exception while transferring Kin,  SendKinServiceBase.KinTransferException ${e.message}")
                }
            }
        } else {
            Log.e(TAG, "service must call bind first and wait until its bounded")
        }
    }

    private fun startSendKinAsync(receiverAddress: String, amount: Int, memo: String, callback: KinTransferCallback) {
        if (isBounded) {
            executorService.execute {
                transferService?.transferKinAsync(receiverAddress, amount, memo, callback)
            }
        }
    }

    @Throws(ServiceConfigurationException::class)
    fun bind() {
        if (!isBounded) {
            val intent = Intent()
            val senderPackageName = context?.packageName
            val serviceFullPath = "$senderPackageName.${Consts.SERVICE_DEFAULT_PACKAGE}.${Consts.SENDER_SERVICE_NAME}"
            intent.component = ComponentName(senderPackageName, serviceFullPath)
            intent.`package` = senderPackageName
            context?.packageManager?.let {
                val resolveInfos: MutableList<ResolveInfo> = it.queryIntentServices(intent, 0)
                if (!resolveInfos.any()) {
                    throw ServiceConfigurationException(serviceFullPath, "Service not found - Service must be implemented")
                }
                if (resolveInfos.filter { it.serviceInfo.exported }.any()) {
                    throw ServiceConfigurationException(serviceFullPath, "Service should not be exported")
                }
            } ?: kotlin.run {
                throw ServiceConfigurationException(serviceFullPath, "packageManager not found")
            }
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
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

    private fun startTimeoutCounter() {
        afterTimeout = false
        transferResponseReceived = false
        handler.postDelayed({
            updateTransferTimeout()
        }, Consts.TRANSACTION_TIMEOUT)
    }

    private fun updateTransferTimeout() {
        afterTimeout = true
        if (!transferResponseReceived) {
            listener?.onTransferTimeout()
        }
    }

    private fun updateTransferCompleted(kinTransferComplete: SendKinServiceBase.KinTransferComplete) {
        transferResponseReceived = true
        if (!afterTimeout) {
            mainThreadHandler.post {
                listener?.onTransferComplete(kinTransferComplete)
            }
        }
    }

    private fun updateTransferFailed(e: SendKinServiceBase.KinTransferException) {
        transferResponseReceived = true
        if (!afterTimeout) {
            mainThreadHandler.post {
                val msg = e.message ?: ""
                listener?.onTransferFailed(msg, e.senderAddress)
            }
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