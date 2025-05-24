package com.example.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.app.R
import com.example.app.model.Song
import com.example.app.ui.components.SongItem
import com.example.app.ui.components.SongOptionMenu
import com.example.app.ui.components.TopBar
import com.shashank.sony.fancytoastlib.FancyToast

@Composable
fun CategoryScreen(
    navController: NavController,
    categoryId: Int,
    onPlayClick: (Song) -> Unit,
    onPlayNextClick: (Song) -> Unit,
    currentSong: Song?,
    isPlaying: Boolean,
    categoryViewModel: CategoryViewModel = viewModel(factory = CategoryViewModel.factory)
) {
    val uiState by categoryViewModel.uiState.collectAsState()
    val context = LocalContext.current
    var songClick by remember { mutableStateOf<Song?>(null) }

    LaunchedEffect(true) {
        try {
            categoryViewModel.fetchData(categoryId)
        } catch (e: Exception) {
            FancyToast.makeText(
                context,
                e.message,
                FancyToast.LENGTH_SHORT,
                FancyToast.ERROR,
                false
            ).show()
        }
    }

    Column {
        SongOptionMenu(
            song = songClick,
            onDismissClick = { songClick = null },
            onPlayNextClick = onPlayNextClick,
            onFavoriteClick = { songId, isFavorite ->
                categoryViewModel.favoriteChange(songId, isFavorite)
            },
            onAddToPlaylist = { songId, playlistId ->
                categoryViewModel.addSongToPlaylist(songId, playlistId)
            },
            onCreatePlaylist = { playlistName ->
                categoryViewModel.createPlaylist(playlistName)
            },
            playlists = uiState.playlists
        )
        TopBar(
            title = uiState.category?.name ?: "",
            onNavigationClick = { navController.navigateUp() },
            onActionClick = { },
            navigationIcon = R.drawable.ic_back,
            actionIcon = R.drawable.ic_search,
            color = MaterialTheme.colorScheme.background
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(uiState.category?.coverUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(180.dp).padding(bottom = 16.dp)
                )
            }

            if (uiState.songs.isNotEmpty()){
                items(uiState.songs.size) { index ->
                    SongItem(
                        song = uiState.songs[index],
                        onMoreOptionClick = { songClick = it },
                        onPlayClick = onPlayClick,
                        currentSong = currentSong,
                        isPlaying = isPlaying,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}