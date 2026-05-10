<div align="center">

<img src="https://i.postimeg.cc/7d5ede9bbeff4fecbfcbbaf8534dc6f1.jpg" alt="SHS Player Logo" width="160" style="border-radius: 20px; box-shadow: 0px 4px 10px rgba(0,0,0,0.5);">

# 🎬 SHS Player v0.15.7

### *The Ultimate Feature-Rich Multimedia Engine for Android*

**Not just a video player. A complete cinematic powerhouse with pro editing tools, built-in music engine, privacy vault, 1-click online subtitles, and Wi-Fi file sharing — all ad-free, forever.**

---

[![Version](https://img.shields.io/badge/Release-v0.15.7-0078D7?style=for-the-badge&logo=android)](https://github.com/hamsazzad/SHS-Player/releases/tag/v0.15.7)
[![License](https://img.shields.io/badge/License-GPL%20v3.0-28A745?style=for-the-badge)](https://www.gnu.org/licenses/gpl-3.0.html)
[![Min SDK](https://img.shields.io/badge/Min_SDK-23_(Android_6.0)-FF6F00?style=for-the-badge&logo=android)]
[![Target SDK](https://img.shields.io/badge/Target_SDK-36_(Android_16)-1565C0?style=for-the-badge&logo=android)]
[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)]
[![Compose](https://img.shields.io/badge/Jetpack_Compose-2026.01.00-4285F4?style=for-the-badge)]

**Created by [Sajjad Hussain Shobuj (SHS)](https://github.com/The-JDdev)** — Built from Bangladesh 🇧🇩

### 👇 Download Latest APK 👇

<a href="https://github.com/hamsazzad/SHS-Player/releases/tag/v0.15.7">
  <img src="https://img.shields.io/badge/📥_DOWNLOAD_LATEST_APK-28A745?style=for-the-badge&logo=android&logoColor=white" alt="Download APK" width="320">
</a>

[🐛 Report a Bug](https://github.com/The-JDdev/SHS-Player/issues) · [💬 Telegram](https://t.me/aamoviesofficial) · [📘 Facebook](https://fb.com/itsshsshobuj)

</div>

---

## 📋 Table of Contents

1. [🚀 What's New in v0.15.7 (Ultimate Control)](#-whats-new-in-v0157-the-ultimate-control-update)
2. [🌟 Previous Update Highlights (v0.15.4)](#-previous-update-highlights-v0154-god-level)
3. [🔥 Why SHS Player?](#-why-shs-player)
4. [📸 App Screenshots](#-app-screenshots)
5. [🍿 Cinematic Video Experience](#-cinematic-video-experience)
6. [🎬 The Creator's Arsenal (Pro Features)](#-the-creators-arsenal-pro-features)
7. [🎵 Complete Music Engine](#-complete-music-engine)
8. [🔒 Privacy Vault](#-privacy-vault)
9. [📡 Wi-Fi File Transfer](#-wi-fi-file-transfer)
10. [📱 Modern & Intuitive UI](#-modern--intuitive-ui)
11. [🎨 Appearance & Theming](#-appearance--theming)
12. [⚙️ Complete Settings Reference](#️-complete-settings-reference)
13. [🏗️ Architecture Deep Dive](#️-architecture-deep-dive)
14. [🧩 Module Structure](#-module-structure)
15. [📚 Tech Stack & Dependencies](#-tech-stack--dependencies)
16. [🔧 Build Variants & Configuration](#-build-variants--configuration)
17. [🤖 CI/CD Pipeline & Fastlane](#-cicd-pipeline--fastlane-deployment)
18. [🌍 Translations (40+ Languages)](#-translations-40-languages)
19. [🤝 Community & Support](#-community--support)
20. [📜 License & Privacy](#-license--privacy)

---

## 🚀 What's New in v0.15.7 (The "Ultimate Control" Update)

This massive update completely overhauls the internal logic, fixes core bugs, and adds highly requested pro-level features.

- **🌐 1-Click Online Subtitles:** Deep OpenSubtitles API integration. Search, download, and instantly inject subtitles into ExoPlayer without ever leaving or reloading the video.
- **🎧 Audio Sync & Equalizers:** Audio out of sync? Fix it instantly with the new millisecond Audio Delay/Forward offset. Added native Android `Equalizer` API for deep audio frequency tuning.
- **🎵 Dedicated Audio UI & Animations:** The audio player is no longer a clone of the video player. Experience a beautifully dedicated Audio UI with spinning album art, previous/next controls, and an interactive playlist queue.
- **📡 Rewritten Wi-Fi File Transfer:** Completely rebuilt from scratch! Powered by ZXing for QR generation, CameraX + MLKit for instant scanning, and NanoHTTPD for rock-solid local cross-platform transfers.
- **🎛️ Customizable Player UI:** Your player, your rules. Edit, remove, and rearrange buttons directly on the player menu.
- **📜 Fast Scroll Gesture:** Effortlessly navigate massive media libraries using the new side-drag fast scroller on all RecyclerViews.
- **🔒 Privacy Vault Crushed Bugs:** Fixed the notorious restore bug by strictly enforcing `MediaScannerConnection`. Vault media now securely plays *internally* via `FileProvider` instead of leaking to external players.
- **🎨 Theme Engine Fixed:** `SharedPreferences` logic rewritten. Dynamic colors and themes now instantly apply globally across the entire app.

---

## 🌟 Previous Update Highlights (v0.15.4 God Level)

- **🔊 EAC3 / Dolby Audio Supported Out-of-the-Box:** No more "Audio format not supported" errors. Hardware/software decoding powered by nextlib's FFmpeg integration.
- **⚡ Context-Aware Playback Queue:** Intelligently syncs your queue based on where you started (Folder, Playlist, Favorites).
- **☑️ Bulk Selection (Audio & Video):** Batch manage favorites and playlists in a single tap.
- **📂 Advanced Folder Navigation:** Click a folder to navigate *into* it seamlessly.

---

## 🔥 Why SHS Player?

Built on the robust core of **Media3 (ExoPlayer)** and powered by **nextlib's FFmpeg integration**, SHS Player breaks the limits of standard video players. Whether you are extracting the perfect high-res screenshot, decoding 10-bit HEVC files, syncing audio tracks, or sharing files over LAN — this is your all-in-one hub.

**SHS Player vs. Other Players:**

| Feature | SHS Player | VLC | MX Player | mpv |
|:---|:---:|:---:|:---:|:---:|
| EAC3/Dolby Audio | ✅ | ✅ | 💰 Paid | ❌ |
| 1-Click Subtitles | ✅ | ✅ | ✅ | ❌ |
| Audio Delay/Sync | ✅ | ✅ | ✅ | ✅ |
| Custom UI Buttons | ✅ | ❌ | ❌ | ❌ |
| Screenshot Capture | ✅ | ❌ | ✅ | ❌ |
| Video-to-Audio | ✅ | ❌ | 💰 Paid | ❌ |
| Dedicated Audio UI | ✅ | ✅ | ❌ | ❌ |
| Privacy Vault | ✅ | ❌ | ❌ | ❌ |
| Wi-Fi Transfer | ✅ | ❌ | ❌ | ❌ |
| Material You Theme | ✅ | ❌ | ❌ | ❌ |
| Ad-Free | ✅ | ✅ | ❌ | ✅ |

---

## 📸 App Screenshots

*A glimpse into the ultimate media experience.*

<div align="center">
  <table>
    <tr>
      <td align="center"><img src="https://i.postimeg.cc/a76f7309a65a4a4889aa7503a982a938.jpg" width="250" alt="Home Screen"/><br><b>Home Screen</b></td>
      <td align="center"><img src="https://i.postimeg.cc/bc9475c4ccbb4935bf3e8f718f8ae0f8.jpg" width="250" alt="Quick Settings"/><br><b>Video Quick Settings</b></td>
      <td align="center"><img src="https://i.postimeg.cc/0a4bc2c38b0e4cc4ab99c9b493a73ef2.jpg" width="250" alt="Video Player"/><br><b>Video Player</b></td>
    </tr>
    <tr>
      <td align="center"><img src="https://i.postimeg.cc/fa3d430185b348978c6ddd50edaa97c8.jpg" width="250" alt="Player Setting"/><br><b>Player Settings</b></td>
      <td align="center"><img src="https://i.postimeg.cc/3b394f2da2e7425fbbf8c2de2ba0242c.jpg" width="250" alt="Music List"/><br><b>Music Library</b></td>
      <td align="center"><img src="https://i.postimeg.cc/2141ab4ceaa24c5391c421485d9c759f.jpg" width="250" alt="Music Folders"/><br><b>Music Folders</b></td>
    </tr>
    <tr>
      <td align="center"><img src="https://i.postimeg.cc/ed55e0794a6b408a8ecf158dcb31d1b3.jpg" width="250" alt="Music Settings"/><br><b>Music Settings</b></td>
      <td align="center"><img src="https://i.postimeg.cc/cc44d142a49f4df7bfc509f1c54dc71b.jpg" width="250" alt="About Page"/><br><b>About & Contact</b></td>
      <td align="center"><img src="https://i.postimeg.cc/1c3a2027f651467bbe001fb6d1a479e2.jpg" width="250" alt="Settings"/><br><b>Main Settings</b></td>
    </tr>
  </table>
</div>

---

## 🍿 Cinematic Video Experience

> Experience media without limits, lags, or ads. Every frame, every codec, every format — handled.

### Decoder Engine & Streaming

| Feature | Detail |
|:---|:---|
| <kbd>HW/SW Decoder Switch</kbd> | Force hardware or software decoding for heavy files. |
| <kbd>EAC3 / Dolby Audio</kbd> | Direct support via FFmpeg integration. |
| <kbd>HDR & 10-bit HEVC</kbd> | Hardware-accelerated High Efficiency Video Coding. |
| <kbd>Network Protocols</kbd> | Seamlessly stream RTSP, RTMP, HLS, DASH, HTTP/HTTPS, MMS, and UDP. |

### Player Controls & Gestures

| Control | How to Use |
|:---|:---|
| **Custom Menu** | Edit, remove, and rearrange buttons directly on the player! |
| **Volume & Brightness** | Swipe right/left edges of the screen. |
| **Seek & Preview** | Swipe left/right, or hold to preview seek position. |
| **Double Tap** | Configurable FF/Rewind or Play/Pause. |
| **Long Press Boost** | Temporarily play at 2x–4x speed. |
| **Pinch & Pan** | Zoom into video with two fingers and drag. |

### Ultimate Subtitle System

A unified, 1-Click subtitle engine built for power users:

| Feature | Detail |
|:---|:---|
| **1-Click Online** | Search & inject subtitles via OpenSubtitles API instantly. |
| **Unified Menu** | No more scattered settings; everything is inside the player menu. |
| **Formats** | SRT, ASS, SSA, VTT, TTML + Embedded Tracks. |
| **Deep Customization**| Font family, size, background opacity, and encoding overrides. |

### Video & Audio Effects

| Effect | Description |
|:---|:---|
| 🎧 **Audio Sync** | Delay/Forward audio tracks in milliseconds to fix desync. |
| 🎛️ **Audio/Video EQ** | Tune Brightness/Contrast/Saturation and Audio Frequencies on the fly. Save your presets. |
| 🎙️ **Voice Changer** | Real-time audio effects: Normal, Deep, Robot, Child, Echo. |
| 🔊 **Volume Boost** | Boost volume up to 200%. |

---

## 🎬 The Creator's Arsenal (Pro Features)

| Feature | Description |
|:---|:---|
| 📸 **One-Tap Screenshot** | Pixel-perfect frames during playback using PixelCopy API. |
| ✂️ **Video Trim/Cut** | Export specific clips directly using MediaMuxer — no quality loss. |
| 🎵 **Video-to-Audio** | Extract audio track and save as M4A. |
| 🔁 **A-B Repeat** | Set specific start (A) and end (B) points for seamless looping. |
| 🪞 **Mirror Mode** | Flip video horizontally for composition or dance practice. |
| 🔖 **Advanced Bookmarks** | Save custom timestamps with labels. |

---

## 🎵 Complete Music Engine

*A fully separate, beautifully animated Dedicated Audio UI.*

| Feature | Description |
|:---|:---|
| **Dedicated Player** | Clean UI with spinning album art, progress bar, and no black video surfaces. |
| **Smart Navigation** | Click a folder to enter a dedicated view. |
| **Fast Scroll Gesture**| Drag the side scrollbar to instantly jump to the bottom of massive lists. |
| **Queue & Playlist** | View your active queue directly inside the player UI. |
| **Background Play** | Seamless audio continuity via Media3 Session. |

---

## 🔒 Privacy Vault

*A password-protected, biometric-secured vault that works flawlessly.*

| Feature | Detail |
|:---|:---|
| 👆 **Biometric Auth** | Unlock with fingerprint via AndroidX Biometric API. |
| ❓ **Security Question** | Password recovery system — never get locked out. |
| ▶️ **Play Internal** | Vault media plays safely *inside* the app via `FileProvider` (blocks external leaks). |
| 📤 **Seamless Restore** | strict `MediaScannerConnection` triggers instantly update your gallery upon restore. |
| 🛡️ **App-Private Storage**| Invisible to gallery apps and file managers. |

---

## 📡 Wi-Fi File Transfer

*Send and receive files between devices over Wi-Fi — completely rewritten with advanced security.*

| Feature | Detail |
|:---|:---|
| 📷 **CameraX + MLKit** | Instant, lightning-fast QR barcode scanning. |
| 📱 **ZXing QR Gen** | Auto-generate QR codes with server URL & auth tokens. |
| 📡 **NanoHTTPD Server** | Start a local HTTP server on port 8080. |
| 🔑 **Auth Token Security**| Unique tokens generated per session to block unauthorized uploads. |
| 🛡️ **Path Traversal Shield**| Strict filename sanitization to prevent directory escape attacks. |
| 🌐 **Cross-Platform** | Transfer files to Android, iOS, PC, Mac, Linux. |

### How It Works
```text
┌──────────────────┐    QR Code / URL     ┌──────────────────┐
│   Device A       │ ──────────────────▶  │   Device B       │
│   (Sender)       │                      │   (Receiver)     │
│                  │    HTTP Upload       │                  │
│   Select Files   │ ──────────────────▶  │   HTTP Server    │
│   Scan QR        │    (Wi-Fi LAN)       │   :8080          │
│   Upload         │                      │   Save to /vault │
└──────────────────┘                      └──────────────────┘
```

---

## 📱 Modern & Intuitive UI

<details>
<summary><b>✨ Click to expand — Full UI & Navigation Details</b></summary>

### Bottom Navigation Bar
| Tab | Icon | Purpose |
|:---|:---|:---|
| **Videos** | 🎬 | Browse video library, tree navigation, quick settings |
| **Music** | 🎵 | Full music player with library management |
| **Me** | 👤 | Privacy Vault, File Transfer, About, Settings |
| **Telegram** | 💬 | Developer info, community, donations |

### Browser & Layouts
- **Folder / Tree View:** Deep navigation.
- **Fast Scroller:** Side drag gesture for quick list jumping.
- **Thumbnail Previews:** Coil-powered caching.
</details>

---

## 🎨 Appearance & Theming

<details>
<summary><b>🎨 Click to expand — Theming & Visual Design</b></summary>

- **Theme Engine Fixed:** `SharedPreferences` instantly applies themes globally without needing app restarts.
- **Material You:** Dynamic colors adapt to your wallpaper (Android 12+).
- **Modes:** System Default / Light / Dark / High Contrast Dark.
- **Splash Screen:** AndroidX Splashscreen API for smooth branding transitions.

</details>

---

## ⚙️ Complete Settings Reference

<details>
<summary><b>⚙️ Click to expand — All Settings</b></summary>

9 comprehensive categories including: Appearance, Media Library, Player, Decoder, Audio, Subtitle, General, About, and Libraries. Full granular control over your media experience.
</details>

---

## 🏗️ Architecture Deep Dive

<details>
<summary><b>🏗️ Click to expand — Architecture, Patterns & Data Flow</b></summary>

Follows **Clean Architecture** across 11 Gradle modules.
- **MVVM** + StateFlow
- **Hilt Dependency Injection**
- **Room Database** (v4 Schema Migrations)
- **Media3 Session** for Background Services
- **Global Crash Handler**
</details>

---

## 🧩 Module Structure

<details>
<summary><b>🧩 Click to expand — All 11 Modules</b></summary>

Structured cleanly into `:app`, `:core:common`, `:core:data`, `:core:database`, `:core:datastore`, `:core:domain`, `:core:media`, `:core:model`, `:core:ui`, `:feature:player`, `:feature:settings`, `:feature:videopicker`.
</details>

---

## 📚 Tech Stack & Dependencies

<details>
<summary><b>📚 Click to expand — Complete Tech Stack</b></summary>

- **Kotlin 2.3.0** & **Compose BOM 2026.01.00**
- **Media3 (ExoPlayer) 1.9.2** + **NextLib FFmpeg**
- **Room 2.8.4**, **DataStore 1.2.0**
- **CameraX 1.4.1** + **MLKit 17.3.0** + **ZXing**
- **OpenSubtitles REST API**, **NanoHTTPD**
</details>

---

## 🔧 Build Variants & Configuration

<details>
<summary><b>🔧 Click to expand — Build Configuration</b></summary>

Targeting SDK 36 (Android 16), JVM 17. Supports all ABIs (`arm64-v8a`, `armeabi-v7a`, `x86`, `x86_64`). Build with `./gradlew assembleRelease`.
</details>

---

## 🤖 CI/CD Pipeline & Fastlane Deployment

<details>
<summary><b>🤖 Click to expand — GitHub Actions & Fastlane</b></summary>

Automated builds, APK testing, version bumping, and automated publishing to the Google Play Store (Internal Track) via Fastlane and GitHub Actions.
</details>

---

## 🌍 Translations (40+ Languages)

SHS Player is translated into 40+ languages natively, including Bengali (`bn`), Hindi (`hi`), Spanish (`es`), and more.

---

## 🤝 Community & Support

Be a part of our ecosystem! Get the latest updates, movie drops, and directly connect with the developer.

<div align="center">

[![Join Telegram](https://img.shields.io/badge/Join_Our_Telegram-2CA5E0?style=for-the-badge&logo=telegram&logoColor=white)](https://t.me/aamoviesofficial)
[![Facebook](https://img.shields.io/badge/Facebook-1877F2?style=for-the-badge&logo=facebook&logoColor=white)](https://fb.com/itsshsshobuj)
[![GitHub](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/The-JDdev)

</div>

### 💎 Donation Vault

If SHS Player has empowered your workflow, your support directly funds continued development, server costs, API testing, and new feature research. Every contribution — no matter the size — is felt by a solo developer building something real from a smartphone in Bangladesh.

- **USDT (TRC20 — Tron Network):** `TH75J4zaMPwhyR3QxEFdwTCgU2Pp3yPUEr`
- **WebMoney WMT (Tether):** `T202226490170`
- **WebMoney WMZ (USD):** `Z430378899900`
- **bKash (Bangladesh Local):** `01310211442`

**Developer:** Sajjad Hussain Shobuj (SHS)
*Bringing professional tools to your fingertips — from Bangladesh, on a smartphone.* 🇧🇩

---

## 📜 License & Privacy

<details>
<summary><b>📜 License</b></summary>

SHS Player is licensed under the **MIT**. Full license text: [LICENSE](LICENSE). Forked from Next Player by Anil Kumar Beesetti.
</details>

<details>
<summary><b>🔒 Privacy Policy</b></summary>

SHS Player **does not collect or access any personal information**. All processing happens locally on your device. Wi-Fi File Transfer stays strictly on your local network. Privacy Vault files are encrypted in App-Private Storage.
</details>

---

<div align="center">

**Built with 🔥 from Bangladesh · By Sajjad Hussain Shobuj (SHS)**

*"In a world of ad-filled players, be the engine."*

[![Star this repo](https://img.shields.io/github/stars/The-JDdev/SHS-Player?style=social)](https://github.com/The-JDdev/SHS-Player)
[![Forks](https://img.shields.io/github/forks/The-JDdev/SHS-Player?style=social)](https://github.com/The-JDdev/SHS-Player)

</div>
