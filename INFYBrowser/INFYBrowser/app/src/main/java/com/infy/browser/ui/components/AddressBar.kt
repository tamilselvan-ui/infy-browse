package com.infy.browser.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Https
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import com.infy.browser.ui.theme.InfyNeonBlue
import com.infy.browser.ui.theme.InfySurfaceAlt
import com.infy.browser.ui.theme.InfyTextSecondary

@Composable
fun AddressBar(
    url: String,
    isLoading: Boolean,
    progress: Int,
    isPrivate: Boolean,
    onSubmit: (String) -> Unit,
    onReload: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    var editing by remember { mutableStateOf(false) }
    var fieldValue by remember(url, editing) {
        mutableStateOf(TextFieldValue(if (editing) url else displayUrl(url)))
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(InfySurfaceAlt, RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                progress = { progress / 100f },
                modifier = Modifier.size(16.dp),
                color = InfyNeonBlue,
                strokeWidth = 2.dp
            )
        } else {
            Icon(
                imageVector = if (url.startsWith("https://")) Icons.Filled.Https else Icons.Filled.Warning,
                contentDescription = null,
                tint = if (url.startsWith("https://")) InfyNeonBlue else InfyTextSecondary,
                modifier = Modifier.size(16.dp)
            )
        }

        Box(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
            TextField(
                value = fieldValue,
                onValueChange = { fieldValue = it },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = InfyNeonBlue
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Go,
                    keyboardType = KeyboardType.Uri
                ),
                keyboardActions = KeyboardActions(
                    onGo = {
                        onSubmit(fieldValue.text)
                        editing = false
                        focusManager.clearFocus()
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { state ->
                        if (state.isFocused) {
                            editing = true
                            fieldValue = TextFieldValue(url, selection = TextRange(0, url.length))
                        } else {
                            editing = false
                        }
                    }
            )
        }

        if (isPrivate) {
            Text(
                text = "PRIVATE",
                style = MaterialTheme.typography.labelSmall,
                color = InfyNeonBlue,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                modifier = Modifier.padding(end = 4.dp)
            )
        }

        IconButton(onClick = onReload, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Filled.Refresh, contentDescription = "Reload", tint = InfyTextSecondary)
        }
    }
}

private fun displayUrl(url: String): String = url
    .removePrefix("https://")
    .removePrefix("http://")
    .removePrefix("www.")
