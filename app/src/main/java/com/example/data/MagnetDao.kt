package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MagnetDao {

    // Custom Designs
    @Query("SELECT * FROM custom_designs ORDER BY createdAt DESC")
    fun getAllCustomDesigns(): Flow<List<CustomDesign>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomDesign(design: CustomDesign)

    @Delete
    suspend fun deleteCustomDesign(design: CustomDesign)

    @Query("DELETE FROM custom_designs")
    suspend fun deleteAllCustomDesigns()

    // Orders
    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    fun getAllOrders(): Flow<List<Order>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order)

    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Int, status: String)

    // Support Messages
    @Query("SELECT * FROM support_messages ORDER BY timestamp ASC")
    fun getAllSupportMessages(): Flow<List<SupportMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupportMessage(message: SupportMessage)

    // User Profile
    @Query("SELECT * FROM user_profile LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfile)

    @Query("DELETE FROM user_profile")
    suspend fun deleteUserProfile()
}
