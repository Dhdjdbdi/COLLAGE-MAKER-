package com.example.data.sample

import com.example.data.model.*

object SampleData {

    val stockPhotos = listOf(
        PhotoItem(
            id = "stock_1",
            uri = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800&auto=format&fit=crop&q=80",
            title = "Tropical Sunset Beach",
            tone = ToneType.WARM
        ),
        PhotoItem(
            id = "stock_2",
            uri = "https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?w=800&auto=format&fit=crop&q=80",
            title = "Wanderlust Roadtrip",
            tone = ToneType.WARM
        ),
        PhotoItem(
            id = "stock_3",
            uri = "https://images.unsplash.com/photo-1501785888041-af3ef285b470?w=800&auto=format&fit=crop&q=80",
            title = "Misty Mountain Lake",
            tone = ToneType.COOL
        ),
        PhotoItem(
            id = "stock_4",
            uri = "https://images.unsplash.com/photo-1513151233558-d860c5398176?w=800&auto=format&fit=crop&q=80",
            title = "Celebration Confetti",
            tone = ToneType.VIBRANT
        ),
        PhotoItem(
            id = "stock_5",
            uri = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=800&auto=format&fit=crop&q=80",
            title = "Golden Hour Portrait",
            tone = ToneType.WARM
        ),
        PhotoItem(
            id = "stock_6",
            uri = "https://images.unsplash.com/photo-1514933651103-005eec06c04b?w=800&auto=format&fit=crop&q=80",
            title = "Cozy Espresso Cafe",
            tone = ToneType.MOODY
        ),
        PhotoItem(
            id = "stock_7",
            uri = "https://images.unsplash.com/photo-1519741497674-611481863552?w=800&auto=format&fit=crop&q=80",
            title = "Romantic Floral Wedding",
            tone = ToneType.WARM
        ),
        PhotoItem(
            id = "stock_8",
            uri = "https://images.unsplash.com/photo-1508739773434-c26b3d09e071?w=800&auto=format&fit=crop&q=80",
            title = "Aesthetic Greenery Forest",
            tone = ToneType.COOL
        ),
        PhotoItem(
            id = "stock_9",
            uri = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=800&auto=format&fit=crop&q=80",
            title = "Retro Cyber Arcade",
            tone = ToneType.VIBRANT
        )
    )

