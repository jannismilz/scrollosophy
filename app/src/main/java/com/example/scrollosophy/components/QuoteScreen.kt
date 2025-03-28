package com.example.scrollosophy.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.example.scrollosophy.data.Quote
import com.example.scrollosophy.data.QuoteRepository
import java.nio.ByteBuffer
import java.security.MessageDigest
import kotlin.random.Random

@Composable
fun QuoteScreen(
    quote: String,
    author: String,
    quoteRepository: QuoteRepository,
) {
    val quotes = remember { mutableStateListOf(Quote(quote, author)) }
    val listState = rememberLazyListState()

    val haptic = LocalHapticFeedback.current
    var lastScrolledIndex by remember { mutableStateOf(0) }

    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(listState.firstVisibleItemIndex) {
        val currentItem = listState.firstVisibleItemIndex

        if (currentItem == quotes.size - 1 && !isLoading) {
            isLoading = true
            try {
                val fetchedQuote = quoteRepository.fetchQuote()
                quotes.add(fetchedQuote)
            } catch (e: Exception) {
                Log.e("ERROR", "Failed to fetch quote", e)
            } finally {
                isLoading = false
            }
        }

        if (currentItem != lastScrolledIndex) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            lastScrolledIndex = currentItem
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize(),
        flingBehavior = rememberSnapFlingBehavior(lazyListState = listState),
    ) {
        itemsIndexed(quotes) { index, quote ->
            val pastelColor = generatePastelColorFromQuote(quote.content)

            Box(
                modifier = Modifier
                    .fillParentMaxSize()
                    .background(pastelColor),
            ) {
                QuoteItem(quote)
            }
        }
    }
}

// ChatGPT
private fun generatePastelColorFromQuote(quote: String): Color {
    val hash = MessageDigest.getInstance("SHA-256").digest(quote.toByteArray())

    val longValue = ByteBuffer.wrap(hash.copyOfRange(0, 8)).long
    val random = Random(longValue)

    val red = (160..240).random(random)
    val green = (160..240).random(random)
    val blue = (160..240).random(random)

    return Color(red, green, blue)
}
