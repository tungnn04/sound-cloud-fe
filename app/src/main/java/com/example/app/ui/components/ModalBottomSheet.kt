package com.example.app.ui.components

import android.util.Log
import android.widget.Toast
import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.app.R
import com.example.app.model.PlayList
import com.example.app.model.Song

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistOption(
    showPlaylistOption: Boolean,
    playlist: PlayList?,
    onDismissClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    if (showPlaylistOption) {
        ModalBottomSheet(
            onDismissRequest = onDismissClick,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            scrimColor = Color.Black.copy(alpha = 0.6f),
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(playlist?.coverUrl ?: "https://cdn2.tuoitre.vn/thumb_w/480/2020/6/16/photo-1-15923021035102079282540.jpg")
                                .crossfade(true)
                                .build(),
                            contentDescription = "Playlist Cover",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = playlist?.name ?: "Unknown playlist",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp)
                        )
                    }
                    HorizontalDivider(color = Color.Gray , thickness = 1.dp)
                    OptionItem(R.drawable.ic_edit, "Edit playlist", onEdit)
                    OptionItem(R.drawable.ic_delete, "Delete playlist", onDelete)
                }
            }
        )
    }
}

@Composable
fun SongOptionMenu(
    song: Song?,
    onCreatePlaylist: (String) -> Unit,
    onAddToPlaylist: (Int, Int) -> Unit,
    onDismissClick: () -> Unit,
    onPlayNextClick: () -> Unit,
    onFavoriteClick: (Int, Boolean) -> Unit,
    playlists: List<PlayList>
) {
    var showSongOption by remember { mutableStateOf(false) }
    var showAddPlaylist by remember { mutableStateOf(false) }
    var showCreatePlaylist by remember { mutableStateOf(false) }

    LaunchedEffect(song) {
        if (song != null) {
            showSongOption = true
            showAddPlaylist = false
            showCreatePlaylist = false
        } else {
            showSongOption = false
            showAddPlaylist = false
            showCreatePlaylist = false
        }
    }

    if (showSongOption && song != null) {
        SongOption(
            showSongOption = true,
            song = song,
            onDismissClick = onDismissClick,
            onAddToPlaylistClick = {
                showAddPlaylist = true
                showSongOption = false
            },
            onPlayNextClick = {
                onPlayNextClick()
                showSongOption = false
            },
            onFavoriteClick = onFavoriteClick,
        )
    }
    if (showAddPlaylist && song != null) {
        AddPlaylist(
            showAddPlaylist = true,
            onDismissClick = { showAddPlaylist = false },
            onCreatePlaylist = {
                showAddPlaylist = false
                showCreatePlaylist = true
            },
            onAddToPlaylist = onAddToPlaylist,
            songId = song.id,
            playlists = playlists
        )
    }
    if (showCreatePlaylist) {
        CreatePlaylist(
            showCreatePlaylist = true,
            onDismissClick = { showCreatePlaylist = false },
            onCreatePlaylist = onCreatePlaylist,
            onDone = {
                showCreatePlaylist = false
                showAddPlaylist = true
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongOption(
    showSongOption: Boolean,
    song: Song?,
    onDismissClick: () -> Unit,
    onPlayNextClick: () -> Unit,
    onAddToPlaylistClick: () -> Unit,
    onFavoriteClick: (Int, Boolean) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    var isFavorite by remember { mutableStateOf(song!!.isFavorite) }
    if (showSongOption && song != null) {
        ModalBottomSheet(
            onDismissRequest = onDismissClick,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            scrimColor = Color.Black.copy(alpha = 0.6f),
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Row {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(song.coverUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Song cover",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(80.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                            Column(
                                modifier = Modifier.padding(start = 16.dp),
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = song.title,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                Text(
                                    text = song.artistName ?: "Unknown artist",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }

                        IconButton(onClick = {
                            onFavoriteClick(song.id, isFavorite)
                            isFavorite = !isFavorite
                        }) {
                            Icon(
                                painterResource(id = if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_outline),
                                contentDescription = "Play song",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    HorizontalDivider(color = Color.Gray , thickness = 1.dp)
                    OptionItem(R.drawable.ic_skip_next, "Play next", onPlayNextClick)
                    OptionItem(R.drawable.ic_add_to_playlist, "Add to playlist", onAddToPlaylistClick)
                }
            }
        )
    }
}

@Composable
private fun OptionItem(
    @DrawableRes iconResId: Int,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp ,vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Icon(
            painterResource(id = iconResId),
            contentDescription = text,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlaylist(
    songId: Int,
    showAddPlaylist: Boolean,
    onDismissClick: () -> Unit,
    onAddToPlaylist: (Int, Int) -> Unit,
    onCreatePlaylist: () -> Unit,
    playlists: List<PlayList>
) {
    val addPlaylistState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    if (showAddPlaylist) {
        ModalBottomSheet(
            onDismissRequest = onDismissClick,
            sheetState = addPlaylistState,
            containerColor = MaterialTheme.colorScheme.surface,
            scrimColor = Color.Black.copy(alpha = 0.6f),
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Add to Playlist",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    HorizontalDivider(color = Color.Gray , thickness = 1.dp)

                    Row(
                        modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton ( onClick = onCreatePlaylist, modifier = Modifier.size(40.dp)) {
                            Icon(
                                painterResource(id = R.drawable.ic_add_circle),
                                contentDescription = "Add playlist",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add New Playlist",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (playlists.isNotEmpty()) {
                        playlists.forEach { playlist ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onAddToPlaylist(playlist.id, songId)
                                        onDismissClick()
                                        Toast.makeText(context, "Added to ${playlist.name}", Toast.LENGTH_SHORT).show()
                                    },
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(playlist.coverUrl ?: "https://cdn2.tuoitre.vn/thumb_w/480/2020/6/16/photo-1-15923021035102079282540.jpg")
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Playlist Cover",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    text = playlist.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 16.dp)
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlaylist(
    showCreatePlaylist: Boolean,
    onDismissClick: () -> Unit,
    onCreatePlaylist: (String) -> Unit,
    onDone: () -> Unit,
) {
    val createState = rememberModalBottomSheetState()
    var playlistName by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    if (showCreatePlaylist) {
        ModalBottomSheet(
            onDismissRequest = onDismissClick,
            sheetState = createState,
            containerColor = MaterialTheme.colorScheme.surface,
            scrimColor = Color.Black.copy(alpha = 0.6f),
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "New Playlist",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline , thickness = 1.dp)
                    OutlinedTextField(
                        value = playlistName,
                        onValueChange = { playlistName = it },
                        placeholder = {
                            Text("My Top 50 Songs",
                            style = MaterialTheme.typography.bodyMedium ,color = MaterialTheme.colorScheme.outline) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color(0xFFF0F0F0),
                            unfocusedContainerColor = Color(0xFFF0F0F0),
                            cursorColor = Color.Black,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedPlaceholderColor = Color.Gray,
                            unfocusedPlaceholderColor = Color.Gray
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    HorizontalDivider(color = Color.Gray , thickness = 1.dp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AppButton("Cancel",
                            onClick = {
                                onDone()
                                playlistName = ""
                            },
                            style = ButtonStyle.SECONDARY
                        )
                        AppButton("Create" ,
                            onClick = {
                                onDone()
                                onCreatePlaylist(playlistName)
                                playlistName = ""
                            },
                            style = ButtonStyle.PRIMARY)
                    }
                }
            }
        )
    }
}