package org.kinecosystem.movekinlib.discovery.view

import org.kinecosystem.movekinlib.base.IBaseView

interface IAppsDiscoveryListView : IBaseView{
    fun updateData(data:List<String>)
    fun navigateToApp(app:String)

}