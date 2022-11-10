package com.xunfos.lib

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

private class WorkerPool<T, U>(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private val inputChannel: Channel<T> = Channel()
    private val outputChannel: Channel<U> = Channel()
    val output: Flow<U> = outputChannel.consumeAsFlow()

    suspend fun doWork(input: T) {
        inputChannel.send(input)
    }

    suspend fun launchWorkers(amountOfWorkers: Int, work: suspend (Worker<T>) -> U) = coroutineScope {
        repeat(amountOfWorkers) { id ->
            launchWorker(id, inputChannel, work)
        }
    }

    private fun CoroutineScope.launchWorker(id: Int, channel: ReceiveChannel<T>, work: suspend (Worker<T>) -> U) =
        launch(dispatcher) {
            for (item in channel) {
                val result = work(Worker(id = id, value = item))
                outputResult(result)
            }
        }

    private fun CoroutineScope.outputResult(result: U) = launch(Dispatchers.IO) {
        outputChannel.send(result)
    }
}

private data class Worker<T>(val id: Int, val value: T)
