package com.example.app.ui.screens.playlist

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.app.R
import com.example.app.model.PlayList
import com.example.app.model.Song
import com.example.app.ui.components.AppButton
import com.example.app.ui.components.ButtonStyle
import com.example.app.ui.components.ConfirmationPrompt
import com.example.app.ui.components.PlaylistOption
import com.example.app.ui.components.SongItem
import com.example.app.ui.components.SongOptionMenu
import com.example.app.ui.components.TopBar
import kotlinx.coroutines.launch

@Composable
fun PlaylistDetailScreen(
    playlistId: Int,
    navController: NavController,
    playlistDetailViewModel: PlaylistDetailViewModel = viewModel(factory = PlaylistDetailViewModel.factory),
    onPlayClick: (Int) -> Unit,
    onPlayAll: (List<Song>, Boolean) -> Unit,
    currentSong: Song?,
    isPlaying: Boolean
) {
    val uiState by playlistDetailViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var songClick by remember { mutableStateOf<Song?>(null) }
    var showPlaylistOption by remember { mutableStateOf(false) }
    var showDeletePlaylist by remember { mutableStateOf(false) }
    var playlistClick by remember { mutableStateOf<PlayList?>(null) }

    LaunchedEffect(true){
        try {
            playlistDetailViewModel.fetchData(playlistId)
        } catch (e: Exception) {
            Toast.makeText(
                context,
                e.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val handleDeletePlaylist: () -> Unit = {
        coroutineScope.launch {
            try {
                playlistDetailViewModel.deletePlaylist(uiState.playlist!!.id)
                navController.navigateUp()
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    e.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    Column {
        PlaylistOption(
            showPlaylistOption = showPlaylistOption,
            playlist = playlistClick,
            onDismissClick = { showPlaylistOption = false },
            onDelete = {
                showDeletePlaylist = true
                showPlaylistOption = false
            },
            onEdit = { }
        )
        ConfirmationPrompt(
            showConfirmationPrompt = showDeletePlaylist,
            title = uiState.playlist?.name ?: "",
            message = "Are you sure delete this playlist",
            onCancel = { showDeletePlaylist = false },
            onDelete = {
                handleDeletePlaylist()
                showDeletePlaylist = false
            }
        )
        SongOptionMenu(
            song = songClick,
            onDismissClick = { songClick = null },
            onPlayNextClick = { },
            onFavoriteClick = { songId, isFavorite ->
                playlistDetailViewModel.favoriteChange(songId, isFavorite)
            },
            onAddToPlaylist = { songId, playlistId ->
                playlistDetailViewModel.addSongToPlaylist(songId, playlistId)
            },
            onCreatePlaylist = { playlistName ->
                playlistDetailViewModel.createPlaylist(playlistName)
            },
            playlists = uiState.playlists
        )

        TopBar(
            title = "",
            onNavigationClick = { navController.navigateUp() },
            onActionClick = {
                playlistClick = uiState.playlist
                showPlaylistOption = true
            },
            navigationIcon = R.drawable.ic_back,
            actionIcon = R.drawable.ic_dots_vertical,
            color = MaterialTheme.colorScheme.background
        )
        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(uiState.playlist?.coverUrl ?: "https://cdn2.tuoitre.vn/thumb_w/480/2020/6/16/photo-1-15923021035102079282540.jpg")
                        .crossfade(true)
                        .build(),
                    contentDescription = uiState.playlist?.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Text(
                    text = uiState.playlist?.name ?: "",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                val numSong = uiState.playlist?.songs?.size ?: 0
                val numSongText = if (numSong > 1) "$numSong songs" else "$numSong song"
                Text(
                    text = numSongText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    AppButton(
                        text = "Shuffle",
                        onClick = {
                            onPlayAll(uiState.playlist?.songs ?: emptyList(), true)
                        },
                        style = ButtonStyle.PRIMARY,
                        iconResId = R.drawable.ic_shuffle,
                        modifier = Modifier.weight(1f)
                    )
                    AppButton(
                        text = "Play",
                        onClick = {
                            onPlayAll(uiState.playlist?.songs ?: emptyList(), false)
                        },
                        style = ButtonStyle.SECONDARY,
                        iconResId = R.drawable.ic_play_circle,
                        modifier = Modifier.weight(1f)
                    )
                }
                HorizontalDivider(color = Color.Gray, thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))
            }
            if (uiState.playlist?.songs?.isNotEmpty() == true){
                items(uiState.playlist?.songs?.size ?: 0 ) { index ->
                    SongItem(
                        song = uiState.playlist!!.songs!![index],
                        onMoreOptionClick = { songClick = it },
                        onPlayClick = onPlayClick,
                        currentSong = currentSong,
                        isPlaying = isPlaying
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}