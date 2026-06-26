package dev.anilbeesetti.nextplayer.feature.player

import android.content.Intent
import android.net.Uri
import android.util.Log

/**
 * MX Player-style intent extras for the playback activities.
 *
 * MX Player's ActivityScreen accepts these structured extras on ACTION_VIEW
 * intents, allowing launchers (file pickers, cloud drives, SMB browsers) to
 * pass rich metadata without forcing the player to re-query MediaStore.
 *
 * SHS Player adopts the same extra-name conventions for interoperability:
 *
 *  - `headers`            String[] alternating key/value pairs (["Referer","https://x","Cookie","sid=1"])
 *  - `video_list`         ArrayList<Uri> (the queue to play; intent.data is the start point)
 *  - `video_list.name`    ArrayList<String> parallel to video_list (display name)
 *  - `video_list.size`    ArrayList<Long> parallel to video_list (bytes)
 *  - `video_list.filename`ArrayList<String> parallel to video_list (filename without path)
 *  - `subs`               ArrayList<Uri> (subtitle files to load)
 *  - `subs.name`          ArrayList<String> parallel to subs (display name)
 *  - `subs.filename`      ArrayList<String> parallel to subs (filename)
 *  - `subs.enable`        ArrayList<Boolean> parallel to subs (true = enabled by default)
 *  - `title`              String (display title — overrides URI lastPathSegment)
 *  - `artist`             String (audio only)
 *  - `position`           Long (ms — seek to position on start; respects resume pref)
 *  - `decode_mode`        Int (0 = auto HW, 1 = force HW, 2 = force SW)
 *  - `return_result`      Boolean (true = activity will setResult with position/duration on finish)
 *  - `launcher`           String (the launching component class name — used to detect re-launch)
 *  - `audio_queue`        ArrayList<Uri> (legacy alias for `video_list` on AudioPlayerActivity)
 *  - `audio_queue_index`  Int (start index in audio_queue)
 */
object PlayerIntentExtras {
    private const val TAG = "PlayerIntentExtras"

    const val HEADERS = "headers"
    const val VIDEO_LIST = "video_list"
    const val VIDEO_LIST_NAME = "video_list.name"
    const val VIDEO_LIST_SIZE = "video_list.size"
    const val VIDEO_LIST_FILENAME = "video_list.filename"
    const val SUBS = "subs"
    const val SUBS_NAME = "subs.name"
    const val SUBS_FILENAME = "subs.filename"
    const val SUBS_ENABLE = "subs.enable"
    const val TITLE = "title"
    const val ARTIST = "artist"
    const val POSITION_MS = "position"
    const val DECODE_MODE = "decode_mode"
    const val RETURN_RESULT = "return_result"
    const val LAUNCHER = "launcher"
    const val AUDIO_QUEUE = "audio_queue"
    const val AUDIO_QUEUE_INDEX = "audio_queue_index"

    /**
     * Extract HTTP headers from the `headers` String[] extra.
     *
     * MX Player packs headers as alternating key/value pairs in a single
     * String array: `["User-Agent","MyApp","Referer","https://example.com"]`.
     * Returns an empty map if the extra is missing or malformed (odd length).
     */
    @JvmStatic
    fun extractHeaders(intent: Intent): Map<String, String> {
        val raw = intent.getStringArrayExtra(HEADERS) ?: return emptyMap()
        if (raw.size % 2 != 0) {
            Log.w(TAG, "headers extra has odd length (${raw.size}); ignoring")
            return emptyMap()
        }
        val map = LinkedHashMap<String, String>(raw.size / 2)
        var i = 0
        while (i < raw.size) {
            val k = raw[i].trim()
            val v = raw[i + 1]
            if (k.isNotEmpty()) map[k] = v
            i += 2
        }
        return map
    }

    /**
     * Extract the playlist from `video_list` (MX Player pattern).
     * Falls back to `[intent.data]` if the extra is absent.
     */
    @JvmStatic
    fun extractVideoList(intent: Intent): ArrayList<Uri> {
        @Suppress("DEPRECATION")
        intent.getParcelableArrayListExtra<Uri>(VIDEO_LIST)?.let { list ->
            if (list.isNotEmpty()) return ArrayList(list)
        }
        return intent.data?.let { arrayListOf(it) } ?: arrayListOf()
    }

    /**
     * Extract the audio queue (legacy alias for `video_list` on AudioPlayerActivity).
     * Tries `audio_queue` first, then `video_list`, then falls back to `[intent.data]`.
     */
    @JvmStatic
    fun extractAudioQueue(intent: Intent): ArrayList<Uri> {
        @Suppress("DEPRECATION")
        intent.getParcelableArrayListExtra<Uri>(AUDIO_QUEUE)?.let { list ->
            if (list.isNotEmpty()) return ArrayList(list)
        }
        return extractVideoList(intent)
    }

    /**
     * Extract subtitle URIs and their parallel metadata.
     */
    @JvmStatic
    fun extractSubs(intent: Intent): List<SubtitleExtra> {
        @Suppress("DEPRECATION")
        val uris = intent.getParcelableArrayListExtra<Uri>(SUBS) ?: return emptyList()
        val names = intent.getStringArrayListExtra(SUBS_NAME)
        val filenames = intent.getStringArrayListExtra(SUBS_FILENAME)
        // Build enables list from the SUBS_ENABLE boolean ArrayList extra
        val enablesBundle = intent.getBundleExtra(SUBS_ENABLE)
        val enables = ArrayList<Boolean>()
        if (enablesBundle != null) {
            for (i in uris.indices) enables.add(enablesBundle.getBoolean(i.toString(), true))
        }
        return uris.mapIndexed { i, uri ->
            SubtitleExtra(
                uri = uri,
                name = names?.getOrNull(i) ?: uri.lastPathSegment ?: "subtitle_$i",
                filename = filenames?.getOrNull(i) ?: uri.lastPathSegment ?: "subtitle_$i",
                enabled = enables.getOrNull(i) ?: true,
            )
        }
    }

    /**
     * Extract playback start position in ms. -1 = unset (caller will use resume pref).
     */
    @JvmStatic
    fun extractPositionMs(intent: Intent): Long = intent.getLongExtra(POSITION_MS, -1L)

    /**
     * Extract decoder mode: 0 = auto (HW pref), 1 = force HW, 2 = force SW.
     */
    @JvmStatic
    fun extractDecodeMode(intent: Intent): Int = intent.getIntExtra(DECODE_MODE, 0)

    /**
     * Whether to call setResult() with the playback result (position + duration) on finish.
     */
    @JvmStatic
    fun shouldReturnResult(intent: Intent): Boolean = intent.getBooleanExtra(RETURN_RESULT, false)

    /**
     * Detect YouTube URLs so the player can trigger yt-dlp extraction.
     */
    @JvmStatic
    fun isYouTubeUrl(url: String): Boolean {
        return url.contains("youtube.com/watch?v=") ||
            url.contains("youtu.be/") ||
            url.contains("youtube.com/embed/") ||
            url.contains("m.youtube.com/watch?v=")
    }

    /**
     * Build a result intent for `setResult` when `return_result` is true.
     */
    @JvmStatic
    fun buildResultIntent(
        position: Long,
        duration: Long,
        mediaUri: Uri? = null,
    ): Intent = Intent().apply {
        putExtra("position", position)
        putExtra("duration", duration)
        mediaUri?.let { data = it }
    }
}

data class SubtitleExtra(
    val uri: Uri,
    val name: String,
    val filename: String,
    val enabled: Boolean,
)
