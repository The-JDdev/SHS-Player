package dev.anilbeesetti.nextplayer.ui.tv

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * M3U/M3U8 playlist parser for live IPTV streams.
 */
data class IptvChannel(
    val name: String,
    val url: String,
    val logoUrl: String? = null,
    val group: String? = null,
    val tvgId: String? = null,
    val tvgName: String? = null,
)

object M3uParser {

    suspend fun parse(context: Context, source: String): List<IptvChannel> = withContext(Dispatchers.IO) {
        runCatching {
            val content = when {
                source.startsWith("http://") || source.startsWith("https://") -> fetchRemote(source)
                source.startsWith("content://") -> {
                    context.contentResolver.openInputStream(Uri.parse(source))?.use { input ->
                        BufferedReader(InputStreamReader(input)).readText()
                    } ?: ""
                }
                source.startsWith("file://") -> {
                    java.io.File(Uri.parse(source).path ?: "").readText()
                }
                else -> {
                    val f = java.io.File(source)
                    if (f.exists()) f.readText() else ""
                }
            }
            parseContent(content)
        }.getOrDefault(emptyList())
    }

    fun parseContent(content: String): List<IptvChannel> {
        val channels = mutableListOf<IptvChannel>()
        val lines = content.lines()
        var currentName: String? = null
        var currentLogo: String? = null
        var currentGroup: String? = null
        var currentTvgId: String? = null
        var currentTvgName: String? = null

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) continue

            if (trimmed.startsWith("#EXTINF", ignoreCase = true)) {
                val commaIndex = trimmed.indexOf(',')
                val attributesPart = if (commaIndex > 0) trimmed.substring(0, commaIndex) else trimmed
                val namePart = if (commaIndex > 0) trimmed.substring(commaIndex + 1).trim() else "Unknown"

                currentName = namePart
                currentLogo = extractAttribute(attributesPart, "tvg-logo")
                currentGroup = extractAttribute(attributesPart, "group-title")
                currentTvgId = extractAttribute(attributesPart, "tvg-id")
                currentTvgName = extractAttribute(attributesPart, "tvg-name")
            } else if (trimmed.startsWith("#")) {
                continue
            } else {
                if (currentName != null && (trimmed.startsWith("http") || trimmed.startsWith("rtmp") ||
                        trimmed.startsWith("rtsp") || trimmed.startsWith("udp"))
                ) {
                    channels.add(
                        IptvChannel(
                            name = currentName,
                            url = trimmed,
                            logoUrl = currentLogo,
                            group = currentGroup,
                            tvgId = currentTvgId,
                            tvgName = currentTvgName,
                        ),
                    )
                }
                currentName = null
                currentLogo = null
                currentGroup = null
                currentTvgId = null
                currentTvgName = null
            }
        }
        return channels
    }

    private fun extractAttribute(text: String, attrName: String): String? {
        val key = "$attrName=\""
        val start = text.indexOf(key, ignoreCase = true)
        if (start < 0) return null
        val valueStart = start + key.length
        val end = text.indexOf('"', valueStart)
        if (end < 0) return null
        return text.substring(valueStart, end).takeIf { it.isNotBlank() }
    }

    private fun fetchRemote(urlStr: String): String {
        val url = URL(urlStr)
        val conn = url.openConnection() as HttpURLConnection
        conn.connectTimeout = 15000
        conn.readTimeout = 15000
        conn.requestMethod = "GET"
        conn.setRequestProperty("User-Agent", "SHSPlayer/1.4")
        return try {
            conn.inputStream.bufferedReader().readText()
        } finally {
            conn.disconnect()
        }
    }
}

object DefaultIptvPlaylists {
    data class Playlist(val name: String, val url: String)

    val playlists = listOf(
        Playlist("Free TV — USA", "https://iptv-org.github.io/iptv/countries/us.m3u"),
        Playlist("Free TV — India", "https://iptv-org.github.io/iptv/countries/in.m3u"),
        Playlist("Free TV — UK", "https://iptv-org.github.io/iptv/countries/uk.m3u"),
        Playlist("Free TV — Sports", "https://iptv-org.github.io/iptv/categories/sports.m3u"),
        Playlist("Free TV — News", "https://iptv-org.github.io/iptv/categories/news.m3u"),
        Playlist("Free TV — Movies", "https://iptv-org.github.io/iptv/categories/movies.m3u"),
        Playlist("Free TV — Kids", "https://iptv-org.github.io/iptv/categories/kids.m3u"),
        Playlist("Free TV — Music", "https://iptv-org.github.io/iptv/categories/music.m3u"),
    )
}
