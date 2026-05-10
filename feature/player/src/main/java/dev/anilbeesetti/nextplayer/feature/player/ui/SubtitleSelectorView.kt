package dev.anilbeesetti.nextplayer.feature.player.ui

import androidx.annotation.OptIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.C
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import dev.anilbeesetti.nextplayer.core.ui.R
import dev.anilbeesetti.nextplayer.feature.player.extensions.getName
import dev.anilbeesetti.nextplayer.feature.player.state.rememberTracksState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

private const val OPENSUB_API_KEY = "HTFDc21sjzlEMlBg9YGIv4ERCmSUFXYD"
private const val OPENSUB_BASE = "https://api.opensubtitles.com/api/v1"

// ── Data models ────────────────────────────────────────────────────────────────
data class OnlineSubtitle(
    val id: String,
    val fileName: String,
    val language: String,
    val rating: String,
    val downloadUrl: String,
    val fileId: Int,
)

// ── Network helpers ────────────────────────────────────────────────────────────
private suspend fun searchOpenSubtitles(query: String): List<OnlineSubtitle> =
    withContext(Dispatchers.IO) {
        try {
            val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
            val url = URL("$OPENSUB_BASE/subtitles?query=$encodedQuery&languages=en")
            val conn = url.openConnection() as HttpURLConnection
            conn.setRequestProperty("Api-Key", OPENSUB_API_KEY)
            conn.setRequestProperty("User-Agent", "SHSPlayer v1")
            conn.setRequestProperty("Content-Type", "application/json")
            conn.connectTimeout = 10_000
            conn.readTimeout = 10_000
            val response = conn.inputStream.bufferedReader().readText()
            conn.disconnect()
            val root = JSONObject(response)
            val arr = root.getJSONArray("data")
            (0 until arr.length()).mapNotNull { i ->
                runCatching {
                    val item = arr.getJSONObject(i)
                    val attrs = item.getJSONObject("attributes")
                    val files = attrs.getJSONArray("files")
                    if (files.length() == 0) return@mapNotNull null
                    val file = files.getJSONObject(0)
                    OnlineSubtitle(
                        id = item.getString("id"),
                        fileName = file.optString("file_name", "subtitle.srt"),
                        language = attrs.optString("language", "en"),
                        rating = attrs.optDouble("ratings", 0.0).toString(),
                        downloadUrl = "",
                        fileId = file.getInt("file_id"),
                    )
                }.getOrNull()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

private suspend fun downloadSubtitle(context: android.content.Context, fileId: Int, fileName: String): File? =
    withContext(Dispatchers.IO) {
        try {
            // Step 1: Get download link
            val url = URL("$OPENSUB_BASE/download")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Api-Key", OPENSUB_API_KEY)
            conn.setRequestProperty("User-Agent", "SHSPlayer v1")
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true
            conn.connectTimeout = 10_000
            conn.readTimeout = 10_000
            val body = """{"file_id":$fileId}"""
            conn.outputStream.use { it.write(body.toByteArray()) }
            val response = conn.inputStream.bufferedReader().readText()
            conn.disconnect()
            val link = JSONObject(response).getString("link")
            // Step 2: Download the file
            val downConn = URL(link).openConnection() as HttpURLConnection
            downConn.connectTimeout = 15_000
            downConn.readTimeout = 30_000
            val subDir = File(context.cacheDir, "subtitles")
            subDir.mkdirs()
            val outFile = File(subDir, fileName.ifBlank { "subtitle_$fileId.srt" })
            downConn.inputStream.use { input ->
                outFile.outputStream().use { out -> input.copyTo(out) }
            }
            downConn.disconnect()
            outFile
        } catch (e: Exception) {
            null
        }
    }

// ── Main composable ────────────────────────────────────────────────────────────
@OptIn(UnstableApi::class)
@Composable
fun BoxScope.SubtitleSelectorView(
    modifier: Modifier = Modifier,
    show: Boolean,
    player: Player,
    onSelectSubtitleClick: () -> Unit,
    onSubtitleFileReady: (android.net.Uri) -> Unit = {},
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val subtitleTracksState = rememberTracksState(player, C.TRACK_TYPE_TEXT)
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) }

    // Online search state
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var searchResults by remember { mutableStateOf<List<OnlineSubtitle>>(emptyList()) }
    var searchError by remember { mutableStateOf("") }
    var downloadingId by remember { mutableStateOf<String?>(null) }

    OverlayView(
        modifier = modifier,
        show = show,
        title = stringResource(R.string.select_subtitle_track),
    ) {
        // ── Tab bar ────────────────────────────────────────────────────────
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Text("Embedded", modifier = Modifier.padding(12.dp))
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text("Online", modifier = Modifier.padding(12.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        when (selectedTab) {
            // ── Tab 0: Embedded subtitle tracks ───────────────────────────
            0 -> {
                Column(modifier = Modifier.selectableGroup()) {
                    subtitleTracksState.tracks.forEachIndexed { index, track ->
                        RadioButtonRow(
                            selected = track.isSelected,
                            text = track.mediaTrackGroup.getName(C.TRACK_TYPE_TEXT, index),
                            onClick = {
                                subtitleTracksState.switchTrack(index)
                                onDismiss()
                            },
                        )
                    }
                    RadioButtonRow(
                        selected = subtitleTracksState.tracks.none { it.isSelected },
                        text = stringResource(R.string.disable),
                        onClick = {
                            subtitleTracksState.switchTrack(-1)
                            onDismiss()
                        },
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                FilledTonalButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onSelectSubtitleClick(); onDismiss() },
                ) {
                    Text(text = stringResource(R.string.open_subtitle))
                }
            }

            // ── Tab 1: OpenSubtitles online search ────────────────────────
            1 -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Movie / episode name") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            if (searchQuery.isNotBlank()) {
                                isSearching = true; searchError = ""
                                scope.launch {
                                    searchResults = searchOpenSubtitles(searchQuery)
                                    isSearching = false
                                    if (searchResults.isEmpty()) searchError = "No results found."
                                }
                            }
                        }),
                    )
                    IconButton(
                        onClick = {
                            if (searchQuery.isNotBlank()) {
                                isSearching = true; searchError = ""
                                scope.launch {
                                    searchResults = searchOpenSubtitles(searchQuery)
                                    isSearching = false
                                    if (searchResults.isEmpty()) searchError = "No results found."
                                }
                            }
                        },
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_info),
                            contentDescription = "Search",
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                when {
                    isSearching -> Box(
                        modifier = Modifier.fillMaxWidth().height(80.dp),
                        contentAlignment = Alignment.Center,
                    ) { CircularProgressIndicator(modifier = Modifier.size(32.dp)) }

                    searchError.isNotEmpty() -> Text(
                        searchError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                    )

                    searchResults.isNotEmpty() -> LazyColumn(
                        modifier = Modifier.fillMaxWidth().heightIn(max = 320.dp),
                    ) {
                        items(searchResults, key = { it.id }) { sub ->
                            val isDownloading = downloadingId == sub.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = !isDownloading) {
                                        downloadingId = sub.id
                                        scope.launch {
                                            val file = downloadSubtitle(context, sub.fileId, sub.fileName)
                                            downloadingId = null
                                            if (file != null) {
                                                val uri = androidx.core.content.FileProvider.getUriForFile(
                                                    context,
                                                    "${context.packageName}.fileprovider",
                                                    file,
                                                )
                                                // Inject subtitle without reloading video
                                                onSubtitleFileReady(uri)
                                                onDismiss()
                                            } else {
                                                searchError = "Download failed. Try another."
                                            }
                                        }
                                    }
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        sub.fileName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Text(
                                        "${sub.language.uppercase()} · ★ ${sub.rating}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                if (isDownloading) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                }
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}
