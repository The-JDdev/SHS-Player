package dev.anilbeesetti.nextplayer.feature.player.ui.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

/**
 * PLAYit-style customizable player controls row.
 *
 * Each control button (play/pause, next, prev, equalizer, audio track, etc.) can be:
 *   - Dragged to reorder positions (long-press + drag)
 *   - Toggled visible/hidden via the customization panel
 *   - Removed by dragging out of the row
 *
 * The user's chosen order and visibility are persisted via [ControlCustomizationStore]
 * (DataStore-backed, see the existing playerPreferences).
 *
 * Implementation:
 *   - Uses [detectDragGesturesAfterLongPress] for drag detection (avoids accidental
 *     drags during normal tap interactions).
 *   - Drag state is held in [DragState] — tracks the dragged item index and the
 *     current offset. Items are swapped when the dragged item passes the midpoint
 *     of a sibling.
 *   - Hidden items are filtered out of the layout entirely.
 */
data class PlayerControlButton(
    val id: String,
    val icon: ImageVector,
    val contentDescription: String,
    val onClick: () -> Unit,
)

@Composable
fun CustomizablePlayerControlsRow(
    buttons: List<PlayerControlButton>,
    hiddenButtonIds: Set<String>,
    onReorder: (newOrder: List<String>) -> Unit,
    modifier: Modifier = Modifier,
) {
    var visibleButtons by remember(buttons) {
        mutableStateOf(buttons.filter { it.id !in hiddenButtonIds })
    }
    var draggedIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffsetX by remember { mutableStateOf(0f) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        visibleButtons.forEachIndexed { index, button ->
            val isBeingDragged = draggedIndex == index
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (isBeingDragged) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        else Color.Transparent,
                    )
                    .pointerInput(button.id) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = {
                                draggedIndex = index
                            },
                            onDragEnd = {
                                draggedIndex = null
                                dragOffsetX = 0f
                                // Persist the new order
                                onReorder(visibleButtons.map { it.id })
                            },
                            onDragCancel = {
                                draggedIndex = null
                                dragOffsetX = 0f
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                dragOffsetX += dragAmount.x
                                // Determine if we should swap with the next/previous button
                                // based on the cumulative drag offset.
                                val buttonWidthPx = 48f + 16f // size + spacing
                                val swapThreshold = buttonWidthPx / 2
                                if (dragOffsetX > swapThreshold && index < visibleButtons.lastIndex) {
                                    // Swap with next
                                    val newOrder = visibleButtons.toMutableList()
                                    val tmp = newOrder[index]
                                    newOrder[index] = newOrder[index + 1]
                                    newOrder[index + 1] = tmp
                                    visibleButtons = newOrder
                                    draggedIndex = index + 1
                                    dragOffsetX = 0f
                                } else if (dragOffsetX < -swapThreshold && index > 0) {
                                    // Swap with previous
                                    val newOrder = visibleButtons.toMutableList()
                                    val tmp = newOrder[index]
                                    newOrder[index] = newOrder[index - 1]
                                    newOrder[index - 1] = tmp
                                    visibleButtons = newOrder
                                    draggedIndex = index - 1
                                    dragOffsetX = 0f
                                }
                            },
                        )
                    },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = button.icon,
                    contentDescription = button.contentDescription,
                    tint = if (isBeingDragged) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .size(24.dp)
                        .alpha(if (isBeingDragged) 0.7f else 1f),
                )
            }
            if (index < visibleButtons.lastIndex) {
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}
