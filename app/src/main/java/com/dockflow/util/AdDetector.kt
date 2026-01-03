package com.dockflow.util

import android.content.Context
import android.media.AudioManager
import android.util.Log

object AdDetector {
    private const val TAG = "AdDetector"
    
    private val AD_PATTERNS = listOf(
        "spotify",
        "advertisement",
        "ad break",
        "sponsored",
        "premium",
        "upgrade"
    )
    
    private var originalVolume: Int = -1
    private var isMuted: Boolean = false
    
    fun isLikelyAd(title: String?, artist: String?): Boolean {
        val titleLower = title?.lowercase() ?: ""
        val artistLower = artist?.lowercase() ?: ""
        
        val hasAdPattern = AD_PATTERNS.any { pattern ->
            titleLower.contains(pattern) || artistLower.contains(pattern)
        }
        
        val isSpotifyArtist = artistLower == "spotify"
        
        return hasAdPattern || isSpotifyArtist
    }
    
    fun muteVolume(context: Context) {
        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            
            if (!isMuted) {
                originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
                isMuted = true
                Log.d(TAG, "Volume muted (original: $originalVolume)")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to mute volume", e)
        }
    }
    
    fun unmuteVolume(context: Context) {
        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            
            if (isMuted && originalVolume >= 0) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0)
                isMuted = false
                Log.d(TAG, "Volume restored to: $originalVolume")
                originalVolume = -1
            } else if (isMuted) {
                // If we're muted but don't have original volume, reset flag
                Log.w(TAG, "Muted state without original volume, resetting")
                isMuted = false
                originalVolume = -1
            } else {
                Log.d(TAG, "Unmute called but already unmuted (isMuted=$isMuted, originalVolume=$originalVolume)")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to unmute volume", e)
        }
    }
    
    fun handleTrackChange(context: Context, title: String?, artist: String?) {
        Log.d(TAG, "Track change - Title: '$title', Artist: '$artist', isMuted: $isMuted")
        if (isLikelyAd(title, artist)) {
            Log.d(TAG, "Ad detected: $title by $artist - Muting")
            muteVolume(context)
        } else {
            Log.d(TAG, "Regular track: $title by $artist - Unmuting")
            unmuteVolume(context)
        }
    }
}
