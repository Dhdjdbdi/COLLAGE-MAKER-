package com.example.data.repository

import com.example.data.local.ProjectDao
import com.example.data.local.ProjectEntity
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject

class ProjectRepository(private val projectDao: ProjectDao) {

    val allProjects: Flow<List<ProjectEntity>> = projectDao.getAllProjects()

    suspend fun getProjectById(id: Long): ProjectEntity? = projectDao.getProjectById(id)

    suspend fun saveProject(
        id: Long = 0,
        title: String,
        canvasPreset: CanvasPreset,
        isFreeform: Boolean,
        gridTemplateId: String,
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
        thumbnailUri: String? = null
    ): Long {
        val photosJson = serializePhotos(photos)
        val layersJson = serializeLayers(layers)
        val gradientString = backgroundGradients.joinToString(",") { it.toString() }

        val entity = ProjectEntity(
            id = id,
            title = title,
            updatedAt = System.currentTimeMillis(),
            canvasPresetName = canvasPreset.name,
            isFreeform = isFreeform,
            gridTemplateId = gridTemplateId,
            backgroundTypeName = backgroundType.name,
            backgroundColor = backgroundColor,
            backgroundGradientColors = gradientString,
            blurPhotoUri = blurPhotoUri,
            cellSpacing = cellSpacing,
            cornerRadius = cornerRadius,
            borderWidth = borderWidth,
            borderColor = borderColor,
            photosJson = photosJson,
            freeformLayersJson = layersJson,
            thumbnailUri = thumbnailUri
        )

        return if (id > 0) {
            projectDao.updateProject(entity)
            id
        } else {
            projectDao.insertProject(entity)
        }
    }

    suspend fun deleteProject(id: Long) = projectDao.deleteProjectById(id)

