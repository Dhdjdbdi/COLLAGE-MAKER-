package com.example.ai

import android.graphics.Bitmap
import android.util.Base64
import com.example.BuildConfig
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

class GeminiAiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    /**
     * AI Auto-Layout Generator:
     * Analyzes photo count, titles, and tones to suggest 4-5 tailored layout arrangements
     */
    suspend fun generateAutoLayoutSuggestions(photos: List<PhotoItem>): List<AIAutoLayoutSuggestion> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext fallbackLayoutSuggestions(photos)
        }

        try {
            val photoSummaries = photos.mapIndexed { idx, p ->
                "Photo #${idx + 1}: ${p.title}, tone: ${p.tone.name}"
            }.joinToString("\n")

            val prompt = """
                You are an expert AI photo collage designer.
                I have ${photos.size} photos:
                $photoSummaries
                
                Suggest 4 distinct collage layout arrangement strategies.
                Return ONLY a JSON array with exactly 4 objects:
                [
                  {
                    "title": "Short catchy title (e.g. Cinematic Story, Symmetry Glow, Hero Focus)",
                    "subtitle": "Short 1-line reason why this layout fits the photos",
                    "gridTemplateId": "one of: grid_2_split_v, grid_2_split_h, grid_2_hero_ratio, grid_3_hero_left, grid_3_hero_top, grid_3_cols, grid_4_quad, grid_4_hero_left_stack, grid_4_masonry, grid_5_center_hero, grid_5_hero_stack, grid_6_3x2, grid_6_hero_center, grid_9_3x3",
                    "harmonicDescription": "Tone harmony summary (e.g., Warm Sunset balanced with Cool Forest)",
                    "storyTag": "Vibe tag (e.g., Editorial, Vibrant, Nostalgic, Minimalist)"
                  }
                ]
            """.trimIndent()

            val requestJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)
                put("generationConfig", JSONObject().put("responseMimeType", "application/json"))
            }

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(requestJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext fallbackLayoutSuggestions(photos)
            }

            val responseBody = response.body?.string() ?: return@withContext fallbackLayoutSuggestions(photos)
            val rootJson = JSONObject(responseBody)
            val candidates = rootJson.optJSONArray("candidates") ?: return@withContext fallbackLayoutSuggestions(photos)
            val firstCandidate = candidates.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text") ?: return@withContext fallbackLayoutSuggestions(photos)

            val array = JSONArray(text.trim())
            val suggestions = mutableListOf<AIAutoLayoutSuggestion>()
            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                suggestions.add(
                    AIAutoLayoutSuggestion(
                        id = "ai_sug_$i",
                        title = item.optString("title", "Smart Layout"),
                        subtitle = item.optString("subtitle", "Optimized composition"),
                        gridTemplateId = item.optString("gridTemplateId", defaultGridForCount(photos.size)),
                        score = 0.95f - (i * 0.05f),
                        harmonicDescription = item.optString("harmonicDescription", "Balanced color distribution"),
                        storyTag = item.optString("storyTag", "AI Arranged")
                    )
                )
            }
            if (suggestions.isNotEmpty()) suggestions else fallbackLayoutSuggestions(photos)
        } catch (e: Exception) {
            fallbackLayoutSuggestions(photos)
        }
    }

    /**
     * AI Color Harmony Arrangement:
     * Sorts photo items to ensure maximum visual balance (warm next to cool or chromatic gradient)
     */
    fun balanceColorHarmony(photos: List<PhotoItem>): List<PhotoItem> {
        if (photos.size <= 2) return photos
        // Separate warm and cool
        val warm = photos.filter { it.tone == ToneType.WARM || it.tone == ToneType.VIBRANT }.toMutableList()
        val cool = photos.filter { it.tone == ToneType.COOL || it.tone == ToneType.MOODY }.toMutableList()
        val neutrals = photos.filter { it.tone == ToneType.NEUTRAL }.toMutableList()

        val harmonized = mutableListOf<PhotoItem>()
        var takeWarm = true
        while (warm.isNotEmpty() || cool.isNotEmpty() || neutrals.isNotEmpty()) {
            if (takeWarm && warm.isNotEmpty()) {
                harmonized.add(warm.removeAt(0))
            } else if (!takeWarm && cool.isNotEmpty()) {
                harmonized.add(cool.removeAt(0))
            } else if (neutrals.isNotEmpty()) {
                harmonized.add(neutrals.removeAt(0))
            } else if (warm.isNotEmpty()) {
                harmonized.add(warm.removeAt(0))
            } else if (cool.isNotEmpty()) {
                harmonized.add(cool.removeAt(0))
            }
            takeWarm = !takeWarm
        }
        return harmonized
    }

    /**
     * AI Auto Caption / Quote Generator based on photo mood
     */
    suspend fun generateCaptionsForCollage(theme: String, count: Int = 4): List<String> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext listOf(
                "Chasing golden hours & unforgettable moments ✨",
                "Collected memories, timeless aesthetic 🎞️",
                "Stories written in light and color 🌈",
                "Living for the moments you can't put into words 💫"
            )
        }

        try {
            val prompt = "Generate $count short, aesthetic, typography-ready phrases or quotes for a photo collage with theme '$theme'. Maximum 6 words per quote. Return ONLY a JSON array of strings: [\"Quote 1\", \"Quote 2\"]"
            val requestJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)
                put("generationConfig", JSONObject().put("responseMimeType", "application/json"))
            }

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(requestJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: return@withContext emptyList()
            val rootJson = JSONObject(responseBody)
            val text = rootJson.optJSONArray("candidates")?.optJSONObject(0)
                ?.optJSONObject("content")?.optJSONArray("parts")?.optJSONObject(0)?.optString("text") ?: return@withContext emptyList()

            val array = JSONArray(text.trim())
            val list = mutableListOf<String>()
            for (i in 0 until array.length()) {
                list.add(array.getString(i))
            }
            list
        } catch (e: Exception) {
            listOf(
                "Chasing golden hours & unforgettable moments ✨",
                "Collected memories, timeless aesthetic 🎞️",
                "Stories written in light and color 🌈",
                "Living for the moments you can't put into words 💫"
            )
        }
    }

    /**
     * AI Generative Background Style Idea Generator
     */
    suspend fun suggestBackdropColorPalette(theme: String): List<Long> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext listOf(0xFF1E1B2E, 0xFF0D0E1A, 0xFF2A1B40)
        }
        try {
            val prompt = "Provide 3 aesthetic hex color codes (including 0xFF prefix) that create a luxurious gradient background for a '$theme' photo collage. Return JSON array of 3 strings: [\"0xFF1E1B2E\", \"0xFF0D0E1A\", \"0xFF2A1B40\"]"
            val requestJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)
                put("generationConfig", JSONObject().put("responseMimeType", "application/json"))
            }

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(requestJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val text = JSONObject(response.body?.string() ?: "")
                .optJSONArray("candidates")?.optJSONObject(0)
                ?.optJSONObject("content")?.optJSONArray("parts")?.optJSONObject(0)?.optString("text") ?: return@withContext listOf(0xFF1E1B2E, 0xFF0D0E1A)

            val array = JSONArray(text.trim())
            val colors = mutableListOf<Long>()
            for (i in 0 until array.length()) {
                val hex = array.getString(i).replace("#", "").replace("0x", "").replace("0X", "")
                val parsed = hex.toLongOrNull(16) ?: 0xFF1E1B2EL
                val colorVal = if (parsed <= 0xFFFFFFL) (0xFF000000L or parsed) else parsed
                colors.add(colorVal)
            }
            if (colors.isNotEmpty()) colors else listOf(0xFF1E1B2E, 0xFF0D0E1A)
        } catch (e: Exception) {
            listOf(0xFF1E1B2E, 0xFF0D0E1A, 0xFF2A1B40)
        }
    }

    private fun fallbackLayoutSuggestions(photos: List<PhotoItem>): List<AIAutoLayoutSuggestion> {
        val count = photos.size
        return when (count) {
            2 -> listOf(
                AIAutoLayoutSuggestion("sug_1", "Symmetric Duo", "Side-by-side balanced alignment", "grid_2_split_v", 0.98f, "Equal visual weight", "Classic"),
                AIAutoLayoutSuggestion("sug_2", "Cinematic Stack", "Top & bottom panoramic split", "grid_2_split_h", 0.92f, "Wide horizontal flow", "Cinematic"),
                AIAutoLayoutSuggestion("sug_3", "Hero & Spotlight", "Featured main photo with secondary glance", "grid_2_hero_ratio", 0.89f, "Dynamic 65/35 asymmetry", "Editorial")
            )
            3 -> listOf(
                AIAutoLayoutSuggestion("sug_1", "Hero Story Left", "Prominent left feature with dual right stack", "grid_3_hero_left", 0.98f, "Golden ratio storytelling", "Magazine"),
                AIAutoLayoutSuggestion("sug_2", "Panorama Header", "Top hero banner with two grounded miniatures", "grid_3_hero_top", 0.94f, "Landscape focus", "Modern"),
                AIAutoLayoutSuggestion("sug_3", "Triptych Strips", "Three vertical aesthetic strips", "grid_3_cols", 0.90f, "Rhythmic vertical balance", "Minimalist")
            )
            4 -> listOf(
                AIAutoLayoutSuggestion("sug_1", "Editorial Masonry", "Staggered dynamic magazine flow", "grid_4_masonry", 0.99f, "Harmonized organic pacing", "Vogue"),
                AIAutoLayoutSuggestion("sug_2", "Classic Quad Grid", "Perfect 2x2 symmetry", "grid_4_quad", 0.95f, "Timeless clean order", "Balanced"),
                AIAutoLayoutSuggestion("sug_3", "Hero Spotlight", "Dominant main photo with vertical triple thumbnails", "grid_4_hero_left_stack", 0.91f, "Story lead with supporting details", "Showcase")
            )
            5 -> listOf(
                AIAutoLayoutSuggestion("sug_1", "Center Star Flow", "Dual top header with triple bottom mosaic", "grid_5_center_hero", 0.98f, "Varied rhythm & rich depth", "Gallery"),
                AIAutoLayoutSuggestion("sug_2", "Half-Screen Showcase", "Large left half with 4 mini quadrant cards", "grid_5_hero_stack", 0.93f, "High impact storytelling", "Lookbook")
            )
            6 -> listOf(
                AIAutoLayoutSuggestion("sug_1", "Mosaic 3x2", "Harmonious six-frame grid layout", "grid_6_3x2", 0.98f, "Even color distribution", "Mosaic"),
                AIAutoLayoutSuggestion("sug_2", "Dual Feature Top", "Two wide header shots with four bottom frames", "grid_6_hero_center", 0.94f, "Header emphasis", "Collage")
            )
            else -> listOf(
                AIAutoLayoutSuggestion("sug_1", "Dynamic AI Grid", "Optimized automatic arrangement for $count photos", "grid_dyn_$count", 0.98f, "Smart geometric fit", "Adaptive")
            )
        }
    }

    private fun defaultGridForCount(count: Int): String {
        return when (count) {
            2 -> "grid_2_split_v"
            3 -> "grid_3_hero_left"
            4 -> "grid_4_masonry"
            5 -> "grid_5_center_hero"
            6 -> "grid_6_3x2"
            9 -> "grid_9_3x3"
            else -> "grid_dyn_$count"
        }
    }
}
