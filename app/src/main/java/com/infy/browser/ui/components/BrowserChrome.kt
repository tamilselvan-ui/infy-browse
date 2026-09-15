package com.infy.browser.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.infy.browser.ui.theme.InfyNeonBlue
import com.infy.browser.ui.theme.InfyNearBlack
import com.infy.browser.ui.theme.InfyTextPrimary
import com.infy.browser.ui.theme.InfyTextSecondary

@Composable
fun BrowserBottomBar(
    canGoBack: Boolean,
    canGoForward: Boolean,
    tabCount: Int,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onHome: () -> Unit,
    onShowTabs: () -> Unit,
    onNewTab: () -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(InfyNearBlack)
            .padding(horizontal = 4.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack, enabled = canGoBack) {
            Icon(
                Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint = if (canGoBack) InfyTextPrimary else InfyTextSecondary.copy(alpha = 0.4f)
            )
        }
        IconButton(onClick = onForward, enabled = canGoForward) {
            Icon(
                Icons.Filled.ArrowForward,
                contentDescription = "Forward",
                tint = if (canGoForward) InfyTextPrimary else InfyTextSecondary.copy(alpha = 0.4f)
            )
        }
        IconButton(onClick = onHome) {
            Icon(Icons.Filled.Home, contentDescription = "Home", tint = InfyTextPrimary)
        }
        IconButton(onClick = onShowTabs) {
            BadgedBox(badge = {
                if (tabCount > 0) {
                    Badge(containerColor = InfyNeonBlue, contentColor = Color.Black) {
                        Text(tabCount.toString())
                    }
                }
            }) {
                Box(modifier = Modifier.size(22.dp), contentAlignment = Alignment.Center) {
                    Text(text = "▭", color = InfyTextPrimary)
                }
            }
        }
        IconButton(onClick = onNewTab) {
            Icon(Icons.Filled.Add, contentDescription = "New tab", tint = InfyTextPrimary)
        }
        IconButton(onClick = onMenu) {
            Icon(Icons.Filled.MoreVert, contentDescription = "Menu", tint = InfyTextPrimary)
        }
    }
}
