package com.example.scrollosophy.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.suspendCancellableCoroutine
import org.chromium.net.CronetEngine
import org.chromium.net.CronetException
import org.chromium.net.UrlRequest
import org.chromium.net.UrlResponseInfo
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class QuoteRepository(context: Context) {
    private val cronetEngine = CronetEngine.Builder(context).build()

    suspend fun fetchQuote(): Quote {
        return suspendCancellableCoroutine { continuation ->
            val executor = Executors.newSingleThreadExecutor()
            val callback =
                object : UrlRequest.Callback() {
                    private val responseBuffer = ByteBuffer.allocateDirect(102400) // 100 KB buffer
                    private val responseString = StringBuilder()

                    // ChatGPT
                    override fun onResponseStarted(
                        request: UrlRequest?,
                        info: UrlResponseInfo?,
                    ) {
                        request?.read(responseBuffer)
                    }

                    // ChatGPT
                    override fun onReadCompleted(
                        request: UrlRequest?,
                        info: UrlResponseInfo?,
                        byteBuffer: ByteBuffer?,
                    ) {
                        byteBuffer?.flip()
                        val bytes = ByteArray(byteBuffer!!.remaining())
                        byteBuffer.get(bytes)
                        responseString.append(String(bytes))
                        byteBuffer.clear()
                        request?.read(byteBuffer)
                    }

                    override fun onSucceeded(
                        request: UrlRequest?,
                        info: UrlResponseInfo?,
                    ) {
                        try {
                            val json = responseString.toString()
                            val jsonObject = org.json.JSONObject(json).getJSONObject("quote")
                            val quote =
                                Quote(
                                    jsonObject.getString("content"),
                                    jsonObject.getJSONObject("author").getString("name"),
                                )
                            continuation.resume(quote)
                        } catch (e: Exception) {
                            Log.e("QuoteRepository", "JSON Parsing Error", e)
                            continuation.resumeWithException(e)
                        }
                    }

                    override fun onFailed(
                        request: UrlRequest?,
                        info: UrlResponseInfo?,
                        error: CronetException?,
                    ) {
                        Log.e("QuoteRepository", "Request Failed", error)
                        continuation.resumeWithException(error ?: Exception("Unknown error"))
                    }

                    override fun onRedirectReceived(
                        request: UrlRequest?,
                        info: UrlResponseInfo?,
                        newLocationUrl: String?,
                    ) {}
                }

            val request = cronetEngine.newUrlRequestBuilder(
                "https://api.quotable.kurokeita.dev/api/quotes/random?maxLength=50",
                callback,
                executor,
            ).build()

            request.start()
        }
    }
}
