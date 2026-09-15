package com.infy.browser.webview

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.webkit.CookieManager
import android.webkit.URLUtil
import android.widget.Toast
import com.infy.browser.R

/**
 * Wires WebView's setDownloadListener to Android's own DownloadManager so
 * files land in the normal system Downloads folder and show up in the
 * system download notifications, instead of the app trying to manage
 * downloads itself.
 */
object DownloadHandler {

    fun enqueue(
        context: Context,
        url: String,
        userAgent: String?,
        contentDisposition: String?,
        mimeType: String?
    ) {
        try {
            val fileName = URLUtil.guessFileName(url, contentDisposition, mimeType)
            val request = DownloadManager.Request(Uri.parse(url)).apply {
                setMimeType(mimeType)
                addRequestHeader("cookie", CookieManager.getInstance().getCookie(url))
                addRequestHeader("User-Agent", userAgent)
                setDescription(context.getString(R.string.app_name))
                setTitle(fileName)
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                setAllowedOverMetered(true)
                setAllowedOverRoaming(true)
            }

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
            Toast.makeText(context, R.string.download_started, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, e.localizedMessage ?: "Download failed", Toast.LENGTH_SHORT).show()
        }
    }
}
