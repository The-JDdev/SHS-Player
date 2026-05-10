package dev.anilbeesetti.nextplayer.feature.player.ui

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.anilbeesetti.nextplayer.core.ui.R as coreUiR
import dev.anilbeesetti.nextplayer.feature.player.state.AudioEqualizerState

val LocalAudioEqualizerState = compositionLocalOf<AudioEqualizerState?> { null }

// ── Profile persistence helpers ───────────────────────────────────────────────
private const val EQ_PREFS = "video_eq_profiles"

data class VideoEqProfile(val name: String, val brightness: Float, val contrast: Float, val saturation: Float)

fun loadEqProfiles(context: Context): List<VideoEqProfile> {
    val prefs = context.getSharedPreferences(EQ_PREFS, Context.MODE_PRIVATE)
    val names = prefs.getStringSet("profile_names", emptySet()) ?: emptySet()
    return names.mapNotNull { name ->
        runCatching {
            val b = prefs.getFloat("${name}_b", 1f)
            val c = prefs.getFloat("${name}_c", 1f)
            val s = prefs.getFloat("${name}_s", 1f)
            VideoEqProfile(name, b, c, s)
        }.getOrNull()
    }.sortedBy { it.name }
}

fun saveEqProfile(context: Context, profile: VideoEqProfile) {
    val prefs = context.getSharedPreferences(EQ_PREFS, Context.MODE_PRIVATE)
    val names = (prefs.getStringSet("profile_names", emptySet()) ?: emptySet()).toMutableSet()
    names.add(profile.name)
    prefs.edit()
        .putStringSet("profile_names", names)
        .putFloat("${profile.name}_b", profile.brightness)
        .putFloat("${profile.name}_c", profile.contrast)
        .putFloat("${profile.name}_s", profile.saturation)
        .apply()
}

fun deleteEqProfile(context: Context, name: String) {
    val prefs = context.getSharedPreferences(EQ_PREFS, Context.MODE_PRIVATE)
    val names = (prefs.getStringSet("profile_names", emptySet()) ?: emptySet()).toMutableSet()
    names.remove(name)
    prefs.edit()
        .putStringSet("profile_names", names)
        .remove("${name}_b").remove("${name}_c").remove("${name}_s")
        .apply()
}

// ── Main composable ───────────────────────────────────────────────────────────
@Composable
fun EqualizerView(
    modifier: Modifier = Modifier,
    brightness: Float,
    contrast: Float,
    saturation: Float,
    onBrightnessChange: (Float) -> Unit,
    onContrastChange: (Float) -> Unit,
    onSaturationChange: (Float) -> Unit,
    onReset: () -> Unit,
    onApplyProfile: (brightness: Float, contrast: Float, saturation: Float) -> Unit = { _, _, _ -> },
) {
    val context = LocalContext.current
    val audioEqualizerState = LocalAudioEqualizerState.current
    var showSaveDialog by remember { mutableStateOf(false) }
    var showProfilesDialog by remember { mutableStateOf(false) }
    var newProfileName by remember { mutableStateOf("") }
    var profiles by remember { mutableStateOf(loadEqProfiles(context)) }

    Column(modifier = modifier.padding(bottom = 24.dp)) {

        // ── Audio Equalizer bands ─────────────────────────────────────────
        if (audioEqualizerState != null && audioEqualizerState.isReady && audioEqualizerState.bandCount > 0) {
            Text(
                text = "Audio Equalizer",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 4.dp),
            )
            audioEqualizerState.bandLevels.forEachIndexed { index, level ->
                val freq = audioEqualizerState.bandFrequencies.getOrElse(index) { "" }
                Column(modifier = Modifier.padding(vertical = 2.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = freq, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = "${if (level >= 0) "+" else ""}${level / 100} dB",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    Slider(
                        value = level.toFloat(),
                        onValueChange = { audioEqualizerState.setBandLevel(index, it.toInt()) },
                        valueRange = audioEqualizerState.minLevel.toFloat()..audioEqualizerState.maxLevel.toFloat(),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            TextButton(
                onClick = { audioEqualizerState.resetBands() },
                modifier = Modifier.align(Alignment.End),
            ) { Text("Reset EQ") }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        }

        // ── Video Adjustments ─────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Video Adjustments",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Row {
                TextButton(onClick = { profiles = loadEqProfiles(context); showProfilesDialog = true }) {
                    Text("Profiles")
                }
                TextButton(onClick = { newProfileName = ""; showSaveDialog = true }) {
                    Text("Save")
                }
            }
        }

        EqualizerSlider(
            label = stringResource(coreUiR.string.brightness_label),
            value = brightness,
            onValueChange = onBrightnessChange,
        )
        EqualizerSlider(
            label = stringResource(coreUiR.string.contrast),
            value = contrast,
            onValueChange = onContrastChange,
        )
        EqualizerSlider(
            label = stringResource(coreUiR.string.saturation),
            value = saturation,
            onValueChange = onSaturationChange,
        )
        TextButton(
            onClick = onReset,
            modifier = Modifier.align(Alignment.End),
        ) { Text(stringResource(coreUiR.string.reset)) }
    }

    // ── Save profile dialog ───────────────────────────────────────────────────
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save EQ Profile") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Current: B=${(brightness * 100).toInt()}% C=${(contrast * 100).toInt()}% S=${(saturation * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(
                        value = newProfileName,
                        onValueChange = { newProfileName = it },
                        label = { Text("Profile name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newProfileName.isNotBlank()) {
                            saveEqProfile(context, VideoEqProfile(newProfileName.trim(), brightness, contrast, saturation))
                            profiles = loadEqProfiles(context)
                            showSaveDialog = false
                        }
                    },
                    enabled = newProfileName.isNotBlank(),
                ) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showSaveDialog = false }) { Text("Cancel") } },
        )
    }

    // ── Load profiles dialog ──────────────────────────────────────────────────
    if (showProfilesDialog) {
        AlertDialog(
            onDismissRequest = { showProfilesDialog = false },
            title = { Text("EQ Profiles") },
            text = {
                if (profiles.isEmpty()) {
                    Text("No saved profiles. Use \"Save\" to create one.",
                        style = MaterialTheme.typography.bodyMedium)
                } else {
                    Column {
                        profiles.forEach { p ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onApplyProfile(p.brightness, p.contrast, p.saturation)
                                        showProfilesDialog = false
                                    }
                                    .padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(p.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                                    Text(
                                        "B=${(p.brightness * 100).toInt()}% C=${(p.contrast * 100).toInt()}% S=${(p.saturation * 100).toInt()}%",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                TextButton(onClick = {
                                    deleteEqProfile(context, p.name)
                                    profiles = loadEqProfiles(context)
                                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
                            }
                            HorizontalDivider()
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showProfilesDialog = false }) { Text("Close") } },
        )
    }
}

@Composable
private fun EqualizerSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
            Text(text = "${(value * 100).toInt()}%", style = MaterialTheme.typography.bodySmall)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..2f,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
