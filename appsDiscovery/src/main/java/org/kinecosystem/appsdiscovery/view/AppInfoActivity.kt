package org.kinecosystem.appsdiscovery.view

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
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import org.kinecosystem.appsdiscovery.R
import org.kinecosystem.transfer.receiver.service.ReceiveKinNotifier
import org.kinecosystem.transfer.receiver.service.ServiceConfigurationException
import org.kinecosystem.appsdiscovery.presenter.AppInfoPresenter
import org.kinecosystem.appsdiscovery.view.customView.AppImagesListAdapter
import org.kinecosystem.appsdiscovery.view.customView.AppStateView
import org.kinecosystem.appsdiscovery.view.customView.TransferBarView
import org.kinecosystem.appsdiscovery.view.customView.TransferInfo
import org.kinecosystem.transfer.repositories.EcosystemAppsLocalRepo
import org.kinecosystem.transfer.repositories.EcosystemAppsRemoteRepo
import org.kinecosystem.transfer.repositories.EcosystemAppsRepository
import org.kinecosystem.transfer.sender.service.SendKinServiceBase
import org.kinecosystem.transfer.sender.manager.TransferManager
import org.kinecosystem.common.base.Consts
import org.kinecosystem.common.utils.load
import org.kinecosystem.common.utils.navigateToUrl
import org.kinecosystem.transfer.model.*
import org.kinecosystem.transfer.repositories.KinTransferCallback
import java.util.concurrent.Executors

