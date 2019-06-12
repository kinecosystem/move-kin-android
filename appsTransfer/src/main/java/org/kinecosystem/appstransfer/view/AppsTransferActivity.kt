package org.kinecosystem.appstransfer.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.constraint.Group
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import org.kinecosystem.appstransfer.R
import org.kinecosystem.appstransfer.presenter.AppsTransferPresenter
import org.kinecosystem.appstransfer.view.customview.AppsTransferList
import org.kinecosystem.appstransfer.view.customview.TransferErrorDialog
import org.kinecosystem.common.utils.navigateToUrl
import org.kinecosystem.transfer.model.EcosystemApp
import org.kinecosystem.transfer.model.name
import org.kinecosystem.transfer.sender.manager.TransferManager

class AppsTransferActivity : AppCompatActivity(), IAppsTransferView {

    private lateinit var dataGroup: Group
    private lateinit var noDataGroup: Group
    private lateinit var loader: ProgressBar
    private lateinit var list: AppsTransferList
    private var presenter: AppsTransferPresenter? = null

    override fun onTransferError(appName: String) {
        TransferErrorDialog(this, TransferErrorDialog.ErrorType.ConnectionError, appName).show()
    }

    override fun onCantFindReceiverInfo(appName: String) {
        TransferErrorDialog(this, TransferErrorDialog.ErrorType.ConnectionFailed, appName).show()
    }

    override fun startTransferAmountActivity(app: EcosystemApp, receiverPublicAddress: String) {
        startActivity(TransferAmountActivity.getIntent(this, app.name, receiverPublicAddress))
    }

    override fun navigateToAppStore(url: String) {
        navigateToUrl(url)
    }

    override fun showLoading() {
        loader.visibility = View.VISIBLE
        noDataGroup.visibility = View.GONE
        dataGroup.visibility = View.GONE
    }

    override fun showData() {
        dataGroup.visibility = View.VISIBLE
        loader.visibility = View.GONE
        noDataGroup.visibility = View.GONE
    }

    override fun showNoData() {
        noDataGroup.visibility = View.VISIBLE
        dataGroup.visibility = View.GONE
        loader.visibility = View.GONE
    }

    override fun invalidateList() {
        list.invalidateApps()
    }

    override fun close() {
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.apps_transfer_activity)
        dataGroup = findViewById(R.id.data)
        noDataGroup = findViewById(R.id.noData)
        loader = findViewById(R.id.loader)
        list = findViewById(R.id.list)
        list.clickListener = presenter
        presenter = AppsTransferPresenter(TransferManager(this))

        findViewById<ImageView>(R.id.close_x).setOnClickListener {
            presenter?.onCloseClicked()
        }
        presenter?.onAttach(this)
        presenter?.let {
            findViewById<AppsTransferList>(R.id.list).setLoadingListener(it)
            list.clickListener = it
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter?.processResponse(requestCode, resultCode, data)
    }

    override fun onResume() {
        super.onResume()
        presenter?.onResume()
    }

    //public method
    companion object {
        fun getIntent(context: Context) = Intent(context, AppsTransferActivity::class.java)
    }
}