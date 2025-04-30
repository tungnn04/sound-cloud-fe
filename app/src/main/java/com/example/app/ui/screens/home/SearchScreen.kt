package com.example.app.ui.screens.home

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app.R
import com.example.app.model.Song
import com.example.app.ui.components.ListAlbum
import com.example.app.ui.components.ListArtist
import com.example.app.ui.components.ListSong
import com.example.app.ui.components.SongOptionMenu
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    searchViewModel: SearchViewModel = viewModel(factory = SearchViewModel.factory),
    onPlayClick: (Int) -> Unit,
    onPlayNextClick: (Int) -> Unit,
    currentSong: Song?,
    isPlaying: Boolean
) {
    val uiState by searchViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var songClick by remember { mutableStateOf<Song?>(null) }

    LaunchedEffect(true) {
        try {
            focusRequester.requestFocus()
        } catch (e: Exception) {
            Toast.makeText(
                context,
                e.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val handleSearch: () -> Unit = {
        coroutineScope.launch {
            try {
                searchViewModel.search()
                focusManager.clearFocus()
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    e.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    Column(
    ) {
        TopAppBar(
            title = {
                TextField(
                    value = uiState.searchText,
                    onValueChange = { searchViewModel.setSearchText(it) },
                    placeholder = {
                        Text("Search" ,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            handleSearch()
                        }
                    ),
                    trailingIcon = {
                        if (uiState.searchText.isNotEmpty()) {
                            IconButton(onClick = { searchViewModel.clearSearch() }) {
                                Icon(
                                    painterResource(id = R.drawable.ic_close),
                                    contentDescription = "Clear",
                                    tint = Color.White
                                )
                            }
                        }
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF311947),
                        unfocusedContainerColor = Color(0xFF311947),
                        focusedIndicatorColor = Color(0xFF311947),
                        unfocusedIndicatorColor = Color(0xFF311947),
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .focusRequester(focusRequester)

                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        painterResource(id = R.drawable.ic_back),
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .pointerInput(Unit) {
                    detectTapGestures {
                        focusManager.clearFocus()
                    }
                }
        ) {
            SongOptionMenu(
                song = songClick,
                onDismissClick = { songClick = null },
                onPlayNextClick = onPlayNextClick,
                onFavoriteClick = { songId, isFavorite ->
                    searchViewModel.favoriteChange(songId, isFavorite)
                },
                onAddToPlaylist = { songId, playlistId ->
                    searchViewModel.addSongToPlaylist(songId, playlistId)
                },
                onCreatePlaylist = { playlistName ->
                    searchViewModel.createPlaylist(playlistName)
                },
                playlists = uiState.playlists
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoryButton(
                    text = "Songs",
                    isSelected = uiState.searchCategory == SearchCategory.SONGS,
                    onClick = { searchViewModel.setSearchCategory(SearchCategory.SONGS) }
                )
                CategoryButton(
                    text = "Artists",
                    isSelected = uiState.searchCategory == SearchCategory.ARTISTS,
                    onClick = { searchViewModel.setSearchCategory(SearchCategory.ARTISTS) }
                )
                CategoryButton(
                    text = "Albums",
                    isSelected = uiState.searchCategory == SearchCategory.ALBUMS,
                    onClick = { searchViewModel.setSearchCategory(SearchCategory.ALBUMS) }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                uiState.searchClick && uiState.listSong.isEmpty() &&
                        uiState.listArtist.isEmpty() && uiState.listAlbum.isEmpty() -> {
                    NotFoundContent()
                }
                else -> {
                    SearchResultList(
                        uiState = uiState,
                        navController = navController,
                        onPlayClick = onPlayClick,
                        onMoreOptionClick = {
                            songClick = it
                        },
                        currentSong = currentSong,
                        isPlaying = isPlaying
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(25.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
        ),
        modifier = Modifier.padding(end = 8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun NotFoundContent() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.not_found),
            contentDescription = "Not Found",
            modifier = Modifier.size(160.dp)
        )
        Text(
            text = "Not Found",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = "Sorry the keyword you entered cannot be found, please check again or search with another keyword",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun SearchResultList(uiState: SearchUiState, navController: NavController, onPlayClick: (Int) -> Unit,
                     onMoreOptionClick: (Song) -> Unit, currentSong: Song?, isPlaying: Boolean) {
    when (uiState.searchCategory) {
        SearchCategory.SONGS -> {
            ListSong(uiState.listSong, onMoreOptionClick ,onPlayClick, currentSong, isPlaying)
        }
        SearchCategory.ARTISTS -> {
            ListArtist(uiState.listArtist, navController)
        }
        SearchCategory.ALBUMS -> {
            ListAlbum(uiState.listAlbum, navController)
        }
    }
}