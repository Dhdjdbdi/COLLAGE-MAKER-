package com.example.ui.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BackgroundType
import com.example.ui.components.ColorPaletteRow
import com.example.ui.components.GlassPillChip
import com.example.ui.theme.*

@Composable
fun BackdropToolPanel(
    selectedType: BackgroundType,
    backgroundColor: Long,
    backgroundGradients: List<Long>,
    onSelectType: (BackgroundType) -> Unit,
    onSelectColor: (Long) -> Unit,
    onSelectGradient: (List<Long>) -> Unit,
    onRequestAiBackdrop: () -> Unit
) {
    val solidColors = listOf(
        0xFF0D0F18L, 0xFF18181BL, 0xFFFFFFFFL, 0xFF1C1917L,
        0xFF1E1B4BL, 0xFF312E81L, 0xFF701A75L, 0xFF064E3BL
    )

    val gradientPresets = listOf(
        listOf(0xFF1E1B2EL, 0xFF0D0E1AL),
        listOf(0xFF2C241EL, 0xFF141210L),
        listOf(0xFF3B1042L, 0xFF100720L),
        listOf(0xFF0F172AL, 0xFF1E293BL),
        listOf(0xFF1E3A8AL, 0xFF064E3BL),
        listOf(0xFF831843L, 0xFF1E1B4BL)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "BACKGROUND STYLE", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)

            Button(
                onClick = onRequestAiBackdrop,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x336366F1)),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = AccentSecondary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "AI Palette", color = AccentSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Background Type Selector
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(BackgroundType.values()) { type ->
                val isSel = selectedType == type
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSel) AccentPrimary.copy(alpha = 0.35f) else Color(0x18FFFFFF))
                        .border(1.dp, if (isSel) AccentPrimary else Color(0x22FFFFFF), RoundedCornerShape(10.dp))
                        .clickable { onSelectType(type) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = type.displayName,
                        color = if (isSel) Color.White else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedType == BackgroundType.SOLID || selectedType == BackgroundType.PATTERN_GRID || selectedType == BackgroundType.PATTERN_DOTS || selectedType == BackgroundType.PATTERN_STRIPES) {
            Text(text = "BASE COLOR", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            ColorPaletteRow(
                colors = solidColors,
                selectedColor = backgroundColor,
                onColorSelected = onSelectColor
            )
        } else if (selectedType == BackgroundType.GRADIENT) {
            Text(text = "GRADIENT PRESETS", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(gradientPresets) { grad ->
                    val isSel = backgroundGradients == grad
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Brush.linearGradient(grad.map { Color(it) }))
                            .border(if (isSel) 2.dp else 1.dp, if (isSel) Color.White else Color(0x33FFFFFF), RoundedCornerShape(10.dp))
                            .clickable { onSelectGradient(grad) }
                    )
                }
            }
        }
    }
}
