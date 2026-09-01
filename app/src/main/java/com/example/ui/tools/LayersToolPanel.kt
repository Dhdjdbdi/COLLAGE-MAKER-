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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FreeformLayer
import com.example.data.model.LayerType
import com.example.ui.theme.*

@Composable
fun LayersToolPanel(
    layers: List<FreeformLayer>,
    selectedLayerId: String?,
    onSelectLayer: (String) -> Unit,
    onBringForward: (String) -> Unit,
    onSendBackward: (String) -> Unit,
    onToggleLock: (String) -> Unit,
    onDuplicateLayer: (String) -> Unit,
    onDeleteLayer: (String) -> Unit
) {
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
            Text(text = "CANVAS LAYERS (${layers.size})", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)

            if (selectedLayerId != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = { onBringForward(selectedLayerId) },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(imageVector = Icons.Default.FlipToFront, contentDescription = "Bring Forward", tint = TextPrimary, modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = { onSendBackward(selectedLayerId) },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(imageVector = Icons.Default.FlipToBack, contentDescription = "Send Back", tint = TextPrimary, modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = { onDuplicateLayer(selectedLayerId) },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = TextPrimary, modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = { onToggleLock(selectedLayerId) },
                        modifier = Modifier.size(30.dp)
                    ) {
                        val isLocked = layers.find { it.id == selectedLayerId }?.isLocked == true
                        Icon(
                            imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = "Lock",
                            tint = if (isLocked) AccentAmber else TextPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = { onDeleteLayer(selectedLayerId) },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (layers.isEmpty()) {
            Text(
                text = "No overlay text or stickers added yet. Tap 'Text' or 'Stickers' below.",
                color = TextSecondary,
                fontSize = 12.sp
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(layers, key = { it.id }) { layer ->
                    val isSel = selectedLayerId == layer.id

                    val label = when (layer.type) {
                        LayerType.TEXT -> layer.textContent.take(12)
                        LayerType.STICKER -> layer.stickerEmoji ?: "Sticker"
                        LayerType.PHOTO -> "Photo Card"
                        LayerType.SHAPE -> "Shape"
                        LayerType.FRAME -> "Frame"
                        else -> "Layer"
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSel) AccentPrimary.copy(alpha = 0.35f) else Color(0x18FFFFFF))
                            .border(1.dp, if (isSel) AccentPrimary else Color(0x22FFFFFF), RoundedCornerShape(12.dp))
                            .clickable { onSelectLayer(layer.id) }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            if (layer.isLocked) {
                                Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(12.dp))
                            }
                            Text(
                                text = label,
                                color = if (isSel) Color.White else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
