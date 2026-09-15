# INFY Browser

A dark, futuristic Android web browser built on Android WebView, with Google
Search as its default search engine, a custom galaxy-themed homepage, tabs,
private browsing, and customizable shortcuts.

## What's included

- **Kotlin + Jetpack Compose** UI (single-activity architecture)
- **Real android.webkit.WebView** engine per tab (Chromium-based on modern
  Android), pooled so switching tabs doesn't reload pages
- Custom home screen: deep-black background, subtle animated starfield,
  minimal infinity-symbol watermark, neon-glow search box
- **Search / Chat / Images** mode switcher on the homepage
  - Search → `https://www.google.com/search?q=...`
  - Images → Google Images
  - Chat → INFY AI placeholder screen (ready to wire up to an AI API later)
- Smart address bar: detects a typed URL vs. a search query
- Full browser chrome: back, forward, reload, home, tabs, new tab, overflow menu
- **Tabs**: open/close/switch, tab-count badge, tab switcher grid screen
- **Private Browsing**: cookies disabled/cleared, no cache, no saved form data
  for private tabs
- Customizable homepage **shortcuts** (Gmail, YouTube, Drive, Google by
  default) — add or long-press-to-remove
- **Downloads** via Android's own `DownloadManager` (shows in the system
  Downloads app/notification)
- File upload support (`<input type="file">`) via the system file/camera picker
- HTTPS/SSL enforcement (fails closed on certificate errors — never bypasses
  Android's SSL validation), mixed content blocked
- Adaptive app icon (vector infinity glyph) and dark launcher theme

## Project structure

```
INFYBrowser/
├── settings.gradle.kts
├── build.gradle.kts
├── gradle.properties
├── gradle/wrapper/gradle-wrapper.properties
└── app/
    ├── build.gradle.kts
    ├── proguard-rules.pro
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/com/infy/browser/
        │   ├── MainActivity.kt
        │   ├── InfyApplication.kt
        │   ├── model/            (Tab, Shortcut, SearchMode)
        │   ├── viewmodel/        (BrowserViewModel — all app state)
        │   ├── ui/               (HomeScreen, BrowserScreen, TabSwitcherScreen, ChatPlaceholderScreen)
        │   ├── ui/components/    (AddressBar, BrowserBottomBar, StarField, dialogs)
        │   ├── ui/theme/         (Compose color/typography/theme)
        │   ├── webview/          (WebViewPool, clients, DownloadHandler)
        │   └── util/             (UrlUtils, ShortcutStore)
        └── res/                  (strings, colors, themes, icons, FileProvider paths)
```

## Building the APK without Android Studio

You don't need to install anything to get an installable APK — a GitHub
Actions workflow (`.github/workflows/build.yml`) is already included that
builds the app on GitHub's own servers.

1. **Create a free GitHub account** at github.com, if you don't have one.
2. **Create a new repository** (any name, e.g. `infy-browser`) — public or
   private both work.
3. **Upload this project**: on the repo page, click *Add file → Upload
   files*, then drag the whole `INFYBrowser` folder (including the hidden
   `.github` folder) into the browser window. If your browser only lets you
   drop individual files, unzip locally first and drag the folder — Chrome
   and Edge support dragging entire folders into GitHub's uploader. Commit
   the upload.
   - Note: GitHub's uploader can miss folders that start with a dot. If
     `.github/workflows/build.yml` doesn't show up in the repo afterward,
     use *Add file → Create new file*, type the path
     `.github/workflows/build.yml`, and paste in the contents of that file.
4. **Watch it build**: click the *Actions* tab in your repo — a workflow run
   should already be in progress (it starts automatically on upload). It
   takes a few minutes.
5. **Download the APK**: once the run finishes (green check), open it,
   scroll to *Artifacts*, and download `INFY-Browser-debug-apk`. That's a
   zip containing `app-debug.apk`.
6. **Install on your phone**: transfer `app-debug.apk` to your phone
   (email it to yourself, save via Google Drive, or USB-copy it), open it
   from a file manager, and allow "install from unknown sources" if
   prompted. That installs INFY Browser directly.

If you'd rather use Android Studio later, the steps in the section above
still apply — the two build paths don't conflict.

## Build instructions (with Android Studio)

1. **Open the project**: Launch Android Studio (Koala/2024.1 or newer
   recommended) → *Open* → select the `INFYBrowser` folder.
2. **Sync Gradle**: Android Studio should prompt automatically; otherwise
   click *File → Sync Project with Gradle Files*. This will download the
   Gradle 8.7 wrapper distribution and all dependencies (Compose, WebKit,
   DataStore) — an internet connection is required for this step.
3. **Build the project**: *Build → Make Project* (or `Ctrl+F9` /
   `Cmd+F9`), or from a terminal in the project root:
   ```
   ./gradlew assembleDebug
   ```
   (On first run, if `gradlew` isn't executable: `chmod +x gradlew`.)
4. **Generate the APK**: *Build → Build Bundle(s) / APK(s) → Build APK(s)*,
   or `./gradlew assembleDebug` for a debug APK / `./gradlew assembleRelease`
   for a signed-ready release build. The debug APK lands at
   `app/build/outputs/apk/debug/app-debug.apk`.
5. **Install on a phone**: enable *Developer options → USB debugging*,
   connect the device, and click *Run ▶* in Android Studio, or manually
   `adb install app/build/outputs/apk/debug/app-debug.apk`.

Minimum SDK 24 (Android 7.0), target/compile SDK 34.

## Troubleshooting the Gradle wrapper

This project includes `gradle/wrapper/gradle-wrapper.properties` (pointing at
Gradle 8.7) but not the binary `gradle-wrapper.jar`, since it's a compiled
binary that isn't practical to hand-author outside of Gradle itself. Android
Studio will usually detect this on first open and offer to regenerate the
wrapper automatically. If it doesn't:

- In Android Studio: *File → Settings → Build, Execution, Deployment → Gradle*
  and temporarily switch **Gradle JDK/distribution** to "Use Gradle from:
  local installation" (any Gradle 8.x you have installed), sync once, then
  optionally run `gradle wrapper --gradle-version 8.7` from the *Terminal*
  tab to regenerate `gradlew`/`gradlew.bat`/`gradle-wrapper.jar` for future
  command-line builds.
- Or, from a terminal with Gradle installed: run `gradle wrapper` in the
  project root before opening it in Android Studio.

## Notes and honest limitations

- **Private mode** disables cookie acceptance and clears cache/history/form
  data for private tabs, but Android's `WebView` does not offer a fully
  separate on-disk profile per tab the way desktop browsers do. For
  stronger per-tab isolation, a future version could move each private
  tab into its own process via `WebView.setDataDirectorySuffix()`
  (API 28+), which needs to be set before any WebView is created and so is
  a slightly larger architectural change.
- **Chat mode** is intentionally a placeholder screen only, as requested —
  no AI API is wired in yet.
- **App icon** ships as a vector drawable (infinity glyph on black) rather
  than a designed raster icon; swap in real PNG/adaptive-icon art in
  `res/mipmap-*` before a store release.
- The browser does **not** include or enable any feature to bypass network,
  parental, workplace, or school content restrictions, per the brief.
