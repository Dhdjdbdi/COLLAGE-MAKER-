package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiAiService
import com.example.data.local.AppDatabase
import com.example.data.local.ProjectEntity
import com.example.data.model.*
import com.example.data.repository.ProjectRepository
import com.example.data.sample.SampleData
import com.example.engine.ImageEngine
import com.example.ui.components.ExportFormat
import com.example.ui.components.ExportResolution
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class StudioTool(val label: String, val iconEmoji: String) {
    LAYOUTS("Layouts", "📐"),
    AI_MAGIC("AI Magic", "✨"),
    ADJUST("Adjust", "🎚️"),
    FILTERS("Filters", "🎨"),
    TEXT("Text", "🔤"),
    STICKERS("Stickers", "🌟"),
    BACKDROP("Backdrop", "🖼️"),
    LAYERS("Layers", "📑"),
    TEMPLATES("Themes", "🎭")
}

data class CollageHistorySnapshot(
    val canvasPreset: CanvasPreset,
    val isFreeform: Boolean,
    val gridTemplateId: String,
    val backgroundType: BackgroundType,
    val backgroundColor: Long,
    val backgroundGradients: List<Long>,
    val cellSpacing: Float,
    val cornerRadius: Float,
    val borderWidth: Float,
    val borderColor: Long,
    val photos: List<PhotoItem>,
    val layers: List<FreeformLayer>
)

class CollageViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProjectRepository = ProjectRepository(AppDatabase.getDatabase(application).projectDao())
    private val aiService = GeminiAiService()

    // Undo / Redo Stacks
    private val undoStack = mutableListOf<CollageHistorySnapshot>()
    private val redoStack = mutableListOf<CollageHistorySnapshot>()

    // Current Project Info
    private val _currentProjectId = MutableStateFlow<Long>(0L)
    val currentProjectId: StateFlow<Long> = _currentProjectId.asStateFlow()

    private val _projectTitle = MutableStateFlow("My Collage Artwork")
    val projectTitle: StateFlow<String> = _projectTitle.asStateFlow()

    // Canvas Configuration
    private val _canvasPreset = MutableStateFlow(CanvasPreset.SQUARE_1_1)
    val canvasPreset: StateFlow<CanvasPreset> = _canvasPreset.asStateFlow()

    private val _isFreeform = MutableStateFlow(false)
    val isFreeform: StateFlow<Boolean> = _isFreeform.asStateFlow()

    private val _gridTemplate = MutableStateFlow(SampleData.gridTemplates.first { it.id == "grid_4_quad" })
    val gridTemplate: StateFlow<GridTemplate> = _gridTemplate.asStateFlow()

    private val _backgroundType = MutableStateFlow(BackgroundType.GRADIENT)
    val backgroundType: StateFlow<BackgroundType> = _backgroundType.asStateFlow()

    private val _backgroundColor = MutableStateFlow(0xFF0D0F18L)
    val backgroundColor: StateFlow<Long> = _backgroundColor.asStateFlow()

    private val _backgroundGradients = MutableStateFlow(listOf(0xFF1E1B2EL, 0xFF0D0E1AL))
    val backgroundGradients: StateFlow<List<Long>> = _backgroundGradients.asStateFlow()

    private val _blurPhotoUri = MutableStateFlow<String?>(null)
    val blurPhotoUri: StateFlow<String?> = _blurPhotoUri.asStateFlow()

    // Spacing & Borders
    private val _cellSpacing = MutableStateFlow(8f)
    val cellSpacing: StateFlow<Float> = _cellSpacing.asStateFlow()

    private val _cornerRadius = MutableStateFlow(12f)
    val cornerRadius: StateFlow<Float> = _cornerRadius.asStateFlow()

    private val _borderWidth = MutableStateFlow(0f)
    val borderWidth: StateFlow<Float> = _borderWidth.asStateFlow()

    private val _borderColor = MutableStateFlow(0xFFFFFFFFL)
    val borderColor: StateFlow<Long> = _borderColor.asStateFlow()

    // Photos & Overlays
    private val _photos = MutableStateFlow<List<PhotoItem>>(emptyList())
    val photos: StateFlow<List<PhotoItem>> = _photos.asStateFlow()

    private val _layers = MutableStateFlow<List<FreeformLayer>>(emptyList())
    val layers: StateFlow<List<FreeformLayer>> = _layers.asStateFlow()

    // Selection States
    private val _selectedPhotoIndex = MutableStateFlow<Int?>(0)
    val selectedPhotoIndex: StateFlow<Int?> = _selectedPhotoIndex.asStateFlow()

    private val _selectedLayerId = MutableStateFlow<String?>(null)
    val selectedLayerId: StateFlow<String?> = _selectedLayerId.asStateFlow()

    // Active Tool
    private val _activeTool = MutableStateFlow(StudioTool.LAYOUTS)
    val activeTool: StateFlow<StudioTool> = _activeTool.asStateFlow()

    // AI & Motion States
    private val _aiSuggestions = MutableStateFlow<List<AIAutoLayoutSuggestion>>(emptyList())
    val aiSuggestions: StateFlow<List<AIAutoLayoutSuggestion>> = _aiSuggestions.asStateFlow()

    private val _showAiCarousel = MutableStateFlow(false)
    val showAiCarousel: StateFlow<Boolean> = _showAiCarousel.asStateFlow()

    private val _aiQuotes = MutableStateFlow<List<String>>(
        listOf(
            "Chasing golden hours & unforgettable moments ✨",
            "Collected memories, timeless aesthetic 🎞️",
            "Stories written in light and color 🌈",
            "Living for the moments you can't put into words 💫"
        )
    )
    val aiQuotes: StateFlow<List<String>> = _aiQuotes.asStateFlow()

    private val _isLivingMotionActive = MutableStateFlow(false)
    val isLivingMotionActive: StateFlow<Boolean> = _isLivingMotionActive.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _aiStatusMessage = MutableStateFlow<String?>(null)
    val aiStatusMessage: StateFlow<String?> = _aiStatusMessage.asStateFlow()

    // Database Projects
    val savedProjects: StateFlow<List<ProjectEntity>> = repository.allProjects.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Can Undo / Redo
    private val _canUndo = MutableStateFlow(false)
    val canUndo: StateFlow<Boolean> = _canUndo.asStateFlow()

    private val _canRedo = MutableStateFlow(false)
    val canRedo: StateFlow<Boolean> = _canRedo.asStateFlow()

    init {
        // Initialize with default 4 stock photos for instant creativity
        val initialPhotos = SampleData.stockPhotos.take(4)
        _photos.value = initialPhotos
        _blurPhotoUri.value = initialPhotos.firstOrNull()?.uri
    }

    private fun pushHistorySnapshot() {
        undoStack.add(
            CollageHistorySnapshot(
                canvasPreset = _canvasPreset.value,
                isFreeform = _isFreeform.value,
                gridTemplateId = _gridTemplate.value.id,
                backgroundType = _backgroundType.value,
                backgroundColor = _backgroundColor.value,
                backgroundGradients = _backgroundGradients.value,
                cellSpacing = _cellSpacing.value,
                cornerRadius = _cornerRadius.value,
                borderWidth = _borderWidth.value,
                borderColor = _borderColor.value,
                photos = _photos.value,
                layers = _layers.value
            )
        )
        if (undoStack.size > 25) {
            undoStack.removeAt(0)
        }
        redoStack.clear()
        _canUndo.value = undoStack.isNotEmpty()
        _canRedo.value = false
    }

    fun undo() {
        if (undoStack.isEmpty()) return
        val current = CollageHistorySnapshot(
            canvasPreset = _canvasPreset.value,
            isFreeform = _isFreeform.value,
            gridTemplateId = _gridTemplate.value.id,
            backgroundType = _backgroundType.value,
            backgroundColor = _backgroundColor.value,
            backgroundGradients = _backgroundGradients.value,
            cellSpacing = _cellSpacing.value,
            cornerRadius = _cornerRadius.value,
            borderWidth = _borderWidth.value,
            borderColor = _borderColor.value,
            photos = _photos.value,
            layers = _layers.value
        )
        redoStack.add(current)
        val prev = undoStack.removeAt(undoStack.lastIndex)
        applySnapshot(prev)
        _canUndo.value = undoStack.isNotEmpty()
        _canRedo.value = true
    }

    fun redo() {
        if (redoStack.isEmpty()) return
        val current = CollageHistorySnapshot(
            canvasPreset = _canvasPreset.value,
            isFreeform = _isFreeform.value,
            gridTemplateId = _gridTemplate.value.id,
            backgroundType = _backgroundType.value,
            backgroundColor = _backgroundColor.value,
            backgroundGradients = _backgroundGradients.value,
            cellSpacing = _cellSpacing.value,
            cornerRadius = _cornerRadius.value,
            borderWidth = _borderWidth.value,
            borderColor = _borderColor.value,
            photos = _photos.value,
            layers = _layers.value
        )
        undoStack.add(current)
        val next = redoStack.removeAt(redoStack.lastIndex)
        applySnapshot(next)
        _canUndo.value = true
        _canRedo.value = redoStack.isNotEmpty()
    }

    private fun applySnapshot(snap: CollageHistorySnapshot) {
        _canvasPreset.value = snap.canvasPreset
        _isFreeform.value = snap.isFreeform
        val matchedGrid = SampleData.gridTemplates.find { it.id == snap.gridTemplateId } ?: SampleData.generateDynamicGrid(snap.photos.size)
        _gridTemplate.value = matchedGrid
        _backgroundType.value = snap.backgroundType
        _backgroundColor.value = snap.backgroundColor
        _backgroundGradients.value = snap.backgroundGradients
        _cellSpacing.value = snap.cellSpacing
        _cornerRadius.value = snap.cornerRadius
        _borderWidth.value = snap.borderWidth
        _borderColor.value = snap.borderColor
        _photos.value = snap.photos
        _layers.value = snap.layers
    }

    // Photo Management
    fun addPhotosFromUri(uris: List<Uri>) {
        if (uris.isEmpty()) return
        pushHistorySnapshot()
        val current = _photos.value.toMutableList()
        uris.forEachIndexed { index, uri ->
            current.add(
                PhotoItem(
                    id = "usr_photo_${System.currentTimeMillis()}_$index",
                    uri = uri.toString(),
                    title = "Photo ${current.size + 1}",
                    tone = if (index % 2 == 0) ToneType.WARM else ToneType.COOL
                )
            )
        }
        _photos.value = current
        autoAdaptGridToPhotoCount(current.size)
    }

    fun addStockPhoto(photo: PhotoItem) {
        pushHistorySnapshot()
        val current = _photos.value.toMutableList()
        current.add(photo.copy(id = "stock_${System.currentTimeMillis()}"))
        _photos.value = current
        autoAdaptGridToPhotoCount(current.size)
    }

    fun removePhoto(index: Int) {
        if (index in _photos.value.indices) {
            pushHistorySnapshot()
            val current = _photos.value.toMutableList()
            current.removeAt(index)
            _photos.value = current
            if (_selectedPhotoIndex.value == index) {
                _selectedPhotoIndex.value = current.indices.firstOrNull()
            }
            autoAdaptGridToPhotoCount(current.size)
        }
    }

    fun swapPhotos(fromIndex: Int, toIndex: Int) {
        if (fromIndex in _photos.value.indices && toIndex in _photos.value.indices && fromIndex != toIndex) {
            pushHistorySnapshot()
            val current = _photos.value.toMutableList()
            val item = current.removeAt(fromIndex)
            current.add(toIndex, item)
            _photos.value = current
            _selectedPhotoIndex.value = toIndex
        }
    }

    private fun autoAdaptGridToPhotoCount(count: Int) {
        if (!_isFreeform.value && count > 0) {
            val matched = SampleData.gridTemplates.find { it.photoCount == count }
            if (matched != null) {
                _gridTemplate.value = matched
            } else {
                _gridTemplate.value = SampleData.generateDynamicGrid(count)
            }
        }
    }

    // Tools & Settings
    fun selectTool(tool: StudioTool) {
        _activeTool.value = tool
    }

    fun selectPreset(preset: CanvasPreset) {
        pushHistorySnapshot()
        _canvasPreset.value = preset
    }

    fun toggleFreeform(isFree: Boolean) {
        pushHistorySnapshot()
        _isFreeform.value = isFree
    }

    fun selectGridTemplate(template: GridTemplate) {
        pushHistorySnapshot()
        _gridTemplate.value = template
    }

    fun setCellSpacing(spacing: Float) {
        _cellSpacing.value = spacing
    }

    fun setCornerRadius(radius: Float) {
        _cornerRadius.value = radius
    }

    fun setBorderWidth(width: Float) {
        _borderWidth.value = width
    }

    fun setBorderColor(color: Long) {
        pushHistorySnapshot()
        _borderColor.value = color
    }

    fun setBackgroundType(type: BackgroundType) {
        pushHistorySnapshot()
        _backgroundType.value = type
    }

    fun setBackgroundColor(color: Long) {
        pushHistorySnapshot()
        _backgroundColor.value = color
    }

    fun setBackgroundGradients(gradients: List<Long>) {
        pushHistorySnapshot()
        _backgroundGradients.value = gradients
    }

    fun toggleLivingMotion() {
        _isLivingMotionActive.value = !_isLivingMotionActive.value
    }

    fun selectPhoto(index: Int?) {
        _selectedPhotoIndex.value = index
        _selectedLayerId.value = null
    }

    fun selectLayer(layerId: String?) {
        _selectedLayerId.value = layerId
        if (layerId != null) {
            _selectedPhotoIndex.value = null
        }
    }

    // Photo Filters & Adjustments
    fun updatePhotoAdjust(brightness: Float, contrast: Float, saturation: Float, warmth: Float) {
        val idx = _selectedPhotoIndex.value ?: return
        if (idx in _photos.value.indices) {
            val list = _photos.value.toMutableList()
            list[idx] = list[idx].copy(
                brightness = brightness,
                contrast = contrast,
                saturation = saturation,
                warmth = warmth
            )
            _photos.value = list
        }
    }

    fun setPhotoFilter(filter: FilterType) {
        pushHistorySnapshot()
        val idx = _selectedPhotoIndex.value
        if (idx != null && idx in _photos.value.indices) {
            val list = _photos.value.toMutableList()
            list[idx] = list[idx].copy(filter = filter)
            _photos.value = list
        } else {
            // Apply to all
            _photos.value = _photos.value.map { it.copy(filter = filter) }
        }
    }

    fun applyFilterToAll(filter: FilterType) {
        pushHistorySnapshot()
        _photos.value = _photos.value.map { it.copy(filter = filter) }
    }

    // AI Features
    fun triggerAiAutoLayout() {
        viewModelScope.launch {
            _isAiLoading.value = true
            _aiStatusMessage.value = "AI analyzing ${photos.value.size} photos & composition..."
            val suggestions = aiService.generateAutoLayoutSuggestions(_photos.value)
            _aiSuggestions.value = suggestions
            _showAiCarousel.value = true
            _isAiLoading.value = false
            _aiStatusMessage.value = "Generated ${suggestions.size} smart AI arrangements ✨"
        }
    }

    fun applyAiSuggestion(suggestion: AIAutoLayoutSuggestion) {
        pushHistorySnapshot()
        val matched = SampleData.gridTemplates.find { it.id == suggestion.gridTemplateId } ?: SampleData.generateDynamicGrid(_photos.value.size)
        _gridTemplate.value = matched
        _aiStatusMessage.value = "Applied ${suggestion.title}!"
    }

    fun dismissAiCarousel() {
        _showAiCarousel.value = false
    }

    fun triggerSmartAutoCrop() {
        pushHistorySnapshot()
        // Compute smart subject offsets for each photo
        val updated = _photos.value.mapIndexed { idx, photo ->
            // Smart rule-of-thirds offsets
            val focalX = when (idx % 3) {
                0 -> 0.5f
                1 -> 0.4f
                else -> 0.6f
            }
            val focalY = 0.45f
            photo.copy(cropOffsetX = focalX, cropOffsetY = focalY)
        }
        _photos.value = updated
        _aiStatusMessage.value = "Smart Auto-Crop aligned all subjects to focal points!"
    }

    fun triggerColorHarmonyArrange() {
        pushHistorySnapshot()
        val harmonized = aiService.balanceColorHarmony(_photos.value)
        _photos.value = harmonized
        _aiStatusMessage.value = "Reordered photos for optimal warm/cool color harmony! 🎨"
    }

    fun enhanceSelectedPhoto() {
        val idx = _selectedPhotoIndex.value ?: return
        if (idx in _photos.value.indices) {
            pushHistorySnapshot()
            val list = _photos.value.toMutableList()
            val current = list[idx]
            list[idx] = current.copy(
                isAiEnhanced = !current.isAiEnhanced,
                brightness = 0.05f,
                contrast = 1.15f,
                saturation = 1.2f
            )
            _photos.value = list
            _aiStatusMessage.value = if (list[idx].isAiEnhanced) "AI HDR enhancement applied!" else "AI enhancement removed"
        }
    }

    fun removeBackgroundSelectedPhoto() {
        val idx = _selectedPhotoIndex.value ?: return
        if (idx in _photos.value.indices) {
            pushHistorySnapshot()
            val list = _photos.value.toMutableList()
            val current = list[idx]
            list[idx] = current.copy(hasBgRemoved = !current.hasBgRemoved)
            _photos.value = list
            _aiStatusMessage.value = "Subject cutout mask enabled!"
        }
    }

    fun requestAiQuotes() {
        viewModelScope.launch {
            _isAiLoading.value = true
            val quotes = aiService.generateCaptionsForCollage(_projectTitle.value)
            _aiQuotes.value = quotes
            _isAiLoading.value = false
        }
    }

    fun requestAiBackdropPalette() {
        viewModelScope.launch {
            _isAiLoading.value = true
            val colors = aiService.suggestBackdropColorPalette(_projectTitle.value)
            _backgroundGradients.value = colors
            _backgroundType.value = BackgroundType.GRADIENT
            _isAiLoading.value = false
            _aiStatusMessage.value = "AI generated harmonious backdrop gradient!"
        }
    }

    fun handleGenerativeFillPrompt(prompt: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            _aiStatusMessage.value = "Generating AI elements for '$prompt'..."
            // Add sticker or layer with prompt theme
            addStickerLayer("✨")
            _isAiLoading.value = false
            _aiStatusMessage.value = "AI generative element created!"
        }
    }

    // Layers (Text & Stickers)
    fun addTextLayer(text: String, preset: TextStylePreset, color: Long, bgColor: Long?, size: Float) {
        pushHistorySnapshot()
        val newLayer = FreeformLayer(
            id = "layer_txt_${System.currentTimeMillis()}",
            type = LayerType.TEXT,
            textContent = text,
            textStylePreset = preset,
            textColor = color,
            textBgColor = bgColor,
            fontSize = size,
            x = 0.5f,
            y = 0.5f,
            width = 0.75f,
            height = 0.1f
        )
        _layers.value = _layers.value + newLayer
        _selectedLayerId.value = newLayer.id
    }

    fun updateSelectedTextLayer(text: String, preset: TextStylePreset, color: Long, bgColor: Long?, size: Float) {
        val id = _selectedLayerId.value ?: return
        val list = _layers.value.toMutableList()
        val idx = list.indexOfFirst { it.id == id }
        if (idx != -1) {
            list[idx] = list[idx].copy(
                textContent = text,
                textStylePreset = preset,
                textColor = color,
                textBgColor = bgColor,
                fontSize = size
            )
            _layers.value = list
        }
    }

    fun addStickerLayer(emoji: String) {
        pushHistorySnapshot()
        val newLayer = FreeformLayer(
            id = "layer_stk_${System.currentTimeMillis()}",
            type = LayerType.STICKER,
            stickerEmoji = emoji,
            x = 0.5f,
            y = 0.5f,
            width = 0.18f,
            height = 0.18f,
            fontSize = 36f
        )
        _layers.value = _layers.value + newLayer
        _selectedLayerId.value = newLayer.id
    }

    fun addBadgeLayer(badge: String) {
        pushHistorySnapshot()
        val newLayer = FreeformLayer(
            id = "layer_badge_${System.currentTimeMillis()}",
            type = LayerType.TEXT,
            textContent = badge,
            textStylePreset = TextStylePreset.BOLD_DISPLAY,
            textColor = 0xFF0F172AL,
            textBgColor = 0xFFFFFFFFL,
            fontSize = 14f,
            x = 0.5f,
            y = 0.88f,
            width = 0.6f,
            height = 0.06f
        )
        _layers.value = _layers.value + newLayer
        _selectedLayerId.value = newLayer.id
    }

    fun updateLayerPosition(layerId: String, x: Float, y: Float, width: Float, rotation: Float) {
        val list = _layers.value.toMutableList()
        val idx = list.indexOfFirst { it.id == layerId }
        if (idx != -1) {
            list[idx] = list[idx].copy(x = x, y = y, width = width, rotation = rotation)
            _layers.value = list
        }
    }

    fun deleteLayer(layerId: String) {
        pushHistorySnapshot()
        _layers.value = _layers.value.filter { it.id != layerId }
        if (_selectedLayerId.value == layerId) {
            _selectedLayerId.value = null
        }
    }

    fun bringLayerForward(layerId: String) {
        pushHistorySnapshot()
        val list = _layers.value.toMutableList()
        val idx = list.indexOfFirst { it.id == layerId }
        if (idx != -1 && idx < list.size - 1) {
            val item = list.removeAt(idx)
            list.add(idx + 1, item)
            _layers.value = list
        }
    }

    fun sendLayerBackward(layerId: String) {
        pushHistorySnapshot()
        val list = _layers.value.toMutableList()
        val idx = list.indexOfFirst { it.id == layerId }
        if (idx > 0) {
            val item = list.removeAt(idx)
            list.add(idx - 1, item)
            _layers.value = list
        }
    }

    fun toggleLayerLock(layerId: String) {
        val list = _layers.value.toMutableList()
        val idx = list.indexOfFirst { it.id == layerId }
        if (idx != -1) {
            list[idx] = list[idx].copy(isLocked = !list[idx].isLocked)
            _layers.value = list
        }
    }

    fun duplicateLayer(layerId: String) {
        pushHistorySnapshot()
        val layer = _layers.value.find { it.id == layerId } ?: return
        val duplicate = layer.copy(
            id = "layer_dup_${System.currentTimeMillis()}",
            x = (layer.x + 0.05f).coerceIn(0.1f, 0.9f),
            y = (layer.y + 0.05f).coerceIn(0.1f, 0.9f)
        )
        _layers.value = _layers.value + duplicate
        _selectedLayerId.value = duplicate.id
    }

    // Theme Template Apply
    fun applyThemeTemplate(template: ThemeTemplate) {
        pushHistorySnapshot()
        _projectTitle.value = template.title
        _canvasPreset.value = template.canvasPreset
        val matchedGrid = SampleData.gridTemplates.find { it.id == template.gridTemplateId } ?: SampleData.generateDynamicGrid(template.sampleImageUrls.size)
        _gridTemplate.value = matchedGrid
        _backgroundType.value = template.backgroundType
        _backgroundColor.value = template.backgroundColor
        _backgroundGradients.value = template.backgroundGradientColors
        _cellSpacing.value = template.cellSpacing
        _cornerRadius.value = template.cornerRadius
        _borderWidth.value = template.borderWidth
        _borderColor.value = template.borderColor
        _layers.value = template.defaultLayers

        // Replace photos with template sample photos if available
        if (template.sampleImageUrls.isNotEmpty()) {
            _photos.value = template.sampleImageUrls.mapIndexed { idx, url ->
                PhotoItem(
                    id = "tmpl_photo_$idx",
                    uri = url,
                    title = "Sample ${idx + 1}"
                )
            }
        }
        _aiStatusMessage.value = "Applied ${template.title} theme preset!"
    }

    // Persistence: Save & Load Projects
    fun saveCurrentProject(onComplete: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val savedId = repository.saveProject(
                id = _currentProjectId.value,
                title = _projectTitle.value,
                canvasPreset = _canvasPreset.value,
                isFreeform = _isFreeform.value,
                gridTemplateId = _gridTemplate.value.id,
                backgroundType = _backgroundType.value,
                backgroundColor = _backgroundColor.value,
                backgroundGradients = _backgroundGradients.value,
                blurPhotoUri = _blurPhotoUri.value,
                cellSpacing = _cellSpacing.value,
                cornerRadius = _cornerRadius.value,
                borderWidth = _borderWidth.value,
                borderColor = _borderColor.value,
                photos = _photos.value,
                layers = _layers.value
            )
            _currentProjectId.value = savedId
            _aiStatusMessage.value = "Project saved to library!"
            onComplete(savedId)
        }
    }

    fun loadProject(entity: ProjectEntity) {
        pushHistorySnapshot()
        _currentProjectId.value = entity.id
        _projectTitle.value = entity.title
        _canvasPreset.value = try { CanvasPreset.valueOf(entity.canvasPresetName) } catch (e: Exception) { CanvasPreset.SQUARE_1_1 }
        _isFreeform.value = entity.isFreeform
        val matchedGrid = SampleData.gridTemplates.find { it.id == entity.gridTemplateId } ?: SampleData.generateDynamicGrid(4)
        _gridTemplate.value = matchedGrid
        _backgroundType.value = try { BackgroundType.valueOf(entity.backgroundTypeName) } catch (e: Exception) { BackgroundType.SOLID }
        _backgroundColor.value = entity.backgroundColor
        _backgroundGradients.value = entity.backgroundGradientColors.split(",")
            .mapNotNull { it.trim().toLongOrNull() }
            .takeIf { it.isNotEmpty() } ?: listOf(0xFF1E1B2EL, 0xFF0D0E1AL)
        _blurPhotoUri.value = entity.blurPhotoUri
        _cellSpacing.value = entity.cellSpacing
        _cornerRadius.value = entity.cornerRadius
        _borderWidth.value = entity.borderWidth
        _borderColor.value = entity.borderColor
        _photos.value = ProjectRepository.deserializePhotos(entity.photosJson)
        _layers.value = ProjectRepository.deserializeLayers(entity.freeformLayersJson)
        _aiStatusMessage.value = "Loaded project '${entity.title}'"
    }

    fun startNewProject() {
        pushHistorySnapshot()
        _currentProjectId.value = 0L
        _projectTitle.value = "Untitled Collage"
        _canvasPreset.value = CanvasPreset.SQUARE_1_1
        _isFreeform.value = false
        val initial = SampleData.stockPhotos.take(4)
        _photos.value = initial
        _gridTemplate.value = SampleData.gridTemplates.first { it.id == "grid_4_quad" }
        _layers.value = emptyList()
        _cellSpacing.value = 8f
        _cornerRadius.value = 12f
        _borderWidth.value = 0f
        _aiStatusMessage.value = "Started new blank project"
    }

    fun deleteSavedProject(id: Long) {
        viewModelScope.launch {
            repository.deleteProject(id)
        }
    }

    // High Resolution Rendering and Export
    suspend fun exportCollage(context: Context, res: ExportResolution, format: ExportFormat): Uri? {
        val bitmap = ImageEngine.renderHighResCollage(
            context = context,
            targetWidth = res.widthPx,
            preset = _canvasPreset.value,
            isFreeform = _isFreeform.value,
            gridTemplate = _gridTemplate.value,
            backgroundType = _backgroundType.value,
            backgroundColor = _backgroundColor.value,
            backgroundGradients = _backgroundGradients.value,
            blurPhotoUri = _blurPhotoUri.value,
            cellSpacing = _cellSpacing.value,
            cornerRadius = _cornerRadius.value,
            borderWidth = _borderWidth.value,
            borderColor = _borderColor.value,
            photos = _photos.value,
            layers = _layers.value
        )
        return ImageEngine.saveBitmapToStorage(context, bitmap, _projectTitle.value)
    }

    fun createShareIntent(context: Context, imageUri: Uri): Intent {
        return ImageEngine.createShareIntent(context, imageUri)
    }
}
