package org.kinecosystem.appsdiscovery

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.kinecosystem.appsdiscovery.model.*
import org.kinecosystem.appsdiscovery.presenter.AmountChooserPresenter
import org.kinecosystem.appsdiscovery.presenter.AppInfoPresenter
import org.kinecosystem.appsdiscovery.repositories.DiscoveryAppsRepository
import org.kinecosystem.appsdiscovery.view.IAppInfoView
import org.kinecosystem.appsdiscovery.view.customView.AppStateView
import org.kinecosystem.appsdiscovery.view.customView.TransferBarView
import org.kinecosystem.transfer.sender.manager.TransferManager
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AppInfoPresenterTest {
    private val appName = "APP_NAME"
    private val amount = 50

    @Mock
    private lateinit var view: IAppInfoView
    private lateinit var appInfoPresenter: AppInfoPresenter
    private var repository = mock(DiscoveryAppsRepository::class.java)
    private var transferManager: TransferManager = mock(TransferManager::class.java)

    private var app_with_transfer = EcosystemApp("cat","id","kit",
            MetaData("about", "name", "url","url",
                    ArrayList(),
                    ExperienceData("about","howto","name"),
                    CardData("0f","name","size", "bgc", "title")),
            TransferData("path"))

    private var app_without_transfer = EcosystemApp("cat","id","kit",
            MetaData("about", "name", "url","url",
                    ArrayList(),
                    ExperienceData("about","howto","name"),
                    CardData("0f","name","size", "bgc", "title")),
            null)

    @Before
    fun setup(){
        MockitoAnnotations.initMocks(this)
        appInfoPresenter = AppInfoPresenter(appName,repository, transferManager)
    }

    @After
    fun teardown(){
        appInfoPresenter.onDetach()
    }

    @Test
    @Throws(Exception::class)
    fun test_OnAttach() {
        `when`(repository.getAppByName(appName)).thenReturn(app_with_transfer)
        appInfoPresenter.onAttach(view)
        verify(view).initViews(app_with_transfer)
    }

    @Test
    @Throws(Exception::class)
    fun test_appState_when_appNotInstalled(){
        val context = mock(Context::class.java)
        val packageManager = mock(PackageManager::class.java)
        `when`(repository.getAppByName(appName)).thenReturn(app_with_transfer)
        `when`(context.packageManager).thenReturn(packageManager)
        `when`(packageManager.getPackageInfo(anyString(), anyInt())).thenThrow(PackageManager.NameNotFoundException())

        appInfoPresenter.onAttach(view)
        appInfoPresenter.onResume(context)
        verify(view).updateAppState(AppStateView.State.NotInstalled)
    }

    @Test
    @Throws(Exception::class)
    fun test_appState_when_appInstalled_and_receiveSupported(){
        val context = mock(Context::class.java)
        val packageManager = mock(PackageManager::class.java)
        `when`(repository.getAppByName(appName)).thenReturn(app_with_transfer)
        `when`(context.packageManager).thenReturn(packageManager)
        `when`(packageManager.getPackageInfo(anyString(), anyInt())).thenReturn(mock(PackageInfo::class.java))

        appInfoPresenter.onAttach(view)
        appInfoPresenter.onResume(context)
        verify(view).updateAppState(AppStateView.State.ReceiveKinSupported)
    }

    @Test
    @Throws(Exception::class)
    fun test_appState_when_appInstalled_and_receiveNotSupported(){
        val context = mock(Context::class.java)
        val packageManager = mock(PackageManager::class.java)
        `when`(repository.getAppByName(appName)).thenReturn(app_without_transfer)
        `when`(context.packageManager).thenReturn(packageManager)
        `when`(packageManager.getPackageInfo(anyString(), anyInt())).thenReturn(mock(PackageInfo::class.java))

        appInfoPresenter.onAttach(view)
        appInfoPresenter.onResume(context)
        verify(view).updateAppState(AppStateView.State.ReceiveKinNotSupported)
    }

    @Test
    @Throws(Exception::class)
    fun test_onActionButtonClicked_when_appNotInstalled(){
        val context = mock(Context::class.java)
        val packageManager = mock(PackageManager::class.java)
        `when`(repository.getAppByName(appName)).thenReturn(app_with_transfer)
        `when`(context.packageManager).thenReturn(packageManager)
        `when`(packageManager.getPackageInfo(anyString(), anyInt())).thenThrow(PackageManager.NameNotFoundException())

        appInfoPresenter.onAttach(view)
        appInfoPresenter.onResume(context)
        appInfoPresenter.onActionButtonClicked()

        verify(view).navigateTo(app_with_transfer.downloadUrl)
    }

    @Test
    @Throws(Exception::class)
    fun test_onActionButtonClicked_when_appInstalled_startTransferRequestActivity_true(){
        val context = mock(Context::class.java)
        val packageManager = mock(PackageManager::class.java)
        `when`(repository.getAppByName(appName)).thenReturn(app_with_transfer)
        `when`(context.packageManager).thenReturn(packageManager)
        `when`(packageManager.getPackageInfo(anyString(), anyInt())).thenReturn(mock(PackageInfo::class.java))
        val intentBuilder = mock(TransferManager.IntentBuilder::class.java)
        `when`(transferManager.intentBuilder(anyString(), anyString())).thenReturn(intentBuilder)
        `when`(intentBuilder.startForResult(anyInt())).thenReturn(true)

        appInfoPresenter.onAttach(view)
        appInfoPresenter.onResume(context)

        val appInfoPresenterSpy = spy(appInfoPresenter)
        appInfoPresenterSpy.onActionButtonClicked()
        verify(appInfoPresenterSpy).onRequestReceiverPublicAddress()
        verify(view, times(0)).updateTransferStatus(TransferBarView.TransferStatus.FailedReceiverError)
    }

    @Test
    @Throws(Exception::class)
    fun test_onActionButtonClicked_when_appInstalled_startTransferRequestActivity_false(){
        val context = mock(Context::class.java)
        val packageManager = mock(PackageManager::class.java)
        `when`(repository.getAppByName(appName)).thenReturn(app_with_transfer)
        `when`(context.packageManager).thenReturn(packageManager)
        `when`(packageManager.getPackageInfo(anyString(), anyInt())).thenReturn(mock(PackageInfo::class.java))
        val intentRequestBuilder = mock(TransferManager.IntentBuilder::class.java)
        `when`(transferManager.intentBuilder(anyString(), anyString())).thenReturn(intentRequestBuilder)
        `when`(intentRequestBuilder.startForResult(anyInt())).thenReturn(false)

        appInfoPresenter.onAttach(view)
        appInfoPresenter.onResume(context)

        val appInfoPresenterSpy = spy(appInfoPresenter)
        appInfoPresenterSpy.onActionButtonClicked()
        verify(appInfoPresenterSpy).onRequestReceiverPublicAddress()
        verify(view).updateTransferStatus(TransferBarView.TransferStatus.FailedReceiverError)
    }


    @Test
    @Throws(Exception::class)
    fun test_processResponse_when_requestCode_AMOUNT_CHOOSER_REQUEST_CODE_resultCode_RESULT_OK(){
        val context = mock(Context::class.java)
        val packageManager = mock(PackageManager::class.java)
        `when`(repository.getAppByName(appName)).thenReturn(app_with_transfer)
        `when`(context.packageManager).thenReturn(packageManager)
        `when`(packageManager.getPackageInfo(anyString(), anyInt())).thenReturn(mock(PackageInfo::class.java))
        `when`(repository.getStoredAppIcon()).thenReturn("icon")
        `when`(repository.getReceiverAppPublicAddress()).thenReturn("public_address")

        appInfoPresenter.onAttach(view)
        appInfoPresenter.onResume(context)

        val returnIntent = Intent()
        returnIntent.putExtra(AmountChooserPresenter.PARAM_AMOUNT, amount)
        appInfoPresenter.processResponse(appInfoPresenter.AMOUNT_CHOOSER_REQUEST_CODE, Activity.RESULT_OK, returnIntent)

        verify(view).updateTransferStatus(TransferBarView.TransferStatus.Started)
        verify(view).startSendKin(anyString(),anyString(),anyInt(),anyString(),anyString())
        verify(view, times(0)).updateTransferStatus(TransferBarView.TransferStatus.Timeout)
    }

    @Test
    @Throws(Exception::class)
    fun test_processResponse_when_requestCode_NOT_AMOUNT_CHOOSER_REQUEST_CODE_onError(){
        val context = mock(Context::class.java)
        val packageManager = mock(PackageManager::class.java)
        `when`(repository.getAppByName(appName)).thenReturn(app_with_transfer)
        `when`(context.packageManager).thenReturn(packageManager)
        `when`(packageManager.getPackageInfo(anyString(), anyInt())).thenReturn(mock(PackageInfo::class.java))
        `when`(repository.getStoredAppIcon()).thenReturn("icon")
        `when`(repository.getReceiverAppPublicAddress()).thenReturn("public_address")

        appInfoPresenter.onAttach(view)
        appInfoPresenter.onResume(context)

        val returnIntent = Intent()
        returnIntent.putExtra(AmountChooserPresenter.PARAM_AMOUNT, amount)

        doAnswer { invocation ->
            val callback = invocation.getArgument<TransferManager.AccountInfoResponseListener>(2)
            callback.onError("some error")
        }.`when`(transferManager).processResponse(anyInt(), any(Intent::class.java), any(TransferManager.AccountInfoResponseListener::class.java))


        appInfoPresenter.processResponse(appInfoPresenter.REMOTE_PUBLIC_ADDRESS_REQUEST_CODE, 0, returnIntent)
        verify(view).updateTransferStatus(TransferBarView.TransferStatus.FailedConnectionError)
    }

    @Throws(Exception::class)
    fun test_processResponse_when_requestCode_NOT_AMOUNT_CHOOSER_REQUEST_CODE_onAddressReceived(){
        val context = mock(Context::class.java)
        val packageManager = mock(PackageManager::class.java)
        `when`(repository.getAppByName(appName)).thenReturn(app_with_transfer)
        `when`(context.packageManager).thenReturn(packageManager)
        `when`(packageManager.getPackageInfo(anyString(), anyInt())).thenReturn(mock(PackageInfo::class.java))
        `when`(repository.getStoredAppIcon()).thenReturn("icon")
        `when`(repository.getReceiverAppPublicAddress()).thenReturn("public_address")
        doNothing().`when`(repository.storeReceiverAppPublicAddress(anyString()))

        appInfoPresenter.onAttach(view)
        appInfoPresenter.onResume(context)

        val returnIntent = Intent()
        returnIntent.putExtra(AmountChooserPresenter.PARAM_AMOUNT, amount)

        doAnswer { invocation ->
            val callback = invocation.getArgument<TransferManager.AccountInfoResponseListener>(2)
            callback.onResult("address")
        }.`when`(transferManager).processResponse(anyInt(), any(Intent::class.java), any(TransferManager.AccountInfoResponseListener::class.java))


        appInfoPresenter.processResponse(0, 0, returnIntent)
        verify(view).startAmountChooserActivity(anyString(), anyInt(), anyInt())
    }
}

private fun <T> any(type : Class<T>): T {
    Mockito.any(type)
    return null as T
}