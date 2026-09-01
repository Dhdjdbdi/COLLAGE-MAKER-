package com.example.ui.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.DoneAll
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
import com.example.data.model.FilterType
import com.example.ui.theme.*

@Composable
fun FiltersToolPanel(
    activeFilter: FilterType,
    onSelectFilter: (FilterType) -> Unit,
    onApplyToAll: () -> Unit
) {
    val filters = FilterType.values()

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
            Text(
                text = "COLOR & MOOD FILTERS",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = onApplyToAll,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x22FFFFFF)),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(14.dp), tint = AccentTertiary)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Apply to All", color = AccentTertiary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filters) { filter ->
                val isSelected = activeFilter == filter

                val gradientColors = when (filter) {
                    FilterType.NONE -> listOf(Color(0xFF4B5563), Color(0xFF1F2937))
                    FilterType.VIVID -> listOf(Color(0xFFF43F5E), Color(0xFF8B5CF6))
                    FilterType.NOIR -> listOf(Color(0xFFF8FAFC), Color(0xFF0F172A))
                    FilterType.SUNSET -> listOf(Color(0xFFFB923C), Color(0xFFE11D48))
                    FilterType.CYBERPUNK -> listOf(Color(0xFF06B6D4), Color(0xFFEC4899))
                    FilterType.VINTAGE -> listOf(Color(0xFFD97706), Color(0xFF78350F))
                    FilterType.GOLDEN_HOUR -> listOf(Color(0xFFFBBF24), Color(0xFFB45309))
                    FilterType.PASTEL -> listOf(Color(0xFFF472B6), Color(0xFFC084FC))
                    FilterType.CHROME -> listOf(Color(0xFF94A3B8), Color(0xFF334155))
                    FilterType.DRAMA -> listOf(Color(0xFF64748B), Color(0xFF0F172A))
                    FilterType.MATTE -> listOf(Color(0xFF71717A), Color(0xFF27272A))
                    FilterType.EMERALD -> listOf(Color(0xFF10B981), Color(0xFF064E3B))
                }

                Box(
                    modifier = Modifier
                        .width(90.dp)
                        .height(84.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) AccentPrimary.copy(alpha = 0.35f) else Color(0x18FFFFFF))
                        .border(1.5.dp, if (isSelected) AccentPrimary else Color(0x22FFFFFF), RoundedCornerShape(14.dp))
                        .clickable { onSelectFilter(filter) }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Brush.linearGradient(gradientColors))
                        )
                        Text(
                            text = filter.displayName,
                            color = if (isSelected) Color.White else TextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
