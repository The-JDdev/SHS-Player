package dev.anilbeesetti.nextplayer.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.anilbeesetti.nextplayer.core.ui.R as coreUiR

enum class BottomNavTab(val iconRes: Int, val labelRes: Int) {
    VIDEOS(coreUiR.drawable.ic_video, coreUiR.string.video),
    MUSIC(coreUiR.drawable.ic_music_note, coreUiR.string.music),
    WATCH_TV(coreUiR.drawable.ic_tv, coreUiR.string.watch_tv),
    ME(coreUiR.drawable.ic_person, coreUiR.string.me),
    TELEGRAM(coreUiR.drawable.ic_info, coreUiR.string.about_name),
}

/**
 * Premium bottom navigation bar — Cupertino/iOS-inspired hybrid with Material 3.
 *
 * Visual changes from stock NavigationBar:
 * - Selected items scale up 1.15x with spring animation
 * - Selected label uses SemiBold weight (Cupertino style)
 * - Active indicator uses primary color with rounded corners
 * - Unselected items use onSurfaceVariant (iOS secondary label color)
 */
@Composable
fun BottomNavBar(
    currentTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
    ) {
        BottomNavTab.entries.forEach { tab ->
            val isSelected = currentTab == tab
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.15f else 1f,
                animationSpec = tween(200),
                label = "navScale",
            )
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.scale(scale),
                    ) {
                        Icon(
                            painter = painterResource(tab.iconRes),
                            contentDescription = stringResource(tab.labelRes),
                            modifier = Modifier.size(24.dp),
                        )
                    }
                },
                label = {
                    Text(
                        text = stringResource(tab.labelRes),
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            )
        }
    }
}
