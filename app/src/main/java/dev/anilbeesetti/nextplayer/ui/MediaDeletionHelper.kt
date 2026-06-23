package dev.anilbeesetti.nextplayer.ui

import android.app.Activity
import android.app.RecoverableSecurityException
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.core.content.ContextCompat

/**
 * Safe media deletion helper — fixes crashes when permanently deleting files.
 *
 * PROBLEM: On Android 10+ (API 29+), apps cannot directly delete files via
 * `contentResolver.delete()` if the file was not created by the app. The call
 * throws `RecoverableSecurityException` (Android 10) or returns 0 rows (Android 11+).
 *
 * SOLUTION: Use `MediaStore.createDeleteRequest()` (Android 10+) which shows a
 * system dialog asking the user for permission to delete. On Android 9 and below,
 * fall back to direct `contentResolver.delete()`.
 *
 * For files inside the app's private storage (Privacy Folder vault), use
 * `File.delete()` directly — no MediaStore involvement needed.
 */
object MediaDeletionHelper {

    private const val TAG = "MediaDeletionHelper"

    /**
     * Delete a list of media URIs safely. On Android 10+, shows a system confirmation
     * dialog. On Android 9 and below, deletes directly.
     *
     * @param context Activity context (needed for startActivityForResult on Android 10)
     * @param uris MediaStore URIs to delete
     * @param intentSenderLauncher ActivityResultLauncher to handle the system dialog result
     * @return true if deletion was initiated (or completed on pre-Q), false if it failed
     */
    fun deleteMediaSafely(
        context: Context,
        uris: List<Uri>,
        intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>? = null,
        onComplete: (Int) -> Unit = {},
    ): Boolean {
        if (uris.isEmpty()) {
            onComplete(0)
            return true
        }

        // Android 11+ (API 30+) — use createDeleteRequest
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return try {
                val pendingIntent = MediaStore.createDeleteRequest(context.contentResolver, uris)
                intentSenderLauncher?.let { launcher ->
                    launcher.launch(IntentSenderRequest.Builder(pendingIntent).build())
                    // Completion will be handled by the launcher's callback
                } ?: run {
                    // No launcher available — cannot show dialog
                    Log.w(TAG, "No intentSenderLauncher provided for Android 11+ deletion")
                    onComplete(0)
                    return false
                }
                true
            } catch (e: Exception) {
                Log.e(TAG, "createDeleteRequest failed", e)
                onComplete(0)
                false
            }
        }

        // Android 10 (API 29) — use RecoverableSecurityException
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            var deletedCount = 0
            for (uri in uris) {
                try {
                    context.contentResolver.delete(uri, null, null)
                    deletedCount++
                } catch (e: RecoverableSecurityException) {
                    // Need user consent — show dialog
                    intentSenderLauncher?.let { launcher ->
                        launcher.launch(IntentSenderRequest.Builder(e.userAction.actionIntent.intentSender).build())
                    } ?: run {
                        Log.w(TAG, "RecoverableSecurityException but no launcher available")
                    }
                    // Deletion will complete in the launcher callback
                    return true
                } catch (e: Exception) {
                    Log.w(TAG, "delete failed for $uri", e)
                }
            }
            onComplete(deletedCount)
            return deletedCount > 0
        }

        // Android 9 and below — direct delete
        var deletedCount = 0
        for (uri in uris) {
            try {
                context.contentResolver.delete(uri, null, null)
                deletedCount++
            } catch (e: Exception) {
                Log.w(TAG, "delete failed for $uri", e)
            }
        }
        onComplete(deletedCount)
        return deletedCount > 0
    }

    /**
     * Delete a single private file (inside app's internal storage).
     * This does NOT require MediaStore or user consent.
     *
     * @return true if the file was deleted or didn't exist
     */
    fun deletePrivateFile(filePath: String): Boolean {
        return try {
            val file = java.io.File(filePath)
            if (file.exists()) {
                file.delete()
            } else {
                true // already gone
            }
        } catch (e: Exception) {
            Log.w(TAG, "deletePrivateFile failed: $filePath", e)
            false
        }
    }
}
