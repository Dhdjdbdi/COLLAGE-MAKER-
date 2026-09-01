package com.example.data.model

import androidx.compose.ui.graphics.Color
import java.util.UUID

enum class CanvasPreset(val displayName: String, val ratioW: Float, val ratioH: Float, val platformBadge: String) {
    SQUARE_1_1("1:1 Square", 1f, 1f, "Instagram Post"),
    STORY_9_16("9:16 Story", 9f, 16f, "TikTok / Reels"),
    PORTRAIT_4_5("4:5 Portrait", 4f, 5f, "IG Portrait"),
    LANDSCAPE_16_9("16:9 Banner", 16f, 9f, "YouTube / Banner"),
    PINTEREST_2_3("2:3 Pin", 2f, 3f, "Pinterest"),
    CLASSIC_4_3("4:3 Classic", 4f, 3f, "Photo Standard"),
    PORTRAIT_3_4("3:4 Feed", 3f, 4f, "Feed Post"),
    CINEMA_21_9("21:9 Cinema", 21f, 9f, "Ultra-Wide"),
    PRINT_A4("A4 Print", 1f, 1.414f, "Print / Book")
}

enum class BackgroundType(val displayName: String) {
    SOLID("Solid"),
    GRADIENT("Gradient"),
    PATTERN_DOTS("Polka Dots"),
    PATTERN_GRID("Grid Mesh"),
    PATTERN_STRIPES("Stripes"),
    PATTERN_CHEVRON("Chevron"),
    BLURRED_PHOTO("Blurred Photo")
}

enum class FilterType(val displayName: String) {
    NONE("Original"),
    VIVID("Vivid Pop"),
    NOIR("Noir B&W"),
    SUNSET("Warm Sunset"),
    CYBERPUNK("Cyber Neon"),
    VINTAGE("Vintage 70s"),
    GOLDEN_HOUR("Golden Glow"),
    PASTEL("Soft Pastel"),
    CHROME("Chrome Film"),
    DRAMA("Dramatic"),
    MATTE("Matte Fade"),
    EMERALD("Lush Emerald")
}

enum class ToneType {
    WARM,
    COOL,
    NEUTRAL,
    VIBRANT,
    MOODY
}

data class PhotoItem(
    val id: String = UUID.randomUUID().toString(),
    val uri: String,
    val title: String = "Photo",
    val cropOffsetX: Float = 0.5f, // 0..1 (focal point X)
    val cropOffsetY: Float = 0.5f, // 0..1 (focal point Y)
    val rotation: Float = 0f,
    val scale: Float = 1f,
    val filter: FilterType = FilterType.NONE,
    val brightness: Float = 0f, // -1f..1f
    val contrast: Float = 1f,   // 0.5f..2f
    val saturation: Float = 1f, // 0f..2f
    val warmth: Float = 0f,     // -1f..1f
    val vignette: Float = 0f,   // 0f..1f
    val blur: Float = 0f,       // 0f..1f
    val isAiEnhanced: Boolean = false,
    val hasBgRemoved: Boolean = false,
    val generativeFillPrompt: String? = null,
    val tone: ToneType = ToneType.NEUTRAL
)

enum class LayerType {
    PHOTO,
    TEXT,
    STICKER,
    SHAPE,
    FRAME
}

enum class TextStylePreset(val label: String) {
    MODERN_SANS("Modern Sans"),
    ELEGANT_SERIF("Elegant Serif"),
    BOLD_DISPLAY("Bold Display"),
    HAND_SCRIPT("Handwritten"),
    NEON_GLOW("Neon Glow"),
    RETRO_TYPE("Retro Type")
}

data class FreeformLayer(
    val id: String = UUID.randomUUID().toString(),
    val type: LayerType = LayerType.PHOTO,
    val photoId: String? = null,
    val textContent: String = "Your Text",
    val textStylePreset: TextStylePreset = TextStylePreset.MODERN_SANS,
    val fontSize: Float = 24f,
    val textColor: Long = 0xFFFFFFFF,
    val textBgColor: Long? = null,
    val hasTextShadow: Boolean = true,
    val stickerEmoji: String? = null,
    val stickerLabel: String? = null,
    val shapeType: String? = null,
    val x: Float = 0.5f, // Relative center 0..1
    val y: Float = 0.5f, // Relative center 0..1
    val width: Float = 0.4f, // Relative size 0..1
    val height: Float = 0.4f, // Relative size 0..1
    val rotation: Float = 0f,
    val zIndex: Int = 0,
    val isLocked: Boolean = false,
    val opacity: Float = 1f
)

data class SlotRect(
    val id: Int,
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
)

data class GridTemplate(
    val id: String,
    val name: String,
    val photoCount: Int,
    val slots: List<SlotRect>,
    val description: String = ""
)

data class AIAutoLayoutSuggestion(
    val id: String,
    val title: String,
    val subtitle: String,
    val gridTemplateId: String,
    val score: Float,
    val harmonicDescription: String,
    val storyTag: String
)

data class ThemeTemplate(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val iconEmoji: String,
    val canvasPreset: CanvasPreset,
    val gridTemplateId: String,
    val backgroundType: BackgroundType,
    val backgroundColor: Long,
    val backgroundGradientColors: List<Long>,
    val cellSpacing: Float,
    val cornerRadius: Float,
    val borderWidth: Float,
    val borderColor: Long,
    val sampleImageUrls: List<String>,
    val defaultLayers: List<FreeformLayer> = emptyList()
)
