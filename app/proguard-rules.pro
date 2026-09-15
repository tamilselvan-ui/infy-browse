# INFY Browser ProGuard / R8 rules

# Keep WebView JavaScript interfaces (none exposed by default, but keep the
# annotation available in case one is added later).
-keepattributes JavascriptInterface
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Keep model classes used for simple JSON-ish serialization in DataStore.
-keep class com.infy.browser.model.** { *; }

# Compose
-dontwarn androidx.compose.**
