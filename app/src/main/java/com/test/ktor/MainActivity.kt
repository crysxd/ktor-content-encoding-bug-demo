package com.test.ktor

import android.health.connect.datatypes.StepsRecord
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.test.ktor.ui.theme.MyApplicationTheme
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.seconds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var sent by remember { mutableStateOf(false) }
            var received by remember { mutableStateOf(false) }
            var completed by remember { mutableStateOf(false) }
            var failed by remember { mutableStateOf(false) }
            var error by remember { mutableStateOf("") }

            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(
                            10.dp,
                            Alignment.CenterVertically
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        Step("Send request", completed = sent)
                        Step("Receive response", completed = received)
                        Step("Complete request", completed = completed, failed = failed)
                        Text(text = error, textAlign = TextAlign.Center, modifier = Modifier.padding(10.dp))
                    }
                }
            }

            LaunchedEffect(Unit) {
                KtorBugSample.reproduceBug(
                    onSent = { sent = true },
                    onReceived = { received = true },
                    onCompleted = { e ->
                        completed = true
                        failed = e != null
                        error = e?.let { "${it.javaClass.simpleName}: ${it.message}" } ?: ""
                    }
                )
            }
        }
    }
}

@Composable
fun Step(
    text: String,
    completed: Boolean,
    failed: Boolean = false,
) = Text(
    text = text,
    modifier = Modifier
        .background(
            when {
                !completed -> Color.LightGray
                completed && failed -> Color.Red
                else -> Color.Green
            }
        )
        .padding(10.dp),
    textAlign = TextAlign.Center
)