package com.example.app.ui.screens.home

import android.widget.Space
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.example.app.ui.components.SongOption
import com.example.app.ui.components.SongOptionMenu
import com.example.app.ui.components.TopBar
import com.example.app.ui.screens.playlist.PlaylistViewModel
import com.example.app.ui.theme.Purple20

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDetailScreen(
    navController: NavController,
    albumDetailViewModel: AlbumDetailViewModel = viewModel(factory = AlbumDetailViewModel.factory),
    albumId: Int,
    onPlayClick: (Int) -> Unit,
    onPlayAll: (List<Song>, Boolean) -> Unit,
    currentSong: Song?,
    isPlaying: Boolean
) {
    val uiState by albumDetailViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var songClick by remember { mutableStateOf<Song?>(null) }

    LaunchedEffect(true) {
        try {
            albumDetailViewModel.fetchAlbumDetail(albumId)
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
            onPlayNextClick = { },
            onFavoriteClick = { songId, isFavorite ->
                albumDetailViewModel.favoriteChange(songId, isFavorite)
            },
            onAddToPlaylist = { songId, playlistId ->
                albumDetailViewModel.addSongToPlaylist(songId, playlistId)
            },
            onCreatePlaylist = { playlistName ->
                albumDetailViewModel.createPlaylist(playlistName)
            },
            playlists = uiState.playlists
        )
        TopBar(
            title = "",
            onNavigationClick = { navController.navigateUp() },
            onActionClick = { },
            navigationIcon = R.drawable.ic_back,
            actionIcon = R.drawable.ic_dots_vertical,
            color = Color(0xFF120320)
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF120320))
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(uiState.album?.coverUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = uiState.album?.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth(0.85f)
                        .aspectRatio(1f)
                        .clip(CircleShape)
                )
                Text(
                    text = uiState.album?.title ?: "",
                    style = TextStyle(color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                val numSong = uiState.album?.songs?.size ?: 0
                val numSongText = if (numSong > 1) "$numSong songs" else "$numSong song"
                Text(
                    text = "${uiState.album?.artistName} | $numSongText | ${uiState.album?.releaseYear}",
                    style = TextStyle(color = Color.Gray, fontSize = 14.sp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    AppButton(
                        text = "Shuffle",
                        onClick = {
                            onPlayAll(uiState.album?.songs ?: emptyList(), true)
                        },
                        style = ButtonStyle.PRIMARY,
                        iconResId = R.drawable.ic_shuffle,
                        modifier = Modifier.weight(1f)
                    )
                    AppButton(
                        text = "Play",
                        onClick = {
                            onPlayAll(uiState.album?.songs ?: emptyList(), false)
                        },
                        style = ButtonStyle.SECONDARY,
                        iconResId = R.drawable.ic_play_circle,
                        modifier = Modifier.weight(1f)
                    )
                }
                HorizontalDivider(color = Color.Gray, thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))
            }
            if (uiState.album?.songs?.isNotEmpty() == true){
                items(uiState.album?.songs?.size ?: 0 ) { index ->
                    SongItem(
                        song = uiState.album!!.songs!![index],
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