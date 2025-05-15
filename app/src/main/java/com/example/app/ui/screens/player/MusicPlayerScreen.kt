package com.example.app.ui.screens.player

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.app.R
import com.example.app.model.Song
import com.example.app.ui.components.ListSong
import com.example.app.ui.components.SongOptionMenu
import com.example.app.ui.components.TopBar


@Composable
fun MusicPlayerScreen(
    musicPlayerViewModel: MusicPlayerViewModel,
    onMinimize: () -> Unit,
    onPlayClick: (Int) -> Unit,
    onPlayNextClick: (Int) -> Unit
) {
    val context = LocalContext.current
    val uiState by musicPlayerViewModel.uiState.collectAsState()
    var songClick by remember { mutableStateOf<Song?>(null) }
    var isFavorite by remember { mutableStateOf(uiState.currentSong!!.isFavorite) }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { errorMsg ->
            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier.padding(top = 16.dp)
    ) {
        SongOptionMenu(
            song = songClick,
            onDismissClick = { songClick = null },
            onPlayNextClick = onPlayNextClick ,
            onFavoriteClick = { songId, isFavorite ->
                musicPlayerViewModel.favoriteChange(songId, isFavorite)
            },
            onAddToPlaylist = { songId, playlistId ->
                musicPlayerViewModel.addSongToPlaylist(songId, playlistId)
            },
            onCreatePlaylist = { playlistName ->
                musicPlayerViewModel.createPlaylist(playlistName)
            },
            playlists = uiState.playlists
        )
        TopBar(
            title = "",
            onNavigationClick = { onMinimize() },
            onActionClick = {
                musicPlayerViewModel.favoriteChange(uiState.currentSong!!.id, isFavorite)
                isFavorite = !isFavorite
            },
            navigationIcon = R.drawable.ic_chevron_down,
            actionIcon = if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_outline,
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(uiState.currentSong?.coverUrl ?: R.drawable.ic_launcher_background)
                    .crossfade(true)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .build(),
                contentDescription = "Album Art",
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .aspectRatio(1f)
                    .padding(16.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = uiState.currentSong?.title ?: "---",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = uiState.currentSong?.artistName ?: "---",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                var sliderDraggingValue by remember { mutableStateOf<Float?>(null) }

                Slider(
                    value = sliderDraggingValue ?: uiState.currentPositionMs.toFloat(),
                    onValueChange = { newValue ->
                        sliderDraggingValue = newValue
                    },
                    onValueChangeFinished = {
                        musicPlayerViewModel.seekToPosition(sliderDraggingValue?.toLong() ?: uiState.currentPositionMs)
                        sliderDraggingValue = null // Reset
                    },
                    modifier = Modifier.fillMaxWidth(),

                    valueRange = 0f..(uiState.durationMs.toFloat().coerceAtLeast(1f)),
                    enabled = uiState.durationMs > 0
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(uiState.currentTimeFormatted, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
                    Text(uiState.totalTimeFormatted, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            PlayerControls(
                modifier = Modifier.fillMaxWidth(),
                isPlaying = uiState.isPlaying,
                onPlayPauseClick = { musicPlayerViewModel.playPause() },
                onNextClick = { musicPlayerViewModel.seekToNext() },
                onPreviousClick = { musicPlayerViewModel.seekToPrevious() },
                onShuffleClick = { musicPlayerViewModel.toggleShuffle() },
                onRepeatClick = {musicPlayerViewModel.cycleRepeatMode()},
                isShuffle = uiState.isShuffleEnabled,
                repeatMode = uiState.repeatMode
            )

            if (uiState.playlist.isNotEmpty()){
                ListSong(
                    listSong = uiState.playlist,
                    onMoreOptionClick = {
                        songClick = it
                    },
                    onPlayClick = onPlayClick,
                    currentSong = uiState.currentSong,
                    isPlaying = uiState.isPlaying
                )
            }
        }
    }
}

@Composable
fun PlayerControls(
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    repeatMode: Int,
    isShuffle: Boolean,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onShuffleClick: () -> Unit,
    onRepeatClick: () -> Unit
) {
    Row(
        modifier = modifier.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onShuffleClick, modifier = Modifier.size(48.dp)) {
            Icon(
                painterResource(id = R.drawable.ic_shuffle),
                contentDescription = "Shuffle",
                tint = if (isShuffle) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
        IconButton(onClick = onPreviousClick, modifier = Modifier.size(48.dp)) {
            Icon(
                painterResource(id = R.drawable.ic_skip_previous),
                contentDescription = "Previous",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        IconButton(onClick = onPlayPauseClick, modifier = Modifier.size(64.dp)) {
            Icon(
                painterResource(id = if (isPlaying) R.drawable.ic_pause_circle else R.drawable.ic_play_circle),
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxSize()
            )
        }
        IconButton(onClick = onNextClick, modifier = Modifier.size(48.dp)) {
            Icon(
                painterResource(id = R.drawable.ic_skip_next),
                contentDescription = "Next",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        IconButton(onClick = onRepeatClick, modifier = Modifier.size(48.dp)) {
            when(repeatMode) {
                0 -> Icon(
                    painterResource(id = R.drawable.ic_repeat),
                    contentDescription = "Repeat",
                    tint = MaterialTheme.colorScheme.onSurface
                )
                1 -> Icon(
                    painterResource(id = R.drawable.ic_repeat_once),
                    contentDescription = "Repeat",
                    tint = MaterialTheme.colorScheme.primary
                )
                2 -> Icon(
                    painterResource(id = R.drawable.ic_repeat),
                    contentDescription = "Repeat",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
