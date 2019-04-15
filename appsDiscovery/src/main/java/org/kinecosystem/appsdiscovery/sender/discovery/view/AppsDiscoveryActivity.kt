package org.kinecosystem.appsdiscovery.sender.discovery.view

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import org.kinecosystem.appsdiscovery.R
import org.kinecosystem.appsdiscovery.receiver.service.ReceiveKinServiceBase
import org.kinecosystem.appsdiscovery.sender.service.SendKinServiceBase

class AppsDiscoveryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.apps_discovery_activity)
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, AppsDiscoveryActivity::class.java)
    }

    private var isBound = false
    private var transferService: SendKinServiceBase? = null
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as SendKinServiceBase.KinTransferServiceBinder
            transferService = binder.service
            isBound = true
            //TODO call only when try to send
            startSendKinRequest()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
        }
    }

    fun startSendKinRequest() {
        //TODO
        //update the transaction bar of the start of the transaction
        if (isBound) {
            val thread = Thread(Runnable {
                val receiver = "SwellyAddress"
                val amount = 3
                val memo = "lean"

                try {
                    val currentBalance = transferService?.currentBalance
                    Log.d("####", "#### current balanbe is $currentBalance")
                } catch (balanceException: SendKinServiceBase.BalanceException) {
                    //TODO need to handle??
                    Log.d("####", "#### balanceException ${balanceException.message}")
                }

                try {
                    val kinTransferComplete = transferService?.transferKin(receiver, amount, memo)!!
                    //TODO notify the transaction bar of complete
                    //notify the receiver of the transaction
                    ReceiveKinServiceBase.notifyTransactionCompleted(baseContext, kinTransferComplete.senderAddress, receiver, amount, kinTransferComplete.transactionId, memo)
                } catch (kinTransferException: SendKinServiceBase.KinTransferException) {
                    //TODO notify the transaction bar of error
                    //notify the receiver of the error
                    Log.d("####", "#### kinTransferException ${kinTransferException.message}")
                    ReceiveKinServiceBase.notifyTransactionFailed(baseContext, kinTransferException.toString(), kinTransferException.senderAddress, receiver, amount, memo)
                }
            })
            thread.start()
        }
    }

    override fun onStart() {
        super.onStart()
        testKinSendService()
    }

    private fun testKinSendService(){
        val intent = Intent()
        //TODO change to receiver pkg
        val receiverPackageName = applicationContext.packageName

        intent.component = ComponentName(receiverPackageName, "$receiverPackageName.SendKinService")
        intent.setPackage(receiverPackageName)
        val resolveInfos: MutableList<ResolveInfo> = packageManager.queryIntentServices(intent, 0)
        if (!resolveInfos.any()) {
            //TODO throw exception service not implemented
            Log.d("####", "#### cant find the service ${receiverPackageName}.SendKinService")
        }
        if (resolveInfos.filter {
                    it.serviceInfo.exported
                }.any()) {
            //TODO throw exception service is exported andr remove the else
            Log.d("####", "####  service ${receiverPackageName}.SendKinService export must be declared on manifest false")
        } else {
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    //TODO maybe need to be in the presenter without unbind
    override fun onStop() {
        super.onStop()
        unbindService(connection)
        isBound = false
    }
}