package dev.anilbeesetti.nextplayer.feature.player.engine

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import androidx.core.content.ContextCompat
import android.util.Log
import org.videolan.libvlc.MediaPlayer

/**
 * VlcPlaybackService — foreground service for VLC-based background playback.
 *
 * REPLACES the Media3-based PlayerService. Uses:
 *   - VlcPlayerEngine for actual playback
 *   - MediaSessionCompat (not Media3 MediaSession) for notification controls
 *   - Custom notification with SHS Player logo (ic_notification_logo)
 *
 * The service runs as a foreground service while media is playing, and stops
 * itself when playback ends or the user dismisses the notification.
 */
class VlcPlaybackService : Service(), VlcPlayerEngine.EventListener {

    companion object {
        private const val TAG = "VlcPlaybackService"
        private const val NOTIFICATION_ID = 1002
        private const val CHANNEL_ID = "shs_player_vlc_playback"
        private const val ACTION_PLAY = "dev.anilbeesetti.nextplayer.action.PLAY"
        private const val ACTION_PAUSE = "dev.anilbeesetti.nextplayer.action.PAUSE"
        private const val ACTION_NEXT = "dev.anilbeesetti.nextplayer.action.NEXT"
        private const val ACTION_PREV = "dev.anilbeesetti.nextplayer.action.PREV"
        private const val ACTION_STOP = "dev.anilbeesetti.nextplayer.action.STOP"
        private const val ACTION_SET_URI = "dev.anilbeesetti.nextplayer.action.SET_URI"

        const val EXTRA_URI = "uri"
        const val EXTRA_TITLE = "title"
        const val EXTRA_IS_AUDIO = "is_audio"

        @Volatile
        var instance: VlcPlaybackService? = null
            private set

        fun startPlayback(context: Context, uri: String, title: String, isAudio: Boolean) {
            val intent = Intent(context, VlcPlaybackService::class.java).apply {
                action = ACTION_SET_URI
                putExtra(EXTRA_URI, uri)
                putExtra(EXTRA_TITLE, title)
                putExtra(EXTRA_IS_AUDIO, isAudio)
            }
            ContextCompat.startForegroundService(context, intent)
        }

        fun sendAction(context: Context, action: String) {
            val intent = Intent(context, VlcPlaybackService::class.java).apply {
                this.action = action
            }
            ContextCompat.startForegroundService(context, intent)
        }
    }

    private var engine: VlcPlayerEngine? = null
    private var mediaSession: MediaSessionCompat? = null
    private var currentTitle: String = "SHS Player"
    private var currentUri: String? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        createNotificationChannel()
        engine = VlcPlayerEngine(applicationContext).also {
            it.init()
            it.addListener(this)
        }
        setupMediaSession()
        Log.d(TAG, "VlcPlaybackService created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_SET_URI -> {
                val uri = intent.getStringExtra(EXTRA_URI) ?: return START_NOT_STICKY
                val title = intent.getStringExtra(EXTRA_TITLE) ?: "Unknown"
                currentUri = uri
                currentTitle = title
                startForeground(NOTIFICATION_ID, buildNotification())
                engine?.setDataSource(android.net.Uri.parse(uri))
                engine?.play()
                updateMediaSessionMetadata(title, uri)
            }
            ACTION_PLAY -> {
                engine?.play()
                updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)
            }
            ACTION_PAUSE -> {
                engine?.pause()
                updatePlaybackState(PlaybackStateCompat.STATE_PAUSED)
            }
            ACTION_NEXT -> { /* TODO: playlist support */ }
            ACTION_PREV -> { /* TODO: playlist support */ }
            ACTION_STOP -> {
                engine?.stop()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun setupMediaSession() {
        mediaSession = MediaSessionCompat(this, "SHSPlayerVlc").apply {
            setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                    MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS,
            )
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() { engine?.play() }
                override fun onPause() { engine?.pause() }
                override fun onStop() {
                    engine?.stop()
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
                override fun onSeekTo(pos: Long) { engine?.seekTo(pos) }
            })
            isActive = true
        }
    }

    private fun updateMediaSessionMetadata(title: String, uri: String) {
        val metadata = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, uri)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, engine?.duration ?: 0)
            .build()
        mediaSession?.setMetadata(metadata)
    }

    private fun updatePlaybackState(state: Int) {
        val pos = engine?.position ?: 0
        val playbackState = PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                    PlaybackStateCompat.ACTION_PAUSE or
                    PlaybackStateCompat.ACTION_PLAY_PAUSE or
                    PlaybackStateCompat.ACTION_STOP or
                    PlaybackStateCompat.ACTION_SEEK_TO,
            )
            .setState(state, pos, 1.0f)
            .build()
        mediaSession?.setPlaybackState(playbackState)
        // Update notification
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIFICATION_ID, buildNotification())
    }

    private fun buildNotification(): Notification {
        val playPauseAction = if (engine?.isPlaying == true) {
            NotificationCompat.Action(
                android.R.drawable.ic_media_pause,
                "Pause",
                PendingIntent.getService(
                    this, 1,
                    Intent(this, VlcPlaybackService::class.java).apply { action = ACTION_PAUSE },
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
                ),
            )
        } else {
            NotificationCompat.Action(
                android.R.drawable.ic_media_play,
                "Play",
                PendingIntent.getService(
                    this, 0,
                    Intent(this, VlcPlaybackService::class.java).apply { action = ACTION_PLAY },
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
                ),
            )
        }

        val stopAction = NotificationCompat.Action(
            android.R.drawable.ic_menu_close_clear_cancel,
            "Stop",
            PendingIntent.getService(
                this, 3,
                Intent(this, VlcPlaybackService::class.java).apply { action = ACTION_STOP },
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            ),
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(dev.anilbeesetti.nextplayer.core.ui.R.drawable.ic_notification_logo)
            .setContentTitle(currentTitle)
            .setContentText("SHS Player")
            .setOngoing(engine?.isPlaying == true)
            .setShowWhen(false)
            .addAction(playPauseAction)
            .addAction(stopAction)
            .setStyle(
                MediaStyle()
                    .setMediaSession(mediaSession?.sessionToken)
                    .setShowActionsInCompactView(0, 1),
            )
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "SHS Player Playback",
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = "Background video/audio playback controls"
            }
            val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    // ── VlcPlayerEngine.EventListener callbacks ──────────────────────────
    override fun onPlaying() {
        updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)
    }

    override fun onPaused() {
        updatePlaybackState(PlaybackStateCompat.STATE_PAUSED)
    }

    override fun onStopped() {
        updatePlaybackState(PlaybackStateCompat.STATE_STOPPED)
    }

    override fun onEndReached() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onError(message: String?) {
        Log.e(TAG, "Playback error: $message")
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onTimeChanged(timeMs: Long) {
        // Update notification position occasionally (every 5s) to avoid spam
        if (timeMs % 5000 < 1000) {
            updatePlaybackState(
                if (engine?.isPlaying == true) PlaybackStateCompat.STATE_PLAYING
                else PlaybackStateCompat.STATE_PAUSED,
            )
        }
    }

    override fun onLengthChanged(lengthMs: Long) {
        updateMediaSessionMetadata(currentTitle, currentUri ?: "")
    }

    override fun onDestroy() {
        super.onDestroy()
        engine?.removeListener(this)
        engine?.release()
        engine = null
        mediaSession?.release()
        mediaSession = null
        instance = null
        Log.d(TAG, "VlcPlaybackService destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
