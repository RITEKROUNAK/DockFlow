package com.dockflow.ui.components

import android.graphics.Bitmap
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.dockflow.ui.theme.NeonBackground
import com.dockflow.ui.theme.ThemeType

/**
 * Glassmorphic background with blurred album art
 * Uses Android 12+ blur with fallback for older versions
 */
@Composable
fun GlassmorphicBackground(
    albumArt: Bitmap?,
    themeType: ThemeType,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (albumArt != null) {
            // Check Android version for blur support
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Android 12+ - Use native blur
                Image(
                    bitmap = albumArt.asImageBitmap(),
                    contentDescription = "Album art background",
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(50.dp), // Native blur for Android 12+
                    contentScale = ContentScale.Crop
                )
            } else {
                // Older versions - Use image with overlay
                Image(
                    bitmap = albumArt.asImageBitmap(),
                    contentDescription = "Album art background",
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(25.dp), // Lighter blur for older devices
                    contentScale = ContentScale.Crop
                )
                
                // Extra overlay for older devices to simulate stronger blur
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                )
            }
            
            // Dark overlay for contrast (semi-transparent as requested)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.4f),
                                Color.Black.copy(alpha = 0.6f)
                            )
                        )
                    )
            )
        } else {
            // Fallback gradient when no music is playing
            val backgroundColor = when (themeType) {
                ThemeType.NEON -> NeonBackground
                ThemeType.MINIMAL -> Color(0xFFE8E8E8)
                ThemeType.ANALOG -> Color(0xFF2C1810)
            }
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                backgroundColor.copy(alpha = 0.8f),
                                backgroundColor
                            )
                        )
                    )
            )
        }
    }
}
