package com.dockflow.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

/**
 * Clock section with date display, color harmony, and pixel shifting for burn-in protection
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ClockSection(
    onLongPress: () -> Unit,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf(getCurrentTime()) }
    var currentDate by remember { mutableStateOf(getCurrentDate()) }
    
    // Pixel shifting for OLED burn-in protection
    var pixelShiftX by remember { mutableStateOf(0.dp) }
    var pixelShiftY by remember { mutableStateOf(0.dp) }

    // Update time every second
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTime = getCurrentTime()
            currentDate = getCurrentDate()
        }
    }
    
    // Pixel shift every 5 minutes to prevent burn-in
    LaunchedEffect(Unit) {
        while (true) {
            delay(300000) // 5 minutes
            // Shift by -2 to +2 pixels randomly
            pixelShiftX = Random.nextInt(-2, 3).dp
            pixelShiftY = Random.nextInt(-2, 3).dp
        }
    }
    
    // Create tinted white color from accent (90% white + 10% accent)
    val tintedWhite = remember(accentColor) {
        Color(
            red = 0.9f + accentColor.red * 0.1f,
            green = 0.9f + accentColor.green * 0.1f,
            blue = 0.9f + accentColor.blue * 0.1f,
            alpha = 1f
        )
    }

    // Minimal layout without card container - non-clickable
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .offset(x = pixelShiftX, y = pixelShiftY),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Digital Clock with color harmony
        Text(
            text = currentTime,
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 88.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            ),
            color = tintedWhite,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Date with subtle tint
        Text(
            text = currentDate,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            ),
            color = tintedWhite.copy(alpha = 0.9f),
            maxLines = 1
        )
    }
}

/**
 * Get current time in HH:mm format
 */
private fun getCurrentTime(): String {
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    return formatter.format(Date())
}

/**
 * Get current date in "EEEE, MMMM dd" format
 */
private fun getCurrentDate(): String {
    val formatter = SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault())
    return formatter.format(Date())
}
