package com.pirro.stores.editModule.model

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.LiveData
import com.pirro.stores.StoreApplication
import com.pirro.stores.common.entities.StoreEntity
import com.pirro.stores.common.utils.StoresException
import com.pirro.stores.common.utils.TypeError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EditStoreInteractor {

    fun getStoreById(id: Long):LiveData<StoreEntity>{
        return StoreApplication.database.storeDao().getStoreById(id)
    }

    suspend fun saveStore(storeEntity: StoreEntity) = withContext(Dispatchers.IO){
        try {
            StoreApplication.database.storeDao().addStore(storeEntity)
        } catch (e: SQLiteConstraintException){
            throw StoresException(TypeError.INSERT)
        }
    }

    suspend fun updateStore(storeEntity: StoreEntity) = withContext(Dispatchers.IO){
        //storeEntity.id = -1
        try {
            val result = StoreApplication.database.storeDao().updateStore(storeEntity)
            if (result == 0) throw StoresException(TypeError.UPDATE)
        } catch (e: SQLiteConstraintException){
            throw StoresException(TypeError.UPDATE)
        }
    }
}