class AppInfoActivity : AppCompatActivity(), IAppInfoView {
    private val TAG = AppInfoActivity::class.java.simpleName
    private var presenter: AppInfoPresenter? = null
    private var appStateView: AppStateView? = null
    private var transferBarView: TransferBarView? = null
    private var list: RecyclerView? = null
    @Volatile
    private var isBound = false
    private var transferService: SendKinServiceBase? = null
    private var executorService = Executors.newCachedThreadPool()
    private lateinit var uiHandler: Handler
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as SendKinServiceBase.KinTransferServiceBinder
            transferService = binder.service
            isBound = true
            requestCurrentBalance()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiHandler = Handler(Looper.getMainLooper())
        val appName = intent.getStringExtra(PARAM_APP_NAME)
        if (appName.isNullOrBlank()) {
            finish()
        }
        setContentView(R.layout.app_discovery_module_app_info_activity)
        list = findViewById(R.id.listImages)
        list?.isFocusable = false
        list?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        findViewById<ImageView>(R.id.closeX).setOnClickListener {
            finish()
        }
        val repository = EcosystemAppsRepository.getInstance(packageName, EcosystemAppsLocalRepo(this), EcosystemAppsRemoteRepo(), Handler(Looper.getMainLooper()))
        transferBarView = findViewById(R.id.transferBar)
        presenter = AppInfoPresenter(appName, repository, TransferManager(this))
        presenter?.onAttach(this)
        presenter?.onStart()

    }

    override fun onResume() {
        super.onResume()
        presenter?.onResume(baseContext)
    }

    private fun sendKinAsync(receiverAddress: String, senderAppName: String, amount: Int, memo: String, receiverPackage: String) {
        transferService?.transferKinAsync(receiverAddress, amount, memo, object : KinTransferCallback {
            override fun onSuccess(kinTransferComplete: SendKinServiceBase.KinTransferComplete) {
                uiHandler.post {
                    presenter?.onTransferComplete()
                    notifyTransactionCompleted(kinTransferComplete, receiverPackage, senderAppName, receiverAddress, amount)
                }
            }

            override fun onError(e: SendKinServiceBase.KinTransferException) {
                uiHandler.post {
                    presenter?.onTransferFailed()
                    notifyTransactionFailed(e, receiverPackage, senderAppName, receiverAddress, amount, memo)
                }
            }

        })
    }

    private fun notifyTransactionCompleted(kinTransferComplete: SendKinServiceBase.KinTransferComplete, receiverPackage: String, senderAppName: String, receiverAddress: String, amount: Int) {
        try {
            ReceiveKinNotifier.notifyTransactionCompleted(baseContext, receiverPackage,
                    kinTransferComplete.senderAddress, senderAppName, receiverAddress, amount,
                    kinTransferComplete.transactionId, kinTransferComplete.transactionMemo)
            Log.d(TAG, "Receiver was notified of transaction complete")
        } catch (e: ServiceConfigurationException) {
            Log.d(TAG, "Error notifying the receiver of transaction complete ${e.message}")
            e.printStackTrace()
        }
    }

    private fun notifyTransactionFailed(e: SendKinServiceBase.KinTransferException, receiverPackage: String, senderAppName: String, receiverAddress: String, amount: Int, memo: String) {
        Log.d(TAG, "Exception while transferring Kin,  SendKinServiceBase.KinTransferException ${e.message}")
        try {
            ReceiveKinNotifier.notifyTransactionFailed(baseContext, receiverPackage,
                    e.toString(), e.senderAddress, senderAppName, receiverAddress, amount, memo)
            Log.d(TAG, "Receiver was notified of transaction failed")

        } catch (e: ServiceConfigurationException) {
            Log.d(TAG, "Error notifying the receiver of transaction failed ${e.message}")
            e.printStackTrace()
        }
    }

    override fun sendKin(receiverAddress: String, senderAppName: String, amount: Int, memo: String, receiverPackage: String) {
        executorService.execute {
            if (isBound) {
                try {
                    Log.e("sendKin", "transferService $transferService receiverAddress $receiverAddress amount:$amount ")
                    val kinTransferComplete = transferService?.transferKin(receiverAddress, amount, memo)
                    kinTransferComplete?.let {
                        uiHandler.post {
                            presenter?.onTransferComplete()
                            notifyTransactionCompleted(it, receiverPackage, senderAppName, receiverAddress, amount)
                        }
                    } ?: run {
                        sendKinAsync(receiverAddress, senderAppName, amount, memo, receiverPackage)
                    }
                } catch (e: SendKinServiceBase.KinTransferException) {
                    uiHandler.post {
                        presenter?.onTransferFailed()
                        notifyTransactionFailed(e, receiverPackage, senderAppName, receiverAddress, amount, memo)
                    }
                }
            }
        }
    }

    override fun unbindToSendService() {
        unbindService(connection)
        isBound = false
    }


    override fun bindToSendService() {
        val intent = Intent()

        val senderPackageName = packageName
        val serviceFullPath = "$senderPackageName.${Consts.SERVICE_DEFAULT_PACKAGE}.${Consts.SENDER_SERVICE_NAME}"
        intent.component = ComponentName(senderPackageName, serviceFullPath)
        intent.`package` = senderPackageName
        val resolveInfos: MutableList<ResolveInfo> = packageManager.queryIntentServices(intent, 0)
        if (!resolveInfos.any()) {
            throw ServiceConfigurationException(serviceFullPath, "Service not found - Service must be implemented")
        }
        if (resolveInfos.filter { it.serviceInfo.exported }.any()) {
            throw ServiceConfigurationException(serviceFullPath, "Service should not be exported")
        }

        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun startAmountChooserActivity(receiverAppIcon: String, balance: Int, requestCode: Int) {
        startActivityForResult(AmountChooserActivity.getIntent(this, receiverAppIcon, balance), requestCode)
    }

    override fun initViews(app: EcosystemApp?) {
        if (app == null) {
            finish()
        }
        with(app?.metaData?.experienceData) {
            findViewById<TextView>(R.id.howInfo).text = this?.howTo ?: ""
            findViewById<TextView>(R.id.experienceName).text = this?.name ?: ""
            findViewById<TextView>(R.id.experienceInfo).text = this?.about ?: ""
        }
        findViewById<TextView>(R.id.byApp).text = resources.getString(R.string.apps_discovery_by_app, app?.name)
        findViewById<TextView>(R.id.aboutAppTitle).text = resources.getString(R.string.apps_discovery_about_app, app?.name)
        findViewById<TextView>(R.id.aboutAppInfo).text = app?.metaData?.about

        findViewById<View>(R.id.headerView).setBackgroundColor(app?.cardColor!!)
        findViewById<TextView>(R.id.category).text = app.category

        findViewById<ImageView>(R.id.icon).load(app.iconUrl)
        findViewById<ImageView>(R.id.appBigIcon).load(app.iconUrl)

        with(findViewById<TextView>(R.id.actionText)) {
            setLineSpacing(android.util.TypedValue.applyDimension(android.util.TypedValue.COMPLEX_UNIT_DIP, app.fontLineSpacing, context.resources.displayMetrics), 1.0f)
            setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, app.cardFontSize)
            typeface = org.kinecosystem.common.utils.TextUtils.getFontTypeForType(context, app.cardFontName)
            setLineSpacing(android.util.TypedValue.applyDimension(android.util.TypedValue.COMPLEX_UNIT_DIP, app.fontLineSpacing, context.resources.displayMetrics), 1.0f)
            text = app.cardTitle
        }
        appStateView = findViewById(R.id.appState)
        appStateView?.setListener(object : AppStateView.IActionClickListener {
            override fun onActionButtonClicked() {
                presenter?.onActionButtonClicked()
            }
        })
        app.metaData?.images?.let {
            list?.adapter = AppImagesListAdapter(this, it)
        }
    }

    override fun initTransfersInfo(transferInfo: TransferInfo) {
        transferBarView?.updateViews(transferInfo)
    }

    override fun updateAmount(amount: Int) {
        transferBarView?.updateAmount(amount)
    }

    override fun updateTransferStatus(status: TransferBarView.TransferStatus) {
        transferBarView?.updateStatus(status)
    }

    override fun requestCurrentBalance() {
        if (isBound) {
            executorService.execute {
                try {
                    transferService?.let {
                        val currentBalance = it.currentBalance.toInt()
                        presenter?.updateBalance(currentBalance)
                    }
                } catch (balanceException: SendKinServiceBase.BalanceException) {
                    //ignore if we dont get balance - user can send amount with no limit but will fail if it exceeds his balance
                    Log.d(TAG, "balanceException ${balanceException.message}")
                }
            }
        }
    }

    companion object {
        private const val PARAM_APP_NAME = "PARAM_APP_NAME"

        fun getIntent(context: Context, appName: String): Intent {
            val intent = Intent(context, AppInfoActivity::class.java)
            intent.putExtra(PARAM_APP_NAME, appName)
            return intent
        }
    }


    override fun navigateTo(downloadUrl: String) {
        navigateToUrl(downloadUrl)
    }

    override fun updateAppState(state: AppStateView.State) {
        appStateView?.update(state)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        presenter?.processResponse(requestCode, resultCode, intent)
    }

    //need to think if need to be in onstop - what happen if get response when you are in background
    override fun onDestroy() {
        super.onDestroy()
        presenter?.onDestroy()
        transferService?.cancelCallback()
    }

}