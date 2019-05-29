package org.kinecosystem.appsdiscovery.repositories

import org.kinecosystem.appsdiscovery.service.SendKinServiceBase

interface OperationCompletionCallback {
    fun onSuccess()
    fun onError(error: String)
}

interface OperationResultCallback<in T> {
    fun onResult(result: T)
    fun onError(error: String)
}

interface kinTransferCallback {
    fun onResult(kinTransferComplete: SendKinServiceBase.KinTransferComplete)
    fun onError(e: SendKinServiceBase.KinTransferException)
}
