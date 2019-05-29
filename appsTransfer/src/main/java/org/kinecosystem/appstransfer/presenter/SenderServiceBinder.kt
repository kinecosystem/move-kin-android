package org.kinecosystem.appstransfer.presenter

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ResolveInfo
import android.os.IBinder
import android.util.Log
import org.kinecosystem.common.base.Consts
import org.kinecosystem.transfer.receiver.service.ServiceConfigurationException
import org.kinecosystem.transfer.sender.service.SendKinServiceBase
import java.util.concurrent.Executors

class SenderServiceBinder(private val context: Context?) {

    private val TAG = "SenderServiceBinder"//::class.simpleName

    interface BinderListener {
        fun onBalanceReceived(balance: Int)
        fun onBalanceFailed()
        fun onServiceConnected()
        fun onServiceDisconnected()
    }

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
            listener?.onServiceConnected()
            // requestCurrentBalance()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBounded = false
            listener?.onServiceDisconnected()
        }
    }

    fun setListener(balanceListener: BinderListener) {
        this.listener = balanceListener
    }

    fun requestCurrentBalance() {
        if (isBounded) {
            executorService.execute {
                try {
                    transferService?.let {
                        listener?.onBalanceReceived(it.currentBalance.toInt())
                    }
                } catch (balanceException: SendKinServiceBase.BalanceException) {
                    listener?.onBalanceFailed()
                    //ignore if we dont get balance - user can send amount with no limit but will fail if it exceeds his balance
                    //Log.d(TAG, "balanceException ${balanceException.message}")
                }
            }
        } else {
            Log.e(TAG, "service must call bind first and wait until its bounded")
        }
    }

    fun startSendKin(receiverAddress: String, senderAppName: String, amount: Int, memo: String, receiverPackage: String) {
        if (isBounded) {
            executorService.execute {
                try {
                    val kinTransferComplete: SendKinServiceBase.KinTransferComplete =
                            transferService?.transferKin(receiverAddress, amount, memo)!!
                    // presenter?.onTransferComplete()
                    try {
//                        ReceiveKinNotifier.notifyTransactionCompleted(baseContext, receiverPackage,
//                                kinTransferComplete.senderAddress, senderAppName, receiverAddress, amount,
//                                kinTransferComplete.transactionId, kinTransferComplete.transactionMemo)
                        Log.d(TAG, "Receiver was notified of transaction complete")
                    } catch (e: ServiceConfigurationException) {
                        Log.d(TAG, "Error notifying the receiver of transaction complete ${e.message}")
                        e.printStackTrace()
                    }
                } catch (e: SendKinServiceBase.KinTransferException) {
                    //presenter?.onTransferFailed()
                    Log.d(TAG, "Exception while transferring Kin,  SendKinServiceBase.KinTransferException ${e.message}")
                    try {
//                        ReceiveKinNotifier.notifyTransactionFailed(baseContext, receiverPackage,
//                                e.toString(), e.senderAddress, senderAppName, receiverAddress, amount, memo)
                        Log.d(TAG, "Receiver was notified of transaction failed")

                    } catch (e: ServiceConfigurationException) {
                        Log.d(TAG, "Error notifying the receiver of transaction failed ${e.message}")
                        e.printStackTrace()
                    }
                }
            }
        } else {
            Log.e(TAG, "service must call bind first and wait until its bounded")
        }
    }


    fun bind() {
        if (isBounded) {
            listener?.onServiceConnected()
            return
        }
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
        context?.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    fun unbind() {
        context?.unbindService(connection)
        isBounded = false
    }
}