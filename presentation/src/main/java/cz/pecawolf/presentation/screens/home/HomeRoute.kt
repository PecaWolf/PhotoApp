package cz.pecawolf.presentation.screens.home

import android.text.Html
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import cz.pecawolf.domain.model.PhotoItem
import cz.pecawolf.presentation.components.Dimensions
import cz.pecawolf.presentation.components.painter
import cz.pecawolf.presentation.screens.home.HomeViewModel.UiState
import cz.pecawolf.presentation.screens.home.HomeViewModel.Event
import cz.pecawolf.presentation.theme.PhotoAppTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
) {
    val viewModel: HomeViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect {
            when (it) {
                else -> Unit
            }
        }
    }

    HomeScreen(
        modifier = modifier,
        uiState = uiState,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun HomeScreen(
    uiState: UiState,
    onEvent: (Event) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {},
    ) { innerPadding: PaddingValues ->
        AnimatedContent(
            targetState = uiState.loading
        ) { loading ->
            if (loading) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(Dimensions.buttonMinSize),
                    )
                }
            } else {
                PhotoList(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    uiState = uiState,
                    onEvent = onEvent,
                )
            }
        }
    }
}

@Composable
private fun PhotoList(
    uiState: UiState,
    onEvent: (Event) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimensions.spaceSmall),
    ) {
        items(items = uiState.photos) { photo ->
            PhotoCard(
                modifier = Modifier.fillMaxWidth(),
                photo = photo,
                onClick = { onEvent(Event.PhotoClick(photo)) },
            )
        }
    }
}

@Composable
fun PhotoCard(
    photo: PhotoItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = true,
    hasError: Boolean = false,
) {
    var _loading: Boolean by remember { mutableStateOf(isLoading) }
    var _error: Boolean by remember { mutableStateOf(hasError) }

    Card(
        modifier = modifier,
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.spaceSmall),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimensions.spaceSmall),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceContainer),
            ) {
                AnimatedContent(
                    modifier = Modifier
                        .fillMaxSize(0.5f)
                        .align(Alignment.Center),
                    targetState = _loading to _error,
                ) { (loading, error) ->
                    if (loading) CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(Dimensions.buttonMinSize),
                        strokeWidth = 8.dp,
                    )
                    if (error) Image(
                        modifier = Modifier
                            .matchParentSize()
                            .alpha(0.33f),
                        painter = Icons.Default.ImageNotSupported.painter(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                    )
                }
                AsyncImage(
                    modifier = Modifier.matchParentSize(),
                    model = photo.imageUrl,
                    contentDescription = "${photo.title} - ${photo.description}",
                    contentScale = ContentScale.Crop,
                )
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                text = photo.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                text = AnnotatedString.fromHtml(htmlString = photo.description),
                style = MaterialTheme.typography.titleMedium,
            )
            AnimatedVisibility(visible = photo.tags.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.spaceSmall),
                    verticalArrangement = Arrangement.spacedBy(Dimensions.spaceSmall),
                ) {
                    photo.tags.forEach { tag ->
                        Text(
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.extraLarge)
                                .background(MaterialTheme.colorScheme.surfaceContainer)
                                .padding(Dimensions.spaceXSmall),
                            text = "#$tag",
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PhotoCardPreview() {
    PhotoAppTheme {
        PhotoCard(
            photo = PhotoItem(
                title = "Title",
                link = "Link",
                imageUrl = "https://i.redd.it/a-random-stray-kitten-appeared-at-my-house-a-few-months-ago-v0-5l92v1v1afxc1.jpg?width=2252&format=pjpg&auto=webp&s=2d839d26c8f99ded0a8f39faf0dc249d263d6ba1",
                dateTaken = "Date Taken",
                description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                published = "Published",
                author = "Author",
                authorId = "Author ID",
                tags = listOf(
                    "Tag1",
                    "Tag2",
                    "Tag3",
                    "Tag4",
                    "Tag5",
                    "Tag6",
                    "Tag7",
                    "Tag8",
                    "Tag9",
                    "Tag10"
                ),
            ),
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun PhotoCardErrorPreview() {
    PhotoAppTheme {
        PhotoCard(
            photo = PhotoItem(
                title = "Title",
                link = "Link",
                imageUrl = "https://i.redd.it/a-random-stray-kitten-appeared-at-my-house-a-few-months-ago-v0-5l92v1v1afxc1.jpg?width=2252&format=pjpg&auto=webp&s=2d839d26c8f99ded0a8f39faf0dc249d263d6ba1",
                dateTaken = "Date Taken",
                description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                published = "Published",
                author = "Author",
                authorId = "Author ID",
                tags = listOf(
                    "Tag1",
                    "Tag2",
                    "Tag3",
                    "Tag4",
                    "Tag5",
                    "Tag6",
                    "Tag7",
                    "Tag8",
                    "Tag9",
                    "Tag10"
                ),
            ),
            onClick = {},
            isLoading = false,
            hasError = true,
        )
    }
}

@Composable
@Preview
private fun HomeScreenPreview() {
    PhotoAppTheme {
        HomeScreen(
            uiState = UiState(
                loading = false,
                photos = List(2) {
                    PhotoItem(
                        title = "Title",
                        author = "Author",
                        authorId = "Author ID",
                        link = "https://example.com/image.jpg",
                        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                        dateTaken = "Date Taken",
                        published = "Published",
                        tags = listOf("Tag1", "Tag2", "Tag3"),
                        imageUrl = "https://i.redd.it/a-random-stray-kitten-appeared-at-my-house-a-few-months-ago-v0-5l92v1v1afxc1.jpg?width=2252&format=pjpg&auto=webp&s=2d839d26c8f99ded0a8f39faf0dc249d263d6ba1",
                    )
                },
                error = null,
            ),
            onEvent = {},
        )
    }
}