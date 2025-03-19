package com.example.scrollosophy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.scrollosophy.components.QuoteScreen
import com.example.scrollosophy.data.QuoteRepository

class QuoteActivity : ComponentActivity() {
    private lateinit var quoteRepository: QuoteRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val quote = intent.getStringExtra("quote") ?: "No quote available"
        val author = intent.getStringExtra("author") ?: "Sorry"

        quoteRepository = QuoteRepository(applicationContext)

        setContent {
            QuoteScreen(quote, author, quoteRepository)
        }
    }
}
