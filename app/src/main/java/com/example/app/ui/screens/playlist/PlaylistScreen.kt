package com.example.app.ui.screens.playlist

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.app.R
import com.example.app.model.PlayList
import com.example.app.ui.MusicScreen
import com.example.app.ui.components.ConfirmationPrompt
import com.example.app.ui.components.CreatePlaylist
import com.example.app.ui.components.EditPlaylist
import com.example.app.ui.components.PlaylistOption
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.launch

@Composable
fun PlaylistScreen(
    navController: NavController,
    playlistViewModel: PlaylistViewModel = viewModel(factory = PlaylistViewModel.factory)
)  {
    val uiState by playlistViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var showCreatePlaylist by remember { mutableStateOf(false)}
    var showDeletePlaylist by remember { mutableStateOf(false)}
    var showEditPlaylist by remember { mutableStateOf(false)}
    var showPlaylistOption by remember { mutableStateOf(false) }
    var playListClick by remember { mutableStateOf<PlayList?>(null) }
    var isDesc by remember { mutableStateOf(true) }

    LaunchedEffect(isDesc) {
        try {
            playlistViewModel.fetchData(isDesc)
        } catch (e: Exception) {
            Toast.makeText(
                context,
                e.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val handleDeletePlaylist: () -> Unit = {
        playListClick?.let { playlist ->
            coroutineScope.launch {
                try {
                    playlistViewModel.deletePlaylist(playlist.id, isDesc)
                    playListClick = null
                    FancyToast.makeText(context, "Delete playlist successfully", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show()
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } ?: FancyToast.makeText(context, "Playlist not selected", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show()
    }

    val handleEditPlaylist: (String) -> Unit = {
        playListClick?.let { playlist ->
            coroutineScope.launch {
                try {
                    playlistViewModel.updatePlaylist(playlist.id,it, isDesc)
                    FancyToast.makeText(context, "Update playlist name successfully", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show()
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } ?: Toast.makeText(context, "Playlist not selected", Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ConfirmationPrompt(
            showConfirmationPrompt = showDeletePlaylist,
            title = playListClick?.name ?: "",
            message = "Are you sure delete this playlist ?",
            onCancel = { showDeletePlaylist = false },
            onDelete = {
                handleDeletePlaylist()
                showDeletePlaylist = false
            }
        )
        CreatePlaylist(
            showCreatePlaylist = showCreatePlaylist,
            onDismissClick = { showCreatePlaylist = false },
            onCreatePlaylist = {
                playlistViewModel.createPlaylist(it, isDesc)
            },
            onDone = {
                showCreatePlaylist = false
            }
        )
        EditPlaylist(
            showEditPlaylist = showEditPlaylist,
            name = playListClick?.name ?: "",
            onDismissClick = { showEditPlaylist = false },
            onEditPlaylist = handleEditPlaylist,
            onDone = {
                showEditPlaylist = false
            }
        )
        PlaylistOption(
            showPlaylistOption = showPlaylistOption,
            playlist = playListClick,
            onDismissClick = {
                showPlaylistOption = false
                playListClick = null
            },
            onDelete = {
                showDeletePlaylist = true
                showPlaylistOption = false
            },
            onEdit = {
                showEditPlaylist = true
                showPlaylistOption = false
            }
        )
        Text(
            text = "Playlists",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(top = 16.dp, start = 16.dp)
                .fillMaxWidth()
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            val numPlaylist = uiState.playlists.size
            val numPlaylistText = if (numPlaylist > 1) "$numPlaylist playlists" else "$numPlaylist playlist"
            Text(
                text = numPlaylistText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(modifier = Modifier.clickable { isDesc = !isDesc }, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Recently Added",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    painterResource(id = if (isDesc) R.drawable.ic_desc else R.drawable.ic_asc),
                    contentDescription = "Sort",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(30.dp).padding(start = 8.dp)
                )
            }
        }
        HorizontalDivider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
        LazyColumn {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable { showCreatePlaylist = true }.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton ( onClick = { showCreatePlaylist = true }, modifier = Modifier.size(80.dp)) {
                        Icon(
                            painterResource(id = R.drawable.ic_add_circle),
                            contentDescription = "Add playlist",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Add New Playlist",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
            if (uiState.playlists.isNotEmpty()){
                items(uiState.playlists.size) { index ->
                    PlayListItem(playList = uiState.playlists[index], navController = navController,
                        onMoreOptionClick = {
                            showPlaylistOption = true
                            playListClick = it
                        },
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun PlayListItem(
    playList: PlayList,
    onMoreOptionClick: (PlayList) -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().clickable { navController.navigate("${MusicScreen.PLAYLIST_DETAIL.name}/${playList.id}") },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(playList.coverUrl ?: "https://cdn2.tuoitre.vn/thumb_w/480/2020/6/16/photo-1-15923021035102079282540.jpg")
                    .crossfade(true)
                    .build(),
                contentDescription = "Playlist cover",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Column(
                modifier = Modifier.padding(start = 8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = playList.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                val numSong = playList.songs?.size ?: 0
                val numSongText = if (numSong > 1) "$numSong songs" else "$numSong song"
                Text(
                    text = numSongText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        IconButton(onClick = {onMoreOptionClick(playList)}) {
            Icon(
                painterResource(id = R.drawable.ic_dots_vertical),
                contentDescription = "More options",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
