package com.infy.browser.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.infy.browser.model.SearchMode
import com.infy.browser.model.Shortcut
import com.infy.browser.model.Tab
import com.infy.browser.util.ShortcutStore
import com.infy.browser.util.UrlUtils
import com.infy.browser.webview.WebViewPool
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class Screen { HOME, BROWSER, TAB_SWITCHER }

class BrowserViewModel(application: Application) : AndroidViewModel(application) {

    private val shortcutStore = ShortcutStore(application)

    private val _tabs = MutableStateFlow<List<Tab>>(emptyList())
    val tabs: StateFlow<List<Tab>> = _tabs.asStateFlow()

    private val _activeTabId = MutableStateFlow<String?>(null)
    val activeTabId: StateFlow<String?> = _activeTabId.asStateFlow()

    private val _screen = MutableStateFlow(Screen.HOME)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val _searchMode = MutableStateFlow(SearchMode.SEARCH)
    val searchMode: StateFlow<SearchMode> = _searchMode.asStateFlow()

    private val _shortcuts = MutableStateFlow<List<Shortcut>>(Shortcut.defaults())
    val shortcuts: StateFlow<List<Shortcut>> = _shortcuts.asStateFlow()

    val activeTab: Tab?
        get() = _tabs.value.firstOrNull { it.id == _activeTabId.value }

    init {
        viewModelScope.launch {
            shortcutStore.shortcuts.collect { _shortcuts.value = it }
        }
    }

    // ---- Tab management -------------------------------------------------

    fun newTab(isPrivate: Boolean = false, url: String = Tab.HOME_URL, goToBrowserScreen: Boolean = false) {
        val tab = Tab(url = url, isPrivate = isPrivate, title = if (url == Tab.HOME_URL) "New Tab" else url)
        _tabs.value = _tabs.value + tab
        _activeTabId.value = tab.id
        _screen.value = if (goToBrowserScreen && url != Tab.HOME_URL) Screen.BROWSER else if (url == Tab.HOME_URL) Screen.HOME else Screen.BROWSER
    }

    fun closeTab(tabId: String) {
        val tab = _tabs.value.firstOrNull { it.id == tabId } ?: return
        WebViewPool.remove(tabId, tab.isPrivate)
        val remaining = _tabs.value.filterNot { it.id == tabId }
        _tabs.value = remaining

        if (_activeTabId.value == tabId) {
            val next = remaining.lastOrNull()
            _activeTabId.value = next?.id
            _screen.value = if (next == null) Screen.HOME else _screen.value
        }
    }

    fun selectTab(tabId: String) {
        _activeTabId.value = tabId
        val tab = _tabs.value.firstOrNull { it.id == tabId }
        _screen.value = if (tab?.isHome == true) Screen.HOME else Screen.BROWSER
    }

    fun updateActiveTab(transform: (Tab) -> Tab) {
        val id = _activeTabId.value ?: return
        _tabs.value = _tabs.value.map { if (it.id == id) transform(it) else it }
    }

    // ---- Navigation -------------------------------------------------------

    fun goHome() {
        val id = _activeTabId.value
        if (id == null) {
            newTab()
        } else {
            updateActiveTab { it.copy(url = Tab.HOME_URL, title = "New Tab", isLoading = false) }
            _screen.value = Screen.HOME
        }
    }

    fun openChat() {
        val id = _activeTabId.value
        if (id == null) {
            newTab(url = Tab.CHAT_URL, goToBrowserScreen = true)
        } else {
            updateActiveTab { it.copy(url = Tab.CHAT_URL, title = "INFY AI") }
            _screen.value = Screen.BROWSER
        }
    }

    fun submitAddressBarInput(input: String) {
        if (input.isBlank()) return
        val destination = when (_searchMode.value) {
            SearchMode.CHAT -> {
                openChat()
                return
            }
            SearchMode.IMAGES -> UrlUtils.resolveInput(input, imagesMode = true)
            SearchMode.SEARCH -> UrlUtils.resolveInput(input, imagesMode = false)
        }
        navigateActiveTab(destination)
    }

    fun navigateActiveTab(url: String) {
        if (_activeTabId.value == null) {
            newTab(url = url, goToBrowserScreen = true)
        } else {
            updateActiveTab { it.copy(url = url, isLoading = true) }
            _screen.value = Screen.BROWSER
        }
    }

    fun openShortcut(shortcut: Shortcut) = navigateActiveTab(shortcut.url)

    fun setSearchMode(mode: SearchMode) {
        _searchMode.value = mode
    }

    fun showTabSwitcher() {
        _screen.value = Screen.TAB_SWITCHER
    }

    fun showBrowserForActiveTab() {
        _screen.value = if (activeTab?.isHome == true) Screen.HOME else Screen.BROWSER
    }

    // ---- Shortcuts ----------------------------------------------------------

    fun addShortcut(label: String, url: String) {
        viewModelScope.launch { shortcutStore.addShortcut(label, UrlUtils.normalizeUrl(url)) }
    }

    fun removeShortcut(shortcut: Shortcut) {
        viewModelScope.launch { shortcutStore.removeShortcut(shortcut) }
    }

    override fun onCleared() {
        WebViewPool.removeAll()
        super.onCleared()
    }
}
