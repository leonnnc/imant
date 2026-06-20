package com.example.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasScreen(viewModel: MagnetViewModel) {
    val preset by viewModel.selectedPreset.collectAsState()
    val isDark by viewModel.isDarkTheme.collectAsState()
    val customText by viewModel.customText.collectAsState()
    val customColorHex by viewModel.customColor.collectAsState()
    val uploadedUri by viewModel.uploadedImageUri.collectAsState()
    val strength by viewModel.magnetStrength.collectAsState()
    val quantity by viewModel.quantity.collectAsState()

    val currentPreset = preset ?: viewModel.presets[0]

    // Screen offsets for the magnet drag & drop preview
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    // Color palette options
    val colorsPalette = listOf(
        "#FF6B6B" to "Rojo Pastel",
        "#FFA502" to "Naranja",
        "#54A0FF" to "Celeste",
        "#10AC84" to "Verde Vida",
        "#5F27CD" to "Púrpura",
        "#FFD2D2" to "Oro Rosa",
        "#3B82F6" to "Azul Royal",
        "#1D2D44" to "Azabache"
    )

    // Image Picker Launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.setUploadedImageUri(uri)
        }
    }

    // Direct mock customizable preset images for offline support
    val mockCustomImages = listOf(
        "https://images.unsplash.com/photo-1543466835-00a7907e9de1?q=80&w=260" to "Mascota Cuti",
        "https://images.unsplash.com/photo-1517841905240-472988babdf9?q=80&w=260" to "Pug Sonriente",
        "https://images.unsplash.com/photo-1534361960057-19889db9621e?q=80&w=260" to "Perrito Golden"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Laboratorio de Imanes", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // Reset offsets
                        offsetX = 0f
                        offsetY = 0f
                    }) {
                        Icon(imageVector = Icons.Default.FilterCenterFocus, contentDescription = "Centrar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) Color(0xFF0F172A) else Color.White,
                    titleContentColor = if (isDark) Color.White else Color(0xFF1E293B)
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(viewModel = viewModel, activeScreen = Screen.CANVAS)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDark) Color(0xFF0F172A) else Color(0xFFF1F5F9))
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            
            // Refrigerator Preview Viewport (Canvas surface)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(290.dp)
                    .background(Color.DarkGray)
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Brush metallic refrigerator background
                Image(
                    painter = painterResource(id = R.drawable.img_fridge_background),
                    contentDescription = "Puerta de refrigeradora",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // High contrast grid overlays & helpful tips
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.TouchApp,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                "¡Arrastra e interactúa con el imán!",
                                fontSize = 9.sp,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // MAGNET PREVIEW LAYER: Placed floatingly inside draggable layout
                Box(
                    modifier = Modifier
                        .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                offsetX += dragAmount.x
                                offsetY += dragAmount.y
                            }
                        }
                        .size(175.dp) // Large design magnet scale
                        .graphicsLayer()
                ) {
                    // Physical magnet visual cutout with drop shadow and borders
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .testTag("magnet_preview_canvas"),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                        border = BorderStroke(
                            4.dp, 
                            Color(android.graphics.Color.parseColor(currentPreset.accentColor))
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color.White,
                                            Color(0xFFE2E8F0)
                                        )
                                    )
                                )
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Dynamic Core content:preset illustration or photo upload
                                if (uploadedUri != null) {
                                    AsyncImage(
                                        model = uploadedUri,
                                        contentDescription = "Custom subida",
                                        modifier = Modifier
                                            .size(90.dp)
                                            .clip(RoundedCornerShape(14.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    // High contrast preset vector layout
                                    val iconVector = when (currentPreset.vectorIconName) {
                                        "pets" -> Icons.Default.Pets
                                        "favorite" -> Icons.Default.Favorite
                                        "rocket_launch" -> Icons.Default.RocketLaunch
                                        "eco" -> Icons.Default.Eco
                                        "videogame_asset" -> Icons.Default.VideogameAsset
                                        "format_quote" -> Icons.Default.FormatQuote
                                        "add_a_photo" -> Icons.Default.AddAPhoto
                                        else -> Icons.Default.Star
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(75.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Color(android.graphics.Color.parseColor(currentPreset.accentColor))
                                                    .copy(alpha = 0.15f)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = iconVector,
                                            contentDescription = null,
                                            tint = Color(android.graphics.Color.parseColor(currentPreset.accentColor)),
                                            modifier = Modifier.size(46.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Real-time customization Text bubble
                                if (customText.isNotBlank()) {
                                    Surface(
                                        color = Color(android.graphics.Color.parseColor(customColorHex)),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, Color.White),
                                        modifier = Modifier.shadow(4.dp, RoundedCornerShape(8.dp))
                                    ) {
                                        Text(
                                            text = customText,
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }

                            // Physically realistic neodymium "N52 Magnet Pin" overlay on bottom corner
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.BottomEnd
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                colors = listOf(Color(0xFFE2E8F0), Color(0xFF64748B))
                                            )
                                        )
                                        .border(1.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(Color.White)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Customizer Tools View
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header details
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentPreset.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isDark) Color.White else Color(0xFF0F172A)
                        )
                        Text(
                            text = "$${viewModel.getUnitPrice()} c/u",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3B82F6)
                        )
                    }
                    Text(
                        text = currentPreset.description,
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                }

                HorizontalDivider(color = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0))

                // Custom Text Input field
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "1. Texto Personalizado Impreso",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color(0xFF475569)
                    )
                    OutlinedTextField(
                        value = customText,
                        onValueChange = { viewModel.setCustomText(it) },
                        placeholder = { Text("Escribe una frase, fecha o nombre...", fontSize = 13.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = if (isDark) Color.White else Color(0xFF1E293B),
                            unfocusedTextColor = if (isDark) Color.White else Color(0xFF1E293B),
                            focusedBorderColor = Color(0xFF3B82F6),
                            unfocusedBorderColor = if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("custom_text_overlay_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Text Theme/Color picker
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "2. Color del Texto / Borde",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color(0xFF475569)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        colorsPalette.forEach { (hex, name) ->
                            val isSelected = customColorHex.uppercase() == hex.uppercase()
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(android.graphics.Color.parseColor(hex)))
                                    .border(
                                        width = if (isSelected) 3.dp else 0.dp,
                                        color = if (isDark) Color.White else Color(0xFF0F172A),
                                        shape = CircleShape
                                    )
                                    .clickable { viewModel.setCustomColor(hex) }
                                    .testTag("color_bubble_$hex")
                            )
                        }
                    }
                }

                // Upload custom photo section
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "3. Subir Imagen/Foto Personalizada",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color(0xFF475569)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                imagePickerLauncher.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            },
                            modifier = Modifier
                                .weight(1.5f)
                                .height(46.dp)
                                .testTag("file_picker_trigger_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF10AC84),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sube Galería", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        if (uploadedUri != null) {
                            OutlinedButton(
                                onClick = { viewModel.setUploadedImageUri(null) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFFEF4444)
                                ),
                                border = BorderStroke(1.dp, Color(0xFFEF4444))
                            ) {
                                Text("Quitar Foto", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Predefined mock custom photos to support offline fallback selection
                    Text(
                        "O selecciona un demo de prueba rápido:",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        mockCustomImages.forEach { (url, label) ->
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setUploadedImageUri(Uri.parse(url)) },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        label, 
                                        fontSize = 11.sp, 
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(4.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                // Magnet Power option
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "4. Tipo de Soporte Magnético",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color(0xFF475569)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf("Estándar", "Neodimio Pro").forEach { type ->
                            val isSelected = strength == type
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setMagnetStrength(type) }
                                    .testTag("strength_option_$type"),
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) Color(0xFF3B82F6).copy(alpha = 0.15f) else Color.Transparent,
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) Color(0xFF3B82F6) else (if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1))
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            type,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isDark) Color.White else Color(0xFF1E293B)
                                        )
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = Color(0xFF3B82F6),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = if (type == "Estándar") "Imán flexible para refri" else "N52 Extra Fuerte (+$2.50)",
                                        fontSize = 10.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }
                    }
                }

                // Quantity counter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Cantidad de Imanes",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isDark) Color.White else Color(0xFF475569)
                        )
                        Text(
                            "Descuento automático de 10% de 5 a más unidades.",
                            fontSize = 10.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { viewModel.decrementQuantity() },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0))
                                .testTag("quantity_decrement")
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Menos")
                        }

                        Text(
                            text = quantity.toString(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isDark) Color.White else Color(0xFF0F172A),
                            modifier = Modifier
                                .widthIn(min = 24.dp)
                                .testTag("quantity_label"),
                            textAlign = TextAlign.Center
                        )

                        IconButton(
                            onClick = { viewModel.incrementQuantity() },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0))
                                .testTag("quantity_increment")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Más")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Bottom summary checkout banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) Color(0xFF1E293B) else Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            val finalUnit = viewModel.getUnitPrice()
                            val preTotal = finalUnit * quantity
                            val finalTotal = viewModel.getTotalPrice()

                            Text(
                                "Total Estimado",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                "$${String.format("%.2f", finalTotal)}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF10AC84)
                            )
                        }

                        Button(
                            onClick = { viewModel.navigateTo(Screen.CHECKOUT) },
                            modifier = Modifier
                                .height(50.dp)
                                .testTag("checkout_navigate_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF3B82F6),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Confirmar Compra", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}
