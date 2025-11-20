// com/example/baro/feature/party/ui/create/PartyCreateViewModel.kt
package com.example.baro.feature.party.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baro.feature.party.PartyServiceLocator
import com.example.baro.feature.party.domain.model.PartyDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PartyCreateViewModel : ViewModel() {

    private val createPartyUseCase = PartyServiceLocator.createPartyUseCase

    private val _createdParty = MutableStateFlow<PartyDetail?>(null)
    val createdParty: StateFlow<PartyDetail?> = _createdParty

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun createParty(
        title: String,
        sport: String,
        place: String,
        description: String,
        date: String,
        startTime: String,
        endTime: String,
        capacity: Int,
    ) {
        viewModelScope.launch {
            _isSubmitting.value = true
            _errorMessage.value = null

            try {
                val request = com.example.baro.feature.party.domain.model.CreateParty(
                    title = title,
                    sport = sport,
                    place = place,
                    description = description,
                    date = date,
                    startTime = startTime,
                    endTime = endTime,
                    capacity = capacity,
                )

                val detail = createPartyUseCase(request)
                _createdParty.value = detail
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "파티 생성 중 오류가 발생했습니다."
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
