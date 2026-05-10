package dev.anilbeesetti.nextplayer.core.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// ── Fast Scroll for LazyColumn ────────────────────────────────────────────────

@Composable
fun FastScrollLazyColumn(
    lazyListState: LazyListState,
    modifier: Modifier = Modifier,
    thumbColor: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
    popupContent: ((index: Int) -> String)? = null,
    content: @Composable () -> Unit,
) {
    var isDragging by remember { mutableStateOf(false) }
    var showPopup by remember { mutableStateOf(false) }
    var popupText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    val totalItems = lazyListState.layoutInfo.totalItemsCount
    val firstVisibleItem = lazyListState.firstVisibleItemIndex
    val scrollProgress = if (totalItems > 1) {
        firstVisibleItem.toFloat() / (totalItems - 1).toFloat()
    } else 0f

    // Auto-hide popup after drag ends
    LaunchedEffect(isDragging) {
        if (!isDragging) {
            kotlinx.coroutines.delay(500)
            showPopup = false
        }
    }

    // Update popup text based on current position
    LaunchedEffect(firstVisibleItem, isDragging) {
        if (isDragging && popupContent != null) {
            popupText = popupContent(firstVisibleItem)
            showPopup = true
        }
    }

    Box(modifier = modifier) {
        // The actual list content
        content()

        // Scrollbar track and thumb
        if (totalItems > 0) {
            val density = LocalDensity.current
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd)
                    .width(40.dp)
                    .padding(end = 4.dp),
            ) {
                val trackHeightPx = with(density) { maxHeight.toPx() }
                val thumbHeightPx = with(density) { 20.dp.toPx() }
                val thumbTopPx = scrollProgress * (trackHeightPx - thumbHeightPx)

                // Track
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .width(4.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(2.dp))
                        .background(trackColor),
                )

                // Thumb
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(y = with(density) { thumbTopPx.toDp() })
                        .width(20.dp)
                        .height(20.dp)
                        .clip(CircleShape)
                        .background(if (isDragging) thumbColor else thumbColor.copy(alpha = 0.6f))
                        .pointerInput(totalItems) {
                            detectDragGestures(
                                onDragStart = {
                                    isDragging = true
                                    showPopup = true
                                },
                                onDragEnd = {
                                    isDragging = false
                                },
                                onDragCancel = {
                                    isDragging = false
                                },
                            ) { _, dragAmount ->
                                val newOffset = (thumbTopPx + dragAmount.y).coerceIn(
                                    0f,
                                    trackHeightPx - thumbHeightPx,
                                )
                                val newProgress = newOffset / (trackHeightPx - thumbHeightPx)
                                val newIndex = (newProgress * (totalItems - 1)).toInt()
                                    .coerceIn(0, totalItems - 1)
                                coroutineScope.launch {
                                    lazyListState.scrollToItem(newIndex)
                                }
                            }
                        },
                )
            }

            // Popup
            AnimatedVisibility(
                visible = showPopup && popupText.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center),
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = thumbColor,
                    tonalElevation = 4.dp,
                ) {
                    Text(
                        text = popupText,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    )
                }
            }
        }
    }
}

// ── Fast Scroll for LazyVerticalGrid ──────────────────────────────────────────

@Composable
fun FastScrollLazyGrid(
    lazyGridState: LazyGridState,
    modifier: Modifier = Modifier,
    thumbColor: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
    popupContent: ((index: Int) -> String)? = null,
    content: @Composable () -> Unit,
) {
    var isDragging by remember { mutableStateOf(false) }
    var showPopup by remember { mutableStateOf(false) }
    var popupText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    val totalItems = lazyGridState.layoutInfo.totalItemsCount
    val firstVisibleItem = lazyGridState.firstVisibleItemIndex
    val scrollProgress = if (totalItems > 1) {
        firstVisibleItem.toFloat() / (totalItems - 1).toFloat()
    } else 0f

    // Auto-hide popup after drag ends
    LaunchedEffect(isDragging) {
        if (!isDragging) {
            kotlinx.coroutines.delay(500)
            showPopup = false
        }
    }

    // Update popup text based on current position
    LaunchedEffect(firstVisibleItem, isDragging) {
        if (isDragging && popupContent != null) {
            popupText = popupContent(firstVisibleItem)
            showPopup = true
        }
    }

    Box(modifier = modifier) {
        // The actual grid content
        content()

        // Scrollbar track and thumb
        if (totalItems > 0) {
            val density = LocalDensity.current
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd)
                    .width(40.dp)
                    .padding(end = 4.dp),
            ) {
                val trackHeightPx = with(density) { maxHeight.toPx() }
                val thumbHeightPx = with(density) { 20.dp.toPx() }
                val thumbTopPx = scrollProgress * (trackHeightPx - thumbHeightPx)

                // Track
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .width(4.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(2.dp))
                        .background(trackColor),
                )

                // Thumb
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(y = with(density) { thumbTopPx.toDp() })
                        .width(20.dp)
                        .height(20.dp)
                        .clip(CircleShape)
                        .background(if (isDragging) thumbColor else thumbColor.copy(alpha = 0.6f))
                        .pointerInput(totalItems) {
                            detectDragGestures(
                                onDragStart = {
                                    isDragging = true
                                    showPopup = true
                                },
                                onDragEnd = {
                                    isDragging = false
                                },
                                onDragCancel = {
                                    isDragging = false
                                },
                            ) { _, dragAmount ->
                                val newOffset = (thumbTopPx + dragAmount.y).coerceIn(
                                    0f,
                                    trackHeightPx - thumbHeightPx,
                                )
                                val newProgress = newOffset / (trackHeightPx - thumbHeightPx)
                                val newIndex = (newProgress * (totalItems - 1)).toInt()
                                    .coerceIn(0, totalItems - 1)
                                coroutineScope.launch {
                                    lazyGridState.scrollToItem(newIndex)
                                }
                            }
                        },
                )
            }

            // Popup
            AnimatedVisibility(
                visible = showPopup && popupText.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center),
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = thumbColor,
                    tonalElevation = 4.dp,
                ) {
                    Text(
                        text = popupText,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    )
                }
            }
        }
    }
}
