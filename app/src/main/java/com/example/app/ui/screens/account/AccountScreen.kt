package com.example.app.ui.screens.account

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.app.R
import com.example.app.model.Song
import com.example.app.ui.MusicScreen
import com.example.app.ui.components.AppButton
import com.example.app.ui.components.ButtonStyle
import com.example.app.ui.components.SongItem
import com.example.app.ui.components.SongOptionMenu


@Composable
fun AccountScreen(
    navController: NavController,
    accountViewModel: AccountViewModel = viewModel(factory = AccountViewModel.factory),
    onPlayClick: (Int) -> Unit,
    onPlayNextClick: (Int) -> Unit,
    currentSong: Song?,
    isPlaying: Boolean
) {
    val uiState by accountViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var songClick by remember { mutableStateOf<Song?>(null) }

    LaunchedEffect(true) {
        try {
            accountViewModel.fetchData()
        } catch (e: Exception) {
            Toast.makeText(
                context,
                e.message,
                Toast.LENGTH_SHORT).
            show()
        }
    }

    Scaffold(

    ) {paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
                .padding(paddingValues)
        ) {
            SongOptionMenu(
                song = songClick,
                onDismissClick = { songClick = null },
                onPlayNextClick = onPlayNextClick,
                onFavoriteClick = { songId, isFavorite ->
                    accountViewModel.favoriteChange(songId, isFavorite)
                },
                onAddToPlaylist = { songId, playlistId ->
                    accountViewModel.addSongToPlaylist(songId, playlistId)
                },
                onCreatePlaylist = { playlistName ->
                    accountViewModel.createPlaylist(playlistName)
                },
                playlists = uiState.playlists
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(uiState.user?.avatarUrl ?: "https://res.cloudinary.com/dcwopmt83/image/upload/v1747213838/user_default_xt53cn.png")
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Column(
                    modifier = Modifier.weight(1f).height(100.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = uiState.user?.fullName ?: "",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = uiState.user?.email ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AppButton(
                            text = "Edit profile",
                            onClick = { navController.navigate(MusicScreen.EDIT_PROFILE.name) },
                            style = ButtonStyle.PRIMARY,
                            modifier = Modifier.weight(1f)
                                .height(40.dp)
                        )
                        AppButton(
                            text = "Logout",
                            onClick = {
                                accountViewModel.logout()
                                navController.navigate(MusicScreen.LOGIN.name)
                            },
                            style = ButtonStyle.SECONDARY,
                            modifier = Modifier.weight(1f)
                                .height(40.dp),

                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My upload",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = { navController.navigate(MusicScreen.UPLOAD_SONG.name) }) {
                    Icon(
                        painterResource(id = R.drawable.ic_add_circle),
                        contentDescription = "Add",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            uiState.songs.forEach { song ->
                Spacer(modifier = Modifier.height(8.dp))
                SongItem(song = song, onPlayClick = onPlayClick, onMoreOptionClick = { songClick = it },
                    currentSong = currentSong, isPlaying = isPlaying)
            }
        }
    }
}