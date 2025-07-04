package com.example.app.ui.screens.account

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import com.example.app.ui.components.ConfirmationPrompt
import com.example.app.ui.components.SongItem
import com.example.app.ui.components.SongOptionUploadMenu
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.launch


@Composable
fun AccountScreen(
    navController: NavController,
    accountViewModel: AccountViewModel = viewModel(factory = AccountViewModel.factory),
    onPlayClick: (Song) -> Unit,
    onPlayNextClick: (Song) -> Unit,
    currentSong: Song?,
    isPlaying: Boolean,
    stopService: () -> Unit
) {
    val uiState by accountViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var songClick by remember { mutableStateOf<Song?>(null) }
    var showDeleteSong by remember { mutableStateOf(false)}

    val handleDeleteSong = {
        songClick?.let { song ->
            coroutineScope.launch {
                try {
                    accountViewModel.deleteSong(song.id)
                    songClick = null
                    FancyToast.makeText(context, "Song deleted successfully", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show()
                } catch (e: Exception) {
                    FancyToast.makeText(context, e.message, FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show()
                }
            }
        }
    }

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.background)
    ) {
        ConfirmationPrompt(
            showConfirmationPrompt = showDeleteSong,
            title = songClick?.title ?: "",
            message = "Are you sure delete this song ?",
            onCancel = { showDeleteSong = false },
            onDelete = {
                handleDeleteSong()
                showDeleteSong = false
            }
        )
        SongOptionUploadMenu(
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
            playlists = uiState.playlists,
            onDelete = { showDeleteSong = true }
        )
        Text(
            text = "Account",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp)
            )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
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
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = uiState.user?.fullName ?: "",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = uiState.user?.email ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp).padding(start = 16.dp),
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
                    painterResource(id = R.drawable.ic_add_song),
                    contentDescription = "Add",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        uiState.songs.forEach { song ->
            Spacer(modifier = Modifier.height(8.dp))
            SongItem(song = song, onPlayClick = onPlayClick, onMoreOptionClick = { songClick = it },
                currentSong = currentSong, isPlaying = isPlaying, modifier = Modifier.padding(start = 24.dp))
        }
        Spacer(Modifier.height(24.dp))
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Setting",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = {navController.navigate(MusicScreen.EDIT_PROFILE.name) })
                    .padding(horizontal = 8.dp ,vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_edit),
                    contentDescription = "Edit profile",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Edit Profile",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = {navController.navigate(MusicScreen.CHANGE_PASSWORD.name) })
                    .padding(horizontal = 8.dp ,vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_key),
                    contentDescription = "Change password",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Change password",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = {navController.navigate(MusicScreen.DARK_MODE.name) })
                    .padding(horizontal = 8.dp ,vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_dark_mode),
                    contentDescription = "Change dark theme",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Dark theme",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = uiState.currentThemeSetting.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = {
                        accountViewModel.logout()
                        stopService()
                        navController.navigate(MusicScreen.LOGIN.name)
                    })
                    .padding(horizontal = 8.dp ,vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_logout),
                    contentDescription = "Logout",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Logout",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

