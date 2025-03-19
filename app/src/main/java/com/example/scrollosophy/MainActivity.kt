package com.example.scrollosophy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.scrollosophy.components.SplashScreen
import com.example.scrollosophy.data.Quote
import com.example.scrollosophy.data.QuoteRepository

class MainActivity : ComponentActivity() {
    private lateinit var quoteRepository: QuoteRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        quoteRepository = QuoteRepository(applicationContext)

        setContent {
            var showSplash by remember { mutableStateOf(true) }
            var quote by remember { mutableStateOf<Quote?>(null) }

            LaunchedEffect(Unit) {
                try {
                    val fetchedQuote = quoteRepository.fetchQuote()
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

    private fun navigateToQuoteScreen(quote: Quote) {
        startActivity(
            Intent(this@MainActivity, QuoteActivity::class.java).apply {
                putExtra("quote", quote.content)
                putExtra("author", quote.author)
            },
        )
        finish()
    }
}
