package com.infy.browser.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import com.infy.browser.R
import com.infy.browser.model.SearchMode
import com.infy.browser.model.Shortcut
import com.infy.browser.ui.components.AddShortcutDialog
import com.infy.browser.ui.components.StarFieldBackground
import com.infy.browser.ui.theme.InfyNeonBlue
import com.infy.browser.ui.theme.InfyNeonPurple
import com.infy.browser.ui.theme.InfySurfaceAlt
import com.infy.browser.ui.theme.InfyTextPrimary
import com.infy.browser.ui.theme.InfyTextSecondary

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomeScreen(
    searchMode: SearchMode,
    shortcuts: List<Shortcut>,
    isPrivateActive: Boolean,
    onSearchModeChange: (SearchMode) -> Unit,
    onSubmit: (String) -> Unit,
    onShortcutClick: (Shortcut) -> Unit,
    onShortcutRemove: (Shortcut) -> Unit,
    onAddShortcut: (label: String, url: String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        StarFieldBackground(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(72.dp))

            InfyWordmark()

            if (isPrivateActive) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.private_mode_on),
                    color = InfyNeonPurple,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            SearchModeTabs(current = searchMode, onChange = onSearchModeChange)

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(InfySurfaceAlt)
                    .glowBorder()
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Search, contentDescription = null, tint = InfyNeonBlue)
                    Spacer(modifier = Modifier.width(10.dp))
                    TextField(
                        value = query,
                        onValueChange = { query = it },
                        singleLine = true,
                        placeholder = {
                            Text(stringResource(R.string.search_hint), color = InfyTextSecondary)
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = InfyNeonBlue
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            onSubmit(query)
                            keyboardController?.hide()
                        }),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        onSubmit(query)
                        keyboardController?.hide()
                    }) {
                        Icon(Icons.Filled.Search, contentDescription = "Go", tint = InfyNeonBlue)
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = stringResource(R.string.shortcuts_title),
                color = InfyTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(shortcuts) { shortcut ->
                    ShortcutTile(
                        shortcut = shortcut,
                        onClick = { onShortcutClick(shortcut) },
                        onLongClick = { onShortcutRemove(shortcut) }
                    )
                }
                item {
                    AddShortcutTile(onClick = { showAddDialog = true })
                }
            }
        }
    }

    if (showAddDialog) {
        AddShortcutDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { label, url ->
                onAddShortcut(label, url)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun InfyWordmark() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "INFY",
            color = InfyTextPrimary,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "∞",
            color = InfyNeonBlue,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold
        )
    }
    Text(
        text = stringResource(R.string.tagline),
        color = InfyTextSecondary,
        fontSize = 13.sp
    )
}

@Composable
private fun SearchModeTabs(current: SearchMode, onChange: (SearchMode) -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(InfySurfaceAlt)
            .padding(4.dp)
    ) {
        ModeTab(stringResource(R.string.mode_search), current == SearchMode.SEARCH) { onChange(SearchMode.SEARCH) }
        ModeTab(stringResource(R.string.mode_chat), current == SearchMode.CHAT) { onChange(SearchMode.CHAT) }
        ModeTab(stringResource(R.string.mode_images), current == SearchMode.IMAGES) { onChange(SearchMode.IMAGES) }
    }
}

@Composable
private fun ModeTab(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) InfyNeonBlue.copy(alpha = 0.18f) else Color.Transparent)
            .combinedClickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = if (selected) InfyNeonBlue else InfyTextSecondary,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun ShortcutTile(shortcut: Shortcut, onClick: () -> Unit, onLongClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(InfySurfaceAlt)
                .combinedClickable(onClick = onClick, onLongClick = onLongClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = shortcut.label.take(1).uppercase(),
                color = InfyNeonBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = shortcut.label,
            color = InfyTextSecondary,
            fontSize = 11.sp,
            maxLines = 1
        )
    }
}

@Composable
private fun AddShortcutTile(onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(InfySurfaceAlt)
                .combinedClickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add shortcut", tint = InfyTextSecondary)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = "Add", color = InfyTextSecondary, fontSize = 11.sp)
    }
}

private fun Modifier.glowBorder(): Modifier = this.then(
    Modifier.background(Color.Transparent)
)
