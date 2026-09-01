package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collage_projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val canvasPresetName: String,
    val isFreeform: Boolean,
    val gridTemplateId: String,
    val backgroundTypeName: String,
    val backgroundColor: Long,
    val backgroundGradientColors: String, // comma separated hex
    val blurPhotoUri: String?,
    val cellSpacing: Float,
    val cornerRadius: Float,
    val borderWidth: Float,
    val borderColor: Long,
    val photosJson: String,
    val freeformLayersJson: String,
    val thumbnailUri: String? = null
)
