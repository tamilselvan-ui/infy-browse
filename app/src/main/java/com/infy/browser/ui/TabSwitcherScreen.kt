package com.infy.browser.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.infy.browser.R
import com.infy.browser.model.Tab
import com.infy.browser.ui.theme.InfyNeonBlue
import com.infy.browser.ui.theme.InfyNeonPurple
import com.infy.browser.ui.theme.InfySurface
import com.infy.browser.ui.theme.InfySurfaceAlt
import com.infy.browser.ui.theme.InfyTextPrimary
import com.infy.browser.ui.theme.InfyTextSecondary
import com.infy.browser.util.UrlUtils

@Composable
fun TabSwitcherScreen(
    tabs: List<Tab>,
    activeTabId: String?,
    onSelect: (Tab) -> Unit,
    onClose: (Tab) -> Unit,
    onNewTab: () -> Unit,
    onNewPrivateTab: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                text = "Tabs",
                color = InfyTextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (tabs.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.tabs_empty), color = InfyTextSecondary)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(tabs, key = { it.id }) { tab ->
                        TabCard(
                            tab = tab,
                            isActive = tab.id == activeTabId,
                            onClick = { onSelect(tab) },
                            onClose = { onClose(tab) }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FloatingActionButton(
                    onClick = onNewTab,
                    containerColor = InfyNeonBlue,
                    contentColor = Color.Black,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null)
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(stringResource(R.string.nav_new_tab))
                    }
                }
                FloatingActionButton(
                    onClick = onNewPrivateTab,
                    containerColor = InfySurfaceAlt,
                    contentColor = InfyNeonPurple,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    ) {
                        Icon(Icons.Filled.Lock, contentDescription = null)
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(stringResource(R.string.menu_new_private_tab))
                    }
                }
            }
        }
    }
}

@Composable
private fun TabCard(tab: Tab, isActive: Boolean, onClick: () -> Unit, onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isActive) InfySurfaceAlt else InfySurface)
            .clickable(onClick = onClick)
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (tab.isPrivate) {
                Icon(
                    Icons.Filled.Lock,
                    contentDescription = null,
                    tint = InfyNeonPurple,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.size(4.dp))
            }
            Text(
                text = if (tab.isHome) "New Tab" else if (tab.isChat) "INFY AI" else tab.title,
                color = InfyTextPrimary,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onClose, modifier = Modifier.size(20.dp)) {
                Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.close_tab), tint = InfyTextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (tab.isHome) "∞" else if (tab.isChat) "∞" else UrlUtils.hostLabel(tab.url).take(1).uppercase(),
                color = InfyNeonBlue,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
