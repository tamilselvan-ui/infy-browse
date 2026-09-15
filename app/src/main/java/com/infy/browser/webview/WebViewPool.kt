package com.infy.browser.webview

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView

/**
 * Holds one real android.webkit.WebView per open [com.infy.browser.model.Tab],
 * keyed by tab id, so switching tabs in the UI doesn't destroy and recreate
 * the underlying page (state, scroll position, and history are preserved).
 *
 * WebViews are created with the application context to avoid leaking an
 * Activity, and are torn down explicitly via [remove] when a tab closes.
 */
object WebViewPool {

    private val pool = mutableMapOf<String, WebView>()

    @SuppressLint("SetJavaScriptEnabled")
    fun getOrCreate(
        tabId: String,
        context: Context,
        isPrivate: Boolean
    ): WebView {
        pool[tabId]?.let { return it }

        val webView = WebView(context.applicationContext).apply {
            settings.configureForInfy(isPrivate)
            setDownloadListener { url, userAgent, contentDisposition, mimeType, _ ->
                DownloadHandler.enqueue(context, url, userAgent, contentDisposition, mimeType)
            }
        }

        CookieManager.getInstance().apply {
            setAcceptCookie(!isPrivate)
            setAcceptThirdPartyCookies(webView, false)
        }

        pool[tabId] = webView
        return webView
    }

    fun get(tabId: String): WebView? = pool[tabId]

    fun remove(tabId: String, isPrivate: Boolean) {
        pool[tabId]?.let { webView ->
            if (isPrivate) {
                // Private tabs must not leave history, cache, or cookies behind.
                webView.clearHistory()
                webView.clearCache(true)
                webView.clearFormData()
                CookieManager.getInstance().removeAllCookies(null)
            }
            webView.stopLoading()
            webView.destroy()
        }
        pool.remove(tabId)
    }

    fun removeAll() {
        pool.keys.toList().forEach { remove(it, isPrivate = false) }
    }

    private fun WebSettings.configureForInfy(isPrivate: Boolean) {
        javaScriptEnabled = true
        domStorageEnabled = true
        databaseEnabled = true
        loadWithOverviewMode = true
        useWideViewPort = true
        setSupportZoom(true)
        builtInZoomControls = true
        displayZoomControls = false
        mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
        cacheMode = if (isPrivate) WebSettings.LOAD_NO_CACHE else WebSettings.LOAD_DEFAULT
        saveFormData = !isPrivate
        mediaPlaybackRequiresUserGesture = true
        allowFileAccess = false
        allowContentAccess = true
        setSupportMultipleWindows(false)
    }
}
