package com.dockflow.service

import android.content.ComponentName
import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.dockflow.data.model.PlaybackState as AppPlaybackState
import com.dockflow.data.model.MediaMetadata as AppMediaMetadata
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MediaSessionService : NotificationListenerService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var mediaSessionManager: MediaSessionManager? = null
    private var activeController: MediaController? = null

    private val callback = object : MediaController.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadata?) {
            updateMediaMetadata()
        }

        override fun onPlaybackStateChanged(state: PlaybackState?) {
            updateMediaMetadata()
        }
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(TAG, "NotificationListener connected")
        updateActiveController()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        updateActiveController()
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        updateActiveController()
    }

    private fun startMonitoring() {
        serviceScope.launch {
            while (isActive) {
                updateActiveController()
                updateMediaMetadata()
                delay(1000)
            }
        }
    }

    private fun updateActiveController() {
        try {
            val controllers = mediaSessionManager?.getActiveSessions(
                ComponentName(this, MediaSessionService::class.java)
            ) ?: emptyList()

            val newController = controllers.firstOrNull { controller ->
                controller.playbackState?.state == PlaybackState.STATE_PLAYING
            } ?: controllers.firstOrNull()

            if (newController != activeController) {
                activeController?.unregisterCallback(callback)
                activeController = newController
                activeController?.registerCallback(callback)
                updateMediaMetadata()
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Security exception accessing media sessions", e)
        }
    }

    private fun updateMediaMetadata() {
        val controller = activeController ?: run {
            _mediaMetadata.value = AppMediaMetadata()
            return
        }

        val metadata = controller.metadata
        val playbackState = controller.playbackState
        
        val title = metadata?.getString(MediaMetadata.METADATA_KEY_TITLE) ?: "Unknown Track"
        val artist = metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST) ?: "Unknown Artist"
        val album = metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM) ?: ""
        val duration = metadata?.getLong(MediaMetadata.METADATA_KEY_DURATION) ?: 0L
        val position = playbackState?.position ?: 0L
        
        com.dockflow.util.AdDetector.handleTrackChange(this, title, artist)
        
        val albumArtBitmap = try {
            metadata?.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
                ?: metadata?.getBitmap(MediaMetadata.METADATA_KEY_ART)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading album art", e)
            null
        }
        
        val appState = when (playbackState?.state) {
            PlaybackState.STATE_PLAYING -> AppPlaybackState.PLAYING
            PlaybackState.STATE_PAUSED -> AppPlaybackState.PAUSED
            PlaybackState.STATE_BUFFERING -> AppPlaybackState.BUFFERING
            else -> AppPlaybackState.STOPPED
        }
        
        _mediaMetadata.value = AppMediaMetadata(
            title = title,
            artist = artist,
            album = album,
            albumArtBitmap = albumArtBitmap,
            duration = duration,
            position = position,
            playbackState = appState,
            isActive = true
        )
    }

    companion object {
        private const val TAG = "MediaSessionService"
        
        private val _mediaMetadata = MutableStateFlow(AppMediaMetadata())
        val mediaMetadata: StateFlow<AppMediaMetadata> = _mediaMetadata.asStateFlow()
        
        private var serviceInstance: MediaSessionService? = null

        fun togglePlayPause() {
            serviceInstance?.activeController?.let { controller ->
                if (controller.playbackState?.state == PlaybackState.STATE_PLAYING) {
                    controller.transportControls.pause()
                } else {
                    controller.transportControls.play()
                }
            }
        }

        fun skipToNext() {
            serviceInstance?.activeController?.transportControls?.skipToNext()
        }

        fun skipToPrevious() {
            serviceInstance?.activeController?.transportControls?.skipToPrevious()
        }
        
        fun seekTo(position: Float) {
            serviceInstance?.activeController?.let { controller ->
                val duration = controller.metadata?.getLong(MediaMetadata.METADATA_KEY_DURATION) ?: 0L
                if (duration > 0) {
                    controller.transportControls.seekTo((duration * position).toLong())
                }
            }
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        serviceInstance = this
        Log.d(TAG, "MediaSessionService created")
        
        mediaSessionManager = getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        startMonitoring()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        serviceInstance = null
        activeController?.unregisterCallback(callback)
        serviceScope.cancel()
    }
}
