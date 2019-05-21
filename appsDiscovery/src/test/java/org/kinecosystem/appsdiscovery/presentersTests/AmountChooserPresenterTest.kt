package org.kinecosystem.appsdiscovery.presentersTests

import org.junit.Before
import org.junit.Test
import org.kinecosystem.appsdiscovery.sender.discovery.presenter.AmountChooserPresenter
import org.kinecosystem.appsdiscovery.sender.discovery.view.IAmountChooserView
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class AmountChooserPresenterTest {
    private val APP_ICON_URL = "appIconUrl"
    private val BALANCE = 100

    @Mock
    private lateinit var view: IAmountChooserView
    private lateinit var amountChooserPresenter: AmountChooserPresenter

    @Before
    fun setup(){
        MockitoAnnotations.initMocks(this)

    }
    @Test
    @Throws(Exception::class)
    fun test_OnAttach() {
        amountChooserPresenter = AmountChooserPresenter(APP_ICON_URL,BALANCE)
        amountChooserPresenter.onAttach(view)
        verify(view).initViews(APP_ICON_URL, BALANCE)
        amountChooserPresenter.onDetach()
    }
}
