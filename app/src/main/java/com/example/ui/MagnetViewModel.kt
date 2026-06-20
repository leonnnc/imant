package com.example.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class Screen {
    LOGIN,
    CATALOG,
    CANVAS,
    CHECKOUT,
    TRACKING,
    HELP,
    SETTINGS
}

data class MagnetPreset(
    val id: String,
    val name: String,
    val price: Double,
    val category: String,
    val description: String,
    val vectorIconName: String,
    val defaultText: String = "",
    val accentColor: String = "#FF6B6B"
)

class MagnetViewModel(application: Application) : AndroidViewModel(application) {

    private val database = MagnetDatabase.getDatabase(application)
    private val repository = MagnetRepository(database.magnetDao())

    // UI States
    private val _currentScreen = MutableStateFlow(Screen.CATALOG)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private var pendingScreen: Screen? = null
    private val restrictedScreens = setOf(Screen.CHECKOUT, Screen.TRACKING, Screen.HELP, Screen.SETTINGS)

    // Navigation Stack (Simple stack for detail screen back press)
    private val screenStack = mutableListOf<Screen>()

    fun navigateTo(screen: Screen) {
        if (screen in restrictedScreens && currentUser.value == null) {
            pendingScreen = screen
            screenStack.add(_currentScreen.value)
            _currentScreen.value = Screen.LOGIN
        } else {
            screenStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack() {
        if (screenStack.isNotEmpty()) {
            _currentScreen.value = screenStack.removeAt(screenStack.size - 1)
        } else {
            _currentScreen.value = Screen.CATALOG
        }
    }

    // Connect flow states from Repository
    val customDesigns: StateFlow<List<CustomDesign>> = repository.allCustomDesigns
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<Order>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val supportMessages: StateFlow<List<SupportMessage>> = repository.allSupportMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentUser: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Editor Canvas Attributes
    private val _selectedPreset = MutableStateFlow<MagnetPreset?>(null)
    val selectedPreset: StateFlow<MagnetPreset?> = _selectedPreset.asStateFlow()

    private val _customText = MutableStateFlow("")
    val customText: StateFlow<String> = _customText.asStateFlow()

    private val _customColor = MutableStateFlow("#FF5252") // Hex String
    val customColor: StateFlow<String> = _customColor.asStateFlow()

    private val _uploadedImageUri = MutableStateFlow<Uri?>(null)
    val uploadedImageUri: StateFlow<Uri?> = _uploadedImageUri.asStateFlow()

    private val _magnetStrength = MutableStateFlow("Estándar") // Estándar, Neodimio Pro
    val magnetStrength: StateFlow<String> = _magnetStrength.asStateFlow()

    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity.asStateFlow()

    // App Preferences (User dark mode preference & Low connectivity simulation)
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _isLowConnectivity = MutableStateFlow(false)
    val isLowConnectivity: StateFlow<Boolean> = _isLowConnectivity.asStateFlow()

    // Push notification alerts
    private val _notifications = MutableStateFlow<List<String>>(emptyList())
    val notifications: StateFlow<List<String>> = _notifications.asStateFlow()

    // Preset Catalog list
    val presets = listOf(
        MagnetPreset("moto_cat", "Gatito Kawaii", 4.90, "Cute", "Imán decorativo de gatito con diseño tierno en 3D.", "pets", "Te amo", "#FF6B6B"),
        MagnetPreset("corgi_dog", "Porky Corgi", 5.20, "Cute", "Adorable imán flexible de perrito Corgi con patitas.", "favorite", "Pórtate bien", "#FFA502"),
        MagnetPreset("astronaut", "Chibi Astronauta", 6.50, "Espacio", "Explorador cósmico imantado con terminación brillante.", "rocket_launch", "Al infinito", "#54A0FF"),
        MagnetPreset("happy_avocado", "Aguacate Feliz", 4.50, "Cute", "Divertido imán de palta con relieve táctil.", "eco", "Tu media mitad", "#10AC84"),
        MagnetPreset("geek_retro", "Geek Retro Glitch", 5.90, "Retro", "Consola retro imantada ideal para refrigerador.", "videogame_asset", "INSERT COIN", "#5F27CD"),
        MagnetPreset("quotes_gold", "Frases Inspiracionales", 3.90, "Letras", "Letras doradas con imán de alta fuerza.", "format_quote", "Hecho con amor", "#FFD2D2"),
        MagnetPreset("custom_photo", "Imán Foto Personalizada", 7.90, "Subidas", "Sube tu propia foto familiar, con tu mascota o diseño único.", "add_a_photo", "Mi Recuerdo", "#70A1FF")
    )

    init {
        // Auto-navigate if already logged in
        viewModelScope.launch {
            repository.userProfile.collect { profile ->
                if (profile != null && _currentScreen.value == Screen.LOGIN) {
                    val target = pendingScreen ?: Screen.CATALOG
                    pendingScreen = null
                    _currentScreen.value = target
                }
            }
        }

        // Add greetings check in support
        viewModelScope.launch {
            repository.allSupportMessages.collect { list ->
                if (list.isEmpty()) {
                    repository.sendSupportMessage(
                        SupportMessage(
                            messageText = "¡Hola! Bienvenido a Soporte Técnico de Imantados. ¿En qué podemos ayudarte hoy con tus pedidos personalizados?",
                            isFromUser = false
                        )
                    )
                }
            }
        }
    }

    fun selectPreset(preset: MagnetPreset) {
        _selectedPreset.value = preset
        _customText.value = preset.defaultText
        _customColor.value = preset.accentColor
        _uploadedImageUri.value = null
        _magnetStrength.value = "Estándar"
        _quantity.value = 1
        navigateTo(Screen.CANVAS)
    }

    fun setCustomText(text: String) {
        _customText.value = text
    }

    fun setCustomColor(color: String) {
        _customColor.value = color
    }

    fun setUploadedImageUri(uri: Uri?) {
        _uploadedImageUri.value = uri
    }

    fun setMagnetStrength(strength: String) {
        _magnetStrength.value = strength
    }

    fun incrementQuantity() {
        _quantity.value += 1
    }

    fun decrementQuantity() {
        if (_quantity.value > 1) {
            _quantity.value -= 1
        }
    }

    // Calculate dynamic pricing
    fun getUnitPrice(): Double {
        val base = _selectedPreset.value?.price ?: 5.00
        val strengthPremium = if (_magnetStrength.value == "Neodimio Pro") 2.50 else 0.00
        return base + strengthPremium
    }

    fun getTotalPrice(): Double {
        return getUnitPrice() * _quantity.value
    }

    // Login logic
    fun login(name: String, email: String, method: String) {
        viewModelScope.launch {
            val avatarUrl = when (method) {
                "Google" -> "https://lh3.googleusercontent.com/a/default-user"
                "Facebook" -> "https://graph.facebook.com/v12.0/user/picture"
                else -> ""
            }
            val profile = UserProfile(email, name, avatarUrl, method)
            repository.loginUser(profile)
            val target = pendingScreen ?: Screen.CATALOG
            pendingScreen = null
            _currentScreen.value = target
            addMockNotification("¡Bienvenido $name! Sesión iniciada con $method de forma segura.")
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logoutUser()
            _currentScreen.value = Screen.LOGIN
            screenStack.clear()
        }
    }

    // Support chat messaging logic
    fun sendUserSupportMessage(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.sendSupportMessage(SupportMessage(messageText = text, isFromUser = true))

            // Trigger natural automated answers based on trigger keywords (to look extremely professional)
            val lower = text.lowercase()
            val replyText = when {
                lower.contains("pedido") || lower.contains("envio") || lower.contains("entrega") -> {
                    "Para rastrear un pedido, puedes ir a la sección 'Historial de Pedidos'. Allí verás el estado en tiempo real, el mapa de envío, y el código de seguimiento de tu paquete."
                }
                lower.contains("pago") || lower.contains("tarjeta") || lower.contains("precio") -> {
                    "Nuestros pagos están encriptados con cifrado SSL de extremo a extremo. Aceptamos tarjetas de crédito y débito. El precio de envío estándar es gratis en compras mayores a $15."
                }
                lower.contains("iman") || lower.contains("fuerza") || lower.contains("neodimio") -> {
                    "Ofrecemos imanes estándar flexibles (ideales para refrigeradoras) y de Neodimio N52 Extra Fuertes que soportan hasta 10 veces su peso, ideales para pizarras metálicas pesadas."
                }
                lower.contains("foto") || lower.contains("imagen") || lower.contains("personaliz") -> {
                    "¡Sí! Puedes usar la sección 'Sube tu propia foto' en el catálogo para cargar cualquier imagen desde tu galería, y previsualizar cómo se verá recortada en el imán de alta fidelidad."
                }
                else -> {
                    "Entendido. Un asesor especialista de Imantados se conectará en breve. Todos los chats están encriptados de extremo a extremo para proteger tu privacidad."
                }
            }

            // Simulate slight delay for online feel, unless simulated low connectivity is on
            val delayMs = if (_isLowConnectivity.value) 500L else 1200L
            kotlinx.coroutines.delay(delayMs)
            repository.sendSupportMessage(SupportMessage(messageText = replyText, isFromUser = false))
        }
    }

    // Order checkout logic
    fun confirmPaymentAndPlaceOrder(cardNumber: String, ownerName: String) {
        viewModelScope.launch {
            val preset = _selectedPreset.value ?: return@launch
            val orderNo = "IM-${Random.nextInt(100000, 999999)}"
            val strengthText = _magnetStrength.value
            val customTextOverlay = _customText.value
            val textPart = if (customTextOverlay.isNotBlank()) " con texto \"$customTextOverlay\"" else ""

            val details = "${_quantity.value}x Imán ${preset.name} [Fuerza: $strengthText]$textPart"

            val order = Order(
                orderNumber = orderNo,
                itemDetails = details,
                totalAmount = getTotalPrice(),
                status = "Procesando",
                estimatedDelivery = "Llega en 3-5 días hábiles",
                trackingCode = "TRK-${Random.nextInt(5000000, 9999999)}",
                paymentMethod = "Tarjeta terminada en ${cardNumber.takeLast(4)}"
            )

            repository.placeOrder(order)
            addMockNotification("订单 exitoso: Pedido $orderNo confirmado. ¡Estamos preparando tu imán de alta resistencia!")

            // Redirect to tracking page directly
            _currentScreen.value = Screen.TRACKING

            // Simulate automatic updates in background to show real-time progress update simulation
            simulateTrackingUpdates(orderNo)
        }
    }

    private fun simulateTrackingUpdates(orderNo: String) {
        viewModelScope.launch {
            // After 10s change status to Production
            kotlinx.coroutines.delay(12000)
            val orders = repository.allOrders.first()
            val targetedOrder = orders.find { it.orderNumber == orderNo }
            if (targetedOrder != null) {
                repository.updateOrderStatus(targetedOrder.id, "En Producción")
                addMockNotification("¡Tu pedido $orderNo ha entrado a la etapa de corte láser y magnetización!")
            }

            // After another 12s change state to Enviado
            kotlinx.coroutines.delay(12000)
            val currentOrders = repository.allOrders.first()
            val targets = currentOrders.find { it.orderNumber == orderNo }
            if (targets != null) {
                repository.updateOrderStatus(targets.id, "Enviado")
                addMockNotification("🚚 ¡Buenas noticias! Tu pedido $orderNo fue despachado. Seguimiento en tiempo real activado.")
            }
        }
    }

    fun toggleDarkMode() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun toggleLowConnectivity() {
        _isLowConnectivity.value = !_isLowConnectivity.value
    }

    private fun addMockNotification(message: String) {
        viewModelScope.launch {
            val current = _notifications.value.toMutableList()
            current.add(0, message)
            _notifications.value = current
        }
    }

    fun clearNotifications() {
        _notifications.value = emptyList()
    }
}
