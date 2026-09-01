package com.example.ui.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CanvasPreset
import com.example.data.model.GridTemplate
import com.example.data.sample.SampleData
import com.example.ui.components.GlassPillChip
import com.example.ui.theme.*

@Composable
fun LayoutsToolPanel(
    selectedPreset: CanvasPreset,
    isFreeform: Boolean,
    activeGridId: String,
    photoCount: Int,
    onSelectPreset: (CanvasPreset) -> Unit,
    onToggleFreeform: (Boolean) -> Unit,
    onSelectGrid: (GridTemplate) -> Unit
) {
    var selectedPhotoCountFilter by remember { mutableStateOf(photoCount.coerceIn(2, 6)) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        // Mode Switch: Grid vs Freeform Canvas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CANVAS MODE",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                GlassPillChip(
                    text = "Grid Layout",
                    isSelected = !isFreeform,
                    icon = Icons.Default.GridOn,
                    onClick = { onToggleFreeform(false) }
                )
                GlassPillChip(
                    text = "Free-Form Canvas",
                    isSelected = isFreeform,
                    icon = Icons.Default.OpenWith,
                    onClick = { onToggleFreeform(true) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Canvas Aspect Ratio Presets
        Text(
            text = "PLATFORM SIZE PRESETS",
            color = TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(CanvasPreset.values()) { preset ->
                val isSelected = selectedPreset == preset
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) AccentPrimary.copy(alpha = 0.25f) else DarkSurfaceElevated)
                        .border(1.dp, if (isSelected) AccentPrimary else DarkCardBorder, RoundedCornerShape(12.dp))
                        .clickable { onSelectPreset(preset) }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = preset.displayName,
                            color = if (isSelected) AccentPrimary else TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = preset.platformBadge,
                            color = if (isSelected) AccentSecondary else TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        if (!isFreeform) {
            Spacer(modifier = Modifier.height(14.dp))

            // Photo Count Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "GRID TEMPLATES",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                // Quick photo count chips
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(2, 3, 4, 5, 6, 9).forEach { count ->
                        val isSel = selectedPhotoCountFilter == count
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) DarkCardBorder else DarkSurfaceElevated)
                                .border(1.dp, if (isSel) AccentPrimary else DarkCardBorder, RoundedCornerShape(8.dp))
                                .clickable { selectedPhotoCountFilter = count },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$count",
                                color = if (isSel) AccentPrimary else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Grid Templates List for selected photo count
            val templates = remember(selectedPhotoCountFilter) {
                val list = SampleData.gridTemplates.filter { it.photoCount == selectedPhotoCountFilter }
                if (list.isNotEmpty()) list else listOf(SampleData.generateDynamicGrid(selectedPhotoCountFilter))
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(templates, key = { it.id }) { tmpl ->
                    val isSelected = tmpl.id == activeGridId

                    Box(
                        modifier = Modifier
                            .width(130.dp)
                            .height(85.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) AccentPrimary.copy(alpha = 0.2f) else DarkSurfaceElevated)
                            .border(1.5.dp, if (isSelected) AccentPrimary else DarkCardBorder, RoundedCornerShape(12.dp))
                            .clickable { onSelectGrid(tmpl) }
                            .padding(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Mini visual representation
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .background(Color(0x33000000), RoundedCornerShape(6.dp))
                                    .padding(2.dp)
                            ) {
                                for (slot in tmpl.slots) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(
                                                start = (slot.left * 110).dp,
                                                top = (slot.top * 36).dp,
                                                end = ((1f - slot.right) * 110).dp,
                                                bottom = ((1f - slot.bottom) * 36).dp
                                            )
                                            .background(if (isSelected) AccentPrimary else Color(0x66CAC4D0), RoundedCornerShape(2.dp))
                                    )
                                }
                            }

                            Text(
                                text = tmpl.name,
                                color = if (isSelected) AccentPrimary else TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
