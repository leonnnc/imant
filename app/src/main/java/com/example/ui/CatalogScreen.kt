package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(viewModel: MagnetViewModel) {
    val isDark by viewModel.isDarkTheme.collectAsState()
    val isLowConn by viewModel.isLowConnectivity.collectAsState()
    val userProfile by viewModel.currentUser.collectAsState()
    val notifications by viewModel.notifications.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Todos") }

    val categories = listOf("Todos", "Cute", "Espacio", "Letras", "Subidas")

    // Filtered presets list
    val filteredPresets = viewModel.presets.filter { preset ->
        val matchesSearch = preset.name.lowercase().contains(searchQuery.lowercase()) ||
                preset.description.lowercase().contains(searchQuery.lowercase())
        val matchesCategory = selectedCategory == "Todos" || preset.category == selectedCategory
        matchesSearch && matchesCategory
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFF3B82F6), Color(0xFF10B981))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Pets,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            "Imantados",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = if (isDark) Color.White else Color(0xFF0F172A)
                        )
                    }
                },
                actions = {
                    // Quick low status banner
                    if (isLowConn) {
                        Surface(
                            color = Color(0xFFF59E0B).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFD97706).copy(alpha = 0.4f)),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SignalCellularConnectedNoInternet0Bar,
                                    contentDescription = "Baja conectividad",
                                    tint = Color(0xFFD97706),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    "Modo Eco",
                                    fontSize = 10.sp,
                                    color = Color(0xFFD97706),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Security check icon always there
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.SETTINGS) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Ajustes",
                            tint = if (isDark) Color.White else Color(0xFF475569)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) Color(0xFF0F172A) else Color.White,
                    titleContentColor = if (isDark) Color.White else Color(0xFF1E293B)
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(viewModel = viewModel, activeScreen = Screen.CATALOG)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC))
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp)
        ) {
            // Push Notification Banner if any new alarm exists
            if (notifications.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable { viewModel.clearNotifications() },
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF0EA5E9).copy(alpha = 0.15f),
                        contentColor = if (isDark) Color(0xFFE0F2FE) else Color(0xFF0369A1)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFF0EA5E9).copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Push Noti",
                            tint = Color(0xFF0EA5E9),
                            modifier = Modifier.size(24.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Notificación Push en Tiempo Real",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                notifications.first(),
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = if (isDark) Color(0xFF94A3B8) else Color(0xFF0284C7),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Generated Hero Banner image! Elevates the visual look instantly
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.BottomStart
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_custom_magnets_hero),
                    contentDescription = "Imanes de Personajes",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // High contrast aesthetic gradient overlay for text readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                            )
                        )
                )

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "LANZAMIENTO NUEVO",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        "Figuras e Imanes Premium",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        "Calidad industrial de corte láser y magnetismo N52 resistente a golpes.",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            // Interactive Search Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar personajes o colecciones...", fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = if (isDark) Color(0xFF1E293B) else Color.White,
                    unfocusedContainerColor = if (isDark) Color(0xFF1E293B) else Color.White,
                    focusedTextColor = if (isDark) Color.White else Color(0xFF1E293B),
                    unfocusedTextColor = if (isDark) Color.White else Color(0xFF1E293B),
                    focusedBorderColor = Color(0xFF3B82F6),
                    unfocusedBorderColor = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("search_presets_input"),
                singleLine = true
            )

            // Category horizontally scrollable chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = { Text(category, fontSize = 13.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color.Transparent,
                            selectedContainerColor = Color(0xFF3B82F6),
                            selectedLabelColor = Color.White,
                            labelColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1),
                            selectedBorderColor = Color(0xFF3B82F6),
                            borderWidth = 1.dp
                        ),
                        modifier = Modifier.testTag("category_chip_${category.lowercase()}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Section main label
            Text(
                text = "Colección de Figuras Disponibles",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else Color(0xFF0F172A),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // Grid Layout of products (adapted as vertical scrolling Column/Rows for perfect scroll performance)
            if (filteredPresets.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) Color(0xFF1E293B) else Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF3B82F6),
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            "Sin figuras coincidentes",
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Color(0xFF1E293B)
                        )
                        Text(
                            "Intenta cambiando los filtros o cargando una imagen propia en 'Foto Personalizada'.",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // Render grid items neatly in sets of 2
                val chunkedItems = filteredPresets.chunked(2)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    chunkedItems.forEach { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            pair.forEach { preset ->
                                Box(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    PresetCard(
                                        preset = preset,
                                        isDark = isDark,
                                        isLowConn = isLowConn,
                                        onSelect = { viewModel.selectPreset(preset) }
                                    )
                                }
                            }
                            if (pair.size < 2) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PresetCard(
    preset: MagnetPreset,
    isDark: Boolean,
    isLowConn: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("preset_card_${preset.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF1E293B) else Color.White
        ),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Mini decorative preview canvas shape
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(android.graphics.Color.parseColor(preset.accentColor)).copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Representative Icon
                    val iconVector = when (preset.vectorIconName) {
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
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.7f))
                            .border(
                                2.dp, 
                                Color(android.graphics.Color.parseColor(preset.accentColor)), 
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = iconVector,
                            contentDescription = null,
                            tint = Color(android.graphics.Color.parseColor(preset.accentColor)),
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    if (preset.defaultText.isNotBlank()) {
                        Surface(
                            color = Color.Black.copy(alpha = 0.65f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = preset.defaultText,
                                color = Color.White,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = preset.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color(0xFF1E293B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = preset.description,
                    fontSize = 10.sp,
                    color = Color(0xFF64748B),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$${preset.price}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF3B82F6)
                )

                Surface(
                    color = Color(0xFF3B82F6).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.clickable { onSelect() }
                ) {
                    Text(
                        "Diseñar",
                        color = Color(0xFF3B82F6),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RowScope.BottomNavItem(
    icon: @Composable () -> Unit,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = isSelected,
        onClick = onClick,
        icon = icon,
        label = { Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color.White,
            selectedTextColor = Color(0xFF3B82F6),
            unselectedIconColor = Color(0xFF64748B),
            unselectedTextColor = Color(0xFF64748B),
            indicatorColor = Color(0xFF3B82F6)
        )
    )
}

@Composable
fun BottomNavigationBar(viewModel: MagnetViewModel, activeScreen: Screen) {
    val isDark by viewModel.isDarkTheme.collectAsState()
    NavigationBar(
        containerColor = if (isDark) Color(0xFF0F172A) else Color.White,
        tonalElevation = 8.dp,
        windowInsets = WindowInsets.navigationBars
    ) {
        BottomNavItem(
            icon = { Icon(Icons.Default.Storefront, contentDescription = "Catálogo") },
            label = "Catálogo",
            isSelected = activeScreen == Screen.CATALOG,
            onClick = { viewModel.navigateTo(Screen.CATALOG) }
        )
        BottomNavItem(
            icon = { Icon(Icons.Default.Brush, contentDescription = "Diseño") },
            label = "Editor",
            isSelected = activeScreen == Screen.CANVAS,
            onClick = {
                // Select first preset as default if nothing selected yet
                val selected = viewModel.selectedPreset.value ?: viewModel.presets[0]
                viewModel.selectPreset(selected)
            }
        )
        BottomNavItem(
            icon = { Icon(Icons.Default.LocalShipping, contentDescription = "Envíos") },
            label = "Seguimiento",
            isSelected = activeScreen == Screen.TRACKING,
            onClick = { viewModel.navigateTo(Screen.TRACKING) }
        )
        BottomNavItem(
            icon = { Icon(Icons.Default.SupportAgent, contentDescription = "Soporte") },
            label = "Soporte",
            isSelected = activeScreen == Screen.HELP,
            onClick = { viewModel.navigateTo(Screen.HELP) }
        )
    }
}
