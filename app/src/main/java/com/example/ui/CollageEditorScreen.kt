package com.example.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.data.model.*
import com.example.data.sample.SampleData
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.tools.*
import com.example.ui.viewmodel.CollageViewModel
import com.example.ui.viewmodel.StudioTool
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollageEditorScreen(
    viewModel: CollageViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // State collections
    val projectTitle by viewModel.projectTitle.collectAsStateWithLifecycle()
    val canvasPreset by viewModel.canvasPreset.collectAsStateWithLifecycle()
    val isFreeform by viewModel.isFreeform.collectAsStateWithLifecycle()
    val gridTemplate by viewModel.gridTemplate.collectAsStateWithLifecycle()
    val backgroundType by viewModel.backgroundType.collectAsStateWithLifecycle()
    val backgroundColor by viewModel.backgroundColor.collectAsStateWithLifecycle()
    val backgroundGradients by viewModel.backgroundGradients.collectAsStateWithLifecycle()
    val blurPhotoUri by viewModel.blurPhotoUri.collectAsStateWithLifecycle()
    val cellSpacing by viewModel.cellSpacing.collectAsStateWithLifecycle()
    val cornerRadius by viewModel.cornerRadius.collectAsStateWithLifecycle()
    val borderWidth by viewModel.borderWidth.collectAsStateWithLifecycle()
    val borderColor by viewModel.borderColor.collectAsStateWithLifecycle()
    val photos by viewModel.photos.collectAsStateWithLifecycle()
    val layers by viewModel.layers.collectAsStateWithLifecycle()
    val selectedPhotoIndex by viewModel.selectedPhotoIndex.collectAsStateWithLifecycle()
    val selectedLayerId by viewModel.selectedLayerId.collectAsStateWithLifecycle()
    val activeTool by viewModel.activeTool.collectAsStateWithLifecycle()
    val aiSuggestions by viewModel.aiSuggestions.collectAsStateWithLifecycle()
    val showAiCarousel by viewModel.showAiCarousel.collectAsStateWithLifecycle()
    val aiQuotes by viewModel.aiQuotes.collectAsStateWithLifecycle()
    val isLivingMotionActive by viewModel.isLivingMotionActive.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
    val aiStatusMessage by viewModel.aiStatusMessage.collectAsStateWithLifecycle()
    val savedProjects by viewModel.savedProjects.collectAsStateWithLifecycle()
    val canUndo by viewModel.canUndo.collectAsStateWithLifecycle()
    val canRedo by viewModel.canRedo.collectAsStateWithLifecycle()

    // Dialog & Sheet States
    var showExportDialog by remember { mutableStateOf(false) }
    var showProjectsSheet by remember { mutableStateOf(false) }
    var showStockPhotosSheet by remember { mutableStateOf(false) }
    var isExporting by remember { mutableStateOf(false) }
    var exportResultMessage by remember { mutableStateOf<String?>(null) }

    // Multi-photo picker using Android Photo Picker (zero broad storage permission!)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 20)
    ) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.addPhotosFromUri(uris)
            Toast.makeText(context, "Added ${uris.size} photos!", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        containerColor = DarkCanvasBg,
        topBar = {
            // Sleek Interface Studio Header
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                shape = RoundedCornerShape(24.dp),
                color = DarkSurfaceBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Logo & My Projects (PhotoMagic AI Powered badge)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Brush.linearGradient(listOf(Color(0xFFD0BCFF), Color(0xFF381E72)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "M",
                                color = Color(0xFF1D1B20),
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp
                            )
                        }

                        Column {
                            Text(
                                text = "PhotoMagic",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                lineHeight = 16.sp
                            )
                            Text(
                                text = "AI POWERED",
                                color = AccentPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.2.sp
                            )
                        }
                    }

                    // Right: Actions (Projects, Motion, Undo, Redo, Export)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Projects Library
                        GlassIconButton(
                            icon = Icons.Default.FolderSpecial,
                            contentDescription = "My Projects",
                            badgeText = if (savedProjects.isNotEmpty()) "${savedProjects.size}" else null,
                            onClick = { showProjectsSheet = true },
                            size = 36.dp
                        )

                        // Living Motion Toggle
                        GlassIconButton(
                            icon = Icons.Default.MotionPhotosOn,
                            contentDescription = "Living Motion",
                            isSelected = isLivingMotionActive,
                            onClick = { viewModel.toggleLivingMotion() },
                            size = 36.dp
                        )

                        // Undo
                        GlassIconButton(
                            icon = Icons.Default.Undo,
                            contentDescription = "Undo",
                            onClick = { viewModel.undo() },
                            isSelected = false,
                            size = 36.dp
                        )

                        // Redo
                        GlassIconButton(
                            icon = Icons.Default.Redo,
                            contentDescription = "Redo",
                            onClick = { viewModel.redo() },
                            isSelected = false,
                            size = 36.dp
                        )

                        // Sleek Export Button
                        Button(
                            onClick = { showExportDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentPrimary,
                                contentColor = AccentOnPrimary
                            ),
                            shape = CircleShape,
                            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Text(
                                text = "Export",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                // Active Tool Configuration Drawer / Panel
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = DarkSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                    shadowElevation = 8.dp
                ) {
                    AnimatedContent(
                        targetState = activeTool,
                        label = "toolPanelTransition"
                    ) { tool ->
                        when (tool) {
                            StudioTool.LAYOUTS -> {
                                LayoutsToolPanel(
                                    selectedPreset = canvasPreset,
                                    isFreeform = isFreeform,
                                    activeGridId = gridTemplate.id,
                                    photoCount = photos.size,
                                    onSelectPreset = { viewModel.selectPreset(it) },
                                    onToggleFreeform = { viewModel.toggleFreeform(it) },
                                    onSelectGrid = { viewModel.selectGridTemplate(it) }
                                )
                            }
                            StudioTool.AI_MAGIC -> {
                                AiMagicToolPanel(
                                    onTriggerAutoLayout = { viewModel.triggerAiAutoLayout() },
                                    onSmartAutoCrop = { viewModel.triggerSmartAutoCrop() },
                                    onColorHarmonyArrange = { viewModel.triggerColorHarmonyArrange() },
                                    onEnhanceSelectedPhoto = { viewModel.enhanceSelectedPhoto() },
                                    onRemoveBackground = { viewModel.removeBackgroundSelectedPhoto() },
                                    onGenerativeFillPrompt = { viewModel.handleGenerativeFillPrompt(it) },
                                    isPhotoSelected = selectedPhotoIndex != null,
                                    isGenerating = isAiLoading,
                                    aiStatusMessage = aiStatusMessage
                                )
                            }
                            StudioTool.ADJUST -> {
                                val selectedPhoto = selectedPhotoIndex?.let { photos.getOrNull(it) }
                                AdjustToolPanel(
                                    cellSpacing = cellSpacing,
                                    cornerRadius = cornerRadius,
                                    borderWidth = borderWidth,
                                    borderColor = borderColor,
                                    selectedPhoto = selectedPhoto,
                                    onUpdateSpacing = { viewModel.setCellSpacing(it) },
                                    onUpdateRadius = { viewModel.setCornerRadius(it) },
                                    onUpdateBorderWidth = { viewModel.setBorderWidth(it) },
                                    onUpdateBorderColor = { viewModel.setBorderColor(it) },
                                    onUpdatePhotoAdjust = { b, c, s, w -> viewModel.updatePhotoAdjust(b, c, s, w) }
                                )
                            }
                            StudioTool.FILTERS -> {
                                val currentFilter = selectedPhotoIndex?.let { photos.getOrNull(it)?.filter } ?: FilterType.NONE
                                FiltersToolPanel(
                                    activeFilter = currentFilter,
                                    onSelectFilter = { viewModel.setPhotoFilter(it) },
                                    onApplyToAll = { viewModel.applyFilterToAll(currentFilter) }
                                )
                            }
                            StudioTool.TEXT -> {
                                val selLayer = layers.find { it.id == selectedLayerId }
                                TextToolPanel(
                                    selectedLayer = selLayer,
                                    aiQuotes = aiQuotes,
                                    onAddTextLayer = { t, p, c, bg, s -> viewModel.addTextLayer(t, p, c, bg, s) },
                                    onUpdateSelectedTextLayer = { t, p, c, bg, s -> viewModel.updateSelectedTextLayer(t, p, c, bg, s) },
                                    onRequestAiQuotes = { viewModel.requestAiQuotes() }
                                )
                            }
                            StudioTool.STICKERS -> {
                                StickersToolPanel(
                                    onAddStickerEmoji = { viewModel.addStickerLayer(it) },
                                    onAddBadgeLabel = { viewModel.addBadgeLayer(it) }
                                )
                            }
                            StudioTool.BACKDROP -> {
                                BackdropToolPanel(
                                    selectedType = backgroundType,
                                    backgroundColor = backgroundColor,
                                    backgroundGradients = backgroundGradients,
                                    onSelectType = { viewModel.setBackgroundType(it) },
                                    onSelectColor = { viewModel.setBackgroundColor(it) },
                                    onSelectGradient = { viewModel.setBackgroundGradients(it) },
                                    onRequestAiBackdrop = { viewModel.requestAiBackdropPalette() }
                                )
                            }
                            StudioTool.LAYERS -> {
                                LayersToolPanel(
                                    layers = layers,
                                    selectedLayerId = selectedLayerId,
                                    onSelectLayer = { viewModel.selectLayer(it) },
                                    onBringForward = { viewModel.bringLayerForward(it) },
                                    onSendBackward = { viewModel.sendLayerBackward(it) },
                                    onToggleLock = { viewModel.toggleLayerLock(it) },
                                    onDuplicateLayer = { viewModel.duplicateLayer(it) },
                                    onDeleteLayer = { viewModel.deleteLayer(it) }
                                )
                            }
                            StudioTool.TEMPLATES -> {
                                TemplatesToolPanel(
                                    onApplyTemplate = { viewModel.applyThemeTemplate(it) }
                                )
                            }
                        }
                    }
                }

                // Sleek Studio Tool Navigation Bar
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = DarkSurfaceBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                ) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(StudioTool.values().toList(), key = { it.name }) { tool ->
                            val isSelected = activeTool == tool
                            GlassPillChip(
                                text = tool.label,
                                emoji = tool.iconEmoji,
                                isSelected = isSelected,
                                onClick = { viewModel.selectTool(tool) }
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Photo Import & Quick Thumbnail Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Photo Picker & Stock photos trigger
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "+ Import", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { showStockPhotosSheet = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x22FFFFFF)),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(text = "Stock 🌄", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }

                    // Save Project quick button
                    Button(
                        onClick = {
                            viewModel.saveCurrentProject {
                                Toast.makeText(context, "Collage saved to library!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x22FFFFFF)),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(14.dp), tint = TextSecondary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Save", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    }
                }

                // Thumbnail chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    itemsIndexed(photos, key = { _, p -> p.id }) { index, photo ->
                        val isSelected = selectedPhotoIndex == index
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) AccentPrimary else Color(0x33FFFFFF),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { viewModel.selectPhoto(index) }
                        ) {
                            AsyncImage(
                                model = photo.uri,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            // Quick Mode Toggle Bar (AI Auto / Grid vs Freeform)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = DarkSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    Row(
                        modifier = Modifier.padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Button(
                            onClick = { viewModel.toggleFreeform(false) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!isFreeform) DarkCardBorder else Color.Transparent,
                                contentColor = if (!isFreeform) TextPrimary else TextSecondary
                            ),
                            shape = CircleShape,
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                            Text(text = "🪄 Auto Grid", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.toggleFreeform(true) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isFreeform) DarkCardBorder else Color.Transparent,
                                contentColor = if (isFreeform) TextPrimary else TextSecondary
                            ),
                            shape = CircleShape,
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                            Text(text = "🖼️ Freeform", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Main Interactive Canvas Stage Container (Sleek 32dp rounded card)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(DarkSurfaceVariant)
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(32.dp)),
                contentAlignment = Alignment.Center
            ) {
                CanvasRenderer(
                    preset = canvasPreset,
                    isFreeform = isFreeform,
                    gridTemplate = gridTemplate,
                    backgroundType = backgroundType,
                    backgroundColor = backgroundColor,
                    backgroundGradients = backgroundGradients,
                    blurPhotoUri = blurPhotoUri,
                    cellSpacing = cellSpacing,
                    cornerRadius = cornerRadius,
                    borderWidth = borderWidth,
                    borderColor = borderColor,
                    photos = photos,
                    layers = layers,
                    selectedPhotoIndex = selectedPhotoIndex,
                    selectedLayerId = selectedLayerId,
                    isLivingMotionActive = isLivingMotionActive,
                    onPhotoSelected = { viewModel.selectPhoto(it) },
                    onSwapPhotos = { from, to -> viewModel.swapPhotos(from, to) },
                    onLayerSelected = { viewModel.selectLayer(it) },
                    onUpdateLayerPosition = { id, x, y, w, rot -> viewModel.updateLayerPosition(id, x, y, w, rot) },
                    onDeleteLayer = { viewModel.deleteLayer(it) }
                )

                // Floating AI Auto-Layout Recommendations Overlay
                if (showAiCarousel && aiSuggestions.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp, start = 12.dp, end = 12.dp)
                    ) {
                        AIAutoLayoutCarousel(
                            suggestions = aiSuggestions,
                            activeGridId = gridTemplate.id,
                            onSelectSuggestion = { viewModel.applyAiSuggestion(it) },
                            onDismiss = { viewModel.dismissAiCarousel() }
                        )
                    }
                }
            }
        }
    }

    // Export Modal Dialog
    if (showExportDialog) {
        ExportDialog(
            onDismiss = {
                showExportDialog = false
                exportResultMessage = null
            },
            isExporting = isExporting,
            savedUriMessage = exportResultMessage,
            onExportAndSave = { res, format ->
                coroutineScope.launch {
                    isExporting = true
                    val uri = viewModel.exportCollage(context, res, format)
                    isExporting = false
                    if (uri != null) {
                        exportResultMessage = "Saved in Ultra Resolution to Pictures/CollageCraft!"
                        Toast.makeText(context, "Collage saved to gallery!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Export finished!", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onDirectShare = { res, format, platform ->
                coroutineScope.launch {
                    isExporting = true
                    val uri = viewModel.exportCollage(context, res, format)
                    isExporting = false
                    if (uri != null) {
                        val shareIntent = viewModel.createShareIntent(context, uri)
                        context.startActivity(Intent.createChooser(shareIntent, "Share your collage via $platform"))
                    } else {
                        Toast.makeText(context, "Unable to generate share image", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }

    // Projects Library Bottom Sheet
    if (showProjectsSheet) {
        ProjectsSheet(
            projects = savedProjects,
            onSelectProject = { viewModel.loadProject(it) },
            onDeleteProject = { viewModel.deleteSavedProject(it) },
            onNewProject = { viewModel.startNewProject() },
            onDismiss = { showProjectsSheet = false }
        )
    }

    // Stock Photos Sheet
    if (showStockPhotosSheet) {
        ModalBottomSheet(
            onDismissRequest = { showStockPhotosSheet = false },
            containerColor = DarkSurfaceBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Add Curated Stock Photos",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(SampleData.stockPhotos, key = { it.id }) { photo ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width(120.dp)
                                .clickable {
                                    viewModel.addStockPhoto(photo)
                                    showStockPhotosSheet = false
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            ) {
                                AsyncImage(
                                    model = photo.uri,
                                    contentDescription = photo.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = photo.title,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
