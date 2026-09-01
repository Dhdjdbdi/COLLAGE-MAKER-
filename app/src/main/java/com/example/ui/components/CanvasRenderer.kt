package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.*
import com.example.engine.ImageEngine
import com.example.ui.theme.*

@Composable
fun CanvasRenderer(
    preset: CanvasPreset,
    isFreeform: Boolean,
    gridTemplate: GridTemplate,
    backgroundType: BackgroundType,
    backgroundColor: Long,
    backgroundGradients: List<Long>,
    blurPhotoUri: String?,
    cellSpacing: Float,
    cornerRadius: Float,
    borderWidth: Float,
    borderColor: Long,
    photos: List<PhotoItem>,
    layers: List<FreeformLayer>,
    selectedPhotoIndex: Int?,
    selectedLayerId: String?,
    isLivingMotionActive: Boolean,
    onPhotoSelected: (Int) -> Unit,
    onSwapPhotos: (Int, Int) -> Unit,
    onLayerSelected: (String?) -> Unit,
    onUpdateLayerPosition: (String, Float, Float, Float, Float) -> Unit,
    onDeleteLayer: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    // Drag-to-swap tracking
    var draggingSlotIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        val maxCanvasWidth = maxWidth
        val maxCanvasHeight = maxHeight

        val containerRatio = maxCanvasWidth.value / maxCanvasHeight.value
        val targetRatio = preset.ratioW / preset.ratioH

        val canvasWidthDp: androidx.compose.ui.unit.Dp
        val canvasHeightDp: androidx.compose.ui.unit.Dp

        if (containerRatio > targetRatio) {
            canvasHeightDp = maxCanvasHeight - 16.dp
            canvasWidthDp = canvasHeightDp * targetRatio
        } else {
            canvasWidthDp = maxCanvasWidth - 16.dp
            canvasHeightDp = canvasWidthDp / targetRatio
        }

        // Main Canvas Outer Frame with Glass Shadow
        Box(
            modifier = Modifier
                .size(canvasWidthDp, canvasHeightDp)
                .shadow(20.dp, RoundedCornerShape(12.dp), ambientColor = Color.Black.copy(alpha = 0.6f))
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                    onLayerSelected(null)
                }
        ) {
            // 1. Background Layer
            CanvasBackground(
                backgroundType = backgroundType,
                backgroundColor = backgroundColor,
                backgroundGradients = backgroundGradients,
                blurPhotoUri = blurPhotoUri,
                modifier = Modifier.fillMaxSize()
            )

            // 2. Grid Mode vs Free-form Mode Photos
            if (!isFreeform) {
                // Grid Rendering
                val slots = gridTemplate.slots
                for (i in slots.indices) {
                    val slot = slots[i]
                    val photo = photos.getOrNull(i)
                    val isSelected = selectedPhotoIndex == i
                    val isDraggingThis = draggingSlotIndex == i

                    val slotLeft = canvasWidthDp * slot.left + (cellSpacing / 2f).dp
                    val slotTop = canvasHeightDp * slot.top + (cellSpacing / 2f).dp
                    val slotWidth = (canvasWidthDp * (slot.right - slot.left)) - cellSpacing.dp
                    val slotHeight = (canvasHeightDp * (slot.bottom - slot.top)) - cellSpacing.dp

                    val safeWidth = if (slotWidth > 0.dp) slotWidth else 1.dp
                    val safeHeight = if (slotHeight > 0.dp) slotHeight else 1.dp

                    Box(
                        modifier = Modifier
                            .offset(x = slotLeft, y = slotTop)
                            .size(safeWidth, safeHeight)
                            .clip(RoundedCornerShape(cornerRadius.dp))
                            .border(
                                width = if (isSelected) (borderWidth + 2f).dp else borderWidth.dp,
                                color = if (isSelected) AccentPrimary else Color(borderColor),
                                shape = RoundedCornerShape(cornerRadius.dp)
                            )
                            .clickable {
                                onPhotoSelected(i)
                            }
                            .pointerInput(i) {
                                detectDragGestures(
                                    onDragStart = {
                                        draggingSlotIndex = i
                                        dragOffset = Offset.Zero
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        dragOffset += dragAmount
                                    },
                                    onDragEnd = {
                                        // Detect drop target slot
                                        val dropX = slotLeft.toPx() + dragOffset.x + (safeWidth.toPx() / 2f)
                                        val dropY = slotTop.toPx() + dragOffset.y + (safeHeight.toPx() / 2f)
                                        val canvasW = canvasWidthDp.toPx()
                                        val canvasH = canvasHeightDp.toPx()

                                        if (canvasW > 0 && canvasH > 0) {
                                            val relX = (dropX / canvasW).coerceIn(0f, 1f)
                                            val relY = (dropY / canvasH).coerceIn(0f, 1f)
                                            val targetSlotIdx = slots.indexOfFirst {
                                                relX >= it.left && relX <= it.right && relY >= it.top && relY <= it.bottom
                                            }
                                            if (targetSlotIdx != -1 && targetSlotIdx != i && targetSlotIdx < photos.size) {
                                                onSwapPhotos(i, targetSlotIdx)
                                            }
                                        }
                                        draggingSlotIndex = null
                                        dragOffset = Offset.Zero
                                    },
                                    onDragCancel = {
                                        draggingSlotIndex = null
                                        dragOffset = Offset.Zero
                                    }
                                )
                            }
                    ) {
                        if (photo != null) {
                            val colorFilter = remember(photo.brightness, photo.contrast, photo.saturation, photo.warmth, photo.filter, photo.isAiEnhanced) {
                                ImageEngine.getComposeColorFilter(
                                    photo.brightness, photo.contrast, photo.saturation,
                                    photo.warmth, photo.filter, photo.isAiEnhanced
                                )
                            }

                            // Living Motion subtle parallax breathing
                            val motionScale by animateFloatAsState(
                                targetValue = if (isLivingMotionActive && i % 2 == 0) 1.05f else 1.0f,
                                label = "livingMotion"
                            )

                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(photo.uri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = photo.title,
                                contentScale = ContentScale.Crop,
                                colorFilter = colorFilter,
                                alignment = BiasAlignment(
                                    (photo.cropOffsetX * 2f) - 1f,
                                    (photo.cropOffsetY * 2f) - 1f
                                ),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer {
                                        scaleX = motionScale
                                        scaleY = motionScale
                                        rotationZ = photo.rotation
                                    }
                            )

                            // AI Enhanced tag badge
                            if (photo.isAiEnhanced) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(6.dp)
                                        .background(Color(0xCC6366F1), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "AI ✨",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        } else {
                            // Empty slot placeholder
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0x22FFFFFF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.AddPhotoAlternate,
                                        contentDescription = "Add Photo",
                                        tint = TextSecondary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Slot ${i + 1}",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }

                        // Dragging indicator
                        if (isDraggingThis) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0x886366F1)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Dragging...", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // 3. Overlay Layers (Text, Stickers, Freeform items)
            for (layer in layers) {
                val isSelected = selectedLayerId == layer.id

                val layerX = canvasWidthDp * layer.x
                val layerY = canvasHeightDp * layer.y
                val layerW = canvasWidthDp * layer.width
                val layerH = canvasHeightDp * layer.height

                Box(
                    modifier = Modifier
                        .offset(x = layerX - layerW / 2f, y = layerY - layerH / 2f)
                        .size(layerW.coerceAtLeast(30.dp), layerH.coerceAtLeast(30.dp))
                        .rotate(layer.rotation)
                        .border(
                            width = if (isSelected) 1.5.dp else 0.dp,
                            color = if (isSelected) AccentSecondary else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .pointerInput(layer.id) {
                            if (!layer.isLocked) {
                                detectTransformGestures { _, pan, zoom, rotationChange ->
                                    val newX = (layer.x + pan.x / canvasWidthDp.toPx()).coerceIn(0.1f, 0.9f)
                                    val newY = (layer.y + pan.y / canvasHeightDp.toPx()).coerceIn(0.1f, 0.9f)
                                    val newW = (layer.width * zoom).coerceIn(0.1f, 1.2f)
                                    val newRot = layer.rotation + rotationChange
                                    onUpdateLayerPosition(layer.id, newX, newY, newW, newRot)
                                }
                            }
                        }
                        .clickable {
                            onLayerSelected(layer.id)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    when (layer.type) {
                        LayerType.STICKER -> {
                            Text(
                                text = layer.stickerEmoji ?: "✨",
                                fontSize = (layer.fontSize * 1.5f).sp,
                                textAlign = TextAlign.Center
                            )
                        }
                        LayerType.TEXT -> {
                            val fontFamily = when (layer.textStylePreset) {
                                TextStylePreset.MODERN_SANS -> FontFamily.SansSerif
                                TextStylePreset.ELEGANT_SERIF -> FontFamily.Serif
                                TextStylePreset.BOLD_DISPLAY -> FontFamily.SansSerif
                                TextStylePreset.HAND_SCRIPT -> FontFamily.Cursive
                                TextStylePreset.NEON_GLOW -> FontFamily.SansSerif
                                TextStylePreset.RETRO_TYPE -> FontFamily.Monospace
                            }

                            val fontW = if (layer.textStylePreset == TextStylePreset.BOLD_DISPLAY || layer.textStylePreset == TextStylePreset.NEON_GLOW) {
                                FontWeight.ExtraBold
                            } else {
                                FontWeight.Bold
                            }

                            Box(
                                modifier = Modifier
                                    .then(
                                        if (layer.textBgColor != null) {
                                            Modifier
                                                .background(Color(layer.textBgColor), RoundedCornerShape(8.dp))
                                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                        } else {
                                            Modifier
                                        }
                                    )
                            ) {
                                Text(
                                    text = layer.textContent,
                                    color = Color(layer.textColor),
                                    fontSize = layer.fontSize.sp,
                                    fontFamily = fontFamily,
                                    fontWeight = fontW,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        LayerType.PHOTO -> {
                            val photo = photos.find { it.id == layer.photoId }
                            if (photo != null) {
                                AsyncImage(
                                    model = photo.uri,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(cornerRadius.dp))
                                )
                            }
                        }
                        else -> Unit
                    }

                    // Selection handle & Delete button
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 10.dp, y = (-10).dp)
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color.Red)
                                .clickable { onDeleteLayer(layer.id) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Delete Layer",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // 4. Print Bleed Guideline (if A4 preset selected)
            if (preset == CanvasPreset.PRINT_A4) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val bleedInset = 16.dp.toPx()
                    drawRect(
                        color = Color(0x66FFCC00),
                        topLeft = Offset(bleedInset, bleedInset),
                        size = Size(size.width - bleedInset * 2f, size.height - bleedInset * 2f),
                        style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                    )
                }
            }

            // Living motion particles overlay
            if (isLivingMotionActive) {
                LivingMotionRenderer(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun CanvasBackground(
    backgroundType: BackgroundType,
    backgroundColor: Long,
    backgroundGradients: List<Long>,
    blurPhotoUri: String?,
    modifier: Modifier = Modifier
) {
    when (backgroundType) {
        BackgroundType.SOLID -> {
            Box(modifier = modifier.background(Color(backgroundColor)))
        }
        BackgroundType.GRADIENT -> {
            val colors = if (backgroundGradients.size >= 2) {
                backgroundGradients.map { Color(it) }
            } else {
                listOf(Color(0xFF1E1B2E), Color(0xFF0D0E1A))
            }
            Box(modifier = modifier.background(Brush.linearGradient(colors)))
        }
        BackgroundType.PATTERN_GRID -> {
            Box(modifier = modifier.background(Color(backgroundColor))) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val step = size.width / 16f
                    val linePaint = Color(0x22FFFFFF)
                    var x = 0f
                    while (x <= size.width) {
                        drawLine(linePaint, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
                        x += step
                    }
                    var y = 0f
                    while (y <= size.height) {
                        drawLine(linePaint, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
                        y += step
                    }
                }
            }
        }
        BackgroundType.PATTERN_DOTS -> {
            Box(modifier = modifier.background(Color(backgroundColor))) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val step = size.width / 14f
                    val dotColor = Color(0x33FFFFFF)
                    for (r in 0..(size.height / step).toInt()) {
                        for (c in 0..(size.width / step).toInt()) {
                            drawCircle(dotColor, radius = 2.5f, center = Offset(c * step + step / 2f, r * step + step / 2f))
                        }
                    }
                }
            }
        }
        BackgroundType.PATTERN_STRIPES, BackgroundType.PATTERN_CHEVRON -> {
            Box(modifier = modifier.background(Color(backgroundColor))) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val stripeColor = Color(0x18FFFFFF)
                    var x = -size.height
                    while (x <= size.width) {
                        drawLine(stripeColor, Offset(x, 0f), Offset(x + size.height, size.height), strokeWidth = 4f)
                        x += 30f
                    }
                }
            }
        }
        BackgroundType.BLURRED_PHOTO -> {
            if (blurPhotoUri != null) {
                AsyncImage(
                    model = blurPhotoUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0.6f) }),
                    modifier = modifier
                        .fillMaxSize()
                        .graphicsLayer { alpha = 0.55f }
                )
            } else {
                Box(modifier = modifier.background(Color(backgroundColor)))
            }
        }
    }
}
