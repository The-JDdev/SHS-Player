<div align="center">

# 🎬 SHS Player

### The Ultimate Open-Source Multimedia Engine for Android

**A privacy-first, dual-engine, feature-packed video & music player built with Jetpack Compose — forked from Next Player and supercharged with a Glassmorphism UI, Privacy Vault, Wi-Fi file transfer, IPTV live TV, an in-app music library, LibVLC as the primary engine, universal downloader, and a QR share ecosystem.**

`v0.17.0` · Built from Bangladesh 🇧🇩 · by **Sajjad Hussain Shobuj (SHS)**

[![GitHub release](https://img.shields.io/github/v/release/The-JDdev/SHS-Player?style=for-the-badge&logo=github&color=4285F4)](https://github.com/The-JDdev/SHS-Player/releases)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge&logo=open-source-initiative&logoColor=white)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android%206.0%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://www.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2026.01.00-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![LibVLC](https://img.shields.io/badge/LibVLC-3.6.0--eap5-FF8800?style=for-the-badge&logo=videolan&logoColor=white)](https://www.videolan.org/vlc/libvlc.html)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-ff69b4.svg?style=for-the-badge&logo=git&logoColor=white)](CONTRIBUTING.md)
[![Telegram](https://img.shields.io/badge/Telegram-Join%20Chat-2CA5E0?style=for-the-badge&logo=telegram&logoColor=white)](https://t.me/aamoviesofficial)

### 📥 Download v0.17.0

| APK | ABI | Best for | Size |
|:---:|:---:|:---|:---:|
| [`SHS-Player-v0.17.0-arm64-v8a.apk`](https://github.com/The-JDdev/SHS-Player/releases/download/v0.17.0/SHS-Player-v0.17.0-arm64-v8a.apk) | 64-bit ARM | Pixel, Samsung, **itel vision 1 pro**, modern phones | ~82 MB |
| [`SHS-Player-v0.17.0-armeabi-v7a.apk`](https://github.com/The-JDdev/SHS-Player/releases/download/v0.17.0/SHS-Player-v0.17.0-armeabi-v7a.apk) | 32-bit ARM | Older / low-end 32-bit phones | ~77 MB |
| [`SHS-Player-v0.17.0-universal.apk`](https://github.com/The-JDdev/SHS-Player/releases/download/v0.17.0/SHS-Player-v0.17.0-universal.apk) | All ABIs | Any device (largest, foolproof) | ~231 MB |

> **itel vision 1 pro?** → Use **arm64-v8a** (it's a 64-bit ARM device).

</div>

---

## 📑 Table of Contents

1. [Overview](#-overview)
2. [What's New in v0.17.0 (LibVLC Default + Bug Fix Release)](#-whats-new-in-v0170-libvlc-default--bug-fix-release)
3. [What's New in v0.16.0 (8-Phase Overhaul)](#-whats-new-in-v0160-8-phase-overhaul)
4. [Why SHS Player?](#-why-shs-player)
5. [Feature Comparison](#-feature-comparison)
6. [Detailed Feature Set](#-detailed-feature-set)
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
7. [Screenshots](#-screenshots)
8. [Architecture](#-architecture)
9. [Tech Stack](#-tech-stack)
10. [Project Structure](#-project-structure)
11. [Getting Started](#-getting-started)
12. [Building from Source](#-building-from-source)
13. [Development](#-development)
14. [Permissions Explained](#-permissions-explained)
15. [Privacy & Security](#-privacy--security)
16. [Internationalization](#-internationalization)
17. [Contributing](#-contributing)
18. [Code of Conduct](#-code-of-conduct)
19. [Security Policy](#-security-policy)
20. [License](#-license)
21. [Credits & Acknowledgements](#-credits--acknowledgements)
22. [Support the Project](#-support-the-project)
23. [Community](#-community)
24. [FAQ](#-faq)
25. [Roadmap](#-roadmap)

---

## 📖 Overview

**SHS Player** is a free, open-source, ad-free Android multimedia application that plays local and network video, audio, and live IPTV streams. It is a heavily-extended fork of [Next Player](https://github.com/anilbeesetti/nextplayer) by Anil Kumar Beesetti, re-architected and rebranded by **Sajjad Hussain Shobuj (SHS)** with a focus on three pillars:

- **Privacy-first design** — all media processing happens on-device. No tracking, no telemetry, no ads.
- **Maximum format coverage** — ships *two* playback engines (**LibVLC** as default + AndroidX Media3/ExoPlayer as fallback) so virtually every container, codec, and streaming protocol "just works".
- **Power-user features** — Privacy Vault, Wi-Fi file transfer, IPTV browsing, voice changer, AB-repeat, sleep timer, bookmarks, online subtitle search, video trim, and much more.

The app is written in **100% Kotlin** with **Jetpack Compose** for the entire UI layer, follows **Clean Architecture** across **12 Gradle modules**, uses **Hilt** for dependency injection, **Room** + **DataStore** for persistence, and is localised into **40+ languages**.

> **Latest version:** `0.17.0` (versionCode `58`) — [Download APK](https://github.com/The-JDdev/SHS-Player/releases)
> **Min Android:** 6.0 (API 23) · **Target:** Android 16 (API 36)
> **Application ID:** `dev.anilbeesetti.nextplayer` (retained for upstream compatibility)

---

## 🆕 What's New in v0.17.0 (LibVLC Default + Bug Fix Release)

v0.17.0 promotes **LibVLC to the primary default playback engine** and fixes five critical bugs that affected audio quality, seek accuracy, surface rendering, and media memory management in the VLC engine path.

### 🔧 Bug Fixes (5 critical)

| # | Component | Bug | Fix |
|:---:|---|---|---|
| **1** | `PlayerActivity` / ExoPlayer | `SeekParameters.EXACT` no longer exists in Media3 1.9.x — caused `NoSuchFieldError` crash on seek | Replaced with `SeekParameters(0L, 0L)` (zero tolerance, exact-seek semantics) |
| **2** | `DelayAudioProcessor` | Original `queueInput()` attempted to read-and-shift the internal `ByteBuffer` in place — caused `BufferUnderflowException` + garbled audio | Full rewrite: drains input → allocates fresh output buffer → writes delay-filled silence or passthrough samples correctly |
| **3** | `AudioDelayState` | Double milliseconds→microseconds conversion: delay was multiplied by 1000 twice, giving 1000× the intended value (3-second delay became 50-minute delay) | Remove one conversion stage; apply a single `delayMs * 1000L` for VLC's microsecond API |
| **4** | `VlcEngine.setSurface()` | Called `media.setHWDecoderEnabled(true, false)` on a null `media` reference when invoked before `setDataSource()` — caused `NullPointerException` black screen | Added `?: return` null guard before hardware-decoder configuration |
| **5** | `VlcPlayerEngine` media leak | `setDataSource()` released the old `Media` object but did not call `player.media = null`, leaving a dangling LibVLC reference and a native memory leak | Explicitly set `player.media = null` after `oldMedia.release()` |

### 🚀 LibVLC as Default Engine (Phase 7 Promotion)

Starting in v0.17.0, **every video intent is handled by `VlcPlayerActivity`** — a fully rewritten, Jetpack Compose-native player UI:

- **All video `ACTION_VIEW` / `ACTION_SEND` intents** now route through `PlayerActivity.onCreate()` Phase 7 block, which immediately forwards them to `VlcPlayerActivity` before ExoPlayer initializes.
- **`VlcPlayerActivity`** is a fully self-contained Compose UI with:
  - Gesture layer: **horizontal drag** → seek (±10 % per drag), **left vertical drag** → brightness, **right vertical drag** → volume, **double-tap left/right** → ±10 s seek
  - Semi-transparent overlay with **play/pause**, **seek bar** with current/total timestamps, **back button**, and **title**
  - Auto-hide controls (5-second timer, resets on any touch)
  - **Audio delay dialog** (±3000 ms slider, 100 ms steps, OK/Cancel)
  - **Picture-in-Picture** (`enterPictureInPictureMode`) with aspect-ratio-safe rational
  - **Background playback** via `VlcPlaybackService` (foreground service with `MediaSessionCompat` notification, play/pause/stop actions)
- **`VlcPlaybackService`** — a dedicated foreground service using `MediaSessionCompat` (not Media3) for the system notification:
  - `NOTIFICATION_ID = 1002` (separate from ExoPlayer's 1001)
  - Playlist-aware with next/prev support
  - Auto-stops when playback ends or the user dismisses the notification
- **`AndroidManifest.xml`** — `VlcPlaybackService` registered with `foregroundServiceType="mediaPlayback"` for Android 14+ compliance.
- ExoPlayer (`PlayerActivity`) remains available as a **fallback** for edge cases where no URI is present or for explicit ExoPlayer decoder-priority testing.

### 📦 Build stats

- versionCode `55` → `58`, versionName `0.16.0` → `0.17.0`
- 7 files modified (5 bug-fix commits + 2 architecture commits)
- 0 new external dependencies

---

## 🆕 What's New in v0.16.0 (8-Phase Overhaul)

v0.16.0 was the biggest release in the project's history — a single 8-phase master overhaul that touches the UI layer, audio engine, secret vault, network streaming, intent capture, video player core, P2P UX, and Live TV.

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

---

## 🔥 Why SHS Player?

Most Android players fall into two camps: feature-rich but bloated/proprietary (MX Player, VLC), or clean but minimal (Next Player, Just Player). SHS Player sits in the rare middle — a clean, modern, Material 3 UI with power-user capabilities that no other open-source player currently bundles together:

| Capability | SHS Player | Next Player | VLC | MX Player |
|---|:---:|:---:|:---:|:---:|
| Dual engine (LibVLC primary + Media3 fallback) | ✅ | ❌ | VLC only | Proprietary |
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
- **An IPTV module** that bundles 8+ free iptv-org playlists and parses any `.m3u` URL.
- **A Privacy Vault** that moves media into app-private storage behind a SHA-256 password and optional biometric unlock.
- **A Wi-Fi file transfer server** (NanoHTTPD) that lets you push/pull files from any browser on the same LAN — no internet, no cloud.
- **A QR scanner** (CameraX + MLKit) and a TrebleShot-compatible QR share format for direct device-to-device transfers.
- **LibVLC 3.6.0-eap5 as the primary engine** — handles the long tail of codecs, containers, and stream protocols that ExoPlayer can't.
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
- **LibVLC 3.6.0-eap5** ⭐ **(PRIMARY DEFAULT)** — all video intents are now routed to `VlcPlayerActivity`. Configured with `:input-fast-seek`, `--no-drop-late-frames`, hardware acceleration with software fallback, and network caching. VLC provides sample-accurate audio delay (microsecond precision), a native 10-band equalizer, and handles virtually every container and codec.
- **AndroidX Media3 / ExoPlayer 1.9.2** *(fallback)* — available via `PlayerActivity` for DASH, HLS, RTSP, SmoothStreaming, and files/streams that need Android's native codec pipeline. Falls back seamlessly when no URI is provided to `VlcPlayerActivity`.

**`VlcPlayerActivity` — Full Compose Player UI**

Introduced as the primary player in v0.17.0. Entirely written in Jetpack Compose:

- **Gesture layer** (draggable transparent overlay over the `AndroidView` surface):
  - **Horizontal drag** → seek (±10% of duration per full-width drag)
  - **Left vertical drag** → brightness (0–100%, persisted across sessions if "Remember brightness" is on)
  - **Right vertical drag** → volume (0–100%, system audio stream)
  - **Double-tap left zone** → rewind 10 s
  - **Double-tap right zone** → fast-forward 10 s
- **Controls overlay** (semi-transparent, auto-hides after 5 s):
  - Back arrow, video title
  - Play / Pause button
  - Seek bar with elapsed / total timestamps
  - Audio delay button → opens `AudioDelayDialog` (slider ±3000 ms, 100 ms steps)
  - PiP button → `enterPictureInPictureMode()` with safe aspect-ratio rational
- **Picture-in-Picture** — enters PiP on `onUserLeaveHint` and on explicit button tap; aspect ratio clamped to `[1/2.39, 2.39/1]` to satisfy Android's `IllegalArgumentException`.
- **Background playback** — connects to `VlcPlaybackService` on `onStart` / disconnects on `onStop`; the foreground service keeps audio alive when the screen is off.
- **SurfaceView lifecycle** — uses `AndroidView { SurfaceView }` with a `SurfaceHolder.Callback` that calls `VlcPlayerEngine.setSurface(holder)` only after the surface is created (guards against the null-media crash fixed in v0.17.0 bug #4).

**Playback controls** (shared across both engines)
- Play / pause / seek bar with 10-second seek increments (configurable 1–60 s).
- Fast seek with configurable threshold.
- Skip silence (`setSkipSilenceEnabled` in ExoPlayer; VLC handles natively).
- Long-press to fast-forward at a configurable speed (0.2×–4.0×).
- Background playback with branded media notification.
- Picture-in-Picture with custom `RemoteAction`s and 32-bit-safe aspect-ratio coercion.
- Loop modes: off / loop all / loop one.
- Shuffle.

**Audio features**
- **Audio track selector** — pick any embedded audio track; remembers last selection per file.
- **Audio delay / sync** — offset audio by −3000 ms to +3000 ms (VLC gives microsecond precision; fixed in v0.17.0 bug #2 and #3).
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
- **Ships with 8+ free iptv-org playlists** baked in: USA, India, UK, Sports, News, Movies, Kids, Music.
- **Searchable & grouped** by `group-title`.
- **Category tabs** — Bangladesh · Sports · News · Popular · Free Channels (heuristic resolver maps groups to tabs).
- **Launches into `VlcPlayerActivity`** for full-screen IPTV stream playback (benefits from VLC's superior RTSP/HLS/UDP support).
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
   │ LibVLC(primary) │ │ 8 pref screens │ │ Library browser   │
   │ + ExoPlayer(fb) │ │ ViewModels     │ │ Selection, sort   │
   │ 17 state holders│ │                │ │                   │
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
- **Single-Activity architecture** — `MainActivity` hosts a Compose `NavHost`; `VlcPlayerActivity`, `PlayerActivity`, and `AudioPlayerActivity` are separate full-screen activities.
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
| **Playback** | LibVLC ⭐ **(Primary)** | 3.6.0-eap5 |
| **Playback** | AndroidX Media3 (ExoPlayer, DASH, HLS, RTSP, Session, UI) | 1.9.2 |
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
│                   ├── HotspotManager.kt   # WifiManager.startLocalOnlyHotspot wrapper
│                   └── P2pPermissionSetupCard.kt
├── feature/
│   ├── player/                             # Player module
│   │   └── src/main/java/.../player/
│   │       ├── PlayerActivity.kt           # ExoPlayer host (fallback)
│   │       ├── AudioPlayerActivity.kt      # Audio-only player
│   │       ├── AudioPlayerScreen.kt        # Vinyl UI
│   │       └── engine/
│   │           ├── VlcPlayerActivity.kt    # ⭐ PRIMARY player (Compose UI + LibVLC)
│   │           ├── VlcPlayerEngine.kt      # LibVLC wrapper
│   │           ├── VlcPlaybackService.kt   # Foreground service (MediaSessionCompat)
│   │           └── ExoPlayerEngine.kt
│   ├── settings/                           # 8 settings screens
│   └── videopicker/                        # Media library browser
├── core/
│   ├── common/                             # Utilities, Logger, Dispatchers
│   ├── data/                               # Repositories, mappers
│   ├── database/                           # Room DB, DAOs, migrations
│   ├── datastore/                          # DataStore, serializers
│   ├── domain/                             # Use cases
│   ├── media/                              # MediaStore sync, MediaService
│   ├── model/                              # Pure Kotlin @Serializable types
│   └── ui/                                 # Theme, components, Glass UI kit
├── gradle/
│   ├── libs.versions.toml                  # Version catalog (single source of truth)
│   └── wrapper/
├── fastlane/                               # Fastlane metadata + Play Store screenshots
├── CONTRIBUTING.md
├── CODE_OF_CONDUCT.md
├── SECURITY.md
├── LICENSE
└── README.md
```

---

## 🏁 Getting Started

### Requirements
- **Android 6.0 (API 23)** or higher
- Storage permission (READ_EXTERNAL_STORAGE on API 28 and below; READ_MEDIA_VIDEO + READ_MEDIA_AUDIO on API 33+)

### Install
1. Download the correct APK from the [Releases page](https://github.com/The-JDdev/SHS-Player/releases).
2. Enable **Install unknown apps** for your browser in Android settings.
3. Install and launch.

No account, login, or internet connection required.

---

## 🛠 Building from Source

```bash
# Clone the repository
git clone https://github.com/The-JDdev/SHS-Player.git
cd SHS-Player

# Build a debug APK (per-ABI)
./gradlew assembleDebug

# Build a universal debug APK
./gradlew assembleDebug -PABI=universal

# Build a release-with-debug-signing APK (same R8 shrinking as release, debug keystore)
./gradlew assembleRelease-with-debug-signing

# Run ktlint check
./gradlew ktlintCheck

# Auto-fix ktlint violations
./gradlew ktlintFormat
```

> **Note:** The release build type (`assembleRelease`) requires a production signing key. The `release-with-debug-signing` build type uses the committed `app/debug.keystore` for CI and community builds.

### JDK
Requires **JDK 17**. Verify with `java -version`.

---

## 💻 Development

### IDE
**Android Studio Meerkat (2024.3.1)** or newer, with the Kotlin plugin matching the project's Kotlin version (2.3.0).

### Module graph
The project has strict module boundaries. Run `./gradlew dependencies --configuration releaseRuntimeClasspath` in any module to inspect its dependency graph. Never add `feature:*` → `feature:*` edges.

### Adding a new feature
1. Put UI in the appropriate `feature:*` module.
2. Put persistence (DAOs, entities) in `core:database`.
3. Put models in `core:model` (no Android imports).
4. Put business logic in `core:domain` (use cases) or `core:data` (repositories).
5. Inject via Hilt; expose to Compose via ViewModel + StateFlow.

### Code style
- `ktlint` 12.3.0. Run `./gradlew ktlintFormat` before committing.
- No raw `System.out.println` — use `req.log` in handlers, `android.util.Log` in Activities, the project `Logger` in domain/data.
- All new Compose screens must be `@Stable` or `@Immutable` for state holders.

---

## 🔐 Permissions Explained

| Permission | Why |
|---|---|
| `READ_MEDIA_VIDEO`, `READ_MEDIA_AUDIO` | Scan the device media library (API 33+) |
| `READ_EXTERNAL_STORAGE` | Scan the device media library (API ≤ 32) |
| `INTERNET` | IPTV stream playback, online subtitle search, yt-dlp binary auto-update |
| `ACCESS_WIFI_STATE`, `ACCESS_NETWORK_STATE` | Wi-Fi file transfer server; detect network type |
| `ACCESS_FINE_LOCATION` | Read SSID/BSSID for QR share format (Android requires location for Wi-Fi SSID) |
| `CHANGE_WIFI_STATE` | Enable Wi-Fi for the file transfer server |
| `CHANGE_NETWORK_STATE` | Manage network for hotspot mode |
| `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_MEDIA_PLAYBACK` | Background audio/video playback notification |
| `USE_BIOMETRIC`, `USE_FINGERPRINT` | Privacy Vault biometric unlock |
| `CAMERA` | QR scanner |
| `RECEIVE_BOOT_COMPLETED` | (Media library reindex after reboot — optional) |
| `VIBRATE` | Haptic feedback on selection actions |
| `REQUEST_INSTALL_PACKAGES` | yt-dlp binary update (downloaded from GitHub) |

---

## 🛡 Privacy & Security

- **No analytics, no crash reporting to a server, no ads, no tracking.** The crash reporter captures logcat and lets you share it manually — nothing is uploaded automatically.
- **Privacy Vault** stores files in app-private storage (`context.filesDir`). Other apps (including the gallery) cannot read these files. Uninstalling SHS Player permanently deletes them — **back up before uninstalling**.
- **Wi-Fi file transfer** binds to the local Wi-Fi interface only. The 16-character UUID auth token prevents unauthorized access. No cloud relay.
- **INTERNET permission** is used only for: IPTV stream fetching, OpenSubtitles API, and yt-dlp binary auto-update from GitHub. None of these transmit user data or device identifiers.
- **Location permission** is used only to read the Wi-Fi SSID/BSSID for the QR share code. The value is encoded into a QR bitmap shown on-screen; it is never stored or transmitted off the device.
- **Application ID** `dev.anilbeesetti.nextplayer` is retained from upstream for Play Store continuity — it does not imply any affiliation with the upstream author.

---

## 🌍 Internationalization

SHS Player is localised into **40+ languages** via Android string resources (`res/values-*/strings.xml`). Supported locales include but are not limited to: `bn` (Bengali), `zh-rCN`, `zh-rTW`, `de`, `es`, `fr`, `hi`, `id`, `it`, `ja`, `ko`, `nl`, `pl`, `pt-rBR`, `pt-rPT`, `ro`, `ru`, `sv`, `tr`, `uk`, `vi`, and many more.

To add or improve a translation:
1. Copy `app/src/main/res/values/strings.xml` to `values-<locale>/strings.xml`.
2. Translate all `<string>` values.
3. Open a PR with just the translation file — no code changes needed.

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

- **Supported versions:** the latest `0.17.x` release only.
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
- **The VideoLAN team** — for LibVLC, the primary engine that handles the full range of codecs and streaming protocols.
- **The AndroidX Media3 team** — for the ExoPlayer-based fallback playback stack.
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

**Q: Why two engines (LibVLC primary + ExoPlayer fallback)?**  
A: LibVLC handles virtually every container, codec, and streaming protocol — including niche formats, broken files, UDP multicast, and RTSP streams that ExoPlayer struggles with. As of v0.17.0, all video intents go to `VlcPlayerActivity` (LibVLC). ExoPlayer (`PlayerActivity`) remains available as a fallback for edge cases and explicit decoder-priority testing via Settings → Decoder.

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

**Q: The audio delay slider went wild in older versions — is it fixed?**  
A: Yes. v0.17.0 fixes bug #3 (double milliseconds→microseconds conversion) and bug #2 (garbled audio in `DelayAudioProcessor`). Audio delay now works precisely in both VLC (microsecond precision) and ExoPlayer paths.

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
- **v0.17.0** — LibVLC promoted to primary default engine; `VlcPlayerActivity` full Compose UI (gestures, overlay, PiP, audio delay dialog, background service); 5 critical bug fixes (SeekParameters, DelayAudioProcessor, AudioDelayState, VlcEngine null guard, media memory leak).
- **v0.16.0** — Dual-engine (ExoPlayer + VLC) architecture; Privacy Vault with biometric unlock; Wi-Fi file transfer with QR auth; IPTV / M3U browser with 8+ free playlists; Voice changer, AB-repeat, sleep timer, bookmarks, favourites; Video trim, video→audio, reverse play, screenshot, mirror; Online subtitle search (OpenSubtitles); Premium Cupertino + Material 3 hybrid theme; Custom crash reporter with logcat capture; 40+ language translations.

---

<div align="center">

**Built with 🔥 from Bangladesh 🇧🇩**

**By Sajjad Hussain Shobuj (SHS)**

*If SHS Player has empowered your workflow, please consider [supporting the project](#-support-the-project).*

[⬆ Back to top](#-shs-player)

</div>