    val gridTemplates = listOf(
        // 2-Photo Grids
        GridTemplate(
            id = "grid_2_split_v",
            name = "2 Vertical Split",
            photoCount = 2,
            slots = listOf(
                SlotRect(0, 0f, 0f, 0.5f, 1f),
                SlotRect(1, 0.5f, 0f, 1f, 1f)
            ),
            description = "Equal vertical columns side-by-side"
        ),
        GridTemplate(
            id = "grid_2_split_h",
            name = "2 Horizontal Split",
            photoCount = 2,
            slots = listOf(
                SlotRect(0, 0f, 0f, 1f, 0.5f),
                SlotRect(1, 0f, 0.5f, 1f, 1f)
            ),
            description = "Top and bottom split"
        ),
        GridTemplate(
            id = "grid_2_hero_ratio",
            name = "2 Asymmetric Split",
            photoCount = 2,
            slots = listOf(
                SlotRect(0, 0f, 0f, 0.65f, 1f),
                SlotRect(1, 0.65f, 0f, 1f, 1f)
            ),
            description = "Hero left column with side panel"
        ),

        // 3-Photo Grids
        GridTemplate(
            id = "grid_3_hero_left",
            name = "3 Hero Left",
            photoCount = 3,
            slots = listOf(
                SlotRect(0, 0f, 0f, 0.5f, 1f),
                SlotRect(1, 0.5f, 0f, 1f, 0.5f),
                SlotRect(2, 0.5f, 0.5f, 1f, 1f)
            ),
            description = "Large feature on left with 2 stacked right"
        ),
        GridTemplate(
            id = "grid_3_hero_top",
            name = "3 Banner Top",
            photoCount = 3,
            slots = listOf(
                SlotRect(0, 0f, 0f, 1f, 0.55f),
                SlotRect(1, 0f, 0.55f, 0.5f, 1f),
                SlotRect(2, 0.5f, 0.55f, 1f, 1f)
            ),
            description = "Panoramic top banner with 2 bottom slots"
        ),
        GridTemplate(
            id = "grid_3_cols",
            name = "3 Equal Strips",
            photoCount = 3,
            slots = listOf(
                SlotRect(0, 0f, 0f, 0.333f, 1f),
                SlotRect(1, 0.333f, 0f, 0.666f, 1f),
                SlotRect(2, 0.666f, 0f, 1f, 1f)
            ),
            description = "Triptych 3 vertical strips"
        ),

        // 4-Photo Grids
        GridTemplate(
            id = "grid_4_quad",
            name = "4 Classic Quad (2x2)",
            photoCount = 4,
            slots = listOf(
                SlotRect(0, 0f, 0f, 0.5f, 0.5f),
                SlotRect(1, 0.5f, 0f, 1f, 0.5f),
                SlotRect(2, 0f, 0.5f, 0.5f, 1f),
                SlotRect(3, 0.5f, 0.5f, 1f, 1f)
            ),
            description = "Balanced 2x2 grid layout"
        ),
        GridTemplate(
            id = "grid_4_hero_left_stack",
            name = "4 Hero & 3 Side",
            photoCount = 4,
            slots = listOf(
                SlotRect(0, 0f, 0f, 0.6f, 1f),
                SlotRect(1, 0.6f, 0f, 1f, 0.333f),
                SlotRect(2, 0.6f, 0.333f, 1f, 0.666f),
                SlotRect(3, 0.6f, 0.666f, 1f, 1f)
            ),
            description = "Prominent main photo with 3 thumbnails"
        ),
        GridTemplate(
            id = "grid_4_masonry",
            name = "4 Editorial Masonry",
            photoCount = 4,
            slots = listOf(
                SlotRect(0, 0f, 0f, 0.5f, 0.6f),
                SlotRect(1, 0.5f, 0f, 1f, 0.4f),
                SlotRect(2, 0f, 0.6f, 0.5f, 1f),
                SlotRect(3, 0.5f, 0.4f, 1f, 1f)
            ),
            description = "Staggered modern editorial magazine flow"
        ),

        // 5-Photo Grids
        GridTemplate(
            id = "grid_5_center_hero",
            name = "5 Center Star",
            photoCount = 5,
            slots = listOf(
                SlotRect(0, 0f, 0f, 0.5f, 0.5f),
                SlotRect(1, 0.5f, 0f, 1f, 0.5f),
                SlotRect(2, 0f, 0.5f, 0.333f, 1f),
                SlotRect(3, 0.333f, 0.5f, 0.666f, 1f),
                SlotRect(4, 0.666f, 0.5f, 1f, 1f)
            ),
            description = "2 top photos + 3 bottom gallery"
        ),
        GridTemplate(
            id = "grid_5_hero_stack",
            name = "5 Big Left + 4 Grid",
            photoCount = 5,
            slots = listOf(
                SlotRect(0, 0f, 0f, 0.5f, 1f),
                SlotRect(1, 0.5f, 0f, 0.75f, 0.5f),
                SlotRect(2, 0.75f, 0f, 1f, 0.5f),
                SlotRect(3, 0.5f, 0.5f, 0.75f, 1f),
                SlotRect(4, 0.75f, 0.5f, 1f, 1f)
            ),
            description = "Half screen hero + 4 quadrant thumbnails"
        ),

        // 6-Photo Grids
        GridTemplate(
            id = "grid_6_3x2",
            name = "6 Classic (3x2)",
            photoCount = 6,
            slots = listOf(
                SlotRect(0, 0f, 0f, 0.333f, 0.5f),
                SlotRect(1, 0.333f, 0f, 0.666f, 0.5f),
                SlotRect(2, 0.666f, 0f, 1f, 0.5f),
                SlotRect(3, 0f, 0.5f, 0.333f, 1f),
                SlotRect(4, 0.333f, 0.5f, 0.666f, 1f),
                SlotRect(5, 0.666f, 0.5f, 1f, 1f)
            ),
            description = "Clean 6-photo mosaic"
        ),
        GridTemplate(
            id = "grid_6_hero_center",
            name = "6 Feature Center",
            photoCount = 6,
            slots = listOf(
                SlotRect(0, 0f, 0f, 0.5f, 0.65f),
                SlotRect(1, 0.5f, 0f, 1f, 0.65f),
                SlotRect(2, 0f, 0.65f, 0.25f, 1f),
                SlotRect(3, 0.25f, 0.65f, 0.5f, 1f),
                SlotRect(4, 0.5f, 0.65f, 0.75f, 1f),
                SlotRect(5, 0.75f, 0.65f, 1f, 1f)
            ),
            description = "2 large top frames + 4 bottom miniatures"
        ),

        // 9-Photo Grid
        GridTemplate(
            id = "grid_9_3x3",
            name = "9 Instagram Moodboard (3x3)",
            photoCount = 9,
            slots = (0 until 9).map { idx ->
                val r = idx / 3
                val c = idx % 3
                SlotRect(idx, c * 0.3333f, r * 0.3333f, (c + 1) * 0.3333f, (r + 1) * 0.3333f)
            },
            description = "Complete 3x3 moodboard grid"
        )
    )

