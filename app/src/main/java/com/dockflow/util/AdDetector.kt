package com.dockflow.util

import android.content.Context
import android.media.AudioManager
import android.util.Log

/**
 * Utility to detect and handle ads in music streaming apps
 */
object AdDetector {
    private const val TAG = "AdDetector"
    
    // Common patterns in Spotify ad titles/artists
    private val AD_PATTERNS = listOf(
        "spotify",
        "advertisement",
        "ad break",
        "sponsored",
        "premium",
        "upgrade"
    )
    
    // Store original volume to restore later
    private var originalVolume: Int = -1
    private var isMuted: Boolean = false
    
    /**
     * Check if the current track is likely an ad
     */
    fun isLikelyAd(title: String?, artist: String?): Boolean {
        val titleLower = title?.lowercase() ?: ""
        val artistLower = artist?.lowercase() ?: ""
        
        // Check for common ad patterns
        val hasAdPattern = AD_PATTERNS.any { pattern ->
            titleLower.contains(pattern) || artistLower.contains(pattern)
        }
        
        // Spotify ads often have "Spotify" as the artist
        val isSpotifyArtist = artistLower == "spotify"
        
        // Very short tracks (< 10 seconds) are often ad markers
        // This would need duration from metadata if available
        
        return hasAdPattern || isSpotifyArtist
    }
    
    /**
     * Mute the device volume
     */
    fun muteVolume(context: Context) {
        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            
            if (!isMuted) {
                // Store original volume
                originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                
                // Mute by setting volume to 0
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    0,
                    0 // No UI flags
                )
                
                isMuted = true
                Log.d(TAG, "Volume muted (original: $originalVolume)")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to mute volume", e)
        }
    }
    
    /**
     * Restore the original volume
     */
    fun unmuteVolume(context: Context) {
        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            
            if (isMuted && originalVolume >= 0) {
                // Restore original volume
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    originalVolume,
                    0 // No UI flags
                )
                
                isMuted = false
                Log.d(TAG, "Volume restored to: $originalVolume")
                originalVolume = -1
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to unmute volume", e)
        }
    }
    
    /**
     * Handle track change - mute if ad, unmute if regular track
     */
    fun handleTrackChange(context: Context, title: String?, artist: String?) {
        if (isLikelyAd(title, artist)) {
            Log.d(TAG, "Ad detected: $title by $artist")
            muteVolume(context)
        } else {
            Log.d(TAG, "Regular track: $title by $artist")
            unmuteVolume(context)
        }
    }
}
