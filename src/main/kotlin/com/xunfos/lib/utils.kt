package com.xunfos.lib

import kotlinx.coroutines.delay

suspend fun <T> withDelay(timeoutMillis: Long = 100L, block: () -> T): T {
    delay(timeoutMillis)
    return block()
}
