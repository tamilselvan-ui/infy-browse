package com.infy.browser.ui

import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.infy.browser.model.Tab
import com.infy.browser.ui.components.AddressBar
import com.infy.browser.ui.components.BrowserBottomBar
import com.infy.browser.webview.InfyWebChromeClient
import com.infy.browser.webview.InfyWebViewClient
import com.infy.browser.webview.WebViewPool
import com.infy.browser.ui.theme.InfyNeonBlue

@Composable
fun BrowserScreen(
    tab: Tab,
    tabCount: Int,
    onTabUpdate: (Tab.() -> Tab) -> Unit,
    onSubmitUrl: (String) -> Unit,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onHome: () -> Unit,
    onShowTabs: () -> Unit,
    onNewTab: () -> Unit,
    onShowMenu: () -> Unit,
    onFileChooser: (ValueCallback<Array<Uri>>, WebChromeClient.FileChooserParams) -> Boolean
) {
    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Box(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            AddressBar(
                url = tab.url,
                isLoading = tab.isLoading,
                progress = tab.progress,
                isPrivate = tab.isPrivate,
                onSubmit = onSubmitUrl,
                onReload = { WebViewPool.get(tab.id)?.reload() }
            )
        }

        if (tab.isLoading) {
            LinearProgressIndicator(
                progress = { tab.progress / 100f },
                modifier = Modifier.fillMaxWidth().height(2.dp),
                color = InfyNeonBlue,
                trackColor = Color.Transparent
            )
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when {
                tab.isChat -> ChatPlaceholderScreen()
                else -> AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        val webView = WebViewPool.getOrCreate(tab.id, ctx, tab.isPrivate)
                        webView.webViewClient = InfyWebViewClient(
                            onPageStarted = { url ->
                                onTabUpdate { copy(url = url, isLoading = true) }
                            },
                            onPageFinished = { url, canBack, canForward ->
                                onTabUpdate {
                                    copy(
                                        url = url,
                                        isLoading = false,
                                        canGoBack = canBack,
                                        canGoForward = canForward
                                    )
                                }
                            },
                            onError = { /* surfaced via title/URL bar already */ }
                        )
                        webView.webChromeClient = InfyWebChromeClient(
                            onProgress = { p -> onTabUpdate { copy(progress = p) } },
                            onTitle = { t -> onTabUpdate { copy(title = t) } },
                            onShowFileChooser = onFileChooser
                        )
                        if (webView.url == null || webView.url == "about:blank") {
                            webView.loadUrl(tab.url)
                        }
                        webView
                    },
                    update = { webView ->
                        if (webView.url != tab.url && !tab.isLoading) {
                            webView.loadUrl(tab.url)
                        }
                    }
                )
            }
        }

        BrowserBottomBar(
            canGoBack = tab.canGoBack,
            canGoForward = tab.canGoForward,
            tabCount = tabCount,
            onBack = {
                val webView = WebViewPool.get(tab.id)
                if (webView?.canGoBack() == true) webView.goBack() else onBack()
            },
            onForward = { WebViewPool.get(tab.id)?.goForward() },
            onHome = onHome,
            onShowTabs = onShowTabs,
            onNewTab = onNewTab,
            onMenu = onShowMenu
        )
    }
}
