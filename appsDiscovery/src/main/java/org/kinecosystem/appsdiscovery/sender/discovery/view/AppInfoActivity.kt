package org.kinecosystem.appsdiscovery.sender.discovery.view

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import org.kinecosystem.appsdiscovery.R
import org.kinecosystem.appsdiscovery.receiver.service.ReceiveKinServiceBase
import org.kinecosystem.appsdiscovery.sender.discovery.presenter.AppInfoPresenter
import org.kinecosystem.appsdiscovery.sender.model.EcosystemApp
import org.kinecosystem.appsdiscovery.sender.model.name
import org.kinecosystem.appsdiscovery.sender.repositories.DiscoveryAppsLocal
import org.kinecosystem.appsdiscovery.sender.repositories.DiscoveryAppsRemote
import org.kinecosystem.appsdiscovery.sender.repositories.DiscoveryAppsRepository
import org.kinecosystem.appsdiscovery.sender.service.SendKinServiceBase
import org.kinecosystem.appsdiscovery.sender.transfer.TransferManager

class AppInfoActivity : AppCompatActivity(), IAppInfoView {

    override fun startSendKin(receiverAddress: String, amount: Int, memo: String, receiverPackage: String) {
        if (isBound) {
            val thread = Thread(Runnable {
                try {
                    val kinTransferComplete: SendKinServiceBase.KinTransferComplete = transferService?.transferKin(receiverAddress, amount, memo)!!
                    //TODO notify the transaction bar of complete
                    try {
                        ReceiveKinServiceBase.notifyTransactionCompleted(baseContext, receiverPackage, kinTransferComplete.senderAddress, receiverAddress, amount, kinTransferComplete.transactionId, memo)
                    } catch (kinReceiverServiceException: ReceiveKinServiceBase.KinReceiverServiceException) {
                        Log.d("####", "#### error notify receiver of transaction success ${kinReceiverServiceException.message}")
                    }
                } catch (kinTransferException: SendKinServiceBase.KinTransferException) {
                    //TODO notify the transaction bar of error
                    //notify the receiver of the error
                    Log.d("####", "#### kinTransferException ${kinTransferException.message}")
                    try {
                        Log.d("####", "#### ReceiveKinServiceBase.notifyTransactionFailed ${kinTransferException.message}")
                        ReceiveKinServiceBase.notifyTransactionFailed(baseContext, receiverPackage, kinTransferException.toString(), kinTransferException.senderAddress, receiverAddress, amount, memo)
                    } catch (kinReceiverServiceException: ReceiveKinServiceBase.KinReceiverServiceException) {
                        Log.d("####", "#### error notify receiver of transaction failed ${kinReceiverServiceException.message}")
                    }
                }
            })
            thread.start()
        }
    }

    override fun unbindToSendService() {
        unbindService(connection)
        isBound = false
    }

    override fun onServiceError(serviceNotFound: AppInfoPresenter.ServiceError) {
        //TODO cant start service show error
    }

    override fun bindToSendService(identifier: String?) {
        val intent = Intent()
        var receiverPackageName = identifier

        //TODO temp change to local sample
        receiverPackageName = packageName
        intent.component = ComponentName(receiverPackageName, "$receiverPackageName.SendKinService")
        intent.setPackage(receiverPackageName)
        val resolveInfos: MutableList<ResolveInfo> = packageManager.queryIntentServices(intent, 0)
        if (!resolveInfos.any()) {
            //TODO throw exception service not implemented
            presenter?.onServiceNotFound()
            Log.d("####", "#### cant find the service ${receiverPackageName}.SendKinService")
        }
        if (resolveInfos.filter {
                    it.serviceInfo.exported
                }.any()) {
            presenter?.onServiceShouldNotBeExported()
            //TODO throw exception service is exported andr remove the else
            Log.d("####", "####  service ${receiverPackageName}.SendKinService export must be declared on manifest false")
        } else {
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onRequestAmountError() {
        Toast.makeText(this, "onRequestAmountError ", Toast.LENGTH_LONG).show()
    }

    override fun startAmountChooserActivity(receiverAppIcon: String, balance: Int, requestCode: Int) {
        startActivityForResult(AmountChooserActivity.getIntent(this, receiverAppIcon, balance), requestCode)
    }

    override fun onRequestReceiverPublicAddressCanceled() {
        Toast.makeText(this, "canceld ", Toast.LENGTH_LONG).show()
    }

    override fun onRequestReceiverPublicAddressError(error: AppInfoPresenter.RequestReceiverPublicAddressError) {
        //TODO show error connection to other app
        Toast.makeText(this, "onRequestReceiverPublicAddressError ${error.name}", Toast.LENGTH_LONG).show()
    }

    override fun onStartRequestReceiverPublicAddress() {
        //TODO
    }

    override fun initViews(app: EcosystemApp?) {
        if (app == null) {
            finish()
        }
        findViewById<TextView>(R.id.name).setText(app?.name)
        findViewById<TextView>(R.id.pkg).setText(app?.identifier)

        findViewById<Button>(R.id.sendBtn).setOnClickListener {
            // presenter?.onSendKinClicked()
        }

        findViewById<Button>(R.id.approveBtn).setOnClickListener {
            presenter?.onRequestReceiverPublicAddress()
        }

    }

    private var presenter: AppInfoPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appName = intent.getStringExtra(PARAM_APP_NAME)
        if (appName.isNullOrBlank()) {
            finish()
        }
        setContentView(R.layout.app_info_activity)
        val discoveryAppsRepository = DiscoveryAppsRepository.getInstance(DiscoveryAppsLocal(this), DiscoveryAppsRemote(), Handler(Looper.getMainLooper()))
        presenter = AppInfoPresenter(appName, discoveryAppsRepository, TransferManager(this))
        presenter?.onAttach(this)
        presenter?.onStart()
    }

    companion object {
        private const val PARAM_APP_NAME = "PARAM_APP_NAME"

        fun getIntent(context: Context, appName: String): Intent {
            val intent = Intent(context, AppInfoActivity::class.java)
            intent.putExtra(PARAM_APP_NAME, appName)
            return intent
        }
    }

    private var isBound = false
    private var transferService: SendKinServiceBase? = null
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as SendKinServiceBase.KinTransferServiceBinder
            transferService = binder.service
            isBound = true
            updateBalance()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
        }
    }

    private fun updateBalance() {
        if (isBound) {
            val thread = Thread(Runnable {
                try {
                    transferService?.let {
                        val currentBalance = it.currentBalance
                        Log.d("####", "#### current balanbe is $currentBalance")
                        presenter?.updateBalance(currentBalance)
                    }
                } catch (balanceException: SendKinServiceBase.BalanceException) {
                    //TODO need to handle??
                    Log.d("####", "#### balanceException ${balanceException.message}")
                }
            })
            thread.start()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter?.processResponse(requestCode, resultCode, data)
    }

    //need to think if need to be in onstop - what happen if get response when you are in background
    override fun onDestroy() {
        super.onDestroy()
        presenter?.onDestroy()
    }

//    override fun onStop() {
//        super.onStop()
//        unbindService(connection)
//        isBound = false
//    }
}