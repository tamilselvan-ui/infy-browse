package com.infy.browser.webview

import android.graphics.Bitmap
import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

/**
 * Reports page lifecycle + navigation state back to the tab that owns this
 * WebView, and enforces basic HTTPS/SSL safety instead of silently trusting
 * bad certificates.
 */
class InfyWebViewClient(
    private val onPageStarted: (String) -> Unit,
    private val onPageFinished: (String, canGoBack: Boolean, canGoForward: Boolean) -> Unit,
    private val onError: (String) -> Unit
) : WebViewClient() {

    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        onPageStarted(url)
    }

    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
        onPageFinished(url, view.canGoBack(), view.canGoForward())
    }

    override fun onReceivedError(
        view: WebView,
        request: WebResourceRequest,
        error: WebResourceError
    ) {
        super.onReceivedError(view, request, error)
        if (request.isForMainFrame) {
            onError(error.description?.toString() ?: "Unknown error")
        }
    }

    // Never silently bypass certificate problems: always fail closed on SSL errors,
    // in line with normal browser security practice.
    override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
        handler.cancel()
        onError("Connection is not private (SSL error)")
    }

    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        // Let the WebView handle standard http/https navigation itself.
        val scheme = request.url.scheme
        return scheme != "http" && scheme != "https"
    }
}
