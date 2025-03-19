package com.example.scrollosophy.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SplashScreen() {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color(0xFF8ED67C)),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Outlined.Lightbulb,
                contentDescription = "Icon",
                tint = Color.White,
                modifier = Modifier.size(80.dp),
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Scrollosophy",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }
    }
}
