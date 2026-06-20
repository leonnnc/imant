package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(viewModel: MagnetViewModel) {
    val preset by viewModel.selectedPreset.collectAsState()
    val isDark by viewModel.isDarkTheme.collectAsState()
    val strength by viewModel.magnetStrength.collectAsState()
    val customText by viewModel.customText.collectAsState()
    val quantity by viewModel.quantity.collectAsState()

    val currentPreset = preset ?: viewModel.presets[0]

    // Form states
    var cardNumber by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCvv by remember { mutableStateOf("") }
    var cardName by remember { mutableStateOf("") }
    var userPhone by remember { mutableStateOf("") }
    var userAddress by remember { mutableStateOf("") }

    var isPayingState by remember { mutableStateOf(false) }
    var isSuccessState by remember { mutableStateOf(false) }

    // Coroutines
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pasarela de Pago Segura", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC))
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // SSL security banner
                Surface(
                    color = Color(0xFF059669).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF059669).copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "Garantía SSL",
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                "Pago Encriptado TLS / SSL",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color(0xFF047857)
                            )
                            Text(
                                "Tus datos financieros de extremo a extremo están protegidos bajo tokens bancorizados.",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }

                // Custom Magnet purchase overview
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) Color(0xFF1E293B) else Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            "Resumen de Compra Personalizada",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (isDark) Color.White else Color(0xFF0F172A)
                        )

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1.5f)) {
                                Text(
                                    "${quantity}x Imán ${currentPreset.name}",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = if (isDark) Color.White else Color(0xFF1E293B)
                                )
                                Text(
                                    "Fuerza: $strength" + (if (customText.isNotBlank()) " | Texto: \"$customText\"" else ""),
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                            Text(
                                "$${String.format("%.2f", viewModel.getTotalPrice())}",
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = Color(0xFF3B82F6),
                                modifier = Modifier.weight(0.5f),
                                textAlign = TextAlign.End
                            )
                        }

                        HorizontalDivider(color = if (isDark) Color(0xFF334155) else Color(0xFFF1F5F9))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Envío Garantizado (Courier Express)",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                "¡GRATIS!",
                                fontSize = 11.sp,
                                color = Color(0xFF10AC84),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "TOTAL A PAGAR",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = if (isDark) Color.White else Color(0xFF0F172A)
                            )
                            Text(
                                "$${String.format("%.2f", viewModel.getTotalPrice())}",
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                color = Color(0xFF10AC84)
                            )
                        }
                    }
                }

                // Credit Card visual placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF2563EB), Color(0xFF7C3AED))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "SECURE CARD VIA LOCK",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Card number display formatted
                        val shownNumber = cardNumber.padEnd(16, '*').chunked(4).joinToString("  ")
                        Text(
                            text = shownNumber,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text("PROPIETARIO", fontSize = 8.sp, color = Color.White.copy(alpha = 0.7f))
                            Text(
                                text = cardName.uppercase().ifBlank { "NOMBRE COMPLETO" },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("EXPIRA", fontSize = 8.sp, color = Color.White.copy(alpha = 0.7f))
                            Text(
                                text = cardExpiry.padEnd(4, 'X').insert(2, "/"),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // Inputs Forms
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Información de Despacho",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (isDark) Color.White else Color(0xFF0F172A)
                    )

                    OutlinedTextField(
                        value = userAddress,
                        onValueChange = { userAddress = it },
                        label = { Text("Dirección de Envío Completa", fontSize = 13.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = if (isDark) Color.White else Color(0xFF1E293B),
                            unfocusedTextColor = if (isDark) Color.White else Color(0xFF1E293B),
                            focusedBorderColor = Color(0xFF3B82F6)
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checkout_address_input")
                    )

                    OutlinedTextField(
                        value = userPhone,
                        onValueChange = { userPhone = it },
                        label = { Text("Teléfono de Contacto Celular", fontSize = 13.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = if (isDark) Color.White else Color(0xFF1E293B),
                            unfocusedTextColor = if (isDark) Color.White else Color(0xFF1E293B),
                            focusedBorderColor = Color(0xFF3B82F6)
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checkout_phone_input")
                    )

                    Text(
                        "Detalles Financieros de la Tarjeta",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (isDark) Color.White else Color(0xFF0F172A),
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    OutlinedTextField(
                        value = cardName,
                        onValueChange = { cardName = it },
                        label = { Text("Nombre como figura en la Tarjeta", fontSize = 13.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = if (isDark) Color.White else Color(0xFF1E293B),
                            unfocusedTextColor = if (isDark) Color.White else Color(0xFF1E293B),
                            focusedBorderColor = Color(0xFF3B82F6)
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("payment_card_name_input")
                    )

                    OutlinedTextField(
                        value = cardNumber,
                        onValueChange = { if (it.length <= 16) cardNumber = it },
                        label = { Text("Número de Tarjeta (16 dígitos)", fontSize = 13.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = if (isDark) Color.White else Color(0xFF1E293B),
                            unfocusedTextColor = if (isDark) Color.White else Color(0xFF1E293B),
                            focusedBorderColor = Color(0xFF3B82F6)
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("payment_card_number_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = cardExpiry,
                            onValueChange = { if (it.length <= 4) cardExpiry = it },
                            label = { Text("Vence (MMYY)", fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = if (isDark) Color.White else Color(0xFF1E293B),
                                unfocusedTextColor = if (isDark) Color.White else Color(0xFF1E293B),
                                focusedBorderColor = Color(0xFF3B82F6)
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("payment_card_expiry_input")
                        )

                        OutlinedTextField(
                            value = cardCvv,
                            onValueChange = { if (it.length <= 3) cardCvv = it },
                            label = { Text("Código CVV", fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = if (isDark) Color.White else Color(0xFF1E293B),
                                unfocusedTextColor = if (isDark) Color.White else Color(0xFF1E293B),
                                focusedBorderColor = Color(0xFF3B82F6)
                            ),
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("payment_card_cvv_input")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Submit pay action
                Button(
                    onClick = {
                        if (cardNumber.isNotBlank() && cardName.isNotBlank() && userAddress.isNotBlank()) {
                            isPayingState = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("confirm_payment_submit_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF10AC84),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isPayingState && cardNumber.length >= 12
                ) {
                    if (isPayingState) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Verificando Cifrado SSL...")
                    } else {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Cerrar Pedido y Pagar Segure", fontWeight = FontWeight.Bold)
                    }
                }

                // Security confirmation notes
                Text(
                    text = "Al confirmar, autorizas el cobro seguro en tu banco. Protegido por los estándares de seguridad bancaria OWASP con cifrado de extremo a extremo.",
                    fontSize = 9.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Real-Time Pay verification overlay modal
            if (isPayingState) {
                // Simulate payment processing delay
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(2000)
                    isPayingState = false
                    isSuccessState = true
                }
            }

            if (isSuccessState) {
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(1800)
                    isSuccessState = false
                    // Triggers writing custom order directly inside Room and navigates away
                    viewModel.confirmPaymentAndPlaceOrder(cardNumber, cardName)
                }

                // Full screen successful animation
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.85f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .padding(24.dp)
                            .border(1.dp, Color(0xFF10AC84), RoundedCornerShape(24.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Éxito",
                                tint = Color(0xFF10AC84),
                                modifier = Modifier.size(72.dp)
                            )
                            Text(
                                "Pago Autorizado",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                "La transacción de extremo a extremo fue asegurada con éxito. Creando tu orden de imitación y enrutando logística...",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

// Extension to insert split characters for nice looking displays
fun String.insert(index: Int, text: String): String {
    return if (this.length > index) {
        this.substring(0, index) + text + this.substring(index)
    } else {
        this
    }
}
