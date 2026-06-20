package com.example.ui

import androidx.compose.animation.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Order

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingScreen(viewModel: MagnetViewModel) {
    val orders by viewModel.orders.collectAsState()
    val isDark by viewModel.isDarkTheme.collectAsState()

    var selectedOrder by remember { mutableStateOf<Order?>(null) }

    // Auto-select latest order
    LaunchedEffect(orders) {
        if (selectedOrder == null && orders.isNotEmpty()) {
            selectedOrder = orders.first()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seguimiento de Envíos", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) Color(0xFF0F172A) else Color.White,
                    titleContentColor = if (isDark) Color.White else Color(0xFF1E293B)
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(viewModel = viewModel, activeScreen = Screen.TRACKING)
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
            if (orders.isEmpty()) {
                // Friendly Empty state
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) Color(0xFF1E293B) else Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = "Sin envíos",
                            tint = Color(0xFF3B82F6),
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            "Aún no tienes pedidos",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = if (isDark) Color.White else Color(0xFF0F172A),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "Visita nuestro catálogo de imanes de personajes y realiza tu primer pedido personalizado. Verás el seguimiento de inmediato aquí.",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { viewModel.navigateTo(Screen.CATALOG) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                        ) {
                            Text("Examinar Catálogo")
                        }
                    }
                }
            } else {
                // Interactive horizontal carousel selector of packages/orders
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "Mis Pedidos Personalizados",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        orders.forEach { order ->
                            val isSelected = selectedOrder?.id == order.id
                            val statusColor = when (order.status) {
                                "Procesando" -> Color(0xFF3B82F6)
                                "En Producción" -> Color(0xFF7C3AED)
                                "Enviado" -> Color(0xFFF59E0B)
                                else -> Color(0xFF10AC84)
                            }

                            Card(
                                modifier = Modifier
                                    .width(200.dp)
                                    .clickable { selectedOrder = order }
                                    .testTag("historical_order_badge_${order.orderNumber}"),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color(0xFF3B82F6).copy(alpha = 0.12f)
                                                    else (if (isDark) Color(0xFF1E293B) else Color.White)
                                ),
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) Color(0xFF3B82F6) else (if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            order.orderNumber,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isDark) Color.White else Color(0xFF0F172A)
                                        )
                                        Surface(
                                            color = statusColor.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                order.status,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = statusColor,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        order.itemDetails,
                                        fontSize = 10.sp,
                                        color = Color(0xFF64748B),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        "Total: $${String.format("%.2f", order.totalAmount)}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = Color(0xFF10AC84)
                                    )
                                }
                            }
                        }
                    }
                }

                // Show tracking coordinates/animation for currently selected order
                selectedOrder?.let { activeOrder ->
                    val statusColor = when (activeOrder.status) {
                        "Procesando" -> Color(0xFF3B82F6)
                        "En Producción" -> Color(0xFF7C3AED)
                        "Enviado" -> Color(0xFFF59E0B)
                        else -> Color(0xFF10AC84)
                    }

                    // Card with detailed tracker
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("order_tracking_details_card"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDark) Color(0xFF1E293B) else Color.White
                        ),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Header tracking details
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        "Rastreo en Tiempo Real",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = if (isDark) Color.White else Color(0xFF0F172A)
                                    )
                                    Text(
                                        "Código Courier: ${activeOrder.trackingCode}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }

                                Surface(
                                    color = Color(0xFF10AC84).copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        activeOrder.estimatedDelivery,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF10AC84),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            HorizontalDivider(color = if (isDark) Color(0xFF334155) else Color(0xFFF1F5F9))

                            // REALISTIC DELIVERY SIMULATION MAP DESIGN
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    "Mapa de Ruta Logística (Cifrado GPRS)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else Color(0xFF475569)
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(130.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (isDark) Color(0xFF0F172A) else Color(0xFFE2E8F0))
                                        .border(
                                            1.dp, 
                                            if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1), 
                                            RoundedCornerShape(14.dp)
                                        )
                                ) {
                                    // Custom vector path illustration for deliveries
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val width = size.width
                                        val height = size.height

                                        // Draw logistics dotted path lines
                                        drawCircle(
                                            color = Color(0xFF3B82F6),
                                            radius = 8f,
                                            center = Offset(width * 0.15f, height * 0.5f)
                                        )

                                        drawCircle(
                                            color = Color(0xFF7C3AED),
                                            radius = 8f,
                                            center = Offset(width * 0.45f, height * 0.3f)
                                        )

                                        drawCircle(
                                            color = Color(0xFFF59E0B),
                                            radius = 8f,
                                            center = Offset(width * 0.75f, height * 0.7f)
                                        )

                                        drawCircle(
                                            color = Color(0xFF10AC84),
                                            radius = 12f,
                                            center = Offset(width * 0.9f, height * 0.4f)
                                        )

                                        // Dotted connection path
                                        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                        drawLine(
                                            color = Color.Gray.copy(alpha = 0.5f),
                                            start = Offset(width * 0.15f, height * 0.5f),
                                            end = Offset(width * 0.45f, height * 0.3f),
                                            strokeWidth = 4f,
                                            pathEffect = pathEffect
                                        )
                                        drawLine(
                                            color = Color.Gray.copy(alpha = 0.5f),
                                            start = Offset(width * 0.45f, height * 0.3f),
                                            end = Offset(width * 0.75f, height * 0.7f),
                                            strokeWidth = 4f,
                                            pathEffect = pathEffect
                                        )
                                        drawLine(
                                            color = Color.Gray.copy(alpha = 0.5f),
                                            start = Offset(width * 0.75f, height * 0.7f),
                                            end = Offset(width * 0.9f, height * 0.4f),
                                            strokeWidth = 4f,
                                            pathEffect = pathEffect
                                        )
                                    }

                                    // Display location names on the map
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        Text(
                                            "Taller",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Gray,
                                            modifier = Modifier.align(Alignment.CenterStart).padding(start = 20.dp, top = 30.dp)
                                        )
                                        Text(
                                            "En Producción",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Gray,
                                            modifier = Modifier.align(Alignment.TopCenter).padding(top = 10.dp)
                                        )
                                        Text(
                                            "Camino",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Gray,
                                            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 10.dp, start = 120.dp)
                                        )
                                        Text(
                                            "Tu Casa",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF10AC84),
                                            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 15.dp, bottom = 44.dp)
                                        )

                                        // Pulse animation representing active coordinates courier
                                        val activeOffsetAndIcon = when (activeOrder.status) {
                                            "Procesando" -> Alignment.CenterStart to Icons.Default.Inventory2
                                            "En Producción" -> Alignment.TopCenter to Icons.Default.PrecisionManufacturing
                                            "Enviado" -> Alignment.BottomCenter to Icons.Default.LocalShipping
                                            else -> Alignment.CenterEnd to Icons.Default.Home
                                        }

                                        val leftPadding = when (activeOrder.status) {
                                            "Procesando" -> 40.dp
                                            "En Producción" -> 140.dp
                                            "Enviado" -> 240.dp
                                            else -> 300.dp
                                        }

                                        val topPadding = when (activeOrder.status) {
                                            "Procesando" -> 50.dp
                                            "En Producción" -> 20.dp
                                            "Enviado" -> 70.dp
                                            else -> 35.dp
                                        }

                                        Box(
                                            modifier = Modifier
                                                .padding(start = leftPadding, top = topPadding)
                                                .shadow(6.dp, CircleShape)
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(statusColor),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = activeOffsetAndIcon.second,
                                                contentDescription = "Courier",
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Package Progress checklist timeline
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    "Historial de Checkpoints",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else Color(0xFF475569)
                                )

                                TrackTimelineStep("Pedido Recibido", "Pago verificado y encriptación SSL de extremo a extremo aprobada.", true, isDark)
                                TrackTimelineStep("Corte Láser y Control Magnetismo", "Corte láser de precisión del contorno del personaje e imantación N52.", activeOrder.status != "Procesando", isDark)
                                TrackTimelineStep("Empacado y Despachado", "Diseño empaquetado en caja protectora y entregado al courier express.", activeOrder.status == "Enviado" || activeOrder.status == "Entregado", isDark)
                                TrackTimelineStep("Entregado con Éxito", "El courier entregó el imán en tus manos.", activeOrder.status == "Entregado", isDark)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrackTimelineStep(
    title: String,
    desc: String,
    isCompleted: Boolean,
    isDark: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(if (isCompleted) Color(0xFF10AC84) else (if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0))),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(30.dp)
                    .background(if (isCompleted) Color(0xFF10AC84) else (if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)))
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (isCompleted) (if (isDark) Color.White else Color(0xFF0F172A)) else Color(0xFF94A3B8)
            )
            Text(
                desc,
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}
