package com.example.data

import kotlinx.coroutines.flow.Flow

class MagnetRepository(private val dao: MagnetDao) {

    // Custom Designs
    val allCustomDesigns: Flow<List<CustomDesign>> = dao.getAllCustomDesigns()

    suspend fun saveCustomDesign(design: CustomDesign) {
        dao.insertCustomDesign(design)
    }

    suspend fun deleteCustomDesign(design: CustomDesign) {
        dao.deleteCustomDesign(design)
    }

    suspend fun clearCustomDesigns() {
        dao.deleteAllCustomDesigns()
    }

    // Orders
    val allOrders: Flow<List<Order>> = dao.getAllOrders()

    suspend fun placeOrder(order: Order) {
        dao.insertOrder(order)
    }

    suspend fun updateOrderStatus(orderId: Int, status: String) {
        dao.updateOrderStatus(orderId, status)
    }

    // Support Messages
    val allSupportMessages: Flow<List<SupportMessage>> = dao.getAllSupportMessages()

    suspend fun sendSupportMessage(message: SupportMessage) {
        dao.insertSupportMessage(message)
    }

    // User Profile
    val userProfile: Flow<UserProfile?> = dao.getUserProfile()

    suspend fun loginUser(profile: UserProfile) {
        dao.saveUserProfile(profile)
    }

    suspend fun logoutUser() {
        dao.deleteUserProfile()
    }
}
