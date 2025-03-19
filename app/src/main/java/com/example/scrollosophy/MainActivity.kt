package com.example.scrollosophy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.suspendCancellableCoroutine
import org.chromium.net.CronetEngine
import org.chromium.net.CronetException
import org.chromium.net.UrlRequest
import org.chromium.net.UrlResponseInfo
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class MainActivity : ComponentActivity() {
    private lateinit var cronetEngine: CronetEngine


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        cronetEngine = CronetEngine.Builder(applicationContext).build()

        setContent {
            var showSplash by remember { mutableStateOf(true) }
            var quote by remember { mutableStateOf<Quote?>(null) }

            LaunchedEffect(Unit) {
                try {
                    val fetchedQuote = fetchQuote(cronetEngine)
                    quote = fetchedQuote
                    navigateToQuoteScreen(fetchedQuote)
                    showSplash = false
                } catch (e: Exception) {
                    Log.e("ERROR", "Failed to fetch quote", e)
                }
            }

            if (showSplash) {
                SplashScreen()
            }
        }
    }

    private suspend fun fetchQuote(cronetEngine: CronetEngine): Quote {
        return suspendCancellableCoroutine { continuation ->
            val executor = Executors.newSingleThreadExecutor()

            val callback = object : UrlRequest.Callback() {
                private val responseBuffer = ByteBuffer.allocateDirect(102400) // 100 KB buffer
                private val responseString = StringBuilder()

                override fun onRedirectReceived(request: UrlRequest?, info: UrlResponseInfo?, newLocationUrl: String?) {
                    request?.followRedirect()
                }

                override fun onResponseStarted(request: UrlRequest?, info: UrlResponseInfo?) {
                    request?.read(responseBuffer)
                }

                override fun onReadCompleted(request: UrlRequest?, info: UrlResponseInfo?, byteBuffer: ByteBuffer?) {
                    byteBuffer?.flip()
                    val bytes = ByteArray(byteBuffer!!.remaining())
                    byteBuffer.get(bytes)
                    responseString.append(String(bytes))
                    byteBuffer.clear()
                    request?.read(byteBuffer)
                }

                override fun onSucceeded(request: UrlRequest?, info: UrlResponseInfo?) {
                    try {
                        val json = responseString.toString()
                        val jsonObject = org.json.JSONObject(json).getJSONObject("quote")
                        val quote = Quote(
                            jsonObject.getString("content"),
                            jsonObject.getJSONObject("author").getString("name")
                        )
                        continuation.resume(quote)
                    } catch (e: Exception) {
                        Log.e("Cronet", "JSON Parsing Error", e)
                        continuation.resumeWithException(e)
                    }
                }

                override fun onFailed(request: UrlRequest?, info: UrlResponseInfo?, error: CronetException?) {
                    Log.e("Cronet", "Request Failed", error)
                    continuation.resumeWithException(error ?: Exception("Unknown error"))
                }
            }

            val request = cronetEngine.newUrlRequestBuilder(
                "https://api.quotable.kurokeita.dev/api/quotes/random?maxLength=50",
                callback,
                executor
            ).build()

            request.start()
        }
    }

    private fun navigateToQuoteScreen(quote: Quote) {
        startActivity(Intent(this@MainActivity, QuoteActivity::class.java).apply {
            putExtra("quote", quote.content)
            putExtra("author", quote.author)
        })
        finish()
    }}

data class Quote(val content: String, val author: String)

@Composable
fun SplashScreen() {
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
                tint = Color.White,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Scrollosophy",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}