package dev.anilbeesetti.nextplayer.ui.share

import android.content.Context
import android.net.wifi.WifiManager
import java.net.InetAddress
import java.net.UnknownHostException
import java.nio.ByteOrder

/**
 * QR code format helpers — ported from TrebleShot's NetworkManagerFragment.kt:264-365.
 *
 * Format A (HOTSPOT mode):  `hs;{pin};{ssid};{bssid};{password};end`
 * Format B (Wi-Fi LAN mode): `wf;{pin};{ssid};{bssid};{ip};end`
 *
 * The delimiter is `;` (matches TrebleShot exactly). The trailing `end` sentinel
 * allows receivers to detect truncated scans.
 *
 * The receiver (see QrScannerScreen) parses these formats and either:
 *   - Hotspot mode → opens system Wi-Fi settings, then re-scans for the host
 *   - Wi-Fi LAN mode → connects directly to the embedded IP
 */
object QrShareFormat {
    const val TYPE_HOTSPOT = "hs"
    const val TYPE_WIFI = "wf"
    const val DELIMITER = ";"
    const val TAIL = "end"

    /** Build the HOTSPOT-mode QR string. */
    fun hotspot(pin: Int, ssid: String, bssid: String?, password: String?): String {
        return buildString {
            append(TYPE_HOTSPOT); append(DELIMITER)
            append(pin); append(DELIMITER)
            append(ssid); append(DELIMITER)
            append(bssid ?: ""); append(DELIMITER)
            append(password ?: ""); append(DELIMITER)
            append(TAIL)
        }
    }

    /** Build the WI-Fi LAN-mode QR string. */
    fun wifi(pin: Int, ssid: String?, bssid: String?, hostIp: String): String {
        return buildString {
            append(TYPE_WIFI); append(DELIMITER)
            append(pin); append(DELIMITER)
            append(ssid ?: ""); append(DELIMITER)
            append(bssid ?: ""); append(DELIMITER)
            append(hostIp); append(DELIMITER)
            append(TAIL)
        }
    }

    /** Parse a scanned QR string. Returns null if the format is unknown. */
    fun parse(content: String): ParsedQr? {
        val parts = content.split(DELIMITER)
        if (parts.size < 6 || parts.last() != TAIL) return null
        return when (parts[0]) {
            TYPE_HOTSPOT -> ParsedQr.Hotspot(
                pin = parts[1].toIntOrNull() ?: return null,
                ssid = parts[2],
                bssid = parts[3].ifBlank { null },
                password = parts[4].ifBlank { null },
            )
            TYPE_WIFI -> ParsedQr.Wifi(
                pin = parts[1].toIntOrNull() ?: return null,
                ssid = parts[2].ifBlank { null },
                bssid = parts[3].ifBlank { null },
                hostIp = parts[4],
            )
            else -> null
        }
    }

    sealed class ParsedQr {
        abstract val pin: Int
        data class Hotspot(
            override val pin: Int,
            val ssid: String,
            val bssid: String?,
            val password: String?,
        ) : ParsedQr()
        data class Wifi(
            override val pin: Int,
            val ssid: String?,
            val bssid: String?,
            val hostIp: String,
        ) : ParsedQr()
    }
}

/**
 * Returns the device's Wi-Fi IP address as a dotted-quad string, or null if not connected.
 *
 * Mirrors TrebleShot's pattern (NetworkManagerFragment.kt:303-318) — reads
 * `WifiManager.connectionInfo.ipAddress` (an int) and byte-swaps if necessary.
 */
fun getWifiIpAddress(context: Context): String? {
    val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val ipInt = wm.connectionInfo.ipAddress
    if (ipInt == 0) return null
    val ip = if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
        Integer.reverseBytes(ipInt)
    } else {
        ipInt
    }
    return try {
        InetAddress.getByAddress(
            byteArrayOf(
                (ip shr 24 and 0xff).toByte(),
                (ip shr 16 and 0xff).toByte(),
                (ip shr 8 and 0xff).toByte(),
                (ip and 0xff).toByte(),
            ),
        ).hostAddress
    } catch (e: UnknownHostException) {
        null
    }
}
