package com.example.app.service

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.app.MainActivity
import com.example.app.MusicApplication
import com.example.app.data.HistoryRepository
import com.example.app.model.HistoryRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PlaybackService: MediaSessionService() {
    private lateinit var player: ExoPlayer
    private var mediaSession: MediaSession? = null
    private lateinit var historyRepository: HistoryRepository
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    companion object {
        private const val TAG = "PlaybackService"
        const val ACTION_SHUTDOWN_SERVICE = "com.example.app.SHUTDOWN_SERVICE"
        const val EXTRA_SHOW_PLAYER = "com.example.app.SHOW_PLAYER"
    }

    override fun onCreate() {
        super.onCreate()
        player = ExoPlayer.Builder(this)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                 true
            )
            .build()
        val sessionActivityIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_NEW_TASK

            putExtra(EXTRA_SHOW_PLAYER, true)
        }
        val sessionActivityPendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                sessionActivityIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(sessionActivityPendingIntent)
            .build()

        val appContainer = (applicationContext as MusicApplication).container
        historyRepository = appContainer.historyRepository
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    @OptIn(UnstableApi::class)
    override fun onTaskRemoved(rootIntent: Intent?) {
        saveLastPlayed()
        stopSelf()
    }

    override fun onGetSession(contronlerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_SHUTDOWN_SERVICE -> {
                if (::player.isInitialized) {
                    val currentMediaId = player.currentMediaItem?.mediaId
                    val currentPosition = if (player.playbackState == Player.STATE_ENDED) 0L else player.currentPosition

                    if (currentMediaId != null) {
                        runBlocking {
                            try {
                                historyRepository.save(currentMediaId.toInt(), currentPosition.toInt())
                                Log.i(TAG, "Successfully saved last played state for $currentMediaId.")
                            } catch (e: Exception) {
                                Log.e(TAG, "Error saving last played state", e)
                            }
                        }
                    }
                }

                mediaSession?.run {
                    player.release()
                    release()
                    mediaSession = null
                }

                stopSelf()
                return START_NOT_STICKY
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun saveLastPlayed() {
        if (!::player.isInitialized) {
            return
        }

        val currentMediaId = player.currentMediaItem?.mediaId
        val currentPosition = if (player.playbackState == Player.STATE_ENDED) 0L else player.currentPosition

        if (currentMediaId != null) {
            serviceScope.launch {
                try {
                    Log.i(TAG, "Saving last played state to backend: songId=$currentMediaId, positionMs=$currentPosition")
                    historyRepository.save(currentMediaId.toInt(), currentPosition.toInt())
                    Log.w(TAG, "Actual API call to historyRepository.recordLastPlayedSong is commented out.")
                    Log.i(TAG, "Successfully queued/sent last played state for $currentMediaId.")
                } catch (e: Exception) {
                    Log.e(TAG, "Error saving last played state for $currentMediaId to backend", e)
                }
            }
        } else {
            Log.d(TAG, "saveLastPlayedStateToBackend: No current media item to save.")
        }
    }
}