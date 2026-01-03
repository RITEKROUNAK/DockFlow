package com.dockflow.util

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette

/**
 * Utility object for extracting colors from album art using Palette API
 */
object ColorExtractor {
    
    /**
     * Extract vibrant color from album art bitmap
     * Returns a default color if extraction fails
     */
    fun extractVibrantColor(bitmap: Bitmap?, defaultColor: Color = Color(0xFF00F5FF)): Color {
        if (bitmap == null) return defaultColor
        
        return try {
            val palette = Palette.from(bitmap).generate()
            val vibrantSwatch = palette.vibrantSwatch
            
            if (vibrantSwatch != null) {
                Color(vibrantSwatch.rgb)
            } else {
                // Fallback to dominant color
                val dominantSwatch = palette.dominantSwatch
                if (dominantSwatch != null) {
                    Color(dominantSwatch.rgb)
                } else {
                    defaultColor
                }
            }
        } catch (e: Exception) {
            defaultColor
        }
    }
    
    /**
     * Extract multiple colors for different UI elements
     */
    data class ExtractedColors(
        val vibrant: Color,
        val vibrantDark: Color,
        val vibrantLight: Color,
        val muted: Color
    )
    
    fun extractColors(bitmap: Bitmap?, defaultVibrant: Color = Color(0xFF00F5FF)): ExtractedColors {
        if (bitmap == null) {
            return ExtractedColors(
                vibrant = defaultVibrant,
                vibrantDark = defaultVibrant.copy(alpha = 0.8f),
                vibrantLight = defaultVibrant.copy(alpha = 0.6f),
                muted = Color.Gray
            )
        }
        
        return try {
            val palette = Palette.from(bitmap).generate()
            
            ExtractedColors(
                vibrant = palette.vibrantSwatch?.let { Color(it.rgb) } ?: defaultVibrant,
                vibrantDark = palette.darkVibrantSwatch?.let { Color(it.rgb) } 
                    ?: defaultVibrant.copy(alpha = 0.8f),
                vibrantLight = palette.lightVibrantSwatch?.let { Color(it.rgb) } 
                    ?: defaultVibrant.copy(alpha = 0.6f),
                muted = palette.mutedSwatch?.let { Color(it.rgb) } ?: Color.Gray
            )
        } catch (e: Exception) {
            ExtractedColors(
                vibrant = defaultVibrant,
                vibrantDark = defaultVibrant.copy(alpha = 0.8f),
                vibrantLight = defaultVibrant.copy(alpha = 0.6f),
                muted = Color.Gray
            )
        }
    }
}
