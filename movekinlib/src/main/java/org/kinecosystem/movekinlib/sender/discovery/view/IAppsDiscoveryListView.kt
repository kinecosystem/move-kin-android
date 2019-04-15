package org.kinecosystem.movekinlib.sender.discovery.view

import org.kinecosystem.movekinlib.base.IBaseView
import org.kinecosystem.movekinlib.sender.model.EcosystemApp

interface IAppsDiscoveryListView : IBaseView{
    fun updateData(apps:List<EcosystemApp>)
    fun navigateToApp(app:EcosystemApp)

}