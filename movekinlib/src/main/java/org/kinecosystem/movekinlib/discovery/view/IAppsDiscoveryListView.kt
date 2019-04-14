package org.kinecosystem.movekinlib.discovery.view

import org.kinecosystem.movekinlib.base.IBaseView
import org.kinecosystem.movekinlib.model.EcosystemApp

interface IAppsDiscoveryListView : IBaseView{
    fun updateData(apps:List<EcosystemApp>)
    fun navigateToApp(app:EcosystemApp)

}