package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MagnetViewModel) {
    val isDark by viewModel.isDarkTheme.collectAsState()
    val isLowConn by viewModel.isLowConnectivity.collectAsState()
    val userProfile by viewModel.currentUser.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes del Sistema", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Social Account Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) Color(0xFF1E293B) else Color.White
                ),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile Avatar (Google standard or letter)
                    if (userProfile?.avatarUrl?.startsWith("http") == true) {
                        AsyncImage(
                            model = userProfile?.avatarUrl,
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFF3B82F6), Color(0xFF7C3AED))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userProfile?.name?.take(1)?.uppercase() ?: "U",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = userProfile?.name ?: "Diseñador de Imanes",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = if (isDark) Color.White else Color(0xFF0F172A)
                        )
                        Text(
                            text = userProfile?.email ?: "anonimo@imantados.com",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            color = Color(0xFF3B82F6).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudQueue,
                                    contentDescription = null,
                                    tint = Color(0xFF3B82F6),
                                    modifier = Modifier.size(10.dp)
                                )
                                Text(
                                    "Cuenta ${userProfile?.loggedInVia ?: "Google"}",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF3B82F6)
                                )
                            }
                        }
                    }
                }
            }

            // Theme Options Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) Color(0xFF1E293B) else Color.White
                ),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        "Configuración Visual",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (isDark) Color.White else Color(0xFF0F172A)
                    )

                    // Dark mode toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = null,
                                tint = if (isDark) Color(0xFFF1C40F) else Color(0xFFF39C12)
                            )
                            Column {
                                Text(
                                    "Modo Oscuro Configurable",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isDark) Color.White else Color(0xFF1E293B)
                                )
                                Text(
                                    "Reduce brillo y fatiga ocular.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                        Switch(
                            checked = isDark,
                            onCheckedChange = { viewModel.toggleDarkMode() },
                            modifier = Modifier.testTag("dark_mode_switch")
                        )
                    }

                    HorizontalDivider(color = if (isDark) Color(0xFF334155) else Color(0xFFF1F5F9))

                    // Low-connectivity simulate toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.NetworkCheck,
                                contentDescription = null,
                                tint = Color(0xFF0EA5E9)
                            )
                            Column {
                                Text(
                                    "Modo Baja Conectividad",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isDark) Color.White else Color(0xFF1E293B)
                                )
                                Text(
                                    "Modo eficiente: Comprime descargas y optimiza respuestas.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                        Switch(
                            checked = isLowConn,
                            onCheckedChange = { viewModel.toggleLowConnectivity() },
                            modifier = Modifier.testTag("low_conn_switch")
                        )
                    }

                    // Dynamic efficient explanation banner
                    AnimatedVisibility(visible = isLowConn) {
                        Surface(
                            color = Color(0xFFF59E0B).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.25f))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CloudOff, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(16.dp))
                                Text(
                                    "Ahorro activado: La UI almacena plantillas en el caché local de SQLite Room Database de forma ultra rápida.",
                                    fontSize = 10.sp,
                                    color = Color(0xFFD97706)
                                )
                            }
                        }
                    }
                }
            }

            // Security details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) Color(0xFF1E293B) else Color.White
                ),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Seguridad Robustecida",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (isDark) Color.White else Color(0xFF0F172A)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Https, contentDescription = null, tint = Color(0xFF10AC84))
                        Column {
                            Text(
                                "Cifrado de Extremo a Extremo",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = if (isDark) Color.White else Color(0xFF1E293B)
                            )
                            Text(
                                "Direcciones, tarjetas y chats de soporte cifrados bajo protocolo AES-DSA-256.",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                              )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.PrivacyTip, contentDescription = null, tint = Color(0xFF3B82F6))
                        Column {
                            Text(
                                "Cumplimiento del Reglamento de Datos",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = if (isDark) Color.White else Color(0xFF1E293B)
                            )
                            Text(
                                "Tus fotos subidas permanecen en tu almacenamiento local aislado.",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Logout action Button
            Button(
                onClick = { viewModel.logout() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("logout_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEF4444),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Cerrar Sesión de Red Social", fontWeight = FontWeight.Bold)
            }
        }
    }
}
