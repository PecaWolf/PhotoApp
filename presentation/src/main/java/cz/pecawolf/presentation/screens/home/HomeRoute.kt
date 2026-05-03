package cz.pecawolf.presentation.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cz.pecawolf.domain.model.PhotoItem
import cz.pecawolf.presentation.components.Dimensions
import cz.pecawolf.presentation.components.PaTopAppBar
import cz.pecawolf.presentation.components.PhotoCard
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
        topBar = {
            PaTopAppBar(
                title = uiState.title,
                subtitle = uiState.subtitle,
                actions = {

                },
            )
        },
    ) { innerPadding: PaddingValues ->
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            isRefreshing = uiState.loading,
            onRefresh = { onEvent(Event.Refresh) },
        ) {
            PhotoList(
                modifier = Modifier
                    .fillMaxSize(),
                uiState = uiState,
                onEvent = onEvent,
            )
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
        contentPadding = PaddingValues(all = Dimensions.spaceMedium),
    ) {
        items(items = uiState.photos) { photo ->
            PhotoCard(
                modifier = Modifier.fillMaxWidth(),
                photo = photo,
                onClick = { onEvent(Event.PhotoClick(photo)) },
                onFullScreenClick = { onEvent(Event.PhotoFullScreenClick(photo)) },
            )
        }
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
            ),
            onEvent = {},
        )
    }
}