    // Dynamic grid generator for any number of photos up to 24
    fun generateDynamicGrid(count: Int): GridTemplate {
        val safeCount = count.coerceIn(1, 24)
        val matched = gridTemplates.find { it.photoCount == safeCount }
        if (matched != null) return matched

        val cols = when {
            safeCount <= 1 -> 1
            safeCount <= 4 -> 2
            safeCount <= 9 -> 3
            safeCount <= 16 -> 4
            else -> 5
        }
        val rows = (safeCount + cols - 1) / cols
        val slots = mutableListOf<SlotRect>()
        for (i in 0 until safeCount) {
            val r = i / cols
            val c = i % cols
            val left = c.toFloat() / cols
            val top = r.toFloat() / rows
            val right = (c + 1).toFloat() / cols
            val bottom = (r + 1).toFloat() / rows
            slots.add(SlotRect(i, left, top, right, bottom))
        }
        return GridTemplate(
            id = "grid_dyn_$safeCount",
            name = "$safeCount Dynamic Auto Grid",
            photoCount = safeCount,
            slots = slots,
            description = "Auto-fitted $cols columns grid"
        )
    }

    val stickerPresets = listOf(
        // Emojis & Vibes
        "✨" to "Sparkles", "💖" to "Love Heart", "🔥" to "Fire Vibe", "📸" to "Vintage Camera",
        "🌴" to "Tropical Palm", "☕" to "Cozy Coffee", "🎂" to "Birthday Cake", "🌸" to "Cherry Blossom",
        "⭐" to "Gold Star", "👑" to "Crown", "🕊️" to "Peace Dove", "🦋" to "Butterfly",
        "🌊" to "Ocean Waves", "🎉" to "Party Popper", "🍸" to "Cocktail", "✈️" to "Travel Jet",
        "🌈" to "Rainbow", "🌻" to "Sunflower", "🎵" to "Music Melody", "💌" to "Love Letter"
    )

    val labelBadges = listOf(
        "MEMORIES", "WANDERLUST", "GOLDEN HOUR", "SUMMER VIBES", "BLESSED",
        "LOVE THIS", "CHASING SUNSETS", "MOOD", "DREAM BIG", "BEST DAY EVER"
    )

