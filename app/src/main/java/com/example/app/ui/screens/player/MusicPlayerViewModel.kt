package com.example.app.ui.screens.player

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.app.MusicApplication
import com.example.app.data.FavoriteRepository
import com.example.app.data.PlaylistRepository
import com.example.app.data.SongRepository
import com.example.app.model.PlayList
import com.example.app.model.Song
import com.example.app.service.PlaybackService
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

data class MusicPlayerUiState(
    val playlist: List<Song> = emptyList(),
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isShuffleEnabled: Boolean = false,
    @Player.RepeatMode val repeatMode: Int = Player.REPEAT_MODE_OFF,
    val playlists: List<PlayList> = emptyList()
) {

    val currentTimeFormatted: String
        get() = formatDuration(currentPositionMs)
    val totalTimeFormatted: String
        get() = formatDuration(durationMs)

    val progress: Float
        get() = if (durationMs > 0) currentPositionMs.toFloat() / durationMs else 0f

    @SuppressLint("DefaultLocale")
    private fun formatDuration(ms: Long): String {
        val totalSeconds = TimeUnit.MILLISECONDS.toSeconds(ms).coerceAtLeast(0)
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }
}

class MusicPlayerViewModel(
    private val application: Application,
    private val songRepository: SongRepository,
    private val playlistRepository: PlaylistRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MusicPlayerUiState())
    val uiState: StateFlow<MusicPlayerUiState> = _uiState.asStateFlow()

    private var progressUpdateJob: Job? = null
    private var mediaController: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var isServiceStarted = false

    init {
        initializeMediaController()
        viewModelScope.launch {
            val res = playlistRepository.findAll();
            if (res.isSuccessful) {
                _uiState.value = _uiState.value.copy(playlists = res.body()?.data ?: emptyList())
            }
        }
    }

    private fun initializeMediaController() {
        val intent = Intent(application, PlaybackService::class.java)
        application.startService(intent)

        val sessionToken = SessionToken(application, ComponentName(application, PlaybackService::class.java))

        controllerFuture = MediaController.Builder(application,sessionToken).buildAsync()
        controllerFuture?.addListener({
            mediaController = controllerFuture?.get()
            mediaController?.let { controller ->
                setupPlayerListener(controller)
                _uiState.update {
                    it.copy(
                        isPlaying = controller.isPlaying,
                        isShuffleEnabled = controller.shuffleModeEnabled,
                        repeatMode = controller.repeatMode,
                        durationMs = controller.duration.coerceAtLeast(0),
                        isLoading = controller.playbackState == Player.STATE_BUFFERING
                    )
                }
                if (controller.isPlaying) {
                    startProgressUpdates()
                }
            }

        }, MoreExecutors.directExecutor())
    }

    private fun ensureServiceStarted() {
        if (!isServiceStarted) {
            Log.d("MusicPlayerViewModel", "Service not started by this VM instance. Starting now.")
            val intent = Intent(application.applicationContext, PlaybackService::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(application.applicationContext, intent)
            } else {
                application.applicationContext.startService(intent)
            }
            isServiceStarted = true
        } else {
            Log.d("MusicPlayerViewModel", "Service already marked as started by this VM instance.")
        }
    }

    private fun setupPlayerListener(player: Player) {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlayingUpdate: Boolean) {
                Log.d("MusicPlayerVM_Listener", "[CALLBACK] onIsPlayingChanged: isPlaying = $isPlayingUpdate")
                _uiState.update { it.copy(isPlaying = isPlayingUpdate) }
                if (isPlayingUpdate) {
                    Log.d("MusicPlayerVM_Listener", "   -> Starting progress updates.")
                    startProgressUpdates()
                } else {
                    Log.d("MusicPlayerVM_Listener", "   -> Stopping progress updates.")
                    stopProgressUpdates()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                val isLoading = playbackState == Player.STATE_BUFFERING || playbackState == Player.STATE_IDLE
                _uiState.update { it.copy(isLoading = isLoading) }

                if (playbackState == Player.STATE_READY || playbackState == Player.STATE_ENDED) {
                    Log.d("MusicPlayerVM_Listener", "   -> State is Ready or Ended. Duration =  ms")
                    _uiState.update { it.copy(durationMs = player.duration.coerceAtLeast(0)) }
                }
                if (playbackState == Player.STATE_ENDED) {
                    stopProgressUpdates()
                    _uiState.update { it.copy(currentPositionMs = 0, isPlaying = false) }
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {

                val newPlayingId = mediaItem?.mediaId?.toIntOrNull()
                val newSong = _uiState.value.playlist.find { it.id == newPlayingId }
                _uiState.update {
                    it.copy(
                        currentSong = newSong,
                        currentPositionMs = 0L,
                        durationMs = player.duration.coerceAtLeast(0),
                        isLoading = false
                    )
                }
                if (_uiState.value.isPlaying) {
                    startProgressUpdates()
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                Log.e("MusicPlayerViewModel", "Playback error: ${error.errorCodeName} - ${error.message}", error)
                val errorMessage = when (error.cause) {
                    is UnknownHostException -> "Network error. Please check your connection."

                    else -> "Playback error: ${error.message}"
                }
                _uiState.update { it.copy(error = errorMessage, isLoading = false, isPlaying = false) }
                stopProgressUpdates()
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                Log.d("MusicPlayerVM_Listener", "[CALLBACK] onShuffleModeEnabledChanged: $shuffleModeEnabled")
                _uiState.update { it.copy(isShuffleEnabled = shuffleModeEnabled) }
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                Log.d("MusicPlayerVM_Listener", "[CALLBACK] onRepeatModeChanged: $repeatMode")
                _uiState.update { it.copy(repeatMode = repeatMode) }
            }

            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {

            }
        })
    }

    fun toggleShuffle() {
        val currentShuffleState = _uiState.value.isShuffleEnabled
        val newShuffleState = !currentShuffleState
        mediaController?.shuffleModeEnabled = newShuffleState
        Log.d("MusicPlayerVM", "Toggled shuffle mode to: $newShuffleState")
    }

    fun cycleRepeatMode() {
        val currentMode = _uiState.value.repeatMode
        val nextMode = when (currentMode) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_OFF
            else -> Player.REPEAT_MODE_OFF
        }
        mediaController?.repeatMode = nextMode
        Log.d("MusicPlayerVM", "Cycled repeat mode to: $nextMode")
    }

    fun addSongToPlaylist(playlistId: Int, songId: Int) {
        viewModelScope.launch {
            playlistRepository.addSong(playlistId, songId)
        }
    }

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            playlistRepository.createPlaylist(name)
            val res = playlistRepository.findAll();
            if (res.isSuccessful) {
                _uiState.value = _uiState.value.copy(playlists = res.body()?.data ?: emptyList())
            }
        }
    }

    fun favoriteChange(songId: Int, isFavorite: Boolean) {
        if (isFavorite) {
            viewModelScope.launch {
                favoriteRepository.deleteSong(songId)
            }
        } else {
            viewModelScope.launch {
                favoriteRepository.addSong(songId)
            }
        }
    }

    fun loadPlaylist(songs: List<Song>, startShuffle: Boolean = false) {
        ensureServiceStarted()
        if (songs.isEmpty()) {
            _uiState.update { it.copy(isLoading = false, error = "Playlist is empty") }
            return
        }
        _uiState.update { it.copy(playlist = songs, isLoading = true, error = null, isShuffleEnabled = startShuffle) }
        val mediaItems = songs.map { song ->
            song.toMediaItem()
        }
        mediaController?.shuffleModeEnabled = startShuffle
        mediaController?.setMediaItems(mediaItems, /* resetPosition= */ true)
        mediaController?.prepare()
        mediaController?.play()
    }

    private fun Song.toMediaItem(): MediaItem {
        return MediaItem.Builder()
            .setUri(Uri.parse(fileUrl))
            .setMediaId(id.toString())
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(artistName)
                    .setAlbumTitle(albumName)
                    .setArtworkUri(Uri.parse(coverUrl))
                    .build()
            )
            .build()
    }

    private suspend fun loadSong(id: Int): Song? {
        val response = songRepository.detail(id)

        if (response.isSuccessful) {
            _uiState.update { it.copy(isLoading = false) }
            val song = response.body()?.data
            if (song != null) {
                return song
            } else {
                _uiState.update { it.copy(error = "Song not found") }
            }
        } else {
            _uiState.update { it.copy(error = "Failed to load song") }
        }
        return null;
    }

    suspend fun playSongImmediately(id: Int) {
        ensureServiceStarted()
        val song = loadSong(id)
        val songMediaId = song?.id.toString()
        var foundIndex = -1

        for (i in 0 until mediaController!!.mediaItemCount) {
            if (mediaController!!.getMediaItemAt(i).mediaId == songMediaId) {
                foundIndex = i
                break
            }
        }

        if (foundIndex != -1) {

            if (mediaController?.currentMediaItemIndex == foundIndex && mediaController!!.isPlaying) {
                Log.d("MusicPlayerVM", "Song is already the current playing item.")
            } else {
                mediaController?.seekTo(foundIndex, 0)
                mediaController?.playWhenReady = true
                if (mediaController?.playbackState == Player.STATE_IDLE) mediaController?.prepare()
                mediaController?.play()
            }
        } else {
            Log.d(
                "MusicPlayerVM",
                "Song not found in current queue. Setting as new playlist."
            )
            val newMediaItem = song?.toMediaItem()
            _uiState.update { it.copy(playlist = listOf(song!!)) }
            newMediaItem?.let { mediaController?.setMediaItem(it) }
            mediaController?.playWhenReady = true
            Log.d("MusicPlayerVM", "Calling prepare() for new playlist.")
            mediaController?.prepare()
            Log.d("MusicPlayerVM", "Called prepare() for new playlist.")
            mediaController?.play()
        }

        _uiState.update { it.copy(currentSong = song) }
        // Xóa lỗi nếu có
        if (_uiState.value.error != null) _uiState.update { it.copy(error = null) }

    }

    suspend fun playSongNext(id: Int) {
        val currentMediaItemIndex = mediaController?.currentMediaItemIndex
        val mediaItemCount = mediaController?.mediaItemCount
        val songMediaId = id.toString()

        if (currentMediaItemIndex == -1 || mediaItemCount == 0) {
            Log.d("MusicPlayerVM", "PlayNext: No current item or empty queue, playing immediately instead.")
            playSongImmediately(id = id)
            return
        }

        Log.d("MusicPlayerVM", "PlayNext: Processing song ID $id. Current index: $currentMediaItemIndex, Count: $mediaItemCount")
        _uiState.update { it.copy(isLoading = true, error = null) }

        val song = this.loadSong(id = id)
        if (song == null) {
            Log.e("MusicPlayerVM", "PlayNext: Failed to load song $id.")
            _uiState.update { it.copy(isLoading = false) }
            return
        }

        val insertIndex = currentMediaItemIndex?.plus(1)

        var existingIndex = -1
        for (i in 0 until mediaItemCount!!) {
            if (mediaController?.getMediaItemAt(i)?.mediaId == songMediaId) {
                existingIndex = i
                break
            }
        }

        try {
            if (existingIndex != -1) {
                Log.d("MusicPlayerVM", "PlayNext: Song $id found at index $existingIndex.")

                when (existingIndex) {
                    insertIndex -> {
                        Log.d("MusicPlayerVM", "PlayNext: Song $id is already the next item.")
                        Toast.makeText(application, "${song.title} is already next", Toast.LENGTH_SHORT).show()
                    }
                    currentMediaItemIndex -> {
                        Log.d("MusicPlayerVM", "PlayNext: Cannot move the currently playing song ($id) to be next of itself.")
                        Toast.makeText(application, "Cannot play next the current song", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Log.d("MusicPlayerVM", "PlayNext: Moving song $id from index $existingIndex to $insertIndex.")
                        if (insertIndex != null) {
                            mediaController?.moveMediaItem(existingIndex, insertIndex)
                        }

                        _uiState.update { currentState ->
                            val currentPlaylist = currentState.playlist.filterNotNull().toMutableList()
                            val itemToMove = currentPlaylist.find { it.id == id }
                            if (itemToMove != null) {
                                val actualExistingIndex = currentPlaylist.indexOfFirst{ it.id == id}
                                if(actualExistingIndex != -1) {
                                    currentPlaylist.removeAt(actualExistingIndex)
                                    val actualInsertIndex = insertIndex?.coerceAtMost(currentPlaylist.size)
                                    if (actualInsertIndex != null) {
                                        currentPlaylist.add(actualInsertIndex, itemToMove)
                                    }
                                }
                            }
                            currentState.copy(playlist = currentPlaylist)
                        }
                        Toast.makeText(application, "Moved ${song.title} to play next", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Log.d("MusicPlayerVM", "PlayNext: Song $id not found in queue. Adding at index $insertIndex.")
                val newMediaItem = song.toMediaItem()

                mediaController?.addMediaItem(insertIndex!!, newMediaItem)

                _uiState.update { currentState ->
                    val currentPlaylist = currentState.playlist.toMutableList()
                    val actualInsertIndex = insertIndex?.coerceAtMost(currentPlaylist.size)
                    if (actualInsertIndex != null) {
                        currentPlaylist.add(actualInsertIndex, song)
                    }
                    currentState.copy(playlist = currentPlaylist)
                }
                Toast.makeText(application, "Added ${song.title} to play next", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("MusicPlayerVM", "Error during playSongNext execution for $id", e)
            _uiState.update { it.copy(error = "An error occurred while modifying the queue.") }
        } finally {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun playPause() {
        if (_uiState.value.currentSong == null && _uiState.value.playlist.isNotEmpty()) {
            mediaController?.seekToDefaultPosition(0)
            mediaController?.play()
        } else if (mediaController?.isPlaying!!) {
            mediaController?.pause()
        } else {
            if (mediaController?.playbackState == Player.STATE_ENDED) {
                mediaController?.seekTo(0)
            }
                mediaController?.play()
        }
        if (_uiState.value.error != null) {
            _uiState.update { it.copy(error = null) }
        }
    }

    fun seekToNext() {
        if (mediaController!!.hasNextMediaItem()) {
            mediaController?.seekToNextMediaItem()
            if (!mediaController!!.isPlaying) mediaController?.play()
        }
    }

    fun seekToPrevious() {
        if (mediaController!!.hasPreviousMediaItem()) {
            mediaController?.seekToPreviousMediaItem()
            if (!mediaController!!.isPlaying) mediaController?.play()
        } else {
            mediaController?.seekTo(0)
            if (!mediaController!!.isPlaying) mediaController?.play()
        }
    }

    fun seekToPosition(positionMs: Long) {
        stopProgressUpdates()
        val targetPosition = positionMs.coerceIn(0, mediaController?.duration)
        _uiState.update { it.copy(currentPositionMs = targetPosition) }
        mediaController?.seekTo(targetPosition)
        if (_uiState.value.isPlaying) {
            startProgressUpdates()
        }
    }

    private fun startProgressUpdates() {
        stopProgressUpdates()
        progressUpdateJob = viewModelScope.launch {
            while (true) {
                if (mediaController!!.isPlaying) {
                    val currentPosition = mediaController!!.currentPosition.coerceAtLeast(0L)
                    if (_uiState.value.currentPositionMs != currentPosition) {
                        _uiState.update { it.copy(currentPositionMs = currentPosition) }
                    }
                } else {
                    stopProgressUpdates()
                }
                delay(500L)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressUpdateJob?.cancel()
        progressUpdateJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopProgressUpdates()
        controllerFuture?.let { future ->
            MediaController.releaseFuture(future)
        }
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MusicApplication)
                val songRepository = application.container.songRepository
                val playlistRepository = application.container.playlistRepository
                val favoriteRepository = application.container.favoriteRepository
                MusicPlayerViewModel(application, songRepository, playlistRepository, favoriteRepository)
            }
        }
    }
}
