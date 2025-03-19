package com.example.scrollosophy

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.suspendCancellableCoroutine
import org.chromium.net.CronetEngine
import org.chromium.net.CronetException
import org.chromium.net.UrlRequest
import org.chromium.net.UrlResponseInfo
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.random.Random

class QuoteActivity : ComponentActivity() {
    private lateinit var cronetEngine: CronetEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val quote = intent.getStringExtra("quote") ?: "No quote available"
        val author = intent.getStringExtra("author") ?: "Unknown"

        cronetEngine = CronetEngine.Builder(applicationContext).build()

        setContent {
            QuoteScreen(quote, author, cronetEngine)
        }
    }
}

@Composable
fun QuoteScreen(quote: String, author: String, cronetEngine: CronetEngine) {
    val quotes = remember { mutableStateListOf(Quote(quote, author)) }
    val listState = rememberLazyListState()

    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(listState.firstVisibleItemIndex) {
        if (listState.firstVisibleItemIndex == quotes.size - 1 && !isLoading) {
            isLoading = true
            try {
                val fetchedQuote = fetchQuote(cronetEngine)
                quotes.add(fetchedQuote) // Add the new quote to the list
            } catch (e: Exception) {
                Log.e("ERROR", "Failed to fetch quote", e)
            } finally {
                isLoading = false
            }
        }
    }

    LazyColumn (
        state = listState,
        modifier = Modifier
            .fillMaxSize(),
        flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    ) {
        itemsIndexed(quotes) { index, quote ->
            val pastelColor = generatePastelColorFromQuote(quote.content)

            Box(
                modifier = Modifier
                    .fillParentMaxSize()
                    .background(pastelColor)
                    .padding(16.dp)
            ) {
                QuoteItem(quote)
            }
        }
    }
}

@Composable
fun QuoteItem(quote: Quote) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp).fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "")
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "\"${quote.content}\"",
                fontSize = 24.sp,
                fontStyle = FontStyle.Italic,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "~ ${quote.author}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                fontStyle = FontStyle.Italic
            )
        }
        Icon(
            imageVector = Icons.Outlined.ArrowDropDown,
            contentDescription = "Scroll to Next Icon",
            tint = Color.White,
            modifier = Modifier.size(64.dp).height(64.dp)
        )
    }
}

// ChatGPT
private fun generatePastelColorFromQuote(quote: String): Color {
    val hash = MessageDigest.getInstance("SHA-256").digest(quote.toByteArray())

    val longValue = ByteBuffer.wrap(hash.copyOfRange(0, 8)).long
    val random = Random(longValue)

    val red = (160..220).random(random)
    val green = (160..220).random(random)
    val blue = (160..220).random(random)

    return Color(red, green, blue)
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