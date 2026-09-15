package com.infy.browser.model

data class Shortcut(
    val id: String,
    val label: String,
    val url: String,
    val isDefault: Boolean = false
) {
    companion object {
        fun defaults(): List<Shortcut> = listOf(
            Shortcut("gmail", "Gmail", "https://mail.google.com", isDefault = true),
            Shortcut("youtube", "YouTube", "https://www.youtube.com", isDefault = true),
            Shortcut("drive", "Drive", "https://drive.google.com", isDefault = true),
            Shortcut("google", "Google", "https://www.google.com", isDefault = true)
        )
    }
}
