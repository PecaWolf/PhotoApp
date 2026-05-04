package cz.pecawolf.presentation.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.automirrored.filled.ViewQuilt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import cz.pecawolf.domain.model.PhotoItem
import cz.pecawolf.presentation.components.Dimensions
import cz.pecawolf.presentation.components.PaButtonSecondary
import cz.pecawolf.presentation.components.PaIconButtonTertiary
import cz.pecawolf.presentation.components.PaSwitchButton
import cz.pecawolf.presentation.components.PaTextField
import cz.pecawolf.presentation.components.PaTopAppBar
import cz.pecawolf.presentation.components.PhotoCard
import cz.pecawolf.presentation.components.painter
import cz.pecawolf.presentation.screens.home.HomeViewModel.UiState
import cz.pecawolf.presentation.screens.home.HomeViewModel.Event
import cz.pecawolf.presentation.theme.PhotoAppTheme
import org.koin.androidx.compose.koinViewModel

private const val MAX_TAGS_DISPLAYED_PER_ITEM = 5
private const val MAX_DESCRIPTION_LINES_DISPLAYED_PER_ITEM = 5

@Composable
fun HomeRoute(
    onNavigateToItemFullScreen: (PhotoItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: HomeViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    var displayedDetail: PhotoItem? by remember { mutableStateOf(null) }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect {
            when (it) {
                is HomeViewModel.Effect.NavigateToItemDetail -> displayedDetail = it.photo
                is HomeViewModel.Effect.NavigateToItemFullScreen -> onNavigateToItemFullScreen(it.photo)
            }
        }
    }

    HomeScreen(
        modifier = modifier,
        uiState = uiState,
        onEvent = viewModel::onEvent,
    )

    PhotoDetailBottomSheet(
        item = displayedDetail,
        onDismissRequest = { displayedDetail = null },
        onFullScreenClick = { viewModel.onEvent(Event.PhotoFullScreenClick(it)) },
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
                    PaSwitchButton(
                        modifier = Modifier.wrapContentSize(),
                        iconChecked = Icons.AutoMirrored.Default.ViewList.painter(),
                        iconUnchecked = Icons.AutoMirrored.Default.ViewQuilt.painter(),
                        checked = uiState.showList,
                        onCheckedChange = { onEvent(Event.OnShowListChange(it)) },
                    )
                },
            )
        },
    ) { innerPadding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Toolbar(
                uiState = uiState,
                onEvent = onEvent,
            )
            PullToRefreshBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                isRefreshing = uiState.loading,
                onRefresh = { onEvent(Event.Refresh) },
            ) {
                AnimatedContent(
                    targetState = uiState.showList,
                    transitionSpec = {
                        if (uiState.showList) {
                            slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                        } else {
                            slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
                        }
                    }
                ) {
                    if (it) {
                        PhotoList(
                            modifier = Modifier
                                .fillMaxSize(),
                            uiState = uiState,
                            onEvent = onEvent,
                        )
                    } else {
                        PhotoGrid(
                            modifier = Modifier
                                .fillMaxSize(),
                            uiState = uiState,
                            onEvent = onEvent,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Toolbar(
    uiState: UiState,
    onEvent: (Event) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = Dimensions.spaceXSmall),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = Dimensions.spaceMedium,
                    end = Dimensions.spaceXSmall
                ),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spaceSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PaTextField(
                modifier = Modifier
                    .wrapContentHeight()
                    .weight(1f),
                value = uiState.searchQuery,
                onValueChange = { onEvent(Event.OnSearchQueryChange(it)) },
                placeholder = "Search",
                imeAction = ImeAction.Search,
                leadingIcon = {
                    PaIconButtonTertiary(
                        painter = Icons.Default.Search.painter(),
                        contentDescription = "Search",
                        onClick = {},
                    )
                },
                trailingIcon = {
                    AnimatedVisibility(
                        visible = uiState.searchQuery.isNotEmpty(),
                    ) {
                        PaIconButtonTertiary(
                            painter = Icons.Default.Close.painter(),
                            onClick = { onEvent(Event.OnSearchQueryChange("")) },
                            contentDescription = "Clear search",
                        )
                    }
                },
            )

            PaSwitchButton(
                modifier = Modifier.wrapContentSize(),
                textChecked = "All",
                textUnchecked = "Any",
                checked = uiState.matchAllTags,
                onCheckedChange = { onEvent(Event.OnAllTagsChange(it)) },
            )
        }
        AnimatedVisibility(visible = uiState.searchTags.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .padding(top = Dimensions.spaceSmall)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.spacedBy(Dimensions.spaceXSmall),
                contentPadding = PaddingValues(horizontal = Dimensions.spaceMedium),
            ) {
                items(uiState.searchTags) {
                    PaButtonSecondary(
                        text = "#$it",
                        onClick = { onEvent(Event.OnDeleteTagClick(it)) },
                        trailingIcon = Icons.Default.Close.painter(),
                    )
                }
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
        contentPadding = PaddingValues(all = Dimensions.spaceMedium),
    ) {
        items(items = uiState.photos) { photo ->
            PhotoCard(
                modifier = Modifier.fillMaxWidth(),
                photo = photo,
                onClick = { onEvent(Event.PhotoClick(photo)) },
                onFullScreenClick = { onEvent(Event.PhotoFullScreenClick(photo)) },
                maxTags = MAX_TAGS_DISPLAYED_PER_ITEM,
                maxDescriptionLines = MAX_DESCRIPTION_LINES_DISPLAYED_PER_ITEM,
            )
        }
    }
}

@Composable
private fun PhotoGrid(
    uiState: UiState,
    onEvent: (Event) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = modifier,
        contentPadding = PaddingValues(all = Dimensions.spaceMedium),
        verticalItemSpacing = Dimensions.spaceSmall,
        horizontalArrangement = Arrangement.spacedBy(Dimensions.spaceSmall),
    ) {
        items(items = uiState.photos) { photo ->
            PhotoCard(
                photo = photo,
                onClick = { onEvent(Event.PhotoClick(photo)) },
                onFullScreenClick = { onEvent(Event.PhotoFullScreenClick(photo)) },
                modifier = Modifier.fillMaxWidth(),
                maxTags = MAX_TAGS_DISPLAYED_PER_ITEM,
                maxDescriptionLines = MAX_DESCRIPTION_LINES_DISPLAYED_PER_ITEM,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun ToolbarPreviewEmptyClosed() {
    PhotoAppTheme {
        Toolbar(
            uiState = UiState(
                searchExpanded = false,
                searchQuery = "",
            ),
            onEvent = {},
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun ToolbarPreviewEmptyOpen() {
    PhotoAppTheme {
        Toolbar(
            uiState = UiState(
                searchExpanded = true,
                searchQuery = "",
            ),
            onEvent = {},
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun ToolbarPreviewFilledClosed() {
    PhotoAppTheme {
        Toolbar(
            uiState = UiState(
                searchExpanded = false,
                searchQuery = "cats dogs pets",
            ),
            onEvent = {},
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun ToolbarPreviewFilledOpen() {
    PhotoAppTheme {
        Toolbar(
            uiState = UiState(
                searchExpanded = true,
                searchQuery = "cats dogs pets",
            ),
            onEvent = {},
        )
    }
}

@Composable
@Preview
private fun HomeScreenListPreview() {
    PhotoAppTheme {
        HomeScreen(
            uiState = UiState(
                loading = false,
                title = "Uploads from everyone",
                subtitle = "Last updated: 4. 5. 2026 10:00",
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
                showList = true,
                searchQuery = "cats dogs pets",
            ),
            onEvent = {},
        )
    }
}

@Composable
@Preview
private fun HomeScreenGridPreview() {
    PhotoAppTheme {
        HomeScreen(
            uiState = UiState(
                loading = false,
                title = "Uploads from everyone",
                subtitle = "Last updated: 4. 5. 2026 10:00",
                photos = List(6) { index ->
                    PhotoItem(
                        title = "Title",
                        author = "Author",
                        authorId = "Author ID",
                        link = "https://example.com/image.jpg",
                        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                        dateTaken = "Date Taken",
                        published = "Published",
                        tags = List(index + 2) { "TAG$it" },
                        imageUrl = "https://i.redd.it/a-random-stray-kitten-appeared-at-my-house-a-few-months-ago-v0-5l92v1v1afxc1.jpg?width=2252&format=pjpg&auto=webp&s=2d839d26c8f99ded0a8f39faf0dc249d263d6ba1",
                    )
                },
                showList = false,
                searchExpanded = true,
                searchQuery = "cats dogs pets",
            ),
            onEvent = {},
        )
    }
}