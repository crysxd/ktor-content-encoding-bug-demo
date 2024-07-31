package com.test.ktor

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.test.ktor.ui.theme.MyApplicationTheme
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Url
import kotlinx.coroutines.async

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val loading = "Loading"
            var state by remember { mutableStateOf(loading) }
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Text(
                        text = state,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }

            LaunchedEffect(Unit) {
                val urls = listOf(
                    "https://www.wikipedia.org/",
                    "https://www.wikipedia.org/portal/wikipedia.org/assets/img/Wikipedia-logo-v2@2x.png",
                    "https://www.wikipedia.org/portal/wikipedia.org/assets/js/index-24c3e2ca18.js",
                    "https://www.wikipedia.org/portal/wikipedia.org/assets/js/gt-ie9-ce3fe8e88d.js",
                    "https://www.wikipedia.org/portal/wikipedia.org/assets/img/sprite-de847d1a.svg",
                    "https://www.wikipedia.org/portal/wikipedia.org/assets/img/Wikinews-logo_sister@2x.png",
                    "https://www.wikipedia.org/static/favicon/wikipedia.ico",
                )

                val httpClient = HttpClient {
                    // This is the problem. Remove to make it work.
                    install(ContentEncoding)

                    // To make it fail fast
                    install(HttpTimeout) {
                        requestTimeoutMillis = 5000
                    }

                    // Just to see some logs
                    install(Logging) {
                        level = LogLevel.INFO
                        logger = object : Logger {
                            var completedCounter = 0
                            override fun log(message: String) {
                                Log.i("KTOR", message)
                                if (message.startsWith("RESPONSE: 200")) {
                                    completedCounter++
                                }
                                if (completedCounter == urls.size) {
                                    Toast.makeText(this@MainActivity,"All requests completed", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                }

                urls.map {
                    async {
                        httpClient.get(it).bodyAsText()
                    }
                }.forEach {
                    try {
                        it.await()
                    } catch (e: Exception) {
                        state = "Failed: ${e::class.simpleName} -> ${e.message}"
                    }
                }

                if (state == loading) {
                    state = "All done"
                }
            }
        }
    }
}

private fun <T> List<T>.duplicate(n: Int) = (0..<n).map { this }.flatten()
