package com.infy.browser.util

import android.net.Uri
import android.util.Patterns

/**
 * Decides whether raw address-bar input should be treated as a URL to
 * navigate to directly, or as a query to hand off to Google Search, and
 * builds the resulting navigation URL.
 */
object UrlUtils {

    fun isLikelyUrl(input: String): Boolean {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return false

        // Explicit scheme -> definitely a URL.
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) return true

        // Contains a space -> definitely a search query.
        if (trimmed.contains(" ")) return false

        // "localhost" or bare IP addresses with an optional port.
        if (trimmed.startsWith("localhost") || Patterns.IP_ADDRESS.matcher(trimmed.substringBefore(":")).matches()) {
            return true
        }

        // Looks like domain.tld (optionally with a path/port), no spaces, has a dot,
        // and the part before the first dot/slash isn't empty.
        val domainLike = Regex(
            "^[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)+(:[0-9]+)?(/.*)?$"
        )
        return domainLike.matches(trimmed)
    }

    fun normalizeUrl(input: String): String {
        val trimmed = input.trim()
        return if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            trimmed
        } else {
            "https://$trimmed"
        }
    }

    fun googleSearchUrl(query: String): String {
        return "https://www.google.com/search?q=" + Uri.encode(query.trim())
    }

    fun googleImagesUrl(query: String): String {
        return "https://www.google.com/search?tbm=isch&q=" + Uri.encode(query.trim())
    }

    /**
     * Resolves free-form address-bar text into a navigable URL, given the
     * currently selected search mode (Search vs Images). Chat mode is
     * handled separately by the caller since it doesn't navigate a WebView.
     */
    fun resolveInput(input: String, imagesMode: Boolean): String {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return ""
        return if (isLikelyUrl(trimmed)) {
            normalizeUrl(trimmed)
        } else if (imagesMode) {
            googleImagesUrl(trimmed)
        } else {
            googleSearchUrl(trimmed)
        }
    }

    fun hostLabel(url: String): String {
        return try {
            Uri.parse(url).host ?: url
        } catch (e: Exception) {
            url
        }
    }
}
