package com.pirro.stores.mainModule.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.pirro.stores.StoreApplication
import com.pirro.stores.common.entities.StoreEntity
import com.pirro.stores.common.utils.StoresException
import com.pirro.stores.common.utils.TypeError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class MainInteractor {
    val stores: LiveData<MutableList<StoreEntity>> = liveData {
        val storesLiveData = StoreApplication.database.storeDao().getAllStore()
        emitSource(storesLiveData.map { stores ->
            stores.sortedBy { it.name }.toMutableList()
        })
    }

    suspend fun deleteStore(storeEntity: StoreEntity) = withContext(Dispatchers.IO){
        val result = StoreApplication.database.storeDao().delateStore(storeEntity)
        if (result == 0) throw StoresException(TypeError.DELETE)
    }

    suspend fun updateStore(storeEntity: StoreEntity) = withContext(Dispatchers.IO){
        delay(300)
        //storeEntity.id = -1
        val result = StoreApplication.database.storeDao().updateStore(storeEntity)
        if (result == 0) throw StoresException(TypeError.UPDATE)
    }
}