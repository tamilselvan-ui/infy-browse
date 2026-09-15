package com.infy.browser

import android.app.Application
import com.infy.browser.webview.WebViewPool

class InfyApplication : Application() {
    override fun onTerminate() {
        WebViewPool.removeAll()
        super.onTerminate()
    }
}
