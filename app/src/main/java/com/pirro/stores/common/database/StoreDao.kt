package com.pirro.stores.common.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.pirro.stores.common.entities.StoreEntity

@Dao
interface StoreDao {
    @Query( "SELECT * FROM StoreEntity")
    fun getAllStore(): LiveData<MutableList<StoreEntity>>

    @Query("SELECT * FROM StoreEntity where id = :id")
    fun getStoreById(id: Long): LiveData<StoreEntity>

    @Insert
    suspend fun addStore(storeEntity: StoreEntity): Long

    @Update
    suspend fun updateStore(storeEntity: StoreEntity): Int

    @Delete
    suspend fun delateStore(storeEntity: StoreEntity): Int
}