@file:OptIn(ExperimentalMaterial3Api::class)

package cz.pecawolf.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cz.pecawolf.presentation.model.PhotoModel
import cz.pecawolf.ui.components.PaBottomSheet
import cz.pecawolf.ui.components.PhotoContent
import io.github.aakira.napier.Napier

@Composable
fun PhotoDetailBottomSheet(
    item: PhotoModel?,
    onDismissRequest: () -> Unit,
    onFullScreenClick: (PhotoModel) -> Unit,
) {
    PaBottomSheet(
        target = item,
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
        ),
        onDismissRequest = onDismissRequest,
    ) { photo ->
        var _loading: Boolean by remember { mutableStateOf(true) }
        var _error: Boolean by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState()),
        ) {
            PhotoContent(
                isLoading = _loading,
                hasError = _error,
                photo = photo,
                onFullScreenClick = { onFullScreenClick(photo) },
                onLoadImageSuccess = {
                    Napier.d { "onSuccess(): ${photo.title}" }
                    _loading = false
                    _error = false
                },
                onLoadImageError = {
                    Napier.w(it.result.throwable) { "onError(): ${photo.title}, " }
                    _loading = false
                    _error = true
                },
                onLoadImage = {
                    Napier.v { "onLoading(): ${photo.title}" }
                    _loading = true
                    _error = false
                },
            )
        }
    }
}
