package com.test.ktor

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.seconds

object KtorBugSample {

    suspend fun reproduceBug(
        onSent: () -> Unit,
        onReceived: () -> Unit,
        onCompleted: (Throwable?) -> Unit,
    ) {
        val url = "https://www.wikipedia.org/"

        val httpClient = HttpClient {
            // ++++++++++
            // TODO remove this line to make example work
            // This is the problem. Remove to make it work.
            install(ContentEncoding)
            // ++++++++++


            // Just to see some logs
            install(Logging) {
                level = LogLevel.INFO
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.i("KTOR", message)
                        if (message.startsWith("REQUEST: ")) {
                            onSent()
                        }
                        if (message.startsWith("RESPONSE: ")) {
                            onReceived()
                        }
                    }
                }
            }
        }

        try {
            withTimeout(3.seconds) {
                httpClient.get(url).bodyAsText()
            }
            onCompleted(null)
        } catch (e: Exception) {
            e.printStackTrace()
            onCompleted(e)
        }
    }
}