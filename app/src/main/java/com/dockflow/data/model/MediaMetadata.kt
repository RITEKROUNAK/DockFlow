package com.dockflow.data.model

import android.graphics.Bitmap

/**
 * Represents the current state of media playback
 */
enum class PlaybackState {
    PLAYING,
    PAUSED,
    STOPPED,
    BUFFERING
}

/**
 * Data class containing all media metadata
 */
data class MediaMetadata(
    val title: String = "No music playing",
    val artist: String = "Unknown Artist",
    val album: String = "",
    val albumArtUri: String? = null,
    val albumArtBitmap: Bitmap? = null,
    val duration: Long = 0L, // in milliseconds
    val position: Long = 0L, // in milliseconds
    val playbackState: PlaybackState = PlaybackState.STOPPED,
    val isActive: Boolean = false
) {
    /**
     * Returns formatted duration string (mm:ss)
     */
    fun getFormattedDuration(): String {
        val minutes = (duration / 1000) / 60
        val seconds = (duration / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    /**
     * Returns formatted position string (mm:ss)
     */
    fun getFormattedPosition(): String {
        val minutes = (position / 1000) / 60
        val seconds = (position / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    /**
     * Returns progress as a float between 0 and 1
     */
    fun getProgress(): Float {
        return if (duration > 0) {
            (position.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
    }
}