    val themeTemplates = listOf(
        ThemeTemplate(
            id = "tmpl_travel_wanderlust",
            title = "Wanderlust Adventure",
            category = "Travel",
            description = "Warm earth tones with hero frame and modern captioning",
            iconEmoji = "✈️",
            canvasPreset = CanvasPreset.PORTRAIT_4_5,
            gridTemplateId = "grid_3_hero_left",
            backgroundType = BackgroundType.GRADIENT,
            backgroundColor = 0xFF1E1B18,
            backgroundGradientColors = listOf(0xFF2C241E, 0xFF141210),
            cellSpacing = 10f,
            cornerRadius = 14f,
            borderWidth = 3f,
            borderColor = 0xFFD4AF37,
            sampleImageUrls = listOf(
                stockPhotos[1].uri,
                stockPhotos[2].uri,
                stockPhotos[0].uri
            ),
            defaultLayers = listOf(
                FreeformLayer(
                    type = LayerType.TEXT,
                    textContent = "EXPLORE & BEYOND",
                    textStylePreset = TextStylePreset.BOLD_DISPLAY,
                    fontSize = 26f,
                    textColor = 0xFFFFFFFF,
                    textBgColor = 0xAA000000,
                    x = 0.5f,
                    y = 0.92f,
                    width = 0.8f,
                    height = 0.08f
                ),
                FreeformLayer(
                    type = LayerType.STICKER,
                    stickerEmoji = "✨",
                    x = 0.88f,
                    y = 0.08f,
                    width = 0.15f,
                    height = 0.15f
                )
            )
        ),
        ThemeTemplate(
            id = "tmpl_aesthetic_moodboard",
            title = "Editorial Moodboard",
            category = "Aesthetic",
            description = "Clean minimalist editorial magazine aesthetic",
            iconEmoji = "✨",
            canvasPreset = CanvasPreset.SQUARE_1_1,
            gridTemplateId = "grid_4_masonry",
            backgroundType = BackgroundType.SOLID,
            backgroundColor = 0xFF0D0F18,
            backgroundGradientColors = listOf(0xFF0D0F18, 0xFF1E2235),
            cellSpacing = 8f,
            cornerRadius = 8f,
            borderWidth = 0f,
            borderColor = 0x00000000,
            sampleImageUrls = listOf(
                stockPhotos[4].uri,
                stockPhotos[5].uri,
                stockPhotos[7].uri,
                stockPhotos[0].uri
            ),
            defaultLayers = listOf(
                FreeformLayer(
                    type = LayerType.TEXT,
                    textContent = "CHAPTER 01 // ESSENCE",
                    textStylePreset = TextStylePreset.ELEGANT_SERIF,
                    fontSize = 18f,
                    textColor = 0xFFE2E8F0,
                    x = 0.5f,
                    y = 0.5f,
                    width = 0.7f,
                    height = 0.06f,
                    textBgColor = 0xDD12141F
                )
            )
        ),
        ThemeTemplate(
            id = "tmpl_birthday_celebration",
            title = "Birthday Sparkle",
            category = "Celebration",
            description = "Vibrant confetti colors with party badges & gold trims",
            iconEmoji = "🎂",
            canvasPreset = CanvasPreset.STORY_9_16,
            gridTemplateId = "grid_4_quad",
            backgroundType = BackgroundType.GRADIENT,
            backgroundColor = 0xFF2D123D,
            backgroundGradientColors = listOf(0xFF3B1042, 0xFF100720),
            cellSpacing = 12f,
            cornerRadius = 20f,
            borderWidth = 4f,
            borderColor = 0xFFEC4899,
            sampleImageUrls = listOf(
                stockPhotos[3].uri,
                stockPhotos[4].uri,
                stockPhotos[0].uri,
                stockPhotos[1].uri
            ),
            defaultLayers = listOf(
                FreeformLayer(
                    type = LayerType.TEXT,
                    textContent = "HAPPY BIRTHDAY!",
                    textStylePreset = TextStylePreset.NEON_GLOW,
                    fontSize = 32f,
                    textColor = 0xFFFF69B4,
                    x = 0.5f,
                    y = 0.08f,
                    width = 0.9f,
                    height = 0.1f
                ),
                FreeformLayer(
                    type = LayerType.STICKER,
                    stickerEmoji = "🎉",
                    x = 0.15f,
                    y = 0.88f,
                    width = 0.18f,
                    height = 0.18f
                ),
                FreeformLayer(
                    type = LayerType.STICKER,
                    stickerEmoji = "👑",
                    x = 0.85f,
                    y = 0.88f,
                    width = 0.18f,
                    height = 0.18f
                )
            )
        ),
        ThemeTemplate(
            id = "tmpl_romantic_wedding",
            title = "Golden Romance",
            category = "Wedding",
            description = "Ethereal champagne glow with delicate script typography",
            iconEmoji = "💍",
            canvasPreset = CanvasPreset.PORTRAIT_3_4,
            gridTemplateId = "grid_3_hero_top",
            backgroundType = BackgroundType.GRADIENT,
            backgroundColor = 0xFF1C1917,
            backgroundGradientColors = listOf(0xFF2E241E, 0xFF120F0D),
            cellSpacing = 14f,
            cornerRadius = 24f,
            borderWidth = 2f,
            borderColor = 0xFFFCD34D,
            sampleImageUrls = listOf(
                stockPhotos[6].uri,
                stockPhotos[4].uri,
                stockPhotos[7].uri
            ),
            defaultLayers = listOf(
                FreeformLayer(
                    type = LayerType.TEXT,
                    textContent = "Together Forever",
                    textStylePreset = TextStylePreset.HAND_SCRIPT,
                    fontSize = 28f,
                    textColor = 0xFFFDE68A,
                    x = 0.5f,
                    y = 0.52f,
                    width = 0.7f,
                    height = 0.08f
                ),
                FreeformLayer(
                    type = LayerType.STICKER,
                    stickerEmoji = "💖",
                    x = 0.5f,
                    y = 0.92f,
                    width = 0.12f,
                    height = 0.12f
                )
            )
        ),
        ThemeTemplate(
            id = "tmpl_retro_vintage",
            title = "Vintage Polaroid '94",
            category = "Retro",
            description = "Retro film grain aesthetic with polaroid styling and date stamps",
            iconEmoji = "📸",
            canvasPreset = CanvasPreset.SQUARE_1_1,
            gridTemplateId = "grid_2_split_v",
            backgroundType = BackgroundType.PATTERN_GRID,
            backgroundColor = 0xFF18181B,
            backgroundGradientColors = listOf(0xFF27272A, 0xFF18181B),
            cellSpacing = 16f,
            cornerRadius = 4f,
            borderWidth = 8f,
            borderColor = 0xFFF8FAFC,
            sampleImageUrls = listOf(
                stockPhotos[8].uri,
                stockPhotos[5].uri
            ),
            defaultLayers = listOf(
                FreeformLayer(
                    type = LayerType.TEXT,
                    textContent = "JULY 1994 // MEMORIES",
                    textStylePreset = TextStylePreset.RETRO_TYPE,
                    fontSize = 16f,
                    textColor = 0xFF0F172A,
                    textBgColor = 0xFFFFFFFF,
                    x = 0.5f,
                    y = 0.93f,
                    width = 0.85f,
                    height = 0.06f
                )
            )
        )
    )
}
