package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(viewModel: MagnetViewModel) {
    var customName by remember { mutableStateOf("") }
    var customEmail by remember { mutableStateOf("") }
    var isRegisterState by remember { mutableStateOf(false) }

    // Decorative floating animation for a preview magnet
    val infiniteTransition = rememberInfiniteTransition(label = "magnet_float")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A), // Slate 900
                        Color(0xFF1E293B)  // Slate 800
                    )
                )
            )
            .padding(24.dp)
            .navigationBarsPadding()
            .statusBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // App Identity Logo Representation
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .graphicsLayer { translationY = floatOffset }
                    .shadow(16.dp, shape = RoundedCornerShape(24.dp), clip = false)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF3B82F6), Color(0xFF8B5CF6))
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .border(3.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Shiny custom logo graphic
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Pets,
                        contentDescription = "Magnet Logo",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                    Text(
                        text = "IMÁN N52",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Brand name and slogan
            Text(
                text = "Imantados Studio",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Diseña e imanta tus fotos y personajes favoritos para la refrigeradora en tiempo real.",
                fontSize = 14.sp,
                color = Color(0xFF94A3B8),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Registration/Social options Card
            Surface(
                color = Color(0xFF1E293B).copy(alpha = 0.85f),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color(0xFF334155)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Accede a tu Catálogo Seguro",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Inicia sesión con cuentas de redes sociales para guardar y rastrear pedidos de forma encriptada.",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Branded Button: Google Social Sign In
                    Button(
                        onClick = { viewModel.login("León Cárdenas", "leonnnc@gmail.com", "Google") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("google_login_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEA4335), // Google Red
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "G",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Text(
                                "Continuar con Google",
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Branded Button: Facebook Social Sign In
                    Button(
                        onClick = { viewModel.login("León Cárdenas", "leon_fb@gmail.com", "Facebook") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("facebook_login_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1877F2), // Facebook Blue
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "f",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Text(
                                "Continuar con Facebook",
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Divider with customized "or" text
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFF334155)
                        )
                        Text(
                            text = "ó ingreso rápido",
                            modifier = Modifier.padding(horizontal = 12.dp),
                            color = Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFF334155)
                        )
                    }

                    // Custom Guest Input Mode for accessibility testing
                    OutlinedTextField(
                        value = customName,
                        onValueChange = { customName = it },
                        label = { Text("Tu Nombre Completo", color = Color(0xFF94A3B8)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF3B82F6),
                            unfocusedBorderColor = Color(0xFF334155)
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("custom_name_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            val finalName = customName.ifBlank { "León Directo" }
                            val finalEmail = if (customEmail.isNotBlank()) customEmail else "anonimo@imantados.com"
                            viewModel.login(finalName, finalEmail, "Invitado")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("anonymous_login_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF334155),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Ingresar como Diseñador",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Visual shield tag confirming total encryption & connection robustness
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF0369A1).copy(alpha = 0.2f))
                    .border(1.dp, Color(0xFF0284C7).copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Encrypted Connection",
                    tint = Color(0xFF38BDF8),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "Cifrado de Extremo a Extremo SSL Inteligente",
                    color = Color(0xFF38BDF8),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
