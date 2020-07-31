package com.example.overlay

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.launch

object TimerUtil {
    @ObsoleteCoroutinesApi
    fun start(seconds: Double, func: (Int) -> (Unit)) {
        val tickerChannel = ticker(delayMillis = 1_000, initialDelayMillis = 0)
        GlobalScope.launch {
            repeat((seconds + 1).toInt()) {
                tickerChannel.receive()
                func.invoke(it)
            }
        }
    }
}