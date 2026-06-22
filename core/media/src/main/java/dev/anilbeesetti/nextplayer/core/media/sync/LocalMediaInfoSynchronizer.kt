package dev.anilbeesetti.nextplayer.core.media.sync

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.anilbeesetti.nextplayer.core.common.Dispatcher
import dev.anilbeesetti.nextplayer.core.common.NextDispatchers
import dev.anilbeesetti.nextplayer.core.common.di.ApplicationScope
import dev.anilbeesetti.nextplayer.core.common.extensions.deleteFiles
import dev.anilbeesetti.nextplayer.core.common.extensions.thumbnailCacheDir
import dev.anilbeesetti.nextplayer.core.database.dao.MediumDao
import dev.anilbeesetti.nextplayer.core.database.entities.AudioStreamInfoEntity
import dev.anilbeesetti.nextplayer.core.database.entities.SubtitleStreamInfoEntity
import dev.anilbeesetti.nextplayer.core.database.entities.VideoStreamInfoEntity
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import kotlin.text.substringBeforeLast
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class LocalMediaInfoSynchronizer @Inject constructor(
    private val mediumDao: MediumDao,
    @ApplicationScope private val applicationScope: CoroutineScope,
    @ApplicationContext private val context: Context,
    @Dispatcher(NextDispatchers.Default) private val dispatcher: CoroutineDispatcher,
) : MediaInfoSynchronizer {

    private val activeSyncJobs = mutableMapOf<String, Job>()
    private val mutex = Mutex()

    override fun sync(uri: Uri) {
        applicationScope.launch(dispatcher) {
            val uriString = uri.toString()

            mutex.withLock {
                activeSyncJobs[uriString]
            }?.join()

            val job = applicationScope.launch(dispatcher) {
                try {
                    performSync(uri)
                } finally {
                    mutex.withLock {
                        activeSyncJobs.remove(uriString)
                    }
                }
            }

            mutex.withLock {
                activeSyncJobs[uriString] = job
            }
        }
    }

    override suspend fun clearThumbnailsCache() = withContext(Dispatchers.IO) {
        activeSyncJobs.forEach { it.value.cancel() }
        context.thumbnailCacheDir.deleteFiles()
    }

    private suspend fun performSync(uri: Uri) {
        val medium = mediumDao.getWithInfo(uri.toString()) ?: return
        if (medium.mediumEntity.thumbnailPath?.let { File(it) }?.exists() == true) {
            return
        }

        val mediaMetadataRetriever = runCatching {
            MediaMetadataRetriever().apply { setDataSource(context, uri) }
        }.getOrNull()

        if (mediaMetadataRetriever == null) {
            Log.d(TAG, "performSync: MediaMetadataRetriever is null for $uri")
            return
        }

        val thumbnail = runCatching {
            listOf(".jpg", ".jpeg", ".png").firstOrNull { imageExtension ->
                File(medium.mediumEntity.path.substringBeforeLast(".") + ".$imageExtension").exists()
            }?.let {
                BitmapFactory.decodeFile(medium.mediumEntity.path.substringBeforeLast(".") + ".$it")
            }
        }.getOrNull()
            ?: runCatching { mediaMetadataRetriever.embeddedPicture?.toBitmap() }.getOrNull()
            ?: runCatching {
                val videoDuration = mediaMetadataRetriever
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()
                    ?: return@runCatching null
                mediaMetadataRetriever.getFrameAtTime((videoDuration * 1000) / 3)
            }.getOrNull()
            ?: runCatching { mediaMetadataRetriever.getFrameAtTime(0) }.getOrNull()

        // Extract basic metadata via MediaMetadataRetriever (replaces nextlib-mediainfo)
        val format = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
        val durationMs = mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()
        val width = mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull()
        val height = mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull()
        val bitrate = mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toLongOrNull()
        val rotation = mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toIntOrNull() ?: 0

        val videoStreamInfo = if (width != null && height != null) {
            VideoStreamInfoEntity(
                index = 0,
                title = null,
                codecName = mediaMetadataRetriever
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE) ?: "video/unknown",
                language = null,
                disposition = 0,
                bitRate = bitrate ?: 0L,
                frameRate = 0.0,
                frameWidth = width,
                frameHeight = height,
                mediumUri = medium.mediumEntity.uriString,
            )
        } else null

        val audioStreamsInfo = listOfNotNull(
            runCatching {
                val sampleRate = mediaMetadataRetriever
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_SAMPLERATE)?.toIntOrNull() ?: 0
                val channels = mediaMetadataRetriever
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS)?.toIntOrNull() ?: 0
                val audioBitrate = mediaMetadataRetriever
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toLongOrNull() ?: 0L
                AudioStreamInfoEntity(
                    index = 1,
                    title = null,
                    codecName = mediaMetadataRetriever
                        .extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE) ?: "audio/unknown",
                    language = null,
                    disposition = 0,
                    bitRate = audioBitrate,
                    sampleFormat = null,
                    sampleRate = sampleRate,
                    channels = channels,
                    channelLayout = null,
                    mediumUri = medium.mediumEntity.uriString,
                )
            }.getOrNull(),
        )

        val subtitleStreamsInfo = emptyList<SubtitleStreamInfoEntity>()

        mediaMetadataRetriever.release()

        val thumbnailPath = thumbnail?.saveTo(
            storageDir = context.thumbnailCacheDir,
            quality = 40,
            fileName = medium.mediumEntity.mediaStoreId.toString(),
        )

        mediumDao.upsert(
            medium.mediumEntity.copy(
                format = format,
                thumbnailPath = thumbnailPath,
            ),
        )
        videoStreamInfo?.let { mediumDao.upsertVideoStreamInfo(it) }
        audioStreamsInfo.onEach { mediumDao.upsertAudioStreamInfo(it) }
        subtitleStreamsInfo.onEach { mediumDao.upsertSubtitleStreamInfo(it) }
    }

    companion object {
        private const val TAG = "MediaInfoSynchronizer"
    }
}

suspend fun Bitmap.saveTo(
    storageDir: File,
    quality: Int = 100,
    fileName: String,
): String? = withContext(Dispatchers.IO) {
    val thumbFile = File(storageDir, fileName)
    try {
        FileOutputStream(thumbFile).use { fos ->
            compress(Bitmap.CompressFormat.JPEG, quality, fos)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return@withContext if (thumbFile.exists()) thumbFile.path else null
}

fun ByteArray.toBitmap(): Bitmap? {
    return BitmapFactory.decodeByteArray(this, 0, this.size)
}
