package dev.dejoe.nougall.ui.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class TimeWindow(val label: String, val apiParam: String) {
    Today("Today", "day"),
    ThisWeek("This Week", "week");
    companion object {
        fun fromApiParam(param: String?): TimeWindow {
            return when (param) {
                "week" -> ThisWeek
                else -> Today
            }
        }
    }
}


@Composable
fun ToggleFilterButton(
    modifier: Modifier = Modifier,
    selectedFilter: TimeWindow = TimeWindow.Today,
    onFilterSelected: (TimeWindow) -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TimeWindow.values().forEach { window ->
            val isSelected = window == selectedFilter
            TextButton(
                onClick = { onFilterSelected(window) },
                colors = ButtonDefaults.textButtonColors(
                    containerColor = if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        Color.Transparent,
                    contentColor = if (isSelected)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .height(36.dp)
                    .defaultMinSize(minWidth = 80.dp)
            ) {
                Text(
                    text = window.label,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
            }
        }
    }
}
