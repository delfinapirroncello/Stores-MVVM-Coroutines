package com.pirro.stores.mainModule.adapter

import com.pirro.stores.common.entities.StoreEntity

interface OnClickListener {
    fun onClick(storeEntity: StoreEntity)
    fun onFavoriteStore(storeEntity: StoreEntity)
    fun onDeleteStore(storeEntity: StoreEntity)
}