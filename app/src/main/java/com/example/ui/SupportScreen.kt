package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SupportMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(viewModel: MagnetViewModel) {
    val messages by viewModel.supportMessages.collectAsState()
    val isDark by viewModel.isDarkTheme.collectAsState()

    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Keep scrolling to most recent message automatically
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Soporte Técnico Especializado", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10AC84))
                            )
                            Text("Soporte en vivo • Encriptado", fontSize = 10.sp, color = Color(0xFF10AC84))
                        }
                    }
                },
                actions = {
                    Surface(
                        color = Color(0xFF0EA5E9).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "AES-256",
                                tint = Color(0xFF0EA5E9),
                                modifier = Modifier.size(12.dp)
                              )
                            Text("E2EE SECURE", fontSize = 9.sp, color = Color(0xFF0EA5E9), fontWeight = FontWeight.Bold)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) Color(0xFF0F172A) else Color.White,
                    titleContentColor = if (isDark) Color.White else Color(0xFF1E293B)
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(viewModel = viewModel, activeScreen = Screen.HELP)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDark) Color(0xFF0F172A) else Color(0xFFF1F5F9))
                .padding(innerPadding)
        ) {
            // Secure conversation disclaimer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF3B82F6).copy(alpha = 0.08f))
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color(0xFF3B82F6),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        "Todas tus conversaciones están cifradas extremo a extremo y protegidas por nuestra política de privacidad.",
                        fontSize = 10.sp,
                        color = Color(0xFF3B82F6),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Message list viewport
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages) { message ->
                    ChatBubble(message = message, isDark = isDark)
                }
            }

            // Input panel box
            Surface(
                tonalElevation = 8.dp,
                color = if (isDark) Color(0xFF0F172A) else Color.White,
                border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        placeholder = { Text("Pregunta sobre envíos, neodimio, precios...", fontSize = 13.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("support_chat_text_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = if (isDark) Color.White else Color(0xFF1E293B),
                            unfocusedTextColor = if (isDark) Color.White else Color(0xFF1E293B),
                            focusedBorderColor = Color(0xFF3B82F6),
                            unfocusedBorderColor = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
                        ),
                        maxLines = 3,
                        shape = RoundedCornerShape(20.dp)
                    )

                    IconButton(
                        onClick = {
                            if (textInput.isNotBlank()) {
                                viewModel.sendUserSupportMessage(textInput)
                                textInput = ""
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(0xFF3B82F6),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .testTag("support_chat_send_button")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Enviar", modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: SupportMessage, isDark: Boolean) {
    val isUser = message.isFromUser
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            if (!isUser) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3B82F6)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SupportAgent,
                        contentDescription = "Rep",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Surface(
                color = if (isUser) Color(0xFF3B82F6) else (if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 16.dp
                )
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = message.messageText,
                        color = if (isUser) Color.White else (if (isDark) Color.White else Color(0xFF1E293B)),
                        fontSize = 13.sp
                    )

                    Text(
                        text = if (isUser) "Tu • Encriptado" else "Soporte Imantados • Verificado",
                        color = if (isUser) Color.White.copy(alpha = 0.7f) else Color.Gray,
                        fontSize = 9.sp,
                        textAlign = if (isUser) TextAlign.End else TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
