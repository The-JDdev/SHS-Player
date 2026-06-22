package dev.anilbeesetti.nextplayer.ui.share

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import java.security.SecureRandom

/**
 * SHS Player — Local-Only Hotspot Manager.
 *
 * Ported from TrebleShot (github.com/The-JDdev/android):
 *   app/src/main/java/org/monora/uprotocol/client/android/util/HotspotManager.kt
 *
 * On Android 8.0+ (API 26+), uses [WifiManager.startLocalOnlyHotspot] with a
 * [WifiManager.LocalOnlyHotspotCallback]. The OS generates the SSID and password;
 * we read them back from the reservation and expose them to the caller so they can
 * be encoded into the QR code.
 *
 * On older devices (API < 26), the TrebleShot fork uses hidden reflection to
 * call `WifiManager.setWifiApEnabled(WifiConfiguration, true)`. We do NOT port
 * that path here because (a) Android 8.0+ is the supported minimum for this app
 * (minSdk 23 means a tiny minority of legacy installs), and (b) the reflection
 * path is unstable on Android 9+ and triggers hidden-API warnings.
 *
 * For minSdk < 26 devices, callers should fall back to the WebShare / Wi-Fi LAN
 * path (the existing VaultHttpServer) instead of starting a hotspot.
 */
abstract class HotspotManager internal constructor(context: Context) {
    protected val context: Context = context.applicationContext

    abstract val configuration: WifiConfiguration?
    abstract val enabled: Boolean
    abstract val started: Boolean

    val wifiManager: WifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    abstract fun disable(): Boolean
    abstract fun enable(): Boolean

    companion object {
        internal const val TAG = "HotspotManager"

        fun newInstance(context: Context): HotspotManager? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                OreoHotspotManager(context)
            } else {
                null
            }
        }
    }
}

@RequiresApi(api = Build.VERSION_CODES.O)
class OreoHotspotManager(context: Context) : HotspotManager(context) {

    override val configuration: WifiConfiguration?
        get() = hotspotReservation?.wifiConfiguration

    override val enabled: Boolean
        get() = hotspotReservation != null

    override val started: Boolean
        get() = hotspotReservation != null

    private var hotspotReservation: WifiManager.LocalOnlyHotspotReservation? = null

    var secondaryCallback: WifiManager.LocalOnlyHotspotCallback? = null

    override fun disable(): Boolean {
        val r = hotspotReservation ?: return false
        runCatching { r.close() }
        hotspotReservation = null
        return true
    }

    override fun enable(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w(Companion.TAG, "enable: ACCESS_FINE_LOCATION not granted")
            return false
        }
        return try {
            wifiManager.startLocalOnlyHotspot(callback, Handler(Looper.getMainLooper()))
            true
        } catch (e: Throwable) {
            Log.e(Companion.TAG, "startLocalOnlyHotspot failed", e)
            false
        }
    }

    val callback = object : WifiManager.LocalOnlyHotspotCallback() {
        override fun onStarted(reservation: WifiManager.LocalOnlyHotspotReservation) {
            super.onStarted(reservation)
            hotspotReservation = reservation
            secondaryCallback?.onStarted(reservation)
        }

        override fun onStopped() {
            super.onStopped()
            hotspotReservation = null
            secondaryCallback?.onStopped()
        }

        override fun onFailed(reason: Int) {
            super.onFailed(reason)
            hotspotReservation = null
            secondaryCallback?.onFailed(reason)
        }
    }
}

object PinGenerator {
    private val rng = SecureRandom()
    fun nextPin(): Int = 100_000 + rng.nextInt(900_000)
}
