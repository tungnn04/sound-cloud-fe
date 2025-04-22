package com.example.app.ui.screens.favourite

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.app.ui.components.SongOption
import com.example.app.ui.components.SongOptionMenu
import com.example.app.ui.screens.playlist.PlaylistViewModel
import com.example.app.ui.theme.Purple20

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    navController: NavController,
    favoriteViewModel: FavoriteViewModel = viewModel(factory = FavoriteViewModel.factory),
    onPlayClick: (Int) -> Unit,
) {
    val uiState by favoriteViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Favorites",
                        style = TextStyle(color = Color.White, fontSize = 28.sp)
                    ) },
                actions = {
                    IconButton(onClick = {  }) {
                        Icon(
                            painterResource(id = R.drawable.ic_search),
                            contentDescription = "Search",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF120320)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(Color(0xFF120320))
                .padding(horizontal = 16.dp)
                .padding(paddingValues),
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
                onPlayNextClick = { },
                onFavoriteClick = { songId, isFavorite ->
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                AppButton(
                    text = "Shuffle",
                    onClick = {},
                    style = ButtonStyle.PRIMARY,
                    iconResId = R.drawable.ic_shuffle
                )
                AppButton(
                    text = "Play",
                    onClick = {},
                    style = ButtonStyle.SECONDARY,
                    iconResId = R.drawable.ic_play_circle
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                val numSong = uiState.songs.size
                val numFavoriteText = if (numSong > 1) "$numSong favorites" else "$numSong favorite"
                Text(
                    text = numFavoriteText,
                    style = TextStyle(color = Color.Gray, fontSize = 14.sp, textAlign = TextAlign.Start),
                )
                Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF120320))) {
                    Icon(
                        painterResource(id = R.drawable.ic_swap),
                        contentDescription = "Sort",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            HorizontalDivider(color = Color.Gray, thickness = 1.dp)
            uiState.songs.forEach { song ->
                SongItem(song = song, onMoreOptionClick = { songClick = it } , onPlayClick = onPlayClick)
            }
        }
    }
}