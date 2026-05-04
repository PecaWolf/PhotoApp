package cz.pecawolf.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import cz.pecawolf.domain.model.PhotoItem
import cz.pecawolf.presentation.R
import cz.pecawolf.presentation.theme.PhotoAppTheme
import io.github.aakira.napier.Napier

@Composable
fun PhotoCard(
    photo: PhotoItem,
    onClick: (() -> Unit)?,
    onFullScreenClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    isLoading: Boolean = true,
    hasError: Boolean = false,
    maxTags: Int = Int.MAX_VALUE,
    maxDescriptionLines: Int = Int.MAX_VALUE,
    colors: CardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
    ),
) {
    var _loading: Boolean by remember { mutableStateOf(isLoading) }
    var _error: Boolean by remember { mutableStateOf(hasError) }

    Card(
        modifier = modifier,
        onClick = { onClick?.invoke() },
        enabled = !_loading && !_error,
        colors = colors,
    ) {
        PhotoContent(
            isLoading = _loading,
            hasError = _error,
            photo = photo,
            onFullScreenClick = onFullScreenClick,
            maxDescriptionLines = maxDescriptionLines,
            maxTags = maxTags,
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

@Composable
fun PhotoContent(
    isLoading: Boolean,
    hasError: Boolean,
    photo: PhotoItem,
    onFullScreenClick: (() -> Unit)?,
    maxDescriptionLines: Int = Int.MAX_VALUE,
    maxTags: Int = Int.MAX_VALUE,
    onLoadImageSuccess: (AsyncImagePainter.State.Success) -> Unit,
    onLoadImageError: (AsyncImagePainter.State.Error) -> Unit,
    onLoadImage: (AsyncImagePainter.State.Loading) -> Unit,
) {
    Column(
        modifier = Modifier.padding(Dimensions.spaceSmall),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimensions.spaceSmall),
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceContainer),
        ) {
            val (imageRef, fullScreenButttonRef, loadingRef, errorRef) = createRefs()

            if (isLoading) CircularProgressIndicator(
                modifier = Modifier
                    .constrainAs(loadingRef) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .fillMaxSize(0.5f),
                strokeWidth = 8.dp,
            )
            if (hasError) Image(
                modifier = Modifier
                    .constrainAs(errorRef) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .alpha(0.33f)
                    .fillMaxSize(0.5f),
                painter = Icons.Default.ImageNotSupported.painter(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )
            AsyncImage(
                modifier = Modifier.constrainAs(imageRef) {
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                model = photo.imageUrl,
                contentDescription = "${photo.title} - ${photo.description}",
                contentScale = ContentScale.Crop,
                onSuccess = onLoadImageSuccess,
                onError = onLoadImageError,
                onLoading = onLoadImage,
            )
            if (onFullScreenClick != null && !isLoading && !hasError) {
                PaIconButtonPrimary(
                    modifier = Modifier
                        .constrainAs(fullScreenButttonRef) {
                            height = Dimension.value(Dimensions.buttonMinSize)
                            width = Dimension.value(Dimensions.buttonMinSize)
                            bottom.linkTo(parent.bottom, margin = Dimensions.spaceSmall)
                            end.linkTo(parent.end, margin = Dimensions.spaceSmall)
                        }
                        .wrapContentSize(),
                    painter = Icons.Default.OpenInFull.painter(),
                    onClick = onFullScreenClick,
                    contentDescription = R.string.photo_card_open_full_content_description.string(),
                )
            }
        }
        if (photo.title.isNotBlank()) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                text = photo.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        }
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            text = AnnotatedString.fromHtml(htmlString = photo.description),
            style = MaterialTheme.typography.titleMedium,
            maxLines = maxDescriptionLines,
            overflow = TextOverflow.Ellipsis,
        )
        AnimatedContent(targetState = photo.tags.size) {
            when (it) {
                0 -> Text(R.string.photo_card_no_tags_message.string())
                in 1..maxTags + 1 ->
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Dimensions.spaceSmall),
                        verticalArrangement = Arrangement.spacedBy(Dimensions.spaceSmall),
                    ) {
                        photo.tags
                            .forEach { tag ->
                                Text(
                                    modifier = Modifier
                                        .clip(MaterialTheme.shapes.extraLarge)
                                        .background(MaterialTheme.colorScheme.surfaceContainer)
                                        .padding(
                                            vertical = Dimensions.spaceXSmall,
                                            horizontal = Dimensions.spaceSmall,
                                        ),
                                    text = "#$tag",
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                    }

                else -> FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.spaceSmall),
                    verticalArrangement = Arrangement.spacedBy(Dimensions.spaceSmall),
                ) {
                    photo.tags
                        .take(maxTags)
                        .forEach { tag ->
                            Text(
                                modifier = Modifier
                                    .clip(MaterialTheme.shapes.extraLarge)
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .padding(
                                        vertical = Dimensions.spaceXSmall,
                                        horizontal = Dimensions.spaceSmall,
                                    ),
                                text = "#$tag",
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    Text(R.string.photo_card_more_tags.string(photo.tags.size - maxTags))
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
            onFullScreenClick = {},
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
            onFullScreenClick = {},
            isLoading = false,
            hasError = true,
        )
    }
}

@Preview
@Composable
private fun PhotoCardFinishedPreview() {
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
            onFullScreenClick = {},
            isLoading = false,
            maxTags = 5,
        )
    }
}

@Preview
@Composable
private fun PhotoCardFinishedNoTagsPreview() {
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
                tags = listOf(),
            ),
            onClick = {},
            onFullScreenClick = {},
            isLoading = false,
        )
    }
}