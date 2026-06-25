package dev.anilbeesetti.nextplayer.feature.player.download

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.io.File

/**
 * Phase 4 — UI for the universal downloader.
 *
 * Shown when the user opens a stream in SHS Player and taps the "Download"
 * action button. The dialog:
 *   1. Extracts available formats via [UniversalDownloader.extractStreamInfo].
 *   2. Lists them with codec + size so the user can pick the best quality.
 *   3. Downloads the chosen format to `Movies/SHSPlayer/` with a live progress bar.
 */
@Composable
fun DownloadStreamDialog(
    url: String,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val downloader = remember { UniversalDownloader(context.applicationContext) }

    var loading by remember { mutableStateOf(true) }
    var streamInfo by remember { mutableStateOf<StreamInfo?>(null) }
    var extractError by remember { mutableStateOf<String?>(null) }

    var selectedFormat by remember { mutableStateOf<VideoFormat?>(null) }
    var downloadProgress by remember { mutableStateOf<DownloadProgress?>(null) }
    var downloadDone by remember { mutableStateOf(false) }
    var downloadError by remember { mutableStateOf<String?>(null) }

    // Trigger yt-dlp auto-update on dialog open (best-effort, won't block).
    LaunchedEffect(Unit) {
        downloader.updateYtDlpIfNeeded()
    }

    // Extract stream info when dialog opens
    LaunchedEffect(url) {
        loading = true
        extractError = null
        val info = downloader.extractStreamInfo(url)
        if (info == null) {
            // Fallback — pretend there's a single "best" format (direct download)
            streamInfo = StreamInfo(
                url = url,
                title = url.substringAfterLast('/', "stream"),
                thumbnail = null,
                duration = 0L,
                formats = listOf(
                    VideoFormat(
                        formatId = "direct",
                        extension = url.substringAfterLast('.', "mp4"),
                        note = "Direct stream",
                        vcodec = "",
                        acodec = "",
                        filesize = 0L,
                    ),
                ),
            )
        } else {
            streamInfo = info
        }
        loading = false
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Download Stream") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    streamInfo?.title ?: "Loading…",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                )

                when {
                    loading -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.height(24.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("Extracting formats…", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    extractError != null -> {
                        Text("Error: $extractError", color = MaterialTheme.colorScheme.error)
                    }
                    downloadDone -> {
                        Text("Download complete!", color = MaterialTheme.colorScheme.primary)
                    }
                    downloadProgress != null -> {
                        val p = downloadProgress!!
                        if (p.totalBytes > 0) {
                            LinearProgressIndicator(
                                progress = { (p.downloadedBytes.toFloat() / p.totalBytes).coerceIn(0f, 1f) },
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Text(
                                "${p.downloadedBytes / 1024} KB / ${p.totalBytes / 1024} KB",
                                style = MaterialTheme.typography.bodySmall,
                            )
                        } else {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                            Text("${p.downloadedBytes / 1024} KB", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    selectedFormat == null -> {
                        val formats = streamInfo?.formats ?: emptyList()
                        LazyColumn(modifier = Modifier.height(220.dp)) {
                            items(formats) { fmt ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            "${fmt.formatId} • ${fmt.extension} • ${fmt.note}",
                                            style = MaterialTheme.typography.bodySmall,
                                        )
                                        if (fmt.filesize > 0) {
                                            Text(
                                                "${fmt.filesize / 1024 / 1024} MB",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                    }
                                    OutlinedButton(
                                        onClick = { selectedFormat = fmt },
                                    ) { Text("Pick") }
                                }
                            }
                        }
                    }
                    else -> {
                        Text(
                            "Ready: ${selectedFormat!!.formatId} (.${selectedFormat!!.extension})",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }

                downloadError?.let {
                    Text("Error: $it", color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            if (selectedFormat != null && !downloadDone && downloadProgress == null) {
                Button(
                    onClick = {
                        val fmt = selectedFormat!!
                        val targetDir = File(
                            android.os.Environment.getExternalStoragePublicDirectory(
                                android.os.Environment.DIRECTORY_MOVIES,
                            ),
                            "SHSPlayer",
                        ).apply { mkdirs() }
                        val filename = (streamInfo?.title?.take(60)?.replace(Regex("[^A-Za-z0-9._-]"), "_")
                            ?: "shs_download") + "." + fmt.extension
                        val targetFile = File(targetDir, filename)
                        scope.launch {
                            val ok = downloader.download(
                                url = url,
                                formatId = fmt.formatId,
                                targetFile = targetFile,
                            ) { progress ->
                                downloadProgress = progress
                            }
                            if (ok) downloadDone = true
                            else downloadError = "Download failed"
                        }
                    },
                ) { Text("Start Download") }
            } else if (downloadDone) {
                Button(onClick = onDismiss) { Text("Done") }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}
