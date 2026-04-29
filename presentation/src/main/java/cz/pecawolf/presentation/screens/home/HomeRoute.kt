package cz.pecawolf.presentation.screens.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cz.pecawolf.presentation.screens.home.HomeViewModel.UiState
import cz.pecawolf.presentation.screens.home.HomeViewModel.Event
import cz.pecawolf.presentation.screens.home.HomeViewModel.Effect
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: HomeViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect {
            when (it) {
                is Effect.NavigateBack -> onNavigateBack()
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
    ) { innerPadding: PaddingValues ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(items = uiState.items) { item ->
                PhotoCard(
                    item = item,
                    onClick = { onEvent(Event.ItemClick(item)) }
                )
            }
        }
    }
}

@Composable
fun PhotoCard(
    item: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {

    }
}