package com.infy.browser

import android.net.Uri
import android.os.Bundle
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.infy.browser.ui.HomeScreen
import com.infy.browser.ui.BrowserScreen
import com.infy.browser.ui.TabSwitcherScreen
import com.infy.browser.ui.components.InfyOverflowMenu
import com.infy.browser.ui.theme.InfyBrowserTheme
import com.infy.browser.viewmodel.BrowserViewModel
import com.infy.browser.viewmodel.Screen

class MainActivity : ComponentActivity() {

    private val viewModel: BrowserViewModel by viewModels()

    private var pendingFileCallback: ValueCallback<Array<Uri>>? = null

    private val fileChooserLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        val uris: Array<Uri>? = when {
            data == null || result.resultCode != RESULT_OK -> null
            data.clipData != null -> Array(data.clipData!!.itemCount) { i -> data.clipData!!.getItemAt(i).uri }
            data.data != null -> arrayOf(data.data!!)
            else -> null
        }
        pendingFileCallback?.onReceiveValue(uris)
        pendingFileCallback = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InfyBrowserTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    InfyApp(
                        viewModel = viewModel,
                        onShowFileChooser = { callback, params ->
                            pendingFileCallback?.onReceiveValue(null)
                            pendingFileCallback = callback
                            val intent = params.createIntent()
                            fileChooserLauncher.launch(intent)
                            true
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun InfyApp(
    viewModel: BrowserViewModel,
    onShowFileChooser: (ValueCallback<Array<Uri>>, WebChromeClient.FileChooserParams) -> Boolean
) {
    val screen by viewModel.screen.collectAsState()
    val tabs by viewModel.tabs.collectAsState()
    val activeTabId by viewModel.activeTabId.collectAsState()
    val searchMode by viewModel.searchMode.collectAsState()
    val shortcuts by viewModel.shortcuts.collectAsState()
    var menuExpanded by remember { mutableStateOf(false) }

    val activeTab = tabs.firstOrNull { it.id == activeTabId }

    // System back button: tab switcher -> browser/home; browser -> home; home -> exit.
    BackHandler(enabled = true) {
        when (screen) {
            Screen.TAB_SWITCHER -> viewModel.showBrowserForActiveTab()
            Screen.BROWSER -> viewModel.goHome()
            Screen.HOME -> { /* let the system handle it (exits the app) */ }
        }
    }

    when (screen) {
        Screen.HOME -> HomeScreen(
            searchMode = searchMode,
            shortcuts = shortcuts,
            isPrivateActive = activeTab?.isPrivate == true,
            onSearchModeChange = viewModel::setSearchMode,
            onSubmit = viewModel::submitAddressBarInput,
            onShortcutClick = viewModel::openShortcut,
            onShortcutRemove = viewModel::removeShortcut,
            onAddShortcut = viewModel::addShortcut
        )

        Screen.BROWSER -> {
            if (activeTab == null) {
                viewModel.newTab()
            } else {
                BrowserScreen(
                    tab = activeTab,
                    tabCount = tabs.size,
                    onTabUpdate = { transform -> viewModel.updateActiveTab { it.transform() } },
                    onSubmitUrl = viewModel::submitAddressBarInput,
                    onBack = viewModel::goHome,
                    onForward = { },
                    onHome = viewModel::goHome,
                    onShowTabs = viewModel::showTabSwitcher,
                    onNewTab = { viewModel.newTab() },
                    onShowMenu = { menuExpanded = true },
                    onFileChooser = onShowFileChooser
                )
                InfyOverflowMenu(
                    expanded = menuExpanded,
                    onDismiss = { menuExpanded = false },
                    onNewTab = { viewModel.newTab() },
                    onNewPrivateTab = { viewModel.newTab(isPrivate = true) },
                    onShare = { /* Sharing the current URL is a natural next step once a Context is threaded in */ }
                )
            }
        }

        Screen.TAB_SWITCHER -> TabSwitcherScreen(
            tabs = tabs,
            activeTabId = activeTabId,
            onSelect = { tab -> viewModel.selectTab(tab.id) },
            onClose = { tab -> viewModel.closeTab(tab.id) },
            onNewTab = { viewModel.newTab() },
            onNewPrivateTab = { viewModel.newTab(isPrivate = true) }
        )
    }
}
