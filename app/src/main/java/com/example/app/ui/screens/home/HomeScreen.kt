package com.example.app.ui.screens.home

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.app.model.Album
import com.example.app.model.Artist
import com.example.app.model.Category
import com.example.app.model.Song
import com.example.app.ui.MusicScreen

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun HomeScreen(
    navController: NavController,
    onPlayClick: (Song) -> Unit ,
    homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.factory),
) {
    val scrollState = rememberScrollState()
    val uiSate by homeViewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(true) {
        try {
            homeViewModel.fetchHomeData()
        } catch (e: Exception) {
            Toast.makeText(
                context,
                e.message,
                Toast.LENGTH_SHORT
            ).show()
            Log.e("Error", e.message.toString())
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(bottom = 16.dp)
    ) {
        Header(uiSate.user?.fullName ?: "")
        SearchBar { navController.navigate(MusicScreen.SEARCH.name) }
        SectionTitle("New Songs")
        HorizontalItemList(uiSate.listSong) { song ->
            SongItem(song, onPlayClick)
        }
        SectionTitle("Top Albums")
        HorizontalItemList(uiSate.listAlbum) { album ->
            AlbumItem(album, navController)
        }
        SectionTitle("Popular Artists")
        HorizontalItemList(uiSate.listArtist) { artist ->
            ArtistItem(artist, navController)
        }
        SectionTitle("Categories")
        HorizontalItemList(uiSate.listCategory) { category ->
            CategoryItem(category, navController)
        }
    }


}

@Composable
fun Header(name: String) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = name,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = "What you want to hear today?",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun SearchBar(onSearchClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 32.dp, end = 48.dp, bottom = 16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable { onSearchClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Search", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Spacer(modifier = Modifier.height(24.dp))
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(bottom = 8.dp).padding(horizontal = 16.dp)
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun <T> HorizontalItemList(items: List<T>, itemContent: @Composable (T) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        items(items) { item ->
            itemContent(item)
        }
    }
}

@Composable
private fun SongItem(song: Song, onPlayClick: (Song) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(song.coverUrl)
                .crossfade(true)
                .build(),
            contentDescription = song.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { onPlayClick(song) }
        )
        Text(
            text = song.title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun AlbumItem(album: Album, navController: NavController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(album.coverUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Believer",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { navController.navigate("${MusicScreen.ALBUM_DETAIL.name}/${album.id}") }
        )
        Text(
            text = album.title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = album.artistName,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ArtistItem(artist: Artist, navController: NavController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(artist.profilePicture)
                .crossfade(true)
                .build(),
            contentDescription = artist.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .clickable { navController.navigate("${MusicScreen.ARTIST_DETAIL.name}/${artist.id}") }
                .padding(bottom = 8.dp)
        )
        Text(
            text = artist.name,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 4.dp)
        )
    }

}

@Composable
fun CategoryItem(category: Category, navController: NavController) {
    Column(horizontalAlignment = Alignment.Start) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(category.coverUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(220.dp, 120.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { navController.navigate("${MusicScreen.CATEGORY_DETAIL.name}/${category.id}") }
        )
        Text(
            text = category.name,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 4.dp)
        )
    }

}


