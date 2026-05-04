package cz.pecawolf.ui.screens.example

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cz.pecawolf.presentation.viewmodel.ExampleViewModel
import cz.pecawolf.presentation.viewmodel.ExampleViewModel.UiState
import cz.pecawolf.presentation.viewmodel.ExampleViewModel.Event
import cz.pecawolf.presentation.viewmodel.ExampleViewModel.Effect
import org.koin.androidx.compose.koinViewModel

@Composable
fun ExampleRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: ExampleViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect {
            when (it) {
                is Effect.NavigateBack -> onNavigateBack()
            }
        }
    }

    ExampleScreen(
        modifier = modifier,
        uiState = uiState,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun ExampleScreen(
    uiState: UiState,
    onEvent: (Event) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
    ) { innerPadding: PaddingValues ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(items = uiState.items) { item ->
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clickable(onClick = { onEvent(Event.ItemClick(item)) }),
                    text = item,
                )
            }
        }
    }
}
