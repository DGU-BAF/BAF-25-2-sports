// feature/feedback/ui/myparty/MyPartyListViewModel.kt
package com.example.baro.feature.feedback.ui.myparty

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baro.feature.feedback.FeedbackServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MyPartyListViewModel : ViewModel() {

    private val getMyPartiesUseCase = FeedbackServiceLocator.getMyPartiesForFeedbackUseCase

    private val _items = MutableStateFlow<List<MyPartyListItemUiModel>>(emptyList())
    val items: StateFlow<List<MyPartyListItemUiModel>> = _items

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun load() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val domains = getMyPartiesUseCase()
                _items.value = domains.map { MyPartyListItemUiModel.from(it) }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
}
