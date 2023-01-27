package com.pirro.stores.mainModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pirro.stores.common.entities.StoreEntity
import com.pirro.stores.common.utils.Constants
import com.pirro.stores.common.utils.StoresException
import com.pirro.stores.common.utils.TypeError
import com.pirro.stores.mainModule.model.MainInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainViewModel: ViewModel () {
    private var interactor: MainInteractor = MainInteractor()

    private val typeError: MutableLiveData<TypeError> = MutableLiveData()

    private val showProcess: MutableLiveData<Boolean> = MutableLiveData()

    private val stores = interactor.stores

    fun getStores(): LiveData<MutableList<StoreEntity>> {
        return stores
    }

    fun getTypeError(): MutableLiveData<TypeError> = typeError

    fun isShowProgress(): LiveData<Boolean> {
        return showProcess
    }

    fun deleteStore(storeEntity: StoreEntity) {
        executeAction { interactor.deleteStore(storeEntity) }
    }

    fun updateStore(storeEntity: StoreEntity) {
        storeEntity.isFavorite = !storeEntity.isFavorite
        executeAction {interactor.updateStore(storeEntity)}
    }

    private fun executeAction(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            showProcess.value = Constants.SHOW

            try {
                block()
            } catch (e: StoresException) {
                typeError.value = e.typeError
            } finally {
                showProcess.value = Constants.HIDE
            }
        }
    }
}
