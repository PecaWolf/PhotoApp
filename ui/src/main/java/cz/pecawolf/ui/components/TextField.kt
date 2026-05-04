package cz.pecawolf.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign

@Composable
fun PaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    supportingText: String? = null,
    isError: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    labelColor: Color = MaterialTheme.colorScheme.primary,
    unfocusedColor: Color = MaterialTheme.colorScheme.secondary,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    cursorColor: Color = MaterialTheme.colorScheme.primary,
    textAlign: TextAlign = TextAlign.Start,
    imeAction: ImeAction = ImeAction.Default,
    maxLines: Int = Int.MAX_VALUE,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
) {
    var state by remember {
        mutableStateOf(
            TextFieldValue(
                text = value,
                selection = TextRange(value.length),
            )
        )
    }

    LaunchedEffect(value) {
        if (value != state.text) {
            val newSelectionStart = state.selection.start.coerceIn(0, value.length)
            val newSelectionEnd = state.selection.end.coerceIn(0, value.length)
            state = TextFieldValue(
                text = value,
                selection = TextRange(newSelectionStart, newSelectionEnd),
            )
        }
    }

    val textStyle = TextStyle(
        textAlign = textAlign,
    )
    OutlinedTextField(
        modifier = modifier,
        value = state,
        onValueChange = {
            state = it
            onValueChange(it.text)
        },
        label = label?.let {
            {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
        },
        supportingText = supportingText?.let { { Text(it) } },
        isError = isError,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction,
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            focusedBorderColor = borderColor,
            unfocusedBorderColor = unfocusedColor,
            focusedLabelColor = labelColor,
            unfocusedLabelColor = unfocusedColor,
            cursorColor = cursorColor,
        ),
        textStyle = textStyle,
        singleLine = maxLines == 1,
        maxLines = maxLines,
        placeholder = (placeholder ?: label)?.let {
            {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = it,
                    color = textColor,
                    style = textStyle,
                )
            }
        },
        trailingIcon = trailingIcon,
        leadingIcon = leadingIcon,
        prefix = prefix,
        suffix = suffix,
        shape = MaterialTheme.shapes.extraLarge,
    )
}
