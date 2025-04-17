package com.example.app.ui.screens.playlist

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.app.R
import com.example.app.model.CreatePlaylist
import com.example.app.model.PlayList
import com.example.app.ui.MusicScreen
import com.example.app.ui.components.AppButton
import com.example.app.ui.components.ButtonStyle
import com.example.app.ui.components.ConfirmationPrompt
import com.example.app.ui.components.CreatePlaylist
import com.example.app.ui.components.PlaylistOption
import com.example.app.ui.theme.Purple20
import kotlinx.coroutines.launch
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistScreen(
    navController: NavController,
    playlistViewModel: PlaylistViewModel = viewModel(factory = PlaylistViewModel.factory)
)  {
    val uiState by playlistViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var showCreatePlaylist by remember { mutableStateOf(false)}
    var showDeletePlaylist by remember { mutableStateOf(false)}
    var showPlaylistOption by remember { mutableStateOf(false) }
    var playListClick by remember { mutableStateOf<PlayList?>(null) }

    LaunchedEffect(true) {
        try {
            playlistViewModel.fetchData()
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
                    playlistViewModel.deletePlaylist(playlist.id)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Playlists",
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
                    playlistViewModel.createPlaylist(it)
                },
                onDone = {
                    showCreatePlaylist = false
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
                onEdit = {  }
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                val numPlaylist = uiState.playlists.size
                val numPlaylistText = if (numPlaylist > 1) "$numPlaylist playlists" else "$numPlaylist playlist"
                Text(
                    text = numPlaylistText,
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
            Row(
                modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton ( onClick = {showCreatePlaylist = true}, modifier = Modifier.size(80.dp)) {
                    Icon(
                        painterResource(id = R.drawable.ic_add_circle),
                        contentDescription = "Add playlist",
                        tint = Color.White,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add New Playlist",
                    style = TextStyle(color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold),
                )
            }
            uiState.playlists.forEach { playlist ->
                PlayListItem(playList = playlist, navController = navController,
                    onMoreOptionClick = {
                        showPlaylistOption = true
                        playListClick = it
                    }
                )
            }
        }
    }
}

@Composable
private fun PlayListItem(
    playList: PlayList,
    onMoreOptionClick: (PlayList) -> Unit,
    navController: NavController
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { navController.navigate("${MusicScreen.PLAYLIST_DETAIL.name}/${playList.id}") }
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(playList.coverUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Playlist cover",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Column(
                modifier = Modifier.padding(start = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = playList.name,
                    style = TextStyle(color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold),
                )
                val numSong = playList.songs?.size ?: 0
                val numSongText = if (numSong > 1) "$numSong songs" else "$numSong song"
                Text(
                    text = numSongText,
                    style = TextStyle(color = Color.Gray, fontSize = 12.sp, textAlign = TextAlign.Start),
                )
            }
        }
        IconButton(onClick = {onMoreOptionClick(playList)}) {
            Icon(
                painterResource(id = R.drawable.ic_dots_vertical),
                contentDescription = "More options",
                tint = Color.White
            )
        }
    }
}
