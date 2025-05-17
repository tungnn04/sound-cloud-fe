package com.example.app.ui.screens.favourite

import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app.R
import com.example.app.model.Song
import com.example.app.ui.components.AppButton
import com.example.app.ui.components.ButtonStyle
import com.example.app.ui.components.ConfirmationPrompt
import com.example.app.ui.components.SongItem
import com.example.app.ui.components.SongOptionMenu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    navController: NavController,
    favoriteViewModel: FavoriteViewModel = viewModel(factory = FavoriteViewModel.factory),
    onPlayClick: (Int) -> Unit,
    onPlayNextClick: (Int) -> Unit,
    onPlayAll: (List<Song>, Boolean) -> Unit,
    currentSong: Song?,
    isPlaying: Boolean
) {
    val uiState by favoriteViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var showDeleteFavorite by remember { mutableStateOf(false) }
    var songClick by remember { mutableStateOf<Song?>(null) }

    LaunchedEffect(true) {
        try {
            favoriteViewModel.fetchData()
        } catch (e: Exception) {
            Toast.makeText(
                context,
                e.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ConfirmationPrompt(
            showConfirmationPrompt = showDeleteFavorite,
            title = songClick?.title ?: "",
            message = "Are you sure remove this song in favorites ?",
            onCancel = {
                showDeleteFavorite = false
                songClick = null
            },
            onDelete = {
                favoriteViewModel.deleteSong(songClick!!.id)
                showDeleteFavorite = false
                songClick = null
            }
        )
        SongOptionMenu(
            song = songClick,
            onDismissClick = { songClick = null },
            onPlayNextClick = onPlayNextClick,
            onFavoriteClick = { _, _ ->
                showDeleteFavorite = true
            },
            onAddToPlaylist = { songId, playlistId ->
                favoriteViewModel.addSongToPlaylist(songId, playlistId)
            },
            onCreatePlaylist = { playlistName ->
                favoriteViewModel.createPlaylist(playlistName)
            },
            playlists = uiState.playlists
        )
        Text(
            text = "Favorites",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(vertical = 16.dp)
                .fillMaxWidth()
        )
        LazyColumn {
            item {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    AppButton(
                        text = "Shuffle",
                        onClick = {
                            onPlayAll(uiState.songs, true)
                        },
                        style = ButtonStyle.PRIMARY,
                        iconResId = R.drawable.ic_shuffle
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    AppButton(
                        text = "Play",
                        onClick = {
                            onPlayAll(uiState.songs, false)
                        },
                        style = ButtonStyle.SECONDARY,
                        iconResId = R.drawable.ic_play_circle
                    )
                }
                Spacer(Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val numSong = uiState.songs.size
                    val numFavoriteText = if (numSong > 1) "$numSong favorites" else "$numSong favorite"
                    Text(
                        text = numFavoriteText,
                        style = TextStyle(color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp, textAlign = TextAlign.Start),
                    )
                    Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                        Icon(
                            painterResource(id = R.drawable.ic_swap),
                            contentDescription = "Sort",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            if (uiState.songs.isNotEmpty()){
                items(uiState.songs.size) { index ->
                    SongItem(
                        song = uiState.songs[index],
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