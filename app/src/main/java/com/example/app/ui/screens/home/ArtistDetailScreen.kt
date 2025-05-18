package com.example.app.ui.screens.home

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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.app.R
import com.example.app.model.Song
import com.example.app.ui.components.AppButton
import com.example.app.ui.components.ButtonStyle
import com.example.app.ui.components.SongItem
import com.example.app.ui.components.SongOptionMenu
import com.example.app.ui.components.TopBar

@Composable
fun ArtistDetailScreen(
    navController: NavController,
    artistDetailViewModel: ArtistDetailViewModel = viewModel(factory = ArtistDetailViewModel.factory),
    onPlayClick: (Song) -> Unit,
    onPlayNextClick: (Song) -> Unit,
    artistId: Int,
    onPlayAll: (List<Song>, Boolean) -> Unit,
    currentSong: Song?,
    isPlaying: Boolean
) {
    val uiState by artistDetailViewModel.uiState.collectAsState()
    val context = LocalContext.current
    var songClick by remember { mutableStateOf<Song?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(true) {
        try {
            artistDetailViewModel.fetchData(artistId)
        } catch (e: Exception) {
            Toast.makeText(
                context,
                e.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Column {
        SongOptionMenu(
            song = songClick,
            onDismissClick = { songClick = null },
            onPlayNextClick = onPlayNextClick,
            onFavoriteClick = { songId, isFavorite ->
                artistDetailViewModel.favoriteChange(songId, isFavorite)
            },
            onAddToPlaylist = { songId, playlistId ->
                artistDetailViewModel.addSongToPlaylist(songId, playlistId)
            },
            onCreatePlaylist = { playlistName ->
                artistDetailViewModel.createPlaylist(playlistName)
            },
            playlists = uiState.playlists
        )
        TopBar(
            title = "",
            onNavigationClick = { navController.navigateUp() },
            onActionClick = { },
            navigationIcon = R.drawable.ic_back,
            actionIcon = R.drawable.ic_dots_vertical,
            color = MaterialTheme.colorScheme.background
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(uiState.artist?.profilePicture)
                        .crossfade(true)
                        .build(),
                    contentDescription = uiState.artist?.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Text(
                    text = uiState.artist?.name ?: "",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                val numAlbums = uiState.artist?.albums?.size ?: 0
                val numSongs = uiState.artist?.songs?.size ?: 0
                val numAlbumsText = if (numAlbums > 1) "$numAlbums albums" else "$numAlbums album"
                val numSongsText = if (numSongs > 1) "$numSongs songs" else "$numSongs song"
                Text(
                    text = "$numAlbumsText | $numSongsText",
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
                            onPlayAll(uiState.artist?.songs ?: emptyList(), true)
                        },
                        style = ButtonStyle.PRIMARY,
                        iconResId = R.drawable.ic_shuffle,
                        modifier = Modifier.weight(1f)
                    )
                    AppButton(
                        text = "Play",
                        onClick = {
                            onPlayAll(uiState.artist?.songs ?: emptyList(), false)
                        },
                        style = ButtonStyle.SECONDARY,
                        iconResId = R.drawable.ic_play_circle,
                        modifier = Modifier.weight(1f)
                    )
                }
                HorizontalDivider(color = Color.Gray, thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))
            }
            if (uiState.artist?.songs?.isNotEmpty() == true){
                items(uiState.artist?.songs?.size ?: 0 ) { index ->
                    SongItem(
                        song = uiState.artist!!.songs!![index],
                        onMoreOptionClick = { songClick = it },
                        onPlayClick = onPlayClick,
                        currentSong = currentSong,
                        isPlaying = isPlaying
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}