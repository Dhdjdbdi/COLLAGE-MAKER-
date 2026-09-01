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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlowActionButton
import com.example.ui.theme.*

@Composable
fun AiMagicToolPanel(
    onTriggerAutoLayout: () -> Unit,
    onSmartAutoCrop: () -> Unit,
    onColorHarmonyArrange: () -> Unit,
    onEnhanceSelectedPhoto: () -> Unit,
    onRemoveBackground: () -> Unit,
    onGenerativeFillPrompt: (String) -> Unit,
    isPhotoSelected: Boolean,
    isGenerating: Boolean,
    aiStatusMessage: String?
) {
    var promptInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        // Status message if any
        if (aiStatusMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x336366F1))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = aiStatusMessage,
                    color = AccentTertiary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Primary AI Action: Smart Auto-Layout & Color Harmony
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GlowActionButton(
                text = if (isGenerating) "Analyzing Photos..." else "AI Auto-Layout",
                icon = Icons.Default.AutoAwesome,
                isLoading = isGenerating,
                onClick = onTriggerAutoLayout,
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = onColorHarmonyArrange,
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "🎨", fontSize = 14.sp)
                    Text(text = "Color Harmony", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // AI Tools Carousel Cards
        Text(
            text = "AI INTELLIGENT TOOLS",
            color = TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                AiToolCard(
                    title = "Smart Auto-Crop",
                    description = "Centers subject & rule-of-thirds",
                    emoji = "🎯",
                    badge = "Subject Focus",
                    onClick = onSmartAutoCrop
                )
            }
            item {
                AiToolCard(
                    title = "AI Auto-Enhance",
                    description = if (isPhotoSelected) "Boost contrast & exposure" else "Select a photo first",
                    emoji = "✨",
                    badge = "HDR Pop",
                    onClick = onEnhanceSelectedPhoto,
                    enabled = isPhotoSelected
                )
            }
            item {
                AiToolCard(
                    title = "Portrait Cutout",
                    description = if (isPhotoSelected) "Subject cutout & clean solid" else "Select a photo first",
                    emoji = "✂️",
                    badge = "BG Removal",
                    onClick = onRemoveBackground,
                    enabled = isPhotoSelected
                )
            }
            item {
                AiToolCard(
                    title = "Generative Extender",
                    description = "Extend photo edges organically",
                    emoji = "🌌",
                    badge = "AI Outpaint",
                    onClick = { onGenerativeFillPrompt("Aesthetic dreamy gradient cloud fill") }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Text-to-Image AI Prompt Input for Stickers / Backgrounds
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = promptInput,
                onValueChange = { promptInput = it },
                placeholder = { Text("Prompt AI for background or sticker...", color = TextMuted, fontSize = 12.sp) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentPrimary,
                    unfocusedBorderColor = Color(0x22FFFFFF),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = Color(0x18FFFFFF),
                    unfocusedContainerColor = Color(0x10FFFFFF)
                ),
                singleLine = true
            )

            Button(
                onClick = {
                    if (promptInput.isNotBlank()) {
                        onGenerativeFillPrompt(promptInput)
                        promptInput = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp)
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Generate", modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun AiToolCard(
    title: String,
    description: String,
    emoji: String,
    badge: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Box(
        modifier = Modifier
            .width(160.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (enabled) Color(0x18FFFFFF) else Color(0x0AFFFFFF))
            .border(1.dp, if (enabled) Color(0x22FFFFFF) else Color(0x11FFFFFF), RoundedCornerShape(14.dp))
            .clickable(enabled = enabled) { onClick() }
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = emoji, fontSize = 20.sp)
                Box(
                    modifier = Modifier
                        .background(AccentPrimary.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(text = badge, color = AccentTertiary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                color = if (enabled) Color.White else TextMuted,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                maxLines = 2
            )
        }
    }
}
