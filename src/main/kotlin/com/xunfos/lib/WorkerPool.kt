/*
 * MIT License
 *
 * Copyright (c) 2022 Renato Costa
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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

class WorkerPool<T, U>(
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

data class Worker<T>(val id: Int, val value: T)
