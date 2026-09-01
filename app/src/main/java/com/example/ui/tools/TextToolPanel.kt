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
import com.example.data.model.FreeformLayer
import com.example.data.model.LayerType
import com.example.data.model.TextStylePreset
import com.example.ui.components.ColorPaletteRow
import com.example.ui.components.GlassPillChip
import com.example.ui.components.ValueSliderRow
import com.example.ui.theme.*

@Composable
fun TextToolPanel(
    selectedLayer: FreeformLayer?,
    aiQuotes: List<String>,
    onAddTextLayer: (String, TextStylePreset, Long, Long?, Float) -> Unit,
    onUpdateSelectedTextLayer: (String, TextStylePreset, Long, Long?, Float) -> Unit,
    onRequestAiQuotes: () -> Unit
) {
    var textInput by remember(selectedLayer) {
        mutableStateOf(selectedLayer?.takeIf { it.type == LayerType.TEXT }?.textContent ?: "AESTHETIC VIBES")
    }
    var selectedPreset by remember(selectedLayer) {
        mutableStateOf(selectedLayer?.textStylePreset ?: TextStylePreset.MODERN_SANS)
    }
    var selectedColor by remember(selectedLayer) {
        mutableStateOf(selectedLayer?.textColor ?: 0xFFFFFFFFL)
    }
    var selectedBgColor by remember(selectedLayer) {
        mutableStateOf(selectedLayer?.textBgColor)
    }
    var fontSize by remember(selectedLayer) {
        mutableStateOf(selectedLayer?.fontSize ?: 24f)
    }

    val textColorPalette = listOf(
        0xFFFFFFFFL, 0xFF000000L, 0xFFFCD34DL, 0xFFEC4899L,
        0xFF6366F1L, 0xFF06B6D4L, 0xFF10B981L, 0xFFF97316L
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = textInput,
                onValueChange = {
                    textInput = it
                    if (selectedLayer != null && selectedLayer.type == LayerType.TEXT) {
                        onUpdateSelectedTextLayer(it, selectedPreset, selectedColor, selectedBgColor, fontSize)
                    }
                },
                placeholder = { Text("Enter caption or quote...", color = TextMuted) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentPrimary,
                    unfocusedBorderColor = Color(0x22FFFFFF),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )

            Button(
                onClick = {
                    if (selectedLayer != null && selectedLayer.type == LayerType.TEXT) {
                        onUpdateSelectedTextLayer(textInput, selectedPreset, selectedColor, selectedBgColor, fontSize)
                    } else {
                        onAddTextLayer(textInput, selectedPreset, selectedColor, selectedBgColor, fontSize)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Icon(imageVector = if (selectedLayer != null) Icons.Default.Check else Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = if (selectedLayer != null) "Update" else "Add", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // AI Generated Quotes Pill Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "AI CAPTION SUGGESTIONS", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(
                text = "✨ Refresh AI",
                color = AccentTertiary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onRequestAiQuotes() }
            )
        }
        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(aiQuotes) { quote ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x18FFFFFF))
                        .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(10.dp))
                        .clickable {
                            textInput = quote
                            if (selectedLayer != null && selectedLayer.type == LayerType.TEXT) {
                                onUpdateSelectedTextLayer(quote, selectedPreset, selectedColor, selectedBgColor, fontSize)
                            }
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(text = quote, color = TextPrimary, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Typography Style presets
        Text(text = "TYPOGRAPHY STYLE", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(TextStylePreset.values()) { preset ->
                val isSelected = selectedPreset == preset
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) AccentPrimary.copy(alpha = 0.35f) else Color(0x18FFFFFF))
                        .border(1.dp, if (isSelected) AccentPrimary else Color(0x22FFFFFF), RoundedCornerShape(10.dp))
                        .clickable {
                            selectedPreset = preset
                            if (selectedLayer != null && selectedLayer.type == LayerType.TEXT) {
                                onUpdateSelectedTextLayer(textInput, preset, selectedColor, selectedBgColor, fontSize)
                            }
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = preset.label,
                        color = if (isSelected) Color.White else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        ValueSliderRow(
            title = "Font Size",
            value = fontSize,
            onValueChange = {
                fontSize = it
                if (selectedLayer != null && selectedLayer.type == LayerType.TEXT) {
                    onUpdateSelectedTextLayer(textInput, selectedPreset, selectedColor, selectedBgColor, it)
                }
            },
            valueRange = 12f..56f,
            valueDisplay = "${fontSize.toInt()} sp"
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Color Palette & Background Box Pill
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "TEXT COLOR", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                GlassPillChip(
                    text = "Background Box",
                    isSelected = selectedBgColor != null,
                    onClick = {
                        val newBg = if (selectedBgColor == null) 0xCC000000L else null
                        selectedBgColor = newBg
                        if (selectedLayer != null && selectedLayer.type == LayerType.TEXT) {
                            onUpdateSelectedTextLayer(textInput, selectedPreset, selectedColor, newBg, fontSize)
                        }
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))

        ColorPaletteRow(
            colors = textColorPalette,
            selectedColor = selectedColor,
            onColorSelected = {
                selectedColor = it
                if (selectedLayer != null && selectedLayer.type == LayerType.TEXT) {
                    onUpdateSelectedTextLayer(textInput, selectedPreset, it, selectedBgColor, fontSize)
                }
            }
        )
    }
}
