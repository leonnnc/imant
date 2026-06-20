package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@Entity(tableName = "custom_designs")
data class CustomDesign(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val baseImage: String, // Preset slug (e.g. "gato") or custom URI
    val customImageUri: String? = null,
    val textOverlay: String = "",
    val textColor: String = "#FFFFFF",
    val quantity: Int = 1,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val orderNumber: String,
    val itemDetails: String, // JSON or list formatted text of items
    val totalAmount: Double,
    val status: String, // "Procesando", "En Producción", "Enviado", "Entregado"
    val estimatedDelivery: String,
    val trackingCode: String,
    val paymentMethod: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "support_messages")
data class SupportMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val messageText: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val email: String,
    val name: String,
    val avatarUrl: String?,
    val loggedInVia: String, // "Google", "Facebook", "Apple"
    val timestamp: Long = System.currentTimeMillis()
)
