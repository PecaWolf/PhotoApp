package cz.pecawolf.ui.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource

@Composable
fun Int.string(vararg formatArgs: Any): String = stringResource(this, *formatArgs)

fun Int.string(context: Context, vararg formatArgs: Any): String = context.getString(this, *formatArgs)

@Composable
fun Int.plural(count: Int): String = stringArrayResource(this)
    .let { it[count.coerceAtMost(it.lastIndex)] }

@Composable
fun Int.painter(): Painter = painterResource(this)

@Composable
fun ImageVector.painter(): Painter = rememberVectorPainter(this)