package org.kinecosystem.appsdiscovery.repositories

import org.kinecosystem.transfer.sender.service.KinTransferComplete
import org.kinecosystem.transfer.sender.service.KinTransferException
import org.kinecosystem.transfer.sender.service.SendKinServiceBase

interface OperationCompletionCallback {
    fun onSuccess()
    fun onError(error: String)
}

interface OperationResultCallback<in T> {
    fun onResult(result: T)
    fun onError(error: String)
}

interface KinTransferCallback {
    fun onSuccess(kinTransferComplete: KinTransferComplete)
    fun onError(e: KinTransferException)
}
