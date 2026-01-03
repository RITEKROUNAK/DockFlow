package com.dockflow.ui.components

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Battery indicator that shows percentage when not charging
 */
@Composable
fun BatteryIndicator(
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var batteryLevel by remember { mutableStateOf(getBatteryStatus(context).first) }
    var isCharging by remember { mutableStateOf(getBatteryStatus(context).second) }

    // Listen to battery changes in real-time
    DisposableEffect(Unit) {
        val batteryReceiver = object : android.content.BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val batteryStatus = getBatteryStatus(context!!)
                batteryLevel = batteryStatus.first
                isCharging = batteryStatus.second
            }
        }
        
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        }
        
        context.registerReceiver(batteryReceiver, filter)
        
        onDispose {
            context.unregisterReceiver(batteryReceiver)
        }
    }

    // Always show battery indicator
    if (batteryLevel > 0) {
        Box(
            modifier = modifier
                .size(70.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            // Circular battery indicator
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 4.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2
                
                // Background circle
                drawCircle(
                    color = Color.White.copy(alpha = 0.2f),
                    radius = radius,
                    style = Stroke(width = strokeWidth)
                )
                
                // Battery level arc - green when charging, color-coded otherwise
                val sweepAngle = (batteryLevel / 100f) * 360f
                val batteryColor = if (isCharging) {
                    Color(0xFF34C759) // Green when charging
                } else {
                    getBatteryColor(batteryLevel, accentColor)
                }
                
                drawArc(
                    color = batteryColor,
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth),
                    topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                    size = Size(size.width - strokeWidth, size.height - strokeWidth)
                )
            }
            
            // Battery percentage text - green when charging
            Text(
                text = "$batteryLevel",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = if (isCharging) Color(0xFF34C759) else Color.White
            )
        }
    }
}

/**
 * Get battery status (level and charging state)
 */
private fun getBatteryStatus(context: Context): Pair<Int, Boolean> {
    val batteryIntent = context.registerReceiver(
        null,
        IntentFilter(Intent.ACTION_BATTERY_CHANGED)
    )
    
    val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
    val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
    val batteryPct = if (level >= 0 && scale > 0) {
        (level * 100 / scale)
    } else {
        0
    }
    
    val status = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
    val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                     status == BatteryManager.BATTERY_STATUS_FULL
    
    return Pair(batteryPct, isCharging)
}

/**
 * Get battery color based on level
 */
private fun getBatteryColor(level: Int, accentColor: Color): Color {
    return when {
        level <= 20 -> Color(0xFFFF3B30) // Red for low battery
        level <= 50 -> Color(0xFFFF9500) // Orange for medium
        else -> accentColor // Accent color for good battery
    }
}
