package com.example.engine

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asAndroidColorFilter
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.URL
import kotlin.math.cos
import kotlin.math.sin

object ImageEngine {

    fun getColorMatrixForFilter(filter: FilterType): ColorMatrix {
        val cm = ColorMatrix()
        when (filter) {
            FilterType.NONE -> cm.reset()
            FilterType.VIVID -> {
                // High saturation and dynamic contrast
                cm.setSaturation(1.4f)
                val contrast = 1.15f
                val scale = contrast
                val translate = (-0.5f * scale + 0.5f) * 255f
                val contrastMatrix = ColorMatrix(
                    floatArrayOf(
                        scale, 0f, 0f, 0f, translate,
                        0f, scale, 0f, 0f, translate,
                        0f, 0f, scale, 0f, translate,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                cm.postConcat(contrastMatrix)
            }
            FilterType.NOIR -> {
                cm.setSaturation(0f)
                // Boost noir contrast
                val noirMatrix = ColorMatrix(
                    floatArrayOf(
                        1.2f, 0f, 0f, 0f, -20f,
                        0f, 1.2f, 0f, 0f, -20f,
                        0f, 0f, 1.2f, 0f, -20f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                cm.postConcat(noirMatrix)
            }
            FilterType.SUNSET -> {
                // Warm golden red/orange tint
                val sunsetMatrix = ColorMatrix(
                    floatArrayOf(
                        1.25f, 0f, 0f, 0f, 15f,
                        0f, 1.05f, 0f, 0f, 5f,
                        0f, 0f, 0.85f, 0f, -15f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                cm.set(sunsetMatrix)
            }
            FilterType.CYBERPUNK -> {
                // Magenta-Cyan punch
                val cyberMatrix = ColorMatrix(
                    floatArrayOf(
                        1.2f, 0f, 0.2f, 0f, 10f,
                        0f, 1.1f, 0.2f, 0f, -10f,
                        0.2f, 0.2f, 1.4f, 0f, 25f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                cm.set(cyberMatrix)
            }
            FilterType.VINTAGE -> {
                // Sepia/Warm tone + faded blacks
                val vintageMatrix = ColorMatrix(
                    floatArrayOf(
                        0.9f, 0.1f, 0.1f, 0f, 20f,
                        0.1f, 0.85f, 0.1f, 0f, 15f,
                        0.1f, 0.1f, 0.7f, 0f, 10f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                cm.set(vintageMatrix)
            }
            FilterType.GOLDEN_HOUR -> {
                val goldenMatrix = ColorMatrix(
                    floatArrayOf(
                        1.2f, 0f, 0f, 0f, 25f,
                        0f, 1.1f, 0f, 0f, 15f,
                        0f, 0f, 0.8f, 0f, -10f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                cm.set(goldenMatrix)
            }
            FilterType.PASTEL -> {
                cm.setSaturation(0.75f)
                val pastelMatrix = ColorMatrix(
                    floatArrayOf(
                        0.9f, 0f, 0f, 0f, 30f,
                        0f, 0.9f, 0f, 0f, 30f,
                        0f, 0f, 0.9f, 0f, 30f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                cm.postConcat(pastelMatrix)
            }
            FilterType.CHROME -> {
                cm.setSaturation(1.2f)
                val chromeMatrix = ColorMatrix(
                    floatArrayOf(
                        1.1f, 0.1f, 0.1f, 0f, 0f,
                        0.1f, 1.2f, 0.1f, 0f, 0f,
                        0.1f, 0.1f, 1.3f, 0f, 10f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                cm.postConcat(chromeMatrix)
            }
            FilterType.DRAMA -> {
                val dramaMatrix = ColorMatrix(
                    floatArrayOf(
                        1.35f, 0f, 0f, 0f, -30f,
                        0f, 1.35f, 0f, 0f, -30f,
                        0f, 0f, 1.35f, 0f, -30f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                cm.set(dramaMatrix)
            }
            FilterType.MATTE -> {
                val matteMatrix = ColorMatrix(
                    floatArrayOf(
                        0.85f, 0f, 0f, 0f, 35f,
                        0f, 0.85f, 0f, 0f, 35f,
                        0f, 0f, 0.85f, 0f, 35f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                cm.set(matteMatrix)
            }
            FilterType.EMERALD -> {
                val emeraldMatrix = ColorMatrix(
                    floatArrayOf(
                        0.8f, 0.1f, 0.1f, 0f, -10f,
                        0.1f, 1.3f, 0.1f, 0f, 15f,
                        0.1f, 0.1f, 1.0f, 0f, 5f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                cm.set(emeraldMatrix)
            }
        }
        return cm
    }

    fun buildAdjustColorMatrix(
        brightness: Float, // -1f..1f
        contrast: Float,   // 0.5f..2f
        saturation: Float, // 0f..2f
        warmth: Float,     // -1f..1f
        filter: FilterType,
        isAiEnhanced: Boolean
    ): ColorMatrix {
        val result = ColorMatrix()

        // 1. Preset filter
        result.set(getColorMatrixForFilter(filter))

        // 2. AI Auto Enhance boost
        if (isAiEnhanced) {
            val aiMatrix = ColorMatrix(
                floatArrayOf(
                    1.12f, 0f, 0f, 0f, 10f,
                    0f, 1.12f, 0f, 0f, 10f,
                    0f, 0f, 1.12f, 0f, 10f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            result.postConcat(aiMatrix)
            val satMatrix = ColorMatrix()
            satMatrix.setSaturation(1.15f)
            result.postConcat(satMatrix)
        }

        // 3. Brightness
        if (brightness != 0f) {
            val bVal = brightness * 100f
            val brightMatrix = ColorMatrix(
                floatArrayOf(
                    1f, 0f, 0f, 0f, bVal,
                    0f, 1f, 0f, 0f, bVal,
                    0f, 0f, 1f, 0f, bVal,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            result.postConcat(brightMatrix)
        }

        // 4. Contrast
        if (contrast != 1f) {
            val scale = contrast
            val translate = (-0.5f * scale + 0.5f) * 255f
            val contrastMatrix = ColorMatrix(
                floatArrayOf(
                    scale, 0f, 0f, 0f, translate,
                    0f, scale, 0f, 0f, translate,
                    0f, 0f, scale, 0f, translate,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            result.postConcat(contrastMatrix)
        }

        // 5. Saturation
        if (saturation != 1f) {
            val satMatrix = ColorMatrix()
            satMatrix.setSaturation(saturation)
            result.postConcat(satMatrix)
        }

        // 6. Warmth
        if (warmth != 0f) {
            val rAdd = warmth * 30f
            val bAdd = -warmth * 30f
            val warmthMatrix = ColorMatrix(
                floatArrayOf(
                    1f, 0f, 0f, 0f, rAdd,
                    0f, 1f, 0f, 0f, 0f,
                    0f, 0f, 1f, 0f, bAdd,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            result.postConcat(warmthMatrix)
        }

        return result
    }

    fun getComposeColorFilter(
        brightness: Float,
        contrast: Float,
        saturation: Float,
        warmth: Float,
        filter: FilterType,
        isAiEnhanced: Boolean
    ): ColorFilter {
        val cm = buildAdjustColorMatrix(brightness, contrast, saturation, warmth, filter, isAiEnhanced)
        return ColorFilter.colorMatrix(androidx.compose.ui.graphics.ColorMatrix(cm.array))
    }

    /**
     * Renders a high-resolution Bitmap from Collage state
     */
    suspend fun renderHighResCollage(
        context: Context,
        targetWidth: Int,
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
        layers: List<FreeformLayer>
    ): Bitmap = withContext(Dispatchers.IO) {
        val targetHeight = (targetWidth * (preset.ratioH / preset.ratioW)).toInt()
        val bitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Draw Background
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        when (backgroundType) {
            BackgroundType.SOLID -> {
                bgPaint.color = backgroundColor.toInt()
                canvas.drawRect(0f, 0f, targetWidth.toFloat(), targetHeight.toFloat(), bgPaint)
            }
            BackgroundType.GRADIENT -> {
                val startColor = backgroundGradients.getOrElse(0) { 0xFF12141FL }.toInt()
                val endColor = backgroundGradients.getOrElse(1) { 0xFF1E2235L }.toInt()
                val shader = android.graphics.LinearGradient(
                    0f, 0f, targetWidth.toFloat(), targetHeight.toFloat(),
                    startColor, endColor, android.graphics.Shader.TileMode.CLAMP
                )
                bgPaint.shader = shader
                canvas.drawRect(0f, 0f, targetWidth.toFloat(), targetHeight.toFloat(), bgPaint)
            }
            BackgroundType.PATTERN_GRID -> {
                bgPaint.color = backgroundColor.toInt()
                canvas.drawRect(0f, 0f, targetWidth.toFloat(), targetHeight.toFloat(), bgPaint)
                val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = 0x22FFFFFF
                    strokeWidth = 3f
                }
                val step = targetWidth / 20f
                var x = 0f
                while (x <= targetWidth) {
                    canvas.drawLine(x, 0f, x, targetHeight.toFloat(), linePaint)
                    x += step
                }
                var y = 0f
                while (y <= targetHeight) {
                    canvas.drawLine(0f, y, targetWidth.toFloat(), y, linePaint)
                    y += step
                }
            }
            BackgroundType.PATTERN_DOTS -> {
                bgPaint.color = backgroundColor.toInt()
                canvas.drawRect(0f, 0f, targetWidth.toFloat(), targetHeight.toFloat(), bgPaint)
                val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = 0x33FFFFFF
                }
                val step = targetWidth / 16f
                for (r in 0..(targetHeight / step).toInt()) {
                    for (c in 0..(targetWidth / step).toInt()) {
                        canvas.drawCircle(c * step + step / 2f, r * step + step / 2f, step / 8f, dotPaint)
                    }
                }
            }
            BackgroundType.PATTERN_STRIPES, BackgroundType.PATTERN_CHEVRON -> {
                bgPaint.color = backgroundColor.toInt()
                canvas.drawRect(0f, 0f, targetWidth.toFloat(), targetHeight.toFloat(), bgPaint)
                val stripePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = 0x1AFFFFFF
                    strokeWidth = 8f
                }
                for (i in -targetHeight..targetWidth step 40) {
                    canvas.drawLine(i.toFloat(), 0f, (i + targetHeight).toFloat(), targetHeight.toFloat(), stripePaint)
                }
            }
            BackgroundType.BLURRED_PHOTO -> {
                bgPaint.color = backgroundColor.toInt()
                canvas.drawRect(0f, 0f, targetWidth.toFloat(), targetHeight.toFloat(), bgPaint)
            }
        }

        // 2. Draw Grid Photos or Freeform Photos
        val scaleFactor = targetWidth / 1000f
        val scaledSpacing = cellSpacing * scaleFactor * 2f
        val scaledRadius = cornerRadius * scaleFactor * 2f
        val scaledBorder = borderWidth * scaleFactor * 2f

        if (!isFreeform && photos.isNotEmpty()) {
            val slots = gridTemplate.slots
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = borderColor.toInt()
                strokeWidth = scaledBorder
                style = Paint.Style.STROKE
            }

            for (i in slots.indices) {
                if (i >= photos.size) break
                val slot = slots[i]
                val photo = photos[i]

                val left = slot.left * targetWidth + scaledSpacing / 2f
                val top = slot.top * targetHeight + scaledSpacing / 2f
                val right = slot.right * targetWidth - scaledSpacing / 2f
                val bottom = slot.bottom * targetHeight - scaledSpacing / 2f
                val rect = RectF(left, top, right, bottom)

                val clipPath = Path().apply {
                    addRoundRect(rect, scaledRadius, scaledRadius, Path.Direction.CW)
                }

                canvas.save()
                canvas.clipPath(clipPath)

                // Load and draw photo
                val photoBitmap = loadBitmap(context, photo.uri)
                if (photoBitmap != null) {
                    val photoPaint = Paint(Paint.ANTI_ALIAS_FLAG)
                    val cm = buildAdjustColorMatrix(
                        photo.brightness, photo.contrast, photo.saturation,
                        photo.warmth, photo.filter, photo.isAiEnhanced
                    )
                    photoPaint.colorFilter = ColorMatrixColorFilter(cm)

                    // Draw center crop or smart offset
                    val srcRect = calculateCropRect(photoBitmap.width, photoBitmap.height, rect.width(), rect.height(), photo.cropOffsetX, photo.cropOffsetY)
                    canvas.drawBitmap(photoBitmap, srcRect, rect, photoPaint)
                } else {
                    // Placeholder background
                    val placeholderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = 0xFF2A2D3D.toInt()
                    }
                    canvas.drawRect(rect, placeholderPaint)
                }

                canvas.restore()

                // Draw Border
                if (scaledBorder > 0f) {
                    canvas.drawRoundRect(rect, scaledRadius, scaledRadius, borderPaint)
                }
            }
        }

        // 3. Draw Overlay Layers (Text, Stickers, Badges)
        for (layer in layers) {
            if (layer.type == LayerType.STICKER && layer.stickerEmoji != null) {
                val cx = layer.x * targetWidth
                val cy = layer.y * targetHeight
                val emojiPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    textSize = 54f * scaleFactor
                    textAlign = Paint.Align.CENTER
                }
                canvas.save()
                canvas.rotate(layer.rotation, cx, cy)
                canvas.drawText(layer.stickerEmoji, cx, cy + emojiPaint.textSize / 3f, emojiPaint)
                canvas.restore()
            } else if (layer.type == LayerType.TEXT && layer.textContent.isNotBlank()) {
                val cx = layer.x * targetWidth
                val cy = layer.y * targetHeight
                val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    textSize = layer.fontSize * scaleFactor * 1.5f
                    color = layer.textColor.toInt()
                    textAlign = Paint.Align.CENTER
                    isFakeBoldText = true
                    if (layer.hasTextShadow) {
                        setShadowLayer(8f * scaleFactor, 2f, 4f, 0xCC000000.toInt())
                    }
                }

                canvas.save()
                canvas.rotate(layer.rotation, cx, cy)

                // Background box if any
                if (layer.textBgColor != null) {
                    val textWidth = textPaint.measureText(layer.textContent)
                    val textHeight = textPaint.textSize
                    val bgRect = RectF(
                        cx - textWidth / 2f - 24f * scaleFactor,
                        cy - textHeight / 2f - 16f * scaleFactor,
                        cx + textWidth / 2f + 24f * scaleFactor,
                        cy + textHeight / 2f + 16f * scaleFactor
                    )
                    val boxPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = layer.textBgColor.toInt()
                    }
                    canvas.drawRoundRect(bgRect, 12f * scaleFactor, 12f * scaleFactor, boxPaint)
                }

                canvas.drawText(layer.textContent, cx, cy + textPaint.textSize / 3f, textPaint)
                canvas.restore()
            }
        }

        bitmap
    }

    private fun calculateCropRect(srcW: Int, srcH: Int, dstW: Float, dstH: Float, focalX: Float, focalY: Float): android.graphics.Rect {
        val srcRatio = srcW.toFloat() / srcH.toFloat()
        val dstRatio = dstW / dstH

        var cropW = srcW
        var cropH = srcH

        if (srcRatio > dstRatio) {
            cropW = (srcH * dstRatio).toInt()
        } else {
            cropH = (srcW / dstRatio).toInt()
        }

        val centerX = (srcW * focalX).toInt()
        val centerY = (srcH * focalY).toInt()

        var left = centerX - cropW / 2
        var top = centerY - cropH / 2

        if (left < 0) left = 0
        if (top < 0) top = 0
        if (left + cropW > srcW) left = srcW - cropW
        if (top + cropH > srcH) top = srcH - cropH

        return android.graphics.Rect(left, top, left + cropW, top + cropH)
    }

    private suspend fun loadBitmap(context: Context, uriString: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            if (uriString.startsWith("http://") || uriString.startsWith("https://")) {
                val connection = URL(uriString).openConnection()
                connection.connectTimeout = 15000
                connection.readTimeout = 15000
                BitmapFactory.decodeStream(connection.getInputStream())
            } else if (uriString.startsWith("content://") || uriString.startsWith("file://")) {
                val uri = Uri.parse(uriString)
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Saves Bitmap to device Gallery / Pictures
     */
    suspend fun saveBitmapToStorage(context: Context, bitmap: Bitmap, title: String): Uri? = withContext(Dispatchers.IO) {
        val fileName = "Collage_${System.currentTimeMillis()}.png"
        var fos: OutputStream? = null
        var imageUri: Uri? = null

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/CollageCraft")
                }
                val resolver = context.contentResolver
                imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                if (imageUri != null) {
                    fos = resolver.openOutputStream(imageUri)
                }
            } else {
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/CollageCraft"
                val file = File(imagesDir)
                if (!file.exists()) file.mkdirs()
                val image = File(imagesDir, fileName)
                fos = FileOutputStream(image)
                imageUri = Uri.fromFile(image)
            }

            fos?.use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            imageUri
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Creates a Share Intent for the rendered collage image
     */
    fun createShareIntent(context: Context, imageUri: Uri, message: String = "Check out my photo collage created with CollageCraft AI! ✨"): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, imageUri)
            putExtra(Intent.EXTRA_TEXT, message)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
}
