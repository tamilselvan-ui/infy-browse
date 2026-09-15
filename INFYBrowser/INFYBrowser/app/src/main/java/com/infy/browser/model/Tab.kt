package com.infy.browser.model

import java.util.UUID

/**
 * Lightweight, serializable-ish representation of a browser tab.
 * The actual android.webkit.WebView instance for this tab is kept
 * separately in [com.infy.browser.webview.WebViewPool], keyed by [id],
 * so that Tab itself stays cheap to copy and safe to hold in Compose state.
 */
data class Tab(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "New Tab",
    val url: String = HOME_URL,
    val isPrivate: Boolean = false,
    val isLoading: Boolean = false,
    val progress: Int = 0,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false
) {
    companion object {
        const val HOME_URL = "infy://home"
        const val CHAT_URL = "infy://chat"
    }

    val isHome: Boolean get() = url == HOME_URL
    val isChat: Boolean get() = url == CHAT_URL
}
