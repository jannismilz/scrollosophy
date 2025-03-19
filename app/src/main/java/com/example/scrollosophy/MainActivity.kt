package com.example.scrollosophy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.net.CronetProviderInstaller
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import org.chromium.net.CronetEngine
import org.chromium.net.CronetException
import org.chromium.net.UrlRequest
import org.chromium.net.UrlResponseInfo
import org.json.JSONObject
import java.nio.ByteBuffer
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {
    private val client = HttpClient(CIO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var showSplash by remember { mutableStateOf(true) }
            var quote by remember { mutableStateOf<Quote?>(null) }

            LaunchedEffect(Unit) {
                val fetchedQuote = fetchQuote()

                // Navigate after fetching
                quote = fetchedQuote
                navigateToQuoteScreen(fetchedQuote)
            }

            if (showSplash) {
                SplashScreen()
            }
        }
    }

    private suspend fun fetchQuote(): Quote {
        return try {
            val response: HttpResponse =
                client.get("https://api.quotable.kurokeita.dev/api/quotes/random?maxLength=50")
            val json = response.bodyAsText()
            Json.decodeFromString<List<Quote>>(json).first()
        } catch (e: Exception) {
            Log.e("QuoteFetchError", "Failed to fetch quote", e)
            Quote("Failed to load quote", "Unknown")
        }
    }

    private fun navigateToQuoteScreen(quote: Quote) {
        startActivity(Intent(this@MainActivity, QuoteActivity::class.java).apply {
            putExtra("quote", quote.content)
            putExtra("author", quote.author)
        })
        finish()
    }}

class QuoteRequestCallback : UrlRequest.Callback() {
    override fun onRedirectReceived(request: UrlRequest?, info: UrlResponseInfo?, newLocationUrl: String?) {
        request?.followRedirect()
    }

    override fun onResponseStarted(request: UrlRequest?, info: UrlResponseInfo?) {
        request?.read(ByteBuffer.allocateDirect(102400))
    }

    override fun onReadCompleted(request: UrlRequest?, info: UrlResponseInfo?, byteBuffer: ByteBuffer?) {
        byteBuffer?.flip() // Prepare buffer for reading
        val data = byteBuffer?.let { ByteArray(it.remaining()) }
        if (byteBuffer != null) {
            byteBuffer.get(data)
        }

        if(data == null) {
            return
        }

        val jsonResponse = JSONObject(String(data))
        Log.i("Response", jsonResponse.toString())

        // Do something with the parsed JSON
    }

    override fun onFailed(request: UrlRequest?, info: UrlResponseInfo?, error: CronetException?) {
        Log.e("MyUrlRequestCallback", "Request failed", error)
    }
    override fun onSucceeded(request: UrlRequest?, info: UrlResponseInfo?) {}

    override fun onCanceled(request: UrlRequest?, info: UrlResponseInfo?) {}
}

data class Quote(val content: String, val author: String)

@Composable
fun SplashScreen() {
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(1f, animationSpec = tween(durationMillis = 1000))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF8ED67C)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Outlined.Lightbulb,
                contentDescription = "Icon",
                tint = Color.White.copy(alpha = alpha.value),
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Scrollosophy",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = alpha.value)
            )
        }
    }
}

@Composable
fun MainScreen() {
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(1f, animationSpec = tween(durationMillis = 1000))
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Main Screen",
            fontSize = 24.sp
        )
    }
}