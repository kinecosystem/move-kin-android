package org.kinecosystem.transfer.repositories


interface OperationCompletionCallback {
    fun onSuccess()
    fun onError(error: String)
}

interface OperationResultCallback<in T> {
    fun onResult(result: T)
    fun onError(error: String)
}
