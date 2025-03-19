package com.example.scrollosophy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

class QuoteActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val quote = intent.getStringExtra("quote") ?: "No quote available"
        val author = intent.getStringExtra("author") ?: "Unknown"

        setContent {
            QuoteScreen(quote, author)
        }
    }
}

@Composable
fun QuoteScreen(quote: String, author: String) {
    val pastelColor = remember { generatePastelColor() } // Generate once per recomposition

    Box(
        modifier = Modifier.fillMaxSize().background(pastelColor),
        contentAlignment = Alignment.Center
    ) {
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
                    text = "\"$quote\"",
                    fontSize = 24.sp,
                    fontStyle = FontStyle.Italic,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "~ $author",
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
}

private fun generatePastelColor(): Color {
    val random = Random(System.currentTimeMillis())
    val red = (160..220).random(random)
    val green = (160..220).random(random)
    val blue = (160..220).random(random)

    return Color(red, green, blue)
}