    companion object {
        fun serializePhotos(photos: List<PhotoItem>): String {
            val jsonArray = JSONArray()
            for (p in photos) {
                val obj = JSONObject()
                obj.put("id", p.id)
                obj.put("uri", p.uri)
                obj.put("title", p.title)
                obj.put("cropOffsetX", p.cropOffsetX.toDouble())
                obj.put("cropOffsetY", p.cropOffsetY.toDouble())
                obj.put("rotation", p.rotation.toDouble())
                obj.put("scale", p.scale.toDouble())
                obj.put("filter", p.filter.name)
                obj.put("brightness", p.brightness.toDouble())
                obj.put("contrast", p.contrast.toDouble())
                obj.put("saturation", p.saturation.toDouble())
                obj.put("warmth", p.warmth.toDouble())
                obj.put("vignette", p.vignette.toDouble())
                obj.put("blur", p.blur.toDouble())
                obj.put("isAiEnhanced", p.isAiEnhanced)
                obj.put("hasBgRemoved", p.hasBgRemoved)
                obj.put("generativeFillPrompt", p.generativeFillPrompt ?: "")
                obj.put("tone", p.tone.name)
                jsonArray.put(obj)
            }
            return jsonArray.toString()
        }

        fun deserializePhotos(json: String): List<PhotoItem> {
            if (json.isBlank()) return emptyList()
            return try {
                val array = JSONArray(json)
                val list = mutableListOf<PhotoItem>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        PhotoItem(
                            id = obj.optString("id"),
                            uri = obj.optString("uri"),
                            title = obj.optString("title", "Photo"),
                            cropOffsetX = obj.optDouble("cropOffsetX", 0.5).toFloat(),
                            cropOffsetY = obj.optDouble("cropOffsetY", 0.5).toFloat(),
                            rotation = obj.optDouble("rotation", 0.0).toFloat(),
                            scale = obj.optDouble("scale", 1.0).toFloat(),
                            filter = try { FilterType.valueOf(obj.optString("filter", FilterType.NONE.name)) } catch (e: Exception) { FilterType.NONE },
                            brightness = obj.optDouble("brightness", 0.0).toFloat(),
                            contrast = obj.optDouble("contrast", 1.0).toFloat(),
                            saturation = obj.optDouble("saturation", 1.0).toFloat(),
                            warmth = obj.optDouble("warmth", 0.0).toFloat(),
                            vignette = obj.optDouble("vignette", 0.0).toFloat(),
                            blur = obj.optDouble("blur", 0.0).toFloat(),
                            isAiEnhanced = obj.optBoolean("isAiEnhanced", false),
                            hasBgRemoved = obj.optBoolean("hasBgRemoved", false),
                            generativeFillPrompt = obj.optString("generativeFillPrompt").takeIf { it.isNotBlank() },
                            tone = try { ToneType.valueOf(obj.optString("tone", ToneType.NEUTRAL.name)) } catch (e: Exception) { ToneType.NEUTRAL }
                        )
                    )
                }
                list
            } catch (e: Exception) {
                emptyList()
            }
        }

        fun serializeLayers(layers: List<FreeformLayer>): String {
            val jsonArray = JSONArray()
            for (l in layers) {
                val obj = JSONObject()
                obj.put("id", l.id)
                obj.put("type", l.type.name)
                obj.put("photoId", l.photoId ?: "")
                obj.put("textContent", l.textContent)
                obj.put("textStylePreset", l.textStylePreset.name)
                obj.put("fontSize", l.fontSize.toDouble())
                obj.put("textColor", l.textColor)
                l.textBgColor?.let { obj.put("textBgColor", it) }
                obj.put("hasTextShadow", l.hasTextShadow)
                obj.put("stickerEmoji", l.stickerEmoji ?: "")
                obj.put("stickerLabel", l.stickerLabel ?: "")
                obj.put("shapeType", l.shapeType ?: "")
                obj.put("x", l.x.toDouble())
                obj.put("y", l.y.toDouble())
                obj.put("width", l.width.toDouble())
                obj.put("height", l.height.toDouble())
                obj.put("rotation", l.rotation.toDouble())
                obj.put("zIndex", l.zIndex)
                obj.put("isLocked", l.isLocked)
                obj.put("opacity", l.opacity.toDouble())
                jsonArray.put(obj)
            }
            return jsonArray.toString()
        }

        fun deserializeLayers(json: String): List<FreeformLayer> {
            if (json.isBlank()) return emptyList()
            return try {
                val array = JSONArray(json)
                val list = mutableListOf<FreeformLayer>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        FreeformLayer(
                            id = obj.optString("id"),
                            type = try { LayerType.valueOf(obj.optString("type", LayerType.PHOTO.name)) } catch (e: Exception) { LayerType.PHOTO },
                            photoId = obj.optString("photoId").takeIf { it.isNotBlank() },
                            textContent = obj.optString("textContent", "Text"),
                            textStylePreset = try { TextStylePreset.valueOf(obj.optString("textStylePreset", TextStylePreset.MODERN_SANS.name)) } catch (e: Exception) { TextStylePreset.MODERN_SANS },
                            fontSize = obj.optDouble("fontSize", 24.0).toFloat(),
                            textColor = obj.optLong("textColor", 0xFFFFFFFF),
                            textBgColor = if (obj.has("textBgColor")) obj.optLong("textBgColor") else null,
                            hasTextShadow = obj.optBoolean("hasTextShadow", true),
                            stickerEmoji = obj.optString("stickerEmoji").takeIf { it.isNotBlank() },
                            stickerLabel = obj.optString("stickerLabel").takeIf { it.isNotBlank() },
                            shapeType = obj.optString("shapeType").takeIf { it.isNotBlank() },
                            x = obj.optDouble("x", 0.5).toFloat(),
                            y = obj.optDouble("y", 0.5).toFloat(),
                            width = obj.optDouble("width", 0.4).toFloat(),
                            height = obj.optDouble("height", 0.4).toFloat(),
                            rotation = obj.optDouble("rotation", 0.0).toFloat(),
                            zIndex = obj.optInt("zIndex", 0),
                            isLocked = obj.optBoolean("isLocked", false),
                            opacity = obj.optDouble("opacity", 1.0).toFloat()
                        )
                    )
                }
                list
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}
