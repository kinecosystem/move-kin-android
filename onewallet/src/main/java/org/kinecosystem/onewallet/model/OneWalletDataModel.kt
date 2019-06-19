package org.kinecosystem.onewallet.model

import org.kinecosystem.common.base.LocalStore

class OneWalletDataModel(private var localStore: LocalStore) {
    private val EXTRA_MASTER_PUBLIC_ADDRESS = "EXTRA_MASTER_PUBLIC_ADDRESS"
    private var errorCount = 0

    var masterPublicAddress: String? = null
        set(value) {
            if (field != value) {
                value?.let {
                    field = it
                    localStore.updateString(EXTRA_MASTER_PUBLIC_ADDRESS, it)
                }
            }
        }
        get() {
            if (field == null && localStore.hasString(EXTRA_MASTER_PUBLIC_ADDRESS)) {
                field = localStore.getString(EXTRA_MASTER_PUBLIC_ADDRESS, "")
            }
            return field
        }

    fun shouldTryAgain(): Boolean{
        val tryAgain = errorCount == 0
        errorCount ++
        return tryAgain
    }

    fun isWalletLinked(): Boolean = masterPublicAddress != null

}
