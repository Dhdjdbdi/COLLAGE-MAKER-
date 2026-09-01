package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*

enum class ExportResolution(val label: String, val widthPx: Int, val badge: String) {
    FHD_1080("1080p Full HD", 1080, "Standard"),
    QHD_2K("2K High Res", 2160, "Crisp"),
    UHD_4K("4K Ultra Studio", 3840, "Pro Master")
}

enum class ExportFormat(val label: String, val ext: String) {
    PNG("PNG (Lossless)", "png"),
    JPG("JPG (Compressed)", "jpg"),
    PDF("Print PDF (Bleed)", "pdf")
}

@Composable
fun ExportDialog(
    onDismiss: () -> Unit,
    onExportAndSave: (ExportResolution, ExportFormat) -> Unit,
    onDirectShare: (ExportResolution, ExportFormat, String) -> Unit,
    isExporting: Boolean = false,
    savedUriMessage: String? = null
) {
    var selectedRes by remember { mutableStateOf(ExportResolution.QHD_2K) }
    var selectedFormat by remember { mutableStateOf(ExportFormat.PNG) }

    Dialog(onDismissRequest = onDismiss) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            backgroundColor = DarkSurfaceBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Export & Share Artwork",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Resolution Selector
                Text(
                    text = "EXPORT RESOLUTION",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ExportResolution.values().forEach { res ->
                        val isSelected = selectedRes == res
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) AccentPrimary.copy(alpha = 0.25f) else Color(0x18FFFFFF))
                                .border(1.dp, if (isSelected) AccentPrimary else Color(0x22FFFFFF), RoundedCornerShape(12.dp))
                                .clickable { selectedRes = res }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = res.label,
                                    color = if (isSelected) Color.White else TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "${res.widthPx}px canvas rendering",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .background(if (isSelected) AccentPrimary else Color(0x33FFFFFF), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = res.badge,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Format Selector
                Text(
                    text = "FORMAT",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ExportFormat.values().forEach { fmt ->
                        val isSelected = selectedFormat == fmt
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) AccentSecondary.copy(alpha = 0.3f) else Color(0x18FFFFFF))
                                .border(1.dp, if (isSelected) AccentSecondary else Color(0x22FFFFFF), RoundedCornerShape(10.dp))
                                .clickable { selectedFormat = fmt }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = fmt.ext.uppercase(),
                                color = if (isSelected) Color.White else TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Direct Share Platform Icons
                Text(
                    text = "ONE-TAP SOCIAL SHARE",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SocialShareButton(name = "Instagram", iconEmoji = "📸") {
                        onDirectShare(selectedRes, selectedFormat, "Instagram")
                    }
                    SocialShareButton(name = "TikTok", iconEmoji = "🎵") {
                        onDirectShare(selectedRes, selectedFormat, "TikTok")
                    }
                    SocialShareButton(name = "WhatsApp", iconEmoji = "💬") {
                        onDirectShare(selectedRes, selectedFormat, "WhatsApp")
                    }
                    SocialShareButton(name = "Pinterest", iconEmoji = "📌") {
                        onDirectShare(selectedRes, selectedFormat, "Pinterest")
                    }
                    SocialShareButton(name = "More", iconEmoji = "🔗") {
                        onDirectShare(selectedRes, selectedFormat, "Share")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (savedUriMessage != null) {
                    Text(
                        text = "✅ $savedUriMessage",
                        color = AccentEmerald,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                // Primary Save Button
                GlowActionButton(
                    text = if (isExporting) "Generating Studio Canvas..." else "Save to Gallery (No Watermark)",
                    icon = Icons.Default.Download,
                    isLoading = isExporting,
                    onClick = {
                        onExportAndSave(selectedRes, selectedFormat)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SocialShareButton(
    name: String,
    iconEmoji: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0x22FFFFFF))
                .border(1.dp, Color(0x33FFFFFF), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = iconEmoji, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = name,
            fontSize = 10.sp,
            color = TextSecondary
        )
    }
}
