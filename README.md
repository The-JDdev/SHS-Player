<div align="center">

# 🎬 SHS Player

### The Ultimate Open-Source Multimedia Engine for Android

**A privacy-first, dual-engine, feature-packed video & music player built with Jetpack Compose — forked from Next Player and supercharged with a Glassmorphism UI, Privacy Vault, Wi-Fi file transfer, IPTV live TV, an in-app music library, a VLC engine, universal downloader, and a QR share ecosystem.**

`v0.16.0` · Built from Bangladesh 🇧🇩 · by **Sajjad Hussain Shobuj (SHS)**

[![GitHub release](https://img.shields.io/github/v/release/The-JDdev/SHS-Player?style=for-the-badge&logo=github&color=4285F4)](https://github.com/The-JDdev/SHS-Player/releases/tag/v0.16.0)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge&logo=open-source-initiative&logoColor=white)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android%206.0%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://www.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2026.01.00-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Media3](https://img.shields.io/badge/AndroidX%20Media3-1.9.2-34A853?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/media/media3)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-ff69b4.svg?style=for-the-badge&logo=git&logoColor=white)](CONTRIBUTING.md)
[![Telegram](https://img.shields.io/badge/Telegram-Join%20Chat-2CA5E0?style=for-the-badge&logo=telegram&logoColor=white)](https://t.me/aamoviesofficial)

### 📥 Download v0.16.0

| APK | ABI | Best for | Size |
|:---:|:---:|:---|:---:|
| [`SHS-Player-v0.16.0-arm64-v8a.apk`](https://github.com/The-JDdev/SHS-Player/releases/download/v0.16.0/SHS-Player-v0.16.0-arm64-v8a.apk) | 64-bit ARM | Pixel, Samsung, **itel vision 1 pro**, modern phones | ~82 MB |
| [`SHS-Player-v0.16.0-armeabi-v7a.apk`](https://github.com/The-JDdev/SHS-Player/releases/download/v0.16.0/SHS-Player-v0.16.0-armeabi-v7a.apk) | 32-bit ARM | Older / low-end 32-bit phones | ~77 MB |
| [`SHS-Player-v0.16.0-universal.apk`](https://github.com/The-JDdev/SHS-Player/releases/download/v0.16.0/SHS-Player-v0.16.0-universal.apk) | All ABIs | Any device (largest, foolproof) | ~231 MB |

> **itel vision 1 pro?** → Use **arm64-v8a** (it's a 64-bit ARM device).

</div>

---

## 📑 Table of Contents

1. [Overview](#-overview)
2. [What's New in v0.16.0 (8-Phase Overhaul)](#-whats-new-in-v0160-8-phase-overhaul)
3. [Why SHS Player?](#-why-shs-player)
4. [Feature Comparison](#-feature-comparison)
5. [Detailed Feature Set](#-detailed-feature-set)
   - [🎥 Video Player](#-video-player)
   - [🎵 Music & Audio Player](#-music--audio-player)
   - [📺 IPTV / Live TV](#-iptv--live-tv)
   - [🔒 Privacy Vault](#-privacy-vault)
   - [📡 Wi-Fi File Transfer](#-wi-fi-file-transfer)
   - [📱 QR Scanner & Share](#-qr-scanner--share)
   - [🌐 Universal Downloader](#-universal-downloader)
   - [🪟 Glassmorphism UI Kit](#-glassmorphism-ui-kit)
   - [🗂️ Media Library](#-media-library)
   - [⚙️ Settings & Customization](#-settings--customization)
6. [Screenshots](#-screenshots)
7. [Architecture](#-architecture)
8. [Tech Stack](#-tech-stack)
9. [Project Structure](#-project-structure)
10. [Getting Started](#-getting-started)
11. [Building from Source](#-building-from-source)
12. [Development](#-development)
13. [Permissions Explained](#-permissions-explained)
14. [Privacy & Security](#-privacy--security)
15. [Internationalization](#-internationalization)
16. [Contributing](#-contributing)
17. [Code of Conduct](#-code-of-conduct)
18. [Security Policy](#-security-policy)
19. [License](#-license)
20. [Credits & Acknowledgements](#-credits--acknowledgements)
21. [Support the Project](#-support-the-project)
22. [Community](#-community)
23. [FAQ](#-faq)
24. [Roadmap](#-roadmap)

---

## 📖 Overview

**SHS Player** is a free, open-source, ad-free Android multimedia application that plays local and network video, audio, and live IPTV streams. It is a heavily-extended fork of [Next Player](https://github.com/anilbeesetti/nextplayer) by Anil Kumar Beesetti, re-architected and rebranded by **Sajjad Hussain Shobuj (SHS)** with a focus on three pillars:

- **Privacy-first design** — all media processing happens on-device. No tracking, no telemetry, no ads.
- **Maximum format coverage** — ships *two* playback engines (AndroidX Media3/ExoPlayer **and** LibVLC) so virtually every container, codec, and streaming protocol "just works".
- **Power-user features** — Privacy Vault, Wi-Fi file transfer, IPTV browsing, voice changer, AB-repeat, sleep timer, bookmarks, online subtitle search, video trim, and much more.

The app is written in **100% Kotlin** with **Jetpack Compose** for the entire UI layer, follows **Clean Architecture** across **12 Gradle modules**, uses **Hilt** for dependency injection, **Room** + **DataStore** for persistence, and is localised into **40+ languages**.

> **Latest version:** `0.16.0` (versionCode `55`) — [Download APK](https://github.com/The-JDdev/SHS-Player/releases/tag/v0.16.0)
> **Min Android:** 6.0 (API 23) · **Target:** Android 16 (API 36)
> **Application ID:** `dev.anilbeesetti.nextplayer` (retained for upstream compatibility)

---

## 🆕 What's New in v0.16.0 (8-Phase Overhaul)

v0.16.0 is the biggest release in the project's history — a single 8-phase master overhaul that touches the UI layer, audio engine, secret vault, network streaming, intent capture, video player core, P2P UX, and Live TV.

| Phase | Headline change | Key files |
|:---:|---|---|
| **1 Glassmorphism UI** | New `GlassCard`/`glassPanel`/`GlassIconButton` modifiers with vibrant Google Material palette (7 colors + 3 gradient trios) + PLAYit-style drag-drop customizable player controls row | `core/ui/components/glass/GlassModifiers.kt`, `Color.kt`, `CustomizablePlayerControlsRow.kt` |
| **2 Audio Engine** | Fixed queue bug (back-press stops audio, new file clears queue), fixed "Connecting to player…" loading screen bug, added Audio Playlists (Room v4→v5 migration with `PlaylistDao` + `PlaylistRepository`) | `AudioPlayerActivity.kt`, `AudioPlayerScreen.kt`, `PlaylistDao.kt`, `PlaylistEntity.kt`, `PlaylistRepository.kt`, `MediaDatabase.kt` |
| **3 Secret Vault** | Vault videos now force-open in internal `PlayerActivity` (no external leak); restore-to-gallery uses `MediaStore.createWriteRequest`; permanent delete does secure-erase overwrite | `PrivacyFolderScreen.kt` |
| **4 Universal Downloader** | `UniversalDownloader.kt` — yt-dlp wrapper via reflection + direct HTTP fallback; `updateYtDlpIfNeeded()` auto-updates the yt-dlp binary from GitHub releases every 24h; `DownloadStreamDialog.kt` UI | `feature/player/download/*` |
| **5 Open With** | `PlayerActivity` now declares `ACTION_VIEW` + `ACTION_SEND` intent filters for `video/*`, `audio/*`, `text/plain` URLs + 12 video extensions + 8 audio extensions; `normaliseIntentUri()` extracts URLs from `EXTRA_TEXT` | `feature/player/src/main/AndroidManifest.xml`, `PlayerActivity.kt` |
| **6 Player Core** | Low-end PiP fix (`FEATURE_PICTURE_IN_PICTURE` check before entering PiP, no more crashes on itel vision 1 pro); real audio delay via `DelayAudioProcessor` injected into ExoPlayer's audio chain | `PlayerActivity.kt`, `DelayAudioProcessor.kt`, `ShsRenderersFactory.kt`, `PlayerService.kt` |
| **7 P2P UX** | New `P2pPermissionSetupCard` composable with live status chips + stepped action button ("Tap to turn on Wi-Fi" → "Tap to turn on Location" → "Grant permissions" → "Start sharing") using ActivityResult API | `share/P2pPermissionSetupCard.kt`, `FileTransferScreen.kt` |
| **8 Live TV** | IPTV channels now categorized into Bangladesh · Sports · News · Popular · Free Channels tabs with heuristic resolver + 10 iptv-org playlists mapped to categories | `tv/M3uParser.kt`, `tv/WatchTvScreen.kt` |

**Build stats:** 17 files modified, 8 new files added, ~944 LOC additions across 12 modules. Built on Kotlin 2.3.0, AGP 8.13.2, Media3 1.9.2, LibVLC 3.6.0-eap5, Room 2.8.4 (v5 schema).

See the [v0.16.0 release notes](https://github.com/The-JDdev/SHS-Player/releases/tag/v0.16.0) for the full changelog.

---

## 🔥 Why SHS Player?

Most Android players fall into two camps: feature-rich but bloated/proprietary (MX Player, VLC), or clean but minimal (Next Player, Just Player). SHS Player sits in the rare middle — a clean, modern, Material 3 UI with power-user capabilities that no other open-source player currently bundles together:

| Capability | SHS Player | Next Player | VLC | MX Player |
|---|:---:|:---:|:---:|:---:|
| Dual engine (Media3 + VLC) | ✅ | ❌ | VLC only | Proprietary |
| Privacy Vault (encrypted on-device) | ✅ | ❌ | ❌ | ❌ |
| Wi-Fi file transfer (no internet) | ✅ | ❌ | ❌ | ❌ |
| IPTV / M3U live TV browser | ✅ | ❌ | ⚠️ (manual) | ❌ |
| Built-in music library | ✅ | ❌ | ✅ | ❌ |
| QR scanner + TrebleShot share | ✅ | ❌ | ❌ | ❌ |
| AB-repeat, sleep timer, bookmarks | ✅ | ❌ | ⚠️ | ✅ |
| Voice changer (pitch shift) | ✅ | ❌ | ❌ | ❌ |
| Video → audio converter | ✅ | ❌ | ❌ | ❌ |
| Video trim + reverse play | ✅ | ❌ | ❌ | ✅ (trim) |
| Online subtitle search | ✅ | ❌ | ❌ | ✅ |
| 100% open source, no ads | ✅ | ✅ | ✅ | ❌ |

---

## 🧩 Feature Comparison

### vs. upstream Next Player
SHS Player inherits Next Player's entire codebase (Media3 player, Material 3 UI, Compose architecture, settings screens, media library) and then layers on top:

- **New top-level navigation** — a 5-tab bottom bar (Videos · Music · Watch TV · Me · Telegram) instead of a single Videos screen.
- **A separate Music library** with files / folders / favourites / recent / playlists tabs and a dedicated audio player activity.
- **An IPTV module** that bundles 8 free iptv-org playlists and parses any `.m3u` URL.
- **A Privacy Vault** that moves media into app-private storage behind a SHA-256 password and optional biometric unlock.
- **A Wi-Fi file transfer server** (NanoHTTPD) that lets you push/pull files from any browser on the same LAN — no internet, no cloud.
- **A QR scanner** (CameraX + MLKit) and a TrebleShot-compatible QR share format for direct device-to-device transfers.
- **A VLC engine** for files / streams / codecs that ExoPlayer can't handle.
- **Voice changer, AB-repeat, sleep timer, bookmarks, favourites, video trim, video→audio, reverse play, screenshot, mirror, audio delay** in the player.
- **Online subtitle search** via OpenSubtitles (hardcoded API key).
- **Volume boost up to 200%** via `LoudnessEnhancer`.
- **A custom crash reporter** that captures the stack trace *and* `logcat` output and offers Share / Copy / Restart.
- **A premium Cupertino + Material 3 hybrid theme** with iOS-blue / iOS-pink accents and a pure-black OLED mode.
- **An animated splash screen** with logo pulse and gradient.
- **bKash / UPI / PayPal / Ko-fi / crypto donation** hooks tuned for the South-Asian developer community.

---

## 🚀 Detailed Feature Set

### 🎥 Video Player

The video player is the heart of SHS Player and the most feature-dense module in the project.

**Engines**
- **AndroidX Media3 / ExoPlayer 1.9.2** (default) — supports DASH, HLS, RTSP, SmoothStreaming, and every codec Android ships natively (H.263, H.264, H.265/HEVC, MPEG-4, VP8, VP9, AV1).
- **LibVLC 3.6.0-eap5** (fallback) — for files/streams/codecs that ExoPlayer can't handle. Configured with `:input-fast-seek`, `--no-drop-late-frames`, hardware acceleration with software fallback, and network caching. VLC also provides sample-accurate audio delay (microsecond precision) and a native 10-band equalizer that ExoPlayer can't match.

**Playback controls**
- Play / pause / seek bar with 10-second seek increments (configurable 1–60 s) and previous-sync seek parameters.
- Fast seek with configurable threshold.
- Skip silence (Media3's `setSkipSilenceEnabled`).
- Long-press to fast-forward at a configurable speed (0.2×–4.0×).
- Background playback with a branded media notification (`NOTIFICATION_ID = 1001` for ExoPlayer, `1002` for VLC).
- Picture-in-Picture with custom `RemoteAction`s and 32-bit-safe aspect-ratio coercion.
- Loop modes: off / loop all / loop one.
- Shuffle.

**Audio features**
- **Audio track selector** — pick any embedded audio track; remembers your last selection per file.
- **Audio delay / sync** — offset audio by −3000 ms to +3000 ms (VLC gives microsecond precision).
- **5/10-band audio equalizer** — uses Android's native `android.media.audiofx.Equalizer`. Save and load EQ profiles.
- **Voice changer** — 5 pitch presets (chipmunk, deep, robot, etc.) via `PlaybackParameters(pitch)`.
- **Volume boost up to 200%** via `LoudnessEnhancer`.
- **System volume panel** integration.

**Video features**
- **Video equalizer** — brightness / contrast / saturation in −100…+100 range, applied via `ColorMatrix` on a `TextureView` with Rec.709 luminance weights. Profiles are persisted.
- **Video content scale** — Best Fit / Stretch / Crop / 100%.
- **Zoom & pan** — pinch to zoom (0.25×–4×), drag to pan, with a pan/zoom lock to disambiguate gestures.
- **Rotation** — rotate the video in 90° increments; auto-rotate based on video orientation; or follow device sensor.
- **Screen mirror** (horizontal flip).
- **Screenshot capture** via `PixelCopy` from the `SurfaceView`.
- **Trim video** — in-app slider dialog that calls `MediaExtractor` + `MediaMuxer` to export a clip.
- **Video → audio** — extracts the audio track into an `.m4a` using `MediaExtractor` + `MediaMuxer`.
- **Reverse play** — plays the video backwards.

**Subtitle features**
- **Subtitle track selector** for embedded subs; **long-press** the subtitle icon to load an external subtitle file.
- **Online subtitle search** via OpenSubtitles (embedded and online tabs).
- **Subtitle text encoding** auto-detection via `juniversalchardet`; manual override in settings.
- **Subtitle styling** — font (Default / Monospace / Sans Serif / Serif), bold, size 10–60, background, embedded styles, or use the system caption style (opens Android's captioning settings).
- **Subtitle delay** sync.

**Gestures**
- **Horizontal drag** — seek (with configurable sensitivity 0.1–2.0).
- **Left vertical drag** — brightness (with optional system-level persistence).
- **Right vertical drag** — volume.
- **Pinch** — zoom.
- **Double tap** — configurable per zone: none / play-pause / fast-forward & rewind / both.
- **Long press** — toggle fast-forward at configurable speed.
- **Controls lock** — hide all controls and prevent touches (great for kids).

**Playlist & session**
- **Playlist panel** — reorder, remove, jump to any item.
- **Bookmarks** — save timestamped bookmarks per video (Room `BookmarkDao`).
- **Favourites** — star videos; filter the library by favourites.
- **AB-repeat** — loop a segment between two timestamps; polling-based playback loop.
- **Sleep timer** — stop playback after N minutes.
- **Resume** — pick up where you left off; configurable Yes/No.
- **Remember selections** — last audio/subtitle track, brightness, playback speed.
- **Recently played** — quick-resume from the library FAB.

**Intent API (MX Player compatible)**
- Accepts `android.intent.action.VIEW` with `video/*` and `audio/*`.
- Supports MX-Player-compatible intent extras via `PlayerApi` (title, position, headers, subtitles, etc.) — so third-party apps that target MX Player will work with SHS Player out of the box.

**Player controls customisation**
- `CustomizablePlayerControlsRow` — drag-to-reorder scaffold for control buttons (PLAYit-style).
- Control buttons position — left or right.
- Controller auto-hide timeout — 1–60 s (default applies).
- Hide player buttons background — transparent button backgrounds for a cleaner overlay.

---

### 🎵 Music & Audio Player

A standalone music library accessible from the bottom navigation, separate from the video library:

- **Five tabs** — Files / Folders / Favourites / Recent / Playlists.
- **List and grid layouts** with sort by title, duration, date, or size.
- **Custom playlists** persisted via `SharedPreferences`.
- **Dedicated `AudioPlayerActivity`** — separate from `PlayerActivity` (the video host) to avoid lifecycle conflicts.
- **Rotating vinyl album art** — extracted on-the-fly via `MediaMetadataRetriever`.
- **Audio visualiser** — real `android.media.audiofx.Visualizer` waveform.
- **Queue bottom sheet** with drag-to-reorder.
- **Audio settings dialog** — speed, skip-silence, EQ.
- **Notification playback controls**.
- **ServiceConnection leak fix** — explicit `isBound` tracking to prevent the crash that plagues many Media3 audio apps.

---

### 📺 IPTV / Live TV

A full IPTV browser (`WatchTvScreen` + `M3uParser`):

- **M3U parser** handles `http`, `content`, and `file` sources; extracts `tvg-logo`, `group-title`, `tvg-id`, `tvg-name`.
- Supports `http`, `rtmp`, `rtsp`, and `udp` stream URLs.
- Sends User-Agent `SHSPlayer/1.4` for streams that require it.
- **Ships with 8 free iptv-org playlists** baked in: USA, India, UK, Sports, News, Movies, Kids, Music.
- **Searchable & grouped** by `group-title`.
- **Launches into `PlayerActivity`** for full-screen playback.
- Add your own `.m3u` URL or local file at runtime.

---

### 🔒 Privacy Vault

A fully on-device media vault (`PrivacyFolderScreen`, ~927 lines):

- **SHA-256 password** protection with security-question recovery.
- **Optional `BiometricPrompt`** unlock (fingerprint / face).
- Files are **moved out of `MediaStore`** into `context.filesDir/vault/{videos,music}/` — invisible to other apps and gallery.
- **Restore flow** uses `MediaStore.IS_PENDING` + `RELATIVE_PATH` on Android Q+ and `MediaScannerConnection` pre-Q.
- **Vault playback** routes through `FileProvider` URIs so vault files never touch `MediaStore` again.
- Works for both videos and music.

---

### 📡 Wi-Fi File Transfer

A local-only file transfer server (`FileTransferScreen`, ~770 lines) — **no internet required, no cloud, no third party**:

- **`VaultHttpServer` extends NanoHTTPD** on a random port (10000–65000).
- Serves an HTML upload page **and** accepts both `multipart/form-data` and `application/octet-stream` POSTs.
- **16-character UUID auth token** validated from query string, `session.parms`, or `X-Auth-Token` header.
- **Path-traversal protection** — requests can't escape the vault directory.
- **ZXing `QRCodeWriter`** encodes `http://<ip>:<port>?token=<auth>` into a scannable QR.
- Sender side uses `HttpURLConnection` with byte-level progress reporting.
- Works on any device with a browser — phone, tablet, laptop, desktop.

---

### 📱 QR Scanner & Share

- **`QrScannerScreen`** — CameraX (Preview + ImageAnalysis) + **MLKit Barcode Scanning** in a full-screen dialog.
- Notable engineering fix: binds to the **Activity's** `LifecycleOwner` (not the Dialog's) to avoid the black-screen bug that affects CameraX-in-dialog patterns.
- `PERFORMANCE` PreviewView mode + `post{}` deferral until the Surface is ready.
- **`QrShareFormat`** — TrebleShot-compatible QR format for device-to-device sharing:
  - Hotspot mode: `hs;pin;ssid;bssid;password;end`
  - Wi-Fi LAN mode: `wf;pin;ssid;bssid;ip;end`
- **`HotspotManager`** — wraps `WifiManager.startLocalOnlyHotspot` (API 26+) for sending without an existing Wi-Fi network, plus a `SecureRandom` 6-digit `PinGenerator`.
- **`P2pPermissionSetupCard`** (Phase 7) — modern permission UX that steps the user through turning on Wi-Fi → Location → granting permissions, with live status chips. No more blind permission nagging.

---

### 🌐 Universal Downloader

Introduced in v0.16.0 Phase 4 — a backend that can extract and download any online video/audio directly from the player.

- **`UniversalDownloader.kt`** — wraps `youtubedl-android` (a `yt-dlp` port for Android) via reflection, with a transparent fallback to direct HTTP stream download for MP4/MP3/M3U8 URLs.
- **`extractStreamInfo(url)`** — fetches the list of available formats (formatId, extension, codec, filesize) for a URL. Works for YouTube, M3U8, direct MP4/MP3, Live TV.
- **`download(url, formatId, targetFile, onProgress)`** — downloads the chosen format with byte-level progress reporting via `DownloadProgress` flow.
- **`updateYtDlpIfNeeded(force)`** — auto-update logic. Fetches the latest `yt-dlp_linux` binary from the [yt-dlp GitHub releases API](https://api.github.com/repos/yt-dlp/yt-dlp/releases/latest), replaces the local copy at `context.filesDir/ytdlp/yt-dlp`, and makes it executable. Runs automatically every 24 hours so extractors never break when websites change their HTML/JSON.
- **`DownloadStreamDialog.kt`** — UI shown when the user taps "Download" in the player. Lists all formats, lets the user pick quality, shows a live progress bar, and saves to `Movies/SHSPlayer/`.

> **Note on native lib size:** `youtubedl-android` ships a ~20 MB ffmpeg binary, so it's intentionally declared as an *optional* dependency in `feature/player/build.gradle.kts` (commented out by default). `UniversalDownloader` uses reflection — if the library is absent, it transparently falls back to direct HTTP download. To enable yt-dlp extraction, uncomment the dependency lines in `feature/player/build.gradle.kts`.

---

### 🪟 Glassmorphism UI Kit

Introduced in v0.16.0 Phase 1 — modern "frosted glass" aesthetic with vibrant Google Material colors.

- **`glassCard(cornerRadius, alpha, blurRadius)`** modifier — translucent surface with vibrant gradient tint (primary → transparent → tertiary), top-edge highlight (light refraction), and hardware-accelerated `Modifier.blur()` on Android 12+ (software-fallback tint-only on older Android).
- **`glassPanel(cornerRadius, alpha)`** modifier — thinner variant for buttons, chips, and tiles inside the player overlay.
- **`GlassCard`** and **`GlassIconButton`** — pre-built composables for common patterns.
- **Vibrant Google palette** in `Color.kt`:
  - `glassBlue` (#4285F4), `glassRed` (#EA4335), `glassYellow` (#FBBC05), `glassGreen` (#34A853), `glassPurple` (#9C27B0), `glassCyan` (#00BCD4), `glassOrange` (#FF6D00)
  - Pre-built gradients: `glassGradientVibrant` (blue→purple→red), `glassGradientCool` (cyan→blue→green), `glassGradientWarm` (yellow→orange→red)
- **PLAYit-style drag-and-drop customizable controls** (`CustomizablePlayerControlsRow.kt`):
  - Long-press + drag to reorder control buttons (snap-swaps at midpoint).
  - Drag a button out 2× its width to remove it from the row.
  - Pencil-icon "Customize" button opens a dialog with eye/eye-off toggles to show/hide any control.

---

### 🗂️ Media Library

The media library (`MediaPickerScreen`, ~834 lines) is the main browser:

- **View modes** — `VIDEOS`, `FOLDERS`, `FOLDER_TREE` (folders + videos in one grid with section headers).
- **Layouts** — List (1 column) and Grid (multi-column; column count auto-computed from 90dp/130dp minimums).
- **Sort** — 5 keys (Title / Duration / Date / Size / Location) × 2 orders (Asc / Desc) with natural/numeric "chunk" comparator. Order label changes per key (A-Z vs Z-A, Shortest vs Longest, Oldest vs Newest, Smallest vs Largest).
- **Quick Settings dialog** — view mode, layout, sort-by, sort order, and 7 toggleable field chips (Duration, Extension, Path, Played progress, Resolution, Size, Thumbnail).
- **Fast scroll** — custom `FastScrollLazyColumn` / `FastScrollLazyGrid` with a draggable thumb and auto-hiding popup showing the folder path segment or first letter.
- **Pull-to-refresh** triggers `MediaSynchronizer.refresh`.
- **Search bar** with animated visibility; filters both videos and folders.
- **Selection mode** — long-press to enter; haptic feedback; pill showing "N/M selected"; select-all/deselect-all; back-handler clears.
- **Selection actions sheet** (animated bottom sheet) — Play, Favourite/Unfavourite, Rename (single video), Share, Info (single video), Privacy (move to vault), Delete (with confirmation when the OS won't ask).
- **Video Info dialog** — file (name, location, size, duration, format), video track (title, codec, WxH, frame rate, bitrate), audio tracks (title, codec, sample rate, sample format, bitrate, channels, channel layout, language), subtitle tracks (title, codec, language).
- **Rename** dialog with OutlinedTextField, auto-focus after 200 ms (rotation-safe).
- **Favourites** — `FavoriteDao` / `FavoriteEntity` in Room; filter icon in the top bar; star/unstar in the selection sheet.
- **Recently played highlight** — title and supporting text tinted with `MaterialTheme.colorScheme.primary` when the "Mark last played media" preference is on.
- **Played progress bar** — 4dp bottom bar on thumbnails using `video.playedPercentage`.
- **FAB menu** — Material 3 `FloatingActionButtonMenu` + `ToggleFloatingActionButton` with three items: Open network stream (URL dialog), Open local video (`ActivityResultContracts.GetContent("video/*")`), Recently played (jumps to the last-played video). Auto-collapses on scroll or selection.

---

### ⚙️ Settings & Customization

Eight top-level categories, each a dedicated Compose screen with a Hilt-injected ViewModel:

| Category | Highlights |
|---|---|
| **Appearance** | Dark theme (System / On / Off), high-contrast dark theme, dynamic theme (Material You, Android 12+, gated on `supportsDynamicTheming()`). Plus the in-app premium Cupertino + Material 3 hybrid palette. |
| **Media Library** | "Mark last played media" toggle, "Manage folders" — exclude any device folder from the library via a checkbox list (paths persisted in `ApplicationPreferences.excludeFolders`). |
| **Player** | Gestures (Seek, Brightness, Volume, Zoom, Pan, Double-tap, Long-press + speed 0.2–4×), Seek increment 1–60 s, Seek sensitivity 0.1–2.0, Controller auto-hide 1–60 s, Hide player buttons background, Resume (Yes/No), Default playback speed 0.2–4×, Autoplay, PiP (auto), Background play, Remember brightness, Remember selections, Player screen orientation (Automatic / Landscape / Landscape reverse / Landscape auto / Portrait / **Video orientation**). |
| **Decoder** | Decoder priority — Prefer device decoders / Prefer app decoders / Device decoders only. |
| **Audio** | Preferred audio language (locale picker via `LocalesHelper.getAvailableLocales()`), Require audio focus, Pause on headset disconnect, Show system volume panel, Volume boost. |
| **Subtitle** | Preferred subtitle language, text encoding (filtered to `Charset.isSupported`), use system caption style (opens Android captioning settings), font (Default/Monospace/Sans Serif/Serif), bold, font size 10–60, background, apply embedded styles. |
| **General** | Delete thumbnail cache (confirm dialog → `MediaInfoSynchronizer.clearThumbnailsCache`), Reset settings (confirm → `PreferencesRepository.resetPreferences`). |
| **About** | Animated radial-gradient hero card with app icon, name, and version. Buttons: **Libraries** (full OSS list via `aboutlibraries`), **Join us on Telegram** → `t.me/aamoviesofficial`, **Contact Us** → `mailto:shsjadinfo@gmail.com`, **Support Development via bKash** → tap-to-copy `01310211442`. |

---

## 📸 Screenshots

> Screenshots live in [`fastlane/metadata/android/en-US/images/phoneScreenshots/`](fastlane/metadata/android/en-US/images/phoneScreenshots/) — 7 phone screenshots ready for Play Store submission.

| Library | Player | Audio Player |
|:---:|:---:|:---:|
| _Library grid_ | _Player with controls_ | _Vinyl audio player_ |

| IPTV | Privacy Vault | Wi-Fi Transfer |
|:---:|:---:|:---:|
| _IPTV browser_ | _Vault unlock_ | _QR + transfer_ |

---

## 🏗 Architecture

SHS Player follows **Clean Architecture** across **12 Gradle modules**, with strict dependency direction:

```
                        ┌──────────────┐
                        │     :app     │  ← Single-Activity Compose host, navigation, splash,
                        │              │    bottom bar, music/IPTV/vault/transfer/QR screens,
                        │              │    crash reporter, FileProvider.
                        └──────┬───────┘
            ┌──────────────────┼──────────────────┐
            ▼                  ▼                  ▼
   ┌─────────────────┐ ┌──────────────┐ ┌────────────────┐
   │ :feature:player │ │:feature:settings│ │:feature:videopicker│
   │ ExoPlayer + VLC │ │ 8 pref screens │ │ Library browser   │
   │ 17 state holders│ │ ViewModels     │ │ Selection, sort   │
   └────────┬────────┘ └───────┬────────┘ └────────┬────────┘
            └──────────────────┴──────────────────┘
                               │
   ┌───────────────────────────┼───────────────────────────┐
   ▼                           ▼                           ▼
┌────────┐  ┌──────────┐  ┌────────────┐  ┌──────────┐  ┌──────┐
│:core:ui│  │:core:domain│ │:core:data  │  │:core:media│  │:core:database│
│Theme,  │  │Use cases  │  │Repos, mappers│ │Sync, MediaStore│ │Room, DAOs   │
│components│ │           │  │            │  │            │  │            │
└────────┘  └─────┬────┘  └─────┬──────┘  └─────┬──────┘  └──────┬─────┘
                  ▼             ▼                ▼                ▼
              ┌──────────────────────────────────────────────────────┐
              │  :core:datastore (typed JSON DataStore)              │
              │  :core:model (pure Kotlin @Serializable types)       │
              │  :core:common (Logger, Utils, Dispatchers, extensions)│
              └──────────────────────────────────────────────────────┘
```

**Dependency direction:** `app` → `feature:*` → `core:*` → (leaf: `core:common`, `core:model`). No cycles. `core:ui` is Compose-only (no Hilt). `core:model` is pure JVM (no Android). `core:database` is the only module that knows about Room.

**Key patterns**
- **Hilt 2.57.2** for DI across every module except `core:model` and `core:ui`.
- **ViewModel + StateFlow** for screen state; **`@Stable` Compose state holders** for player state (17 of them in `feature:player`).
- **Single-Activity architecture** — `MainActivity` hosts a Compose `NavHost`; `PlayerActivity` and `AudioPlayerActivity` are the only other activities.
- **Repository pattern** — `MediaRepository` and `PreferencesRepository` interfaces in `core:data` with `Local*` implementations bound via Hilt.
- **Use-case layer** — `core/domain` has 5 use cases that compose the three sorters + folder-tree builder + playlist builder.
- **DataStore with typed JSON serializers** — `ApplicationPreferences` and `PlayerPreferences` are `@Serializable` data classes persisted via `DataStoreFactory.create` with custom `Serializer` and `Json { ignoreUnknownKeys = true }`.
- **Room v4 with 3 migrations and 4 exported schemas** — `MediaDatabase` v4 has 8 entities and `fallbackToDestructiveMigration(false)`.

---

## 🧪 Tech Stack

| Layer | Technology | Version |
|---|---|---|
| **Language** | Kotlin | 2.3.0 |
| **Build** | Android Gradle Plugin | 8.13.2 |
| **Build** | Gradle | 9.3.1 |
| **Build** | KSP | 2.3.4 |
| **UI** | Jetpack Compose BOM | 2026.01.00 |
| **UI** | Material 3 | 1.5.0-alpha12 (Expressive) |
| **UI** | Material Components | 1.13.0 |
| **UI** | Navigation Compose | 2.9.6 |
| **UI** | Lifecycle | 2.10.0 |
| **UI** | Activity Compose | 1.12.3 |
| **UI** | Coil (image loading) | 2.7.0 |
| **UI** | Accompanist Permissions | 0.37.3 |
| **DI** | Hilt | 2.57.2 |
| **Persistence** | Room | 2.8.4 |
| **Persistence** | DataStore | 1.2.0 |
| **Playback** | AndroidX Media3 | 1.9.2 (common, exoplayer, dash, hls, rtsp, session, ui, ui-compose) |
| **Playback** | LibVLC | 3.6.0-eap5 |
| **Camera** | CameraX | 1.4.1 (core, camera2, lifecycle, view) |
| **ML** | MLKit Barcode Scanning | 17.3.0 |
| **QR** | ZXing | 3.5.3 |
| **HTTP** | NanoHTTPD | 2.3.1 |
| **Charset** | juniversalchardet | 2.5.0 |
| **Auth** | AndroidX Biometric | 1.1.0 |
| **OSS info** | aboutlibraries | 13.2.1 |
| **Serialization** | kotlinx.serialization | 1.10.0 |
| **Coroutines** | kotlinx.coroutines | 1.10.2 |
| **Splash** | AndroidX Core Splashscreen | 1.2.0 |
| **Lint** | ktlint | 12.3.0 |
| **SDK** | compileSdk / targetSdk | 36 (Android 16) |
| **SDK** | minSdk | 23 (Android 6.0) |
| **JVM** | source/target | 17 |

---

## 📁 Project Structure

```
SHS-Player/
├── app/                                    # Application module (single Activity host)
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   ├── debug.keystore                      # Committed debug signing key
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── ic_launcher-playstore.png
│       └── java/dev/anilbeesetti/nextplayer/
│           ├── MainActivity.kt             # Single-Activity Compose host
│           ├── NextPlayerApplication.kt    # @HiltAndroidApp + crash handler install
│           ├── MainViewModel.kt
│           ├── crash/
│           │   ├── CrashActivity.kt        # Crash dump viewer (Share/Copy/Restart)
│           │   └── GlobalExceptionHandler.kt  # Thread.UncaughtExceptionHandler + logcat
│           ├── navigation/
│           │   ├── MediaNavGraph.kt
│           │   └── SettingsNavGraph.kt
│           └── ui/
│               ├── splash/AnimatedSplashScreen.kt
│               ├── BottomNavBar.kt         # 5-tab bottom nav
│               ├── MusicScreen.kt          # Music library (~1666 lines)
│               ├── PrivacyFolderScreen.kt  # Privacy vault (~927 lines)
│               ├── MediaDeletionHelper.kt  # Safe MediaStore deletion
│               ├── QrScannerScreen.kt      # CameraX + MLKit
│               ├── FileTransferScreen.kt   # NanoHTTPD + ZXing (~770 lines)
│               ├── MeScreen.kt             # "Me" hub
│               ├── TelegramScreen.kt       # Developer info
│               ├── tv/
│               │   ├── WatchTvScreen.kt    # IPTV browser
│               │   └── M3uParser.kt        # M3U parser
│               └── share/
│                   ├── QrShareFormat.kt    # TrebleShot-compatible QR format
│                   └── HotspotManager.kt   # LocalOnlyHotspot + PIN
│
├── core/                                   # 8 core modules
│   ├── common/                             # Logger, Utils, Dispatchers, extensions
│   │   └── src/main/java/.../core/common/
│   │       ├── Logger.kt, Utils.kt, NextDispatchers.kt
│   │       ├── extensions/ (Float, File, Uri, Context)
│   │       └── di/ (CoroutineScopesModule, DispatchersModule)
│   ├── model/                              # 21 @Serializable pure-Kotlin types
│   │   └── src/main/java/.../core/model/
│   │       ├── ApplicationPreferences.kt, PlayerPreferences.kt
│   │       ├── Video.kt, Folder.kt, Sort.kt
│   │       ├── ThemeConfig.kt, DecoderPriority.kt, ScreenOrientation.kt
│   │       ├── VideoContentScale.kt, MediaViewMode.kt, MediaLayoutMode.kt
│   │       ├── Font.kt, FastSeek.kt, Resume.kt, LoopMode.kt
│   │       ├── DoubleTapGesture.kt, ControlButtonsPosition.kt
│   │       └── VideoStreamInfo.kt, AudioStreamInfo.kt, SubtitleStreamInfo.kt
│   ├── database/                           # Room v4 — 8 entities, 5 DAOs, 3 migrations
│   │   ├── schemas/ (4 exported JSON schemas v1–v4)
│   │   └── src/main/java/.../core/database/
│   │       ├── MediaDatabase.kt, DatabaseModule.kt, DaoModule.kt
│   │       ├── dao/ (MediumDao, MediumStateDao, DirectoryDao, BookmarkDao, FavoriteDao)
│   │       ├── entities/ (MediumEntity, MediumStateEntity, DirectoryEntity,
│   │       │            VideoStreamInfoEntity, AudioStreamInfoEntity, SubtitleStreamInfoEntity,
│   │       │            BookmarkEntity, FavoriteEntity)
│   │       ├── relations/ (MediumWithInfo, DirectoryWithMedia)
│   │       ├── converter/UriListConverter.kt
│   │       └── src/androidTest/.../dao/ (MediumDaoTest, DirectoryDaoTest)
│   ├── datastore/                          # Typed JSON DataStore
│   │   └── src/main/java/.../core/datastore/
│   │       ├── datasource/ (PreferencesDataSource, AppPreferencesDataSource, PlayerPreferencesDataSource)
│   │       ├── serializer/ (ApplicationPreferencesSerializer, PlayerPreferencesSerializer)
│   │       └── di/DataStoreModule.kt
│   ├── data/                               # Repositories + mappers
│   │   └── src/main/java/.../core/data/
│   │       ├── DataModule.kt
│   │       ├── repository/ (MediaRepository, LocalMediaRepository,
│   │       │               PreferencesRepository, LocalPreferencesRepository,
│   │       │               fake/FakeMediaRepository, fake/FakePreferencesRepository)
│   │       ├── mappers/ (ToVideo, ToFolder, ToVideoState, ToVideoStreamInfo,
│   │       │             ToAudioStreamInfo, ToSubtitleStreamInfo)
│   │       └── models/VideoState.kt
│   ├── domain/                             # 5 use cases
│   │   └── src/main/java/.../core/domain/
│   │       ├── GetSortedMediaUseCase.kt
│   │       ├── GetSortedVideosUseCase.kt
│   │       ├── GetSortedFoldersUseCase.kt
│   │       ├── GetSortedFolderTreeUseCase.kt
│   │       ├── GetSortedPlaylistUseCase.kt
│   │       └── src/test/.../GetSortedVideosUseCaseTest.kt  # 8 tests, all Sort × Order combos
│   ├── media/                              # MediaStore sync + MediaMetadataRetriever
│   │   └── src/main/java/.../core/media/
│   │       ├── MediaModule.kt
│   │       ├── model/MediaVideo.kt
│   │       ├── services/ (MediaService, LocalMediaService)
│   │       └── sync/ (MediaSynchronizer, LocalMediaSynchronizer,
│   │                  MediaInfoSynchronizer, LocalMediaInfoSynchronizer)
│   └── ui/                                 # Compose-only component library
│       └── src/main/java/.../core/ui/
│           ├── theme/ (Theme.kt, Color.kt, Type.kt)
│           ├── designsystem/NextIcons.kt
│           ├── base/ (ScreenState, DataState)
│           ├── components/ (PreferenceItem, ClickablePreferenceItem, PreferenceSwitch,
│           │                 PreferenceSwitchWithDivider, PreferenceSlider, NextSwitch,
│           │                 RadioTextButton, Buttons, NextDialog, TopAppBar, ListItemComponent,
│           │                 ClickablePreferenceItem)
│           ├── composables/ (PermissionRationaleDialog, PermissionDetailView,
│           │                  PermissionMissingView, FastScroll)
│           ├── preview/ (DevicePreviews, DayNightPreview, VideoPickerPreviewParameterProvider)
│           ├── extensions/PaddingValues.kt
│           └── res/
│               ├── drawable/ (72 icon XMLs + ic_shs_icon.jpg + ic_developer_photo.png)
│               ├── values/ (strings.xml, colors.xml, dimens.xml, arrays.xml, themes.xml)
│               └── values-*/  (40 locale folders: ar, bg, bn, ca, cs, da, de, el, es, et,
│                                fa, fi, fr, hi, hu, ia, in, it, iw, ja, kn, ko, ml, nb-rNO,
│                                nl, pa, pl, pt, pt-rBR, ro, ru, sv, ta, th, tr, uk, ur, vi,
│                                zh-rCN, zh-rTW)
│
├── feature/                                # 3 feature modules
│   ├── player/                             # The big one — ExoPlayer + VLC + 17 state holders
│   │   ├── build.gradle.kts
│   │   └── src/main/java/.../feature/player/
│   │       ├── PlayerActivity.kt           # Video host
│   │       ├── AudioPlayerActivity.kt      # Audio host (separate, leak-fixed)
│   │       ├── PlayerViewModel.kt
│   │       ├── MediaPlayerScreen.kt        # Orchestrates 13 state holders + 12 overlays
│   │       ├── AudioPlayerScreen.kt
│   │       ├── PlayerContentFrame.kt
│   │       ├── engine/                     # ← VLC engine (unique to SHS Player)
│   │       │   ├── VlcEngine.kt
│   │       │   ├── VlcPlayerEngine.kt
│   │       │   ├── VlcPlayerActivity.kt
│   │       │   └── VlcPlaybackService.kt
│   │       ├── service/
│   │       │   ├── PlayerService.kt        # MediaSessionService (ExoPlayer)
│   │       │   └── CustomCommands.kt       # 6 SessionCommands
│   │       ├── state/                      # 17 @Stable Compose state holders
│   │       │   ├── TapGestureState.kt, AudioEqualizerState.kt, VolumeState.kt,
│   │       │   ├── MediaPresentationState.kt, CuesState.kt, PlaybackParametersState.kt,
│   │       │   ├── SeekGestureState.kt, BrightnessState.kt, ControlsVisibilityState.kt,
│   │       │   ├── MetadataState.kt, AudioDelayState.kt, VideoZoomAndContentScaleState.kt,
│   │       │   ├── TracksState.kt, ErrorState.kt, RotationState.kt,
│   │       │   ├── VolumeAndBrightnessGestureState.kt, PictureInPictureState.kt,
│   │       │   └── VideoEqualizerState.kt
│   │       ├── ui/                         # 20+ player UI panels
│   │       │   ├── EqualizerView.kt, SleepTimerView.kt, AbRepeatView.kt,
│   │       │   ├── SubtitleView.kt, VideoContentScaleSelectorView.kt,
│   │       │   ├── PlaybackSpeedSelectorView.kt, VoiceChangerView.kt,
│   │       │   ├── VerticalProgressView.kt, DecoderSelectorView.kt,
│   │       │   ├── AudioTrackSelectorView.kt, OverlayShowView.kt,
│   │       │   ├── DoubleTapIndicator.kt, PlaylistPanelView.kt, PlayerGestures.kt,
│   │       │   ├── RadioButtonRow.kt, SubtitleSelectorView.kt, BookmarkView.kt,
│   │       │   ├── PlayerMenuView.kt, OverlayView.kt, ShutterView.kt
│   │       │   └── controls/ (ControlsTopView, ControlsBottomView,
│   │       │                 CustomizablePlayerControlsRow)
│   │       ├── buttons/ (PlayPauseButton, PreviousButton, NextButton,
│   │       │              RepeatButton, ShuffleButton, PlayerButton)
│   │       ├── extensions/ (Player, PlayerView, MediaItem, Uri, Duration, Font,
│   │       │                 Enum, Activity, Modifier, ImageButton, PointerInputScope,
│   │       │                 Rational, ScreenOrientation, SuspendActivityResultLauncher,
│   │       │                 TrackGroup, MappedTrackInfo, VideoSize, VideoContentScale)
│   │       ├── utils/ (PlayerApi.kt, ScreenshotUtil.kt)
│   │       └── model/Subtitle.kt
│   │
│   ├── settings/                           # 8 settings screens + navigation
│   │   └── src/main/java/.../settings/
│   │       ├── SettingsScreen.kt
│   │       ├── screens/
│   │       │   ├── audio/ (AudioPreferencesScreen, AudioPreferencesViewModel)
│   │       │   ├── about/ (AboutPreferencesScreen, LibrariesScreen)
│   │       │   ├── medialibrary/ (FolderPreferencesScreen, FolderPreferencesViewModel,
│   │       │   │                          MediaLibraryPreferencesScreen, MediaLibraryPreferencesViewModel)
│   │       │   ├── appearance/ (AppearancePreferencesScreen, AppearancePreferencesViewModel)
│   │       │   ├── general/ (GeneralPreferencesScreen, GeneralPreferencesViewModel)
│   │       │   ├── player/ (PlayerPreferencesScreen, PlayerPreferencesViewModel)
│   │       │   ├── subtitle/ (SubtitlePreferencesScreen, SubtitlePreferencesViewModel)
│   │       │   └── decoder/ (DecoderPreferencesScreen, DecoderPreferencesViewModel)
│   │       ├── navigation/ (9 NavGraph extensions)
│   │       ├── extensions/ (FastSeek, Font, ControlButtonsPosition, Resume,
│   │       │                 DoubleTapGesture, ScreenOrientation, ThemeConfig, DecoderPriority)
│   │       ├── composables/OptionsDialog.kt
│   │       └── utils/LocalesHelper.kt
│   │
│   └── videopicker/                        # Library browser
│       └── src/main/java/.../feature/videopicker/
│           ├── screens/
│           │   ├── mediapicker/ (MediaPickerScreen, MediaPickerViewModel)
│           │   └── MediaState.kt
│           ├── navigation/MediaPickerNavigation.kt
│           ├── extensions/ (MediaViewMode, SortOrder, MediaLayoutMode)
│           ├── state/SelectionManager.kt
│           └── composables/ (MediaView, FolderItem, RenameDialog, VideoInfoDialog,
│                             VideoItem, QuickSettingsDialog, MediaContent,
│                             TextIconToggleButton, InfoChip)
│
├── fastlane/                               # Play Store automation
│   ├── Appfile, Fastfile, README.md
│   └── metadata/android/en-US/
│       ├── title.txt, short_description.txt, full_description.txt
│       ├── changelogs/ (18, 19, 20, 29, 31)
│       └── images/ (featureGraphic, icon, 7 phoneScreenshots)
│
├── gradle/
│   ├── libs.versions.toml                  # Version catalog
│   └── wrapper/ (gradle-wrapper.jar, gradle-wrapper.properties)
│
├── build.gradle.kts                        # Root build (ktlint + aboutlibraries everywhere)
├── settings.gradle.kts                     # rootProject.name = "SHSPlayer"
├── gradle.properties
├── gradlew, gradlew.bat
├── Gemfile                                 # fastlane gem
├── renovate.json                           # Renovate config
├── index.html                              # GitHub Pages landing page
├── LICENSE                                 # MIT
├── PRIVACY                                 # Privacy policy
├── CODE_OF_CONDUCT.md
├── CONTRIBUTING.md
└── SECURITY.md
```

---

## 🎯 Getting Started

### For end users

1. **Download the latest APK** from the [Releases page](https://github.com/The-JDdev/SHS-Player/releases).
2. Allow "Install from unknown sources" for your browser/file manager.
3. Open the APK and install.
4. Launch **SHS Player** and grant the media-permission prompt on first run.

> **Android 13+:** Only `READ_MEDIA_VIDEO` / `READ_MEDIA_AUDIO` are requested.  
> **Android 12 and below:** `READ_EXTERNAL_STORAGE` (and `WRITE_EXTERNAL_STORAGE` on Android 10 and below).  
> **Privacy Vault** uses app-private storage — no extra permission needed.

---

## 🔧 Building from Source

### Prerequisites

- **JDK 17** (set `JAVA_HOME`)
- **Android SDK** with `platform-android-36` and `build-tools` matching AGP 8.13.2
- **Android NDK** is *not* required — VLC ships pre-built `.so` files for all 4 ABIs.
- **Git**, **Python 3** (for fastlane, optional)
- ~6 GB free disk space for Gradle caches and build outputs

### Clone & build

```bash
# Clone
git clone https://github.com/The-JDdev/SHS-Player.git
cd SHS-Player

# Build a debug APK (universal)
./gradlew assembleDebug

# Build release APKs (per-ABI splits + universal)
./gradlew assembleRelease

# Build an App Bundle (.aab) for Play Store
./gradlew bundleRelease

# Or use Fastlane
bundle install                      # one-time
bundle exec fastlane build          # builds release APK + AAB
```

### Build variants

| Variant | Application ID suffix | Signature | Notes |
|---|---|---|---|
| `debug` | `.debug` | Debug keystore (committed) | App name shows "SHS Player"; debuggable. |
| `release` | — | Your own keystore | R8 minify + resource shrink. Set up a `release` signing config in `app/build.gradle.kts` or pass via Fastlane env vars. |
| `release-with-debug-signing` | `.release` | Debug keystore | Same as `release` but signed with the debug key — useful for testing R8 on a real device without setting up release signing. |

### ABI splits

Release builds split into per-ABI APKs (smaller download size):
- `armeabi-v7a` (32-bit ARM — old devices)
- `arm64-v8a` (64-bit ARM — modern phones)
- `x86` (32-bit Intel/AMD — emulator)
- `x86_64` (64-bit Intel/AMD — emulator / Chromebook)
- `universal` (everything in one APK)

App Bundles (`bundleRelease`) disable the splits and let Google Play handle per-device delivery.

### Build outputs

```
app/build/outputs/apk/debug/app-debug.apk
app/build/outputs/apk/release/app-arm64-v8a-release.apk
app/build/outputs/apk/release/app-universal-release.apk
app/build/outputs/bundle/release/app-release.aab
```

---

## 🛠 Development

### Environment

- **IDE:** Android Studio (Ladybug or newer) or IntelliJ IDEA with the Kotlin plugin
- **Kotlin:** 2.3.0
- **Gradle:** 9.3.1 (via wrapper — do not use a system Gradle)
- **KSP:** 2.3.4 (matches Kotlin)
- **Compose Compiler:** bundled with Kotlin 2.3.0 via `org.jetbrains.kotlin.plugin.compose`

### Code style

- **ktlint 12.3.0** is applied to every module via the root `build.gradle.kts`. Run:
  ```bash
  ./gradlew ktlintCheck       # check
  ./gradlew ktlintFormat      # auto-format
  ```
- Follow the existing Compose conventions: `@Stable` state holders, `remember<State>()` + `LaunchedEffect(player) { state.observe() }`, `compositionLocalOf` for cross-tree state.
- Use Hilt `@Inject` for constructor injection; never use field injection.
- Keep modules decoupled — `feature:*` modules must not depend on each other; `core:*` modules must not depend on `feature:*`.

### Tests

```bash
./gradlew test                         # JVM unit tests (core/domain has 8)
./gradlew connectedAndroidTest         # Instrumented tests (core/database DAO tests)
```

Tests are configured with `ignoreFailures = true` at the root level — failures won't break the build, but please don't add broken tests.

### Debugging tips

- The **custom crash reporter** captures `logcat -d` at crash time and offers Share / Copy / Restart — useful when debugging on a device without ADB.
- `core/common/Logger.kt` wraps `Log.d/i/e` — use it instead of raw `Log.*` so logs can be toggled centrally in the future.
- `NextDispatchers` (`Default`, `IO`) is injected via Hilt — never use `Dispatchers.IO` directly in production code.
- Room schema exports are in `core/database/schemas/` — if you change `MediaDatabase`, generate a new schema and write a migration (do **not** use `fallbackToDestructiveMigration`).

### Fastlane

The `fastlane/` folder automates Play Store publishing:

```bash
# Build (release APK + AAB)
bundle exec fastlane build

# Build + publish to Play Store Internal Track
bundle exec fastlane publish track:internal
```

Environment variables for signing:
- `KEYSTORE` — path to your `.keystore` / `.jks`
- `KEYSTORE_PASSWORD`
- `KEY_ALIAS`
- `KEY_PASSWORD`
- `PLAY_API_CREDENTIALS` — path to Play Store service-account JSON

---

## 🔐 Permissions Explained

SHS Player requests **only the permissions it needs** — no contact list, no location (except for Wi-Fi peer discovery metadata, never stored), no SMS, no call log.

| Permission | Why |
|---|---|
| `INTERNET` | Streaming (DASH/HLS/RTSP), online subtitle search, IPTV, Wi-Fi transfer server. |
| `READ_MEDIA_VIDEO` / `READ_MEDIA_AUDIO` (Android 13+) | Read your video and audio files. |
| `READ_EXTERNAL_STORAGE` (≤ Android 12) | Read media on older Android versions. |
| `WRITE_EXTERNAL_STORAGE` (≤ Android 10) | Write to shared storage on pre-Scoped-Storage Android. |
| `MANAGE_MEDIA` | Bulk delete media via `MediaStore.createDeleteRequest` flow on Android 11+. |
| `SYSTEM_ALERT_WINDOW` | Picture-in-Picture overlay on older Android; future overlay features. |
| `CAMERA` | QR scanner. |
| `RECORD_AUDIO` | Reserved for future voice-input features (not currently active). |
| `ACCESS_WIFI_STATE` / `CHANGE_WIFI_STATE` / `ACCESS_NETWORK_STATE` / `CHANGE_NETWORK_STATE` / `NEARBY_WIFI_DEVICES` (Android 13+) | Wi-Fi file transfer, local-only hotspot, device discovery. |
| `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION` | Required by Android to expose Wi-Fi BSSID/SSID for the TrebleShot-style share format. **Never stored or transmitted.** |
| `BLUETOOTH` / `BLUETOOTH_ADMIN` (≤ Android 11) / `BLUETOOTH_CONNECT` / `BLUETOOTH_SCAN` (Android 12+) | Reserved for future Bluetooth file transfer. |
| `USE_BIOMETRIC` / `USE_FINGERPRINT` | Privacy Vault biometric unlock. |

---

## 🛡 Privacy & Security

- **No data collection.** SHS Player does not collect, transmit, or share any personal information. There is no analytics SDK, no crash-reporting SDK, no ad SDK.
- **All processing is on-device.** Media decoding, thumbnail generation, subtitle parsing, charset detection, QR encoding — everything happens locally.
- **Wi-Fi File Transfer stays on your LAN.** The NanoHTTPD server binds to your local Wi-Fi interface; there is no cloud relay.
- **Privacy Vault files are stored in app-private storage** (`context.filesDir/vault/`) — invisible to other apps and to the gallery. They are deleted if you uninstall the app (so back them up first!).
- **Custom crash handler** stores crash logs locally and only shares them when you explicitly tap Share.
- **Open source.** Every line of code is auditable. If you find a vulnerability, see [SECURITY.md](SECURITY.md) for responsible disclosure.
- The full privacy policy is in [`PRIVACY`](PRIVACY).

---

## 🌍 Internationalization

SHS Player is localised into **40+ languages** thanks to the upstream Next Player community and additional South-Asian language focus:

`ar` · `bg` · `bn` · `ca` · `cs` · `da` · `de` · `el` · `es` · `et` · `fa` · `fi` · `fr` · `hi` · `hu` · `ia` · `in` · `it` · `iw` · `ja` · `kn` · `ko` · `ml` · `nb-rNO` · `nl` · `pa` · `pl` · `pt` · `pt-rBR` · `ro` · `ru` · `sv` · `ta` · `th` · `tr` · `uk` · `ur` · `vi` · `zh-rCN` · `zh-rTW` (+ English default).

Particular emphasis on South-Asian scripts: **Bengali** (`bn`), **Hindi** (`hi`), **Punjabi** (`pa`), **Urdu** (`ur`), **Tamil** (`ta`), **Kannada** (`kn`), **Malayalam** (`ml`), **Persian** (`fa`), **Arabic** (`ar`).

**Want to add or improve a translation?** Open a PR with updated `strings.xml` in the relevant `values-*` folder under `core/ui/src/main/res/`. Weblate / Crowdin integration is on the roadmap.

---

## 🤝 Contributing

Contributions are welcome and appreciated! Please read [`CONTRIBUTING.md`](CONTRIBUTING.md) first.

**Quick rules:**
1. Branch from `main`.
2. Run `./gradlew ktlintCheck` before committing.
3. Add tests for new logic where reasonable.
4. Don't bump the version number in your PR — the maintainer handles releases.
5. Don't change `applicationId` — it's pinned to `dev.anilbeesetti.nextplayer` for upstream-compatibility and Play Store continuity.
6. Keep modules decoupled — no `feature:*` → `feature:*` dependencies.
7. Open an issue first for big architectural changes so we can discuss.

**Good first issues:** look for the `good first issue` and `help wanted` labels in the Issues tab.

---

## 📜 Code of Conduct

This project follows the [Contributor Covenant Code of Conduct](CODE_OF_CONDUCT.md). By participating you agree to uphold it. Report violations to **thejddev.official@gmail.com**.

---

## 🔒 Security Policy

See [`SECURITY.md`](SECURITY.md). Summary:

- **Supported versions:** the latest `0.15.x` release only.
- **Reporting a vulnerability:** email **thejddev.official@gmail.com** privately. Do not open a public issue.
- **Backup contact:** Telegram **@aamoviesadmin**.
- Please allow up to 72 hours for an initial response and 90 days before public disclosure.

---

## 📄 License

SHS Player is licensed under the **MIT License** — see [`LICENSE`](LICENSE).

```
MIT License

Copyright (c) 2026 Sajjad Hussain Shobuj (SHS)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

SHS Player is a fork of [Next Player](https://github.com/anilbeesetti/nextplayer) by Anil Kumar Beesetti. The upstream project's contributions are gratefully acknowledged.

---

## 🙏 Credits & Acknowledgements

- **Anil Kumar Beesetti** — original author of [Next Player](https://github.com/anilbeesetti/nextplayer), without which SHS Player would not exist.
- **The AndroidX Media3 team** — for the ExoPlayer-based playback stack.
- **The VideoLAN team** — for LibVLC, the second engine that handles the long tail of codecs.
- **The Jetpack Compose team** — for the modern UI toolkit that makes Compose-first apps possible.
- **Google MLKit** — for on-device barcode scanning.
- **The ZXing project** — for QR code generation.
- **NanoHTTPD** — for the tiny embedded HTTP server that powers Wi-Fi file transfer.
- **juniversalchardet** — for subtitle charset auto-detection.
- **aboutlibraries** — for the OSS license listing in the About screen.
- **All translators** across the 40+ supported locales.
- **The TrebleShot project** — for the QR share format inspiration.
- **iptv-org** — for the free, community-maintained IPTV playlists bundled with the app.

Full list of third-party libraries and their licenses is available in-app under **Settings → About → Libraries**, powered by `aboutlibraries`.

---

## 💎 Support the Project

SHS Player is built and maintained by a **solo developer working from a smartphone in Bangladesh**. Every contribution — no matter the size — directly funds continued development, server costs, API testing, and new feature research.

### 💳 Donations

| Method | Address / Link |
|---|---|
| **bKash** (Bangladesh) | `01310211442` |
| **UPI** (India) | _see in-app About screen_ |
| **PayPal** | [paypal.me](https://paypal.me) — see in-app |
| **Ko-fi** | [ko-fi.com](https://ko-fi.com) — see in-app |
| **Crypto (USDT / TON / AAVE)** | _see the GitHub Pages landing page_ |

The bKash number is also baked into the **About** screen as a tap-to-copy card.

### ⭐ Other ways to help

- **Star** the repo on GitHub — it helps others discover the project.
- **Share** the app with friends and family.
- **Report bugs** and **request features** via [Issues](https://github.com/The-JDdev/SHS-Player/issues).
- **Translate** the app into your language.
- **Contribute code** — see [Contributing](#-contributing).

---

## 💬 Community

Join the conversation and connect with the developer and other users:

| Platform | Link |
|---|---|
| 📱 **Telegram channel** | [t.me/aamoviesofficial](https://t.me/aamoviesofficial) |
| 📘 **Facebook** | [fb.com/itsshsshobuj](https://fb.com/itsshsshobuj) |
| 💻 **GitHub** | [github.com/The-JDdev](https://github.com/The-JDdev) |
| 🐛 **Report a bug** | [Issues](https://github.com/The-JDdev/SHS-Player/issues) |
| ✉️ **Email (security)** | `thejddev.official@gmail.com` |
| ✉️ **Email (general)** | `shsjadinfo@gmail.com` |

---

## ❓ FAQ

**Q: Is SHS Player really free?**  
A: Yes — free as in beer (no cost, no ads, no in-app purchases) and free as in speech (MIT-licensed open source).

**Q: Why two engines (ExoPlayer + VLC)?**  
A: ExoPlayer is fast and well-integrated with Android's media pipeline, but it can't play every codec/container. VLC fills the gaps — especially for niche codecs, broken files, and certain stream protocols. SHS Player picks the right engine per file (or you can force it in Decoder settings).

**Q: Where are my Privacy Vault files stored?**  
A: In app-private storage at `context.filesDir/vault/{videos,music}/`. They are invisible to other apps and to the gallery, and they are deleted if you uninstall SHS Player. **Back them up before uninstalling!**

**Q: Does Wi-Fi File Transfer send my files to the cloud?**  
A: No. The NanoHTTPD server binds to your local Wi-Fi interface. There is no cloud relay — only devices on the same Wi-Fi can reach it, and the 16-character UUID auth token prevents unauthorised access even on shared networks.

**Q: Why does the app need location permission?**  
A: On Android, accessing Wi-Fi SSID/BSSID (needed for the TrebleShot-style QR share format) requires `ACCESS_FINE_LOCATION`. SHS Player reads these values locally to populate the QR code; it never stores or transmits your location.

**Q: Can I add my own IPTV playlist?**  
A: Yes — open the **Watch TV** tab, tap the FAB / menu, and paste any `.m3u` URL or pick a local `.m3u` file. Eight free iptv-org playlists are bundled as defaults.

**Q: Why is the application ID `dev.anilbeesetti.nextplayer`?**  
A: For upstream-compatibility and Play Store continuity. Changing the application ID would make SHS Player a different app and break updates for existing users.

**Q: How do I report a crash?**  
A: SHS Player has a built-in crash reporter. When the app crashes, you'll see a Crash screen with the stack trace and `logcat` output. Tap **Share** to send it via your preferred app, or **Copy** to paste it into a GitHub Issue.

**Q: Will my settings transfer from upstream Next Player?**  
A: Not automatically. SHS Player uses the same DataStore file names but with extended preference schemas. Install SHS Player fresh and reconfigure.

**Q: Is there a dark theme?**  
A: Three, actually: System (follows your device), On (always dark, with optional high-contrast / pure-black OLED mode), and Off (always light). Plus Material You dynamic theming on Android 12+.

---

## 🗺 Roadmap

**In progress / planned:**
- Bluetooth file transfer (foundations already in the manifest).
- Weblate / Crowdin integration for community translations.
- More IPTV playlist sources and EPG (Electronic Programme Guide) support.
- Playlist export/import.
- Chromecast support.
- Video gestures editor (customise which gesture does what).
- More voice-changer presets and a custom pitch slider.
- Folder-level playback settings (e.g. always-start-at-30% for a specific folder).
- Android Auto support for the audio player.
- Wear OS companion.

**Recently shipped:**
- Dual-engine (ExoPlayer + VLC) architecture.
- Privacy Vault with biometric unlock.
- Wi-Fi file transfer with QR auth.
- IPTV / M3U browser with 8 free playlists.
- Voice changer, AB-repeat, sleep timer, bookmarks, favourites.
- Video trim, video→audio, reverse play, screenshot, mirror.
- Online subtitle search (OpenSubtitles).
- Premium Cupertino + Material 3 hybrid theme.
- Custom crash reporter with logcat capture.
- 40+ language translations.

---

<div align="center">

**Built with 🔥 from Bangladesh 🇧🇩**

**By Sajjad Hussain Shobuj (SHS)**

*If SHS Player has empowered your workflow, please consider [supporting the project](#-support-the-project).*

[⬆ Back to top](#-shs-player)

</div>
