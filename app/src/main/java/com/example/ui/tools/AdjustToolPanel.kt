package com.example.ui.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PhotoItem
import com.example.ui.components.ColorPaletteRow
import com.example.ui.components.ValueSliderRow
import com.example.ui.theme.*

@Composable
fun AdjustToolPanel(
    cellSpacing: Float,
    cornerRadius: Float,
    borderWidth: Float,
    borderColor: Long,
    selectedPhoto: PhotoItem?,
    onUpdateSpacing: (Float) -> Unit,
    onUpdateRadius: (Float) -> Unit,
    onUpdateBorderWidth: (Float) -> Unit,
    onUpdateBorderColor: (Long) -> Unit,
    onUpdatePhotoAdjust: (brightness: Float, contrast: Float, saturation: Float, warmth: Float) -> Unit
) {
    val scrollState = rememberScrollState()

    val borderPalette = listOf(
        0xFFFFFFFFL, 0xFF000000L, 0xFFD4AF37L, 0xFFEC4899L,
        0xFF6366F1L, 0xFF06B6D4L, 0xFF10B981L, 0x44FFFFFFL
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "CANVAS & GRID ADJUSTMENTS",
            color = TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))

        ValueSliderRow(
            title = "Cell Spacing",
            value = cellSpacing,
            onValueChange = onUpdateSpacing,
            valueRange = 0f..36f,
            valueDisplay = "${cellSpacing.toInt()} dp",
            icon = Icons.Default.SpaceBar
        )

        ValueSliderRow(
            title = "Corner Rounding",
            value = cornerRadius,
            onValueChange = onUpdateRadius,
            valueRange = 0f..40f,
            valueDisplay = "${cornerRadius.toInt()} dp",
            icon = Icons.Default.RoundedCorner
        )

        ValueSliderRow(
            title = "Border Thickness",
            value = borderWidth,
            onValueChange = onUpdateBorderWidth,
            valueRange = 0f..16f,
            valueDisplay = "${borderWidth.toInt()} dp",
            icon = Icons.Default.BorderOuter
        )

        if (borderWidth > 0f) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "BORDER COLOR",
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            ColorPaletteRow(
                colors = borderPalette,
                selectedColor = borderColor,
                onColorSelected = onUpdateBorderColor
            )
        }

        // Photo-Specific Tuning
        if (selectedPhoto != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x18FFFFFF))
                    .padding(12.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SELECTED PHOTO TUNING",
                            color = AccentSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = selectedPhoto.title,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    ValueSliderRow(
                        title = "Brightness",
                        value = selectedPhoto.brightness,
                        onValueChange = {
                            onUpdatePhotoAdjust(it, selectedPhoto.contrast, selectedPhoto.saturation, selectedPhoto.warmth)
                        },
                        valueRange = -0.5f..0.5f,
                        valueDisplay = String.format("%.2f", selectedPhoto.brightness),
                        icon = Icons.Default.Brightness6
                    )

                    ValueSliderRow(
                        title = "Contrast",
                        value = selectedPhoto.contrast,
                        onValueChange = {
                            onUpdatePhotoAdjust(selectedPhoto.brightness, it, selectedPhoto.saturation, selectedPhoto.warmth)
                        },
                        valueRange = 0.5f..1.8f,
                        valueDisplay = String.format("%.2f", selectedPhoto.contrast),
                        icon = Icons.Default.Contrast
                    )

                    ValueSliderRow(
                        title = "Saturation",
                        value = selectedPhoto.saturation,
                        onValueChange = {
                            onUpdatePhotoAdjust(selectedPhoto.brightness, selectedPhoto.contrast, it, selectedPhoto.warmth)
                        },
                        valueRange = 0f..2.0f,
                        valueDisplay = String.format("%.2f", selectedPhoto.saturation),
                        icon = Icons.Default.ColorLens
                    )

                    ValueSliderRow(
                        title = "Warmth",
                        value = selectedPhoto.warmth,
                        onValueChange = {
                            onUpdatePhotoAdjust(selectedPhoto.brightness, selectedPhoto.contrast, selectedPhoto.saturation, it)
                        },
                        valueRange = -0.6f..0.6f,
                        valueDisplay = String.format("%.2f", selectedPhoto.warmth),
                        icon = Icons.Default.WbSunny
                    )
                }
            }
        }
    }
}
