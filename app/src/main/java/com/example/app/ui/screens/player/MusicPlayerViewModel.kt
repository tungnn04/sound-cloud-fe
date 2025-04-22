package com.example.app.ui.screens.player // Hoặc package ViewModel của bạn

import android.annotation.SuppressLint
import android.app.Application
import android.net.Uri
import android.util.Log
import android.widget.Toast
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
import androidx.media3.exoplayer.ExoPlayer
import com.example.app.MusicApplication
import com.example.app.data.FavoriteRepository
import com.example.app.data.PlaylistRepository
import com.example.app.data.SongRepository
import com.example.app.model.PlayList
import com.example.app.model.Song
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
    val playlist: List<Song?> = emptyList(),
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val isLoading: Boolean = true,
    val error: String? = null,
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

    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(application).build()
    private var progressUpdateJob: Job? = null

    init {
        setupPlayerListener()
        viewModelScope.launch {
            val res = playlistRepository.findAll();
            if (res.isSuccessful) {
                _uiState.value = _uiState.value.copy(playlists = res.body()?.data ?: emptyList())
            }
        }
    }

    private fun setupPlayerListener() {
        exoPlayer.addListener(object : Player.Listener {
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
                    _uiState.update { it.copy(durationMs = exoPlayer.duration.coerceAtLeast(0)) }
                }
                if (playbackState == Player.STATE_ENDED) {
                    stopProgressUpdates()
                    _uiState.update { it.copy(currentPositionMs = 0, isPlaying = false) }
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {

                val newPlayingId = mediaItem?.mediaId?.toIntOrNull()
                val newSong = _uiState.value.playlist.find { it?.id == newPlayingId }
                _uiState.update {
                    it.copy(
                        currentSong = newSong,
                        currentPositionMs = 0L,
                        durationMs = exoPlayer.duration.coerceAtLeast(0),
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

            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {

            }
        })
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

    fun loadPlaylist(songs: List<Song>) {
        if (songs.isEmpty()) {
            _uiState.update { it.copy(isLoading = false, error = "Playlist is empty") }
            return
        }
        _uiState.update { it.copy(playlist = songs, isLoading = true, error = null) }
        val mediaItems = songs.map { song ->
            song.toMediaItem()
        }
        exoPlayer.setMediaItems(mediaItems, /* resetPosition= */ true)
        exoPlayer.prepare()
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
        val song = loadSong(id)
        val songMediaId = song?.id.toString()
        var foundIndex = -1

        for (i in 0 until exoPlayer.mediaItemCount) {
            if (exoPlayer.getMediaItemAt(i).mediaId == songMediaId) {
                foundIndex = i
                break
            }
        }

        if (foundIndex != -1) {

            if (exoPlayer.currentMediaItemIndex == foundIndex && exoPlayer.isPlaying) {
                Log.d("MusicPlayerVM", "Song is already the current playing item.")
            } else {
                exoPlayer.seekTo(foundIndex, 0)
                exoPlayer.playWhenReady = true
                if(exoPlayer.playbackState == Player.STATE_IDLE) exoPlayer.prepare()
                exoPlayer.play()
            }
        } else {
            Log.d("MusicPlayerVM", "Song not found in current queue. Setting as new playlist.")
            val newMediaItem = song?.toMediaItem()
            _uiState.update { it.copy(playlist = listOf(song)) }
            newMediaItem?.let { exoPlayer.setMediaItem(it) }
            exoPlayer.playWhenReady = true
            Log.d("MusicPlayerVM", "Calling prepare() for new playlist.")
            exoPlayer.prepare()
            Log.d("MusicPlayerVM", "Called prepare() for new playlist.")
            exoPlayer.play()
        }

        _uiState.update { it.copy(currentSong = song) }
        // Xóa lỗi nếu có
        if (_uiState.value.error != null) _uiState.update { it.copy(error = null) }
    }

    suspend fun playSongNext(id: Int) {
        val currentMediaItemIndex = exoPlayer.currentMediaItemIndex
        val mediaItemCount = exoPlayer.mediaItemCount
        val songMediaId = id.toString()

        if (currentMediaItemIndex == -1 || mediaItemCount == 0) {
            Log.d("MusicPlayerVM", "PlayNext: No current item or empty queue, playing immediately instead.")
            playSongImmediately(id)
            return
        }

        Log.d("MusicPlayerVM", "PlayNext: Processing song ID $id. Current index: $currentMediaItemIndex, Count: $mediaItemCount")
        _uiState.update { it.copy(isLoading = true, error = null) }

        val song = loadSong(id)
        if (song == null) {
            Log.e("MusicPlayerVM", "PlayNext: Failed to load song $id.")
            _uiState.update { it.copy(isLoading = false) }
            return
        }

        val insertIndex = currentMediaItemIndex + 1

        var existingIndex = -1
        for (i in 0 until mediaItemCount) {
            if (exoPlayer.getMediaItemAt(i).mediaId == songMediaId) {
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
                        exoPlayer.moveMediaItem(existingIndex, insertIndex)

                        _uiState.update { currentState ->
                            val currentPlaylist = currentState.playlist.filterNotNull().toMutableList()
                            val itemToMove = currentPlaylist.find { it.id == id }
                            if (itemToMove != null) {
                                val actualExistingIndex = currentPlaylist.indexOfFirst{ it.id == id}
                                if(actualExistingIndex != -1) {
                                    currentPlaylist.removeAt(actualExistingIndex)
                                    val actualInsertIndex = insertIndex.coerceAtMost(currentPlaylist.size)
                                    currentPlaylist.add(actualInsertIndex, itemToMove)
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

                exoPlayer.addMediaItem(insertIndex, newMediaItem)

                _uiState.update { currentState ->
                    val currentPlaylist = currentState.playlist.toMutableList()
                    val actualInsertIndex = insertIndex.coerceAtMost(currentPlaylist.size)
                    currentPlaylist.add(actualInsertIndex, song)
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
            exoPlayer.seekToDefaultPosition(0)
            exoPlayer.play()
        } else if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            if (exoPlayer.playbackState == Player.STATE_ENDED) {
                exoPlayer.seekTo(0)
            }
            exoPlayer.play()
        }
        if (_uiState.value.error != null) {
            _uiState.update { it.copy(error = null) }
        }
    }

    fun seekToNext() {
        if (exoPlayer.hasNextMediaItem()) {
            exoPlayer.seekToNextMediaItem()
            if (!exoPlayer.isPlaying) exoPlayer.play()
        }
    }

    fun seekToPrevious() {
        if (exoPlayer.hasPreviousMediaItem()) {
            exoPlayer.seekToPreviousMediaItem()
            if (!exoPlayer.isPlaying) exoPlayer.play()
        } else {
            exoPlayer.seekTo(0)
            if (!exoPlayer.isPlaying) exoPlayer.play()
        }
    }

    fun seekToPosition(positionMs: Long) {
        stopProgressUpdates()
        val targetPosition = positionMs.coerceIn(0, exoPlayer.duration)
        _uiState.update { it.copy(currentPositionMs = targetPosition) }
        exoPlayer.seekTo(targetPosition)
        if (_uiState.value.isPlaying) {
            startProgressUpdates()
        }
    }

    private fun startProgressUpdates() {
        stopProgressUpdates()
        progressUpdateJob = viewModelScope.launch {
            while (true) {
                if (exoPlayer.isPlaying) {
                    val currentPosition = exoPlayer.currentPosition.coerceAtLeast(0L)
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
        exoPlayer.release()
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
